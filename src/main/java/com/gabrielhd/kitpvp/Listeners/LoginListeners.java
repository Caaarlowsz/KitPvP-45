package com.gabrielhd.kitpvp.Listeners;

import com.gabrielhd.kitpvp.Database.Database;
import com.gabrielhd.kitpvp.Kits.Kit;
import com.gabrielhd.kitpvp.KitPvP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public class LoginListeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        KitPvP.getPlayerManager().createPlayer(player);
        KitPvP.getPlayerManager().createOPPlayer(player);

        AtomicBoolean lastPlayed = new AtomicBoolean(true);

        Bukkit.getScheduler().runTaskAsynchronously(KitPvP.getInstance(), () -> lastPlayed.set(Database.getStorage().existsPlayer(player)));

        if(!lastPlayed.get()) {
            for(Kit kit : KitPvP.getKitsManager().getKits().values()) {
                player.getInventory().clear();

                player.getInventory().setContents(kit.getContents());
                player.getInventory().setArmorContents(kit.getArmor());
                player.updateInventory();
                break;
            }
        }

        if(KitPvP.getMainLobby() != null) player.teleport(KitPvP.getMainLobby());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        KitPvP.getPlayerManager().deleteOPPlayer(player);
        KitPvP.getPlayerManager().deletePlayer(player);
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        Player player = event.getPlayer();

        KitPvP.getPlayerManager().deleteOPPlayer(player);
        KitPvP.getPlayerManager().deletePlayer(player);
    }
}
