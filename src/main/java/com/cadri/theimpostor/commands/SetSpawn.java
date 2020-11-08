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
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author cadri
 */
public class SetSpawn implements SubCommand{

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        String arenaName = args[0];
        
        if(sender instanceof Player){
            Player player = (Player) sender;
            Arena arenaFound = null;
            
            for(Arena arena: ArenaManager.arenas){
                if(arena == null){
                    TheImpostor.plugin.getLogger().log(Level.SEVERE,"Arenas contains a null!");
                    return; 
                }
                if(arena.getName() == null){
                    TheImpostor.plugin.getLogger().log(Level.SEVERE,"One arena name is null");
                    return;
                }
                if(arena.getName().equals(arenaName))
                    arenaFound = arena;
            }
            if(arenaFound != null){
                arenaFound.setSpawn(player.getLocation());
                
                player.sendMessage(LanguageManager.getTranslation(MessageKeys.ARENA_SPAWN_SET.key));
            }else{
                player.sendMessage(ChatColor.RED + "La arena no existe");
            }
        }
    }
    
}