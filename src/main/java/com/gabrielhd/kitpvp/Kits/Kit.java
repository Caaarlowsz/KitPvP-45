package com.gabrielhd.kitpvp.Kits;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Kit {

    @Getter private final String name;
    @Getter @Setter private int slot;
    @Getter @Setter private double cost;
    @Getter @Setter private Material icon;
    @Getter @Setter private ItemStack[] armor;
    @Getter @Setter private ItemStack[] contents;
    @Getter @Setter private String displayName;

    public Kit(String name) {
        this.name = name;
        this.slot = -1;

        this.icon = Material.GRASS;
    }
}
