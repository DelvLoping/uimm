import java.io.IOException;

public class Program extends AProgram {
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
             WriteFile("output/profiles/"+agent.getFileName("html"),GenerateProfil(agent));

         }catch(Exception e){
             System.out.println(e);
         }
     }
    }

    public static void main(String[] args) throws IOException {
        // On va chercher les agents
        var agents = GetAgents();

        // Création du index.html
        var indexFileContent = GenerateIndexView(agents);
        WriteFile("output/index.html", indexFileContent);

        // Création des profiles
        // TODO: Florent
        GenerateProfils(agents);
    }
}
