package com.corvidina.corvidAgriculture.items;

public enum MobDrops implements AgricultureItem {
    CROW_FEATHER("Crow Feather"),
    INSECT_LARVAE("Insect Larvae"),
    INSECT_CARAPACE("Insect Carapace"),

    ;
    private final String name;
    MobDrops(String name){
        this.name=name;
    }
    public String toName(){
        return name;
    }
}
