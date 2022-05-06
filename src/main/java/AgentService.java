import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/*
 * Service de gestion des agents à partir des fichiers txt
 */
public class AgentService extends AAgentService implements IAgentService {

    public AgentService(Logger logger) throws IOException, NoSuchAlgorithmException {
        this._logger = logger;
    }

    /**
     * Parse les fichiers txt et retourne la liste des agents
     */
    public AgentModel[] GetAgents() throws IOException {
        List<AgentModel> agentList = new ArrayList<AgentModel>();

        Files.walk(FileSystems.getDefault().getPath(RESOURCE_DIRECTORY)).filter(file -> file.toString().endsWith(".txt") && !file.toString().endsWith("liste.txt") && !file.toString().endsWith("agent.txt")).forEach(file -> {
            List<MaterialModel> materials = new ArrayList<MaterialModel>();

            var firstName = new AtomicReference<String>();
            var lastName = new AtomicReference<String>();
            var materiel = new AtomicReference<String>();
            var mot = new AtomicReference<String>();
            var nbline = new AtomicReference<Integer>(0);
            List<String> tools = new ArrayList<String>();

            try {
                Files.lines(file).forEach(line -> {
                    if (nbline.get() <= 4) {
                        switch (nbline.get()) {
                            case 0:
                                firstName.set(line);
                                break;
                            case 1:
                                lastName.set(line);
                                break;
                            case 2:
                                materiel.set(line);
                                break;
                            case 3:
                                mot.set(line);
                                break;
                            case 4:
                                break;
                        }
                    } else {
                        tools.add(line);
                    }
                    nbline.set(nbline.get() + 1);
                });

                var liste = this.GetResourceByName("liste.txt");

                for (var line: liste.split("(\\r\\n|\\r|\\n)")) {
                    var exist = new AtomicBoolean(false);
                    var words = line.split("    ");
                    var label = words[1];

                    tools.forEach(t -> {
                        if (t.equals(words[0])) {
                            exist.set(true);
                        }
                    });

                    materials.add(new MaterialModel(label, exist.get()));
                }

                agentList.add(new AgentModel(firstName.get(), lastName.get(), materiel.get(), PRAVATAR_URL, mot.get(), materials));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return agentList.toArray(AgentModel[]::new);
    }
}
