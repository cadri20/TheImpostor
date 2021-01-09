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

import org.bukkit.entity.Player;

/**
 *
 * @author cadri
 */
public class ArenaUtils {
    public static boolean isInArena(Player player, Arena arena){
        return arena.getPlayers().contains(player);
    }
    
    /**
     * 
     * @param player
     * @return The arena where the player is. If it's not in an arena returns null 
     */
    public static Arena whereArenaIs(Player player){
        for(Arena arena: ArenaManager.arenas){
            if(isInArena(player,arena))
                return arena;
        }
        return null;
    }
    
}
