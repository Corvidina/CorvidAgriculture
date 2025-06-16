package com.corvidina.corvidAgriculture;

import com.corvidina.corvidAgriculture.items.*;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CateringOption {

    public enum Options {
        NICO_LEMONY_DELIVERY(new CateringOption()
                .addMeal(Crop.LEMON, 64),
                ItemHandler.playerHead("Nico"),
                "A lemony delivery for Nico",
                CateringReward.Builder.init()
                        .setRewardedMoney(400000)
                        .setRewardedVermiculite(20000)
                        .addItemReward(MobDrops.CROW_FEATHER, 3)
                        .addItemReward(MobDrops.INSECT_LARVAE, 2)
                        .build()
        ),


        ;
        private final CateringOption option;
        private final CateringReward cateringReward;
        private final ItemStack displayItem;

        private Options(CateringOption option, ItemStack displayItem, String name, CateringReward reward) {
            this.option=option;
            this.cateringReward=reward;
            this.displayItem=displayItem;

            displayItem.setData(DataComponentTypes.ITEM_NAME, Component.text()
                    .content(name)
                    .style(Style.style(TextColor.color(0xFFFFFF),TextDecoration.ITALIC.withState(false)))
                    .build()
            );

            ItemLore.Builder builder = ItemLore.lore();
            builder.addLine(
                    Component.text()
                            .content("Desired Items: ")
                            .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                            .build()
            );

            for (Object object : option.getMealMap().keySet()) {
                if(object instanceof AgricultureItem agricultureItem) {
                    builder.addLine(
                            Component.text()
                                    .content("    ● " + option.getMealMap().get(object) + " " + agricultureItem.toName())
                                    .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                    );
                } else if (object instanceof Material material) {
                    builder.addLine(
                            Component.text()
                                    .content("    ● " + option.getMealMap().get(object) + " " + toSpacedCamelCase(material.toString()))
                                    .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                    );
                }
            }

            builder.addLine(Component.text(""));

            builder.addLine(
                    Component.text()
                            .content("Rewards: ")
                            .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
            );

            for (AgricultureItem item : reward.getRewards().keySet()) {
                builder.addLine(
                        Component.text()
                                .content("    ● " + reward.getRewards().get(item)+"x "+item.toName())
                                .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false)))
                );
            }

            if(reward.getRewardedVermiculite()>0) {
                builder.addLine(
                        Component.text()
                                .content("    ● " + reward.getRewardedVermiculite() + " vermiculite")
                                .style(Style.style(TextColor.color(0xCBB9),TextDecoration.ITALIC.withState(false)))
                );
            }

            if(reward.getRewardedMoney()>0) {
                builder.addLine(
                        Component.text()
                                .content("    ● $" + reward.getRewardedMoney())
                                .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false)))
                );
            }

            displayItem.setData(DataComponentTypes.LORE, builder.build());
        }

        private String toSpacedCamelCase(String string){
            StringBuilder ret = new StringBuilder();
            ret.append(string.substring(0, 1));
            boolean lastWasSpace = false;
            for (int i = 1; i < string.length(); i++) {
                if(lastWasSpace) {
                    ret.append(string.substring(i, i + 1).toUpperCase());
                    lastWasSpace=false;
                } else {
                     if(string.charAt(i)=='_') {
                         ret.append(' ');
                         lastWasSpace=true;
                     } else {
                         ret.append(string.substring(i,i+1).toLowerCase());
                     }
                }
            }
            return ret.toString();
        }

        public CateringOption getCateringOption(){
            return option;
        }

        public ItemStack getDisplayItem(){
            return displayItem;
        }

        public CateringReward getCateringReward(){
            return cateringReward;
        }
    }

    public CateringOption() {
        mealMap = new HashMap<>();
    }

    private HashMap<Object, Integer> mealMap;

    public CateringOption addMeal(AgricultureItem ingredient, int amount) {
        mealMap.put(ingredient,amount);
        return this;
    }

    public HashMap<Object, Integer> getMealMap(){
        return mealMap;
    }
}
