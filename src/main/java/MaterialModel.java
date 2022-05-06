
/**
 * Objet représentant un materiel
 * **/
public class MaterialModel {
    public String label;
    public boolean value;

    public MaterialModel(String _label, boolean _value) {
        label = _label;
        value = _value;
    }

    /**
     * Méthode qui retourne le nom du fichier à partir du materiel actuel (supression des caractères spéciaux etc)
     */
    public String getFileName(String extension) {
        return String.format("%s.%s", label.toLowerCase().replaceAll("[^a-z]+",""), extension);
    }
}
