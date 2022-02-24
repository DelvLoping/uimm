import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Classe abstraite de base du programme
 * */
public abstract class AProgram {
    protected static Gson gson = new Gson();

    /**
     * Permet de retourner le contenu d'un fichier resource sous forme de chaine de caractères
     * @param viewName Le nom du fichier concerné
     * */
    private static String GetResourceByName(String viewName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(viewName);
        return new String(input.readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Génère le fichier index.html qui permet la navigation entre les différents profiles
     * */
    protected static String GenerateIndexView(AgentModel[] viewModel) throws IOException {
        var indexFile = GetResourceByName("index.html");

        var htmlBuilder = new HtmlBuilder();

        for (var model: viewModel)
            htmlBuilder.append(String.format("<li><a href='profiles/"+model.getFileName("html")+"'>%s</a></li>", model.getFullName()));

        indexFile = indexFile.replace("$elements", htmlBuilder.toString());
        return indexFile;
    }

    /**
     * Génération d'un profile
     * */
    protected static String GenerateProfile(AgentModel agent) throws IOException {
        var indexFile = GetResourceByName("Profil.html");

        var htmlBuilder = new HtmlBuilder();
        indexFile = indexFile.replace("$fullName", agent.getFullName());
        indexFile = indexFile.replace("$img", agent.img);

        for (var materiel: agent.materials)
            htmlBuilder.append(String.format("<label>%s</label><input type='checkbox' %s>", materiel.label, materiel.value ? "checked disabled" : "disabled"));

        indexFile = indexFile.replace("$materials", htmlBuilder.toString());
        return indexFile;
    }

    /**
     * Génération de tout les profiles
     * */
    protected static void GenerateProfiles(AgentModel[] viewModel) throws IOException {
        for (var agent: viewModel) {
            WriteFile("output/profiles/" + agent.getFileName("html"), GenerateProfile(agent));
        }
    }

    /**
     * Parse l'objet JSON et retourne la liste des agents
     * */
    protected static AgentModel[] GetAgents() throws IOException {
        var jsonFileContent = GetResourceByName("data.json");
        return gson.fromJson(jsonFileContent, AgentModel[].class);
    }

    /**
     * Ecris un fichier sur le disque
     * */
    protected static void WriteFile(String fileName, String content) throws IOException {
        Files.write(Paths.get(fileName), content.getBytes(StandardCharsets.UTF_8));
    }
}
