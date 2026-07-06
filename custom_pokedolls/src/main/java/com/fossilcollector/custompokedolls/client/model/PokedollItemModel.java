package com.fossilcollector.custompokedolls.client.model;

import com.fossilcollector.custompokedolls.item.PokedollBlockItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollItemModel extends GeoModel<PokedollBlockItem> {
    @Override
    public Identifier getModelResource(PokedollBlockItem animatable) {
        return animatable.getSpecies().getGeoModelPath();
    }

    @Override
    public Identifier getTextureResource(PokedollBlockItem animatable) {
        return animatable.getSpecies().getTexturePath(animatable.isShiny());
    }

    @Override
    public Identifier getAnimationResource(PokedollBlockItem animatable) {
        return null;
    }
}
