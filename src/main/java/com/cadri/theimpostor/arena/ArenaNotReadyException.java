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
package com.cadri.theimpostor.arena;

import com.cadri.theimpostor.LanguageManager;
import com.cadri.theimpostor.MessageKey;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cadri
 */
public class ArenaNotReadyException extends RuntimeException{
    private final int remainingSpawns;
    private final int remainingTasks;
    private final boolean areSabotagesSet;
    private final boolean isEmergencyBlockSet;
    private final boolean areTasksNumberSet;

    public ArenaNotReadyException(Arena arena){
        this.remainingSpawns = arena.getMaxPlayers() - arena.getPlayerSpawnPoints().size();
        this.remainingTasks = arena.getMaxPlayers() - arena.getTasks().size();
        this.areSabotagesSet = !arena.getSabotages().isEmpty();
        this.isEmergencyBlockSet = arena.isEmergencyMeetingBlockSet();
        this.areTasksNumberSet = arena.getPlayerTasksNumber() > 0;
    }
    public int getRemainingTasksNumber(){
        return remainingTasks;
    }
    
    public int getRemainingSpawnsNumber(){
        return remainingSpawns;
    }
    
    public String[] getCauses(){
        List<String> causes = new ArrayList<>();
     
        if(remainingSpawns != 0)
            causes.add(LanguageManager.getTranslation(MessageKey.REMAINING_SPAWNS, remainingSpawns));
        if(remainingTasks != 0)
            causes.add(LanguageManager.getTranslation(MessageKey.REMAINING_TASKS, remainingTasks));
        if(!areSabotagesSet)
            causes.add(LanguageManager.getTranslation(MessageKey.SABOTAGES_NOT_SET));
        if(!isEmergencyBlockSet)
            causes.add(LanguageManager.getTranslation(MessageKey.EMERGENCY_MEETING_BLOCK_NOT_SET));
        if(!areTasksNumberSet)
            causes.add(LanguageManager.getTranslation(MessageKey.TASKS_NUMBER_NOT_SET));
        return causes.toArray(new String[0]);
    }
}
