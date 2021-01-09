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

import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 *
 * @author cadri
 */
public class GameScoreboard {
    private Scoreboard board;
    private Objective mainObjective;
    private Map<String, Object> map = new HashMap<>();
    private int currentIndex = 0;
    private ChatColor valuesColor;
    //private String spreaderBars = ChatColor.BLACK.toString() + ChatColor.BOLD + "-----------";
    
    public GameScoreboard(String title, ChatColor valuesColor){
        board = Bukkit.getScoreboardManager().getNewScoreboard();
        mainObjective = board.registerNewObjective("Main", "dummy", title);
        mainObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.valuesColor = valuesColor;
        //mainObjective.getScore(spreaderBars).setScore(currentIndex + 1);
    }
    
    public void put(String key, Object value){
        Object previousValue = map.put(key, value);
        if(previousValue == null){
            mainObjective.getScore(scoreFormat(key,value)).setScore(currentIndex);
            currentIndex++;
            //mainObjective.getScore(spreaderBars).setScore(currentIndex);
        }else{
            String score = scoreFormat(key, previousValue);
            int index = mainObjective.getScore(score).getScore();
            board.resetScores(score);
            mainObjective.getScore(scoreFormat(key, value)).setScore(index);
        }
        
    }
    
    public void remove(String key){
        Object valueRemoved = map.remove(key);
        if(valueRemoved != null){
            String score = scoreFormat(key, valueRemoved);
            board.resetScores(score);
        }
        
    }
    
    public Scoreboard getScoreboard(){
        return board;
    }
    
    public String scoreFormat(String key, Object value){
        return String.format("%s: " + valuesColor + "%s", key, value) ;
    }
}
