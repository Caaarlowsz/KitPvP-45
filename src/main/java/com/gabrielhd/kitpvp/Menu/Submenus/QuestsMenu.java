package com.gabrielhd.kitpvp.Menu.Submenus;

import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.Menu.Menu;
import com.gabrielhd.kitpvp.Player.PlayerData;
import com.gabrielhd.kitpvp.Quests.Quest;
import com.gabrielhd.kitpvp.Utils.ItemsBuild;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.List;

public class QuestsMenu extends Menu {

    private static final int[] GLASS_SLOTS = new int[] {
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            9,                      17,
            18,                     26,
            27,28,29,30,31,32,33,34,35
    };

    public QuestsMenu(Player player) {
        super(KitPvP.getConfigManager().getQuests().getString("Menu.Title"), 4);

        this.update(player);
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

    public void update(Player player) {
        FileConfiguration config = KitPvP.getConfigManager().getQuests();
        for(int i : GLASS_SLOTS) {
            this.setItem(i, ItemsBuild.crearItem(Material.getMaterial(config.getInt("Menu.Background.ID")), 1, config.getInt("Menu.Background.Data"), "&f"));
        }

        PlayerData playerData = KitPvP.getPlayerManager().getPlayerData(player);
        if(playerData != null) {
            Quest selected = playerData.getQuest();
            int slot = 10;
            for(Quest quest : KitPvP.getQuestsManager().getDailyQuests().values()) {
                List<String> lore = config.getStringList("Quests.Daily."+quest.getQuestType().name()+"."+quest.getMissionID()+".Description");

                if (selected != null && selected.getQuestType() == quest.getQuestType() && selected.getMissionID().equalsIgnoreCase(quest.getMissionID())) {
                    lore.add("&f");
                    lore.add("&eSELECTED");
                    this.setItem(slot++, ItemsBuild.crearItemEnch(Material.getMaterial(config.getString("Quests.Daily."+quest.getQuestType().name()+".ID")), 1, 8, config.getString("Quests.Daily."+quest.getQuestType().name()+".Name"), lore));
                } else {
                    this.setItem(slot++, ItemsBuild.crearItemEnch(Material.getMaterial(config.getString("Quests.Daily."+quest.getQuestType().name()+".ID")), 1, 0, config.getString("Quests.Daily."+quest.getQuestType().name()+".Name"), lore));
                }
            }
        }
    }
}
