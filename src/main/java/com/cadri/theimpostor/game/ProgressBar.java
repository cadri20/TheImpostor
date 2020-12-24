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
package com.cadri.theimpostor.game;

import com.cadri.theimpostor.TheImpostor;
import java.util.logging.Level;
import org.bukkit.ChatColor;

/**
 *
 * @author cadri
 */
public class ProgressBar {
    private int current;
    private int max;
    private int totalBars;
    private char symbol;
    private ChatColor completedColor;
    private ChatColor notCompletedColor;

    public ProgressBar(int max, int totalBars){
        this(max,totalBars, '\u2588', ChatColor.GREEN, ChatColor.RED);
    }
    
    public ProgressBar(int max, int totalBars, char symbol){
        this(max,totalBars,symbol, ChatColor.GREEN, ChatColor.RED);
    }
    public ProgressBar(int max, int totalBars, char symbol, ChatColor completedColor, ChatColor notCompletedColor) {
        this.current = 0;
        this.max = max;
        this.totalBars = totalBars;
        this.symbol = symbol;
        this.completedColor = completedColor;
        this.notCompletedColor = notCompletedColor;
    }

    public void setCurrent(int current){
        this.current = current;
    }
    
    public void increase(){
        current++;
    }
    
    @Override
    public String toString() {
        float percent = (float) current / max;
        int progressBars = (int)(totalBars * percent);
        int leftOver = totalBars - progressBars;
        
        StringBuilder sb = new StringBuilder();
        sb.append(completedColor);
        for(int i = 0; i < progressBars; i++){
            sb.append(symbol);
        }
        sb.append(notCompletedColor);
        for(int i = 0; i < leftOver; i++){
            sb.append(symbol);
        }

        return sb.toString();
    }
    
    
}
