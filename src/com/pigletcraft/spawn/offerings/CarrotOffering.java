package com.pigletcraft.spawn.offerings;

import com.pigletcraft.spawn.SpawnPlugin;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CarrotOffering extends Offering {

    public CarrotOffering(SpawnPlugin plugin, World world) {
        super(plugin, world);
    }

    @Override
    public void grantOffering(Player player) {
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Billy is pleased!");


    }
}
