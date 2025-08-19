package net.gamma.qualityoflife.util;

import net.hypixel.api.apache.ApacheHttpClient;

import java.util.UUID;

import static net.gamma.qualityoflife.QualityofLifeMods.LOGGER;
import static net.gamma.qualityoflife.api.ApiKeyManager.getApiKey;
import static net.gamma.qualityoflife.util.JsonReaders.mayorReader;

public class ApiUtils {

    public static void pollMayorApi()
    {
        ApacheHttpClient client = new ApacheHttpClient(UUID.fromString(getApiKey()));
        client.makeRequest("https://api.hypixel.net/v2/resources/skyblock/election")
                .whenComplete((hypixelHttpResponse, throwable) -> {
                            if(throwable != null)
                            {
                                throwable.printStackTrace();
                            }
                            else
                            {
                                if(hypixelHttpResponse.getStatusCode() == 200)
                                {
                                    if(hypixelHttpResponse.getRateLimit() == null)
                                    {
                                        mayorReader(hypixelHttpResponse.getBody());
                                    }
                                }
                                else
                                {
                                    LOGGER.warn("[QUALITYOFLIFE] Error Retrieving Election Data");
                                }
                                client.shutdown();
                            }
                        }
                );
    }
    public static String removeQuotes(String givenText)
    {
        if(givenText == null){return null;}

        if(givenText.startsWith("\""))
        {
            givenText = givenText.substring(1);
        }
        if(givenText.endsWith("\""))
        {
            givenText = givenText.substring(0,givenText.length()-1);
        }
        return givenText;
    }
}
