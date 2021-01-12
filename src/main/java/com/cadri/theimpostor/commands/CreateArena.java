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
import com.cadri.theimpostor.MessageKey;
import com.cadri.theimpostor.TheImpostor;
import com.cadri.theimpostor.arena.Arena;
import com.cadri.theimpostor.arena.ArenaManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
            
            if(args.length != 3){
                sender.sendMessage(LanguageManager.getTranslation(MessageKey.INVALID_ARGUMENTS_NUMBER));
                return;
            }
            String arenaName = args[0];
            if(arenaName.startsWith(" ") || arenaName.isEmpty()){
                sender.sendMessage(LanguageManager.getTranslation(MessageKey.INVALID_ARENA_NAME));
                return;
            }
            int maxPlayers = 0;
            int minPlayers = 0;
            try{
                maxPlayers = Integer.parseInt(args[1]);
                minPlayers = Integer.parseInt(args[2]);
            }catch(NumberFormatException e){
                sender.sendMessage(LanguageManager.getTranslation(MessageKey.ARGUMENT_NOT_NUMBER));
                return;
            }
            Player player = (Player) sender;
            
            if (!ArenaManager.getArenaNames().contains(args[0])) {
                Arena arena = new Arena(arenaName, maxPlayers, minPlayers, player.getLocation());
                ArenaManager.arenas.add(arena);               
                try {
                    arena.saveConfig();
                } catch (IOException e) {
                    player.sendMessage("Error saving the files");

                }
                player.sendMessage(LanguageManager.getTranslation(MessageKey.ARENA_CREATED_SUCCESSFULLY));
            } else {
                player.sendMessage(LanguageManager.getTranslation(MessageKey.ARENA_ALREADY_EXISTS, args[0]));
            }
        }

    }

    @Override
    public List<String> onTabComplete(String[] args) {
        switch (args.length) {
            case 1:
                return Arrays.asList("<arenaname>");
            case 2:
                return Arrays.asList("<maxplayers>");
            case 3:
                return Arrays.asList("<minplayers>");
            default:
                break;
        }
        
        return Collections.emptyList();
    }
}
