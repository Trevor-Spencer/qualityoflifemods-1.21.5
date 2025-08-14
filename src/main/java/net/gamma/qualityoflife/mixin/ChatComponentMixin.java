package net.gamma.qualityoflife.mixin;

import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.gamma.qualityoflife.QualityofLifeMods.DEBUGMODE;
import static net.gamma.qualityoflife.QualityofLifeMods.LOGGER;
import static net.gamma.qualityoflife.event.CampfireClientEvent.placedCampfire;
import static net.gamma.qualityoflife.event.PestChatClientEvent.addPlot;
import static net.gamma.qualityoflife.event.SkyblockClientEvent.onGlacite;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    String regexOffline = "GROSS! While you were offline,\\s*\\S*\\s*Pests? spawned in Plots? ((?:\\d+(?:, \\d+)*)(?:,? and \\d+)?)!";
    String regexFarming = "(?:GROSS!|EWW!|YUCK!) (?:A\\s*\\S*\\s*Pest has appeared|\\d+\\s*\\S*\\s*Pests? have spawned) in Plots? - (\\d+)!";
    Pattern patternCase2 = Pattern.compile(regexOffline);
    Pattern patternCase3 = Pattern.compile(regexFarming);

    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("TAIL"))
    private void onChatRead(Component chatComponent, MessageSignature headerSignature, GuiMessageTag tag, CallbackInfo ci)
    {
        if(tag != null)
        {
            if(Objects.equals(tag.logTag(), "System"))
            {
                String givenString = chatComponent.getString();
                String strippedString = stripColorCodes(givenString);
                parseString(strippedString);
            }
        }
        if(DEBUGMODE)
        {
            LOGGER.info(String.format("[QUALITYOFLIFE] Chat Message: %s", chatComponent.getString()));
            LOGGER.info(String.format("[QUALITYOFLIFE] headerSignature: %s", headerSignature));
            LOGGER.info(String.format("[QUALITYOFLIFE] Tag: %s", tag));
        }
    }


    private String stripColorCodes(String input) {
        return input.replaceAll("§.", "");
    }

    private void parseString(String message)
    {
        if(onGlacite)
        {
            glaciteParseString(message);
            return;
        }
        Matcher matcher;

        matcher = patternCase2.matcher(message);
        if(matcher.matches())
        {
            if(DEBUGMODE){LOGGER.info("[QUALITYOFLIFE] Matched case 2");}
            String list = matcher.group(1);
            String[] plots = list.split(", | and ");
            for(String plot : plots)
            {
                addPlot(plot);
            }
            return;
        }

        matcher = patternCase3.matcher(message);
        if(matcher.matches())
        {
            if(DEBUGMODE){LOGGER.info("[QUALITYOFLIFE] Matched case 3");}
            addPlot(matcher.group(1));
        }
    }

    private void glaciteParseString(String message)
    {
        if(message.startsWith("You've warmed your feet by your campfire"))
        {
            placedCampfire = true;
        }
    }
}
