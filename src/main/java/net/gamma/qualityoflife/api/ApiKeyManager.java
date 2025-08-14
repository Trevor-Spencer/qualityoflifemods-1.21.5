package net.gamma.qualityoflife.api;

import java.io.*;
import java.util.Properties;

import static net.gamma.qualityoflife.QualityofLifeMods.LOGGER;

public class ApiKeyManager {
    private static String apiKey;
    private static final String filePath = "config/qualityoflifemods/api.properties";
    private static final String folderPath = "config/qualityoflifemods/";

    public static void loadKey()
    {
        File folder = new File(folderPath);
        File file = new File(filePath);

        try
        {
            if(!folder.exists())
            {
                folder.mkdirs();
                LOGGER.info(String.format("[QUALITYOFLIFE] Created config folder at %s", folderPath));
            }

            if(!file.exists())
            {
                Properties properties =  new Properties();
                properties.setProperty("API_KEY", "your_api_key_here");
                try(FileOutputStream fileOutputStream = new FileOutputStream(filePath))
                {
                    properties.store(fileOutputStream, "API Key for QUALITYOFLIFEMODS");
                }
                LOGGER.warn("[QUALITYOFLIFE] Created api.properties, add API KEY");
            }

            Properties properties = new Properties();
            try(FileInputStream fileInputStream = new FileInputStream(filePath))
            {
                properties.load(fileInputStream);
            }

            apiKey = properties.getProperty("API_KEY");
            if(apiKey == null || apiKey.isBlank())
            {
                LOGGER.warn("[QUALITYOFLIFE] API key is missing or empty in api.properties");
            }
        } catch (Exception e) {
            LOGGER.error("[QUALITYOFLIFE] Error loading or creating API key file");
        }
    }

    public static String getApiKey()
    {
        return apiKey;
    }
}
