package net.gamma.qualityoflife.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.Map;

import static net.gamma.qualityoflife.QualityofLifeMods.DEBUGMODE;
import static net.gamma.qualityoflife.QualityofLifeMods.LOGGER;
import static net.gamma.qualityoflife.event.MayorClientEvent.*;
import static net.gamma.qualityoflife.event.PestChatClientEvent.stripColorCodes;
import static net.gamma.qualityoflife.util.ApiUtils.removeQuotes;

public class JsonReaders {

    public static void mayorReader(String text)
    {
        String strippedString = stripColorCodes(text);
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            mayorPerks.clear();
            Map<String, Component> map;
            JsonNode root = objectMapper.readTree(strippedString);
            JsonNode mayor = root.get("mayor");
            mayorName = Component.literal(removeQuotes(mayor.get("name").toString())).withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD, ChatFormatting.UNDERLINE);
            map = MAYORTOPERK.getOrDefault(mayorName.getString(), null);
            if(map != null)
            {
                for(JsonNode perk : mayor.get("perks"))
                {
                    String perkName = removeQuotes(perk.get("name").toString());
                    Component editedName = map.getOrDefault(perkName, null);
                    if(editedName != null)
                    {
                        mayorPerks.add(editedName);
                    }
                    else
                    {
                        mayorPerks.add(Component.literal(removeQuotes(perk.get("description").toString())));
                    }
                }
            }
            else
            {
                for(JsonNode perk : mayor.get("perks"))
                {
                    mayorPerks.add(Component.literal(removeQuotes(perk.get("description").toString())));
                }
            }

            JsonNode minister = mayor.get("minister");
            ministerName = Component.literal(removeQuotes(minister.get("name").toString())).withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD, ChatFormatting.UNDERLINE);
            map = MAYORTOPERK.getOrDefault(ministerName.getString(), null);
            if(map != null)
            {
                JsonNode perk = minister.get("perk");
                String perkName = removeQuotes(perk.get("name").toString());
                Component editedName = map.getOrDefault(perkName, null);
                if(editedName != null)
                {
                    ministerPerk = editedName;
                }
                else
                {
                    ministerPerk = Component.literal(removeQuotes(minister.get("description").toString()));
                }
            }
            else
            {
                ministerPerk = Component.literal(removeQuotes(minister.get("description").toString()));
            }

            if(DEBUGMODE){
                LOGGER.info(String.format("[QUALITYOFLIFE] Mayor Name: %s", mayorName.getString()));
                for(Component perk : mayorPerks)
                {
                    LOGGER.info(String.format("[QUALITYOFLIFE] Mayor Perk: %s", perk.getString()));
                }

                LOGGER.info(String.format("[QUALITYOFLIFE] Minister Name: %s", ministerName.getString()));
                LOGGER.info(String.format("[QUALITYOFLIFE] Minister Perk %s", ministerPerk.getString()));
            }


        } catch (Exception e) {
            LOGGER.info("[QUALITYOFLIFE] Error reading Mayor json object");
            e.printStackTrace();
        }

    }
}

