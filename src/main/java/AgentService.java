import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/*
 * Service de gestion des agents à partir des fichiers txt
 */
public class AgentService extends AAgentService implements IAgentService {

    public AgentService(Logger logger) throws IOException {
        this.logger = logger;
    }

    /**
     * Parse les fichiers txt et retourne la liste des agents
     */
    public AgentModel[] GetAgents() throws IOException {
        List<AgentModel> agentList = new ArrayList<AgentModel>();

        Files.walk(FileSystems.getDefault().getPath(RESOURCE_DIRECTORY)).filter(file -> file.toString().endsWith(".txt") && !file.toString().endsWith("liste.txt") && !file.toString().endsWith("agent.txt")).forEach(file -> {

        });

        Files.walk(FileSystems.getDefault().getPath(RESOURCE_DIRECTORY)).filter(file -> file.toString().endsWith(".txt") && !file.toString().endsWith("liste.txt") && !file.toString().endsWith("agent.txt")).forEach(file -> {
            //System.out.println(file.toString());

            List<MaterialModel> materials = new ArrayList<MaterialModel>();

            final String[] ft = {""};
            final String[] lt = {""};
            final String[] met = {""};
            final String[] mot = {""};

            List<String> tools = new ArrayList<String>();
            final int[] nbline = {0};

            try {
                Files.lines(file).forEach(line -> {
                    if (nbline[0] <= 4) {
                        switch (nbline[0]) {
                            case 0:
                                ft[0] = line;
                                break;
                            case 1:
                                lt[0] = line;
                                break;
                            case 2:
                                met[0] = line;
                                break;
                            case 3:
                                mot[0] = line;
//                                var name =file.getFileName().toString().split("[.]")[0];
//                                crendentials.put(name, line);
                                break;
                            case 4:
                                break;
                        }
                    } else {
                        tools.add(line);
                    }
                    nbline[0]++;
                });

/*                var liste = this.GetResourceByName("liste.txt");

                var listLines = liste.split("(\\r\\n|\\r|\\n)");

                for (var line: listLines) {
                    final String[] labelle = {""};
                    final boolean[] existe = {false};
                    String[] words = line.split("    ");
                    labelle[0] = words[1];
                    tools.forEach(t -> {
                        if (t.equals(words[0])) {
                            existe[0] = true;
                        }
                    });
                    materials.add(new MaterialModel(labelle[0], existe[0]));
                }*/

                Files.walk(FileSystems.getDefault().getPath(RESOURCE_DIRECTORY)).filter(fileliste -> fileliste.toString().endsWith("liste.txt")).forEach(liste -> {
                    try {
                        Files.lines(liste).forEach(tool -> {
                            final String[] labelle = {""};
                            final boolean[] existe = {false};
                            String[] words = tool.split("    ");
                            labelle[0] = words[1];
                            tools.forEach(t -> {
                                if (t.equals(words[0])) {
                                    existe[0] = true;
                                }
                            });
                            materials.add(new MaterialModel(labelle[0], existe[0]));
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                agentList.add(new AgentModel(ft[0], lt[0], met[0], PRAVATAR_URL, mot[0], materials));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        //System.out.println(agentList);

        return agentList.toArray(AgentModel[]::new);
    }

}
