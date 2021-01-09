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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author cadri
 */
public class SabotageComponent {
    private String name;
    private Block block;
    private boolean isSabotaged;
    private int time;
    private BukkitTask timer;

    public SabotageComponent(String name, Block block, int time) {
        this.name = name;
        this.block = block;
        this.time = time;
        this.isSabotaged = false;
    }

    public Block getBlock() {
        return block;
    }

    public boolean isSabotaged() {
        return isSabotaged;
    }

    public void setIsSabotaged(boolean isSabotaged) {
        this.isSabotaged = isSabotaged;
    }

    public int getTime() {
        return time;
    }

    public String getName() {
        return name;
    }
    
    public ItemStack getItem(){
        Material material;
        if(isSabotaged)
            material = Material.RED_WOOL;
        else
            material = Material.GREEN_WOOL;
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + name);
        item.setItemMeta(meta);
        
        return item;
    }
    
    public void startTimer(Arena arena){
        timer = Bukkit.getScheduler().runTaskTimer(TheImpostor.plugin, new Runnable() {
            int count = time;
            @Override
            public void run() {
                if(count != 0){
                    arena.getBoard().put(LanguageManager.getTranslation(MessageKey.SABOTAGE_BOARD_ADVERTISEMENT, name), count);
                    count--;
                }
                else{
                    arena.endGame(true);
                    stopTimer();
                }
            }
        }, 10L, 20L);
    }

    public void stopTimer(){
        timer.cancel();
    }
    
    public BukkitTask getTaskTimer() {
        return timer;
    }
    
    
}
