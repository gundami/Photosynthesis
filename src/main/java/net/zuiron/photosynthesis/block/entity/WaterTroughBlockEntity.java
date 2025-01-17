package net.zuiron.photosynthesis.block.entity;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.zuiron.photosynthesis.api.Seasons;
import net.zuiron.photosynthesis.block.ModBlocks;
import net.zuiron.photosynthesis.block.custom.MapleExtractorBlock;
import net.zuiron.photosynthesis.block.custom.WaterTroughBlock;
import net.zuiron.photosynthesis.fluid.ModFluids;
import net.zuiron.photosynthesis.item.ModItems;
import net.zuiron.photosynthesis.networking.ModMessages;
import net.zuiron.photosynthesis.screen.MapleExtractorScreenHandler;
import net.zuiron.photosynthesis.screen.WaterTroughScreenHandler;
import net.zuiron.photosynthesis.util.getCustomVarsPassiveEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class WaterTroughBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY); //N#:SLOTS



    public void setInventory(DefaultedList<ItemStack> inventory) {
        for (int i = 0; i < inventory.size(); i++) {
            this.inventory.set(i, inventory.get(i));
        }
    }

    public void syncItems() {
        if(!world.isClient()) {
            PacketByteBuf data = PacketByteBufs.create();
            data.writeInt(inventory.size());
            for(int i = 0; i < inventory.size(); i++) {
                data.writeItemStack(inventory.get(i));
            }
            data.writeBlockPos(getPos());

            for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, getPos())) {
                ServerPlayNetworking.send(player, ModMessages.ITEM_SYNC, data);
            }
        }
    }

    @Override
    public void markDirty() {
        syncItems();
        if(!world.isClient()) {
            sendFluidPacket();
        }

        super.markDirty();
    }

    public final SingleVariantStorage<FluidVariant> fluidStorage = new SingleVariantStorage<FluidVariant>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            //return FluidStack.convertDropletsToMb(FluidConstants.BUCKET) * 2; // 20k mB
            return FluidConstants.BUCKET * 2; // 20k mB
        }

        @Override
        protected void onFinalCommit() {
            markDirty();
            if(!world.isClient()) {
                sendFluidPacket();
            }
        }
    };

    private void sendFluidPacket() {
        PacketByteBuf data = PacketByteBufs.create();
        fluidStorage.variant.toPacket(data);
        data.writeLong(fluidStorage.amount);
        data.writeBlockPos(getPos());

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, getPos())) {
            ServerPlayNetworking.send(player, ModMessages.FLUID_SYNC, data);
        }
    }

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 60;

    public WaterTroughBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WATERTROUGH, pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                switch (index) {
                    case 0: return WaterTroughBlockEntity.this.progress;
                    case 1: return WaterTroughBlockEntity.this.maxProgress;
                    default: return 0;
                }
            }

            public void set(int index, int value) {
                switch(index) {
                    case 0: WaterTroughBlockEntity.this.progress = value; break;
                    case 1: WaterTroughBlockEntity.this.maxProgress = value; break;
                }
            }

            public int size() {
                return 2;
            }
        };
    }

    public void setFluidLevel(FluidVariant fluidVariant, long fluidLevel) {
        this.fluidStorage.variant = fluidVariant;
        this.fluidStorage.amount = fluidLevel;
    }

    @Override
    public DefaultedList<ItemStack> getItemsNoConflicts() {
        return this.inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Water Trough");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if(!world.isClient()) {
            sendFluidPacket(); //SYNC when we open gui.
        }
        return new WaterTroughScreenHandler(syncId, inv, this, this.propertyDelegate);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("watertrough.progress", progress);

        nbt.put("watertrough.variant", fluidStorage.variant.toNbt());
        nbt.putLong("watertrough.fluid", fluidStorage.amount);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, inventory);
        super.readNbt(nbt);
        progress = nbt.getInt("watertrough.progress");

        fluidStorage.variant = FluidVariant.fromNbt((NbtCompound) nbt.get("watertrough.variant"));
        fluidStorage.amount = nbt.getLong("watertrough.fluid");
    }

    private void resetProgress() {
        this.progress = 0;
        syncItems();
    }


    public static void tick(World world, BlockPos blockPos, BlockState state, WaterTroughBlockEntity entity) {
        if(world.isClient()) {
            return;
        }

        //if tank doesnt hold water or atleast one droplet. dont even tick. (lets try reduce lag)
        /*if(entity.fluidStorage.amount < FluidConstants.DROPLET && !entity.fluidStorage.variant.isOf(Fluids.WATER)) {
            return;
        }*/

        //property water level control.
        long water = entity.fluidStorage.amount;
        long maxWater = entity.fluidStorage.getCapacity();
        double water_percentage = (double) water / maxWater * 100;
        if(water_percentage > 50 && state.get(WaterTroughBlock.WATER_AMOUNT) != 2) {
            world.setBlockState(blockPos, state.with(WaterTroughBlock.WATER_AMOUNT, 2), 2);
        }
        else if(water_percentage <= 50 && water > 0 && state.get(WaterTroughBlock.WATER_AMOUNT) != 1) {
            world.setBlockState(blockPos, state.with(WaterTroughBlock.WATER_AMOUNT, 1), 2);
        }
        else if(water == 0 && state.get(WaterTroughBlock.WATER_AMOUNT) != 0) {
            world.setBlockState(blockPos, state.with(WaterTroughBlock.WATER_AMOUNT, 0), 2);
        }


        // Get the range in which you want to scan for entities
        double range = 10.0; // Adjust this value as needed

        // Calculate the bounding box around the block position
        Box boundingBox = new Box(
                blockPos.getX() - range, blockPos.getY() - range, blockPos.getZ() - range,
                blockPos.getX() + range, blockPos.getY() + range, blockPos.getZ() + range
        );

        // Look for passiveEntity entities
        Predicate<Entity> entityPredicate = entityP -> {
            return entityP instanceof PassiveEntity;
        };

        //if tank doesnt hold water or atleast one droplet. dont even scan. (lets try reduce lag)
        if(entity.fluidStorage.amount > FluidConstants.DROPLET && entity.fluidStorage.variant.isOf(Fluids.WATER)) {
            List<Entity> filteredEntities = world.getEntitiesByClass(Entity.class, boundingBox, entityPredicate);
            for (Entity ScannedPassiveEntity : filteredEntities) {

                if (ScannedPassiveEntity instanceof CowEntity || ScannedPassiveEntity instanceof SheepEntity || ScannedPassiveEntity instanceof ChickenEntity || ScannedPassiveEntity instanceof PigEntity || ScannedPassiveEntity instanceof HorseEntity || ScannedPassiveEntity instanceof GoatEntity) {
                    int mod_Water = ((getCustomVarsPassiveEntity) ScannedPassiveEntity).getMod_Water();
                    int mod_Water_max = ((getCustomVarsPassiveEntity) ScannedPassiveEntity).getMod_Water_max();

                    int missing = mod_Water_max - mod_Water;
                    int amount = 4000; //how much the entity gets of water per extracted water.
                    if (missing >= amount) { //how much does one item give?
                        //entity.getStack(0).decrement(1);
                        if (extractFluid(entity, FluidConstants.NUGGET)) {
                            //we had enough water, it IS extracted/removed.
                            ((getCustomVarsPassiveEntity) ScannedPassiveEntity).setMod_Water(mod_Water + amount);
                            world.playSound(null, ScannedPassiveEntity.getBlockPos(), SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.NEUTRAL, 0.5F, 1.0F);
                        }
                    }
                }

            }
        }




        if(hasFluidSourceInSlot(entity)) {
            transferFluidToFluidStorage(entity);
        }

        if(hasEmptyBucketInSlot(entity)) {
            extractFluidAndMakeBucket(entity);
        }
    }

    /*private static boolean canHarvestMaple(World world, BlockPos blockPos, WaterTroughBlockEntity entity) {
        Direction localDir = world.getBlockState(blockPos).get(MapleExtractorBlock.FACING);

        if(!world.getBlockState(blockPos.up()).isOf(Blocks.AIR)) {
            return false;
        }
        BlockPos relativeSouth = blockPos.offset(localDir, -1);
        BlockState relSouthState = world.getBlockState(relativeSouth);
        //check if block behind and one up is stripped rubber log.
        BlockPos relativeSouthUp = blockPos.offset(localDir, -1).up();
        BlockState relSouthStateUp = world.getBlockState(relativeSouthUp);

        if (relSouthState.getBlock() == ModBlocks.MAPLETREE_LOG && relSouthStateUp.getBlock() == ModBlocks.MAPLETREE_LOG) {
            return true;
        } else {
            //drop contents
            ItemScatterer.spawn(world, blockPos, entity);
            //remove block
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
            //spawn extractor block as drop.
            ItemStack itemStack = new ItemStack(ModBlocks.MAPLE_EXTRACTOR);
            ItemEntity itemEntity = new ItemEntity(world, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, itemStack);
            world.spawnEntity(itemEntity);
        }

        return false;
    }*/

    private static boolean hasEmptyBucketInSlot(WaterTroughBlockEntity entity) {
        if(entity.getStack(0).getItem() == Items.BUCKET) {
            return true;
        }
        return false;
    }

    private static void extractFluidAndMakeBucket(WaterTroughBlockEntity entity) {
        //if(entity.fluidStorage.amount >= 1000 && entity.getStack(1).isEmpty()) {
        if(entity.fluidStorage.amount >= FluidConstants.BUCKET && entity.getStack(1).isEmpty()) {
            ItemStack itemStack = new ItemStack(Items.WATER_BUCKET);
            //if(extractFluid(entity, 1000)) {
            if(extractFluid(entity, FluidConstants.BUCKET)) {
                //TODO fix, double buckets in slot 0 or more.
                int count = entity.getStack(0).getCount();
                if(count > 1) {
                    entity.getStack(0).setCount(count - 1);
                } else {
                    entity.setStack(0, new ItemStack(Items.AIR));
                }

                entity.setStack(1, itemStack);
            }
        }
    }

    private static boolean extractFluid(WaterTroughBlockEntity entity, long Amount) {
        try(Transaction transaction = Transaction.openOuter()) {
            entity.fluidStorage.extract(FluidVariant.of(Fluids.WATER),
                    Amount, transaction);
            transaction.commit();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }



    private static boolean insertFluid(WaterTroughBlockEntity entity, long amount) {
        try(Transaction transaction = Transaction.openOuter()) {
            entity.fluidStorage.insert(FluidVariant.of(Fluids.WATER), amount, transaction);
            transaction.commit();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private static void transferFluidToFluidStorage(WaterTroughBlockEntity entity) {

        if(entity.getStack(1).isEmpty() && canTankAcceptBucketWorth(entity)) {
            //if (insertFluid(entity, FluidStack.convertDropletsToMb(FluidConstants.BUCKET))) {
            if (insertFluid(entity, FluidConstants.BUCKET)) {
                entity.setStack(0, new ItemStack(Items.AIR));
                entity.setStack(1, new ItemStack(Items.BUCKET));
            }
        }
    }

    private static boolean canTankAcceptBucketWorth(WaterTroughBlockEntity entity) {
        long availableSpace = entity.fluidStorage.getCapacity() - entity.fluidStorage.getAmount();
        //if(availableSpace >= FluidStack.convertDropletsToMb(FluidConstants.BUCKET)) {
        if(availableSpace >= FluidConstants.BUCKET) {
            return true;
        }
        return false;
    }

    private static boolean hasFluidSourceInSlot(WaterTroughBlockEntity entity) {
        return entity.getStack(0).getItem() == Items.WATER_BUCKET;
    }


    /*private static void craftItem(WaterTroughBlockEntity entity) {
        SimpleInventory inventory = new SimpleInventory(entity.size());
        for (int i = 0; i < entity.size(); i++) {
            inventory.setStack(i, entity.getStack(i));
        }

        if(hasRecipe(entity)) {
            try(Transaction transaction = Transaction.openOuter()) {
                entity.fluidStorage.insert(FluidVariant.of(ModFluids.STILL_MAPLE),
                        FluidConstants.NUGGET, transaction);
                transaction.commit();
            }

            entity.resetProgress();
        }
    }

    private static boolean hasRecipe(WaterTroughBlockEntity entity) {
        SimpleInventory inventory = new SimpleInventory(entity.size());
        for (int i = 0; i < entity.size(); i++) {
            inventory.setStack(i, entity.getStack(i));
        }

        assert entity.world != null;
        long time = entity.world.getTimeOfDay();
        if(Seasons.isSeasonsEnabled()) {
            if(Objects.equals(Seasons.getSeasonString(Seasons.getCurrentSeason(time)), "Spring")) {
                //only produce maple in spring.
                return true;
            } else return false;
        }
        return true;
    }*/

    private static final int[] INPUT_SLOTS = {0};
    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        Direction localDir = this.getWorld().getBlockState(pos).get(WaterTroughBlock.FACING);

        if (side == Direction.DOWN) {
            return false;
        }

        if (side == Direction.UP) {
            return isInputSlot(slot);
        }

        //input top, left, back
        return switch (localDir) {
            default -> //NORTH
                    (side.getOpposite() == Direction.NORTH || side.getOpposite() == Direction.WEST) && isInputSlot(slot);
            case EAST ->
                    (side.rotateYClockwise() == Direction.NORTH || side.rotateYClockwise() == Direction.WEST) && isInputSlot(slot);
            case SOUTH ->
                    (side == Direction.NORTH || side == Direction.WEST) && isInputSlot(slot);
            case WEST ->
                    (side.rotateYCounterclockwise() == Direction.NORTH || side.rotateYCounterclockwise() == Direction.WEST) && isInputSlot(slot);
        };
    }

    private boolean isInputSlot(int slot) {
        for (int inputSlot : INPUT_SLOTS) {
            if (slot == inputSlot) {
                return true;
            }
        }
        return false;
    }


    private static final int[] OUTPUT_SLOTS = {1};
    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        Direction localDir = this.getWorld().getBlockState(this.pos).get(WaterTroughBlock.FACING);

        if (side == Direction.UP) {
            return false;
        }

        if (side == Direction.DOWN) {
            return isOutputSlot(slot);
        }

        //extract front, right, and down.
        return switch (localDir) {
            default -> (side.getOpposite() == Direction.SOUTH || side.getOpposite() == Direction.EAST) && isOutputSlot(slot);
            case EAST -> (side.rotateYClockwise() == Direction.SOUTH || side.rotateYClockwise() == Direction.EAST) && isOutputSlot(slot);
            case SOUTH -> (side == Direction.SOUTH || side == Direction.EAST) && isOutputSlot(slot);
            case WEST -> (side.rotateYCounterclockwise() == Direction.SOUTH || side.rotateYCounterclockwise() == Direction.EAST) && isOutputSlot(slot);
        };
    }

    private boolean isOutputSlot(int slot) {
        for (int outputSlot : OUTPUT_SLOTS) {
            if (slot == outputSlot) {
                return true;
            }
        }
        return false;
    }
}