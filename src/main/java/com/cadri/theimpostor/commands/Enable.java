/*
 * Copyright (C) 2021 cadri
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
import com.cadri.theimpostor.arena.Arena;
import com.cadri.theimpostor.arena.ArenaManager;
import com.cadri.theimpostor.arena.ArenaNotReadyException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author cadri
 */
public class Enable implements SubCommand{

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            Arena arena = ArenaManager.getArena(args[0]);
            if(arena != null){
                try{
                    arena.enable();
                    player.sendMessage(LanguageManager.getTranslation(MessageKey.ARENA_ENABLED));
                }catch(ArenaNotReadyException e){
                    int remainingSpawns = e.getRemainingSpawnsNumber();
                    int remainingTasks = e.getRemainingTasksNumber();
                    player.sendMessage(LanguageManager.getTranslation(MessageKey.ARENA_NOT_READY));
                    if(remainingSpawns != 0)
                        player.sendMessage(LanguageManager.getTranslation(MessageKey.REMAINING_SPAWNS,remainingSpawns));
                    if(remainingTasks > 0)
                        player.sendMessage(LanguageManager.getTranslation(MessageKey.REMAINING_TASKS, remainingTasks));
                }
            }else{
                player.sendMessage(LanguageManager.getTranslation(MessageKey.ARENA_DOESNT_EXIST, args[0]));
            }
        }
    }
    
}