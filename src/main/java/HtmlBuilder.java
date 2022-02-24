
/**
 * Classe helper qui permet de gérer la génération d'un fichier
 * */
public class HtmlBuilder {
    private StringBuilder _stringBuilder = new StringBuilder();

    public String toString() {
        return _stringBuilder.toString();
    }

    public void append(String str) {
        _stringBuilder.append(str);
    }
}
