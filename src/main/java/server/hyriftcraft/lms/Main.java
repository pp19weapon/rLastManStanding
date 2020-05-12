package server.hyriftcraft.lms;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import server.hyriftcraft.lms.commands.LMSCommand;
import server.hyriftcraft.lms.lms.LMSHandler;
import server.hyriftcraft.lms.placeholders.CustomPlaceholders;
import server.hyriftcraft.lms.utils.ConfigManager;
import server.hyriftcraft.lms.utils.EconomyHandler;
import server.hyriftcraft.lms.utils.PermissionHandler;

import java.util.logging.Logger;

public class Main extends JavaPlugin implements Listener {
    public static final Logger LOGGER = Logger.getLogger("Minecraft");

    @Override
    public void onEnable() {
        super.onEnable();

        ConfigManager.getInstance().setPlugin(this);
        ConfigManager.getInstance().getConfig("config.yml");

        //Hook PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new CustomPlaceholders(this).register();
        } else {
            getServer().getPluginManager().disablePlugin(this);
            getLogger().severe("[rLastManStanding] - Disabled due to no PlaceholderAPI dependency found!");
        }

        //Hook vault
        if (getServer().getPluginManager().getPlugin("Vault") == null){
            getServer().getPluginManager().disablePlugin(this);
            getLogger().severe("[rLastManStanding] - Disabled due to no Vault dependency found!");
        } else {
            RegisteredServiceProvider<Economy> rspEcon = getServer().getServicesManager().getRegistration(Economy.class);
            EconomyHandler.getInstance().setEconomy(rspEcon.getProvider());

            RegisteredServiceProvider<Permission> rspPerm = getServer().getServicesManager().getRegistration(Permission.class);
            PermissionHandler.getInstance().setPermission(rspPerm.getProvider());
        }

        this.getServer().getPluginManager().registerEvents(this, this);

        this.getCommand("lastmanstanding").setExecutor(new LMSCommand(this));

    }

    @EventHandler
    public void onWorldLoadEvent(WorldLoadEvent event){
        LMSHandler.getInstance().autoRegisterAlreadyDefinedGames(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        getLogger().info("[rLastManStanding] - Shutting down.");
    }
}
