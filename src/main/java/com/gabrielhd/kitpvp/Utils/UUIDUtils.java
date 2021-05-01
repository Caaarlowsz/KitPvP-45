package com.gabrielhd.kitpvp.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.util.UUIDTypeAdapter;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class UUIDUtils {

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();

    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s?at=%d";

    public static UUID getUUID(String name) {
        name = name.toLowerCase();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUID_URL, name, System.currentTimeMillis() / 1000)).openConnection();
            connection.setReadTimeout(5000);

            PlayerUUID player = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), PlayerUUID.class);

            return player.getId();
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§cYour server has no connection to the mojang servers or is runnig slow.");
            Bukkit.getConsoleSender().sendMessage("§cTherefore the UUID cannot be parsed.");
            return null;
        }
    }

    public static boolean isUUID(String uuid) {
        return uuid.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}");
    }

    class PlayerUUID {
        private String name;

        public String getName() {
            return name;
        }

        public UUID getId() {
            return id;
        }

        private UUID id;
    }
}
