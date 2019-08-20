package net.momentonetwork.listeners;

import net.momentonetwork.MineableSpawners;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class BlockBreakListener implements Listener {
    // Make a class variable plugin with main class instance
    private MineableSpawners plugin;
    // Class config variable
    private FileConfiguration config;

    // Constructor
    public BlockBreakListener(MineableSpawners plugin) {
        this.plugin = plugin;
        this.config = this.plugin.getConfigInstance();
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onSpawnerMine(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();
        Material material = block.getType();

        if (material == Material.SPAWNER && !e.isCancelled()) {
            CreatureSpawner spawner = (CreatureSpawner) block.getState();

            if(player.hasPermission("mineablespawners.break") || !config.getBoolean("require_permission.spawner_break")) {

                if (checkLore(player.getInventory().getItemInMainHand()) || player.hasPermission("mineablespawners.nosilk") || !config.getBoolean("require_permission.no_silk")) {
                    // Cancel exp drop
                    e.setExpToDrop(0);
                    // Bring block straight to inv
                    if (player.getInventory().firstEmpty() != -1) {
                        ItemStack item = new ItemStack(material);
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(Collections.singletonList(ChatColor.YELLOW + "type: " + ChatColor.GRAY + spawner.getSpawnedType().toString().toLowerCase()));
                        item.setItemMeta(meta);
                        player.getInventory().addItem(item);
                        block.getDrops().clear();
                    } else {
                        player.sendMessage(ChatColor.RED + "Please clear up some inventory space before mining a spawner!");
                        e.setCancelled(true);
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "Sorry, you do not have permission to do this!");
                e.setCancelled(true);
            }
        }
    }

    private boolean checkLore(ItemStack handItem) {
        if (handItem == null)
            return false;

        List<String> lore = handItem.getItemMeta().getLore();

        if (lore == null || lore.isEmpty())
            return false;

        for(String loretext : lore) {
            if (loretext.toLowerCase().contains("spawner miner"))
                return true;
        }

        return false;
    }
}
