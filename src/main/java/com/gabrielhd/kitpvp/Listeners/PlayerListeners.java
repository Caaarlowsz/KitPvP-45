package com.gabrielhd.kitpvp.Listeners;

import com.gabrielhd.guns.Utils.NBTItem;
import com.gabrielhd.kitpvp.Kits.Kit;
import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.Menu.Submenus.ReforgeMenu;
import com.gabrielhd.kitpvp.Player.OPPlayer;
import com.gabrielhd.kitpvp.Player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class PlayerListeners implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(KitPvP.getMainLobby() == null ){
            return;
        }
        if (player.getWorld().equals(KitPvP.getMainLobby().getWorld())) {
            if (player.getLocation().clone().getBlock().getType() == Material.SLIME_BLOCK) {
                player.setVelocity(player.getLocation().getDirection().multiply(5).setY(7.0));
                player.getWorld().playEffect(player.getLocation(), Effect.FIREWORKS_SPARK, 20);
            }
            else if (player.getLocation().getBlock().getType() == Material.SLIME_BLOCK) {
                player.setVelocity(player.getLocation().getDirection().multiply(5).setY(7.0));
                player.getWorld().playEffect(player.getLocation(), Effect.FIREWORKS_SPARK, 20);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSelectRegion(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block != null && player.getInventory().getItemInHand().getType() == Material.BLAZE_ROD && player.hasPermission("injectpvp.admin")) {
                OPPlayer opPlayer = KitPvP.getPlayerManager().getOPPlayer(player);
                if(opPlayer != null) {
                    if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        opPlayer.setSecond(block.getLocation());
                        player.sendMessage(KitPvP.Color("&a&lSecond point selected correctly"));
                        event.setCancelled(true);
                        return;
                    }
                    if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        opPlayer.setFirst(block.getLocation());
                        player.sendMessage(KitPvP.Color("&a&lFirst point selected correctly"));
                        event.setCancelled(true);
                    }
                }
            }

            ItemStack item = player.getItemInHand();
            if(block != null && block.getType() == Material.ENCHANTMENT_TABLE) {
                if(item != null) {
                    NBTItem nbtitem = new NBTItem(item);
                    if(nbtitem.hasKey("CustomArmor") || nbtitem.hasKey("Weapon")){
                        new ReforgeMenu(item).openInventory(player);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerData playerData = KitPvP.getPlayerManager().getPlayerData(player);
        FileConfiguration settings = KitPvP.getConfigManager().getSettings();
        if(playerData != null) {
            if(player.getKiller() != null) {
                PlayerData killerData = KitPvP.getPlayerManager().getPlayerData(player.getKiller());
                if(killerData != null) {
                    killerData.setKills(killerData.getKills()+1);
                    killerData.setKillstreak(killerData.getKillstreak()+1);
                    killerData.setExp(killerData.getExp()+this.getExp());
                    killerData.setCoins(killerData.getCoins()+settings.getInt("CoinsPerKill"));
                    killerData.updateKDR();

                    if(settings.isSet("Levels") && settings.getBoolean("Levels.Enable")) {
                        if(killerData.getExp() >= settings.getInt("Levels."+(killerData.getLevel()+1)+".ExpRequired")) {
                            killerData.setLevel(killerData.getLevel()+1);

                            for(String cmd : settings.getStringList("Levels."+killerData.getLevel()+".Rewards")) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
                            }
                        }
                    }

                    if(settings.isSet("KillStreaks."+killerData.getKillstreak())) {
                        for(String cmd : settings.getStringList("KillStreaks."+killerData.getKillstreak())) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
                        }
                    }
                }
            }
            event.getDrops().clear();

            playerData.setCoins(playerData.getCoins()+settings.getInt("CoinsPerDeath"));
            playerData.setDeaths(playerData.getDeaths()+1);
            playerData.setKillstreak(0);
            playerData.updateKDR();

            Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> player.spigot().respawn(), 5L);
            Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
                if(KitPvP.getMainLobby() != null) player.teleport(KitPvP.getMainLobby());

                player.getInventory().clear();
                for(Kit kit : KitPvP.getKitsManager().getKits().values()) {
                    player.getInventory().setContents(kit.getContents());
                    player.getInventory().setArmorContents(kit.getArmor());
                    player.updateInventory();
                    return;
                }
            }, 20L);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if((event.getEntity() instanceof Player)) {
            Player player = (Player) event.getEntity();
            if(KitPvP.getRegionManager().getSpawnRegion() != null) {
                if (KitPvP.getRegionManager().getSpawnRegion().inCuboid(player) || event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                }
            }
        }
    }

    public int getExp() {
        int exp = new Random().nextInt(KitPvP.getConfigManager().getSettings().getInt("ExpPerKillMax"));
        if(exp < KitPvP.getConfigManager().getSettings().getInt("ExpPerKillMin")) {
            exp = KitPvP.getConfigManager().getSettings().getInt("ExpPerKillMin");
        }
        return exp;
    }
}
