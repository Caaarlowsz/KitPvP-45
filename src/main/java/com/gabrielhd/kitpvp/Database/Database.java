package com.gabrielhd.kitpvp.Database;

import com.gabrielhd.kitpvp.Database.Types.MySQL;
import com.gabrielhd.kitpvp.Database.Types.SQLite;
import com.gabrielhd.kitpvp.KitPvP;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

public class Database {

    @Getter private static DataHandler storage;

    public Database() {
        FileConfiguration data = KitPvP.getConfigManager().getSettings();
        if (data.getString("StorageType").equalsIgnoreCase("MySQL")) {
            storage = new MySQL(data.getString("MySQL.Host"), data.getString("MySQL.Port"), data.getString("MySQL.Database"), data.getString("MySQL.Username"), data.getString("MySQL.Password"));
        } else {
            storage = new SQLite();
        }
    }
}
