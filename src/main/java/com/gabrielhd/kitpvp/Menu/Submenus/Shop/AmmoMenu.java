package com.gabrielhd.kitpvp.Menu.Submenus.Shop;

import com.gabrielhd.guns.Guns.Ammo;
import com.gabrielhd.guns.GunsPlugin;
import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.Menu.Menu;
import com.gabrielhd.kitpvp.Player.PlayerData;
import com.gabrielhd.kitpvp.Utils.ItemsBuild;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AmmoMenu extends Menu {

    public AmmoMenu() {
        super(KitPvP.getConfigManager().getAmmoMenu().getString("Title"), KitPvP.getConfigManager().getAmmoMenu().getInt("Rows"));

        this.updateMenu();
    }

    @Override
    public void onClose(Player player, InventoryCloseEvent event) {}

    @Override
    public void onOpen(Player player, InventoryOpenEvent event) {}

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        FileConfiguration messages = KitPvP.getConfigManager().getMessages();
        FileConfiguration storeConfig = KitPvP.getConfigManager().getAmmoMenu();

        PlayerData playerData = KitPvP.getPlayerManager().getPlayerData(player);
        if(playerData != null){
            for (String section: storeConfig.getConfigurationSection("Items").getKeys(false)){
                if(event.getSlot() == storeConfig.getInt("Items." + section + ".Slot")) {
                    double price = storeConfig.getDouble("Items." + section + ".Price");
                    if (playerData.getCoins() < price) {
                        player.sendMessage(KitPvP.Color(messages.getString("InsufficientMoney")));
                        return;
                    }

                    playerData.setCoins(playerData.getCoins() - price);

                    Ammo ammo = GunsPlugin.getWeaponManager().getAmmo(storeConfig.getString("Items." + section + ".Ammo.Type"));
                    if (ammo != null) {
                        for (int i = 0; i < storeConfig.getInt("Items." + section + ".Ammo.Amount"); i++) {
                            ammo.giveAmmoItem(player);
                        }
                    }
                }
            }
        }
    }

    public void updateMenu(){
        FileConfiguration ammoConfig = KitPvP.getConfigManager().getAmmoMenu();

        for(String section : ammoConfig.getConfigurationSection("Items"). getKeys(false)){
            this.setItem(ammoConfig.getInt("Items."+section+".Slot"), ItemsBuild.crearItemEnch(Material.getMaterial(ammoConfig.getString("Items."+section+".ID")), 1, ammoConfig.getInt("Items."+section+".Data"), ammoConfig.getString("Items."+section+".Name"), replacer(ammoConfig.getStringList("Items."+section+".Description"),section)));
        }

        if(ammoConfig.isSet("Background") && ammoConfig.getBoolean("Background.Enable")) {
            for (int i = 0; i < this.getInventory().getSize(); i++) {
                ItemStack itemStack = this.getInventory().getItem(i);
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    this.setItem(i, ItemsBuild.crearItem(Material.getMaterial(ammoConfig.getString("Background.ID")), 1, ammoConfig.getInt("Background.Data"), "&f"));
                }
            }
        }
    }

    public List<String> replacer(List<String> lores, String section) {
        FileConfiguration storeConfig = KitPvP.getConfigManager().getAmmoMenu();

        List<String> newLore = Lists.newArrayList();
        for(String s : lores) {
            newLore.add(KitPvP.Color(s.replace("%price%", String.valueOf(storeConfig.getDouble("Items."+section+".Price")))));
        }

        return newLore;
    }
}


