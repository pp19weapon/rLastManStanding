package server.hyriftcraft.lms.lms;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import server.hyriftcraft.lms.Main;
import server.hyriftcraft.lms.utils.ConfigManager;
import server.hyriftcraft.lms.utils.EconomyHandler;
import server.hyriftcraft.lms.utils.InventoryHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LMSGame implements Listener {
    private ConfigManager configManager = ConfigManager.getInstance();
    private Plugin plugin;
    private ArrayList<Player> joinedPlayers = new ArrayList<Player>();
    private Map<Player, LMSPlayerData> recordedPlayersData = new HashMap<Player, LMSPlayerData>();
    private int id;
    private boolean isActive = false;
    private List<Location> spawn_locations = new ArrayList<Location>();
    private double prize = 0;
    private int startingTime = 0;
    private int numberOfSpawns = 0;
    private double duration = 0;
    private int maxRetries = 0;
    private double joiningTime = 0;

    private boolean isOpen = false;
    private boolean isRunning = false;

    public boolean isRunning() { return isRunning; }
    public boolean isGameOpen() { return isOpen; }
    public double getJoiningTime() { return joiningTime; }

    public LMSGame(Plugin plg, int id){
        plugin = plg;
        this.id = id;
        loadConfigData();
    }

    public void start(){
        isRunning = true;
        prepareGame();
    }

    void stop(){
        recordedPlayersData.clear();
        joinedPlayers.clear();
        isOpen = false;
        isRunning = false;
    }

    void loadConfigData(){
        startingTime = configManager.getInt(configManager.getConfig("config.yml"), id + ".starting_time");
        isActive = configManager.getBoolean(configManager.getConfig("config.yml"), id + ".isActive");
        prize = configManager.getInt(configManager.getConfig("config.yml"), id + ".prize");
        maxRetries = configManager.getInt(configManager.getConfig("config.yml"), "config.retries");
        duration = configManager.getDouble(configManager.getConfig("config.yml"), id + ".duration");
        joiningTime = configManager.getDouble(configManager.getConfig("config.yml"), id + ".joining_time");

        //Loop trough spawn locations and load them in
        numberOfSpawns = configManager.getInt(configManager.getConfig("config.yml"), id + ".number_of_spawns");
        for (int i = 0; i < numberOfSpawns; i++){
            String path = String.format("%d.spawn_locations.loc%d", id, i);
            Location loc = configManager.getLocation(configManager.getConfig("config.yml"), path);
            spawn_locations.add(loc);
        }
    }

    //Prepares game then starts it
    void prepareGame(){
        for (Player player : Bukkit.getOnlinePlayers()){
            player.sendMessage(configManager.getString(configManager.getConfig("config.yml"), "broadcast.announce", player));
        }
        Main.LOGGER.info("[LMS] Will open soon!");

        //Start game after delay
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                Main.LOGGER.info("[LMS] Now open!");
                for (Player player : Bukkit.getOnlinePlayers()){
                    player.sendMessage(configManager.getString(configManager.getConfig("config.yml"), "broadcast.open", player));

                }
                startGame();
            }
        }, (int)(configManager.getDouble(configManager.getConfig("config.yml"), "config.before_game_announce_delay") * 1200));
    }

    void startGame(){
        isOpen = true;

        //Close joining
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                isOpen = false;
                for (Player player : Bukkit.getOnlinePlayers()){
                    player.sendMessage(configManager.getString(configManager.getConfig("config.yml"), "broadcast.close", player));
                }
                Main.LOGGER.info("[LMS] Event is now closed!");
            }
        }, (int)(joiningTime * 1200));

        //End event
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (checkWinCondition()) {
                    win(joinedPlayers.get(0));
                } else{
                    noWinner();
                }
            }
        },  (int)((joiningTime + duration) * 1200));
    }

    boolean checkWinCondition(){
        if (joinedPlayers.size() == 1){
            return true;
        }
        return false;
    }

    public boolean addSpawnLocation(Location location, Player senderPlayer){
        String path = String.format("%d.spawn_locations.loc%d", id, numberOfSpawns++);
        configManager.setData(configManager.getConfig("config.yml"), id + ".number_of_spawns", numberOfSpawns);

        if (!configManager.addLocation(configManager.getConfig("config.yml"), location, path)){
            senderPlayer.sendMessage( ChatColor.RED + "[LMS] Failed adding spawnpoint! Check console for more info!");
            Main.LOGGER.severe("[LMS] Failed adding spawnpoint to event id " + id + " at location: " + location);
            return false;
        }
        spawn_locations.add(location);
        return true;
    }

    private void win(Player player){
        player.sendMessage(ChatColor.YELLOW + "Congratulations! You won!");
        Main.LOGGER.info("[LastManStanding] Event ended!");
        EconomyHandler.getInstance().addMoney(player, prize);
        LMSHandler.getInstance().setLastWinner(player);
        recordedPlayersData.get(player).setPrize(prize);
        player.teleport(recordedPlayersData.get(player).getLastKnownLocation());
        stop();

        for (Player p : Bukkit.getOnlinePlayers()){
            player.sendMessage(configManager.getString(configManager.getConfig("config.yml"), "broadcast.win", p));
        }

    }

    private void noWinner(){
        for (Player player : Bukkit.getOnlinePlayers()){
            player.sendMessage(configManager.getString(configManager.getConfig("config.yml"), "broadcast.no_winner", player));
        }
        for (Player player : joinedPlayers){
            player.teleport(recordedPlayersData.get(player).getLastKnownLocation());
        }
        stop();
    }

    public boolean joinPlayer(Player player){
        if (!isGameOpen()){
            return false;
        }
        //Record and join new player
        if (!(recordedPlayersData.containsKey(player))){
            joinedPlayers.add(player);

            LMSPlayerData playerData = new LMSPlayerData();
            playerData.setRetried(1);
            playerData.setLastKnownLocation(player.getLocation());
            recordedPlayersData.put(player, playerData);

            teleportPlayerToSpawnpoints(player);
        } else {
            //Join players who have left tries
            if (recordedPlayersData.get(player).getRetried() < maxRetries){
                recordedPlayersData.get(player).setRetried(recordedPlayersData.get(player).getRetried() + 1);
                recordedPlayersData.get(player).setLastKnownLocation(player.getLocation());
                joinedPlayers.add(player);
                teleportPlayerToSpawnpoints(player);
            } else {
                player.sendMessage("[LastManStanding] You already used up all your chances, better luck next time!");
                return false;
            }
        }
        return true;
    }

    private int spawnID = 0;
    private void teleportPlayerToSpawnpoints(Player player){
        player.teleport(spawn_locations.get(spawnID));
        spawnID++;
        if (spawnID >= numberOfSpawns){
            spawnID = 0;
        }
    }



    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        if (joinedPlayers.contains(player)) {
            InventoryHandler.getInstance().saveInventoryAndArmor(player);
            event.setDroppedExp(0);
            event.getDrops().clear();
            event.setKeepLevel(true);
            joinedPlayers.remove(player);
            joinedPlayers.trimToSize();
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        InventoryHandler inventoryHandler = InventoryHandler.getInstance();

        if ((inventoryHandler.hasInventorySaved(player)) && (inventoryHandler.hasArmorSaved(player))){
            player.getInventory().setContents(inventoryHandler.loadInventory(player));
            player.getInventory().setArmorContents(inventoryHandler.loadArmor(player));
            inventoryHandler.removeInventoryAndArmor(player);
        }
    }
}
