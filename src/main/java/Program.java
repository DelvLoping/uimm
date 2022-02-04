import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Program {
    private static Gson gson = new Gson();

    private static String GetResourceByName(String viewName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(viewName);
        return new String(input.readAllBytes(), StandardCharsets.UTF_8);
    }

    private static String GenerateIndexView(AgentModel[] viewModel) throws IOException {
        var indexFile = GetResourceByName("index.html");

        var htmlBuilder = new HtmlBuilder();


        //viewModel.forEach(agentModel -> {});
        for (var model: viewModel)
            htmlBuilder.append(String.format("<li>%s %s</li>", model.firstName, model.lastName));

        indexFile = indexFile.replace("$elements", htmlBuilder.toString());
        return indexFile;
    }

    private static AgentModel[] GetAgents() throws IOException {
        var jsonFileContent = GetResourceByName("data.json");
        return gson.fromJson(jsonFileContent, AgentModel[].class);
    }

    private static void WriteFile(String fileName, String content) {
        //Files.write(Paths.get(fileName), content.getBytes(StandardCharsets.UTF_8));
    }


    private static String GenerateProfil(AgentModel agent)throws IOException {
        var indexFile = GetResourceByName("Profil.html");
        var htmlBuilder = new HtmlBuilder();
        indexFile = indexFile.replace("$firstName",agent.firstName);
        indexFile = indexFile.replace("$lastName",agent.lastName);
        indexFile = indexFile.replace("$img",agent.img);
        for (var materiel: agent.materials)
            htmlBuilder.append(String.format("<input type='checkbox' checked=%b><label>%s</label>",materiel.value, materiel.label));

        indexFile = indexFile.replace("$materials", htmlBuilder.toString());
        return indexFile;
    }
    private static void GenerateProfils(AgentModel[] viewModel) {
     for (var agent: viewModel){
         try {
             System.out.println(GenerateProfil(agent));

         }catch(Exception e){
             System.out.println(e);
         }
     }
    }

    public static void main(String[] args) throws IOException {
        var agents = GetAgents();

        var indexFileContent = GenerateIndexView(agents);

        GenerateProfils(agents);


        System.out.println(indexFileContent);

    }
}
