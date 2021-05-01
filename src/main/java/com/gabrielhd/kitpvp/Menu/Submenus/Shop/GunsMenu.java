package com.gabrielhd.kitpvp.Menu.Submenus.Shop;

import com.gabrielhd.guns.Enums.Rarity;
import com.gabrielhd.guns.Guns.Guns;
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

public class GunsMenu extends Menu {

    public GunsMenu() {
        super(KitPvP.getConfigManager().getGunsMenu().getString("Title"), KitPvP.getConfigManager().getGunsMenu().getInt("Rows"));

        this.updateMenu();
    }

    @Override
    public void onClose(Player player, InventoryCloseEvent event) {}

    @Override
    public void onOpen(Player player, InventoryOpenEvent event) {}

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        FileConfiguration messages = KitPvP.getConfigManager().getMessages();
        FileConfiguration storeConfig = KitPvP.getConfigManager().getGunsMenu();

        PlayerData playerData = KitPvP.getPlayerManager().getPlayerData(player);
        if(playerData != null){
            for (String section: storeConfig.getConfigurationSection("Items").getKeys(false)){
                if(event.getSlot() == storeConfig.getInt("Items." + section + ".Slot")){
                    double price = storeConfig.getDouble("Items." + section + ".Price");
                    if (playerData.getCoins() < price){
                        player.sendMessage(KitPvP.Color(messages.getString("InsufficientMoney")));
                        return;
                    }

                    playerData.setCoins(playerData.getCoins() - price);

                    Guns guns = GunsPlugin.getWeaponManager().getGun(storeConfig.getString("Items." + section + ".Gun.Type"));
                    if(guns != null) {
                        guns.giveGunItem(player, Rarity.getRarity(storeConfig.getString("Items."+section+".Gun.Rarity")));
                    }
                }
            }
        }
    }

    public void updateMenu() {
        FileConfiguration gunsConfig = KitPvP.getConfigManager().getGunsMenu();

        for (String section : gunsConfig.getConfigurationSection("Items").getKeys(false)) {
            this.setItem(gunsConfig.getInt("Items." + section + ".Slot"), ItemsBuild.crearItemEnch(Material.getMaterial(gunsConfig.getString("Items." + section + ".ID")), 1, gunsConfig.getInt("Items." + section + ".Data"), gunsConfig.getString("Items." + section + ".Name"), replacer(gunsConfig.getStringList("Items." + section + ".Description"), section)));
        }

        if(gunsConfig.isSet("Background") && gunsConfig.getBoolean("Background.Enable")) {
            for (int i = 0; i < this.getInventory().getSize(); i++) {
                ItemStack itemStack = this.getInventory().getItem(i);
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    this.setItem(i, ItemsBuild.crearItem(Material.getMaterial(gunsConfig.getString("Background.ID")), 1, gunsConfig.getInt("Background.Data"), "&f"));
                }
            }
        }
    }

    public List<String> replacer(List<String> lores, String section) {
        FileConfiguration storeConfig = KitPvP.getConfigManager().getGunsMenu();

        List<String> newLore = Lists.newArrayList();
        for (String s : lores) {
            newLore.add(KitPvP.Color(s.replace("%price%", String.valueOf(storeConfig.getDouble("Items." + section + ".Price")))));
        }

        return newLore;
    }
}
