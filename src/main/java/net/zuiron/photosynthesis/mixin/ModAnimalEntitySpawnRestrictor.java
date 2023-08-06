package net.zuiron.photosynthesis.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.predicate.entity.EntityTypePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import net.zuiron.photosynthesis.Photosynthesis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(AnimalEntity.class)
public class ModAnimalEntitySpawnRestrictor {
    @Inject(method = "isValidNaturalSpawn", at = @At("HEAD"), cancellable = true)
    private static void isValidNaturalSpawn(EntityType<? extends AnimalEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir) {
        //vanilla code
        //return world.getBlockState(pos.down()).isIn(BlockTags.ANIMALS_SPAWNABLE_ON) && AnimalEntity.isLightLevelValidForNaturalSpawn(world, pos);

        Photosynthesis.LOGGER.info("trying spawning: "+type+" at: "+pos+", reason: "+spawnReason);
        if(     type.equals(EntityType.PIG) ||
                type.equals(EntityType.COW) ||
                type.equals(EntityType.SHEEP) ||
                type.equals(EntityType.CHICKEN) ||
                type.equals(EntityType.HORSE) ||
                type.equals(EntityType.GOAT)) {
            if(spawnReason == SpawnReason.SPAWN_EGG || spawnReason == SpawnReason.BREEDING) {
                cir.setReturnValue(true);
            } else {
                Photosynthesis.LOGGER.info("Prevented " + type + " from spawning @" + pos + ", it's not from breeding, or spawn-egg!");
                cir.setReturnValue(false);
            }
        }

    }
}