import java.util.List;

/**
 * Objet représentant un agent
 */
public class AgentModel {
    public String firstName;
    public String lastName;
    public String job;
    public String imageUrl;
    public String password;
    public List<MaterialModel> materials;

    public AgentModel(String _firstName, String _lastName, String _job, String _imageUrl, String _password, List<MaterialModel> _materials) {
      firstName =_firstName;
      lastName =_lastName;
      job = _job;
      imageUrl = _imageUrl;
      password = _password;
      materials = _materials;
    }

    /**
      * Fonction qui retourne un nom de fichier unique à cet agent
      * @param extension Extenstion du fichier
      */
    public String getFileName(String extension) {
      return String.format("%s_%s.%s", lastName.toLowerCase(), firstName.toLowerCase(), extension);
    }

    /**
      * Fonction qui retourne le nom complet de l'agent
      */
    public String getFullName() {
      return String.format("%s %s", lastName.toUpperCase(), firstName);
    }
}
