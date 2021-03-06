package com.pigletcraft.spawn.offerings;

import com.pigletcraft.spawn.SpawnPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Benshiro on 11/04/14.
 */
public class GoldenCarrotOffering extends Offering {
    public GoldenCarrotOffering(SpawnPlugin plugin, World world) {
        super(plugin, world);
    }

    @Override
    public void grantOffering(Player player, Object object) {
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Billy is very pleased!");
               player.sendMessage(ChatColor.LIGHT_PURPLE + "Max health increased!");

               if (player.hasPotionEffect(PotionEffectType.HEALTH_BOOST)){
                   player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
               }

               PotionEffect healthBoost = PotionEffectType.HEALTH_BOOST.createEffect(36000,0);
               player.addPotionEffect(healthBoost);
               player.setHealth(28);
               Location location = player.getLocation();
               player.playSound(location, Sound.PIG_IDLE,1,1);


    }
}
