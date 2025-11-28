import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Article {
    public String uuid;
    public String title;
    public String author;
    public String url;
    public String text;
    public String published;
    public String language;
    public Set<String> categories;
    


    public Article() {}  // necesar pentru Jackson

    @Override
    public String toString() {
        return "Article{" +
                "id='" + uuid + '\'' +
                ", title='" + title + '\'' +
                ", language='" + language + '\'' +
                ", category='" + categories + '\'' +
                '}';
    }
}