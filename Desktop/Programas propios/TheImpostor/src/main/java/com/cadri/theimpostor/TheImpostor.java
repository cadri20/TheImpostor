package com.cadri.theimpostor;

import com.cadri.theimpostor.arena.Arena;
import com.cadri.theimpostor.arena.ArenaManager;
import com.cadri.theimpostor.commands.BaseCommand;
import com.cadri.theimpostor.commands.CreateArena;
import com.cadri.theimpostor.commands.JoinArena;
import java.io.File;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
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
        this.getCommand(BaseCommand.mainCommand).setExecutor(new BaseCommand());
        BaseCommand.loadCommands();
        LanguageManager.loadLanguage();
        ArenaManager.loadArenas();
        
    }

    @Override
    public void onDisable(){
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "The plugin has been Disabled");
    }

}
