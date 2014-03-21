package com.pigletcraft.spawn.offerings;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ConcurrentHashMap;

public class PorkChopOffering extends Offering {

    private ConcurrentHashMap<BillyBomber, BukkitTask> billyBomberHashMap;

    public PorkChopOffering(JavaPlugin plugin, World world) {
        super(plugin, world);
        this.billyBomberHashMap = new ConcurrentHashMap<>();
    }

    @Override
    public void grantOffering(Player player) {
        BillyBomber billyBomber = new BillyBomber(0);
        billyBomberHashMap.put(billyBomber, Bukkit.getScheduler().runTaskTimer(super.plugin, billyBomber, 0, 2));
    }

    private class BillyBomber implements Runnable {

        int cycles;

        public BillyBomber(int cycles) {
            this.cycles = cycles;
        }

        @Override
        public void run() {
            Location pillarOne = new Location(Bukkit.getWorld("world"), 534, 23, -217);
            for (int i = 0; i < 20; i++) {
                Bukkit.getWorld("world").spawnEntity(pillarOne, EntityType.PRIMED_TNT);
            }

            cycles++;
            if (cycles >= 10) {
                billyBomberHashMap.get(this).cancel();
            }
        }

    }
}
