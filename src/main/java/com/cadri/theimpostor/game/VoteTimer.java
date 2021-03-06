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
public class VoteTimer extends BukkitRunnable{
    int counter;
    Arena arena;

    public VoteTimer(int counter, Arena arena) {
        this.counter = counter;
        this.arena = arena;
    }
    
    @Override
    public void run() {
        String votingEndingTitle = LanguageManager.getTranslation(MessageKey.VOTING_ENDS_IN);
        if(counter > 0){          
            arena.getBoard().put(votingEndingTitle, counter);
            
            if(counter < 4){
                String votingFinishMessage = LanguageManager.getTranslation(MessageKey.VOTE_FINISH_TIME, counter);
                for(Player player: arena.getPlayers()){
                    player.sendMessage(votingFinishMessage);
                }
            }
            counter--;
        }else{
            arena.getBoard().remove(votingEndingTitle);
            arena.stopVote();
            this.cancel();
        }        
    }
    
}
