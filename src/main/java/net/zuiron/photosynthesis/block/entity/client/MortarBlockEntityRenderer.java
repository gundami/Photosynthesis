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
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.zuiron.photosynthesis.block.custom.ShelfBlock;
import net.zuiron.photosynthesis.block.entity.CookingPotBlockEntity;
import net.zuiron.photosynthesis.block.entity.MortarBlockEntity;

public class MortarBlockEntityRenderer implements BlockEntityRenderer<MortarBlockEntity> {
    public MortarBlockEntityRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(MortarBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {

        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        //Items in pot
        DefaultedList<ItemStack> inventoryList = entity.getInventoryMod();

        double max = 1.0f;
        //case: SOUTH: player looking north
        float x = 0.5f; //0 is LEFT, 1.0 is RIGHT edge.
        float y = 0.2f; //0 is BOTTOM, 1 is TOP.
        float z = 0.5f; //0, is BACK, 1.0 is FRONT outside of block. toward player when looking at block.
        float rot = 0;
        float rotx = 25f;
        double itemOffset = 0.2;
        float scale = 0.25f;
        int inventoryMaxSize = 3;

        for(int i = 0; i < inventoryMaxSize; i++) {
            matrices.push();

            if(i == 0) {
                z = 0.4f;
                x = 0.4f;
                y = 0.26f;
                rot = 45f;
                scale = 0.5f;
            } else {
                z = 0.5f;
                x = 0.5f;
                rot = 0f;
                scale = 0.25f;
                y = 0.2f;
            }

            switch (entity.getCachedState().get(ShelfBlock.FACING)) {
                case NORTH -> { //player looking south
                    matrices.translate(max - x, y, max - z);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotx));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180+rot));
                }
                case SOUTH -> { //player looking north
                    matrices.translate(x, y, z);
                    matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(rotx));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0+rot));
                }
                case EAST -> { //player looking west
                    matrices.translate(z, y, max - x);
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotx));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90+rot));
                }
                case WEST -> { //player looking east
                    matrices.translate(max - z, y, x);
                    matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(rotx));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90+rot));
                }
            }

            matrices.scale(scale, scale, scale);

            //IF output item is in block, hide the ingredients.
            ItemStack itemStack = inventoryList.get(i);
            if(!inventoryList.get(2).isEmpty() && i != 2 && i != 0) {
                itemStack = ItemStack.EMPTY;
            }

            itemRenderer.renderItem(itemStack, ModelTransformationMode.NONE, getLightLevel(entity.getWorld(), entity.getPos()), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 1);
            matrices.pop();
        }
    }

    private int getLightLevel(World world, BlockPos pos) {
        int bLight = world.getLightLevel(LightType.BLOCK, pos);
        int sLight = world.getLightLevel(LightType.SKY, pos);
        return LightmapTextureManager.pack(bLight, sLight);
    }
}
