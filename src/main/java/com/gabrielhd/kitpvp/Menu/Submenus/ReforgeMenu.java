package com.gabrielhd.kitpvp.Menu.Submenus;

import com.gabrielhd.guns.Enums.Rarity;
import com.gabrielhd.guns.Manager.ConfigManager;
import com.gabrielhd.guns.Utils.NBTItem;
import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.Menu.Menu;
import com.gabrielhd.kitpvp.Player.PlayerData;
import com.gabrielhd.kitpvp.Utils.ItemsBuild;
import com.gabrielhd.kitpvp.Utils.YamlConfig;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ReforgeMenu extends Menu {

    public ReforgeMenu(ItemStack item ) {
        super(KitPvP.getConfigManager().getReforgeMenu().getString("Title"), 3);
        this.updateMenu(item);
    }

    @Override
    public void onClose(Player player, InventoryCloseEvent event) {}

    @Override
    public void onOpen(Player player, InventoryOpenEvent event) {}

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        YamlConfig messages = KitPvP.getConfigManager().getMessages();
        YamlConfig config = KitPvP.getConfigManager().getReforgeMenu();
        NBTItem nbtItem = new NBTItem(player.getItemInHand());
        Rarity rarity = Rarity.getRarity(nbtItem.getString("Rarity"));
        PlayerData playerData = KitPvP.getPlayerManager().getPlayerData(player);

        if(playerData != null) {
            for (Rarity rarities : Rarity.values()) {
                if (rarities.name().equalsIgnoreCase("common")) {
                    continue;
                }
                if(event.getCurrentItem().isSimilar(ItemsBuild.crearItemEnch(Material.getMaterial(config.getString("Rarity." + rarities.name() + ".ID")), 1, config.getInt("Rarity." + rarities.name() + ".Data"), config.getString("Rarity." + rarities.name() + ".Name"), replacer(config.getStringList("Rarity." + rarities.name() + ".Description"), rarities.name())))) {
                    if (rarities.getId() <= rarity.getId()) {
                        player.sendMessage(KitPvP.Color(messages.getString("CantLevelDown")));
                        player.closeInventory();
                        return;
                    }
                    double price = config.getDouble("Rarity." + rarities.name() + ".Price");
                    if (playerData.getCoins() < price) {
                        player.sendMessage(KitPvP.Color(messages.getString("InsufficientMoney")));
                        player.closeInventory();
                        return;
                    }

                    playerData.setCoins(playerData.getCoins() - price);

                    nbtItem.setString("Rarity", rarities.name());
                    ItemStack new_item = nbtItem.getItem();
                    ItemMeta new_itemMeta = new_item.getItemMeta();

                    List<String> new_metalore = Lists.newArrayList();
                    for(String s : new_itemMeta.getLore()) {
                        s = s.replace(KitPvP.Color(ConfigManager.getSettings().getString("Messages.Rarities."+rarity.name())), KitPvP.Color(ConfigManager.getSettings().getString("Messages.Rarities."+rarities.name())));

                        new_metalore.add(KitPvP.Color(s));
                    }
                    new_itemMeta.setLore(new_metalore);
                    new_item.setItemMeta(new_itemMeta);

                    player.setItemInHand(new_item);
                    player.sendMessage(KitPvP.Color(messages.getString("EnchaintedItem")));

                    this.updateMenu(new_item);
                    return;
                }
            }
        }
    }


    public void updateMenu(ItemStack item ) {
        YamlConfig config = KitPvP.getConfigManager().getReforgeMenu();
        NBTItem nbtItem = new NBTItem(item);

        if(nbtItem.hasKey("Rarity")) {
            if(nbtItem.getString("Rarity").equalsIgnoreCase("Exotic")){
                this.maxLevel(item);
                return;
            }
            this.setItem(10, item);

            int slot = 12;
            for (Rarity rarity : Rarity.values()) {
                if (rarity.name().equalsIgnoreCase("common")) {
                    continue;
                }

                this.setItem(slot++, ItemsBuild.crearItemEnch(Material.getMaterial(config.getString("Rarity." + rarity.name() + ".ID")), 1, config.getInt("Rarity." + rarity.name() + ".Data"), config.getString("Rarity." + rarity.name() + ".Name"), replacer(config.getStringList("Rarity." + rarity.name() + ".Description"), rarity.name())));
            }
        }
    }

    public void maxLevel(ItemStack item) {
        this.getInventory().clear();

        YamlConfig config = KitPvP.getConfigManager().getReforgeMenu();

        this.setItem(13, item);

        for(int i = 0; i < this.getInventory().getSize(); i++){
            ItemStack itemStack = this.getInventory().getItem(i);
            if(itemStack == null || itemStack.getType() == Material.AIR) {
                this.setItem(i, ItemsBuild.crearItem(Material.STAINED_GLASS_PANE,1,5, "&f"));
            }
        }
    }

    public List<String> replacer(List<String> lores, String section) {
        FileConfiguration storeConfig = KitPvP.getConfigManager().getReforgeMenu();

        List<String> newLore = Lists.newArrayList();
        for (String s : lores) {
            newLore.add(KitPvP.Color(s.replace("%price%", String.valueOf(storeConfig.getDouble("Rarity." + section + ".Price")))));
        }

        return newLore;
    }
}
