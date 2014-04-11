package com.pigletcraft.spawn.offerings;

import com.pigletcraft.spawn.SpawnPlugin;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class BeefOffering extends Offering {

    public BeefOffering(SpawnPlugin plugin, World world) {
        super(plugin, world);
    }

    @Override
    public void grantOffering(Player player) {

        World world = player.getWorld();
        Location location = player.getLocation();
        location.setX(location.getX() - 4.0);

        Cow cow = (Cow)world.spawnEntity(location, EntityType.COW);
        cow.setTarget(player);

        player.playSound(player.getLocation(), Sound.COW_IDLE, 1.0F, 1.0F);

        cow.setLeashHolder(player);
        cow.setPassenger(world.spawnEntity(location, EntityType.CREEPER));
        //cow.setVelocity(new Vector(0.8, 0.0, 0.0));
    }
}
