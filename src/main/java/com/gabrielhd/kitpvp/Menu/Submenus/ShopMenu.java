package com.gabrielhd.kitpvp.Menu.Submenus;

import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.Menu.Menu;
import com.gabrielhd.kitpvp.Menu.Submenus.Shop.*;
import com.gabrielhd.kitpvp.Utils.ItemsBuild;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class ShopMenu extends Menu {

    public ShopMenu() {
        super(KitPvP.getConfigManager().getShopMenu().getString("Title"), KitPvP.getConfigManager().getShopMenu().getInt("Rows"));

        this.updateMenu();
    }

    @Override
    public void onClose(Player player, InventoryCloseEvent event) {}

    @Override
    public void onOpen(Player player, InventoryOpenEvent event) {}

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        FileConfiguration shop = KitPvP.getConfigManager().getShopMenu();

        if(shop.isSet("Guns")) {
            if (event.getSlot() == shop.getInt("Guns.Slot")) {
                new GunsMenu().openInventory(player);
                return;
            }
        }
        if(shop.isSet("Ammo")) {
            if(event.getSlot() == shop.getInt("Ammo.Slot")) {
                new AmmoMenu().openInventory(player);
                return;
            }
        }
        if(shop.isSet("Armor")) {
            if (event.getSlot() == shop.getInt("Armor.Slot")) {
                new ArmorMenu().openInventory(player);
                return;
            }
        }
        if(shop.isSet("Others")) {
            if (event.getSlot() == shop.getInt("Others.Slot")) {
                new OthersMenu().openInventory(player);
                return;
            }
        }
        if(shop.isSet("Blocks")) {
            if (event.getSlot() == shop.getInt("Blocks.Slot")) {
                new BlocksMenu().openInventory(player);
            }
        }
    }

    public void updateMenu() {
        FileConfiguration shop = KitPvP.getConfigManager().getShopMenu();

        if(shop.isSet("Guns")) this.setItem(shop.getInt("Guns.Slot"), ItemsBuild.crearItemEnch(Material.getMaterial(shop.getString("Guns.ID")), 1, shop.getInt("Guns.Data"), shop.getString("Guns.Name"), shop.getStringList("Guns.Description")));
        if(shop.isSet("Ammo")) this.setItem(shop.getInt("Ammo.Slot"), ItemsBuild.crearItemEnch(Material.getMaterial(shop.getString("Ammo.ID")), 1, shop.getInt("Ammo.Data"), shop.getString("Ammo.Name"), shop.getStringList("Ammo.Description")));
        if(shop.isSet("Armor")) this.setItem(shop.getInt("Armor.Slot"), ItemsBuild.crearItemEnch(Material.getMaterial(shop.getString("Armor.ID")), 1, shop.getInt("Armor.Data"), shop.getString("Armor.Name"), shop.getStringList("Armor.Description")));
        if(shop.isSet("Others")) this.setItem(shop.getInt("Others.Slot"), ItemsBuild.crearItemEnch(Material.getMaterial(shop.getString("Others.ID")), 1, shop.getInt("Others.Data"), shop.getString("Others.Name"), shop.getStringList("Others.Description")));
        if(shop.isSet("Blocks")) this.setItem(shop.getInt("Blocks.Slot"), ItemsBuild.crearItemEnch(Material.getMaterial(shop.getString("Blocks.ID")), 1, shop.getInt("Blocks.Data"), shop.getString("Blocks.Name"), shop.getStringList("Blocks.Description")));

        if(shop.isSet("Background") && shop.getBoolean("Background.Enable")) {
            for (int i = 0; i < this.getInventory().getSize(); i++) {
                ItemStack itemStack = this.getInventory().getItem(i);
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    this.setItem(i, ItemsBuild.crearItem(Material.getMaterial(shop.getString("Background.ID")), 1, shop.getInt("Background.Data"), "&f"));
                }
            }
        }
    }
}
