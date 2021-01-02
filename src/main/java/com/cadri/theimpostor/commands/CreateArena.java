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
package com.cadri.theimpostor.commands;

import com.cadri.theimpostor.LanguageManager;
import com.cadri.theimpostor.MessageKeys;
import com.cadri.theimpostor.TheImpostor;
import com.cadri.theimpostor.arena.Arena;
import com.cadri.theimpostor.arena.ArenaManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author cadri
 */
public class CreateArena implements SubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!ArenaManager.arenaNames.contains(args[0])) {
                Arena arena = new Arena(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), player.getLocation());
                ArenaManager.arenas.add(arena);
                ArenaManager.arenaNames.add(arena.getName());
                File arenaNamesFile = new File(TheImpostor.plugin.getDataFolder() + File.separator + "arenas.yml");
                YamlConfiguration fcn = YamlConfiguration.loadConfiguration(arenaNamesFile);
                fcn.set("arena-list", ArenaManager.arenaNames);                
                try {
                    arena.saveConfig();
                    fcn.save(arenaNamesFile);
                } catch (IOException e) {
                    player.sendMessage("Error saving the files");

                }
                player.sendMessage(LanguageManager.getTranslation(MessageKeys.ARENA_CREATED_SUCCESSFULLY.key));
            } else {
                player.sendMessage(LanguageManager.getTranslation(MessageKeys.ARENA_ALREADY_EXISTS.key));
            }
        }

    }
}
