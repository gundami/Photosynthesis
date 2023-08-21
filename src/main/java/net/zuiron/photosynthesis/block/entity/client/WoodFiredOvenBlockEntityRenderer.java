package net.zuiron.photosynthesis.block.entity.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.zuiron.photosynthesis.block.custom.SkilletBlock;
import net.zuiron.photosynthesis.block.custom.WoodFiredOvenBlock;
import net.zuiron.photosynthesis.block.entity.SkilletBlockEntity;
import net.zuiron.photosynthesis.block.entity.WoodFiredOvenBlockEntity;

public class WoodFiredOvenBlockEntityRenderer implements BlockEntityRenderer<WoodFiredOvenBlockEntity> {
    public WoodFiredOvenBlockEntityRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(WoodFiredOvenBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {

        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        //render slot lock
        double x = 0.9f; //0 is LEFT, 1.0 is RIGHT edge.
        double y = 0.1f; //0 is BOTTOM, 1 is TOP.
        double z = 1.0f; //0, is BACK, 1.0 is FRONT outside of shelf. toward player when looking at block.
        int rot = 0;
        float scale2 = 0.15f;
        renderSlotLock.render(entity,itemRenderer,tickDelta,matrices,vertexConsumers,light,overlay, x, y, z, rot, scale2);

        ItemStack itemStack = entity.getRenderStack();
        //TOOL
        matrices.push();
        float scale = 0.6f;
        float offset = 0.45f;
        float rotate = -90;
        float center = 0.5f;
        switch (entity.getCachedState().get(WoodFiredOvenBlock.FACING)) {
            case NORTH -> {
                matrices.translate(center, offset, center); //DONE
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotate));
                matrices.scale(scale, scale, scale);
            }
            case EAST -> {
                matrices.translate(center, offset, center); //DONE
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-270));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotate));
                matrices.scale(scale, scale, scale);
            }
            case SOUTH -> {
                matrices.translate(center, offset, center); //DONE
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotate));
                matrices.scale(scale, scale, scale);
            }
            case WEST -> {
                matrices.translate(center, offset, center); //DONE
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotate));
                matrices.scale(scale, scale, scale);
            }
        }
        //itemRenderer.renderItem(itemStack, ModelTransformation.Mode.GUI, getLightLevel(entity.getWorld(), entity.getPos()), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 1);
        itemRenderer.renderItem(itemStack, ModelTransformationMode.GUI, getLightLevel(entity.getWorld(), entity.getPos()), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 1);
        matrices.pop();

    }

    private int getLightLevel(World world, BlockPos pos) {
        int bLight = world.getLightLevel(LightType.BLOCK, pos);
        int sLight = world.getLightLevel(LightType.SKY, pos);
        return LightmapTextureManager.pack(bLight, sLight);
    }
}
