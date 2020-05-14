package server.hyriftcraft.lms.lms;

import server.hyriftcraft.lms.utils.ConfigManager;

public class LMSScheduler {
    private static LMSScheduler single_inst = null;

    ConfigManager configManager = ConfigManager.getInstance();

    private LMSScheduler() {
    }

    public static LMSScheduler getInstance() {
        if (single_inst == null) {
            single_inst = new LMSScheduler();
        }
        return single_inst;
    }

    public void init(){
        if (!configManager.getConfig("config.yml").contains("config.use_schedule")){
            return;
        }

        int numberOfEvents = configManager.getInt(configManager.getConfig("config.yml"), "config.number_of_events");

        for (int id = 0; id < numberOfEvents; id++){
        }
    }
}