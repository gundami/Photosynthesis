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
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.zuiron.photosynthesis.Photosynthesis;
import net.zuiron.photosynthesis.block.custom.KegBlock;
import net.zuiron.photosynthesis.networking.ModMessages;
import net.zuiron.photosynthesis.recipe.KegRecipe;
import net.zuiron.photosynthesis.screen.KegScreenHandler;
import net.zuiron.photosynthesis.util.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class KegBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(6, ItemStack.EMPTY);

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

    public final SingleVariantStorage<FluidVariant> fluidInput = new SingleVariantStorage<FluidVariant>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return FluidStack.convertDropletsToMb(FluidConstants.BUCKET) * 4; // 4 buckets
        }

        @Override
        protected void onFinalCommit() {
            markDirty();
            if(!world.isClient()) {
                sendFluidPacket();
            }
        }
    };

    public final SingleVariantStorage<FluidVariant> fluidOutput = new SingleVariantStorage<FluidVariant>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return FluidStack.convertDropletsToMb(FluidConstants.BUCKET) * 4; // 4 buckets
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
        PacketByteBuf data2 = PacketByteBufs.create();

        fluidInput.variant.toPacket(data);
        data.writeLong(fluidInput.amount);

        fluidOutput.variant.toPacket(data2);
        data2.writeLong(fluidOutput.amount);

        data.writeBlockPos(getPos());
        data2.writeBlockPos(getPos());

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, getPos())) {
            ServerPlayNetworking.send(player, ModMessages.FLUID_SYNC, data);
            ServerPlayNetworking.send(player, ModMessages.FLUID_SYNC2, data2);
        }
    }

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 20;

    public KegBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KEG, pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                switch (index) {
                    case 0: return KegBlockEntity.this.progress;
                    case 1: return KegBlockEntity.this.maxProgress;
                    default: return 0;
                }
            }

            public void set(int index, int value) {
                switch(index) {
                    case 0: KegBlockEntity.this.progress = value; break;
                    case 1: KegBlockEntity.this.maxProgress = value; break;
                }
            }

            public int size() {
                return 2;
            }
        };
    }

    public void setInputFluidLevel(FluidVariant fluidVariant, long fluidLevel) {
        this.fluidInput.variant = fluidVariant;
        this.fluidInput.amount = fluidLevel;
    }

    public void setOutputFluidLevel(FluidVariant fluidVariant, long fluidLevel) {
        this.fluidOutput.variant = fluidVariant;
        this.fluidOutput.amount = fluidLevel;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal(""); //Keg
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if(!world.isClient()) {
            sendFluidPacket(); //SYNC when we open gui.
        }
        return new KegScreenHandler(syncId, inv, this, this.propertyDelegate);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("keg.progress", progress);
        nbt.putInt("keg.cookingTime", maxProgress);

        nbt.put("inputFluid.variant", fluidInput.variant.toNbt());
        nbt.putLong("inputFluid.fluid", fluidInput.amount);

        nbt.put("outputFluid.variant", fluidOutput.variant.toNbt());
        nbt.putLong("outputFluid.fluid", fluidOutput.amount);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, inventory);
        super.readNbt(nbt);
        progress = nbt.getInt("keg.progress");
        maxProgress = nbt.getInt("keg.cookingTime");

        fluidInput.variant = FluidVariant.fromNbt((NbtCompound) nbt.get("inputFluid.variant"));
        fluidInput.amount = nbt.getLong("inputFluid.fluid");

        fluidOutput.variant = FluidVariant.fromNbt((NbtCompound) nbt.get("outputFluid.variant"));
        fluidOutput.amount = nbt.getLong("outputFluid.fluid");
    }

    private void resetProgress() {
        this.progress = 0;
        syncItems();
    }

    public static void animationTick(World level, BlockPos pos, BlockState state) {
        if (isBlockBelowBurning(level, pos) && level.isClient()) {
            Random random = level.random;
            /*if (random.nextFloat() < 0.2F) {
                double x = (double) pos.getX() + 0.5D + (random.nextDouble() * 0.4D - 0.2D);
                double y = (double) pos.getY() + 1.1D;
                double z = (double) pos.getZ() + 0.5D + (random.nextDouble() * 0.4D - 0.2D);
                double motionY = random.nextBoolean() ? 0.015D : 0.005D;
                level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 0.0D, motionY, 0.0D);
            }*/
            if (state.get(BooleanProperty.of("processing"))) {
                double x = (double) pos.getX() + 0.5D + (random.nextDouble() * 0.4D - 0.2D);
                double y = (double) pos.getY() + 0.1D;
                double z = (double) pos.getZ() + 0.5D + (random.nextDouble() * 0.4D - 0.2D);
                double motionY = random.nextBoolean() ? 0.015D : 0.005D;
                level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, motionY, 0.0D); //ENCHANTED_HIT
            }
        }
    }


    public static void playKegSound(World world, BlockPos blockPos) {
        if(!world.isClient()) { world.playSound(null, blockPos, Photosynthesis.KEG_SOUND_EVENT, SoundCategory.BLOCKS, 1f, 1f); }
    }

    private static int tickCounter = 0;

    public static void loopKegSound(World world, BlockPos blockPos) {
        int ticksPerLoop = (int) (2 * 20);
        tickCounter++;
        if (tickCounter >= ticksPerLoop) {
            playKegSound(world, blockPos);
            tickCounter = 0;
        }
    }

    public static void tick(World world, BlockPos blockPos, BlockState state, KegBlockEntity entity) {
        animationTick(world, blockPos, state);

        if(world.isClient()) {
            return;
        }
        boolean isBelowHeat = isBlockBelowBurning(world, blockPos);

        //check see if belowblock is burning, if so. enable hot texture!
        state = (BlockState)state.with(KegBlock.LIT, isBelowHeat);
        world.setBlockState(blockPos, state, 3);
        if(isBelowHeat) {
            markDirty(world, blockPos, state);
        }

        if(hasRecipe(entity) && isBelowHeat) {
            entity.progress++;
            state = (BlockState)state.with(KegBlock.PROCESSING, true); world.setBlockState(blockPos, state, 3);
            markDirty(world, blockPos, state);

            loopKegSound(world, blockPos);

            if(entity.progress >= entity.maxProgress) {
                craftItem(entity);
                state = (BlockState)state.with(KegBlock.PROCESSING, false); world.setBlockState(blockPos, state, 3);
            }
        } else {
            entity.resetProgress();
            state = (BlockState)state.with(KegBlock.PROCESSING, false); world.setBlockState(blockPos, state, 3);
            markDirty(world, blockPos, state);
        }
    }

    private static boolean isBlockBelowBurning(World world, BlockPos blockPos) {
        return true;
    }

    private static void craftItem(KegBlockEntity entity) {
        SimpleInventory inventory = new SimpleInventory(4);
        for (int i = 0; i < 4; i++) {
            inventory.setStack(i, entity.getStack(i+1)); //ingredients start from slot1.
        }

        Optional<KegRecipe> recipe = entity.getWorld().getRecipeManager()
                .getFirstMatch(KegRecipe.Type.INSTANCE, inventory, entity.getWorld());

        if(hasRecipe(entity)) {
            FluidStack inputFluid = recipe.get().getFluidInput();
            FluidStack outputFluid = recipe.get().getOutputFluid();

            //TODO - remove input fluid from input fluid tank

            entity.removeStack(1, (Integer) recipe.get().getCounts().get(0));   //input
            entity.removeStack(2, (Integer) recipe.get().getCounts().get(1));   //input
            entity.removeStack(3, (Integer) recipe.get().getCounts().get(2));   //input
            entity.removeStack(4, (Integer) recipe.get().getCounts().get(3));   //input

            //TODO - add output fluid to output tank
            try(Transaction transaction = Transaction.openOuter()) {
                entity.fluidOutput.insert(FluidVariant.of(outputFluid.fluidVariant.getFluid()),
                        FluidStack.convertDropletsToMb(outputFluid.amount), transaction);
                transaction.commit();
            }

            entity.resetProgress();
            Photosynthesis.LOGGER.info("Item crafted! SUCCESS.");
        }
    }

    private static boolean hasRecipe(KegBlockEntity entity) {
        SimpleInventory inventory = new SimpleInventory(4);
        for (int i = 0; i < 4; i++) {
            inventory.setStack(i, entity.getStack(i+1));
        }

        Optional<KegRecipe> match = entity.getWorld().getRecipeManager()
                .getFirstMatch(KegRecipe.Type.INSTANCE, inventory, entity.getWorld());

        entity.maxProgress = match.map(KegRecipe::getCookTime).orElse(20);

        if (match.isPresent()) {
            Photosynthesis.LOGGER.info("match is present! continue");
            KegRecipe recipe = match.get();
            List<Ingredient> ingredients = recipe.getIngredients();
            DefaultedList counts = recipe.getCounts();

            FluidStack inputFluid = recipe.getFluidInput();
            FluidStack outputFluid = recipe.getOutputFluid();

            //check item counts.
            for (int i = 0; i < ingredients.size(); i++) {
                Ingredient ingredient = ingredients.get(i);
                ItemStack itemStack = entity.getStack(i+1);
                //Photosynthesis.LOGGER.info("ingredient:"+ingredient.toJson()+", itemStack:"+itemStack);

                int reqCount = (int) counts.get(i);

                if (ingredient.isEmpty() || itemStack.isEmpty()) {
                    continue;
                } else if (ingredient.test(itemStack) && itemStack.getCount() >= reqCount) {
                    Photosynthesis.LOGGER.info("recipe requires min:"+reqCount+", we got:"+itemStack.getCount());
                    continue;
                } else {
                    Photosynthesis.LOGGER.info("FAILED - recipe requires:"+reqCount+", we got:"+itemStack.getCount());
                    return false;
                }
            }
            //TODO - check if input fluid has enough input fluid.

            //TODO - check if output can accept output.

            return canInsertFluidIntoFluidOutput(entity, outputFluid, FluidStack.convertDropletsToMb(outputFluid.amount));
            /*return canInsertAmountIntoOutputSlot(inventory, recipe.getOutput().getCount())
                    && canInsertItemIntoOutputSlot(inventory, recipe.getOutput().getItem());*/
            //return true;
        } else {
            Photosynthesis.LOGGER.info("no match is present...");
            return false;
        }
    }

    private static boolean canInsertFluidIntoFluidOutput(KegBlockEntity entity, FluidStack outputFluid, long amount) {
        long outputTankCapacity = entity.fluidOutput.getCapacity();
        long outputTankAmount = entity.fluidOutput.getAmount();
        long outputTankRemainingCapacity = outputTankCapacity - outputTankAmount;
        if(outputTankRemainingCapacity < amount) { return false; } //WORKING

        //TODO -- check if output fluid is equal to outputtankfluid or is empty.
        FluidVariant outputTankFluidVariant = entity.fluidOutput.getResource();

        FluidVariant recipeFluidVariant = outputFluid.fluidVariant;

        Photosynthesis.LOGGER.info("capacity: "+outputTankCapacity+", Amount: "+outputTankAmount+", of: "+outputTankFluidVariant);
        Photosynthesis.LOGGER.info("trying to insert: "+recipeFluidVariant+", amount: "+amount);
        return true;
    }


    private static boolean canInsertItemIntoOutputSlot(SimpleInventory inventory, Item output) {
        return false;
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleInventory inventory, int amount) {
        return false;
    }
}