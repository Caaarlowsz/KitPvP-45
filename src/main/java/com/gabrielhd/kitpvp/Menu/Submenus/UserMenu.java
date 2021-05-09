package com.gabrielhd.kitpvp.Menu.Submenus;

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

import java.text.DecimalFormat;
import java.util.List;

public class UserMenu extends Menu {

    public UserMenu(Player player) {
        super(KitPvP.getConfigManager().getUserMenu().getString("Title").replace("%player%", player.getName()), KitPvP.getConfigManager().getUserMenu().getInt("Rows"));

        this.updateMenu(player);
    }

    @Override
    public void onClose(Player player, InventoryCloseEvent event) {}

    @Override
    public void onOpen(Player player, InventoryOpenEvent event) {}

    @Override
    public void onClick(Player player, InventoryClickEvent event) {}

    public void updateMenu(Player player) {
        FileConfiguration userMenu = KitPvP.getConfigManager().getUserMenu();
        PlayerData playerData = KitPvP.getPlayerManager().getPlayerData(player);

        if(playerData != null) {
            for (String section : userMenu.getConfigurationSection("Items").getKeys(false)) {
                Material material = Material.getMaterial(userMenu.getString("Items." + section + ".ID"));
                if(material == Material.SKULL_ITEM) {

                } else {
                    this.setItem(userMenu.getInt("Items." + section + ".Slot"), ItemsBuild.crearItemEnch(material, 1, userMenu.getInt("Items." + section + ".Data"), userMenu.getString("Items." + section + ".Name"), replacer(userMenu.getStringList("Items." + section + ".Description"), playerData)));
                }
            }

            if (userMenu.isSet("Background") && userMenu.getBoolean("Background.Enable")) {
                for (int i = 0; i < this.getInventory().getSize(); i++) {
                    ItemStack itemStack = this.getInventory().getItem(i);
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        this.setItem(i, ItemsBuild.crearItem(Material.getMaterial(userMenu.getString("Background.ID")), 1, userMenu.getInt("Background.Data"), "&f"));
                    }
                }
            }
        }
    }

    public List<String> replacer(List<String> lores, PlayerData playerData) {
        FileConfiguration storeConfig = KitPvP.getConfigManager().getOthersMenu();

        List<String> newLore = Lists.newArrayList();
        DecimalFormat df2 = new DecimalFormat("#.##");
        for(String s : lores) {
            s = s.replace("%kills%", String.valueOf(playerData.getKills()));
            s = s.replace("%deaths%", String.valueOf(playerData.getDeaths()));
            s = s.replace("%killstreak%", String.valueOf(playerData.getKillstreak()));
            s = s.replace("%coins%", String.valueOf(playerData.getCoins()));
            s = s.replace("%level%", String.valueOf(playerData.getLevel()));
            s = s.replace("%kdr%", df2.format(playerData.getKilltodeathratio()));

            newLore.add(KitPvP.Color(s));
        }

        return newLore;
    }
}
