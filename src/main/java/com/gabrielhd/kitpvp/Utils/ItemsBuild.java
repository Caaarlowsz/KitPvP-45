package com.gabrielhd.kitpvp.Utils;

import com.gabrielhd.guns.Utils.NBTItem;
import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.Quests.Quest;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

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

    public static ItemStack crearCabeza(String owner, String name, List<String> lore) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

        SkullMeta meta = (SkullMeta)item.getItemMeta();
        meta.setOwner(owner);
        meta.setDisplayName(KitPvP.Color(name));
        if(lore != null) {
            ArrayList<String> color = new ArrayList<>();
            for (String b : lore) {
                color.add(KitPvP.Color(b));
            }
            meta.setLore(color);
        }
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

    public static ItemStack crearItemEnch(Material id, int amount, int data, String name, List<String> lore, Quest quest) {
        ItemStack item = new ItemStack(id, amount, (short) data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(KitPvP.Color(name));
        ArrayList<String> color = new ArrayList<>();
        for (String b : lore) {
            color.add(KitPvP.Color(b));
        }
        meta.setLore(color);
        item.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("QuestID", quest.getMissionID());
        nbtItem.setString("QuestType", quest.getQuestType().name());

        return nbtItem.getItem();
    }
}