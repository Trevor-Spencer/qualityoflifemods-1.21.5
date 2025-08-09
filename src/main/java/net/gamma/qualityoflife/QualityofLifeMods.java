package net.gamma.qualityoflife;

import net.gamma.qualityoflife.keybinding.ModKeyBinding;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.lifecycle.ClientStoppingEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

import static net.gamma.qualityoflife.event.PestChatClientEvent.readPestPlotJson;
import static net.gamma.qualityoflife.event.PestChatClientEvent.writePestPlotJson;
import static net.gamma.qualityoflife.util.InventoryUtils.readIn;
import static net.gamma.qualityoflife.util.InventoryUtils.writeOut;
import static net.gamma.qualityoflife.widget.ManagerWidget.writeJsons;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(QualityofLifeMods.MOD_ID)
public class QualityofLifeMods
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "qualityoflifemods";
    public static final Logger LOGGER = LoggerFactory.getLogger(QualityofLifeMods.MOD_ID);
    public static final Path MODCONFIGFOLDER = FMLPaths.CONFIGDIR.get().resolve(MOD_ID);
    public static final boolean DEBUGMODE = false;

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public QualityofLifeMods(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (QualityofLifeMods) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.addListener(this::onClientClose);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    public void onClientClose(ClientStoppingEvent event)
    {
        writeOut("lockedSlots.json");
        writePestPlotJson();
        writeJsons();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            readIn("lockedSlots.json");
            readPestPlotJson();
        }

        @SubscribeEvent
        public static void registerKeyMapping(RegisterKeyMappingsEvent event)
        {
            ModKeyBinding.register(event);
        }
    }

}
