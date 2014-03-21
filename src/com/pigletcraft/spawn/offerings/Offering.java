package com.pigletcraft.spawn.offerings;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Offering {

    protected World world;
    protected JavaPlugin plugin;
    protected Player player;

    public abstract void grantOffering(Player player);

    public Offering(JavaPlugin plugin, World world) {
        this.world = world;
        this.plugin = plugin;

    }
}
