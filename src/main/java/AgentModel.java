import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class AgentModel {
 public String firstName;
 public String lastName;
 public String img;
 public List<MaterialModel> materials;

 public String getFileName(String extension) {
  return String.format("%s_%s.%s", lastName.toLowerCase(), firstName.toLowerCase(), extension);

 }

 public String getFullName() {
  return String.format("%s %s", lastName.toUpperCase(), firstName);

 }

};



