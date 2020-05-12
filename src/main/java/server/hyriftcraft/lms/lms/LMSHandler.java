package server.hyriftcraft.lms.lms;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import server.hyriftcraft.lms.Main;
import server.hyriftcraft.lms.utils.ConfigManager;

import java.util.ArrayList;

public class LMSHandler {
    private static LMSHandler single_inst = null;

    private ArrayList<LMSGame> registeredLMSGames = new ArrayList<LMSGame>();
    ConfigManager configManager = ConfigManager.getInstance();

    private int gameID = 0;

    private Player lastWinner = null;

    public Player getLastWinner() {
        return lastWinner;
    }

    public void setLastWinner(Player lastWinner) {
        this.lastWinner = lastWinner;
    }

    private LMSHandler() {
    }

    public void autoRegisterAlreadyDefinedGames(Plugin plg){
        if (configManager.getConfig("config.yml").contains(String.format("%d", gameID))){
            Main.LOGGER.info("[rLastManStanding] Registering already existing event found in config!");
            registerNewLMSGame(plg);
            autoRegisterAlreadyDefinedGames(plg);
        }
    }


    //Returns the ID of the new game that it's registered with
    public int registerNewLMSGame(Plugin plg) {
        LMSGame game = new LMSGame(plg, gameID);
        plg.getServer().getPluginManager().registerEvents(game, plg);
        registeredLMSGames.add(game);
        gameID++;
        Main.LOGGER.info("[rLastManStanding] Registered LMS game with id " + (gameID - 1) + ". Don't forget to configure it!");
        return gameID - 1;
    }

    public LMSGame getLMSGame(int id){
        if (registeredLMSGames.size() > id){
            return registeredLMSGames.get(id);
        } else {
            Main.LOGGER.severe("[rLastManStanding] Couldn't get game with ID, is it registered?");
            return null;
        }
    }

    public boolean startLMSGame(int id){
        if (registeredLMSGames.size() > id){
            if (!isAGameRunning()){
                getLMSGame(id).start();
                return true;
            }
            Main.LOGGER.warning("[rLastManStanding] A game is already started!");
        }
        return false;
    }

    public void removeLMSGame(int id){
        if (registeredLMSGames.size() > id){
            registeredLMSGames.get(id).stop();
            registeredLMSGames.remove(id);
            registeredLMSGames.trimToSize();
        } else {
            Main.LOGGER.severe("[rLastManStanding] Couldn't get game with ID, is it registered?");
        }
    }

    public boolean isAGameRunning(){
        for (LMSGame lmsGame : registeredLMSGames){
            if (lmsGame.isRunning()){
                return true;
            }
        }
        return false;
    }

    public LMSGame getRunningGame(){
        for (LMSGame lmsGame : registeredLMSGames){
            if (lmsGame.isRunning()){
                return lmsGame;
            }
        }
        return null;
    }

    public static LMSHandler getInstance() {
        if (single_inst == null) {
            single_inst = new LMSHandler();
        }
        return single_inst;
    }
}