package com.gabrielhd.kitpvp.Commands;

import com.gabrielhd.kitpvp.Kits.Kit;
import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.NPCs.CustomNPC;
import com.gabrielhd.kitpvp.NPCs.NPCType;
import com.gabrielhd.kitpvp.Player.OPPlayer;
import com.gabrielhd.kitpvp.Utils.LocationUtils;
import com.jitse.npclib.api.NPC;
import com.jitse.npclib.api.skin.SkinFetcher;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class KitCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            FileConfiguration messages = KitPvP.getConfigManager().getMessages();
            if(!player.hasPermission("kitpvp.admin")) {
                player.sendMessage(KitPvP.Color(messages.getString("NoPermissions")));
                return true;
            }
            if(args.length <= 0 || args.length >= 5) {
                this.sendHelp(player);
                return true;
            }

            if(args.length == 4) {
                if(args[0].equalsIgnoreCase("kit")) {
                    if(args[1].equalsIgnoreCase("setDisplayName")) {
                        String name = args[2];
                        String displayName = args[3];
                        Kit kit = KitPvP.getKitsManager().getKit(name);
                        if(kit != null) {
                            kit.setDisplayName(displayName);
                            player.sendMessage(KitPvP.Color("&aKit DisplayName set correctly"));

                            KitPvP.getKitsManager().saveKits();
                            return true;
                        } else {
                            player.sendMessage(KitPvP.Color("&cThis kit does not exist"));
                        }
                        return true;
                    }

                    this.sendHelp(player);
                    return true;
                }
                if(args[0].equalsIgnoreCase("npc")) {
                    if (args[1].equalsIgnoreCase("setskin")) {
                        CustomNPC customnpc = KitPvP.getNpcManager().getNPC(args[2]);
                        if (customnpc != null && customnpc.getNpcType() != NPCType.USER) {
                            NPC npc = customnpc.getNPC();
                            SkinFetcher.fetchSkinFromNameAsync(args[3], npc::setSkin);
                            SkinFetcher.fetchSkinFromNameAsync(args[3], npc::updateSkin);
                            player.sendMessage(KitPvP.Color("&aNPC skin update successfully"));
                        }
                        return true;
                    }
                    this.sendHelp(player);
                    return true;
                }
                this.sendHelp(player);
                return true;
            }

            if(args.length == 3) {
                Player target = Bukkit.getPlayer(args[0]);
                if(target != null) {
                    if(args[1].equalsIgnoreCase("setkit")) {
                        String name = args[2];
                        Kit kit = KitPvP.getKitsManager().getKit(name);
                        if(kit != null) {
                            target.getInventory().clear();

                            target.getInventory().setContents(kit.getContents());
                            target.getInventory().setArmorContents(kit.getArmor());
                            target.updateInventory();
                            return true;
                        } else {
                            player.sendMessage(KitPvP.Color("&cThis kit does not exist"));
                        }
                        return true;
                    }
                    this.sendHelp(player);
                    return true;
                }

                if(args[0].equalsIgnoreCase("npc")) {
                    if(args[1].equalsIgnoreCase("spawn")) {
                        NPCType npcType = NPCType.getByName(args[2]);
                        if (npcType != null) {
                            if (KitPvP.getNpcManager().getNPC(npcType.name().toLowerCase()) != null) {
                                KitPvP.getNpcManager().removeNPC(KitPvP.getNpcManager().getNPC(npcType.name().toLowerCase()));
                            }
                            KitPvP.getNpcManager().createNPC(npcType.name().toLowerCase(), player.getLocation(), npcType, player);
                            player.sendMessage(KitPvP.Color("&aNPC successfully established"));
                            return true;
                        } else {
                            player.sendMessage(KitPvP.Color("&cThis type of NPC does not exist."));
                            player.sendMessage(KitPvP.Color("&cNPC Types: Kit/User/Shop/Quests/Upgrades"));
                        }
                        return true;
                    }
                    this.sendHelp(player);
                    return true;
                }
                if(args[0].equalsIgnoreCase("kit")) {
                    if(args[1].equalsIgnoreCase("create")) {
                        String name = args[2];
                        if(KitPvP.getKitsManager().getKit(name) == null) {
                            KitPvP.getKitsManager().createKit(name, player);
                            player.sendMessage(KitPvP.Color("&aKit created successfully"));
                            return true;
                        } else {
                            player.sendMessage(KitPvP.Color("&cThis kit already exists"));
                        }
                        return true;
                    }
                    if(args[1].equalsIgnoreCase("delete")) {
                        String name = args[2];
                        if(KitPvP.getKitsManager().getKit(name) != null) {
                            KitPvP.getKitsManager().deleteKit(name);
                            player.sendMessage(KitPvP.Color("&cKit deleted successfully"));

                            KitPvP.getKitsManager().saveKits();
                            return true;
                        } else {
                            player.sendMessage(KitPvP.Color("&cThis kit does not exist"));
                        }
                        return true;
                    }
                    if(args[1].equalsIgnoreCase("setinv")) {
                        String name = args[2];
                        Kit kit = KitPvP.getKitsManager().getKit(name);
                        if(kit != null) {
                            kit.setContents(player.getInventory().getContents());
                            kit.setArmor(player.getInventory().getArmorContents());
                            player.sendMessage(KitPvP.Color("&aKit inventory successfully updated"));

                            KitPvP.getKitsManager().saveKits();
                            return true;
                        } else {
                            player.sendMessage(KitPvP.Color("&cThis kit does not exist"));
                        }
                        return true;
                    }
                    if(args[1].equalsIgnoreCase("seticon")) {
                        String name = args[2];
                        Kit kit = KitPvP.getKitsManager().getKit(name);
                        if(kit != null) {
                            Material icon = Material.getMaterial(args[3]);
                            if(icon != null) {
                                kit.setIcon(icon);
                                player.sendMessage(KitPvP.Color("&aKit icon set correctly"));

                                KitPvP.getKitsManager().saveKits();
                                return true;
                            }
                            return true;
                        } else {
                            player.sendMessage(KitPvP.Color("&cThis kit does not exist"));
                        }
                        return true;
                    }
                    if(args[1].equalsIgnoreCase("setprice")) {
                        String name = args[2];
                        Kit kit = KitPvP.getKitsManager().getKit(name);
                        if(kit != null) {
                            if(this.isInt(args[3])) {
                                kit.setCost(Double.parseDouble(args[3]));
                                player.sendMessage(KitPvP.Color("&aKit price set correctly"));

                                KitPvP.getKitsManager().saveKits();
                                return true;
                            } else {
                                player.sendMessage(KitPvP.Color("&cThis value is not numeric"));
                            }
                            return true;
                        } else {
                            player.sendMessage(KitPvP.Color("&cThis kit does not exist"));
                        }
                        return true;
                    }

                    this.sendHelp(player);
                    return true;
                }

                this.sendHelp(player);
                return true;
            }

            if(args.length == 2) {
                if(args[0].equalsIgnoreCase("kit")) {
                    if(args[1].equalsIgnoreCase("seticon")) {
                        String name = args[2];
                        Kit kit = KitPvP.getKitsManager().getKit(name);
                        if(kit != null) {
                            kit.setIcon(player.getItemInHand().getType());

                            KitPvP.getKitsManager().saveKits();
                            return true;
                        } else {
                            player.sendMessage(KitPvP.Color("&cThis kit does not exist"));
                        }
                        return true;
                    }

                    this.sendHelp(player);
                    return true;
                }
                if(args[0].equalsIgnoreCase("set")) {
                    if (args[1].equalsIgnoreCase("lobby")) {
                        KitPvP.setMainLobby(player.getLocation());
                        KitPvP.getConfigManager().getRegions().set("LobbyLocation", LocationUtils.LocationToString(player.getLocation()));
                        KitPvP.getConfigManager().getRegions().save();

                        player.sendMessage(KitPvP.Color("&aLobby location set correctly."));
                        return true;
                    }

                    if(args[1].equalsIgnoreCase("spawnregion")) {
                        OPPlayer opPlayer = KitPvP.getPlayerManager().getOPPlayer(player);
                        if(opPlayer != null) {
                            if(opPlayer.getFirst() != null) {
                                if(opPlayer.getSecond() != null) {
                                    KitPvP.getRegionManager().setSpawnRegion(opPlayer);

                                    KitPvP.getRegionManager().saveRegions();
                                    player.sendMessage(KitPvP.Color("&aRegion set correctly."));
                                    return true;
                                } else {
                                    player.sendMessage(KitPvP.Color("&cYou have not established the second point."));
                                }
                                return true;
                            } else {
                                player.sendMessage(KitPvP.Color("&cYou have not established the first point."));
                            }
                            return true;
                        }
                        return true;
                    }

                    this.sendHelp(player);
                    return true;
                }
                if(args[0].equalsIgnoreCase("npc")) {
                    if(args[1].equalsIgnoreCase("spawn")) {
                        player.sendMessage(KitPvP.Color("&cNPC Types: Kit/User/Shop/Quests/Upgrades"));
                        return true;
                    }
                    this.sendHelp(player);
                    return true;
                }

                this.sendHelp(player);
                return true;
            }

            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("reload")) {
                    KitPvP.getInstance().reload();
                    player.sendMessage(KitPvP.Color("&cReload complete"));
                    return true;
                }
                return true;
            }
            return true;
        } else {
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("reload")) {
                    KitPvP.getInstance().reload();
                    sender.sendMessage(KitPvP.Color("&cReload complete"));
                    return true;
                }
            } else if(args.length == 3) {
                Player target = Bukkit.getPlayer(args[0]);
                if(target != null) {
                    if(args[1].equalsIgnoreCase("setkit")) {
                        String name = args[2];
                        Kit kit = KitPvP.getKitsManager().getKit(name);
                        if(kit != null) {
                            target.getInventory().clear();

                            target.getInventory().setContents(kit.getContents());
                            target.getInventory().setArmorContents(kit.getArmor());
                            target.updateInventory();
                            return true;
                        } else {
                            sender.sendMessage(KitPvP.Color("&cThis kit does not exist"));
                        }
                        return true;
                    }
                    this.sendHelp(sender);
                    return true;
                }
                return true;
            }
        }
        return false;
    }

    public void sendHelp(CommandSender player) {
        FileConfiguration messages = KitPvP.getConfigManager().getMessages();
        for(String lines : messages.getStringList("KitpvpHelp")) {
            player.sendMessage(KitPvP.Color(lines));
        }
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
