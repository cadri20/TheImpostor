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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author cadri
 */
public enum PlayerColor {
    RED("Red",ChatColor.RED,Material.RED_WOOL),
    BLUE("Blue",ChatColor.BLUE,Material.BLUE_WOOL);
    
    private String name;
    private ChatColor chatColor;
    private Material material;
    
    private PlayerColor(String name,ChatColor chatColor, Material material) {
        this.name = name;
        this.chatColor = chatColor;
        this.material = material;
    }
    
    public ItemStack getItem(){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(chatColor + name);
        item.setItemMeta(meta);
        
        return item;
    }
    
    public static PlayerColor getPlayerColor(ItemStack item){
        for(PlayerColor color: values()){
            if(item.equals(color.getItem()))
                return color;
        }
        
        return null;
    }

    public String getName() {
        return name;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }
    
    public Material getMaterial(){
        return material;
    }
}

