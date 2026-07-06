package com.fossilcollector.pokedolls.client.gui;

import com.fossilcollector.pokedolls.block.PokedollBlock;
import com.fossilcollector.pokedolls.entity.PokedollBlockEntity;
import com.fossilcollector.pokedolls.network.SizeSelectPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class PokedollSizeScreen extends Screen {
    private final BlockPos pos;
    private float currentScale = 2.0F;
    private float currentRot = 0.0F;
    private float currentOffsetX = 0.0F;
    private float currentOffsetY = 0.0F;
    private float currentOffsetZ = 0.0F;
    private boolean currentGlowing = false;
    private boolean currentAutoRotate = false;
    private String currentPose = "static";

    private CustomSlider scaleSlider;
    private CustomSlider rotSlider;
    private CustomSlider offsetXSlider;
    private CustomSlider offsetYSlider;
    private CustomSlider offsetZSlider;
    private CheckboxWidget glowingCheckbox;
    private CheckboxWidget autoRotateCheckbox;
    private ButtonWidget poseButton;

    private static final String[] POSES = {"static", "ground_idle", "battle_idle", "ground_walk", "sleep"};
    private static final String[] POSE_LABELS = {
        "Pose: Static (No Anim)",
        "Pose: Idle / Breathing",
        "Pose: Battle Stance",
        "Pose: Walk / Fly",
        "Pose: Sleeping"
    };

    public PokedollSizeScreen(BlockPos pos) {
        super(Text.literal("Pokedoll Customization Screen"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();
        if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().world.getBlockEntity(pos) instanceof PokedollBlockEntity be) {
            this.currentScale = be.getCustomScale() > 0 ? be.getCustomScale() : be.getCachedState().get(PokedollBlock.SIZE).getScaleMultiplier();
            this.currentRot = be.hasCustomRotation() ? be.getCustomRotation() : -(be.getCachedState().get(PokedollBlock.ROTATION) * 22.5F);
            while (this.currentRot < 0) this.currentRot += 360.0F;
            this.currentRot = this.currentRot % 360.0F;
            this.currentOffsetX = be.getOffsetX();
            this.currentOffsetY = be.getOffsetY();
            this.currentOffsetZ = be.getOffsetZ();
            this.currentGlowing = be.getCachedState().get(PokedollBlock.GLOWING);
            this.currentAutoRotate = be.isAutoRotate();
            this.currentPose = be.getPose() != null ? be.getPose() : "static";
        }

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int colW = 155;
        int leftX = centerX - colW - 5;
        int rightX = centerX + 5;
        int startY = centerY - 70;

        // Left Column: Sliders
        this.scaleSlider = this.addDrawableChild(new CustomSlider(leftX, startY, colW, 20, "Scale", 0.5, 15.0, this.currentScale, "x"));
        this.rotSlider = this.addDrawableChild(new CustomSlider(leftX, startY + 24, colW, 20, "Rotation", 0.0, 360.0, this.currentRot, "°"));
        this.offsetXSlider = this.addDrawableChild(new CustomSlider(leftX, startY + 48, colW, 20, "Offset X", -1.0, 1.0, this.currentOffsetX, ""));
        this.offsetYSlider = this.addDrawableChild(new CustomSlider(leftX, startY + 72, colW, 20, "Offset Y", -1.0, 1.0, this.currentOffsetY, ""));
        this.offsetZSlider = this.addDrawableChild(new CustomSlider(leftX, startY + 96, colW, 20, "Offset Z", -1.0, 1.0, this.currentOffsetZ, ""));

        // Right Column: Controls
        this.poseButton = this.addDrawableChild(ButtonWidget.builder(Text.literal(getPoseLabel(this.currentPose)), button -> {
            int idx = 0;
            for (int i = 0; i < POSES.length; i++) {
                if (POSES[i].equals(this.currentPose)) { idx = i; break; }
            }
            idx = (idx + 1) % POSES.length;
            this.currentPose = POSES[idx];
            button.setMessage(Text.literal(getPoseLabel(this.currentPose)));
            sendUpdate();
        }).dimensions(rightX, startY, colW, 20).build());

        this.glowingCheckbox = this.addDrawableChild(CheckboxWidget.builder(Text.literal("Glowing Effect"), this.textRenderer)
            .pos(rightX, startY + 28)
            .checked(this.currentGlowing)
            .callback((checkbox, checked) -> sendUpdate())
            .build());

        this.autoRotateCheckbox = this.addDrawableChild(CheckboxWidget.builder(Text.literal("Auto-Rotate (Showcase)"), this.textRenderer)
            .pos(rightX, startY + 52)
            .checked(this.currentAutoRotate)
            .callback((checkbox, checked) -> sendUpdate())
            .build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Reset Offsets & Rot"), button -> {
            if (this.offsetXSlider != null) this.offsetXSlider.setVal(0.0);
            if (this.offsetYSlider != null) this.offsetYSlider.setVal(0.0);
            if (this.offsetZSlider != null) this.offsetZSlider.setVal(0.0);
            if (this.rotSlider != null) this.rotSlider.setVal(0.0);
            sendUpdate();
        }).dimensions(rightX, startY + 74, colW, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Reset Scale (2.0x Normal)"), button -> {
            if (this.scaleSlider != null) this.scaleSlider.setVal(2.0);
            sendUpdate();
        }).dimensions(rightX, startY + 96, colW, 20).build());

        // Bottom Row: Save & Close
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Save & Close"), button -> {
            sendUpdate();
            this.close();
        }).dimensions(centerX - 100, startY + 124, 200, 20).build());
    }

    private String getPoseLabel(String pose) {
        for (int i = 0; i < POSES.length; i++) {
            if (POSES[i].equals(pose)) return POSE_LABELS[i];
        }
        return "Pose: Static (No Anim)";
    }

    private void sendUpdate() {
        ClientPlayNetworking.send(new SizeSelectPayload(
            this.pos,
            "normal",
            this.glowingCheckbox != null && this.glowingCheckbox.isChecked(),
            this.scaleSlider != null ? (float)this.scaleSlider.getVal() : this.currentScale,
            this.rotSlider != null ? (float)this.rotSlider.getVal() : this.currentRot,
            this.offsetXSlider != null ? (float)this.offsetXSlider.getVal() : this.currentOffsetX,
            this.offsetYSlider != null ? (float)this.offsetYSlider.getVal() : this.currentOffsetY,
            this.offsetZSlider != null ? (float)this.offsetZSlider.getVal() : this.currentOffsetZ,
            this.autoRotateCheckbox != null && this.autoRotateCheckbox.isChecked(),
            this.currentPose
        ));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 90, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private class CustomSlider extends SliderWidget {
        private final String prefix;
        private final double minVal;
        private final double maxVal;
        private final String suffix;

        public CustomSlider(int x, int y, int width, int height, String prefix, double minVal, double maxVal, double currentVal, String suffix) {
            super(x, y, width, height, Text.empty(), (MathHelper.clamp(currentVal, minVal, maxVal) - minVal) / (maxVal - minVal));
            this.prefix = prefix;
            this.minVal = minVal;
            this.maxVal = maxVal;
            this.suffix = suffix;
            this.updateMessage();
        }

        public double getVal() {
            return this.minVal + this.value * (this.maxVal - this.minVal);
        }

        public void setVal(double val) {
            this.value = (MathHelper.clamp(val, this.minVal, this.maxVal) - this.minVal) / (this.maxVal - this.minVal);
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            double val = getVal();
            this.setMessage(Text.literal(String.format("%s: %.2f%s", this.prefix, val, this.suffix)));
        }

        @Override
        protected void applyValue() {
            sendUpdate();
        }
    }
}
