import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 * Classe principale
 */
public class Program {

    /**
     * Point d'entrée
     * */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        // Instanciation du logger
        var logger = Logger.getLogger("Agent");

        // Instanciation du service de gestion des agents (utilisant les fichiers txt OU le fichier json)
        var agentService = new AgentService(logger);
        //var agentService = new AgentJsonService(logger);

        // On va chercher les agents
        var agents = agentService.GetAgents();
        agentService.GeneratePasswords(agents);
        agentService.GenerateMaterialsView(agents);

        // Création du index.html
        var indexFileContent = agentService.GenerateIndexView(agents);
        agentService.WriteFile("output/index.html", indexFileContent);

        // Création des profiles
        agentService.GenerateProfiles(agents);

        // Copie du fichier des styles
        agentService.WriteFile("output/style.css", agentService.GetResourceByName("style.css"));
    }
}
