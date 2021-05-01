package com.gabrielhd.kitpvp.Managers;

import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.Utils.YamlConfig;
import lombok.Getter;

public class ConfigManager {

    @Getter private final YamlConfig Settings;
    @Getter private final YamlConfig Messages;
    @Getter private final YamlConfig ReforgeMenu;
    @Getter private final YamlConfig Regions;
    @Getter private final YamlConfig Quests;
    @Getter private final YamlConfig Kits;

    @Getter private final YamlConfig userMenu;
    @Getter private final YamlConfig gunsMenu;
    @Getter private final YamlConfig shopMenu;
    @Getter private final YamlConfig ammoMenu;
    @Getter private final YamlConfig armorMenu;
    @Getter private final YamlConfig blocksMenu;
    @Getter private final YamlConfig othersMenu;

    public ConfigManager(KitPvP plugin) {
        Settings = new YamlConfig("Settings");
        Messages = new YamlConfig("Messages");
        Regions = new YamlConfig("Settings/Regions");
        Quests = new YamlConfig("Settings/Quests");
        Kits = new YamlConfig("Settings/Kits");

        gunsMenu = new YamlConfig("Menus/GunsMenu");
        shopMenu = new YamlConfig("Menus/ShopMenu");
        ammoMenu = new YamlConfig("Menus/AmmoMenu");
        armorMenu = new YamlConfig("Menus/ArmorMenu");
        blocksMenu = new YamlConfig("Menus/BlocksMenu");
        othersMenu = new YamlConfig("Menus/OthersMenu");
        userMenu = new YamlConfig("Menus/UserMenu");
        ReforgeMenu = new YamlConfig("Menus/ReforgeMenu");
    }
}
