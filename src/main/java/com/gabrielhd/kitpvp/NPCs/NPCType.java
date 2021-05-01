package com.gabrielhd.kitpvp.NPCs;

public enum NPCType {

    SHOP, KIT, USER, UPGRADES, QUESTS;

    public static NPCType getByName(String str) {
        for (NPCType npcType : values()) {
            if(npcType.name().equalsIgnoreCase(str)) {
                return npcType;
            }
        }
        return null;
    }
}
