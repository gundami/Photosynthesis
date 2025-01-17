package net.zuiron.photosynthesis.block.cropblocks;

import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.zuiron.photosynthesis.api.Seasons;
import net.zuiron.photosynthesis.block.ModBlocks;
import net.zuiron.photosynthesis.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class CustomGrassCropBlock extends CropBlock {
    String seed;
    public CustomGrassCropBlock(Settings settings, String itemname) {
        super(settings);
        seed = itemname;
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        switch (seed) {
            case "rice_crop_anotherclass": if(world.getFluidState(pos.up(1)).isOf(Fluids.WATER)) { return true; }
            default: return floor.isOf(Blocks.GRASS_BLOCK) || floor.isOf(Blocks.DIRT) || floor.isOf(Blocks.FARMLAND);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if(state.get(Properties.AGE_7) == 7 && super.canPlaceAt(state, world, pos) && (world.getBaseLightLevel(pos, 0) >= 8 || world.isSkyVisible(pos))) {
            //WE MUST DO THIS, IF WORLD-GEN CAN PLANT IT IN THE WILD!
            return true;
        }
        return (world.getBaseLightLevel(pos, 0) >= 8 || world.isSkyVisible(pos)) && super.canPlaceAt(state, world, pos);
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

    private static final VoxelShape[] FLOWER_AGE_TO_SHAPE = new VoxelShape[]{
            Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 5.0, 11.0),
            Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0),
            Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0),
            Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 7.0, 11.0),
            Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 7.0, 11.0),
            Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 8.0, 11.0),
            Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 8.0, 11.0),
            Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 10.0, 11.0)
    };

    //DOING THIS CAUSES issues with right click harvest. however. if we do not. icon is not present in WTHIT!
    //Think this issue is fixed.
    @Override
    protected ItemConvertible getSeedsItem() {

        switch (seed) {
            case "grass_crop": return ModItems.GRASS_SEEDS;
            case "dandelion_crop": return ModItems.DANDELION_SEEDS;
            case "poppy_crop": return ModItems.POPPY_SEEDS;
            case "blue_orchid_crop": return ModItems.BLUE_ORCHID_SEEDS;
            case "allium_crop": return ModItems.ALLIUM_SEEDS;
            case "azure_bluet_crop": return ModItems.AZURE_BLUET_SEEDS;
            case "red_tulip_crop": return ModItems.RED_TULIP_SEEDS;
            case "orange_tulip_crop": return ModItems.ORANGE_TULIP_SEEDS;
            case "white_tulip_crop": return ModItems.WHITE_TULIP_SEEDS;
            case "pink_tulip_crop": return ModItems.PINK_TULIP_SEEDS;
            case "oxeye_daisy_crop": return ModItems.OXEYE_DAISY_SEEDS;
            case "cornflower_crop": return ModItems.CORNFLOWER_SEEDS;
            case "lily_of_the_valley_crop": return ModItems.LILY_OF_THE_VALLEY_SEEDS;
            case "floramelissia_crop": return ModItems.FLORAMELISSIA_SEEDS;
            case "wither_rose_crop": return ModItems.WITHER_ROSE_SEEDS;

            default: return Items.AIR;
        }
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
        if (Seasons.isSeasonsEnabled()) {
            return false;
        } else return this.getAge(state) < 7;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if(state.isOf(ModBlocks.GRASS_CROP)) {
            return AGE_TO_SHAPE[(Integer) state.get(this.getAgeProperty())];
        } else {
            Vec3d vec3d = state.getModelOffset(world, pos);
            return FLOWER_AGE_TO_SHAPE[(Integer) state.get(this.getAgeProperty())].offset(vec3d.x, vec3d.y, vec3d.z);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if(state.isOf(ModBlocks.GRASS_CROP)) {
            float f;
            int i;
            if (world.getBaseLightLevel(pos, 0) >= 9 &&
                    (i = this.getAge(state)) < this.getMaxAge() &&
                    random.nextInt(8) == 0) { //12.5% chance.

                if (world.getBlockState(pos).contains(AGE)) { //check first in case it doesn't have age7 property.
                    if(Seasons.isSeasonsEnabled()) {
                        if(Objects.equals(Seasons.getSeasonString(Seasons.getCurrentSeason(world.getTimeOfDay())), "Winter")) {
                            //world.setBlockState(pos, state.with(AGE, this.getAge(state) + 1), 2);
                        } else {
                            world.setBlockState(pos, state.with(AGE, this.getAge(state) + 1), 2);
                        }
                    } else {
                        world.setBlockState(pos, state.with(AGE, this.getAge(state) + 1), 2);
                    }
                }
            }
        } else {
            super.randomTick(state, world, pos, random);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        if(Seasons.isSeasonsEnabled() && stack.isOf(ModItems.GRASS_SEEDS)) {
            tooltip.add(Text.literal("Grass grows all year round, except in winter time."));
        }
        super.appendTooltip(stack, world, tooltip, options);
    }
}
