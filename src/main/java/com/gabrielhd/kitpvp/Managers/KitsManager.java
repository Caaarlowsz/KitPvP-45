package com.gabrielhd.kitpvp.Managers;

import com.gabrielhd.kitpvp.Kits.Kit;
import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.Player.PlayerData;
import com.gabrielhd.kitpvp.Utils.YamlConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class KitsManager {

    @Getter private final Map<String, Kit> kits = Maps.newHashMap();

    public KitsManager() {
        this.loadKits();
    }

    public void loadKits() {
        YamlConfig kits = KitPvP.getConfigManager().getKits();
        if(kits.isSet("Kits") && !kits.getConfigurationSection("Kits").getKeys(false).isEmpty()) {
            for (String section : kits.getConfigurationSection("Kits").getKeys(false)) {
                Kit kit = new Kit(kits.getString("Kits." + section + ".Name"));
                String displayName = kits.getString("Kits." + section + ".DisplayName");
                ItemStack[] contents = (ItemStack[]) ((List) kits.get("Kits." + section + ".Contents")).<ItemStack>toArray(new ItemStack[0]);
                ItemStack[] armor = (ItemStack[]) ((List) kits.get("Kits." + section + ".Armor")).<ItemStack>toArray(new ItemStack[0]);
                Material icon = Material.getMaterial(kits.getString("Kits." + section + ".Icon"));
                double coins = kits.getDouble("Kits." + section + ".Cost");
                int slot = kits.getInt("Kits."+section+".Slot");

                kit.setDisplayName(displayName);
                kit.setContents(contents);
                kit.setArmor(armor);
                kit.setIcon(icon);
                kit.setCost(coins);
                kit.setSlot(slot);

                this.kits.put(section.toLowerCase(), kit);
            }
        }
    }

    public void deleteKit(String name) {
        this.kits.remove(name.toLowerCase());

        for(Player player : Bukkit.getOnlinePlayers()) {
            PlayerData playerData = KitPvP.getPlayerManager().getPlayerData(player);
            if(playerData != null) {
                playerData.getKits().removeIf(kit -> kit.getName().equalsIgnoreCase(name));
            }
        }

        YamlConfig kits = KitPvP.getConfigManager().getKits();
        kits.set("Kits", null);
        kits.save();

        this.saveKits();
    }

    public void createKit(String name, Player player) {
        Kit kit = new Kit(name);
        kit.setArmor(player.getInventory().getArmorContents());
        kit.setContents(player.getInventory().getContents());

        this.kits.put(name.toLowerCase(), kit);
    }

    public Kit getKit(String name) {
        return this.kits.get(name.toLowerCase());
    }

    public void saveKits() {
        YamlConfig kits = KitPvP.getConfigManager().getKits();
        for(Kit kit : this.kits.values()) {
            kits.set("Kits."+kit.getName().toLowerCase()+".Name", kit.getName());
            kits.set("Kits."+kit.getName().toLowerCase()+".Cost", kit.getCost());
            kits.set("Kits."+kit.getName().toLowerCase()+".Slot", -1);
            if(kit.getDisplayName() != null) kits.set("Kits."+kit.getName().toLowerCase()+".DisplayName", kit.getDisplayName());
            if(kit.getContents() != null) kits.set("Kits."+kit.getName().toLowerCase()+".Contents", kit.getContents());
            if(kit.getArmor() != null) kits.set("Kits."+kit.getName().toLowerCase()+".Armor", kit.getArmor());
            if(kit.getIcon() != null) kits.set("Kits."+kit.getName().toLowerCase()+".Icon", kit.getIcon().name());

            if(!kits.isSet("Kits."+kit.getName().toLowerCase()+".BuyDescription")) {
                List<String> lore = Lists.newArrayList();
                lore.add("&7Click to buy");
                lore.add("&f");
                lore.add("&fPrice: %price%");

                kits.set("Kits."+kit.getName().toLowerCase()+".BuyDescription", lore);
            }

            if(!kits.isSet("Kits."+kit.getName().toLowerCase()+".AlreadyKit")) {
                List<String> lore = Lists.newArrayList();
                lore.add("&f");
                lore.add("&aYou already have this kit");
                lore.add("&f");

                kits.set("Kits."+kit.getName().toLowerCase()+".AlreadyKit", lore);
            }

            if(!kits.isSet("Kits."+kit.getName().toLowerCase()+".Description")) {
                List<String> lore = Lists.newArrayList();
                lore.add("&f");

                kits.set("Kits."+kit.getName().toLowerCase()+".Description", lore);
            }
        }

        kits.save();
    }
}
