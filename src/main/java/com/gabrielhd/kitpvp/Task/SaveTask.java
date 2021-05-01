package com.gabrielhd.kitpvp.Task;

import com.gabrielhd.kitpvp.Database.Database;
import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.Player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class SaveTask implements Runnable {

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            PlayerData playerData = KitPvP.getPlayerManager().getPlayerData(player);
            if(playerData != null) {
                Database.getStorage().uploadPlayer(playerData);
            }
        }
    }
}
