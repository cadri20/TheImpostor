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
package com.cadri.theimpostor.commands;

import java.util.List;
import org.bukkit.command.CommandSender;

/**
 *
 * @author cadri
 */
public interface SubCommand{
    public void onCommand(CommandSender sender, String[] args);
    public List<String> onTabComplete(String[] args);
    public String getUsage();
    public String getDescription();
    public String getPermission();
}
