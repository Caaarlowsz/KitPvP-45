package com.bizarrealex.aether;

import com.bizarrealex.aether.event.BoardCreateEvent;
import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.BoardAdapter;
import com.bizarrealex.aether.scoreboard.BoardEntry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Aether implements Listener
{
    private final JavaPlugin plugin;
    private final AetherOptions options;
    private BukkitTask bukkitTask;
    BoardAdapter adapter;
    
    public Aether(JavaPlugin plugin, BoardAdapter adapter, AetherOptions options) {
        this.options = options;
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.setAdapter(adapter);
        this.run();
    }
    
    public Aether(JavaPlugin plugin, BoardAdapter adapter) {
        this(plugin, adapter, AetherOptions.defaultOptions());
    }
    
    public Aether(JavaPlugin plugin) {
        this(plugin, null, AetherOptions.defaultOptions());
    }

    public void delete() {
        this.bukkitTask.cancel();
    }
    
    private void run() {
        this.bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (Aether.this.adapter == null) {
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Board board = Board.getByPlayer(player);
                    if (board != null) {
                        List<String> scores = Aether.this.adapter.getScoreboard(player, board, board.getCooldowns());
                        List<String> translatedScores = new ArrayList<>();
                        if (scores == null) {
                            if (board.getEntries().isEmpty()) {
                                continue;
                            }
                            for (BoardEntry boardEntry : board.getEntries()) {
                                boardEntry.remove();
                            }
                            board.getEntries().clear();
                        }
                        else {
                            for (String line : scores) {
                                translatedScores.add(ChatColor.translateAlternateColorCodes('&', line));
                            }
                            if (!Aether.this.options.scoreDirectionDown()) {
                                Collections.reverse(scores);
                            }
                            Scoreboard scoreboard = board.getScoreboard();
                            Objective objective = board.getObjective();
                            if (!objective.getDisplayName().equals(Aether.this.adapter.getTitle(player))) {
                                objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Aether.this.adapter.getTitle(player)));
                            }
                        Block_12:
                            for (int i = 0; i < scores.size(); ++i) {
                                String text = scores.get(i);
                                int position;
                                if (Aether.this.options.scoreDirectionDown()) {
                                    position = 15 - i;
                                }
                                else {
                                    position = i + 1;
                                }
                                while (true) {
                                    for (BoardEntry boardEntry2 : new ArrayList<>(board.getEntries())) {
                                        Score score = objective.getScore(boardEntry2.getKey());
                                        if (score != null && boardEntry2.getText().equals(ChatColor.translateAlternateColorCodes('&', text)) && score.getScore() == position) {
                                            continue Block_12;
                                        }
                                    }
                                    int positionToSearch = Aether.this.options.scoreDirectionDown() ? (15 - position) : (position - 1);
                                    Iterator<BoardEntry> iterator = board.getEntries().iterator();
                                    while (iterator.hasNext()) {
                                        BoardEntry boardEntry3 = iterator.next();
                                        int entryPosition = scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(boardEntry3.getKey()).getScore();
                                        if (!Aether.this.options.scoreDirectionDown() && entryPosition > scores.size()) {
                                            iterator.remove();
                                            boardEntry3.remove();
                                        }
                                    }
                                    BoardEntry entry = board.getByPosition(positionToSearch);
                                    if (entry == null) {
                                        new BoardEntry(board, text).send(position);
                                    }
                                    else {
                                        entry.setText(text).setup().send(position);
                                    }
                                    if (board.getEntries().size() > scores.size()) {
                                        iterator = board.getEntries().iterator();
                                        while (iterator.hasNext()) {
                                            BoardEntry boardEntry4 = iterator.next();
                                            if (!translatedScores.contains(boardEntry4.getText()) || Collections.frequency(board.getBoardEntriesFormatted(), boardEntry4.getText()) > 1) {
                                                iterator.remove();
                                                boardEntry4.remove();
                                            }
                                        }
                                    }
                                }
                            }
                            player.setScoreboard(scoreboard);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(this.plugin, 20L, 20L);
    }
    
    public void setAdapter(BoardAdapter adapter) {
        this.adapter = adapter;
        for (Player player : Bukkit.getOnlinePlayers()) {
            Board board = Board.getByPlayer(player);
            if (board != null) {
                Board.getBoards().remove(board);
            }
            Bukkit.getPluginManager().callEvent(new BoardCreateEvent(new Board(player, this, this.options), player));
        }
    }
    
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (Board.getByPlayer(event.getPlayer()) == null) {
            Bukkit.getPluginManager().callEvent(new BoardCreateEvent(new Board(event.getPlayer(), this, this.options), event.getPlayer()));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Board board = Board.getByPlayer(event.getPlayer());
        if (board != null) {
            Board.getBoards().remove(board);
        }
    }
    
    public JavaPlugin getPlugin() {
        return this.plugin;
    }
    
    public AetherOptions getOptions() {
        return this.options;
    }
    
    public BoardAdapter getAdapter() {
        return this.adapter;
    }
}
