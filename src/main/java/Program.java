import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Map;

/**
 * Classe principale
 */
public class Program extends AProgram {


    /*public static Map<String, String> GetDirectoryFiles(String directory, String filter)
    {
        return;
    }*/

    /**
     * Point d'entrée
     * */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        headFile = GetResourceByName("head.html");

        // On va chercher les agents
        var agents = GetAgents();

        // Création du index.html
        var indexFileContent = GenerateIndexView(agents);
        WriteFile("output/index.html", indexFileContent);

        // Création des profiles
        GenerateProfiles(agents);
    }
}
