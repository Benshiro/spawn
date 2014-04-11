package com.pigletcraft.spawn.offerings;

import com.pigletcraft.spawn.SpawnPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class CarrotOffering extends Offering {

    public CarrotOffering(SpawnPlugin plugin, World world) {
        super(plugin, world);
    }

    @Override
    public void grantOffering(Player player) {
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Billy is pleased!");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Health and hunger restored!");
        if (player.hasPotionEffect(PotionEffectType.HEALTH_BOOST)) {
            player.setHealth(28);

        } else {
            player.setHealth(20);
        }

        player.setFoodLevel(20);
        player.setSaturation(10);
        Location location = player.getLocation();
        player.playSound(location, Sound.PIG_IDLE, 1, 1);


    }
}
