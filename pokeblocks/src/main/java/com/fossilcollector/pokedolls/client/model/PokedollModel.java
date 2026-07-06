package com.fossilcollector.pokedolls.client.model;

import com.fossilcollector.pokedolls.block.PokedollBlock;
import com.fossilcollector.pokedolls.entity.PokedollBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PokedollModel extends GeoModel<PokedollBlockEntity> {
    @Override
    public Identifier getModelResource(PokedollBlockEntity animatable) {
        return animatable.getSpecies().getGeoModelPath();
    }

    @Override
    public Identifier getTextureResource(PokedollBlockEntity animatable) {
        boolean isShiny = animatable.getCachedState().get(PokedollBlock.SHINY);
        return animatable.getSpecies().getTexturePath(isShiny);
    }

    @Override
    public Identifier getAnimationResource(PokedollBlockEntity animatable) {
        return Identifier.of("static_pokedolls", "animations/" + animatable.getSpecies().getId() + ".animation.json");
    }
}
