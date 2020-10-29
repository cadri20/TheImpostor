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
import com.cadri.theimpostor.arena.Arena;
import com.cadri.theimpostor.arena.ArenaManager;
import com.cadri.theimpostor.arena.ArenaUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author cadri
 */
public class JoinArena implements SubCommand{
    public void onCommand(CommandSender sender, String[] args) {
        
        if (sender instanceof Player) {
            Player player = (Player) sender;
            
            if(ArenaUtils.whereArenaIs(player) != null){
                player.sendMessage("You're in an arena!");
                return;
            }
            if (ArenaManager.arenaNames.contains(args[0])) {
                Arena arena = ArenaManager.getArena(args[0]);
                
                player.teleport(arena.getLobby());
                arena.addPlayer(player);
                
            }else{
                player.sendMessage(LanguageManager.getTranslation(MessageKeys.ARENA_NOT_EXISTS.key  ));
            }
            
            
        }
        
        
            
    }   

}
