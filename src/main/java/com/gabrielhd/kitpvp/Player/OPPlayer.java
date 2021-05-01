package com.gabrielhd.kitpvp.Player;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class OPPlayer {

    private final UUID uuid;
    private final Player player;
    private Location first;
    private Location second;

    public OPPlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.player = player;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getFirst() {
        return first;
    }

    public void setFirst(Location first) {
        this.first = first;
    }

    public Location getSecond() {
        return second;
    }

    public void setSecond(Location second) {
        this.second = second;
    }
}
