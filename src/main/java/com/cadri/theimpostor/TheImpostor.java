package com.cadri.theimpostor;

import com.cadri.theimpostor.arena.ArenaManager;
import com.cadri.theimpostor.commands.CommandManager;
import com.cadri.theimpostor.events.ArenaEvents;
import com.cadri.theimpostor.events.ChatEvents;
import com.cadri.theimpostor.events.ServerEvents;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author cadri
 */
public class TheImpostor extends JavaPlugin{
    public static TheImpostor plugin;
    public static String pluginTitle = ChatColor.translateAlternateColorCodes('&', "&6&lTheImpostor &9&lby cadri1");
    
    public TheImpostor() {
        plugin = this;
    }
    
    
    @Override
    public void onEnable() {
        try{
            Class.forName("org.golde.bukkit.corpsereborn.CorpseAPI.CorpseAPI");
        }catch(ClassNotFoundException e){
            getLogger().log(Level.SEVERE, "CorpseReborn not found, this plugin need it to work. Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        saveDefaultConfig();
   
        getCommand(CommandManager.mainCommand).setExecutor(new CommandManager());
        getCommand(CommandManager.mainCommand).setTabCompleter(new CommandManager());
        CommandManager.loadCommands();
        LanguageManager.loadLanguage();
        ArenaManager.loadArenas();
        
        this.getServer().getPluginManager().registerEvents(new ArenaEvents(), plugin);
        this.getServer().getPluginManager().registerEvents(new ChatEvents(), plugin);
        this.getServer().getPluginManager().registerEvents(new ServerEvents(), plugin);
        this.getLogger().log(Level.INFO, "The plugin has been Enabled");
        
        
    }

    @Override
    public void onDisable(){
        this.getLogger().log(Level.INFO, "The plugin has been Disabled");
        ArenaManager.removePlayersFromArenas();
    }

}
