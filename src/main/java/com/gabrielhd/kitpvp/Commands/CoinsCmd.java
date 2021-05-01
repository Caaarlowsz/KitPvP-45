package com.gabrielhd.kitpvp.Commands;

import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.Player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CoinsCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if(args.length != 1 && args.length != 3) {
                player.sendMessage(KitPvP.Color("&aUse: /Coins balance"));
                if(player.hasPermission("kitpvp.admin")) {
                    player.sendMessage(KitPvP.Color("&aUse: /Coins give [Player] [Amount]"));
                    player.sendMessage(KitPvP.Color("&aUse: /Coins take [Player] [Amount]"));
                    player.sendMessage(KitPvP.Color("&aUse: /Coins reset [Player]"));
                }
                return true;
            }
            FileConfiguration messages = KitPvP.getConfigManager().getMessages();
            PlayerData playerData = KitPvP.getPlayerManager().getPlayerData(player);
            if(playerData != null) {
                if(args.length == 3) {
                    if(args[0].equalsIgnoreCase("give")) {
                        if(!player.hasPermission("kitpvp.admin") && !player.hasPermission("kitpvp.coins.give")) {
                            player.sendMessage(KitPvP.Color(messages.getString("NoPermissions")));
                            return true;
                        }
                        Player target = Bukkit.getPlayer(args[1]);
                        if(target != null) {
                            PlayerData targetData = KitPvP.getPlayerManager().getPlayerData(target);
                            if(targetData != null) {
                                if (this.isInt(args[2])) {
                                    targetData.setCoins(targetData.getCoins() + Integer.parseInt(args[2]));
                                    target.sendMessage(KitPvP.Color(messages.getString("CoinsReceived").replace("%amount%", args[2])));
                                    player.sendMessage(KitPvP.Color(messages.getString("GiveedCoins").replace("%amount%", args[2]).replace("%player%", target.getName())));
                                    return true;
                                } else {
                                    player.sendMessage(KitPvP.Color("&cYou must enter the amount of Coins"));
                                }
                                return true;
                            }
                            return true;
                        } else {
                            player.sendMessage(KitPvP.Color(messages.getString("OfflinePlayer")));
                        }
                        return true;
                    }
                    if(args[0].equalsIgnoreCase("take")) {
                        if(!player.hasPermission("kitpvp.admin") && !player.hasPermission("kitpvp.coins.take")) {
                            player.sendMessage(KitPvP.Color(messages.getString("NoPermissions")));
                            return true;
                        }
                        Player target = Bukkit.getPlayer(args[1]);
                        if(target != null) {
                            PlayerData targetData = KitPvP.getPlayerManager().getPlayerData(target);
                            if(targetData != null) {
                                if (this.isInt(args[2])) {
                                    int amount = Integer.parseInt(args[2]);
                                    if(amount > targetData.getCoins()) {
                                        targetData.setCoins(0);
                                    } else {
                                        targetData.setCoins(targetData.getCoins() - amount);
                                    }
                                    target.sendMessage(KitPvP.Color(messages.getString("CoinsRemove").replace("%amount%", String.valueOf(amount))));
                                    player.sendMessage(KitPvP.Color(messages.getString("TakeCoins").replace("%amount%", String.valueOf(amount)).replace("%player%", target.getName())));
                                    return true;
                                } else {
                                    player.sendMessage(KitPvP.Color("&cYou must enter the amount of Coins"));
                                }
                                return true;
                            }
                            return true;
                        } else {
                            player.sendMessage(KitPvP.Color(messages.getString("OfflinePlayer")));
                        }
                        return true;
                    }
                    player.sendMessage(KitPvP.Color("&aUse: /Coins balance"));
                    if(player.hasPermission("kitpvp.admin")) {
                        player.sendMessage(KitPvP.Color("&aUse: /Coins give [Player] [Amount]"));
                        player.sendMessage(KitPvP.Color("&aUse: /Coins take [Player] [Amount]"));
                        player.sendMessage(KitPvP.Color("&aUse: /Coins reset [Player]"));
                    }
                    return true;
                }
                if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("reset")) {
                        if(!player.hasPermission("kitpvp.admin") && !player.hasPermission("kitpvp.coins.reset")) {
                            player.sendMessage(KitPvP.Color(messages.getString("NoPermissions")));
                            return true;
                        }
                        Player target = Bukkit.getPlayer(args[1]);
                        if(target != null) {
                            PlayerData targetData = KitPvP.getPlayerManager().getPlayerData(target);
                            if(targetData != null) {
                                targetData.setCoins(0);
                                player.sendMessage(KitPvP.Color("&cYou reset "+target.getName()+" Coins"));
                                return true;
                            }
                            return true;
                        } else {
                            player.sendMessage(KitPvP.Color(messages.getString("OfflinePlayer")));
                        }
                        return true;
                    }
                    player.sendMessage(KitPvP.Color("&aUse: /Coins balance"));
                    if(player.hasPermission("kitpvp.admin")) {
                        player.sendMessage(KitPvP.Color("&aUse: /Coins give [Player] [Amount]"));
                        player.sendMessage(KitPvP.Color("&aUse: /Coins take [Player] [Amount]"));
                        player.sendMessage(KitPvP.Color("&aUse: /Coins reset [Player]"));
                    }
                    return true;
                }
                if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("balance")) {
                        if(!player.hasPermission("kitpvp.admin") && !player.hasPermission("kitpvp.coins.balance")) {
                            player.sendMessage(KitPvP.Color(messages.getString("NoPermissions")));
                            return true;
                        }
                        player.sendMessage(KitPvP.Color(messages.getString("CoinsBalance").replace("%amount%", String.valueOf(playerData.getCoins()))));
                        return true;
                    }
                    player.sendMessage(KitPvP.Color("&aUse: /Coins balance"));
                    if(player.hasPermission("kitpvp.admin")) {
                        player.sendMessage(KitPvP.Color("&aUse: /Coins give [Player] [Amount]"));
                        player.sendMessage(KitPvP.Color("&aUse: /Coins take [Player] [Amount]"));
                        player.sendMessage(KitPvP.Color("&aUse: /Coins reset [Player]"));
                    }
                    return true;
                }
                return true;
            }
            return true;
        }
        return false;
    }

    public boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}
