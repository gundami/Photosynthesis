package net.zuiron.photosynthesis.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.zuiron.photosynthesis.Photosynthesis;
import net.zuiron.photosynthesis.api.CropData;
import net.zuiron.photosynthesis.api.Seasons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CropBlock.class)
public abstract class ModCropBlock {
    @Shadow public abstract int getMaxAge();

    @Shadow protected abstract int getAge(BlockState state);

    @Shadow public abstract BlockState withAge(int age);

    @Inject(method = "isFertilizable", at = @At("HEAD"), cancellable = true)
    public void isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient, CallbackInfoReturnable<Boolean> cir) {
        //Photosynthesis.LOGGER.info("bonemealing not allowed");
        //TODO make it so, only crops that has cropData is disabled.
        cir.setReturnValue(false);
        cir.cancel();
    }

    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/CropBlock;withAge(I)Lnet/minecraft/block/BlockState;", ordinal = 0), cancellable = true)
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {

        if(Seasons.isSeasonsEnabled()) {
            CropData cropData = CropData.getCropDataFor(state.getBlock().getTranslationKey());
            if(cropData != null) {
                int minAge = cropData.getMinAge(Seasons.getCurrentSeason(world.getTimeOfDay()));
                int maxAge = cropData.getMaxAge(Seasons.getCurrentSeason(world.getTimeOfDay()));
                float seasonPercentage = Seasons.getSeasonPercentage(world.getTimeOfDay());
                int currentCropAge = this.getAge(state);
                if(currentCropAge >= minAge && currentCropAge < maxAge && seasonPercentage > 0.5f) { //0.5f = 50% "halfway thru season"
                    Photosynthesis.LOGGER.info("Crop: "+state.getBlock().getTranslationKey()+", minAge:"+minAge+", maxAge:"+maxAge+", CurrentCropAge: "+currentCropAge+", NewCropAge: "+(this.getAge(state) + 1)+", %:"+seasonPercentage);
                    world.setBlockState(pos, this.withAge(this.getAge(state) + 1), 2);
                } else {
                    Photosynthesis.LOGGER.info("Crop: "+state.getBlock().getTranslationKey()+", minAge:"+minAge+", maxAge:"+maxAge+", CurrentCropAge: "+currentCropAge+", NO GROW"+", %:"+seasonPercentage);
                }
                ci.cancel(); //cancel, do not run vanilla code.
                //Note we only cancel IF we found seasons data for the crop. otherwise vanilla code runs.
            }
        }
        //world.setBlockState(pos, this.withAge(i + 1), 2); //Original MC Code
    }

    /*@Inject(method = "randomTick", at = @At("HEAD"))
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        Photosynthesis.LOGGER.info("test");
    }*/
}
