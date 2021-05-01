package com.gabrielhd.kitpvp.Database.Types;

import com.gabrielhd.kitpvp.Database.DataHandler;
import com.gabrielhd.kitpvp.Kits.Kit;
import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.Player.PlayerData;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

public class MySQL implements DataHandler {

    private final String url;
    private final String username;
    private final String password;
    private final String table;
    private Connection connection;
    private HikariDataSource ds;

    public MySQL(String host, String port, String database, String username, String password) {
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        this.username = username;
        this.password = password;
        this.table = KitPvP.getConfigManager().getSettings().getString("MySQL.TableName");

        try {
            setConnectionArguments();
        } catch (RuntimeException e) {
            if (e instanceof IllegalArgumentException) {
                System.out.println("Invalid database arguments! Please check your configuration!");
                System.out.println("If this error persists, please report it to the developer!");
                throw new IllegalArgumentException(e);
            }
            if (e instanceof HikariPool.PoolInitializationException) {
                System.out.println("Can't initialize database connection! Please check your configuration!");
                System.out.println("If this error persists, please report it to the developer!");
                throw new HikariPool.PoolInitializationException(e);
            }
            System.out.println("Can't use the Hikari Connection Pool! Please, report this error to the developer!");
            throw e;
        }

        this.setupTable();
    }

    private synchronized void setConnectionArguments() throws RuntimeException {
        this.ds = new HikariDataSource();
        this.ds.setPoolName("Friends MySQL");
        this.ds.setDriverClassName("com.mysql.jdbc.Driver");
        this.ds.setJdbcUrl(this.url);
        this.ds.addDataSourceProperty("cachePrepStmts", "true");
        this.ds.addDataSourceProperty("prepStmtCacheSize", "250");
        this.ds.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        this.ds.addDataSourceProperty("characterEncoding", "utf8");
        this.ds.addDataSourceProperty("encoding", "UTF-8");
        this.ds.addDataSourceProperty("useUnicode", "true");
        this.ds.addDataSourceProperty("useSSL", "false");
        this.ds.setUsername(username);
        this.ds.setPassword(password);
        this.ds.setMaxLifetime(180000);
        this.ds.setIdleTimeout(60000);
        this.ds.setMinimumIdle(1);
        this.ds.setMaximumPoolSize(8);
        try {
            this.connection = this.ds.getConnection();
        } catch (SQLException e) {
            System.out.println("Error on setting connection!");
        }
        System.out.println("Connection arguments loaded, Hikari ConnectionPool ready!");
    }

    public void setupTable() {
        try {
            Statement statement = this.connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.table + " (UUID VARCHAR(100), PlayerName VARCHAR(40))");
            DatabaseMetaData dm = this.connection.getMetaData();

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
    }

    public ResultSet query(String query) {
        try {
            Statement stmt = this.connection.createStatement();
            return stmt.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
        if (this.connection != null) {
            try {
                if (!this.playerExists(player.getUniqueId())) {
                    this.connection.createStatement().executeUpdate("INSERT INTO " + this.table + " (UUID, PlayerName, Kills, Deaths, KillStreak, Coins, Level, Exp, Kits) VALUES ('" + player.getUniqueId() + "', '" + player.getName() + "', '0', '0', '0', '0.0', '1', '10', 'none');");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
        if (this.connection != null) {
            try {
                this.connection.createStatement().executeUpdate("UPDATE "+this.table+" SET " +
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
            if(i != 0) {
                s.append(":");
            }
            i--;
        }
        return s.toString();
    }

    @Override
    public void close() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}