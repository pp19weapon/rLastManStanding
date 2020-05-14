package server.hyriftcraft.lms.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import server.hyriftcraft.lms.Main;
import server.hyriftcraft.lms.lms.LMSGame;
import server.hyriftcraft.lms.lms.LMSHandler;
import server.hyriftcraft.lms.utils.ConfigManager;
import server.hyriftcraft.lms.utils.PermissionHandler;

public class LMSCommand implements CommandExecutor {
    Plugin plugin;
    LMSHandler lmsHandler = LMSHandler.getInstance();
    PermissionHandler permissionHandler = PermissionHandler.getInstance();
    ConfigManager configManager = ConfigManager.getInstance();

    public LMSCommand(Plugin plg){
        plugin = plg;
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof ConsoleCommandSender){
            if (command.getLabel().equalsIgnoreCase("lastmanstanding")) {
                if (args.length == 0) {
                    //Print help
                }
                if (args[0].equalsIgnoreCase("start")) {
                    if (args.length == 2) {
                        int id = Integer.parseInt(args[1]);
                        if (lmsHandler.startLMSGame(id)) {
                            Main.LOGGER.info(ChatColor.GOLD + "[rLastManStanding] You started LMS game with id " + id);
                            return true;
                        } else {
                            Main.LOGGER.info(ChatColor.RED + "[rLastManStanding] Could not start LMS game with id " + id + ". Is a game already started?");
                        }
                    } else {
                        Main.LOGGER.info(ChatColor.RED + "[rLastManStanding] Please only enter the ID of the game!");
                    }

                }
            }


            return false;
        }

        Player player = (Player) commandSender;



        if (command.getLabel().equalsIgnoreCase("lastmanstanding")) {
            if (args.length == 0){
                //Print help menu
            }

            if (args[0].equalsIgnoreCase("setspawn")){
                if (permissionHandler.hasPerms(player, "lms.admin")) {
                    if (args.length == 2) {
                        int id = Integer.parseInt(args[1]);
                        LMSGame lmsGame = lmsHandler.getLMSGame(id);

                        if (lmsGame != null) {
                            if (lmsGame.addSpawnLocation(player.getLocation(), player)){
                                player.sendMessage("[Last Man Standing] Created spawnpoint at " + player.getLocation().toString());
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Please only enter the ID of the game!");
                    }
                }

            } else if (args[0].equalsIgnoreCase("create")) {
                if (permissionHandler.hasPerms(player, "lms.admin")) {
                    int id = lmsHandler.registerNewLMSGame(plugin);
                    player.sendMessage(ChatColor.RED + "[Last Man Standing] Created with id " + ChatColor.GOLD + id);
                }

            } else if (args[0].equalsIgnoreCase("start")) {
                if (permissionHandler.hasPerms(player, "lms.admin")) {
                    if (args.length == 2){
                        int id = Integer.parseInt(args[1]);
                        if (lmsHandler.startLMSGame(id)){
                            player.sendMessage(ChatColor.GOLD + "You started LMS game with id " + id);
                        } else {
                            player.sendMessage(ChatColor.RED + "Could not start LMS game with id " + id + ". Is a game already started?");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Please only enter the ID of the game!");
                    }
                }

            } else if (args[0].equalsIgnoreCase("remove")) {
                if (permissionHandler.hasPerms(player, "lms.admin")) {
                    if (args.length == 2) {
                        int id = Integer.parseInt(args[1]);
                        lmsHandler.removeLMSGame(id);
                    } else {
                        player.sendMessage(ChatColor.RED + "Please only enter the ID of the game!");
                    }
                }

            } else if (args[0].equalsIgnoreCase("join")) {
                if (permissionHandler.hasPerms(player, "lms.basic")) {
                    if (lmsHandler.isAGameRunning()) {
                        LMSGame lmsGame = lmsHandler.getRunningGame();
                        if (lmsGame.joinPlayer(player)) {
                            player.sendMessage(configManager.getString(configManager.getConfig("config.yml"), "broadcast.join", player));
                            return true;
                        }
                    }
                    //player.sendMessage(configManager.getString(configManager.getConfig("config.yml"), "broadcast.failed_join", player));
                }
            }
        }

        return true;
    }


}
