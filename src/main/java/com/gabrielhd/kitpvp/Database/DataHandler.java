package com.gabrielhd.kitpvp.Database;

import com.gabrielhd.kitpvp.Player.PlayerData;
import org.bukkit.entity.Player;

public interface DataHandler {

    void loadPlayer(PlayerData playerData);

    void uploadPlayer(PlayerData playerData);

    boolean existsPlayer(Player player);

    void close();
}
