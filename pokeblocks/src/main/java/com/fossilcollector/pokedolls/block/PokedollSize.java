package com.fossilcollector.pokedolls.block;

import net.minecraft.util.StringIdentifiable;

public enum PokedollSize implements StringIdentifiable {
    SMALL("small", 0.75F),
    DOLL("doll", 1.0F),
    NORMAL("normal", 2.0F),
    GIANT("giant", 3.5F),
    ENORMOUS("enormous", 5.0F);

    private final String name;
    private final float scaleMultiplier;

    PokedollSize(String name, float scaleMultiplier) {
        this.name = name;
        this.scaleMultiplier = scaleMultiplier;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public float getScaleMultiplier() {
        return this.scaleMultiplier;
    }
}
