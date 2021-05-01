package com.gabrielhd.kitpvp;

import com.bizarrealex.aether.Aether;
import com.gabrielhd.kitpvp.Blocks.GameBlock;
import com.gabrielhd.kitpvp.Commands.CoinsCmd;
import com.gabrielhd.kitpvp.Commands.KitCmd;
import com.gabrielhd.kitpvp.Database.Database;
import com.gabrielhd.kitpvp.Listeners.LoginListeners;
import com.gabrielhd.kitpvp.Listeners.PlayerListeners;
import com.gabrielhd.kitpvp.Managers.*;
import com.gabrielhd.kitpvp.Scoreboard.BoardBuilder;
import com.gabrielhd.kitpvp.Task.TimeChecker;
import com.gabrielhd.kitpvp.Utils.LocationUtils;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class KitPvP extends JavaPlugin {

    @Getter private static KitPvP instance;
    @Getter private static ConfigManager configManager;
    @Getter private static PlayerManager playerManager;
    @Getter private static RegionManager regionManager;
    @Getter private static QuestsManager questsManager;
    @Getter private static KitsManager kitsManager;
    @Getter private static NPCManager npcManager;

    @Getter @Setter private static Location mainLobby;
    @Getter private static final List<GameBlock> blocks = Lists.newArrayList();
    private Aether aether;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        playerManager = new PlayerManager();
        regionManager = new RegionManager();
        questsManager = new QuestsManager();
        kitsManager = new KitsManager();
        npcManager = new NPCManager();

        new Database();

        this.getCommand("kitpvp").setExecutor(new KitCmd());
        this.getCommand("coins").setExecutor(new CoinsCmd());
        this.getServer().getPluginManager().registerEvents(new LoginListeners(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListeners(), this);
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new TimeChecker(), 1200L, 1200L);

        if(configManager.getSettings().getBoolean("Scoreboard.Enable")) {
            this.aether = new Aether(this, new BoardBuilder());
        }

        if(configManager.getRegions().isSet("LobbyLocation")) {
            mainLobby = LocationUtils.StringToLocation(configManager.getRegions().getString("LobbyLocation"));
        }
    }

    @Override
    public void onDisable() {
        npcManager.destroy();
    }

    public static String Color(String a) {
        return a.replaceAll("&", "§");
    }
}
