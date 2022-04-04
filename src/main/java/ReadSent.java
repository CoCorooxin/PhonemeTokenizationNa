import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ReadSent {
    private ArrayList<String> sentences;
    private ArrayList<String> goldTok;

    public ReadSent() throws IOException {
        this.sentences = sentSentences("final_na_examples.txt");
        this.goldTok = sentGoldToken("final_na_examples.txt");
    }

    public ArrayList<String> getGoldTok(){
        return this.goldTok;
    }
    public ArrayList<String> getSentences(){
        return this.sentences;
    }

    private ArrayList<String> sentSentences(String path) throws IOException {
        ArrayList<String> brut = new ArrayList<>();
        try (Stream<String> lines = Files.lines(
                Paths.get(path))){
            for (String line:
                    (Iterable<String>) lines::iterator){
                brut.add(line.split("@@@")[0]);
            }
        }
        return normalizeStr(brut);
    }

    private ArrayList<String> normalizeStr(ArrayList<String> sents){
        ArrayList<String> res = new ArrayList<>();
        for(String str: sents){
            if(str.contains("BEGAIEMENT")){
                res.add("");
                //System.out.println(str);
                continue;
            }
           // System.out.println("original " + str);
            str = str.replaceAll("[◊]","|").replaceAll("[\\[](.*?)[\\]]|[<»«>.↑”“]|[\\s+\\n]|(?![|])\\p{Punct}|(?<![əm])…", "").replaceAll("m+[…]", "mmm…").replaceAll("ə+[…]", "əəə…");
            //res.add(Normalizer.normalize(str, Normalizer.Form.NFKD));
            res.add(str);
           // System.out.println("normalized " + str);
        }return res;
    }

    private ArrayList<String> sentGoldToken(String path) throws IOException {
        ArrayList<String> gold = new ArrayList<>();
        try (Stream<String> lines = Files.lines(
                Paths.get(path))){
            for (String line:
                    (Iterable<String>) lines::iterator){
                gold.add(normalize(line.split("@@@")[1].trim()));
            }
        }
        return gold;
    }
    private String normalize(String phones){
        ArrayList<String> todo = new ArrayList<>(List.of(phones.split(" ")));
        for(int i=0; i< todo.size(); i++){
            if( todo.get(i) == "ṽ̩" || todo.get(i) == "ṽ̩"){
                Normalizer.normalize(todo.get(i), Normalizer.Form.NFKD);
            }
        }return String.join(" ", todo);
    }

}
