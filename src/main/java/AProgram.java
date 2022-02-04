import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class AProgram {
    protected static Gson gson = new Gson();

    protected static String GetResourceByName(String viewName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(viewName);
        return new String(input.readAllBytes(), StandardCharsets.UTF_8);
    }

    protected static String GenerateIndexView(AgentModel[] viewModel) throws IOException {
        var indexFile = GetResourceByName("index.html");

        var htmlBuilder = new HtmlBuilder();


        //viewModel.forEach(agentModel -> {});
        for (var model: viewModel)
            htmlBuilder.append(String.format("<li>%s %s</li>", model.firstName, model.lastName));

        indexFile = indexFile.replace("$elements", htmlBuilder.toString());
        return indexFile;
    }

    protected static AgentModel[] GetAgents() throws IOException {
        var jsonFileContent = GetResourceByName("data.json");
        return gson.fromJson(jsonFileContent, AgentModel[].class);
    }

    protected static void WriteFile(String fileName, String content) throws IOException {
        Files.write(Paths.get(fileName), content.getBytes(StandardCharsets.UTF_8));
    }
}
