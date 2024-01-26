package com.ashkiano.infinitegoldencarrot;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class InfiniteGoldenCarrot extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        this.getCommand("infinitegoldencarrot").setExecutor(new GoldenCarrotCommand());
        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        Metrics metrics = new Metrics(this, 19577);

        this.getLogger().info("Thank you for using the InfiniteGoldenCarrot plugin! If you enjoy using this plugin, please consider making a donation to support the development. You can donate at: https://donate.ashkiano.com");

    }

    public class GoldenCarrotCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                ItemStack carrot = new ItemStack(Material.GOLDEN_CARROT);
                ItemMeta meta = carrot.getItemMeta();

                List<String> lore = new ArrayList<>();
                lore.add("InfiniteGoldenCarrot");

                if (meta != null) {
                    meta.setLore(lore);
                    carrot.setItemMeta(meta);
                }

                player.getInventory().addItem(carrot);
                player.sendMessage("You've received an InfiniteGoldenCarrot!");
            }

            return true;
        }
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (item.getType() == Material.GOLDEN_CARROT && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasLore() && meta.getLore().contains("InfiniteGoldenCarrot")) {
                event.setCancelled(true);

                int newFoodLevel = Math.min(player.getFoodLevel() + 6, 20);
                player.setFoodLevel(newFoodLevel);
                float newSaturation = player.getSaturation() + 14.4F;
                player.setSaturation(newSaturation);
            }
        }
    }
}