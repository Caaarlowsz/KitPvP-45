package com.gabrielhd.kitpvp.Menu.Submenus;

import com.gabrielhd.guns.Utils.NBTItem;
import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.Menu.Menu;
import com.gabrielhd.kitpvp.Player.PlayerData;
import com.gabrielhd.kitpvp.Quests.Quest;
import com.gabrielhd.kitpvp.Quests.QuestType;
import com.gabrielhd.kitpvp.Utils.ItemsBuild;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class ConfMenu extends Menu {

    private final ItemStack itemStack;

    public ConfMenu(ItemStack itemStack) {
        super("&7Confirm", 3);
        this.itemStack = itemStack;
        this.update();
    }

    @Override
    public void onClose(Player player, InventoryCloseEvent event) {}

    @Override
    public void onOpen(Player player, InventoryOpenEvent event) {}

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        FileConfiguration config = KitPvP.getConfigManager().getQuests();
        if(event.getSlot() == 11) {
            PlayerData playerData = KitPvP.getPlayerManager().getPlayerData(player);
            if(playerData != null && this.itemStack != null) {
                NBTItem nbtItem = new NBTItem(this.itemStack);

                QuestType type = QuestType.getQuest(nbtItem.getString("QuestType"));
                String key = nbtItem.getString("QuestID");
                Quest quest = new Quest(type, key, config.getInt("Quests.Daily."+type+"."+key+".Amount"), config.getInt("Quests.Daily."+type+"."+key+".Time"));
                playerData.setQuest(quest);

                player.sendMessage(KitPvP.Color(KitPvP.getConfigManager().getMessages().getString("SelectedQuest")));

                new QuestsMenu(player).openInventory(player);
            }
            return;
        }
        if(event.getSlot() == 15) {
            player.closeInventory();
        }
    }

    public void update() {
        this.setItem(11, ItemsBuild.crearItemEnch(Material.WOOL, 1, 5, "&aAccept", KitPvP.getConfigManager().getMessages().getStringList("ConfirmLore")));
        this.setItem(15, ItemsBuild.crearItem(Material.WOOL, 1, 14, "&cDeny"));
    }
}
