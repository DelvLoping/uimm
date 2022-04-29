import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Classe principale
 */
public class Program extends AProgram {

    public Program() throws IOException {
        headFile = GetResourceByName("head.html");
    }

    /**
     * Point d'entrée
     * */
    public static void main(String[] args) throws IOException {
        // On va chercher les agents
        /*
        var agents = GetAgents();

        // Création du index.html
        var indexFileContent = GenerateIndexView(agents);
        WriteFile("output/index.html", indexFileContent);

        // Création des profiles
        GenerateProfiles(agents);
        */
        Files.walk(FileSystems.getDefault().getPath("src\\main\\resources")).filter(p -> p.toString().endsWith(".txt")).forEach(p -> {
            System.out.println(p.toString());
        });

    }
}
