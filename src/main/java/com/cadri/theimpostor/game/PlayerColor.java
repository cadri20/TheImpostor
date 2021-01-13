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
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 *
 * @author cadri
 */
public enum PlayerColor {
    RED(LanguageManager.getTranslation(MessageKey.RED_COLOR), ChatColor.RED, Material.RED_WOOL, Color.RED),
    BLUE(LanguageManager.getTranslation(MessageKey.BLUE_COLOR),ChatColor.BLUE,Material.BLUE_WOOL, Color.BLUE),
    YELLOW(LanguageManager.getTranslation(MessageKey.YELLOW_COLOR), ChatColor.YELLOW,Material.YELLOW_WOOL, Color.YELLOW),
    GREEN(LanguageManager.getTranslation(MessageKey.GREEN_COLOR), ChatColor.GREEN, Material.GREEN_WOOL, Color.GREEN);
    
    private String name;
    private ChatColor chatColor;
    private Material material;
    private ItemStack[] armor;
    
    private PlayerColor(String name,ChatColor chatColor, Material material, Color armorColor) {
        this.name = name;
        this.chatColor = chatColor;
        this.material = material;
        this.armor = new ItemStack[4];
        
        armor[3] = getColoredLeatherArmor(Material.LEATHER_HELMET, armorColor);
        armor[2] = getColoredLeatherArmor(Material.LEATHER_CHESTPLATE, armorColor);
        armor[1] = getColoredLeatherArmor(Material.LEATHER_LEGGINGS, armorColor);
        armor[0] = getColoredLeatherArmor(material.LEATHER_BOOTS, armorColor);
    }
    
    public ItemStack getItem(){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(chatColor + name);
        item.setItemMeta(meta);
        
        return item;
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

    public ItemStack[] getArmor() {
        return armor;
    }
    
    public static PlayerColor getPlayerColor(ItemStack item){
        for(PlayerColor color: values()){
            if(item.equals(color.getItem()))
                return color;
        }
        
        return null;
    }

    public static PlayerColor getPlayerColor(Material material){
        for(PlayerColor color: values()){
            if(material.equals(color.getMaterial()))
                return color;
        }
        
        return null;
    }
    
    private ItemStack getColoredLeatherArmor(Material armorPart, Color color){
        ItemStack item = new ItemStack(armorPart);
        ItemMeta meta = item.getItemMeta();
        if(! (meta instanceof LeatherArmorMeta))
            throw new IllegalArgumentException("Material is not leather");
        LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
        leatherMeta.setColor(color);
        item.setItemMeta(leatherMeta);
        
        return item;
    }
    
}

