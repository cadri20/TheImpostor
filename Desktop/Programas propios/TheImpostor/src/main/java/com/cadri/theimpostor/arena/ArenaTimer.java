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

import com.cadri.theimpostor.LanguageManager;
import com.cadri.theimpostor.MessageKeys;
import com.cadri.theimpostor.TheImpostor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author cadri
 */
public class ArenaTimer extends BukkitRunnable{
    private int counter;
    private Arena arena;
    
    public ArenaTimer(int time, Arena arena) {
        this.counter = time;
        this.arena = arena;
    }

    @Override
    public void run() {
        if(counter > 0){
            counter--;
            for(Player player: arena.getPlayers()){
                player.setLevel(counter);
            }
            if(counter < 4){
                for(Player player: arena.getPlayers())
                    player.sendMessage(LanguageManager.getTranslation(MessageKeys.ARENA_COUNTDOWN.key) + " " + counter);
            }
                
        }
        else{
            for(Player player: arena.getPlayers())
                player.sendMessage(LanguageManager.getTranslation(MessageKeys.ARENA_GAME_START.key));
            this.cancel();
        }
    }
}
