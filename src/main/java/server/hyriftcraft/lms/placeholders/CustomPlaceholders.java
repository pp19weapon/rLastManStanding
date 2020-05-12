package server.hyriftcraft.lms.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import server.hyriftcraft.lms.lms.LMSHandler;
import server.hyriftcraft.lms.utils.ConfigManager;

public class CustomPlaceholders extends PlaceholderExpansion {

    private Plugin plugin;

    public CustomPlaceholders(Plugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "rLMS";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        if (p == null){
            return "";
        }

        switch (params){
            case "announce_delay":
                return String.format("%d", ConfigManager.getInstance().getInt(ConfigManager.getInstance().getConfig("config.yml"), "config.before_game_announce_delay"));
            case "join_time":
                return String.format("%d", (int)LMSHandler.getInstance().getRunningGame().getJoiningTime());
            case "%winner_name%":
                return String.format(LMSHandler.getInstance().getLastWinner().getName());
        }

        return null;
    }


}
