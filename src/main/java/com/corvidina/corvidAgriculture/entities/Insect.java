package com.corvidina.corvidAgriculture.entities;

import com.corvidina.corvidAgriculture.items.MobDrops;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class Insect extends Silverfish {
    private final Logger logger = Bukkit.getLogger();
    public Insect(Level level){
        super(EntityType.SILVERFISH,level);
        this.setCustomName(
                Component.literal("Insect").withStyle(ChatFormatting.DARK_GREEN,ChatFormatting.BOLD)
        );
    }

    @Override
    public void postDeathDropItems(@NotNull EntityDeathEvent event){
        super.postDeathDropItems(event);
        if(Math.random()<0.2) {
            ItemEntity item = new ItemEntity(level(), getX(), getY(), getZ(), ItemHandler.buildItem(MobDrops.INSECT_CARAPACE));
            level().getWorld().addEntity(item, CreatureSpawnEvent.SpawnReason.CUSTOM);
            item = new ItemEntity(level(), getX(), getY(), getZ(), ItemHandler.buildItem(MobDrops.INSECT_LARVAE));
            level().getWorld().addEntity(item, CreatureSpawnEvent.SpawnReason.CUSTOM);
        }
    }

}
