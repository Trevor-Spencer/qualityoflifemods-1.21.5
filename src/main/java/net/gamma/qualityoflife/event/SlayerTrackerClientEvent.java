package net.gamma.qualityoflife.event;

import com.mojang.logging.LogUtils;
import net.gamma.qualityoflife.QualityofLifeMods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.slf4j.Logger;

import static net.gamma.qualityoflife.Config.SLAYER_ACTIVE;
import static net.gamma.qualityoflife.event.CoordinatesClientEvent.stringBiome;

import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = QualityofLifeMods.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class SlayerTrackerClientEvent {
    private static int blazeBossID = -1;
    private static UUID blazeBossUUID;
    private static int blazeNameID = -1;
    private static int blazeSpawnByID = -1;
    private static int blazeTimeRemainingID = -1;
    private static int blazeCount = 0;
    private static boolean blazeFound = false;

    private static int skeletonBossID = -1;
    private static UUID skeletonBossUUID;
    private static int skeletonNameID = -1;
    private static int skeletonTimeRemainingID = -1;
    private static int skeletonCount = 0;
    private static boolean skeletonFound = false;

    private static int piglinBossID = -1;
    private static UUID piglinBossUUID;
    private static int piglinNameID = -1;
    private static int piglinTimeRemainingID = -1;
    private static int piglinCount = 0;
    private static boolean piglinFound = false;

    private static boolean onCrimsonIsle = false;
    private static boolean trackingBoss = false;

    private static final List<String> LOCATIONS = List.of("Crimson Isle", "Dojo", "Crimson Fields", "Burning Desert",
            "Dragontail", "Dragontail Blacksmith", "Dragontail Bank", "Dragontail Townsquare", "Dragontail Bazaar",
            "Dragontail Auction House", "The Bastion", "The Dukedom", "Magma Chamber", "Matriarch's Lair",
            "Blazing Volcano", "Odger's Hut", "The Wasteland", "Forgotten Skull", "Smoldering Tomb", "Cathedral",
            "Scarleton", "Scarleton Plaza", "Scarleton Bazaar", "Scarleton Auction House", "Scarleton Bank",
            "Mystic Marsh", "Courtyard", "Community Center");


    @SubscribeEvent
    private static void bossTracker(ClientTickEvent.Post event)
    {
        if(!SLAYER_ACTIVE.get()){return;}
        //Check if player is on crimsonIsle
        if(Minecraft.getInstance().player == null) {return;}
        if(Minecraft.getInstance().level.isClientSide)
        {
            PlayerTeam team_10 = Minecraft.getInstance().level.getScoreboard().getPlayerTeam("team_10");
            boolean location10Found = false;
            if (team_10 != null) {
                String location10 = "Biome: " + team_10.getPlayerPrefix().getString() + team_10.getPlayerSuffix().getString();
                location10Found = LOCATIONS.stream().anyMatch(loc -> location10.contains(loc));
                if (location10Found) {
                    onCrimsonIsle = true;
                    stringBiome = location10;
                } else {
                    onCrimsonIsle = false;
                }
            }
            if (!location10Found) {
                PlayerTeam team_6 = Minecraft.getInstance().level.getScoreboard().getPlayerTeam("team_6");
                if (team_6 != null) {
                    String location06 = "Biome: " + team_6.getPlayerPrefix().getString() + team_6.getPlayerSuffix().getString();
                    boolean location06Found = LOCATIONS.stream().anyMatch(loc -> location06.contains(loc));
                    if (location06Found) {
                        onCrimsonIsle = true;
                        stringBiome = location06;
                    } else {
                        onCrimsonIsle = false;
                    }
                }
            }
            PlayerTeam team_5 = Minecraft.getInstance().level.getScoreboard().getPlayerTeam("team_5");
            if(team_5 != null)
            {
                String slayerStatus = team_5.getPlayerPrefix().getString() + team_5.getPlayerSuffix().getString();
                if(!slayerStatus.contains("Slayer Quest")) {return;}
            }
            PlayerTeam team_3 = Minecraft.getInstance().level.getScoreboard().getPlayerTeam("team_3");
            if(team_3 != null)
            {
                String slayerStatus = team_3.getPlayerPrefix().getString() + team_3.getPlayerSuffix().getString();
                if(slayerStatus.contains("Slay the boss!"))
                {
                    trackingBoss = true;
                }
                else
                {
                    trackingBoss = false;
                }
            }
        }


        if(!onCrimsonIsle){return;}
        //If blaze Slayer is not active
        if(!trackingBoss)
        {
            blazeBossID = -1;
            blazeBossUUID = null;
            blazeNameID = -1;
            blazeSpawnByID = -1;
            blazeTimeRemainingID = -1;
            blazeCount = 0;
            blazeFound = false;

            skeletonBossID = -1;
            skeletonBossUUID = null;
            skeletonNameID = -1;
            skeletonTimeRemainingID = -1;
            skeletonCount = 0;
            skeletonFound = false;

            piglinBossID = -1;
            piglinBossUUID = null;
            piglinNameID = -1;
            piglinTimeRemainingID = -1;
            piglinCount = 0;
            piglinFound = false;


        }
        else
        {
            if(Minecraft.getInstance().player instanceof LocalPlayer player)
            {
                ClientLevel level = Minecraft.getInstance().level;
                Logger LOGGER = LogUtils.getLogger();
                if(level.getEntity(blazeNameID) != null && level.getEntity(blazeBossID) != null && level.getEntity(blazeSpawnByID) != null && level.getEntity(blazeTimeRemainingID) != null)
                {
                    //LOG BOSS FOUND WITH INFO
                    LOGGER.info("---[BLAZE BOSS FOUND INFO]---");
                    LOGGER.info("BossID: " + blazeBossID);
                    LOGGER.info("BossUUID: " + blazeBossUUID);
                    LOGGER.info("NameID: " + blazeNameID + " NameComponent: " + level.getEntity(blazeNameID).getCustomName().getString());
                    LOGGER.info("SpawnedByID: " + blazeSpawnByID + " NameComponent: " + level.getEntity(blazeSpawnByID).getCustomName().getString());
                    LOGGER.info("TimeID: " + blazeTimeRemainingID + " NameComponent: " + level.getEntity(blazeTimeRemainingID).getCustomName().getString());
                    return;
                }
                if((level.getEntity(skeletonBossID) != null && level.getEntity(skeletonNameID) != null && level.getEntity(skeletonTimeRemainingID) != null) || (level.getEntity(piglinBossID) != null && level.getEntity(piglinNameID) != null && level.getEntity(piglinTimeRemainingID) != null))
                {
                    if(level.getEntity(skeletonBossID) != null)
                    {
                        //LOG MINI FOUND WITH INFO
                        LOGGER.info("---[SKELETON MINI FOUND INFO]---");
                        LOGGER.info("BossID: " + skeletonBossID);
                        LOGGER.info("BossUUID: " + skeletonBossUUID);
                        LOGGER.info("NameID: " + skeletonNameID + " NameComponent: " + level.getEntity(skeletonNameID).getCustomName().getString());
                        LOGGER.info("TimeID: " + skeletonTimeRemainingID + " NameComponent: " + level.getEntity(skeletonTimeRemainingID).getCustomName().getString());
                    }
                    if(level.getEntity(piglinBossID) != null)
                    {
                        //LOG MINI FOUND WITH INFO
                        LOGGER.info("---[PIGLIN MINI FOUND INFO]---");
                        LOGGER.info("BossID: " + piglinBossID);
                        LOGGER.info("BossUUID: " + piglinBossUUID);
                        LOGGER.info("NameID: " + piglinNameID + " NameComponent: " + level.getEntity(piglinNameID).getCustomName().getString());
                        LOGGER.info("TimeID: " + piglinTimeRemainingID + " NameComponent: " + level.getEntity(piglinTimeRemainingID).getCustomName().getString());
                    }
                    return;
                }

                //RESET ALL TRACKERS
                blazeBossID = -1;
                blazeBossUUID = null;
                blazeNameID = -1;
                blazeSpawnByID = -1;
                blazeTimeRemainingID = -1;
                blazeCount = 0;
                blazeFound = false;

                skeletonNameID = -1;
                skeletonTimeRemainingID = -1;
                skeletonCount = 0;
                skeletonFound = false;
                piglinNameID = -1;
                piglinTimeRemainingID = -1;
                piglinCount = 0;
                piglinFound = false;

                AABB area = new AABB(player.getOnPos()).inflate(20);
                List<Entity> nearbyBlazes = level.getEntities(
                        player,
                        area,
                        entity -> entity.getType() == EntityType.BLAZE);

                for(Entity entityBlaze : nearbyBlazes)
                {
                    int tempNameID = -1;
                    int tempSpawnByID = -1;
                    int tempTimeRemainingID = -1;
                    int tempBossID = entityBlaze.getId();
                    UUID tempBossUUID = entityBlaze.getUUID();
                    int tempCount = 0;

                    AABB areaArmorstand = new AABB(entityBlaze.blockPosition()).inflate(0d,2d,0d);
                    List<Entity> nearbyArmorStand = Minecraft.getInstance().level.getEntities(player,areaArmorstand, entity -> entity.getType() == EntityType.ARMOR_STAND);

                    for(Entity entityArmorstand : nearbyArmorStand)
                    {
                        Component customName = entityArmorstand.getCustomName();
                        int armorstandID = entityArmorstand.getId();
                        if(customName != null)
                        {
                            if(customName.getString().contains("Inferno Demonlord"))
                            {
                                tempNameID = armorstandID;
                                tempCount++;
                            }
                            else if(customName.getString().contains("Spawned by: " + player.getName().getString()))
                            {
                                tempSpawnByID = armorstandID;
                                tempCount++;
                            }
                            else if(customName.getString().matches(".*\\d+:\\d+.*"))
                            {
                                tempTimeRemainingID = armorstandID;
                                tempCount++;
                            }
                        }
                    }

                    if(tempNameID != -1 && tempSpawnByID != -1 && tempTimeRemainingID != -1)
                    {
                        blazeBossID = tempBossID;
                        blazeBossUUID = tempBossUUID;
                        blazeNameID = tempNameID;
                        blazeSpawnByID = tempSpawnByID;
                        blazeTimeRemainingID = tempTimeRemainingID;
                        blazeCount = tempCount;
                        blazeFound = true;
                        //LOG BOSS INITIALIZE WITH INFO
                        LOGGER.info("---[BLAZE BOSS INITIALIZED INFO]---");
                        LOGGER.info("BossID: " + blazeBossID);
                        LOGGER.info("BossUUID: " + blazeBossUUID);
                        LOGGER.info("NameID: " + blazeNameID + " NameComponent: " + level.getEntity(blazeNameID).getCustomName().getString());
                        LOGGER.info("SpawnedByID: " + blazeSpawnByID + " NameComponent: " + level.getEntity(blazeSpawnByID).getCustomName().getString());
                        LOGGER.info("TimeID: " + blazeTimeRemainingID + " NameComponent: " + level.getEntity(blazeTimeRemainingID).getCustomName().getString());
                        break;
                    }
                }
                //NOT IN BLAZE PHASE (miniboss phase)
                if(!blazeFound)
                {
                    blazeNameID = -1;
                    blazeSpawnByID = -1;
                    blazeTimeRemainingID = -1;
                    blazeCount = 0;
                    blazeFound = false;

                    //RESET ALL TRACKERS
                    skeletonBossID = -1;
                    skeletonBossUUID = null;
                    skeletonNameID = -1;
                    skeletonTimeRemainingID = -1;
                    skeletonCount = 0;
                    skeletonFound = false;
                    AABB areaSkeleton = new AABB(player.getOnPos()).inflate(20);
                    List<Entity> nearbySkeletons = level.getEntities(
                            player,
                            areaSkeleton,
                            entity -> entity.getType() == EntityType.WITHER_SKELETON);

                    for(Entity entitySkeleton : nearbySkeletons)
                    {
                        int tempNameID = -1;
                        int tempTimeRemainingID = -1;
                        int tempBossID = entitySkeleton.getId();
                        UUID tempBossUUID = entitySkeleton.getUUID();
                        int tempCount = 0;

                        AABB areaArmorstand = new AABB(entitySkeleton.blockPosition()).inflate(0d,3d,0d);
                        List<Entity> nearbyArmorStand = Minecraft.getInstance().level.getEntities(player,areaArmorstand, entity -> entity.getType() == EntityType.ARMOR_STAND);

                        for(Entity entityArmorstand : nearbyArmorStand)
                        {
                            Component customName = entityArmorstand.getCustomName();
                            int armorstandID = entityArmorstand.getId();
                            if(customName != null)
                            {
                                if(customName.getString().contains("ⓆⓊⒶⓏⒾⒾ"))
                                {
                                    tempNameID = armorstandID;
                                    tempCount++;
                                }
                                else if(customName.getString().matches(".*\\d+:\\d+.*"))
                                {
                                    tempTimeRemainingID = armorstandID;
                                    tempCount++;
                                }
                            }
                        }

                        if(tempNameID != -1 && tempTimeRemainingID != -1)
                        {
                            skeletonBossID = tempBossID;
                            skeletonBossUUID = tempBossUUID;
                            skeletonNameID = tempNameID;
                            skeletonTimeRemainingID = tempTimeRemainingID;
                            skeletonCount = tempCount;
                            skeletonFound = true;
                            //LOG BOSS INITIALIZE WITH INFO
                            LOGGER.info("---[SKELETON MINI INITIALIZED INFO]---");
                            LOGGER.info("BossID: " + skeletonBossID);
                            LOGGER.info("BossUUID: " + skeletonBossUUID);
                            LOGGER.info("NameID: " + skeletonNameID + " NameComponent: " + level.getEntity(skeletonNameID).getCustomName().getString());
                            LOGGER.info("TimeID: " + skeletonTimeRemainingID + " NameComponent: " + level.getEntity(skeletonTimeRemainingID).getCustomName().getString());
                            break;
                        }
                    }

                    //RESET ALL TRACKERS
                    piglinBossID = -1;
                    piglinBossUUID = null;
                    piglinNameID = -1;
                    piglinTimeRemainingID = -1;
                    piglinCount = 0;
                    piglinFound = false;

                    AABB areaPiglin = new AABB(player.getOnPos()).inflate(20);
                    List<Entity> nearbyZombifiedPiglin = level.getEntities(
                            player,
                            areaPiglin,
                            entity -> entity.getType() == EntityType.ZOMBIFIED_PIGLIN);

                    for(Entity entityPiglin : nearbyZombifiedPiglin)
                    {
                        int tempNameID = -1;
                        int tempTimeRemainingID = -1;
                        int tempBossID = entityPiglin.getId();
                        UUID tempBossUUID = entityPiglin.getUUID();
                        int tempCount = 0;

                        AABB areaArmorstand = new AABB(entityPiglin.blockPosition()).inflate(0d,3d,0d);
                        List<Entity> nearbyArmorStand = Minecraft.getInstance().level.getEntities(player,areaArmorstand, entity -> entity.getType() == EntityType.ARMOR_STAND);

                        for(Entity entityArmorstand : nearbyArmorStand)
                        {
                            Component customName = entityArmorstand.getCustomName();
                            int armorstandID = entityArmorstand.getId();
                            if(customName != null)
                            {
                                if(customName.getString().contains("ⓉⓎⓅⒽⓄⒺⓊⓈ"))
                                {
                                    tempNameID = armorstandID;
                                    tempCount++;
                                }
                                else if(customName.getString().matches(".*\\d+:\\d+.*"))
                                {
                                    tempTimeRemainingID = armorstandID;
                                    tempCount++;
                                }
                            }
                        }

                        if(tempNameID != -1 && tempTimeRemainingID != -1)
                        {
                            piglinBossID = tempBossID;
                            piglinBossUUID = tempBossUUID;
                            piglinNameID = tempNameID;
                            piglinTimeRemainingID = tempTimeRemainingID;
                            piglinCount = tempCount;
                            piglinFound = true;
                            //LOG BOSS INITIALIZE WITH INFO
                            LOGGER.info("---[PIGLIN MINI INITIALIZED INFO]---");
                            LOGGER.info("BossID: " + piglinBossID);
                            LOGGER.info("BossUUID: " + piglinBossUUID);
                            LOGGER.info("NameID: " + piglinNameID + " NameComponent: " + level.getEntity(piglinNameID).getCustomName().getString());
                            LOGGER.info("TimeID: " + piglinTimeRemainingID + " NameComponent: " + level.getEntity(piglinTimeRemainingID).getCustomName().getString());
                            break;
                        }
                    }
                }

            }
        }
    }

    @SubscribeEvent
    private static void renderGui(RenderGuiEvent.Post event)
    {
        if(!SLAYER_ACTIVE.get()){return;}
        if(Minecraft.getInstance().level != null)
        {
            PlayerTeam team_10 = Minecraft.getInstance().level.getScoreboard().getPlayerTeam("team_10");
            boolean location10Found = false;
            if(team_10 != null)
            {
                String location10 = "Biome: " + team_10.getPlayerPrefix().getString() + team_10.getPlayerSuffix().getString();
                location10Found = LOCATIONS.stream().anyMatch(loc -> location10.contains(loc));
                if(location10Found)
                {
                    onCrimsonIsle = true;
                    stringBiome = location10;
                }
                else
                {
                    onCrimsonIsle = false;
                }
            }
            if(!location10Found)
            {
                PlayerTeam team_06 = Minecraft.getInstance().level.getScoreboard().getPlayerTeam("team_6");
                if(team_06 != null)
                {
                    String location06 = "Biome: " + team_06.getPlayerPrefix().getString() + team_06.getPlayerSuffix().getString();
                    boolean location06Found = LOCATIONS.stream().anyMatch(loc -> location06.contains(loc));
                    if(location06Found)
                    {
                        onCrimsonIsle = true;
                        stringBiome = location06;
                    }
                    else
                    {
                        onCrimsonIsle = false;
                    }
                }
            }

            if(trackingBoss)
            {
                if(blazeFound)
                {
                    event.getGuiGraphics().drawString(Minecraft.getInstance().font, String.format("Blaze # of ArmorStands Found: %d", blazeCount), 50, 50, 0xFFFFFF);
                    if(Minecraft.getInstance().level.getEntity(blazeNameID) != null)
                    {
                        if(Minecraft.getInstance().level.getEntity(blazeNameID).getCustomName() != null)
                        {
                            event.getGuiGraphics().drawString(Minecraft.getInstance().font, String.format("Name: %s", Minecraft.getInstance().level.getEntity(blazeNameID).getCustomName().getString()), 50, 60, 0xFFFFFF);
                        }
                        else
                        {
                            event.getGuiGraphics().drawString(Minecraft.getInstance().font, "Name: NULL Custom Name", 50, 60, 0xFFFFFF);
                        }
                    }
                    else
                    {
                        event.getGuiGraphics().drawString(Minecraft.getInstance().font, "Name: NULL Entity", 50, 60, 0xFFFFFF);
                    }
                    if(Minecraft.getInstance().level.getEntity(blazeTimeRemainingID) != null)
                    {
                        if(Minecraft.getInstance().level.getEntity(blazeTimeRemainingID).getCustomName() != null)
                        {
                            event.getGuiGraphics().drawString(Minecraft.getInstance().font, String.format("Time: %s", Minecraft.getInstance().level.getEntity(blazeTimeRemainingID).getCustomName().getString()), 50, 70, 0xFFFFFF);
                        }
                        else
                        {
                            event.getGuiGraphics().drawString(Minecraft.getInstance().font, "Time: NULL Custom Name", 50, 70, 0xFFFFFF);
                        }
                    }
                    else
                    {
                        event.getGuiGraphics().drawString(Minecraft.getInstance().font, "Time: NULL Entity", 50, 70, 0xFFFFFF);
                    }
                    if(Minecraft.getInstance().level.getEntity(blazeSpawnByID) != null)
                    {
                        if(Minecraft.getInstance().level.getEntity(blazeSpawnByID).getCustomName() != null)
                        {
                            event.getGuiGraphics().drawString(Minecraft.getInstance().font, String.format("Spawned by: %s", Minecraft.getInstance().level.getEntity(blazeSpawnByID).getCustomName().getString()), 50, 80, 0xFFFFFF);
                        }
                        else
                        {
                            event.getGuiGraphics().drawString(Minecraft.getInstance().font, "Spawned by: NULL Custom Name", 50, 80, 0xFFFFFF);
                        }
                    }
                    else
                    {
                        event.getGuiGraphics().drawString(Minecraft.getInstance().font, "Spawned by: NULL Entity", 50, 80, 0xFFFFFF);

                    }
                    event.getGuiGraphics().drawString(Minecraft.getInstance().font, String.format("BossID: %d", blazeBossID), 50, 90, 0xFFFFFF);
                    if(blazeBossUUID != null)
                    {
                        event.getGuiGraphics().drawString(Minecraft.getInstance().font, String.format("BossUUID: %s", blazeBossUUID.toString()), 50, 100, 0xFFFFFF);
                    }
                    else
                    {
                        event.getGuiGraphics().drawString(Minecraft.getInstance().font, "BossUUID: NULL UUID", 50, 100, 0xFFFFFF);
                    }
                    event.getGuiGraphics().drawString(Minecraft.getInstance().font, "Boss Tracking", 50, 110, 0xFFFFFF);
                }
                else
                {
                    event.getGuiGraphics().drawString(Minecraft.getInstance().font, String.format("Skeleton # of ArmorStands Found: %d", skeletonCount), 50, 50, 0xFFFFFF);
                    event.getGuiGraphics().drawString(Minecraft.getInstance().font, String.format("Piglin # of ArmorStands Found: %d", piglinCount), 50, 60, 0xFFFFFF);
                    if(skeletonFound)
                    {
                        if(Minecraft.getInstance().level.getEntity(skeletonNameID) != null)
                        {
                            if(Minecraft.getInstance().level.getEntity(skeletonNameID).getCustomName() != null)
                            {
                                event.getGuiGraphics().drawString(Minecraft.getInstance().font, String.format("Name: %s", Minecraft.getInstance().level.getEntity(skeletonNameID).getCustomName().getString()), 50, 70, 0xFFFFFF);
                            }
                            else
                            {
                                event.getGuiGraphics().drawString(Minecraft.getInstance().font, "Name: NULL Custom Name", 50, 70, 0xFFFFFF);
                            }
                        }
                        else
                        {
                            event.getGuiGraphics().drawString(Minecraft.getInstance().font, "Name: NULL Entity", 50, 70, 0xFFFFFF);
                        }
                    }

                    if(piglinFound)
                    {
                        if(Minecraft.getInstance().level.getEntity(piglinNameID) != null)
                        {
                            if(Minecraft.getInstance().level.getEntity(piglinNameID).getCustomName() != null)
                            {
                                event.getGuiGraphics().drawString(Minecraft.getInstance().font, String.format("Name: %s", Minecraft.getInstance().level.getEntity(piglinNameID).getCustomName().getString()), 50, 80, 0xFFFFFF);
                            }
                            else
                            {
                                event.getGuiGraphics().drawString(Minecraft.getInstance().font, "Name: NULL Custom Name", 50, 80, 0xFFFFFF);
                            }
                        }
                        else
                        {
                            event.getGuiGraphics().drawString(Minecraft.getInstance().font, "Name: NULL Entity", 50, 80, 0xFFFFFF);

                        }
                    }
                    event.getGuiGraphics().drawString(Minecraft.getInstance().font, "Mini Tracking", 50, 90, 0xFFFFFF);
                }
            }
        }
    }
}
