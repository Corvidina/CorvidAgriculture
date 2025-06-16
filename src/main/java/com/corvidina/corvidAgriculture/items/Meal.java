package com.corvidina.corvidAgriculture.items;

import org.bukkit.Material;

public enum Meal implements AgricultureItem {
    // Must be in order from the fewest ingredients to most in order to avoid overlap problems

    CHICKEN_POT_PIE("Chicken Pot Pie",
            CraftingData.Builder.init()
                .addIngredient(Material.BOWL, 1)
                .addIngredient(Material.COOKED_CHICKEN, 1)
                .addIngredient(Crop.CARROT, 2)
                .addIngredient(HelperIngredient.DOUGH, 1)
                .build()
    ),
    BEEF_STEW ("Beef Stew",
            CraftingData.Builder.init()
                .addIngredient(Material.BOWL, 1)
                .addIngredient(Material.COOKED_BEEF, 3)
                .addIngredient(Crop.CARROT, 2)
                .addIngredient(Crop.POTATO,2)
                .build()
    ),
    HAMBURGER ( "Hamburger",
            CraftingData.Builder.init()
                    .addIngredient(Material.BREAD, 1)
                    .addIngredient(Material.COOKED_BEEF,1)
                    .addIngredient(Crop.TOMATO,1)
                    .addIngredient(Crop.LETTUCE, 1)
                    .build()
    ),
    LEMON_CHEESECAKE ( "Lemon Cheesecake",
            CraftingData.Builder.init()
                    .addIngredient(Crop.LEMON, 3)
                    .addIngredient(Material.MILK_BUCKET, 1)
                    .build()
    ),
    BLUEBERRY_PIE ("Blueberry Pie",
            CraftingData.Builder.init()
                .addIngredient(Crop.BLUEBERRY, 4)
                .addIngredient(HelperIngredient.DOUGH, 1)
                .build()
    ),

    ;
    private final CraftingData data;
    private final String name;
    Meal(String name, CraftingData data){
        this.name=name;
        this.data=data;
    }

    public String toName(){
        return name;
    }

    public CraftingData getCraftingData(){
        return data;
    }
}
