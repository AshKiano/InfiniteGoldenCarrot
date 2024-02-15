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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class InfiniteGoldenCarrot extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        this.getCommand("infinitegoldencarrot").setExecutor(new GoldenCarrotCommand());
        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        Metrics metrics = new Metrics(this, 19577);

        this.getLogger().info("Thank you for using the InfiniteGoldenCarrot plugin! If you enjoy using this plugin, please consider making a donation to support the development. You can donate at: https://donate.ashkiano.com");

        checkForUpdates();
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

    private void checkForUpdates() {
        try {
            String pluginName = this.getDescription().getName();
            URL url = new URL("https://www.ashkiano.com/version_check.php?plugin=" + pluginName);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();
                JSONObject jsonObject = new JSONObject(jsonResponse);
                if (jsonObject.has("error")) {
                    this.getLogger().warning("Error when checking for updates: " + jsonObject.getString("error"));
                } else {
                    String latestVersion = jsonObject.getString("latest_version");

                    String currentVersion = this.getDescription().getVersion();
                    if (currentVersion.equals(latestVersion)) {
                        this.getLogger().info("This plugin is up to date!");
                    } else {
                        this.getLogger().warning("There is a newer version (" + latestVersion + ") available! Please update!");
                    }
                }
            } else {
                this.getLogger().warning("Failed to check for updates. Response code: " + responseCode);
            }
        } catch (Exception e) {
            this.getLogger().warning("Failed to check for updates. Error: " + e.getMessage());
        }
    }
}