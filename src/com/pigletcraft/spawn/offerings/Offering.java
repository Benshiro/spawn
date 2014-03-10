package com.pigletcraft.spawn.offerings;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Offering {

    protected World world;
    protected JavaPlugin plugin;

    public abstract void grantOffering();

    public Offering(JavaPlugin plugin, World world) {
        this.world = world;
        this.plugin = plugin;
    }
}
