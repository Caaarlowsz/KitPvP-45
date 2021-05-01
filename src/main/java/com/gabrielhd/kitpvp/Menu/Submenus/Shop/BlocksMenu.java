package com.gabrielhd.kitpvp.Menu.Submenus.Shop;

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

public class BlocksMenu extends Menu {

    public BlocksMenu() {
        super(KitPvP.getConfigManager().getBlocksMenu().getString("Title"), KitPvP.getConfigManager().getBlocksMenu().getInt("Rows"));

        this.updateMenu();
    }

    @Override
    public void onClose(Player player, InventoryCloseEvent event) {}

    @Override
    public void onOpen(Player player, InventoryOpenEvent event) {}

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        FileConfiguration messages = KitPvP.getConfigManager().getMessages();
        FileConfiguration storeConfig = KitPvP.getConfigManager().getBlocksMenu();
        PlayerData playerData = KitPvP.getPlayerManager().getPlayerData(player);
        if(playerData != null) {
            if (event.getSlot() == 10) {
                double price = storeConfig.getDouble("Items.Stone.Price");
                if (playerData.getCoins() < price){
                    player.sendMessage(KitPvP.Color(messages.getString("InsufficientMoney")));
                    return;
                }

                playerData.setCoins(playerData.getCoins() - price);

                player.getInventory().addItem(new ItemStack(Material.STONE, storeConfig.getInt("Items.Stone.Amount")));
                return;
            }
            if (event.getSlot() == 13) {
                double price = storeConfig.getDouble("Items.Glass.Price");
                if (playerData.getCoins() < price){
                    player.sendMessage(KitPvP.Color(messages.getString("InsufficientMoney")));
                    return;
                }

                playerData.setCoins(playerData.getCoins() - price);

                player.getInventory().addItem(new ItemStack(Material.STAINED_GLASS, storeConfig.getInt("Items.Glass.Amount"), (short)storeConfig.getInt("Items.Glass.Data")));
                return;
            }
            if (event.getSlot() == 16) {
                double price = storeConfig.getDouble("Items.Wool.Price");
                if (playerData.getCoins() < price){
                    player.sendMessage(KitPvP.Color(messages.getString("InsufficientMoney")));
                    return;
                }

                playerData.setCoins(playerData.getCoins() - price);

                player.getInventory().addItem(new ItemStack(Material.WOOL, storeConfig.getInt("Items.Wool.Amount"), (short)storeConfig.getInt("Items.Wool.Data")));
            }
        }
    }

    public void updateMenu(){
        FileConfiguration blocksMenu = KitPvP.getConfigManager().getBlocksMenu();

        this.setItem(10, ItemsBuild.crearItemEnch(Material.getMaterial(blocksMenu.getString("Items.Stone.ID")), 1, blocksMenu.getInt("Items.Stone.Data"), blocksMenu.getString("Items.Stone.Name"), replacer(blocksMenu.getStringList("Items.Stone.Description"),"Stone")));
        this.setItem(13, ItemsBuild.crearItemEnch(Material.getMaterial(blocksMenu.getString("Items.Glass.ID")), 1, blocksMenu.getInt("Items.Glass.Data"), blocksMenu.getString("Items.Glass.Name"), replacer(blocksMenu.getStringList("Items.Glass.Description"),"Glass")));
        this.setItem(16, ItemsBuild.crearItemEnch(Material.getMaterial(blocksMenu.getString("Items.Wool.ID")), 1, blocksMenu.getInt("Items.Wool.Data"), blocksMenu.getString("Items.Wool.Name"), replacer(blocksMenu.getStringList("Items.Wool.Description"),"Wool")));

        if(blocksMenu.isSet("Background") && blocksMenu.getBoolean("Background.Enable")) {
            for (int i = 0; i < this.getInventory().getSize(); i++) {
                ItemStack itemStack = this.getInventory().getItem(i);
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    this.setItem(i, ItemsBuild.crearItem(Material.getMaterial(blocksMenu.getString("Background.ID")), 1, blocksMenu.getInt("Background.Data"), "&f"));
                }
            }
        }
    }

    public List<String> replacer(List<String> lores, String section) {
        FileConfiguration storeConfig = KitPvP.getConfigManager().getBlocksMenu();

        List<String> newLore = Lists.newArrayList();
        for(String s : lores) {
            newLore.add(KitPvP.Color(s.replace("%price%", String.valueOf(storeConfig.getDouble("Items."+section+".Price")))));
        }

        return newLore;
    }
}
