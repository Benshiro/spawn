package com.pigletcraft.spawn.offerings;

import com.pigletcraft.spawn.SpawnPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Created by Benshiro on 11/04/14.
 */
public class CakeOffering extends Offering {
    public CakeOffering(SpawnPlugin plugin, World world) {
        super(plugin, world);
    }

    @Override
    public void grantOffering(Player player) {
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Billy thinks your offering is a lie...");
             Location location = player.getLocation();
        player.playSound(location, Sound.PIG_IDLE, 1f, 1.1f);

    }
}
