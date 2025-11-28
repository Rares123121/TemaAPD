import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.*;

public class FisiereOut {

    public static String normalizeString(String categorie){
        return categorie
            .replace(",", "")
            .trim()
            .replaceAll("\\s+", "_");
    }

    public static void generateLanguageFiles() {
        //System.out.println("Directorul curent de lucru: " + System.getProperty("user.dir"));
        for (var entry : Data.FilePeLimbi.entrySet()) {
            String language = entry.getKey();
            List<String> list = entry.getValue();

            // Creez o copie sortată
            List<String> sorted = new ArrayList<>(list);
            Collections.sort(sorted);
            if(sorted.size() == 0)
                continue;
            //System.out.println(language);
           // File file = new File(language + ".txt");
            //System.out.println("Scriu in: " + file.getAbsolutePath());
            try (FileWriter fw = new FileWriter("./" + language + ".txt")) {

                // prima linie: numărul de articole
                //pw.println(sorted.size());

                // uuid-urile, una pe linie
                for (String uuid : sorted) {
                    fw.write(uuid + "\n");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void generateCategoriiFiles() {
        //System.out.println("Directorul curent de lucru: " + System.getProperty("user.dir"));
        for (var entry : Data.FilePeCategorii.entrySet()) {
            String categorie = entry.getKey();
            List<String> list = entry.getValue();

            categorie = normalizeString(categorie);
            // Creez o copie sortată
            List<String> sorted = new ArrayList<>(list);
            Collections.sort(sorted);
            //System.out.println(language);
            //File file = new File(language + ".txt");
            //System.out.println("Scriu in: " + file.getAbsolutePath());
            if(sorted.size() == 0)
                continue;
            try (FileWriter fw = new FileWriter("./" + categorie + ".txt")) {

                // prima linie: numărul de articole
                //pw.println(sorted.size());

                // uuid-urile, una pe linie
                for (String uuid : sorted) {
                    fw.write(uuid + "\n");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void generateAllArticles() {
        //System.out.println("Directorul curent de lucru: " + System.getProperty("user.dir"));
        try (FileWriter fw = new FileWriter("./" + "all_articles.txt")) {
            for (Article a : Data.uniqueArticles) {
                fw.write(a.uuid + " " + a.published + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateCuvinte(){
            //System.out.println(language);
            //File file = new File(language + ".txt");
            //System.out.println("Scriu in: " + file.getAbsolutePath());
            try (FileWriter fw = new FileWriter("./" + "keywords_count.txt")) {

                // prima linie: numărul de articole
                //pw.println(sorted.size());

                // uuid-urile, una pe linie
                for (var entry : Statistici.sorted) {
                    String cuvant = entry.getKey();
                    AtomicInteger nr = entry.getValue();
                    fw.write(cuvant + " " + nr + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        
    }

    public static void raport(){
        try (FileWriter fw = new FileWriter("./" + "reports.txt")) {
            fw.write("duplicates_found - " + Statistici.duplicate + "\n");
            fw.write("unique_articles - " + Statistici.unice + "\n");
            fw.write("best_author - " + Statistici.NumeAutor + " " + Statistici.articoleScrise + "\n");
            fw.write("top_language - " + Statistici.LimbaTop + " " + Statistici.countLimba + "\n");
            fw.write("top_category - " + normalizeString(Statistici.CatTop) + " " + Statistici.CatCount + "\n");
            fw.write("most_recent_article - " + Statistici.DataRecenta + " " + Statistici.URL + "\n");
            fw.write("top_keyword_en - " + Statistici.CuvantBest + " " + Statistici.CuvCount + "\n");
                //fw.write("duplicates_found - " Statistici.duplicate + "\n");
                //fw.write("unique_articles - " Statistici.duplicate + "\n");

            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
