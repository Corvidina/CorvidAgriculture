package com.corvidina.corvidAgriculture.items;

public enum Seeds implements AgricultureItem {
    LEMON_TREE_SAPLING ("Lemon Tree Sapling"),
    CRANBERRY_SEEDS ("Cranberry Seeds"),
    BLUEBERRY_SEEDS ("Blueberry Seeds"),
    RICE ("Rice"),
    TOMATO_SEEDS ("Tomato Seeds"),
    BANANA_TREE_SAPLING("Banana Tree Sapling"),
    LETTUCE_SEEDS("Lettuce Seeds")

    ;
    private final String name;
    Seeds(String name){
        this.name=name;
    }
    public String toName(){
        return name;
    }
}
