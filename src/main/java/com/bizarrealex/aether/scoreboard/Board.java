package com.bizarrealex.aether.scoreboard;

import com.bizarrealex.aether.Aether;
import com.bizarrealex.aether.AetherOptions;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Board
{
    private static Set<Board> boards;
    private Scoreboard scoreboard;
    private final Player player;
    private Objective objective;
    private Objective objective2;
    private final Set<String> keys;
    private final List<BoardEntry> entries;
    private final Set<BoardCooldown> cooldowns;
    private final Aether aether;
    private final AetherOptions options;
    
    static {
        Board.boards = new HashSet<>();
    }
    
    public Board(Player player, Aether aether, AetherOptions options) {
        this.player = player;
        this.aether = aether;
        this.options = options;
        this.keys = new HashSet<>();
        this.cooldowns = new HashSet<>();
        this.entries = new ArrayList<>();
        this.setup();
    }
    
    private void setup() {
        if (this.options.hook() && !this.player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            this.scoreboard = this.player.getScoreboard();
        }
        else {
            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }
        (this.objective = this.scoreboard.registerNewObjective("glaedr_is_shit", "dummy")).setDisplaySlot(DisplaySlot.SIDEBAR);
        (this.objective2 = this.scoreboard.registerNewObjective("belowName", Criterias.HEALTH)).setDisplaySlot(DisplaySlot.BELOW_NAME);
        if (this.aether.getAdapter() != null) {
            this.objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.aether.getAdapter().getTitle(this.player)));
            this.objective2.setDisplayName(ChatColor.DARK_RED+"\u2764");
        }
        else {
            this.objective.setDisplayName("Default Title");
        }
        Board.boards.add(this);
    }
    
    public String getNewKey(BoardEntry entry) {
        ChatColor[] values;
        for (int length = (values = ChatColor.values()).length, i = 0; i < length; ++i) {
            ChatColor color = values[i];
            String colorText = String.valueOf(color) + ChatColor.WHITE;
            if (entry.getText().length() > 16) {
                String sub = entry.getText().substring(0, 16);
                colorText = colorText + ChatColor.getLastColors(sub);
            }
            if (!this.keys.contains(colorText)) {
                this.keys.add(colorText);
                return colorText;
            }
        }
        throw new IndexOutOfBoundsException("No more keys available!");
    }
    
    public List<String> getBoardEntriesFormatted() {
        List<String> toReturn = new ArrayList<>();
        for (BoardEntry entry : new ArrayList<>(this.entries)) {
            toReturn.add(entry.getText());
        }
        return toReturn;
    }
    
    public BoardEntry getByPosition(int position) {
        int i = 0;
        for (BoardEntry board : this.entries) {
            if (i == position) {
                return board;
            }
            ++i;
        }
        return null;
    }
    
    public BoardCooldown getCooldown(String id) {
        for (BoardCooldown cooldown : this.getCooldowns()) {
            if (cooldown.getId().equals(id)) {
                return cooldown;
            }
        }
        return null;
    }
    
    public Set<BoardCooldown> getCooldowns() {
        this.cooldowns.removeIf(cooldown -> System.currentTimeMillis() >= cooldown.getEnd());
        return this.cooldowns;
    }
    
    public static Board getByPlayer(Player player) {
        for (Board board : Board.boards) {
            if (board.getPlayer().getName().equals(player.getName())) {
                return board;
            }
        }
        return null;
    }
    
    public static Set<Board> getBoards() {
        return Board.boards;
    }
    
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public Objective getObjective() {
        return this.objective;
    }
    
    public Set<String> getKeys() {
        return this.keys;
    }
    
    public List<BoardEntry> getEntries() {
        return this.entries;
    }
}
