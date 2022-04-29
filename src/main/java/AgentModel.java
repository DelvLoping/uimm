import java.util.List;

/**
 * Objet représentant un agent
 */
public class AgentModel {
 public String firstName;
 public String lastName;
 public String metier;
 public String img;
 public List<MaterialModel> materials;

 public AgentModel(String firstN, String lastNe, String met, String im, List<MaterialModel> mt){
  firstName=firstN;
  lastName=lastNe;
  metier = met;
  img=im;
  materials=mt;

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



