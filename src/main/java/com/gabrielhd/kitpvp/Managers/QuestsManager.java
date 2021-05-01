package com.gabrielhd.kitpvp.Managers;

import com.gabrielhd.kitpvp.KitPvP;
import com.gabrielhd.kitpvp.Quests.Quest;
import com.gabrielhd.kitpvp.Quests.QuestType;
import com.gabrielhd.kitpvp.Utils.YamlConfig;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.SimpleDateFormat;
import java.util.*;

public class QuestsManager {

    @Getter private final Map<String, Quest> dailyQuests = Maps.newHashMap();
    @Getter private final Map<String, Quest> weeklyQuests = Maps.newHashMap();

    public void loadQuests() {
        this.dailyQuests.clear();
        this.weeklyQuests.clear();

        YamlConfig cache = new YamlConfig("cache.yml");
        String s = new SimpleDateFormat("dd").format(new Date());
        if(cache.isSet("Date")){
            if(!cache.getString("Date").equalsIgnoreCase(s)) {
                this.generateQuests();
                return;
            }
        }

        if(cache.isSet("Quests.Daily")) {
            for(QuestType type : QuestType.values()) {
                if (cache.isSet("Quests.Daily."+type)) {
                    Set<String> secList = cache.getConfigurationSection("Quests.Daily."+type).getKeys(false);
                    if (!secList.isEmpty()) {
                        for (String key : secList) {
                            Quest quest = new Quest(type, key);
                            quest.setAmount(cache.getInt("Quests.Daily."+type+"."+key+".Amount"));
                            quest.setTime(cache.getInt("Quests.Daily."+type+"."+key+".Time"));

                            this.dailyQuests.put(key.toLowerCase(), quest);
                        }
                    }
                }
            }
        }

        if(cache.isSet("Quests.Weekly")) {
            for(QuestType type : QuestType.values()) {
                if (cache.isSet("Quests.Weekly."+type)) {
                    Set<String> secList = cache.getConfigurationSection("Quests.Weekly."+type).getKeys(false);
                    if (!secList.isEmpty()) {
                        for (String key : secList) {
                            Quest quest = new Quest(type, key);
                            quest.setAmount(cache.getInt("Quests.Weekly."+type+"."+key+".Amount"));
                            quest.setTime(cache.getInt("Quests.Weekly."+type+"."+key+".Time"));

                            this.weeklyQuests.put(key.toLowerCase(), quest);
                        }
                    }
                }
            }
        }
    }

    public void generateQuests() {
        YamlConfig date = new YamlConfig("cache.yml");

        this.dailyQuests.clear();
        this.weeklyQuests.clear();

        FileConfiguration config = KitPvP.getConfigManager().getQuests();
        String s = new SimpleDateFormat("dd").format(new Date());
        if(config.isSet("Quests.Daily")) {
            for(QuestType type : QuestType.values()) {
                if (config.isSet("Quests.Daily."+type)) {
                    List<String> secList = new ArrayList<>(config.getConfigurationSection("Quests.Daily."+type).getKeys(false));
                    if (!secList.isEmpty()) {
                        for (int i = 0; i < 7; i++) {
                            if(this.dailyQuests.size() == 7) {
                                break;
                            }

                            int random = new Random().nextInt(secList.size());
                            String key = secList.get(random);

                            Quest quest = new Quest(QuestType.Killing, key);
                            quest.setAmount(config.getInt("Quests.Daily."+type+"."+key+".Amount"));
                            quest.setTime(config.getInt("Quests.Daily."+type+"."+key+".Time"));

                            this.dailyQuests.put(key.toLowerCase(), quest);
                        }
                    }
                }
            }
        }

        if(config.isSet("Quests.Weekly")) {
            for(QuestType type : QuestType.values()) {
                if (config.isSet("Quests.Weekly."+type)) {
                    List<String> secList = new ArrayList<>(config.getConfigurationSection("Quests.Weekly."+type).getKeys(false));
                    if (!secList.isEmpty()) {
                        for (int i = 0; i < 7; i++) {
                            if(this.weeklyQuests.size() == 7) {
                                break;
                            }

                            int random = new Random().nextInt(secList.size());
                            String key = secList.get(random);

                            Quest quest = new Quest(QuestType.Killing, key);
                            quest.setAmount(config.getInt("Quests.Weekly."+type+"."+key+".Amount"));
                            quest.setTime(config.getInt("Quests.Weekly."+type+"."+key+".Time"));

                            this.weeklyQuests.put(key.toLowerCase(), quest);
                        }
                    }
                }
            }
        }

        date.set("Date", s);
        date.save();
    }
}
