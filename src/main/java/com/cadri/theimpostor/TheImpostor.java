package com.cadri.theimpostor;

import com.cadri.theimpostor.arena.ArenaManager;
import com.cadri.theimpostor.commands.CommandManager;
import com.cadri.theimpostor.events.ArenaEvents;
import com.cadri.theimpostor.events.ChatEvents;
import com.cadri.theimpostor.events.ServerEvents;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author cadri
 */
public class TheImpostor extends JavaPlugin{
    public static TheImpostor plugin;

    public TheImpostor() {
        plugin = this;
    }
    
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "The plugin has been Enabled");
        this.getCommand(CommandManager.mainCommand).setExecutor(new CommandManager());
        CommandManager.loadCommands();
        LanguageManager.loadLanguage();
        ArenaManager.loadArenas();
        
        this.getServer().getPluginManager().registerEvents(new ArenaEvents(), plugin);
        this.getServer().getPluginManager().registerEvents(new ChatEvents(), plugin);
        this.getServer().getPluginManager().registerEvents(new ServerEvents(), plugin);
    }

    @Override
    public void onDisable(){
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "The plugin has been Disabled");
    }

}
