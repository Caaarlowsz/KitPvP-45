package com.gabrielhd.kitpvp.Menu.Submenus.Shop;

import com.gabrielhd.guns.Enums.Rarity;
import com.gabrielhd.guns.Guns.Armor;
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

public class ArmorMenu extends Menu {

    public ArmorMenu() {
        super(KitPvP.getConfigManager().getArmorMenu().getString("Title"), KitPvP.getConfigManager().getArmorMenu().getInt("Rows"));

        this.updateMenu();
    }

    @Override
    public void onClose(Player player, InventoryCloseEvent event) {}

    @Override
    public void onOpen(Player player, InventoryOpenEvent event) {}

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        FileConfiguration messages = KitPvP.getConfigManager().getMessages();
        FileConfiguration storeConfig = KitPvP.getConfigManager().getArmorMenu();

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

                    Armor armor = GunsPlugin.getWeaponManager().getArmor(storeConfig.getString("Items." + section + ".Armor.Type"));
                    if(armor != null) {
                        if(storeConfig.getString("Items."+section+".Armor.Slot").equalsIgnoreCase("helmet")) {
                            armor.giveHelmet(player, Rarity.getRarity(storeConfig.getString("Items."+section+".Armor.Rarity")), true);
                        }
                        if(storeConfig.getString("Items."+section+".Armor.Slot").equalsIgnoreCase("chestplate")) {
                            armor.giveChestplate(player, Rarity.getRarity(storeConfig.getString("Items."+section+".Armor.Rarity")), true);
                        }
                        if(storeConfig.getString("Items."+section+".Armor.Slot").equalsIgnoreCase("leggings")) {
                            armor.giveLeggings(player, Rarity.getRarity(storeConfig.getString("Items."+section+".Armor.Rarity")), true);
                        }
                        if(storeConfig.getString("Items."+section+".Armor.Slot").equalsIgnoreCase("boots")) {
                            armor.giveBoots(player, Rarity.getRarity(storeConfig.getString("Items."+section+".Armor.Rarity")), true);
                        }
                    }
                }
            }
        }

    }
    public void updateMenu() {
        FileConfiguration storeConfig = KitPvP.getConfigManager().getArmorMenu();

        for(String section : storeConfig.getConfigurationSection("Items").getKeys(false)) {
            this.setItem(storeConfig.getInt("Items."+section+".Slot"), ItemsBuild.crearItemEnch(Material.getMaterial(storeConfig.getString("Items." + section + ".ID")), 1, storeConfig.getInt("Items." + section + ".Data"), storeConfig.getString("Items." + section + ".Name"), replacer(storeConfig.getStringList("Items." + section + ".Description"), section)));
        }

        if(storeConfig.isSet("Background") && storeConfig.getBoolean("Background.Enable")) {
            for (int i = 0; i < this.getInventory().getSize(); i++) {
                ItemStack itemStack = this.getInventory().getItem(i);
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    this.setItem(i, ItemsBuild.crearItem(Material.getMaterial(storeConfig.getString("Background.ID")), 1, storeConfig.getInt("Background.Data"), "&f"));
                }
            }
        }
    }

    public List<String> replacer(List<String> lores, String section) {
        FileConfiguration storeConfig = KitPvP.getConfigManager().getArmorMenu();

        List<String> newLore = Lists.newArrayList();
        for(String s : lores) {
            newLore.add(KitPvP.Color(s.replace("%price%", String.valueOf(storeConfig.getDouble("Items."+section+".Price")))));
        }

        return newLore;
    }
}




