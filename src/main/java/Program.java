import java.io.IOException;

/**
 * Classe principale
 */
public class Program extends AProgram {
    /**
     * Point d'entrée
     * */
    public static void main(String[] args) throws IOException {
        // On va chercher les agents
        var agents = GetAgents();

        // Création du index.html
        var indexFileContent = GenerateIndexView(agents);
        WriteFile("output/index.html", indexFileContent);

        // Création des profiles
        GenerateProfiles(agents);
    }
}
