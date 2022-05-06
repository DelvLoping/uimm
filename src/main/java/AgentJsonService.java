import com.google.gson.Gson;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Service de gestion des agents à partir du fichier JSON
 */
public class AgentJsonService extends AAgentService implements IAgentService {
    private Gson gson = new Gson();

    public AgentJsonService(Logger logger) throws IOException {
        this.logger = logger;
    }

    /**
     * Parse l'objet JSON et retourne la liste des agents
     */
    public AgentModel[] GetAgents() throws IOException {
        var jsonFileContent = GetResourceByName("data.json");
        return gson.fromJson(jsonFileContent, AgentModel[].class);
    }
}
