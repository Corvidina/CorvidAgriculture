package com.corvidina.corvidAgriculture.items;

public enum HelperIngredient implements AgricultureItem {
    DOUGH ("Dough", CraftingData.Builder.init()
            .addIngredient(Crop.WHEAT, 3)
            .setRequiresWaterCauldron(true)
            .build()
    )
    ;

    private CraftingData data;
    private final String name;
    HelperIngredient(String name, CraftingData data) {
        this.name=name;
        this.data=data;
    }
    public String toName(){
        return  name;
    }
    public CraftingData getCraftingData() {
        return data;
    }
}
