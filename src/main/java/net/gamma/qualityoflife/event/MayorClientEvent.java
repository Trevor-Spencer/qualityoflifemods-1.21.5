package net.gamma.qualityoflife.event;

import net.gamma.qualityoflife.QualityofLifeMods;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.gamma.qualityoflife.Config.MAYOR_ACTIVE;
import static net.gamma.qualityoflife.QualityofLifeMods.DEBUGMODE;
import static net.gamma.qualityoflife.QualityofLifeMods.LOGGER;
import static net.gamma.qualityoflife.event.SkyblockClientEvent.onSkyblock;
import static net.gamma.qualityoflife.util.ApiUtils.pollMayorApi;
import static net.gamma.qualityoflife.util.DisplayUtils.renderContentMayor;
import static net.gamma.qualityoflife.widget.ManagerWidget.MAYORWIDGET;

@EventBusSubscriber(modid = QualityofLifeMods.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class MayorClientEvent {
    private static int pollTimer = 0;
    private static final int POLLTIMERMAX = 72000;
    public static boolean forceMayorApiPoll = true;
    public static boolean alreadyOn = false;

    public static Component mayorName;
    public static List<Component> mayorPerks = new ArrayList<>();
    public static Component ministerName;
    public static Component ministerPerk;

    private static final String TITLE = "MAYOR";
    private static final int TITLECOLOR = 0xFFFFFFFF;
    private static final int TEXTCOLOR = 0xFFFFFFFF;
    private static final int BACKGROUNDCOLOR = 0x805C5C5C;

    public static final Map<String, Component> AATROXPERKS = Map.of(
            "Slayer XP Buff", Component.literal("Slayer XP ").withStyle(ChatFormatting.WHITE)
                    .append(Component.literal("+25%").withStyle(ChatFormatting.GREEN)),
            "Pathfinder", Component.literal("Rare Drops ").withStyle(ChatFormatting.WHITE)
                    .append(Component.literal("+20%").withStyle(ChatFormatting.GREEN)),
            "SLAHED Pricing", Component.literal("1/2").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(" Price").withStyle(ChatFormatting.WHITE)));
    public static final Map<String, Component> COLEPERKS = Map.of(
            "Mining Fiesta", Component.literal("5 Mining Fiesta with ").withStyle(ChatFormatting.WHITE)
                    .append(Component.literal("+75").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(" during Fiesta's").withStyle(ChatFormatting.WHITE)),
            "Mining XP Buff", Component.literal("+60").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(" Mining Wisdom on public islands").withStyle(ChatFormatting.WHITE)),
            "Molten Forge", Component.literal("-25%").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(" Forge time").withStyle(ChatFormatting.WHITE)),
            "Prospection", Component.literal("Mining minions ").withStyle(ChatFormatting.WHITE)
                    .append(Component.literal("25%").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(" Faster").withStyle(ChatFormatting.WHITE)));
    public static final Map<String, Component> DIANAPERKS = Map.of(
            "Pet XP Buff", Component.literal("Pet XP ").withStyle(ChatFormatting.WHITE)
                    .append(Component.literal("+35%").withStyle(ChatFormatting.GREEN)),
            "Lucky!", Component.literal("Pet Luck ").withStyle(ChatFormatting.WHITE)
                    .append(Component.literal("+25").withStyle(ChatFormatting.GREEN)),
            "Mythological Ritual", Component.literal("Mythological Ritual").withStyle(ChatFormatting.WHITE),
            "Sharing is Caring", Component.literal("3 EXP Share pets active and ").withStyle(ChatFormatting.WHITE)
                    .append(Component.literal("+10%").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(" EXP Share rate").withStyle(ChatFormatting.WHITE)));
    public static final Map<String, Component> DIAZPERKS = Map.of(
            "Long Term Investment", Component.literal("Elected minster all perks next election").withStyle(ChatFormatting.WHITE),
            "Shopping Spree", Component.literal("10X").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(" NPC buy limit").withStyle(ChatFormatting.WHITE)),
            "Stock Exchange", Component.literal("Stonks Auction and extravagant items").withStyle(ChatFormatting.WHITE),
            "Volume Trading", Component.literal("2X").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(" Special auction quantity").withStyle(ChatFormatting.WHITE)));
    public static final Map<String, Component> FINNEGANPERKS = Map.of(
            "Blooming Business", Component.literal("Garden Visitors give Fine Flour. Higher rarity Visitors. ").withStyle(ChatFormatting.WHITE)
                    .append(Component.literal("+10%").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(" Copper").withStyle(ChatFormatting.WHITE)),
            "GOATed", Component.literal("Farming brackets contain ").withStyle(ChatFormatting.WHITE)
                    .append(Component.literal("10%").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(" more players").withStyle(ChatFormatting.WHITE)),
            "Pelt-pocalypse", Component.literal("1.5X").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(" Pelts").withStyle(ChatFormatting.WHITE)),
            "Pest Eradicator", Component.literal("Pesthunter Philip's bonus is 60 minutes. Pest spawns ").withStyle(ChatFormatting.WHITE)
                    .append(Component.literal("4X").withStyle(ChatFormatting.RED)));
    public static final Map<String, Component> FOXYPERKS = Map.of(
            "A Tune for Giving", Component.literal("Party Chest and Party Gifts").withStyle(ChatFormatting.WHITE),
            "Chivalrous Carnival", Component.literal("Carnival in HUB").withStyle(ChatFormatting.WHITE),
            "Sweet Benevolence", Component.literal("+30%").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(" Candy,Gifts,Chocolate").withStyle(ChatFormatting.WHITE)));
    public static final Map<String, Component> MARINAPERKS = Map.of(
            "Double Trouble", Component.literal("Every 1 SCC gain ").withStyle(ChatFormatting.WHITE)
                    .append(Component.literal("+0.1").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(" DHC").withStyle(ChatFormatting.WHITE)),
            "Fishing XP Buff", Component.literal("+50").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(" Fishing Wisdom").withStyle(ChatFormatting.WHITE)),
            "Fishing Festival", Component.literal("Fishing event each month").withStyle(ChatFormatting.WHITE),
            "Luck of the Sea 2.0", Component.literal("+15").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(" SCC").withStyle(ChatFormatting.WHITE)));
    public static final Map<String, Component> PAULPERKS = Map.of(
            "Benediction", Component.literal("Blessings ").withStyle(ChatFormatting.WHITE)
                    .append(Component.literal("+25%").withStyle(ChatFormatting.GREEN)),
            "Marauder", Component.literal("Dungeon Chest ").withStyle(ChatFormatting.WHITE)
                    .append(Component.literal("20% cheaper").withStyle(ChatFormatting.GREEN)),
            "EZPZ", Component.literal("Dungeon Score ").withStyle(ChatFormatting.WHITE)
                    .append(Component.literal("+10").withStyle(ChatFormatting.GREEN)));
    public static final Map<String, Component> JERRYPERKS = Map.of(
            "Perkpocalypse", Component.literal("Rotate all mayor's and perks every 6 hours").withStyle(ChatFormatting.WHITE),
            "Statpocalypse", Component.literal("+10%").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(" Most stats").withStyle(ChatFormatting.WHITE)),
            "Jerrypocalypse", Component.literal("Hidden Jerries").withStyle(ChatFormatting.WHITE));
    public static final Map<String, Component> DERPYPERKS = Map.of(
            "QUAD TAXES!!!", Component.literal("4X").withStyle(ChatFormatting.RED)
                    .append(Component.literal(" Taxes").withStyle(ChatFormatting.WHITE)),
            "TURBO MINIONS!!!", Component.literal("2X").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(" Minion output").withStyle(ChatFormatting.WHITE)),
            "DOUBLE MOBS HP!!!", Component.literal("2X").withStyle(ChatFormatting.RED)
                    .append(Component.literal(" Mob Health").withStyle(ChatFormatting.WHITE)),
            "MOAR SKILLZ!!!", Component.literal("+50%").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(" More skill experience").withStyle(ChatFormatting.WHITE)));
    public static final Map<String, Component> SCORPIUSPERKS = Map.of(
            "Bribe", Component.literal("Offered Coins").withStyle(ChatFormatting.GREEN),
            "Darker Auctions", Component.literal("6 Round dark auctions with special items").withStyle(ChatFormatting.WHITE));

    public static final Map<String, Map<String,Component>> MAYORTOPERK = Map.ofEntries(
            Map.entry("Aatrox", AATROXPERKS),
            Map.entry("Cole", COLEPERKS),
            Map.entry("Diana", DIANAPERKS),
            Map.entry("Diaz", DIAZPERKS),
            Map.entry("Finnegan", FINNEGANPERKS),
            Map.entry("Foxy", FOXYPERKS),
            Map.entry("Marina", MARINAPERKS),
            Map.entry("Paul", PAULPERKS),
            Map.entry("Jerry", JERRYPERKS),
            Map.entry("Derpy", DERPYPERKS),
            Map.entry("Scorpius", SCORPIUSPERKS));

    @SubscribeEvent
    private static void tick(ClientTickEvent.Post event)
    {
        Level level = Minecraft.getInstance().level;
        if(level == null || Minecraft.getInstance().player == null){return;}
        if(!level.isClientSide){return;}
        pollTimer++;
        if(onSkyblock)
        {
            if(pollTimer > POLLTIMERMAX || forceMayorApiPoll)
            {
                if(DEBUGMODE){LOGGER.info("Request Mayor Data");}
                pollMayorApi();
                forceMayorApiPoll = false;
                pollTimer = 0;
            }
        }
        else
        {
            if(pollTimer > POLLTIMERMAX)
            {
                if(DEBUGMODE){LOGGER.info("Poll timer over max. Fallback reset");}
                pollTimer = 0;
            }
        }
    }

    @SubscribeEvent
    private static void serverJoin(ClientPlayerNetworkEvent.LoggingIn event)
    {
        try
        {
            if(event.getConnection() == null){return;}
            String address = event.getConnection().getRemoteAddress().toString();
            if(DEBUGMODE){LOGGER.info(String.format("[QUALITYOFLIFE] Server Joining Address: %s", address));}
            if(address.contains("hypixel"))
            {
                if(!alreadyOn){forceMayorApiPoll = true;}

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @SubscribeEvent
    private static void serverLeave(ClientPlayerNetworkEvent.LoggingOut event)
    {
        try
        {
            if(event.getConnection() == null){return;}
            String address = event.getConnection().getRemoteAddress().toString();
            if(DEBUGMODE){LOGGER.info(String.format("[QUALITYOFLIFE] Server Leaving Address: %s", address));}
            if(address.contains("hypixel"))
            {
                forceMayorApiPoll = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    private static void render(RenderGuiEvent.Post event)
    {
        if(MAYOR_ACTIVE.get())
        {
            if(Minecraft.getInstance().level == null){return;}
            if(MAYORWIDGET == null){return;}
            if(!onSkyblock){return;}
            GuiGraphics graphics = event.getGuiGraphics();
            int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

            List<Component> strings = new ArrayList<>();
            strings.add(mayorName);
            strings.addAll(mayorPerks);
            strings.add(ministerName);
            strings.add(ministerPerk);

            renderContentMayor(graphics,
                    strings, TEXTCOLOR, TITLE, TITLECOLOR, BACKGROUNDCOLOR,
                    screenWidth, screenHeight, MAYORWIDGET);
        }
    }
}
