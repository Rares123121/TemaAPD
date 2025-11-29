import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Tema1 {

    public static BlockingQueue<String> taskQueue = new LinkedBlockingQueue<>();
    public static class AuxData {
        public List<String> languages;
        public List<String> categories;
        public List<String> linkingWords;
    }

    public static List<String> loadSimpleList(String path) {
        List<String> result = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            int n = Integer.parseInt(lines.get(0).trim());

            for (int i = 1; i <= n; i++) {
                result.add(lines.get(i).trim());
            }
        } catch(Exception e) {
            System.out.println(e);
        }
        return result;
    }

    public static AuxData loadAuxFiles(String auxFile) {
        AuxData data = new AuxData();

        try {
            List<String> lines = Files.readAllLines(Paths.get(auxFile));

            String languagesPath = lines.get(1);
            String categoriesPath = lines.get(2);
            String linkingWordsPath = lines.get(3);
            // astea sunt caile pt teste manuale
            String baseDir = auxFile.substring(0, auxFile.lastIndexOf('/') + 1);
            data.languages = loadSimpleList(baseDir + languagesPath.trim());
            data.categories = loadSimpleList(baseDir + categoriesPath.trim());
            data.linkingWords = loadSimpleList(baseDir + linkingWordsPath.trim());

        } catch(Exception e) {
            System.out.println(e);
        }

        return data;
    }

    public static List<String> LoadArticles(String path){
        List<String> result = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get(path));

            int n = Integer.parseInt(lines.get(0).trim());
            String baseDir = path.substring(0, path.lastIndexOf('/') + 1);

            for (int i = 1; i <= n; i++) {
                String file = lines.get(i).trim();
                result.add(baseDir + file);
            }

        } catch(Exception e) {
            System.out.println("Eroare la citirea fisierului articles: " + e);
        }

        return result;
    }

    public static void main(String[] args) {
        int nrThreads = Integer.parseInt(args[0]);
        String articole = args[1];
        String auxFile = args[2];

        // in date am fisierele alea cu limbi, categorii, etc
        AuxData date = loadAuxFiles(auxFile);
        //System.out.println(date.linkingWords.get(0));
        List<String> articles = LoadArticles(articole);

        for (String f : articles) {
            taskQueue.add(f);
        }

        Thread[] threads = new Thread[nrThreads];

        for (int i = 0; i < nrThreads; i++) {
            threads[i] = new Thread(new Citire(taskQueue, date));
            threads[i].start();
        }

        for (int i = 0; i < nrThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("RUNNING IN: " + new java.io.File(".").getAbsolutePath());
        // System.out.println(Data.all);
        // System.out.println(Data.uniqueArticles);
        // System.out.println(Data.FilePeCategorii);
        // System.out.println(Data.FilePeLimbi);
        // System.out.println(Data.Autori);
        // System.out.println(Data.getSortedKeywords());

        //System.out.println(nrThreads);
        Data.sequentialDedup();
        Statistici.ComputeStats();
        // System.out.println(Statistici.unice + " " + Statistici.duplicate);
        // System.out.println(Statistici.NumeAutor + " " + Statistici.articoleScrise);
        // System.out.println(Statistici.LimbaTop + " " + Statistici.countLimba);
        // System.out.println(Statistici.CatTop + " " + Statistici.CatCount);
        // System.out.println(Statistici.DataRecenta + " " + Statistici.URL);
        // System.out.println(Statistici.CuvantBest + " " + Statistici.CuvCount);
        // System.out.println(Data.uniqueArticles);
        FisiereOut.generateLanguageFiles();
        FisiereOut.generateCategoriiFiles();
        FisiereOut.generateAllArticles();
        FisiereOut.generateCuvinte();
        FisiereOut.raport();
        
    }
}