package com.fossilcollector.pokedolls.entity;

import com.fossilcollector.pokedolls.block.PokedollBlock;
import com.fossilcollector.pokedolls.registry.ModBlockEntities;
import com.fossilcollector.pokedolls.registry.PokedollSpecies;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PokedollBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private float customScale = -1.0F;
    private float customRotation = -1.0F;
    private float offsetX = 0.0F;
    private float offsetY = 0.0F;
    private float offsetZ = 0.0F;
    private boolean autoRotate = false;
    private String pose = "static";

    public PokedollBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POKEDOLL_BLOCK_ENTITY, pos, state);
    }

    public PokedollSpecies getSpecies() {
        if (this.getCachedState().getBlock() instanceof PokedollBlock block) {
            return block.getSpecies();
        }
        return PokedollSpecies.TYRANTRUM;
    }

    public float getCustomScale() { return this.customScale; }
    public void setCustomScale(float scale) { this.customScale = scale; }

    public float getCustomRotation() { return this.customRotation; }
    public void setCustomRotation(float rot) { this.customRotation = rot; }
    public boolean hasCustomRotation() { return this.customRotation >= 0.0F; }

    public float getOffsetX() { return this.offsetX; }
    public void setOffsetX(float x) { this.offsetX = x; }

    public float getOffsetY() { return this.offsetY; }
    public void setOffsetY(float y) { this.offsetY = y; }

    public float getOffsetZ() { return this.offsetZ; }
    public void setOffsetZ(float z) { this.offsetZ = z; }

    public boolean isAutoRotate() { return this.autoRotate; }
    public void setAutoRotate(boolean autoRotate) { this.autoRotate = autoRotate; }

    public String getPose() { return this.pose; }
    public void setPose(String pose) { this.pose = pose; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, state -> {
            String currentPose = this.getPose();
            if (currentPose == null || currentPose.isEmpty() || currentPose.equals("static")) {
                return PlayState.STOP;
            }
            String speciesId = this.getSpecies().getId();
            String target = speciesId.equals("tyrunt_pokedoll") ? "tyrunt" : speciesId;
            String animName = "animation." + target + "." + currentPose;
            
            if (currentPose.equals("battle_idle")) {
                if (speciesId.equals("mew") || speciesId.equals("mewtwo")) animName = "animation." + target + ".pose";
                else if (speciesId.equals("arctovish") || speciesId.equals("cradily") || speciesId.equals("kabuto") || speciesId.equals("lileep") || speciesId.equals("tirtouga")) animName = "animation." + target + ".ground_idle";
            } else if (currentPose.equals("sleep")) {
                if (speciesId.equals("mew") || speciesId.equals("mewtwo") || speciesId.equals("tyrantrum")) animName = "animation." + target + ".ground_idle";
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop(animName));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putFloat("CustomScale", this.customScale);
        nbt.putFloat("CustomRotation", this.customRotation);
        nbt.putFloat("OffsetX", this.offsetX);
        nbt.putFloat("OffsetY", this.offsetY);
        nbt.putFloat("OffsetZ", this.offsetZ);
        nbt.putBoolean("AutoRotate", this.autoRotate);
        if (this.pose != null) {
            nbt.putString("Pose", this.pose);
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("CustomScale")) this.customScale = nbt.getFloat("CustomScale");
        if (nbt.contains("CustomRotation")) this.customRotation = nbt.getFloat("CustomRotation");
        if (nbt.contains("OffsetX")) this.offsetX = nbt.getFloat("OffsetX");
        if (nbt.contains("OffsetY")) this.offsetY = nbt.getFloat("OffsetY");
        if (nbt.contains("OffsetZ")) this.offsetZ = nbt.getFloat("OffsetZ");
        if (nbt.contains("AutoRotate")) this.autoRotate = nbt.getBoolean("AutoRotate");
        if (nbt.contains("Pose")) this.pose = nbt.getString("Pose");
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
