package com.gabrielhd.kitpvp.Utils;

import com.gabrielhd.kitpvp.KitPvP;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class YamlConfig extends YamlConfiguration {

    private final File file;
    private final String path;

	public YamlConfig(String path) {
		this.path = (path + ".yml");
		this.file = new File(KitPvP.getInstance().getDataFolder(), this.path);
		saveDefault();
		reload();
	}

	public void reload() {
    	try {
    		 super.load(file);
    	} catch (Exception ignored) {}
	}

	public void save() {
		try {
   		 	super.save(file);
		} catch (Exception ignored) {}
	}

	public void saveDefault() {
		try {
			if (!file.exists()) {
				if(KitPvP.getInstance().getResource(path) != null) {
					KitPvP.getInstance().saveResource(path, false);
				} else {
					file.createNewFile();
				}
		    }
		} catch (Exception ignored) {}
	}
}
