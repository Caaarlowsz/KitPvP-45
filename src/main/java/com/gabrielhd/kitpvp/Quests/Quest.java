package com.gabrielhd.kitpvp.Quests;

import lombok.Getter;
import lombok.Setter;

public class Quest {

    @Getter private final String missionID;
    @Getter private final QuestType questType;
    @Getter @Setter private int amount;
    @Getter @Setter private int time;

    public Quest(QuestType type, String missionID) {
        this.questType = type;
        this.missionID = missionID;
    }
}
