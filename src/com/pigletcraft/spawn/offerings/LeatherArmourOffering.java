package com.pigletcraft.spawn.offerings;

import com.pigletcraft.spawn.SpawnPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * Created by Benshiro on 11/04/14.
 */
public class LeatherArmourOffering extends Offering {
    public LeatherArmourOffering(SpawnPlugin plugin, World world) {
        super(plugin, world);
    }

    @Override
    public void grantOffering(Player player, Object object) {
        Item item = (Item) object;

        ItemStack itemStack = item.getItemStack();

        ItemMeta meta = itemStack.getItemMeta();
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
        armorMeta.setColor(Color.fromRGB(0xf03d7b));
        switch (itemStack.getType()){
            case LEATHER_HELMET:
                meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Pig Hat");
                break;
            case LEATHER_CHESTPLATE:
                meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Pig Vest");
                break;
            case LEATHER_LEGGINGS:
                meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Pig Pants");
                break;
            case LEATHER_BOOTS:
                meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Pig Boots");
                break;

        }
        itemStack.setItemMeta(armorMeta);
        player.setItemInHand(itemStack);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Billy has blessed your armour!");


    }
}
