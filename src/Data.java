import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import javax.sql.rowset.spi.SyncResolver;

public class Data {

    /*
        De facut adaugarea la toate in paralel, si dupa stergera duplicatelor si restul secvential
    */


    public static ConcurrentHashMap<String, Article> idMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Article> titleMap = new ConcurrentHashMap<>();
    public static List<Article> uniqueArticles = Collections.synchronizedList(new ArrayList<>());
    public static List<Article> all = Collections.synchronizedList(new ArrayList<>());
    // pt a nu mai baga elemente duplicate chiar daca nu mai exista in 
    public static Set<String> removedUUIDs = ConcurrentHashMap.newKeySet();
    public static Set<String> removedTitles = ConcurrentHashMap.newKeySet();

        /*
    imi mai trebuie hashmap pt cuvinte, pt limbi, pt articole pe limbi, pt articole pe categorii
    pt cel mai recent articol, pt cel mai bun scriitor, pt top category
    */
    public static ConcurrentHashMap<String, String> Publicate = new ConcurrentHashMap<>();  
    public static ConcurrentHashMap<String, List<String>> FilePeLimbi = new ConcurrentHashMap<>();    
    public static ConcurrentHashMap<String, List<String>> FilePeCategorii = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, AtomicInteger> KeywordsCount = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, List<String>> Autori = new ConcurrentHashMap<>();  
    // public static List<Map.Entry<String, AtomicInteger>> sorted = getSortedKeywords();//new ArrayList<>();

    /*
        Mai am nevoie de functii care sa caute cel mai bun autor, categorie, limba si sa faca si articolele in functie de data
    */

    public static synchronized boolean addRawArticle(Article a) {

    // 1. Dacă articolul a fost deja marcat ca „eliminat definitiv”
        if (removedUUIDs.contains(a.uuid) || removedTitles.contains(a.title)) {
            return false;
        }

        // 2. Verificăm dacă există alt articol cu același uuid sau title
        Article oldUUID = idMap.get(a.uuid);
        Article oldTitle = titleMap.get(a.title);

        boolean hasDupUUID = oldUUID != null;
        boolean hasDupTitle = oldTitle != null;

        // 3. Caz fără duplicate => adăugare temporară
        if (!hasDupUUID && !hasDupTitle) {
            idMap.put(a.uuid, a);
            titleMap.put(a.title, a);
            uniqueArticles.add(a);
            return true;
        }

        // 4. Există două articole cu același title/uuid => eliminăm TOT
        removedUUIDs.add(a.uuid);
        removedTitles.add(a.title);

        // 4.a Elimină articolul vechi din structuri
        if (oldUUID != null) {
            uniqueArticles.remove(oldUUID);
            removeArticleFromStructures(oldUUID);
            idMap.remove(oldUUID.uuid);
            titleMap.remove(oldUUID.title);
        }

        if (oldTitle != null && oldTitle != oldUUID) { 
            uniqueArticles.remove(oldTitle);
            removeArticleFromStructures(oldTitle);
            idMap.remove(oldTitle.uuid);
            titleMap.remove(oldTitle.title);
        }

        // 4.b Nu inserăm articolul nou
        return false;
    }

    // public static synchronized boolean addRawArticle(Article a) {
    //     if (removedUUIDs.contains(a.uuid) || removedTitles.contains(a.title)) {
    //         return false;
    //     }

    //     Article oldUUID = idMap.putIfAbsent(a.uuid, a);
    //     Article oldTitle = titleMap.putIfAbsent(a.title, a);

    //     if (oldUUID == null && oldTitle == null) {
    //         uniqueArticles.add(a);
    //         return true;
    //     }
    //     removedUUIDs.add(a.uuid);
    //     removedTitles.add(a.title);

    //     if (oldUUID != null) {
    //         uniqueArticles.remove(oldUUID);
    //         removeArticleFromStructures(oldUUID);
    //     }
    //     if (oldTitle != null && oldTitle != oldUUID) {
    //         uniqueArticles.remove(oldTitle);
    //         removeArticleFromStructures(oldTitle);
    //     }

    //     idMap.remove(a.uuid);
    //     titleMap.remove(a.title);

    //     return false;
    // }


    public static void removeArticleFromStructures(Article a) {
        for (String c : a.categories) {
            List<String> list = FilePeCategorii.get(c);
            if (list != null) 
                list.remove(a.uuid);
        }

        List<String> langs = FilePeLimbi.get(a.language);
        if (langs != null) 
            langs.remove(a.uuid);

        List<String> autList = Autori.get(a.author);
        if (autList != null)
            autList.remove(a.uuid);

        if(a.language.equals("english")){
            Set<String> words = tokenize(a.text);
            for (String w : words) {
                AtomicInteger cnt = KeywordsCount.get(w);
                if (cnt != null) {
                    cnt.decrementAndGet();
                    if (cnt.get() <= 0) 
                        KeywordsCount.remove(w);
                }
            }
        }
    }


    public static synchronized Set<String> tokenize(String text) {
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
