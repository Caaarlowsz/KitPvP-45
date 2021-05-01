package com.gabrielhd.kitpvp.Menu.Submenus;

import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.Menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class UserMenu extends Menu {

    public UserMenu(Player player) {
        super(KitPvP.getConfigManager().getUserMenu().getString("Title").replace("%player%", player.getName()), KitPvP.getConfigManager().getUserMenu().getInt("Rows"));
    }

    @Override
    public void onClose(Player player, InventoryCloseEvent event) {

    }

    @Override
    public void onOpen(Player player, InventoryOpenEvent event) {

    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {

    }
}
