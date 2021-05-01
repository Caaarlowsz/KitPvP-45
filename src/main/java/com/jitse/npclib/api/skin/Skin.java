/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package com.jitse.npclib.api.skin;

public class Skin {

    private final String name, value, signature;

    public Skin(String name, String value, String signature) {
        this.name = name;
        this.value = value;
        this.signature = signature;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return this.value;
    }

    public String getSignature() {
        return this.signature;
    }
}
