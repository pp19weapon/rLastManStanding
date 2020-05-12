package server.hyriftcraft.lms.utils;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryHandler {
    private static InventoryHandler instance = new InventoryHandler();
    private HashMap<Player, ItemStack[]> inventories;
    private HashMap<Player, ItemStack[]> armors;

    private InventoryHandler() {
        inventories = new HashMap<Player, ItemStack[]>();
        armors = new HashMap<Player, ItemStack[]>();
    }

    public void saveInventoryAndArmor(Player _player) {
        ItemStack[] tmpInv;
        ItemStack[] tmpArmor;
        tmpArmor = _player.getInventory().getArmorContents();
        tmpInv = _player.getInventory().getContents();
        inventories.put(_player, tmpInv);
        armors.put(_player, tmpArmor);
    }

    public ItemStack[] loadInventory(Player _player) {
        return (ItemStack[]) inventories.get(_player);
    }

    public ItemStack[] loadArmor(Player _player) {
        return (ItemStack[]) armors.get(_player);
    }

    public void removeInventoryAndArmor(Player _player) {
        inventories.remove(_player);
        armors.remove(_player);
    }

    public boolean hasInventorySaved(Player _player) {
        return inventories.containsKey(_player);
    }

    public boolean hasArmorSaved(Player _player) {
        return armors.containsKey(_player);
    }

    public static InventoryHandler getInstance() {
        return instance;
    }
}