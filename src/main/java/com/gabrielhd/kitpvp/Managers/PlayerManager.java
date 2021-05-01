package com.gabrielhd.kitpvp.Managers;

import com.gabrielhd.kitpvp.Database.Database;
import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.Player.OPPlayer;
import com.gabrielhd.kitpvp.Player.PlayerData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    @Getter private final Map<UUID, OPPlayer> opPlayers = new HashMap<>();
    @Getter private final Map<UUID, PlayerData> players = new HashMap<>();

    public void createOPPlayer(Player player) {
        OPPlayer opPlayer = new OPPlayer(player);
        this.opPlayers.put(player.getUniqueId(), opPlayer);
    }

    public void createPlayer(Player player) {
        PlayerData playerData = new PlayerData(player);
        this.players.put(player.getUniqueId(), playerData);
    }

    public OPPlayer getOPPlayer(Player player) {
        return this.opPlayers.get(player.getUniqueId());
    }

    public PlayerData getPlayerData(Player player) {
        return this.players.get(player.getUniqueId());
    }

    public void deleteOPPlayer(Player player) {
        this.opPlayers.remove(player.getUniqueId());
    }

    public void deletePlayer(Player player) {
        PlayerData playerData = this.getPlayerData(player);
        if(playerData != null) {
            Bukkit.getScheduler().runTaskAsynchronously(KitPvP.getInstance(), () -> Database.getStorage().uploadPlayer(playerData));

            this.players.remove(player.getUniqueId());
        }
    }
}
