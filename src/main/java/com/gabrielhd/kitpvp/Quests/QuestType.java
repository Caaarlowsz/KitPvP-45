package com.gabrielhd.kitpvp.Quests;

public enum QuestType {

    Survive,
    Killing;

    public static QuestType getQuest(String name) {
        for(QuestType questType : values()) {
            if(questType.name().equalsIgnoreCase(name)) {
                return questType;
            }
        }
        return null;
    }
}
