import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Statistici {
    public static int duplicate;
    public static int unice;

    public static String NumeAutor;
    public static int articoleScrise;

    public static String LimbaTop;
    public static int countLimba;

    public static String CatTop;
    public static int CatCount;

    public static String DataRecenta;
    public static String URL;

    public static String CuvantBest;
    public static int CuvCount;

    public static List<Map.Entry<String, AtomicInteger>> sorted = Data.getSortedKeywords();


    public static void Dups(){
        duplicate = Data.all.size() - Data.uniqueArticles.size();
        unice = Data.uniqueArticles.size();
    }

    public static void topAutor(){
        String celMaiBunAutor = null;
        int maxArticole = -1;
        for (Map.Entry<String, List<String>> entry : Data.Autori.entrySet()) {
            String autorCurent = entry.getKey();
            int nrArticoleCurent = entry.getValue().size();

            if (nrArticoleCurent > maxArticole) {
                maxArticole = nrArticoleCurent;
                celMaiBunAutor = autorCurent;
            } 
            else if (nrArticoleCurent == maxArticole) {
                if (celMaiBunAutor != null && autorCurent.compareTo(celMaiBunAutor) < 0) {
                    celMaiBunAutor = autorCurent;
                }
            }
        }
        if(celMaiBunAutor != null){
            NumeAutor = celMaiBunAutor;
            articoleScrise = maxArticole;
        }
    }

    public static void topLimba(){
        String celMaiBunLimba = null;
        int maxi = -1;
        for (Map.Entry<String, List<String>> entry : Data.FilePeLimbi.entrySet()) {
            String autorCurent = entry.getKey();
            int nrArticoleCurent = entry.getValue().size();

            if (nrArticoleCurent > maxi) {
                maxi = nrArticoleCurent;
                celMaiBunLimba = autorCurent;
            } 
            else if (nrArticoleCurent == maxi) {
                if (celMaiBunLimba != null && autorCurent.compareTo(celMaiBunLimba) < 0) {
                    celMaiBunLimba = autorCurent;
                }
            }
        }

        if(celMaiBunLimba != null){
            LimbaTop = celMaiBunLimba;
            countLimba = maxi;
        }
    }

    public static void topCat(){
        String celMaiBunCat = null;
        int maxi = -1;
        for (Map.Entry<String, List<String>> entry : Data.FilePeCategorii.entrySet()) {
            String autorCurent = entry.getKey();
            int nrArticoleCurent = entry.getValue().size();

            if (nrArticoleCurent > maxi) {
                maxi = nrArticoleCurent;
                celMaiBunCat  = autorCurent;
            } 
            else if (nrArticoleCurent == maxi) {
                if (celMaiBunCat  != null && autorCurent.compareTo(celMaiBunCat ) < 0) {
                    celMaiBunCat  = autorCurent;
                }
            }
        }

        if(celMaiBunCat  != null){
            CatTop = celMaiBunCat;
            CatCount = maxi;
        }
    }

    public static void CuvTop(){
        String celMaiFrecventCuvant = null;
        int maxAparitii = -1;

        for (Map.Entry<String, AtomicInteger> entry : Data.KeywordsCount.entrySet()) {
            String cuvantCurent = entry.getKey();
            // Aici este diferenta fata de exemplul anterior: folosim .get()
            int aparitiiCurente = entry.getValue().get(); 

            // Verificăm dacă am găsit un nou maxim
            if (aparitiiCurente > maxAparitii) {
                maxAparitii = aparitiiCurente;
                celMaiFrecventCuvant = cuvantCurent;
            } 
            // Tratăm egalitatea (ordine lexicografică)
            else if (aparitiiCurente == maxAparitii) {
                if (celMaiFrecventCuvant != null && cuvantCurent.compareTo(celMaiFrecventCuvant) < 0) {
                    celMaiFrecventCuvant = cuvantCurent;
                }
            }
        }

        if(celMaiFrecventCuvant != null){
            CuvantBest = celMaiFrecventCuvant;
            CuvCount = maxAparitii;
        }
    }

    public static void recent(){
        Article articolRecent = null;

        for (Article art : Data.uniqueArticles) {
            if (articolRecent == null || art.published.compareTo(articolRecent.published) > 0) {
                articolRecent = art;
            }
        }

        if (articolRecent != null) {
            DataRecenta = articolRecent.published;
            URL = articolRecent.url;
        }
    }

    public static void sortArticle(){
        Data.uniqueArticles.sort((a, b) -> {
            int cmp = b.published.compareTo(a.published); // ordonare descrescător după timp
            if (cmp != 0) return cmp;
            return a.uuid.compareTo(b.uuid); // la egalitate lexicografic
        });
    }

    public static void ComputeStats(){
        recent();
        CuvTop();
        Dups();
        topAutor();
        topCat();
        topLimba();
        sortArticle();
    }
}
