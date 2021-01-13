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
            YamlConfiguration fc = YamlConfiguration.loadConfiguration(arenaFile);
            String name = fc.getString("name");
            int minPlayers = fc.getInt("minPlayers");
            int maxPlayers = fc.getInt("maxPlayers");

            Location lobby = Serializer.getLocation(fc.getString("lobby_location"));

            List<Location> playerSpawnPoints = new ArrayList<>();
            for (String stringLocation : fc.getStringList("player_spawn_points")) {
                playerSpawnPoints.add(Serializer.getLocation(stringLocation));
            }

            List<CrewTask> tasksList = new ArrayList<>();

            ConfigurationSection tasksSection = fc.getConfigurationSection("tasks");
            if (tasksSection != null) {
                for (String taskName : tasksSection.getKeys(false)) {
                    String taskPath = "tasks." + taskName + ".";

                    Location taskLoc = Serializer.getLocation(fc.getString(taskPath + "location"));

                    int time = fc.getInt("tasks." + taskName + ".duration");
                    CrewTask task = new CrewTask(taskName, taskLoc, time);
                    tasksList.add(task);
                }
            }

            List<SabotageComponent> sabotagesList = new ArrayList<>();
            ConfigurationSection sabotagesSection = fc.getConfigurationSection("sabotages");
            if (sabotagesSection != null) {
                for (String sabotageName : sabotagesSection.getKeys(false)) {
                    String sabotagePath = "sabotages." + sabotageName;

                    Location blockLoc = Serializer.getLocation(fc.getString(sabotagePath + ".block_location"));
                    Block sabotageBlock = blockLoc.getBlock();
                    int sabotageTime = fc.getInt(sabotagePath + ".cooldown");
                    sabotagesList.add(new SabotageComponent(sabotageName, sabotageBlock, sabotageTime));
                }

            }
            Block emergencyMeetingBlock = null;
            if (fc.get("emergency_meeting_block_location") != null) {
                Location blockLocation = Serializer.getLocation(fc.getString("emergency_meeting_block_location"));
                emergencyMeetingBlock = blockLocation.getBlock();
            }

            int impostors = fc.getInt("impostors");
            int discussionTime = fc.getInt("discussion_time");
            int votingTime = fc.getInt("voting_time");
            int killCooldown = fc.getInt("kill_cooldown");
            int sabotageCooldown = fc.getInt("sabotage_cooldown");
            Arena a = new Arena(name, maxPlayers, minPlayers, impostors, discussionTime, votingTime, killCooldown, sabotageCooldown, lobby, playerSpawnPoints, tasksList, sabotagesList, emergencyMeetingBlock, fc.getBoolean("enabled"), fc.getInt("player_tasks_number"));
            ArenaManager.arenas.add(a);
           
            TheImpostor.plugin.getLogger().log(Level.INFO, "The arena " + name + " has been loaded");
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
