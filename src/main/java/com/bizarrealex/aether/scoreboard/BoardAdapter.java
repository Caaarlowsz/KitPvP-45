package com.bizarrealex.aether.scoreboard;

import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public interface BoardAdapter
{
    String getTitle(Player p0);
    
    List<String> getScoreboard(Player p0, Board p1, Set<BoardCooldown> p2);
}
