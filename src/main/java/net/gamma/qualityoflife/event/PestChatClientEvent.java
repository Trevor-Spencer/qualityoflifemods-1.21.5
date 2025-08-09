package net.gamma.qualityoflife.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.gamma.qualityoflife.QualityofLifeMods;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.gamma.qualityoflife.Config.PEST_ACTIVE;
import static net.gamma.qualityoflife.QualityofLifeMods.*;
import static net.gamma.qualityoflife.QualityofLifeMods.MODCONFIGFOLDER;
import static net.gamma.qualityoflife.event.HuntingClientEvent.SOLID_TRANSLUCENT_RENDERTYPE;
import static net.gamma.qualityoflife.event.HuntingClientEvent.drawSolidRedFace;
import static net.gamma.qualityoflife.event.SkyblockClientEvent.onGarden;
import static net.gamma.qualityoflife.event.SkyblockClientEvent.onSkyblock;

@EventBusSubscriber(modid = QualityofLifeMods.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class PestChatClientEvent {
    public static List<Integer> PESTPLOTS = new ArrayList<>();
    private static final int PLOT_WIDTH = 96;
    private static final int PLOT_HEIGHT = 10;
    private static final Map<Integer, BlockPos> PLOT_TO_COORDS = Map.ofEntries(
            Map.entry(21, new BlockPos(-240,67,-240)),
            Map.entry(13, new BlockPos(-144,67, -240)),
            Map.entry(9, new BlockPos(-48, 67, -240)),
            Map.entry(14, new BlockPos(48, 67, -240)),
            Map.entry(22, new BlockPos(144, 67, -240)),

            Map.entry(15, new BlockPos(-240, 67, -144)),
            Map.entry(5, new BlockPos(-144, 67, -144)),
            Map.entry(1, new BlockPos(-48, 67, -144)),
            Map.entry(6, new BlockPos(48, 67, -144)),
            Map.entry(16, new BlockPos(144, 67, -144)),

            Map.entry(10, new BlockPos(-240, 67, -48)),
            Map.entry(2, new BlockPos(-144, 67, -48)),
            Map.entry(3, new BlockPos(48, 67, -48)),
            Map.entry(11, new BlockPos(144, 67, -48)),

            Map.entry(17, new BlockPos(-240, 67, 48)),
            Map.entry(7, new BlockPos(-144, 67, 48)),
            Map.entry(4, new BlockPos(-48, 67, 48)),
            Map.entry(8, new BlockPos(48, 67, 48)),
            Map.entry(18, new BlockPos(144, 67, 48)),

            Map.entry(23, new BlockPos(-240, 67, 144)),
            Map.entry(19, new BlockPos(-144, 67, 144)),
            Map.entry(12, new BlockPos(-48, 67, 144)),
            Map.entry(20, new BlockPos(48, 67, 144)),
            Map.entry(24, new BlockPos(144, 67, 144))
            );

    private static final int SCANDELAY = 20;
    private static int scanTick = 0;

    private static final int RED = 0xFF;
    private static final int GREEN = 0xFF;
    private static final int BLUE = 0xFF;
    private static final int ALPHA = 0x80;

    private static final String plotClear = "Plot - (\\d+)$";
    private static final Pattern clearPlotPattern = Pattern.compile(plotClear);
    private static final String plotPest = "Plot - (\\d+)\\s*\\S*\\s*x(\\d+)$";
    private static final Pattern pestPlotPattern = Pattern.compile(plotPest);

    @SubscribeEvent
    private static void tick(ClientTickEvent.Post event)
    {
        if(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null){return;}
        if(!Minecraft.getInstance().level.isClientSide || !onSkyblock || !PEST_ACTIVE.get()){return;}
        if(scanTick < SCANDELAY)
        {
            scanTick++;
            return;
        }
        scanTick = 0;

        checkPest();
    }

    private static void checkPest()
    {
        Objective obj = Minecraft.getInstance().level.getScoreboard().getDisplayObjective(DisplaySlot.SIDEBAR);
        if(obj == null) {return;}
        Collection<PlayerTeam> teams = Minecraft.getInstance().level.getScoreboard().getPlayerTeams();
        for(PlayerTeam team : teams)
        {
            String scoreboardLineGiven = String.format("%s%s",team.getPlayerPrefix().getString(), team.getPlayerSuffix().getString());
            String scoreboardLine = stripColorCodes(scoreboardLineGiven);
            if(scoreboardLine.contains("The Garden"))
            {
                if(!scoreboardLine.contains("x"))
                {
                    if(!PESTPLOTS.isEmpty()){PESTPLOTS.clear();}
                    return;
                }
            }
            else if(scoreboardLine.contains("Plot - "))
            {
                if(!scoreboardLine.contains("x"))
                {
                    Matcher matcher = clearPlotPattern.matcher(scoreboardLine);
                    if(matcher.find())
                    {
                        removePlot(matcher.group(1));
                    }
                    else
                    {
                        if(DEBUGMODE){LOGGER.info(String.format("[QUALITYOFLIFE] Doesn't match Clear Plot Regex: %s", scoreboardLine));}
                    }
                }
                else
                {
                    Matcher matcher = pestPlotPattern.matcher(scoreboardLine);
                    if(matcher.find())
                    {
                        addPlot(matcher.group(1));
                    }
                    else
                    {
                        if(DEBUGMODE){LOGGER.info(String.format("[QUALITYOFLIFE] Doesn't match Pest Plot Regex: %s", scoreboardLine));}
                    }
                }
            }
        }
    }

    private static String stripColorCodes(String input) {
        return input.replaceAll("§.", "");
    }

    public static void removePlot(String plot) {
        try{
            int plotNumber = Integer.parseInt(plot);
            if(PESTPLOTS.contains(plotNumber))
            {
                PESTPLOTS.remove(Integer.valueOf(plotNumber));
                if(DEBUGMODE){LOGGER.info(String.format("[QUALITYOFLIFE] Removed %d to list", plotNumber));}
            }
            else
            {
                if(DEBUGMODE){LOGGER.info(String.format("[QUALITYOFLIFE] List doesn't contain Plot: %d", plotNumber));}
            }
        } catch (Exception e) {
            LOGGER.error(String.format("[QUALITYOFLIFE] Error Removing Plot %s from list", plot));
        }
    }

    public static void addPlot(String stringPlot)
    {
        try{
            int plotNumber = Integer.parseInt(stringPlot);
            if(!PESTPLOTS.contains(plotNumber))
            {
                PESTPLOTS.add(plotNumber);
                if(DEBUGMODE){LOGGER.info(String.format("[QUALITYOFLIFE] Added %d to list", plotNumber));}
            }
            else
            {
                if(DEBUGMODE){LOGGER.info(String.format("[QUALITYOFLIFE] List already contain Plot: %d", plotNumber));}
            }
        } catch (NumberFormatException e) {
            LOGGER.error(String.format("Invalid number format: %s", stringPlot));
        }
    }

    @SubscribeEvent
    private static void renderWorld(RenderLevelStageEvent event)
    {
        if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS){return;}
        if(!PEST_ACTIVE.get() || !onSkyblock || !onGarden){return;}
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 camPos = camera.getPosition();

        VertexConsumer buffer = mc.renderBuffers().bufferSource()
                .getBuffer(SOLID_TRANSLUCENT_RENDERTYPE);

        for (int plotNumber : PESTPLOTS) {
                BlockPos pos = PLOT_TO_COORDS.get(plotNumber);
                double x = pos.getX();
                double y = pos.getY();
                double z = pos.getZ();

                double minX = x - camPos.x;
                double minY = y - camPos.y;
                double minZ = z - camPos.z;
                double maxX = minX + PLOT_WIDTH;
                double maxY = minY + PLOT_HEIGHT;
                double maxZ = minZ + PLOT_WIDTH;

                //North Face
                drawSolidRedFace(buffer,
                        maxX, minY, minZ,
                        minX, minY, minZ,
                        minX, maxY, minZ,
                        maxX, maxY, minZ,
                        RED, GREEN, BLUE, ALPHA);
                //South Face
                drawSolidRedFace(buffer,
                        maxX, minY, maxZ,
                        minX, minY, maxZ,
                        minX, maxY, maxZ,
                        maxX, maxY, maxZ,
                        RED, GREEN, BLUE, ALPHA);
                //East Face
                drawSolidRedFace(buffer,
                        maxX,minY,minZ,
                        maxX,minY,maxZ,
                        maxX,maxY,maxZ,
                        maxX,maxY,minZ,
                        RED, GREEN, BLUE, ALPHA);
                //West Face
                drawSolidRedFace(buffer,
                        minX,minY,minZ,
                        minX,minY,maxZ,
                        minX,maxY,maxZ,
                        minX,maxY,minZ,
                        RED, GREEN, BLUE, ALPHA);
//                //Bottom Face
//                drawSolidRedFace(buffer,
//                        maxX,minY,minZ,
//                        minX,minY,minZ,
//                        minX,minY,maxZ,
//                        maxX,minY,maxZ,
//                        RED, GREEN, BLUE, ALPHA);
                //Top Face
                drawSolidRedFace(buffer,
                        maxX,maxY,minZ,
                        minX,maxY,minZ,
                        minX,maxY,maxZ,
                        maxX,maxY,maxZ,
                        RED, GREEN, BLUE, ALPHA);
        }

        // Important: flush the buffer
        mc.renderBuffers().bufferSource().endBatch(SOLID_TRANSLUCENT_RENDERTYPE);
    }

    public static void readPestPlotJson()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        Path filePath = MODCONFIGFOLDER.resolve("pestplots.json");
        String debugStatement;
        if(!Files.exists(filePath))
        {
            debugStatement = String.format("[QUALITYOFLIFE] Filepath not found: %s", filePath);
            LOGGER.warn(debugStatement);
            return;
        }
        try
        {
            JsonNode jsonNode = objectMapper.readTree(filePath.toFile());
            if(jsonNode.has("pestPlots") && jsonNode.get("pestPlots").isArray())
            {
                for (JsonNode node : jsonNode.get("pestPlots"))
                {
                    PESTPLOTS.add(node.asInt());
                }
            }
            debugStatement = String.format("[QUALITYOFLIFE] Finished Reading file: %s", filePath);
            LOGGER.info(debugStatement);
        } catch(IOException exception) {
            debugStatement = String.format("[QUALITYOFLIFE] Unable to parse file: %s", filePath);
            LOGGER.warn(debugStatement);
        }
    }

    public static void writePestPlotJson()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();
        ArrayNode pestPlots = objectMapper.createArrayNode();
        for(int plot : PESTPLOTS)
        {
            pestPlots.add(plot);
        }
        jsonNode.set("pestPlots", pestPlots);

        String debugStatement;
        try
        {
            Files.createDirectories(MODCONFIGFOLDER);
        } catch (IOException e) {
            debugStatement = String.format("[QUALITYOFLIFE] Unable to create config directory: %s", MODCONFIGFOLDER);
            LOGGER.warn(debugStatement);
        }

        Path filePath = MODCONFIGFOLDER.resolve("pestplots.json");
        try
        {
            objectMapper.writeValue(filePath.toFile(), jsonNode);

            debugStatement = String.format("[QUALITYOFLIFE] Finished Writing file: %s", filePath);
            LOGGER.info(debugStatement);
        } catch(IOException exception) {
            debugStatement = String.format("[QUALITYOFLIFE] Unable to write file: %s", filePath);
            LOGGER.warn(debugStatement);
        }
    }
}
