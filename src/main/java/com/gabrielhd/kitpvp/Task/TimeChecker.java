package com.gabrielhd.kitpvp.Task;

import com.gabrielhd.kitpvp.Managers.QuestsManager;
import com.gabrielhd.kitpvp.Utils.YamlConfig;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeChecker implements Runnable {

    @Override
    public void run() {
        YamlConfig date = new YamlConfig("cache.yml");
        String s = new SimpleDateFormat("dd").format(new Date());
        if(date.isSet("Date")){
            if(!date.getString("Date").equalsIgnoreCase(s)) {
                new QuestsManager().generateQuests();
            }
        }
    }
}