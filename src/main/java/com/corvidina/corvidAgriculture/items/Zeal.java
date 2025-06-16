package com.corvidina.corvidAgriculture.items;

public enum Zeal {
    REPLANTER("Replanter", (byte)7),


    ;
    private String name;
    private byte maxLevel;
    Zeal(String name, byte maxLevel) {
        this.name=name;
        this.maxLevel = maxLevel;
    }
    public String toName(){
        return name;
    }
    public byte getMaxLevel(){
        return maxLevel;
    }
}
