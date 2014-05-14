package com.pigletcraft.spawn.offerings;

import com.pigletcraft.spawn.SpawnPlugin;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Benshiro on 14/05/14.
 */
public class DiamondOffering extends Offering {
    public DiamondOffering(SpawnPlugin plugin, World world) {
        super(plugin, world);
    }

    @Override
    public void grantOffering(Player player, Object object) {
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Billy is impressed by your generosity, and rewards you with an incredible gift.");
        ItemStack itemStack = new ItemStack(383, 1, (byte) 90);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Blessing of Billy");
        itemStack.setItemMeta(meta);
        player.setItemInHand(itemStack);

    }
}
