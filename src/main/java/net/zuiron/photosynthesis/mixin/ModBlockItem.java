package net.zuiron.photosynthesis.mixin;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.zuiron.photosynthesis.api.CropData;
import net.zuiron.photosynthesis.api.Seasons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.List;

@Mixin(BlockItem.class)
public abstract class ModBlockItem {
    @Shadow public abstract Block getBlock();

    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        CropData cropData = CropData.getCropDataFor(Registries.ITEM.getId(this.getBlock().asItem()));
        if(cropData != null && Seasons.isSeasonsEnabled()) {
            String getHarvestSeason = cropData.getHarvestSeasonStr();
            String getPlantSeason = cropData.getPlantSeasonStr();
            String getMaxTemperature = String.valueOf(cropData.getMaxBiomesTemperature());
            String getMinTemperature = String.valueOf(cropData.getMinBiomesTemperature());
            String getMaxHumidity = String.valueOf(cropData.getMaxBiomesHumidity());
            String getMinHumidity = String.valueOf(cropData.getMinBiomesHumidity());


            TextColor getHarvestSeasonTextColor = cropData.getHarvestSeasonTextColor();
            TextColor getPlantSeasonTextColor = cropData.getPlantSeasonTextColor();

            tooltip.add(Text.literal("Plant: ").append(Text.literal(getPlantSeason).styled(style -> style.withColor(getPlantSeasonTextColor))));
            tooltip.add(Text.literal("Harvest: ").append(Text.literal(getHarvestSeason).styled(style -> style.withColor(getHarvestSeasonTextColor))));
            tooltip.add(Text.literal("Temperature: ").append(Text.literal(getMinTemperature+"-"+getMaxTemperature).styled(style -> style.withColor(0x32CD32))));
            tooltip.add(Text.literal("Humidity: ").append(Text.literal(getMinHumidity+"-"+getMaxHumidity).styled(style -> style.withColor(0x00CED1))));

        }
    }
}
