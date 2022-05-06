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

public abstract class AAgentService {
    protected Logger _logger;
    protected String _headFile, _indexFile, _profileFile, _materialFile;
    protected final String RESOURCE_DIRECTORY = "src/main/resources";
    protected final String PRAVATAR_URL = "https://i.pravatar.cc/300";
    private MessageDigest _md;

    public AAgentService() throws IOException, NoSuchAlgorithmException {
        _headFile = GetResourceByName("head.html");
        _indexFile = GetResourceByName("index.html");
        _profileFile = GetResourceByName("Profil.html");
        _materialFile = GetResourceByName("Material.html");
        _md = MessageDigest.getInstance("SHA-1");
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
    protected String GenerateIndexView(AgentModel[] agents) throws IOException {
        _logger.info(String.format("Génération de la vue index pour %d agents", agents.length));

        var stringBuilder = new StringBuilder();

        for (var agent : agents) {
            // Ancienne version
            //final var template = "<li><a href='profiles/%s'>%s</a></li>";

            // Nouvelle version avec BS
            final var template = "<a class=\"list-group-item list-group-item-action\" href='profiles/%s'>%s</a>";
            stringBuilder.append(String.format(template, agent.getFileName("html"), agent.getFullName()));
        }

        return _indexFile.replace("$elements", stringBuilder.toString()).replace("$head", _headFile);
    }

    /**
     * Génération d'un profile
     */
    protected String GenerateProfile(AgentModel agent) throws IOException {
        _logger.info(String.format("Génération de la vue d'un profile pour l'agent %s", agent.getFullName()));

        var stringBuilder = new StringBuilder();

        var profileFile = _profileFile.replace("$fullName", agent.getFullName()).replace("$img", agent.imageUrl).replace("$metier", agent.job);

        for (var materiel : agent.materials) {
            final var template = "<div style=\"display: flex;align-items: center; flex-direction: row;\"><input type='checkbox' %s><a href=\"materials/%s\">%s</a></div>";
            stringBuilder.append(String.format(template, materiel.value ? "checked disabled" : "disabled", materiel.getFileName("html"), materiel.label));
        }

        return profileFile.replace("$materials", stringBuilder.toString()).replace("$head", _headFile);
    }

    /**
     * Génération de tout les profiles
     */
    protected void GenerateProfiles(AgentModel[] agents) throws IOException {
        for (var agent : agents) {
            WriteFile(String.format("output/profiles/%s", agent.getFileName("html")), GenerateProfile(agent));
        }
    }

    /**
     * Méthode qui permet de générer le fichier .htpasswd à partir d'une liste d'identifiants utilisateur/mot de passe
     */
    protected String GenerateHtPasswd(Map<String, String> credentials) throws NoSuchAlgorithmException {
        var result = new StringBuilder();

        for (var cred : credentials.entrySet()) {
            var passwordBytes = cred.getValue().getBytes();
            _md.update(passwordBytes);
            var digest = _md.digest();
            var passwordHash = Base64.getEncoder().encodeToString(digest);
            result.append(String.format("%s:{SHA}%s\n", cred.getKey(), passwordHash));
        }

        return result.toString();
    }

    /**
     * Méthode qui permet de générer la liste des identifiants à partir d'une liste d'agent
     */
    protected Map<String, String> GeneratePasswords(AgentModel[] agents) throws IOException, NoSuchAlgorithmException {
        var credentials = new HashMap<String, String>();

        for (var agent : agents) {
            credentials.put(agent.firstName.toLowerCase().charAt(0) + agent.lastName.toLowerCase(), agent.password);
        }

        return credentials;

        //var content = GenerateHtPasswd(credentials);
        //WriteFile("output/.htpasswd", content);
    }

    /**
     * Génération d'une vue materiel
     */
    protected String GenerateMaterialView(String label, AgentModel[] agents) throws IOException {
        _logger.info(String.format("Génération d'une vue materiel (%s) pour %d agents", label, agents.length));

        var stringBuilder = new StringBuilder();
        var materialFile = _materialFile.replace("$label", label);

        for (var agent : agents) {
            final var template = "<div class=\"card\" style=\"margin: 10px;\"><div class=\"top-container\" style=\"margin-right: 20px;display: flex!important; justify-content: space-evenly!important;\"> <div class=\"ml-3\"><h5 class=\"name\">%s</h5><p class=\"mail\">%s</p></div><img src=\"%s\" class=\"img-fluid profile-image\" width=\"70\"></div></div>";
            stringBuilder.append(String.format(template, agent.getFullName(), agent.job, agent.imageUrl));
        }

        return materialFile.replace("$agents", stringBuilder.toString()).replace("$head", _headFile);
    }

    /**
     * Génération de toutes les vues materiel
     */
    protected void GenerateMaterialsView(AgentModel[] agents) throws IOException {
        _logger.info(String.format("Génération des vues des materiaux pour %d agents", agents.length));

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
                    _logger.info(String.format("Nombre d'agent %d", agentListe.size()));
                    try {
                        var fileName = words[1].toLowerCase().replaceAll("[^a-z]+","")+".html";
                        WriteFile("output/materials/" + fileName, GenerateMaterialView(words[1], agentListe.toArray(AgentModel[]::new)));
                        _logger.info(String.format("Fichier %s sauvegardé", fileName));
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
