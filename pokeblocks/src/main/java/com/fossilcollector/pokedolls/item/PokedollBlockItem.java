package com.fossilcollector.pokedolls.item;

import com.fossilcollector.pokedolls.block.PokedollBlock;
import com.fossilcollector.pokedolls.client.render.item.PokedollItemRenderer;
import com.fossilcollector.pokedolls.registry.PokedollSpecies;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class PokedollBlockItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final boolean isShiny;
    private final PokedollSpecies species;

    public PokedollBlockItem(Block block, Settings settings, PokedollSpecies species, boolean isShiny) {
        super(block, settings);
        this.species = species;
        this.isShiny = isShiny;
    }

    public PokedollSpecies getSpecies() {
        return this.species;
    }

    public boolean isShiny() {
        return this.isShiny;
    }

    @Override
    public String getTranslationKey() {
        if (this.isShiny) {
            return "item.static_pokedolls." + this.species.getId() + "_shiny_doll";
        }
        return super.getTranslationKey();
    }

    @Override
    protected BlockState getPlacementState(ItemPlacementContext context) {
        BlockState state = super.getPlacementState(context);
        return state != null ? state.with(PokedollBlock.SHINY, this.isShiny) : null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Static items
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private PokedollItemRenderer renderer;

            @Override
            public BuiltinModelItemRenderer getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new PokedollItemRenderer();
                }
                return this.renderer;
            }
        });
    }
}
