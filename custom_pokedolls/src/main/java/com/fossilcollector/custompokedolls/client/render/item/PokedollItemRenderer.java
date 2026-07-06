package com.fossilcollector.custompokedolls.client.render.item;

import com.fossilcollector.custompokedolls.client.model.PokedollItemModel;
import com.fossilcollector.custompokedolls.item.PokedollBlockItem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PokedollItemRenderer extends GeoItemRenderer<PokedollBlockItem> {

    public PokedollItemRenderer() {
        super(new PokedollItemModel());
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        
        if (stack.getItem() instanceof PokedollBlockItem item) {
            float normScale = item.getSpecies().getScaleModifier();
            
            // Center transformations around the item space center (0.5, 0.5, 0.5)
            matrices.translate(0.5F, 0.5F, 0.5F);
            
            if (mode == ModelTransformationMode.GUI) {
                // In GUI/Inventory: rotate 180 degrees around Y so the model faces forward toward the user
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
                float guiScale = normScale * 0.7F;
                matrices.scale(guiScale, guiScale, guiScale);
            } else if (mode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND) {
                float handScale = normScale * 0.5F;
                matrices.scale(handScale, handScale, handScale);
            } else if (mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND || mode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND) {
                float handScale = normScale * 0.5F;
                matrices.scale(handScale, handScale, handScale);
            } else if (mode == ModelTransformationMode.GROUND) {
                float groundScale = normScale * 0.5F;
                matrices.scale(groundScale, groundScale, groundScale);
            } else if (mode == ModelTransformationMode.FIXED) {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
                float frameScale = normScale * 0.6F;
                matrices.scale(frameScale, frameScale, frameScale);
            } else {
                float defaultScale = normScale * 0.5F;
                matrices.scale(defaultScale, defaultScale, defaultScale);
            }
            matrices.translate(-0.5F, -0.5F, -0.5F);
        }
        
        try {
            super.render(stack, mode, matrices, vertexConsumers, light, overlay);
        } catch (Exception ignored) {
            // Ignore rendering errors if model or texture file is deleted or missing
        }
        
        matrices.pop();
    }
}
