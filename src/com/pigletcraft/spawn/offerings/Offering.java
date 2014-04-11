package com.pigletcraft.spawn.offerings;

import com.pigletcraft.spawn.SpawnPlugin;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Offering {

    protected World world;
    protected SpawnPlugin plugin;
    protected Player player;

    public abstract void grantOffering(Player player);

    public Offering(SpawnPlugin plugin, World world) {
        this.world = world;
        this.plugin = plugin;
    }
}
