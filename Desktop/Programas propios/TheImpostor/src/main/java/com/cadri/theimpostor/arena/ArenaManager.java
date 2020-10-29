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
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author cadri
 */
public class ArenaManager {

    public static File arenaNamesFile = new File(TheImpostor.plugin.getDataFolder(), "arenas.yml");
    public static YamlConfiguration fc = YamlConfiguration.loadConfiguration(arenaNamesFile);
    public static List<Arena> arenas = new ArrayList<>();
    public static List<String> arenaNames = fc.getStringList("arena-list");

    public static void loadArenas() {
        for (String s : ArenaManager.arenaNames) {
            File arena = new File(TheImpostor.plugin.getDataFolder() + File.separator + s + File.separator + "arena_settings.yml");
            YamlConfiguration fc = YamlConfiguration.loadConfiguration(arena);
            String Name = fc.getString("Name");
            int minPlayers = fc.getInt("minPlayers");
            int maxPlayers = fc.getInt("maxPlayers");
            if (fc.getString("Lobby") == null) {
                Arena a = new Arena(Name, null);
                ArenaManager.arenas.add(a);
                TheImpostor.plugin.getLogger().log(Level.INFO, "La arena " + s + " no tiene lobby");
            } else {
                String World = fc.getString("Lobby" + ".world");
                World lobbyWorld = Bukkit.getWorld(World);
                Double x = fc.getDouble("Lobby" + ".x");
                Double y = fc.getDouble("Lobby" + ".y");
                Double z = fc.getDouble("Lobby" + ".z");
                Location lobby = new Location(lobbyWorld, x, y, z);
                
                World spawnWorld = Bukkit.getWorld(fc.getString("spawn.world"));
                Double spawnX = fc.getDouble("spawn.x");
                Double spawnY = fc.getDouble("spawn.y");
                Double spawnZ = fc.getDouble("spawn.z");
                
                Location spawn = new Location(spawnWorld, spawnX, spawnY, spawnZ);
                Arena a = new Arena(Name, maxPlayers, minPlayers, lobby, spawn);
                ArenaManager.arenas.add(a);

            }

            TheImpostor.plugin.getLogger().log(Level.INFO, "La arena" + s + "ha sido cargada");
        }
    }

    public static Arena getArena(String arena) {
        for (Arena a : arenas) {
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

}
