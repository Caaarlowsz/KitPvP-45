package com.gabrielhd.kitpvp.NPCs;

import com.jitse.npclib.api.NPC;

public class CustomNPC {

    private final String key;
    private final NPC npc;
    private final NPCType npcType;

    public CustomNPC(String key, NPC npc, NPCType npcType) {
        this.key = key;
        this.npc = npc;
        this.npcType = npcType;
    }

    public String getKey() {
        return key;
    }

    public NPC getNPC() {
    	return this.npc;
    }

    public NPCType getNpcType() {
        return npcType;
    }
}
