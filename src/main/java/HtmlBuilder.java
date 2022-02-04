
public class HtmlBuilder {
    private StringBuilder _stringBuilder = new StringBuilder();

    public HtmlBuilder() {

    }

    /*public void addUnorderedListItem() {
        _stringBuilder.append("");
    }

    public void addUnorderedList() {
        var nestedBuiled = new HtmlBuilder();
        //nestedBuiled.add

    }*/

    public String toString() {
        return _stringBuilder.toString();
    }

    public void append(String str) {
        _stringBuilder.append(str);
    }
}
