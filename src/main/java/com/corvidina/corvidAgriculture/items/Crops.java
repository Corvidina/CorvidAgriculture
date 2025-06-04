package com.corvidina.corvidAgriculture.items;

public enum Crops {
    CARROT,
    POTATO,
    BEETROOT,
    WHEAT,
    MELON_SLICE,
    PUMPKIN,
    NETHER_WART,
    SWEET_BERRIES,
    COCOA_BEANS,
    SUGAR_CANE,
    CACTUS,
    APPLE,
    GLOW_BERRIES,
    CHORUS_FRUIT,

    PRICKLY_PEAR,

    CRANBERRY,
    LEMON,
    BLUEBERRY;

    public static Seeds getCorrespondingSeed(Crops item){
        return switch (item) {
            case BLUEBERRY -> Seeds.BLUEBERRY_SEEDS;
            case CRANBERRY -> Seeds.CRANBERRY_SEEDS;
            case LEMON -> Seeds.LEMON_TREE_SAPLING;
            default -> throw new IllegalArgumentException("Crop type has no seed equivalent");
        };
    }
}


