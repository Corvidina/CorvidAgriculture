package com.corvidina.corvidAgriculture.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;

public class HexMarker extends ArmorStand {

    public HexMarker(Level level) {
        super(EntityType.ARMOR_STAND, level);
    }

    @Override
    public void tick(){
        super.tick();

        //implement features (AOE effects, animation, etc)
    }




}
