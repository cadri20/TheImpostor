/*
 * Copyright (C) 2020 cadri
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cadri.theimpostor.arena;

import com.cadri.theimpostor.TheImpostor;
import com.cadri.theimpostor.game.CrewTask;
import com.cadri.theimpostor.game.SabotageComponent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author cadri
 */
public class ArenaManager {

    public static File arenasDirectory = new File(TheImpostor.plugin.getDataFolder(), "arenas");
    public static YamlConfiguration fc = YamlConfiguration.loadConfiguration(arenasDirectory);
    public static List<Arena> arenas = new ArrayList<>();

    public static void loadArenas() {
        
        if(!arenasDirectory.exists())
            return;
        
        for (File arenaFile : arenasDirectory.listFiles()) {
            Arena arena = new Arena(arenaFile);
            arenas.add(arena);
           
            TheImpostor.plugin.getLogger().log(Level.INFO, "The arena " + arena.getName() + " has been loaded");
        }
    }

    public static Arena getArena(String arena) {
        for (Arena a : arenas) {
            if(a.getName() == null){
                TheImpostor.plugin.getLogger().log(Level.SEVERE, "Arena name is null");
                return null;
            }
            if (a.getName().equals(arena)) {
                return a;
            }
        }
        return null;
    }

    public static Arena getArenaPlayer(Player player) {
        for (Arena arena : arenas) {
            if (arena.getPlayers().contains(player)) {
                return arena;
            }
        }
        return null;
    }
    
    public static void removePlayersFromArenas(){
        for(Arena arena: arenas){
            arena.removeAllPlayers();
        }
    }
    
    public static List<String> getArenaNames(){
        List<String> arenaNames = new ArrayList<>();
        for(Arena arena: arenas){
            arenaNames.add(arena.getName());
        }
        
        return arenaNames;
    }
}
