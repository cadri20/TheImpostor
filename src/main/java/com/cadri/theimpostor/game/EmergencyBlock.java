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
package com.cadri.theimpostor.game;

import com.cadri.theimpostor.LanguageManager;
import com.cadri.theimpostor.MessageKey;
import com.cadri.theimpostor.TheImpostor;
import com.cadri.theimpostor.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author cadri
 */
public class EmergencyBlock {
    private Block block;
    private boolean enabled;
    private BukkitTask timer;
    private Arena arena;
    private int counter;

    public EmergencyBlock(Block block, Arena arena) {
        this.block = block;
        this.arena = arena;
        this.enabled = false;
    }

    public Block getBlock() {
        return block;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public void enableIn(int seconds){
        counter = seconds;
        timer = Bukkit.getScheduler().runTaskTimer(TheImpostor.plugin, () -> {
            if(counter != 0){
                counter--;
            }else{
                stopTimer();
                setEnabled(true);
            }
        }, 10L, 20L);
    }
    
    public void stopTimer(){
        if(timer != null)
            timer.cancel();
    }
    
    public boolean isEnabled(){
        return enabled;
    }
    
    public int getEnableCount(){
        return counter;
    }
}
