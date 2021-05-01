package com.gabrielhd.kitpvp.Managers;

import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.Player.OPPlayer;
import com.gabrielhd.kitpvp.Utils.Cuboid;
import com.gabrielhd.kitpvp.Utils.LocationUtils;
import com.gabrielhd.kitpvp.Utils.YamlConfig;
import lombok.Getter;

public class RegionManager {

    @Getter private Cuboid spawnRegion;

    public RegionManager() {
        this.loadRegions();
    }

    public void loadRegions() {
        YamlConfig regions = KitPvP.getConfigManager().getRegions();
        if(regions.isSet("SpawnRegion")) {
            this.spawnRegion = new Cuboid(LocationUtils.StringToLocation(regions.getString("SpawnRegion.Max")), LocationUtils.StringToLocation(regions.getString("SpawnRegion.Min")));
        }
    }

    public void setSpawnRegion(OPPlayer opPlayer) {
        this.spawnRegion = new Cuboid(opPlayer.getFirst(), opPlayer.getSecond());
    }

    public void saveRegions() {
        YamlConfig regions = KitPvP.getConfigManager().getRegions();

        if(this.getSpawnRegion() != null) {
            regions.set("SpawnRegion.Max", LocationUtils.LocationToString(this.getSpawnRegion().getL1()));
            regions.set("SpawnRegion.Min", LocationUtils.LocationToString(this.getSpawnRegion().getL2()));
        }

        regions.save();
    }
}
