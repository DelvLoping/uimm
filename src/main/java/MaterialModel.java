
/**
 * Objet représentant un materiel
 * **/
public class MaterialModel {
    public String label;
    public boolean value;

    public MaterialModel(String text, boolean val){
        label=text;
        value=val;
    }

    public String getFileName(String extension) {
        return String.format("%s.%s", label.toLowerCase().replaceAll("[^a-z]+",""), extension);
    }
}
