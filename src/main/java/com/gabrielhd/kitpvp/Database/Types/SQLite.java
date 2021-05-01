package com.gabrielhd.kitpvp.Database.Types;

import com.gabrielhd.kitpvp.Database.DataHandler;
import com.gabrielhd.kitpvp.Kits.Kit;
import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.Player.PlayerData;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

public class SQLite implements DataHandler {

    private final String table;
    private Connection con;

    public SQLite() {
        this.table = KitPvP.getConfigManager().getSettings().getString("MySQL.TableName");
        this.connect();
        this.setup();
    }

    private synchronized void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.con = DriverManager.getConnection("jdbc:sqlite:" + KitPvP.getInstance().getDataFolder() + "/Database.db");
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private synchronized void setup() {
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.table + " (UUID VARCHAR(100), PlayerName VARCHAR(40))");
            DatabaseMetaData dm = this.con.getMetaData();

            ResultSet kills = dm.getColumns(null, null, this.table, "Kills");
            if (!kills.next()) {
                statement.executeUpdate("ALTER TABLE " + this.table + " ADD COLUMN Kills int AFTER PlayerName;");
            }
            kills.close();

            ResultSet deaths = dm.getColumns(null, null, this.table, "Deaths");
            if (!deaths.next()) {
                statement.executeUpdate("ALTER TABLE " + this.table + " ADD COLUMN Deaths int AFTER Kills;");
            }
            deaths.close();

            ResultSet killstreak = dm.getColumns(null, null, this.table, "KillStreak");
            if (!killstreak.next()) {
                statement.executeUpdate("ALTER TABLE " + this.table + " ADD COLUMN KillStreak int AFTER Deaths;");
            }
            killstreak.close();

            ResultSet coins = dm.getColumns(null, null, this.table, "Coins");
            if (!coins.next()) {
                statement.executeUpdate("ALTER TABLE " + this.table + " ADD COLUMN Coins double AFTER KillStreak;");
            }
            coins.close();

            ResultSet level = dm.getColumns(null, null, this.table, "Level");
            if (!level.next()) {
                statement.executeUpdate("ALTER TABLE " + this.table + " ADD COLUMN Level int AFTER Coins;");
            }
            level.close();

            ResultSet exp = dm.getColumns(null, null, this.table, "Exp");
            if (!exp.next()) {
                statement.executeUpdate("ALTER TABLE " + this.table + " ADD COLUMN Exp int AFTER Level;");
            }
            exp.close();

            ResultSet kits = dm.getColumns(null, null, this.table, "Kits");
            if (!kits.next()) {
                statement.executeUpdate("ALTER TABLE " + this.table + " ADD COLUMN Kits TEXT AFTER Exp;");
            }
            kits.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        KitPvP.getInstance().getLogger().info("SQLite Setup finished");
    }

    public boolean playerExists(UUID uuid) {
        try {
            ResultSet rs = this.query("SELECT * FROM " + this.table + " WHERE UUID='" + uuid.toString() + "'");
            return (rs != null && rs.next()) && rs.getString("UUID") != null;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void createPlayer(Player player) {
        if (this.con != null) {
            try {
                if (!this.playerExists(player.getUniqueId())) {
                    this.con.createStatement().executeUpdate("INSERT INTO " + this.table + " (UUID, PlayerName, Kills, Deaths, KillStreak, Coins, Level, Exp, Kits) VALUES ('" + player.getUniqueId() + "', '" + player.getName() + "', '0', '0', '0', '0.0', '1', '10', 'none');");
                    KitPvP.getInstance().getLogger().info("Data "+player.getUniqueId().toString()+" created.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ResultSet query(String query) {
        try {
            Statement stmt = this.con.createStatement();
            return stmt.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void loadPlayer(PlayerData playerData) {
        ResultSet resultSet = this.query("SELECT * FROM '"+this.table+"' WHERE UUID='" + playerData.getUuid() + "'");
        try {
            if(resultSet != null && resultSet.next()) {
                playerData.setKills(resultSet.getInt("Kills"));
                playerData.setDeaths(resultSet.getInt("Deaths"));
                playerData.setKillstreak(resultSet.getInt("KillStreak"));
                playerData.setCoins(resultSet.getDouble("Coins"));
                playerData.setLevel(resultSet.getInt("Level"));
                playerData.setExp(resultSet.getLong("Exp"));
                String[] skits = resultSet.getString("Kits").split(":");
                for(String skit : skits) {
                    Kit kit = KitPvP.getKitsManager().getKit(skit);
                    if(kit != null) {
                        playerData.getKits().add(kit);
                    }
                }
            } else {
                this.createPlayer(playerData.getPlayer());
            }
        } catch (SQLException ex) {
            this.createPlayer(playerData.getPlayer());
        }
    }

    @Override
    public void uploadPlayer(PlayerData playerData) {
        if (this.con != null) {
            try {
                this.con.createStatement().executeUpdate("UPDATE "+this.table+" SET " +
                        "Kills='"+playerData.getKills() +"', " +
                        "Deaths='"+playerData.getDeaths() +"', " +
                        "KillStreak='"+playerData.getKillstreak() +"', " +
                        "Coins='"+playerData.getCoins() +"', " +
                        "Level='"+playerData.getLevel() +"', " +
                        "Exp='"+playerData.getCoins() +"', " +
                        "Kits='"+ getKitsString(playerData) +"' " +
                        "WHERE UUID='"+playerData.getUuid()+"';");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean existsPlayer(Player player) {
        try {
            ResultSet rs = this.query("SELECT * FROM " + this.table + " WHERE UUID='" + player.getUniqueId().toString() + "'");
            return (rs != null && rs.next()) && rs.getString("UUID") != null;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public String getKitsString(PlayerData playerData) {
        StringBuilder s = new StringBuilder();
        int i = playerData.getKits().size();
        for(Kit kit : playerData.getKits()) {
            s.append(kit.getName());
            if(i > 0) {
                s.append(":");
            }
            i--;
        }
        return s.toString();
    }

    @Override
    public void close() {
        try {
            if (this.con != null && !this.con.isClosed()) {
                this.con.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
