package com.jitse.npclib.api.skin;

import com.gabrielhd.kitpvp.Utils.UUIDUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Jitse Boonstra
 */
public class SkinFetcher {

    private static final String MINESKIN_API_ID = "https://api.mineskin.org/get/id/";
    private static final String MINESKIN_API_UUID = "https://api.mineskin.org/get/uuid/";
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public static Skin fetchSkinFromIdSync(int id) {
        try {
            StringBuilder builder = new StringBuilder();
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(MINESKIN_API_ID + id).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();

            Scanner scanner = new Scanner(httpURLConnection.getInputStream());
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }

            scanner.close();
            httpURLConnection.disconnect();

            JsonObject jsonObject = (JsonObject) new JsonParser().parse(builder.toString());
            JsonObject textures = jsonObject.get("data").getAsJsonObject().get("texture").getAsJsonObject();
            String value = textures.get("value").getAsString();
            String signature = textures.get("signature").getAsString();

            return (new Skin(String.valueOf(id), value, signature));
        } catch (IOException exception) {
            Bukkit.getLogger().severe("Could not fetch skin! (Id: " + id + "). Message: " + exception.getMessage());
            exception.printStackTrace();
            return null;
        }
    }

    public static void fetchSkinFromIdAsync(int id, Callback callback) {
        EXECUTOR.execute(() -> {
            try {
                StringBuilder builder = new StringBuilder();
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(MINESKIN_API_ID + id).openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                Scanner scanner = new Scanner(httpURLConnection.getInputStream());
                while (scanner.hasNextLine()) {
                    builder.append(scanner.nextLine());
                }

                scanner.close();
                httpURLConnection.disconnect();

                JsonObject jsonObject = (JsonObject) new JsonParser().parse(builder.toString());
                JsonObject textures = jsonObject.get("data").getAsJsonObject().get("texture").getAsJsonObject();
                String value = textures.get("value").getAsString();
                String signature = textures.get("signature").getAsString();

                callback.call(new Skin(String.valueOf(id), value, signature));
            } catch (IOException exception) {
                Bukkit.getLogger().severe("Could not fetch skin! (Id: " + id + "). Message: " + exception.getMessage());
                exception.printStackTrace();
                callback.failed();
            }
        });
    }

    public static void fetchSkinFromNameAsync(String player, Callback callback) {
        EXECUTOR.execute(() -> {
            try {
                String uuid;
                if(UUIDUtils.isUUID(player)) {
                    uuid = player;
                } else {
                    uuid = UUIDUtils.getUUID(player).toString();
                }
                URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
                InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
                JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
                String texture = textureProperty.get("value").getAsString();
                String signature = textureProperty.get("signature").getAsString();

                callback.call(new Skin(player, texture, signature));
            } catch (IOException exception) {
                Bukkit.getLogger().severe("Could not fetch skin! (Id: " + player + "). Message: " + exception.getMessage());
                callback.failed();
            }
        });
    }

    public interface Callback {

        void call(Skin skinData);

        default void failed() {
        }
    }
}
