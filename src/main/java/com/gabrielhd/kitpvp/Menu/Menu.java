package com.gabrielhd.kitpvp.Menu;

import com.gabrielhd.kitpvp.KitPvP;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class Menu implements Listener {

    Inventory inventory;

    public Menu(String name, int rows) {
        this.inventory = Bukkit.createInventory(null, 9 * rows, KitPvP.Color(name));
        KitPvP.getInstance().getServer().getPluginManager().registerEvents(this, KitPvP.getInstance());
    }

    public void addItem(ItemStack stack) {
        this.inventory.addItem(stack);
    }

    public void setItem(int i, ItemStack stack) {
        this.inventory.setItem(i, stack);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void openInventory(Player p) {
        p.openInventory(this.inventory);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().equals(this.inventory) && event.getPlayer() instanceof Player) {
            this.onOpen((Player) event.getPlayer(), event);
        }
    }

    @EventHandler
    public void itemMove(InventoryMoveItemEvent event) {
        if (event.getDestination().equals(this.inventory)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().equals(this.inventory) && event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR && event.getWhoClicked() instanceof Player) {
            this.onClick((Player) event.getWhoClicked(), event);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(this.inventory) && event.getPlayer() instanceof Player) {
            this.onClose((Player) event.getPlayer(), event);
        }
    }

    public abstract void onClose(Player player, InventoryCloseEvent event);

    public abstract void onOpen(Player player, InventoryOpenEvent event);

    public abstract void onClick(Player player, InventoryClickEvent event);
}
