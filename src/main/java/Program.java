import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

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
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        // On va chercher les agents
        /*
        var agents = GetAgents();

        // Création du index.html
        var indexFileContent = GenerateIndexView(agents);
        WriteFile("output/index.html", indexFileContent);

        // Création des profiles
        GenerateProfiles(agents);
        */

        var crendentials = new HashMap<String, String>();
        crendentials.put("jean1", "jean1");
        crendentials.put("jean2", "jean2");
        crendentials.put("jean3", "jean3");
        var content = GenerateHtPasswd(crendentials);
        WriteFile("output/.htpasswd", content);

        Files.walk(FileSystems.getDefault().getPath("src\\main\\resources")).filter(p -> p.toString().endsWith(".txt")).forEach(p -> {
            System.out.println(p.toString());
        });

    }
}
