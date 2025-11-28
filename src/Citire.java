
import java.io.File;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.concurrent.atomic.*;

public class Citire implements Runnable {
    private final BlockingQueue<String> taskQueue;
    private final Tema1.AuxData date;

    ObjectMapper mapper = new ObjectMapper();
    
    public Citire(BlockingQueue<String> taskQueue1, Tema1.AuxData date1){
        this.taskQueue = taskQueue1;
        this.date = date1;
    }

    @Override
    public void run() {
        while (true) {
            String file = taskQueue.poll();
            if (file == null)
                break;

            // aici fac procesarea fisierului JSON
            try {
                // citesc lista de articole
                List<Article> articles = mapper.readValue(
                    new File(file),
                    new TypeReference<List<Article>>() {}
                );

                for (Article a : articles) {
                    Data.all.add(a);
                    boolean keep = Data.addRawArticle(a);
                    if (keep) {
                        for (String c : a.categories) {
                            Data.FilePeCategorii.computeIfAbsent(c, k -> Collections.synchronizedList(new ArrayList<>())).add(a.uuid);
                        }
                        
                        Data.Autori.computeIfAbsent(a.author, k -> Collections.synchronizedList(new ArrayList<>())).add(a.uuid);

                        // pun limba doar daca exista in lista de limbi
                        if(date.languages.contains(a.language))
                            Data.FilePeLimbi.computeIfAbsent(a.language, k -> Collections.synchronizedList(new ArrayList<>())).add(a.uuid);
                        
                        if(a.language.equals("english")) {
                            Set<String> words = Data.tokenize(a.text);
                            for (String w : words) {
                                if (!date.linkingWords.contains(w)) {
                                    Data.KeywordsCount.computeIfAbsent(w, k -> new AtomicInteger(0)).incrementAndGet();
                                }
                            }
                        }
                    }
                }
            } catch(Exception e){
                System.out.println(e);
            }
        }
    }
}
