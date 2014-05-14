package com.pigletcraft.spawn.offerings;

import com.pigletcraft.spawn.SpawnPlugin;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Created by Benshiro on 13/05/14.
 */
public class GrassOffering extends Offering {
    public GrassOffering(SpawnPlugin plugin, World world) {
        super(plugin, world);
    }

    @Override
    public void grantOffering(Player player, Object object) {
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Billy starts singing: " + ChatColor.GOLD + "'The old home town looks the same...'");

    }
}
