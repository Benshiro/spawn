package com.pigletcraft.spawn.offerings;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CarrotOffering extends Offering {

    public CarrotOffering(JavaPlugin plugin, World world) {
        super(plugin, world);
    }

    @Override
    public void grantOffering(Player player) {
        player.sendMessage("Billy is pleased!");


    }
}
