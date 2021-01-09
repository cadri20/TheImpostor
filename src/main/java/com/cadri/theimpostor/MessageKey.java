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
package com.cadri.theimpostor;

/**
 *
 * @author cadri
 */
public enum MessageKey {
    ARENA_CREATED_SUCCESSFULLY("arena_created_successfully"),
    ARENA_ALREADY_EXISTS("arena_already_exists"),
    ARENA_READY("arena_ready"),    
    ARENA_DOESNT_EXIST("arena_doesnt_exist"),
    ARENA_COUNTDOWN("arena_countdown"),
    ARENA_GAME_START("arena_game_start"),
    ARENA_SPAWN_ADDED("arena_spawn_added"),
    ARENA_ENABLED("arena_enabled"),
    ARENA_NOT_ENABLED("arena_not_enabled"),
    ARENA_NOT_READY("arena_not_ready"),
    ALL_SPAWNS_SETTED("all_spawns_setted"),
    
    PLAYER_IN_ARENA("player_in_arena"),
    PLAYER_NOT_IN_ARENA("player_not_in_arena"),
    PLAYER_VOTE_FOR("player_vote_for"),
    IMPOSTOR_TITLE("impostor_title"),
    IMPOSTOR_SUBTITLE("impostor_subtitle"),
    CREWMATE_TITLE("crewmate_title"),
    CREWMATE_SUBTITLE("crewmate_subtitle"),
    
    TASK_CREATED("task_created"),
    TASK_COMPLETED("task_completed"),
    TASK_PROGRESS("task_progress"),
    TASK_PROGRESS_BAR("task_progress_bar"),
    TASK_SAME_LOCATION("task_same_location"),
    TASKS_NUMBER_NOT_SET("tasks_number_not_set"),
    VOTE("vote"),
    VOTES("votes"),
    SKIP_VOTE("skip_vote"),
    VOTE_START_TIME("vote_start_time"),
    VOTE_FINISH_TIME("vote_finish_time"),
    VOTE_STARTED("vote_started"),
    
    DEAD_BODY_REPORTED("dead_body_reported"),
    PLAYER_REPORT_CORPSE("player_report_corpse"),
    
    EMERGENCY_MEETING_START("emergency_meeting_start"),
    EMERGENCY_MEETING_TITLE("emergency_meeting_title"),
    EMERGENCY_MEETING_SUBTITLE("emergency_meeting_subtitle"),
    EMERGENCY_MEETING_BLOCK_CREATED("emergency_meeting_block_created"),
    EMERGENCY_MEETING_BLOCK_PROBLEM("emergency_meeting_block_problem"),
    EMERGENCY_MEETING_BLOCK_NOT_SET("emergency_meeting_block_not_set"),
    
    IMPOSTOR_ABLE_TO_KILL("impostor_able_to_kill"),
    NOBODY_EJECTED("nobody_ejected"),
    
    WINNERS("winners"),
    DEFEAT("defeat"),
    WINNERS_LIST_TITLE("winners_list_title"),
    IMPOSTORS("impostors"),
    CREW("crew"),
    
    CREWMATE_EJECTED_MESSAGE("crewmate_ejected_message"),
    IMPOSTOR_EJECTED_MESSAGE("impostor_ejected_message"),
    
    DEADCHAT_PREFIX("deadchat_prefix"),
    
    REMAINING_SPAWNS("remaining_spawns"),
    REMAINING_TASKS("remaining_tasks"),
    
    PLAYERS_NUMBER("players_number"),
    ARENA("arena"),
    MIN_PLAYERS("min_players"),
    MAX_PLAYERS("max_players"),
    
    INVALID_TASKS_NUMBER("invalid_tasks_number"),
    TASKS_NUMBER_SETTED("tasks_number_setted"),
    
    SABOTAGE_CREATED("sabotage_created"),
    SABOTAGE_GUI_TITLE("sabotage_gui_title"),
    SABOTAGE_ADVERTISEMENT("sabotage_advertisement"),
    SABOTAGE_FIXED("sabotage_fixed"),
    SABOTAGE_BOARD_ADVERTISEMENT("sabotage_board_advertisement"),
    CANT_SABOTAGE("cant_sabotage"),
    SABOTAGES_NOT_SET("sabotages_not_set");
    private String key;
    
    private MessageKey(String key){
        this.key = key;
    }
    
    @Override
    public String toString(){
        return key;
    }
}
