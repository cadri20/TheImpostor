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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author cadri
 */
public class LanguageManager {
    public static YamlConfiguration language;
    
    public static void loadLanguage() {
        String languageChoosed = TheImpostor.plugin.getConfig().getString("language");
        if(languageChoosed == null)
            throw new NullPointerException("language property not defined");
        
    
        File languageFile = new File(TheImpostor.plugin.getDataFolder() + File.separator + languageChoosed);
        if(!languageFile.exists()){
            InputStream fileFromPlugin =  TheImpostor.plugin.getResource(languageChoosed);
            try {
                Files.copy(fileFromPlugin, languageFile.toPath());
            } catch (IOException ex) {
                TheImpostor.plugin.getLogger().log(Level.SEVERE,"Error reading file");
            }
        }
        try{
        language = YamlConfiguration.loadConfiguration(languageFile);
        
        language.save(new File(TheImpostor.plugin.getDataFolder() + File.separator + languageChoosed));
        }catch(IllegalArgumentException e){
            TheImpostor.plugin.getLogger().log(Level.SEVERE, ChatColor.RED +  languageChoosed + "file not found");
        } catch (IOException ex) {
            Logger.getLogger(LanguageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        List<String> invalidKeys = getInvalidKeys();
        if(invalidKeys == null){
            TheImpostor.plugin.getLogger().log(Level.WARNING,"Keys from " + languageChoosed + " are incompleted" );
        }else if(!invalidKeys.isEmpty()){
            for(String key: invalidKeys){
                TheImpostor.plugin.getLogger().log(Level.WARNING, "The key " + key + " is invalid");
            }
        }
    }
    
    /**
     * 
     * @return List of invalidd keys. If they have a different number this return null
     */
    public static List<String> getInvalidKeys(){
        MessageKey[] keysInEnum = MessageKey.values();
        Set<String> keys = language.getKeys(false);
        List<String> invalidKeys = new ArrayList<>();
        
        if(keysInEnum.length != keys.size())
            return null;
        
        int i = 0;
        for(String key: keys){
            if(!key.equals(keysInEnum[i].toString()))
                invalidKeys.add(key);
            i++;
        }
        return invalidKeys;        
    }
    
    public static String getTranslation(MessageKey key){
        String translation = language.getString(key.toString());
       
        return ChatColor.translateAlternateColorCodes('&', translation);
    }
    
    public static String getTranslation(MessageKey key, Object... replacements){
        String translation = String.format(language.getString(key.toString()), replacements);
        
        return ChatColor.translateAlternateColorCodes('&', translation);
    }
}
