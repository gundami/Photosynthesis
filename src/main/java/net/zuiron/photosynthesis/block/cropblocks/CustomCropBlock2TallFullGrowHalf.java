package net.zuiron.photosynthesis.block.cropblocks;

import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.zuiron.photosynthesis.Photosynthesis;
import net.zuiron.photosynthesis.api.CropData;
import net.zuiron.photosynthesis.api.Seasons;
import net.zuiron.photosynthesis.item.ModItems;
import net.zuiron.photosynthesis.state.property.ModProperties;
import net.zuiron.photosynthesis.util.ModConstants;

import java.util.Objects;

public class CustomCropBlock2TallFullGrowHalf extends CropBlock implements Waterloggable {
    String seed;

    public static final EnumProperty<DoubleBlockHalf> HALF;
    public CustomCropBlock2TallFullGrowHalf(Settings settings, String itemname) {
        super(settings);
        seed = itemname;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if(state.get(Properties.AGE_7) == 7 && (world.getBlockState(pos.down()).isIn(BlockTags.DIRT) || world.getBlockState(pos.down()).isOf(Blocks.FARMLAND) || world.getBlockState(pos.down()).isOf(this))) {
            //WE MUST DO THIS, IF WORLD-GEN CAN PLANT IT IN THE WILD!
            return true;
        }
        if (state.get(HALF) == DoubleBlockHalf.LOWER && world.getBlockState(pos.down()).isOf(Blocks.FARMLAND)) {
            return true;
        }
        else if (state.get(HALF) == DoubleBlockHalf.UPPER && world.getBlockState(pos.down()).isOf(this)) {
            return true;
        }
        return false;
    }

    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };


    @Override
    protected ItemConvertible getSeedsItem() {
        switch (seed) {
            case "corn_crop": return ModItems.CORN_SEEDS;
            case "tomato_crop": return ModItems.TOMATO_SEEDS;
            case "sunflower_crop": return ModItems.SUNFLOWER_SEEDS;
            default: return Items.AIR;
        }
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return AGE_TO_SHAPE[(Integer)state.get(this.getAgeProperty())];
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        //broke upper. (hand or machine break blocked)
        if(!newState.contains(Properties.AGE_7) && world.getBlockState(pos.down()).isOf(this)) {
            world.breakBlock(pos, false);
            world.breakBlock(pos.down(), false);
        }
        //broke lower. (hand or machine break blocked)
        else if(!newState.contains(Properties.AGE_7)) {
            world.breakBlock(pos.up(), false);
            world.breakBlock(pos, false);
        }
        //right click upper. (right click harvest or create harvested)
        else if(world.getBlockState(pos.down()).isOf(this) && newState.get(Properties.AGE_7) < state.get(Properties.AGE_7)) {
            world.breakBlock(pos, false);
            world.breakBlock(pos.down(), false);
            //right click harvest compat
            if(world.getBlockState(pos.down(2)).isOf(Blocks.FARMLAND)) {
                world.setBlockState(pos.down(), this.withAge(0).with(HALF, DoubleBlockHalf.LOWER), Block.NOTIFY_LISTENERS);
            }
        }
        //right click lowered. (right click harvest or create harvested)
        else if(world.getBlockState(pos.up()).isOf(this) && newState.get(Properties.AGE_7) < state.get(Properties.AGE_7)) {
            world.breakBlock(pos.up(), false);
            world.breakBlock(pos, false);
            //right click harvest compat
            if(world.getBlockState(pos.down(1)).isOf(Blocks.FARMLAND)) {
                world.setBlockState(pos, this.withAge(0).with(HALF, DoubleBlockHalf.LOWER), Block.NOTIFY_LISTENERS);
            }
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        //WE MUST DO THIS, IF WORLD-GEN CAN PLANT IT IN THE WILD!
        if(state.get(Properties.AGE_7) == 7 && world.getBlockState(pos.down()).isIn(BlockTags.DIRT)) {
            world.setBlockState(pos.up(1), (BlockState)this.getDefaultState().with(AGE, 7).with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_LISTENERS);
        }

        float f2;
        int i2;
        if(Seasons.isSeasonsEnabled() && world.getBaseLightLevel(pos.up(1), 0) >= 9 && (i2 = this.getAge(state)) < this.getMaxAge() && random.nextInt((int) (25.0f / (f2 = 7.0f)) + 1) == 0) {
            CropData cropData = CropData.getCropDataFor(Registries.ITEM.getId(state.getBlock().asItem()));
            if(cropData != null) {
                int minAge = cropData.getMinAge(Seasons.getCurrentSeason(world.getTimeOfDay()));
                int maxAge = cropData.getMaxAge(Seasons.getCurrentSeason(world.getTimeOfDay()));
                float seasonPercentage = Seasons.getSeasonPercentage(world.getTimeOfDay());
                int currentCropAge = this.getAge(state);

                if(currentCropAge >= minAge && currentCropAge < maxAge && seasonPercentage > ModConstants.GROWATABOVEPCT) { //0.5f = 50% "halfway thru season"
                    //Photosynthesis.LOGGER.info("CropWL: "+state.getBlock().getTranslationKey()+", minAge:"+minAge+", maxAge:"+maxAge+", CurrentCropAge: "+currentCropAge+", NewCropAge: "+(this.getAge(state) + 1)+", %:"+seasonPercentage);
                    if(currentCropAge < 7 && state.get(HALF) == DoubleBlockHalf.LOWER) {
                        //world.setBlockState(pos, this.withAge(currentCropAge + 1).with(HALF, DoubleBlockHalf.LOWER), Block.NOTIFY_LISTENERS);
                        world.setBlockState(pos, state.with(AGE, currentCropAge + 1).with(HALF, DoubleBlockHalf.LOWER), Block.NOTIFY_LISTENERS);
                        if(world.getBlockState(pos.up()).isOf(this)) {
                            //world.setBlockState(pos.up(), this.withAge(currentCropAge + 1).with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_LISTENERS);
                            world.setBlockState(pos.up(), state.with(AGE, currentCropAge + 1).with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_LISTENERS);
                        }
                    }
                    if(state.get(HALF) == DoubleBlockHalf.LOWER && currentCropAge >= 3 && world.getBlockState(pos.up(1)).isOf(Blocks.AIR) && world.getBlockState(pos.down(1)).isOf(Blocks.FARMLAND)) {
                        //world.setBlockState(pos.up(1), (BlockState)this.getDefaultState().with(AGE, 3).with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_LISTENERS);
                        world.setBlockState(pos.up(1), state.with(AGE, 3).with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_LISTENERS);
                    }
                } else {
                    //Photosynthesis.LOGGER.info("CropWL: "+state.getBlock().getTranslationKey()+", minAge:"+minAge+", maxAge:"+maxAge+", CurrentCropAge: "+currentCropAge+", NO GROW"+", %:"+seasonPercentage);
                }
            }
        } else if(!Seasons.isSeasonsEnabled()) {
            //SEASONS DISABLED
            float f;
            int i;
            int currentCropAge = this.getAge(state);
            if (world.getBaseLightLevel(pos.up(1), 0) >= 9 && (i = currentCropAge) < this.getMaxAge() && random.nextInt((int) (25.0f / (f = 7.0f)) + 1) == 0) {

                if(currentCropAge < 7 && state.get(HALF) == DoubleBlockHalf.LOWER) {
                    //world.setBlockState(pos, this.withAge(currentCropAge + 1).with(HALF, DoubleBlockHalf.LOWER), Block.NOTIFY_LISTENERS);
                    world.setBlockState(pos, state.with(AGE, currentCropAge + 1).with(HALF, DoubleBlockHalf.LOWER), Block.NOTIFY_LISTENERS);
                    if(world.getBlockState(pos.up()).isOf(this)) {
                        //world.setBlockState(pos.up(), this.withAge(currentCropAge + 1).with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_LISTENERS);
                        world.setBlockState(pos.up(), state.with(AGE, currentCropAge + 1).with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_LISTENERS);
                    }
                }
                if(state.get(HALF) == DoubleBlockHalf.LOWER && currentCropAge >= 3 && world.getBlockState(pos.up(1)).isOf(Blocks.AIR) && world.getBlockState(pos.down(1)).isOf(Blocks.FARMLAND)) {
                    //world.setBlockState(pos.up(1), (BlockState)this.getDefaultState().with(AGE, 3).with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_LISTENERS);
                    world.setBlockState(pos.up(1), state.with(AGE, 3).with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_LISTENERS);
                }

            }
        }
    }

    @Override
    public void applyGrowth(World world, BlockPos pos, BlockState state) {
        int currentCropAge = this.getAge(state);
        if(currentCropAge < 7 && state.get(HALF) == DoubleBlockHalf.LOWER) {
            //world.setBlockState(pos, this.withAge(currentCropAge + 1).with(HALF, DoubleBlockHalf.LOWER), Block.NOTIFY_LISTENERS);
            world.setBlockState(pos, state.with(AGE, currentCropAge + 1).with(HALF, DoubleBlockHalf.LOWER), Block.NOTIFY_LISTENERS);
            if(world.getBlockState(pos.up()).isOf(this)) {
                //world.setBlockState(pos.up(), this.withAge(currentCropAge + 1).with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_LISTENERS);
                world.setBlockState(pos.up(), state.with(AGE, currentCropAge + 1).with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_LISTENERS);
            }
        }
        if(state.get(HALF) == DoubleBlockHalf.LOWER && currentCropAge >= 3 && world.getBlockState(pos.up(1)).isOf(Blocks.AIR) && world.getBlockState(pos.down(1)).isOf(Blocks.FARMLAND)) {
            //world.setBlockState(pos.up(1), (BlockState)this.getDefaultState().with(AGE, 3).with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_LISTENERS);
            world.setBlockState(pos.up(1), state.with(AGE, 3).with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
        if(Seasons.isSeasonsEnabled()) {
            return false;
        }

        int currentCropAge = this.getAge(state);
        if(currentCropAge < 7 && state.get(HALF) == DoubleBlockHalf.LOWER) {
            return true;
        }
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{HALF}).add(new Property[]{AGE}).add(ModProperties.MOD_PESTICIDED).add(ModProperties.MOD_FERTILIZED);
    }

    static {
        HALF = Properties.DOUBLE_BLOCK_HALF;
    }
}
