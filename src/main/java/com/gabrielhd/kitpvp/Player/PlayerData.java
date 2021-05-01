package com.gabrielhd.kitpvp.Player;

import com.gabrielhd.kitpvp.Database.Database;
import com.gabrielhd.kitpvp.Kits.Kit;
import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.Quests.Quest;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PlayerData {

    @Getter private final Player player;
    @Getter private final UUID uuid;
    @Getter @Setter private Quest quest;

    @Getter @Setter private int kills;
    @Getter @Setter private int deaths;
    @Getter @Setter private int killstreak;
    @Getter @Setter private int level;
    @Getter @Setter private long exp;

    @Getter @Setter private double coins;
    @Getter @Setter private double killtodeathratio;

    @Getter private final List<Kit> kits = Lists.newArrayList();

    public PlayerData(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();

        Bukkit.getScheduler().runTaskAsynchronously(KitPvP.getInstance(), () -> Database.getStorage().loadPlayer(this));

        this.updateKDR();
    }

    public void updateKDR() {
        this.killtodeathratio = (double)this.kills/(double)this.deaths;
    }
}
