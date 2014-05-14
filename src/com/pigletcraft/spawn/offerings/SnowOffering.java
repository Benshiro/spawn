package com.pigletcraft.spawn.offerings;

import com.pigletcraft.spawn.SpawnPlugin;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Created by Benshiro on 14/05/14.
 */
public class SnowOffering extends Offering {
    public SnowOffering(SpawnPlugin plugin, World world) {
        super(plugin, world);
    }

    @Override
    public void grantOffering(Player player, Object object) {
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Billy is glad your offering isn't yellow.");
    }
}
