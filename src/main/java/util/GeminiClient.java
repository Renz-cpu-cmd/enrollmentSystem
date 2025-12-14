
package util;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeminiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeminiClient.class);

    private static final String PROJECT_ID = Config.getEnvOrProperty("gemini.projectId", null);
    private static final String LOCATION = Config.getEnvOrProperty("gemini.location", null);
    private static final String MODEL_NAME = Config.getEnvOrProperty("gemini.model", "gemini-2.5-pro");

    private static GenerativeModel model;

    static {
        if (PROJECT_ID == null || LOCATION == null) {
            LOGGER.warn("Gemini client disabled: missing projectId/location. Configure gemini.projectId and gemini.location via env or config.properties.");
        } else {
            try (VertexAI vertexAI = new VertexAI(PROJECT_ID, LOCATION)) {
                model = new GenerativeModel(MODEL_NAME, vertexAI);
                LOGGER.info("Gemini client initialized for project {} in {} using model {}", PROJECT_ID, LOCATION, MODEL_NAME);
            } catch (Exception e) {
                LOGGER.error("Failed to initialize Gemini client", e);
            }
        }
    }

    public static GenerativeModel getModel() {
        if (model == null) {
            throw new IllegalStateException("Gemini model is not initialized. Configure gemini.projectId and gemini.location.");
        }
        return model;
    }

    public static boolean isAvailable() {
        return model != null;
    }
}
