import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Classe principale
 */
public class Program extends AProgram {
    /**
     * Point d'entrée
     */
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

        AgentModel[] agentListe;

        Files.walk(FileSystems.getDefault().getPath("src\\main\\resources")).filter(file -> file.toString().endsWith(".txt") && !file.toString().endsWith("liste.txt") && !file.toString().endsWith("agent.txt")).forEach(file -> {
            System.out.println(file.toString());
            final int[] nbline = {0};
            try {
                List<MaterialModel> materials=null;
                Files.lines(file).forEach(line -> {
                    if (nbline[0] <= 4) {
                        System.out.println(line.toString());
                    } else {
                        try {
                            Files.walk(FileSystems.getDefault().getPath("src\\main\\resources")).filter(fileliste -> fileliste.toString().endsWith("liste.txt")).forEach(liste -> {
                                try {
                                    System.out.println(liste);
                                    Files.lines(liste).forEach(tool -> {
                                        String[] words = tool.split("    ");
                                        if(line.equals(words[0])){
                                            materials.add(new MaterialModel(words[1],true));
                                        }else {
                                            materials.add(new MaterialModel(words[1],false));
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    nbline[0]++;
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }


}

