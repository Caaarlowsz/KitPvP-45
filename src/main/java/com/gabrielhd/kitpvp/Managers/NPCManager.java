package com.gabrielhd.kitpvp.Managers;

import com.gabrielhd.kitpvp.Kits.Kit;
import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.Menu.Submenus.QuestsMenu;
import com.gabrielhd.kitpvp.Menu.Submenus.ReforgeMenu;
import com.gabrielhd.kitpvp.Menu.Submenus.ShopMenu;
import com.gabrielhd.kitpvp.Menu.Submenus.UserMenu;
import com.gabrielhd.kitpvp.NPCs.CustomNPC;
import com.gabrielhd.kitpvp.NPCs.NPCType;
import com.gabrielhd.kitpvp.Utils.LocationUtils;
import com.gabrielhd.kitpvp.Utils.YamlConfig;
import com.google.common.collect.Maps;
import com.jitse.npclib.NPCLib;
import com.jitse.npclib.api.NPC;
import com.jitse.npclib.api.NPCInteractEvent;
import com.jitse.npclib.api.skin.SkinFetcher;
import com.jitse.npclib.api.state.NPCSlot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class NPCManager implements Listener {

	private final Map<UUID, CustomNPC> playerNpcs = Maps.newHashMap();
	private static final Map<UUID, Long> cooldownPlayer = Maps.newHashMap();
    private final List<CustomNPC> npcs = new ArrayList<>();
    private final YamlConfig config;
	private final NPCLib library;

    public NPCManager() {
    	this.config = new YamlConfig("Settings/NPCs");
		NPCLib library;
        try {
        	library = new NPCLib(KitPvP.getInstance());
		} catch (Exception e) {
        	library = null;
		}
        this.library = library;
        Bukkit.getPluginManager().registerEvents(this, KitPvP.getInstance());

        this.setup();
    }
    
    public void setup() {
        this.npcs.clear();
        Bukkit.getScheduler().runTask(KitPvP.getInstance(), () -> {
            if (config.contains("NPCs")) {
                for (String key : config.getConfigurationSection("NPCs").getKeys(false)) {
                    Location loc = LocationUtils.StringToLocation(config.getString("NPCs." + key + ".loc"));
                    loc.setX(loc.getBlockX() + 0.5);
                    loc.setZ(loc.getBlockZ() + 0.5);

                    NPCType npcType = NPCType.getByName(config.getString("NPCs." + key + ".type", "SHOP"));
                    NPC npc = addNPC(key, loc, npcType).getNPC();
                    config.set("NPCs." + key + ".id", npc.getId());
					for(Player online : Bukkit.getOnlinePlayers()) {
						npc.show(online);
						if(npcType == NPCType.USER) {
							SkinFetcher.fetchSkinFromNameAsync(online.getName().toLowerCase(), callback -> npc.updateSkinToPlayer(callback, online));
						}
					}
                }
            }

            config.save();
            config.reload();
        });
    }

	public CustomNPC createNPC(String key, Location loc, NPCType npcType, Player player) {
		String locString = LocationUtils.LocationToString(loc);
		if(npcType == NPCType.USER) {
			config.set("NPCs." + key + ".name", "&a%player%");
		} else {
			config.set("NPCs." + key + ".name", "&a" + npcType.name());
		}
		config.set("NPCs."+key+".loc", locString);
		config.set("NPCs."+key+".type", npcType.name());
		config.set("NPCs."+key+".skin", player.getName());
		config.set("NPCs."+key+".inv.mainhand", "DIAMOND_SWORD");
		config.set("NPCs."+key+".inv.offhand", "");
		config.set("NPCs."+key+".inv.helmet", "");
		config.set("NPCs."+key+".inv.chestplate", "");
		config.set("NPCs."+key+".inv.leggings", "");
		config.set("NPCs."+key+".inv.boots", "");

		CustomNPC customNPC = addNPC(key, loc, npcType);
		config.set("NPCs." + key + ".id", customNPC.getNPC().getId());
		config.save();
		config.reload();

		for(Player online : Bukkit.getOnlinePlayers()) {
			customNPC.getNPC().show(online);
			if(npcType == NPCType.USER) {
				SkinFetcher.fetchSkinFromNameAsync(online.getName().toLowerCase(), callback -> customNPC.getNPC().updateSkinToPlayer(callback, online));
			}
		}
		return customNPC;
	}

	public CustomNPC addNPC(String key, Location loc, NPCType npcType) {
		List<String> lines = Collections.singletonList(KitPvP.Color(config.getString("NPCs." + key + ".name")));
		NPC npc = library.createNPC(lines);
		npc.setLocation(loc);

		for(NPCSlot slot : NPCSlot.values()) {
			if(config.isSet("NPCs."+key+".inv."+slot.getNmsName().toLowerCase()) && config.getString("NPCs."+key+".inv."+slot.getNmsName().toLowerCase()) != null && !config.getString("NPCs."+key+".inv."+slot.getNmsName().toLowerCase()).isEmpty()) {
				npc.setItem(slot, new ItemStack(Material.getMaterial(config.getString("NPCs."+key+".inv."+slot.getNmsName().toLowerCase()))));
			}
		}

		SkinFetcher.fetchSkinFromNameAsync(config.getString("NPCs." + key + ".skin", "GabrielHD55"), npc::setSkin);

		CustomNPC customNPC = new CustomNPC(key, npc.create(), npcType);
		npcs.add(customNPC);
		return customNPC;
	}
    
    public boolean removeNPC(CustomNPC npc) {
    	if (npc == null) {
    		return false;
    	}
    	npc.getNPC().destroy();
		config.set("NPCs." + npc.getKey(), null);
		config.save();
		config.reload();
    	npcs.remove(npc);
    	return true;
    }
    
    public CustomNPC getNPC(String key) {
        for (CustomNPC npc : npcs) {
        	if (npc.getNPC().getId().equals(key) || npc.getKey().equalsIgnoreCase(key)) {
        		return npc;
			}
        }
        return null;
    }
    
    public void destroy() {
    	npcs.forEach(npc -> npc.getNPC().destroy());
    	npcs.clear();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
    	Player player = e.getPlayer();
    	Bukkit.getScheduler().runTask(KitPvP.getInstance(), () -> {
    		if (player.isOnline()) {
    		    npcs.forEach(customNPC -> customNPC.getNPC().show(player));
            }
    	});
    }

    @EventHandler
    public void onInteract(NPCInteractEvent e) {
    	CustomNPC npc = getNPC(e.getNPC().getId());
    	if (npc == null) {
    		return;
		}
    	
    	Player player = e.getWhoClicked();
    	if(npc.getNpcType() == NPCType.SHOP) {
			new ShopMenu().openInventory(player);
			return;
		}
    	if(npc.getNpcType() == NPCType.USER) {
    		new UserMenu(player).openInventory(player);
    		return;
		}
    	if(npc.getNpcType() == NPCType.QUESTS) {
    		new QuestsMenu(player).openInventory(player);
    		return;
		}
    	if(npc.getNpcType() == NPCType.UPGRADES) {
    		new ReforgeMenu(player.getItemInHand()).openInventory(player);
    		return;
		}

		if(npc.getNpcType() == NPCType.KIT) {
			if(System.currentTimeMillis() - cooldownPlayer.getOrDefault(player.getUniqueId(), 0L) > TimeUnit.SECONDS.toMillis(KitPvP.getConfigManager().getSettings().getInt("KitCooldown"))) {
				cooldownPlayer.remove(player.getUniqueId());

				for (Kit kit : KitPvP.getKitsManager().getKits().values()) {
					if(kit.getName().equalsIgnoreCase(KitPvP.getConfigManager().getKits().getString("Default-Kit"))) {
						player.getInventory().clear();

						player.getInventory().setContents(kit.getContents());
						player.getInventory().setArmorContents(kit.getArmor());
						player.updateInventory();
						break;
					}
				}

				cooldownPlayer.put(player.getUniqueId(), System.currentTimeMillis());
			}
		}
    }

    public List<CustomNPC> getNPCs() {
        return npcs;
    }

    public YamlConfig getConfig() {
        return config;
    }
}
