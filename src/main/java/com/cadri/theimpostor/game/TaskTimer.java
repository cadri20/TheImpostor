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

import com.cadri.theimpostor.LanguageManager;
import com.cadri.theimpostor.MessageKey;
import com.cadri.theimpostor.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author cadri
 */
public class TaskTimer extends BukkitRunnable{
    private int count;
    private CrewTask task;
    private ProgressBar taskProgress;
    private Player player;
    private Arena arena;
    
    public TaskTimer(CrewTask task, Player player, Arena arena) {
        this.count = 0;
        this.player = player;
        this.task = task;
        this.taskProgress = new ProgressBar(task.getTimeToComplete(), 6);
        this.arena = arena;
    }
    
    @Override
    public void run() {
        if(count != task.getTimeToComplete()){
            count++;
            taskProgress.setCurrent(count);
            player.sendTitle(taskProgress.toString(), LanguageManager.getTranslation(MessageKey.TASK_PROGRESS, task.getName()), 0, 22, 0);
        }else{
            task.complete();
            player.sendMessage(LanguageManager.getTranslation(MessageKey.TASK_COMPLETED, task.getName()));
            if(arena.tasksAreCompleted())
                arena.endGame(false);
            this.cancel();
        }
        
        
    }
    
}
