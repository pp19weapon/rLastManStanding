package server.hyriftcraft.lms.utils;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;

public class PermissionHandler {
    private static  PermissionHandler single_inst = null;

    private Permission permission;

    private PermissionHandler(){
    }

    public static PermissionHandler getInstance(){
        if (single_inst == null){
            single_inst = new PermissionHandler();
        }

        return single_inst;
    }

    public void setPermission(Permission perm){
        permission = perm;
    }

    public Permission getPermission() {
        return permission;
    }

    public boolean hasPerms(Player player, String perm){
        if (permission.has(player, perm)){
            return true;
        } else {
            player.sendMessage("You don't have permission!");
            return false;
        }
    }
}


