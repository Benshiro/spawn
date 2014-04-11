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
public class GravelOffering extends Offering {
    public GravelOffering(SpawnPlugin plugin, World world) {
        super(plugin, world);
    }

    @Override
    public void grantOffering(Player player) {
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Billy is unimpressed, nobody likes gravel.");
        if (player.hasPotionEffect(PotionEffectType.BLINDNESS)){
                   player.removePotionEffect(PotionEffectType.BLINDNESS);
               }

               PotionEffect blindness = PotionEffectType.BLINDNESS.createEffect(400,1);
               player.addPotionEffect(blindness);
               Location location = player.getLocation();
               player.playSound(location, Sound.PIG_IDLE,1f,0.3f);

    }
}
