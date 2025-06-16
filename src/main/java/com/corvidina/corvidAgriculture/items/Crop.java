package com.corvidina.corvidAgriculture.items;

public enum Crop implements AgricultureItem {
    CARROT ("Carrot"),
    POTATO ("Potato"),
    BEETROOT ("Beetroot"),
    WHEAT ("Wheat"),
    MELON_SLICE ("Melon Slice"),
    NETHER_WART ("Nether Wart"),
    SWEET_BERRIES ("Sweet Berries"),
    COCOA_BEANS ("Cocoa Beans"),
    APPLE ("Apple"),
    GLOW_BERRIES ("Glow Berries"),
    CHORUS_FRUIT ("Chorus Fruit"),

    PRICKLY_PEAR ("Prickly Pear"),

    CRANBERRY ("Cranberry"),
    LEMON ("Lemon"),
    RICE ("Rice"),
    BLUEBERRY ("Blueberry"),
    TOMATO ("Tomato"),
    BANANA ("Banana"),
    LETTUCE ("Lettuce")
    ;

    private final String name;
    private Crop(String name) {
        this.name=name;
    }

    public String toName(){
        return name;
    }

    public static Seeds getCorrespondingSeed(Crop item){
        return switch (item) {
            case BLUEBERRY -> Seeds.BLUEBERRY_SEEDS;
            case CRANBERRY -> Seeds.CRANBERRY_SEEDS;
            case LEMON -> Seeds.LEMON_TREE_SAPLING;
            case TOMATO -> Seeds.TOMATO_SEEDS;
            case BANANA -> Seeds.BANANA_TREE_SAPLING;
            case RICE -> Seeds.RICE;
            case LETTUCE -> Seeds.LETTUCE_SEEDS;
            default -> throw new IllegalArgumentException("Crop type has no seed equivalent");
        };
    }
}


