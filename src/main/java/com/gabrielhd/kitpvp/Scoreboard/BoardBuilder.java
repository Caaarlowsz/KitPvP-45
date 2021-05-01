package com.gabrielhd.kitpvp.Scoreboard;

import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.BoardAdapter;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.Player.PlayerData;
import com.google.common.collect.Lists;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

public class BoardBuilder implements BoardAdapter {

    @Override
    public String getTitle(Player p0) {
        return KitPvP.Color(KitPvP.getConfigManager().getSettings().getString("Scoreboard.Title"));
    }

    @Override
    public List<String> getScoreboard(Player p, Board p1, Set<BoardCooldown> p2) {
        PlayerData playerData = KitPvP.getPlayerManager().getPlayerData(p);
        if (playerData != null) {
            return this.getGameBoard(p, playerData);
        }
        return null;
    }

    private List<String> getGameBoard(Player p, PlayerData playerData) {
        List<String> lines = Lists.newArrayList();
        lines.clear();

        DecimalFormat df2 = new DecimalFormat("#.##");

        for (String replaceText : KitPvP.getConfigManager().getSettings().getStringList("Scoreboard.Lines")) {
            replaceText = replaceText.replaceAll("%player%", p.getName());
            replaceText = replaceText.replaceAll("%player-displayname%", p.getDisplayName());

            replaceText = replaceText.replaceAll("%kills%", String.valueOf(playerData.getKills()));
            replaceText = replaceText.replaceAll("%deaths%", String.valueOf(playerData.getDeaths()));
            replaceText = replaceText.replaceAll("%killstreak%", String.valueOf(playerData.getKillstreak()));
            replaceText = replaceText.replaceAll("%kdr%", df2.format(playerData.getKilltodeathratio()));
            replaceText = replaceText.replaceAll("%level%", String.valueOf(playerData.getLevel()));
            replaceText = replaceText.replaceAll("%xp%", String.valueOf(playerData.getExp()));
            replaceText = replaceText.replaceAll("%coins%", String.valueOf(playerData.getCoins()));

            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                replaceText = PlaceholderAPI.setPlaceholders(p, replaceText);
            }

            replaceText = replaceText.replaceAll("%empty%", " ");
            replaceText = KitPvP.Color(replaceText);

            lines.add(replaceText);
        }
        return lines;
    }
}