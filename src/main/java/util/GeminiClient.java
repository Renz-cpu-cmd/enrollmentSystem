package util;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.GenerativeModel;

public class GeminiClient {

    private static final String PROJECT_ID = System.getenv("GEMINI_PROJECT_ID");
    private static final String LOCATION = System.getenv("GEMINI_LOCATION");
    private static final String MODEL_NAME = "gemini-2.5-pro";

    private static GenerativeModel model;

    static {
        if (PROJECT_ID == null || LOCATION == null) {
            System.err.println("GEMINI_PROJECT_ID and GEMINI_LOCATION environment variables must be set.");
        } else {
            try (VertexAI vertexAI = new VertexAI(PROJECT_ID, LOCATION)) {
                model = new GenerativeModel(MODEL_NAME, vertexAI);
            }
        }
    }

    public static GenerativeModel getModel() {
        if (model == null) {
            throw new IllegalStateException("Gemini model is not initialized. Check environment variables.");
        }
        return model;
    }
}
