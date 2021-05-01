package com.gabrielhd.kitpvp.Utils;

import com.gabrielhd.kitpvp.KitPvP;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemsBuild {

    public static ItemStack crearNormal(Material id, int amount, int data) {
        return new ItemStack(id, amount, (short)data);
    }

    public static ItemStack crearItem(Material id, int amount, int data, String name) {
        ItemStack item = new ItemStack(id, amount, (short) data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(KitPvP.Color(name));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack crearItemEnch(Material id, int amount, int data, String name, List<String> lore) {
        ItemStack item = new ItemStack(id, amount, (short) data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(KitPvP.Color(name));
        ArrayList<String> color = new ArrayList<>();
        for (String b : lore) {
            color.add(KitPvP.Color(b));
        }
        meta.setLore(color);
        item.setItemMeta(meta);
        return item;
    }
}