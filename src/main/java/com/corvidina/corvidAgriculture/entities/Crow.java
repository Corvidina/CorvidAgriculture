package com.corvidina.corvidAgriculture.entities;

import com.corvidina.corvidAgriculture.items.CorvidAgricultureItems;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class Crow extends Vex {
    private final Logger logger = Bukkit.getLogger();
    //Crow entity to extend base Vex behavior and mob data with a few server side brain changes.
    public Crow(Level level) {
        super(EntityType.VEX, level);
    }

    @Override
    public void tick(){
        super.tick();
        // Prevent phasing
        if (level().getBlockState(super.blockPosition()).getBukkitMaterial().isSolid()) {
            super.setDeltaMovement(0, 0, 0);
            //Implement move out of block logic
        }
        this.setCustomName(
                Component.literal("Crow").withColor(0).withStyle(ChatFormatting.BOLD)
        );
        this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);


    }

    @Override
    public void postDeathDropItems(@NotNull EntityDeathEvent event){
        super.postDeathDropItems(event);
        if(Math.random()<0.33) {
            ItemEntity item = new ItemEntity(level(), getX(), getY(), getZ(), ItemHandler.buildItem(CorvidAgricultureItems.CROW_FEATHER));
            level().getWorld().addEntity(item, CreatureSpawnEvent.SpawnReason.CUSTOM);
        }
    }



    @Override
    protected void registerGoals(){
        /*temporary*/ super.registerGoals();

        //implement custom pathfinding eventually

    }

}
