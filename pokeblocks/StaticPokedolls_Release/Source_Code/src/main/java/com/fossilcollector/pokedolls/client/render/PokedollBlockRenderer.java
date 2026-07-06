package com.fossilcollector.pokedolls.client.render;

import com.fossilcollector.pokedolls.block.PokedollBlock;
import com.fossilcollector.pokedolls.client.model.PokedollModel;
import com.fossilcollector.pokedolls.entity.PokedollBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PokedollBlockRenderer extends GeoBlockRenderer<PokedollBlockEntity> {

    public PokedollBlockRenderer(BlockEntityRendererFactory.Context context) {
        super(new PokedollModel());
    }

    @Override
    public boolean rendersOutsideBoundingBox(PokedollBlockEntity entity) {
        // Globally disable frustum culling for large models
        return true;
    }

    @Override
    public void render(PokedollBlockEntity entity, float partialTick, MatrixStack matrixStack, 
                       VertexConsumerProvider bufferSource, int packedLight, int packedOverlay) {
        if (entity.getCachedState().get(PokedollBlock.GLOWING)) {
            packedLight = net.minecraft.client.render.LightmapTextureManager.MAX_LIGHT_COORDINATE;
        }
        matrixStack.push();
        
        int rot = entity.getCachedState().get(PokedollBlock.ROTATION);
        matrixStack.translate(0.5D + entity.getOffsetX(), entity.getOffsetY(), 0.5D + entity.getOffsetZ());
        
        float rotation = entity.hasCustomRotation() ? entity.getCustomRotation() : -(rot * 22.5F);
        if (entity.isAutoRotate()) {
            rotation += (System.currentTimeMillis() % 360000L) * 0.05F;
        }
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        
        // Normalized base scale * custom scale (or fallback to SIZE multiplier)
        float baseScale = entity.getSpecies().getScaleModifier();
        float sizeMult = entity.getCustomScale() > 0 ? entity.getCustomScale() : entity.getCachedState().get(PokedollBlock.SIZE).getScaleMultiplier();
        float scale = baseScale * sizeMult;
        matrixStack.scale(scale, scale, scale);
        
        matrixStack.translate(-0.5D, 0.0D, -0.5D);

        super.render(entity, partialTick, matrixStack, bufferSource, packedLight, packedOverlay);
        
        matrixStack.pop();
    }
}
