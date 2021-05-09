package com.gabrielhd.kitpvp.Quests;

import com.gabrielhd.kitpvp.KitPvP;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitRunnable;

public class Quest {

    @Getter private final String missionID;
    @Getter private final QuestType questType;
    @Getter @Setter private int amount;
    @Getter @Setter private int time;

    public Quest(QuestType type, String missionID, int amount, int time) {
        this.questType = type;
        this.missionID = missionID;

        this.amount = amount;
        this.time = time;

        new BukkitRunnable() {

            @Override
            public void run() {
                if(Quest.this.time <= 0) {
                    this.cancel();
                    return;
                }

                Quest.this.time--;
            }
        }.runTaskTimer(KitPvP.getInstance(), 0L, 20L);
    }
}
