package com.me.exendv2.vasilityenchantsrecoded;

import com.me.exendv2.vasilityenchantsrecoded.commands.mainCommand;
import com.me.exendv2.vasilityenchantsrecoded.listeners.blockListener;
import com.me.exendv2.vasilityenchantsrecoded.listeners.closeInventoryListener;
import com.me.exendv2.vasilityenchantsrecoded.listeners.enchantListener;
import com.me.exendv2.vasilityenchantsrecoded.listeners.mainClickListener;
import com.me.exendv2.vasilityenchantsrecoded.utils.GUIManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class VasilityEnchants extends JavaPlugin {

    public static VasilityEnchants instance;
    public static Economy econ = null;

    @Override
    public void onEnable() {

        // Checks if there is a vault plugin to support the economy
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cCouldn't find vault plugin. Disabling the Plugin..."));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();
        setupEconomy();

        // Checks if there are any economy provider plugin otherwise the plugin will be disabled and demand for an economy plugin
        if (econ == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cCouldn't find any economy provider plugin. Disabling the Plugin..."));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        instance = this;
        getServer().getPluginCommand("vasilityenchants").setExecutor(new mainCommand());
        getServer().getPluginManager().registerEvents(new mainClickListener(), this);
        getServer().getPluginManager().registerEvents(new closeInventoryListener(), this);
        getServer().getPluginManager().registerEvents(new enchantListener(), this);
        getServer().getPluginManager().registerEvents(new blockListener(), this);
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Loop all players
        for (Player p : Bukkit.getOnlinePlayers()) {
            // Checks if opened inventory of player is the Enchant GUI
            if (p.getOpenInventory().getTopInventory() == GUIManager.MainGUI){
                // Checks if the item in Enchant GUI and Slot 13 isn't a barrier
                if (!(p.getOpenInventory().getTopInventory().getItem(13).getType() == Material.BARRIER)) {
                    // Gives the item in slot 13
                    p.getInventory().addItem(p.getOpenInventory().getItem(13));
                }
                // Closes Enchant GUI
                p.closeInventory();
                p.sendMessage(ChatColor.translateAlternateColorCodes('&' , getConfig().getString("PREFIX")) + "GUI has been closed due to plugin deactivation.");
            }
        }
    }
        // Plugin shutdown logic

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        econ = rsp.getProvider();


    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

}
