import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Classe abstraite de base du programme
 * */
public abstract class AProgram {
    protected static Gson gson = new Gson();
    protected static String headFile;

    private static String RESOURCE_DIRECTORY = "src/main/resources"; // "src\\main\\resources"

    /**
     * Permet de retourner le contenu d'un fichier resource sous forme de chaine de caractères
     * @param viewName Le nom du fichier concerné
     * */
    public static String GetResourceByName(String viewName) throws IOException {
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
        {
            // Ancienne version
            // htmlBuilder.append(String.format("<li><a href='profiles/"+model.getFileName("html")+"'>%s</a></li>", model.getFullName()));

            // Nouvelle version avec BS
            htmlBuilder.append(String.format("<a class=\"list-group-item list-group-item-action\" href='profiles/"+model.getFileName("html")+"'>%s</a>", model.getFullName()));
        }

        indexFile = indexFile.replace("$elements", htmlBuilder.toString());
        indexFile = indexFile.replace("$head", headFile);
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
        indexFile = indexFile.replace("$metier", agent.metier);

        for (var materiel: agent.materials)
            htmlBuilder.append(String.format("<div style=\"display: flex;align-items: center; flex-direction: row;\"><input type='checkbox' %s><label>%s</label></div>", materiel.value ? "checked disabled" : "disabled",materiel.label));

        indexFile = indexFile.replace("$materials", htmlBuilder.toString());
        indexFile = indexFile.replace("$head", headFile);
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

    protected static String GenerateHtPasswd(Map<String, String> credentials) throws NoSuchAlgorithmException {
        var result = "";
        MessageDigest md = MessageDigest.getInstance("SHA-1");

        for (var cred: credentials.entrySet())
        {
            var passwordBytes = cred.getValue().getBytes();
            md.update(passwordBytes);
            var digest = md.digest();
            var passwordHash = Base64.getEncoder().encodeToString(digest);
            result += String.format("%s:{SHA}%s\n", cred.getKey(), passwordHash);
        }

        return result;
    }

    /**
     * Parse l'objet JSON et retourne la liste des agents
     */
    protected static AgentModel[] GetAgents() throws IOException, NoSuchAlgorithmException {

        var crendentials = new HashMap<String, String>();

        List<AgentModel> agentListe=new ArrayList<AgentModel>();

        Files.walk(FileSystems.getDefault().getPath(RESOURCE_DIRECTORY)).filter(file -> file.toString().endsWith(".txt") && !file.toString().endsWith("liste.txt") && !file.toString().endsWith("agent.txt")).forEach(file -> {
            System.out.println(file.toString());
            List<MaterialModel> materials= new ArrayList<MaterialModel>();
            final String[] ft = {""};
            final String[] lt = {""};
            final String[] met = {""};
            List<String> tools= new ArrayList<String>();
            String img="https://i.pravatar.cc/300";
            final int[] nbline = {0};
            try {
                Files.lines(file).forEach(line -> {
                    if (nbline[0] <= 4) {
                        switch (nbline[0]){
                            case 0:
                                ft[0] =line;
                                break;
                            case 1:
                                lt[0] =line;
                                break;
                            case 2:
                                met[0] =line;
                                break;
                            case 3:
                                var name =file.getFileName().toString().split("[.]")[0];
                                crendentials.put(name, line);
                                break;
                            case 4:
                                break;
                        }
                    } else {
                        tools.add(line);
//                            Files.walk(FileSystems.getDefault().getPath("src\\main\\resources")).filter(fileliste -> fileliste.toString().endsWith("liste.txt")).forEach(liste -> {
//                                final String[] labelle = {""};
//                                final boolean[] existe = {false};
//                                try {
//                                    System.out.println(liste);
//                                    Files.lines(liste).forEach(tool -> {
//                                        String[] words = tool.split("    ");
//                                        if(line.equals(words[0])){
//                                            labelle[0] =words[1];
//                                            existe[0] =true;
//                                        }
//                                    });
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                                materials.add(new MaterialModel(labelle[0], existe[0]));
//                            });

                    }
                    nbline[0]++;
                });
                Files.walk(FileSystems.getDefault().getPath(RESOURCE_DIRECTORY)).filter(fileliste -> fileliste.toString().endsWith("liste.txt")).forEach(liste -> {
                    try {
                        Files.lines(liste).forEach(tool -> {
                            final String[] labelle = {""};
                            final boolean[] existe = {false};
                            String[] words = tool.split("    ");
                            labelle[0] =words[1];
                            tools.forEach(t ->{
                                if(t.equals(words[0])){
                                    existe[0] =true;
                                }
                            });
                            materials.add(new MaterialModel(labelle[0], existe[0]));
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                AgentModel agent= new AgentModel(ft[0], lt[0], met[0],img,materials);
                agentListe.add(agent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println(agentListe);
        var content = GenerateHtPasswd(crendentials);
        WriteFile("output/.htpasswd", content);
        return agentListe.toArray(AgentModel[]::new);
    }

    /**
     * Ecris un fichier sur le disque
     * */
    protected static void WriteFile(String fileName, String content) throws IOException {
        Files.write(Paths.get(fileName), content.getBytes(StandardCharsets.UTF_8));
    }
}
