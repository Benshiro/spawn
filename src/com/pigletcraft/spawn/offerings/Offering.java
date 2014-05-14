package com.pigletcraft.spawn.offerings;

import com.pigletcraft.spawn.SpawnPlugin;
import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class Offering {

    protected World world;
    protected SpawnPlugin plugin;
    protected Player player;

    public abstract void grantOffering(Player player, Object object);

    public Offering(SpawnPlugin plugin, World world) {
        this.world = world;
        this.plugin = plugin;
    }
}
