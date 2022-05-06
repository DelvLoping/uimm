import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Logger;

public class AAgentService {
    protected Logger logger;
    protected String headFile, indexFile, profileFile, materialFile;
    protected final String RESOURCE_DIRECTORY = "src/main/resources";
    protected final String PRAVATAR_URL = "https://i.pravatar.cc/300";

    public AAgentService() throws IOException {
        headFile = GetResourceByName("head.html");
        indexFile = GetResourceByName("index.html");
        profileFile = GetResourceByName("Profil.html");
        materialFile = GetResourceByName("Material.html");
    }

    /**
     * Permet de retourner le contenu d'un fichier resource sous forme de chaine de caractères
     *
     * @param viewName Le nom du fichier concerné
     */
    public String GetResourceByName(String viewName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(viewName);
        return new String(input.readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Génère le fichier index.html qui permet la navigation entre les différents profiles
     */
    protected String GenerateIndexView(AgentModel[] viewModel) throws IOException {
        var htmlBuilder = new HtmlBuilder();

        for (var model : viewModel) {
            // Ancienne version
            // htmlBuilder.append(String.format("<li><a href='profiles/"+model.getFileName("html")+"'>%s</a></li>", model.getFullName()));

            // Nouvelle version avec BS
            htmlBuilder.append(String.format("<a class=\"list-group-item list-group-item-action\" href='profiles/" + model.getFileName("html") + "'>%s</a>", model.getFullName()));
        }

        indexFile = indexFile.replace("$elements", htmlBuilder.toString());
        indexFile = indexFile.replace("$head", headFile);
        return indexFile;
    }

    /**
     * Génération d'un profile
     */
    protected String GenerateProfile(AgentModel agent) throws IOException {
        var htmlBuilder = new HtmlBuilder();
        profileFile = profileFile.replace("$fullName", agent.getFullName());
        profileFile = profileFile.replace("$img", agent.img);
        profileFile = profileFile.replace("$metier", agent.metier);

        for (var materiel : agent.materials)
            htmlBuilder.append(String.format("<div style=\"display: flex;align-items: center; flex-direction: row;\"><input type='checkbox' %s><a href=\"materials/%s\">%s</a></div>", materiel.value ? "checked disabled" : "disabled",materiel.getFileName("html") ,materiel.label));


        profileFile = profileFile.replace("$materials", htmlBuilder.toString());
        profileFile = profileFile.replace("$head", headFile);
        return profileFile;
    }

    /**
     * Génération de tout les profiles
     */
    protected void GenerateProfiles(AgentModel[] viewModel) throws IOException {
        for (var agent : viewModel) {
            WriteFile("output/profiles/" + agent.getFileName("html"), GenerateProfile(agent));
        }
    }

    protected String GenerateHtPasswd(Map<String, String> credentials) throws NoSuchAlgorithmException {
        var result = "";
        MessageDigest md = MessageDigest.getInstance("SHA-1");

        for (var cred : credentials.entrySet()) {
            var passwordBytes = cred.getValue().getBytes();
            md.update(passwordBytes);
            var digest = md.digest();
            var passwordHash = Base64.getEncoder().encodeToString(digest);
            result += String.format("%s:{SHA}%s\n", cred.getKey(), passwordHash);
        }

        return result;
    }

    protected void GeneratePasswords(AgentModel[] agents) throws IOException, NoSuchAlgorithmException {
        var crendentials = new HashMap<String, String>();
        for (var agent : agents) {
            crendentials.put(agent.firstName.toLowerCase().charAt(0) + agent.lastName.toLowerCase(), agent.mdp);
        }
        var content = GenerateHtPasswd(crendentials);
        WriteFile("output/.htpasswd", content);
    }

    protected String GenerateMaterialView(String label,AgentModel[] agents) throws IOException {
        var htmlBuilder = new HtmlBuilder();
        materialFile = materialFile.replace("$label", label);

        for (var agent : agents) {
            htmlBuilder.append(String.format("<div class=\"card\" style=\"margin: 10px;\"><div class=\"top-container\" style=\"margin-right: 20px;display: flex!important; justify-content: space-evenly!important;\"> <div class=\"ml-3\"><h5 class=\"name\">%s</h5><p class=\"mail\">%s</p></div><img src=\"%s\" class=\"img-fluid profile-image\" width=\"70\"></div></div>",agent.getFullName(), agent.metier,agent.img));
        }

        materialFile = materialFile.replace("$agents", htmlBuilder.toString());
        materialFile = materialFile.replace("$head", headFile);
        return materialFile;
    }

    /**
     * Génération de toutes les vues materiel
     */
    protected void GenerateMaterialsView(AgentModel[] agents) throws IOException {
        Files.walk(FileSystems.getDefault().getPath(RESOURCE_DIRECTORY)).filter(fileliste -> fileliste.toString().endsWith("liste.txt")).forEach(liste -> {
            try {
                Files.lines(liste).forEach(tool -> {
                    List<AgentModel> agentListe = new ArrayList<AgentModel>();
                    String[] words = tool.split("    ");
                    for (var agent : agents) {
                        for (var material : agent.materials) {
                            if (material.value&& words[1].equals(material.label)) {
                                agentListe.add(agent);
                            }
                        }
                    }
                    try {
                        WriteFile("output/materials/" + words[1].toLowerCase().replaceAll("[^a-z]+","")+".html", GenerateMaterialView(words[1],agentListe.toArray(AgentModel[]::new)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Ecris un fichier sur le disque
     */
    protected void WriteFile(String fileName, String content) throws IOException {
        Files.write(Paths.get(fileName), content.getBytes(StandardCharsets.UTF_8));
    }
}
