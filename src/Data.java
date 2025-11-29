import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import javax.sql.rowset.spi.SyncResolver;

public class Data {

    /*
        De facut adaugarea la toate in paralel, si dupa stergera duplicatelor si restul secvential
    */


    //public static ConcurrentHashMap<String, Article> idMap = new ConcurrentHashMap<>();
    //public static ConcurrentHashMap<String, Article> titleMap = new ConcurrentHashMap<>();
    public static List<Article> uniqueArticles = Collections.synchronizedList(new ArrayList<>());
    public static List<Article> all = Collections.synchronizedList(new ArrayList<>());
    // pt a nu mai baga elemente duplicate chiar daca nu mai exista in 
    //public static Set<String> removedUUIDs = ConcurrentHashMap.newKeySet();
    //public static Set<String> removedTitles = ConcurrentHashMap.newKeySet();

        /*
    imi mai trebuie hashmap pt cuvinte, pt limbi, pt articole pe limbi, pt articole pe categorii
    pt cel mai recent articol, pt cel mai bun scriitor, pt top category
    */
    //public static ConcurrentHashMap<String, String> Publicate = new ConcurrentHashMap<>();  
    public static ConcurrentHashMap<String, List<String>> FilePeLimbi = new ConcurrentHashMap<>();    
    public static ConcurrentHashMap<String, List<String>> FilePeCategorii = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, AtomicInteger> KeywordsCount = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, List<String>> Autori = new ConcurrentHashMap<>();  
    // public static List<Map.Entry<String, AtomicInteger>> sorted = getSortedKeywords();//new ArrayList<>();

    /*
        Mai am nevoie de functii care sa caute cel mai bun autor, categorie, limba si sa faca si articolele in functie de data
    */

    public static void sequentialDedup() {

        Map<String, Integer> uuidCount = new HashMap<>();
        Map<String, Integer> titleCount = new HashMap<>();

        for (Article a : all) {
            uuidCount.put(a.uuid, uuidCount.getOrDefault(a.uuid, 0) + 1);
            titleCount.put(a.title, titleCount.getOrDefault(a.title, 0) + 1);
        }

        uniqueArticles.clear();
        List<Article> duplicates = new ArrayList<>();

        for (Article a : all) {
            if (uuidCount.get(a.uuid) > 1 || titleCount.get(a.title) > 1) {
                duplicates.add(a);
            } else {
                uniqueArticles.add(a);
            }
        }

        cleanupDataStructures(duplicates);
    }


    public static void cleanupDataStructures(List<Article> duplicates) {

    // Scoatem uuid-urile din structurile auxiliare
        for (Article a : duplicates) {

            // limbi
            List<String> langList = FilePeLimbi.get(a.language);
            if (langList != null) {
                langList.remove(a.uuid);
            }

            // categorii
            for (String c : a.categories) {
                List<String> catList = FilePeCategorii.get(c);
                if (catList != null) {
                    catList.remove(a.uuid);
                }
            }

            // autori
            List<String> autList = Autori.get(a.author);
            if (autList != null) {
                autList.remove(a.uuid);
            }
        }

        // Scadem keywords doar pentru articolele duplicate
        for (Article a : duplicates) {
            if (a.language.equals("english")) {

                Set<String> words = tokenize(a.text);  // set = cuvinte unice din articol

                for (String w : words) {
                    KeywordsCount.computeIfPresent(w, (key, val) -> {
                        int newVal = val.decrementAndGet();
                        return newVal <= 0 ? null : val;
                    });
                }
            }
        }
        // Scoatem articolele din idMap și titleMap
        // for (Article a : duplicates) {
        //     idMap.remove(a.uuid);
        //     titleMap.remove(a.title);
        // }
    }

    public static Set<String> tokenize(String text) {
        text = text.toLowerCase();
        String[] tokens = text.split("\\s+");

        Set<String> result = new HashSet<>();
        for (String tok : tokens) {
            String cleaned = tok.replaceAll("[^a-z]", "");
            if (!cleaned.isEmpty()) {
                result.add(cleaned);
            }
        }

        return result;
    }

    public static List<Map.Entry<String, AtomicInteger>> getSortedKeywords() {

        List<Map.Entry<String, AtomicInteger>> lista =
                new ArrayList<>(KeywordsCount.entrySet());

        lista.sort((a, b) -> {
            int countA = a.getValue().get();
            int countB = b.getValue().get();

            // DESC după count
            if (countA != countB) {
                return Integer.compare(countB, countA);
            }

            // lexicografic după cuvânt
            return a.getKey().compareTo(b.getKey());
        });

        return lista;
    }
}
