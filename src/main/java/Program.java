import java.io.IOException;

public class Program extends AProgram {
    public static void main(String[] args) throws IOException {
        // On va chercher les agents
        var agents = GetAgents();

        // Création du index.html
        var indexFileContent = GenerateIndexView(agents);
        WriteFile("output/index.html", indexFileContent);

        // Création des profiles
        // TODO: Florent
    }
}
