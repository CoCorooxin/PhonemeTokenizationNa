import java.io.IOException;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Stream;

public class Tokenize {
    private String path;
    private ArrayList<String> tokenized_Na;
    private ArrayList<String> goldToken;
    private ArrayList<String> ton;
    private HashMap<String, Integer> freqMap = new HashMap<>();

    public Tokenize(String path) throws IOException {
        ReadPhone phone = new ReadPhone();
        ReadSent sentence = new ReadSent();
        //depuis la classe ReadPhone et La classe ReadSent on récupère les données qu'on va traiter et la référence
        this.ton = this.getTon();
        this.goldToken = sentence.getGoldTok();
        this.path = String.valueOf(Paths.get(path).toAbsolutePath());
        for(String ph: phone.getSortedPhone()){
            this.freqMap.putIfAbsent(ph, 0);
        } // freqMap crée avant d'être appélé dans le tokenizer
        this.tokenized_Na = tokenize_Na(sentence.getSentences(), phone.getSortedPhone());
    }

    public ArrayList<String> getTokenized(){
        return this.tokenized_Na;
    }

    public ArrayList<String> getTon(){
        return new ArrayList<>(Arrays.asList("˧˥","˩˥","˩˧","˧˩","˥","˧","˩"));
    }// créer une liste de tons manuellement

    public void printFreqMap(){
        freqMap.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .forEach(System.out::println);
    }

    private ArrayList<String> tokenize_Na(ArrayList<String> sentLst, ArrayList<String> phonemes) {
        ArrayList<String> res = new ArrayList<>();
        HashSet<String> inconnu = new HashSet<>();
        for(String sent: sentLst){
            res.add(this.segmentPhone(sent, phonemes, inconnu));
        }
        //System.out.println(this.freqMap);
        //System.out.println("les phonemes inconnus sont " + inconnu); //les phonemes inconnus sont [ʰ, a, ʔ, D, F, ¨]]
        return res;
    }

    private String segmentPhone(String sentence, ArrayList<String> phonemes , HashSet<String> inconnu) {
        ArrayList<String> tokenized = new ArrayList<>();
        for(String word: List.of(sentence.split("[|]"))){
           // System.out.println("word is " + word);
            while(!word.isEmpty()){
                word = this.extractedNextPhoneme(word, phonemes, tokenized, inconnu);
            }
            tokenized.add("|");
            //System.out.println(tokenized);
        }
        this.trimEnd(sentence, tokenized);
        this.makeFreqMap(tokenized);
        return String.join(" ", tokenized);
    }

    private String extractedNextPhoneme(String todo, ArrayList<String> phonemes, ArrayList<String> tokenize, HashSet<String> inconnu) {
        for(int i= this.counter(todo); i>0; i--) {
            todo = this.rmHesitation(todo, tokenize);
            if(todo.length()< i){
                break;// gage que l'itération ne va pas rétourner l'erreur
            }
            String tmp = todo.substring(0,i);
            if (phonemes.contains(tmp) || this.ton.contains(tmp)) {
                tokenize.add(tmp);
                todo = todo.substring(i);
                if(i!=1){
                    break;// gage que le plus grand phoneme soit traité avant tous;
                }
            } else if(i==1){ //Par défaut, les phonemes inconnus sont traité comme des phonemes singletons
                inconnu.add(tmp);
                todo = todo.substring(i);
            }
        }return todo;
    }

    private String rmHesitation(String todo, ArrayList<String> tokenize) {
        if(todo.startsWith("mmm…")|| todo.startsWith("əəə…")){
            tokenize.add(todo.substring(0,4));
            todo = todo.substring(4);
            return rmHesitation(todo, tokenize); // traiter le mots contenant deux ou plus hésitations consécutives
        }else{
            return todo;
        }
    }
    // Une fonction qui nous permet d'itérer sur le mot au lieu de sur la liste de phones
    private int counter(String todo) {
        if(todo.length()> 3){
            return 3;
        }
        else{
            return todo.length();
        }
    }

    private void trimEnd(String sentence, ArrayList<String> tokenized) {
        if(!sentence.endsWith("|")){
            //remove last "|" because it is redundant
            tokenized.remove(tokenized.size()-1);
        }
        if(sentence.endsWith("||")){
            //add missing "|" because the last word is empty;
            tokenized.add("|");
        }
    }

    private HashMap<String, Integer> makeFreqMap(ArrayList<String> tokenized){
        HashMap<String, Integer> res = this.freqMap;
        for(String predPhone: tokenized){
            res.computeIfPresent(predPhone, (key, val)->val+1);
        }return res;
    }

    //Partie évaluation;
    public void printEval() {
        int length = this.tokenized_Na.size();
        int countCorr = 0;
        int countErr = 0;
        for (int i = 0; i < length; i++) {
            if (Objects.equals(Normalizer.normalize(this.tokenized_Na.get(i), Normalizer.Form.NFKD),Normalizer.normalize(this.goldToken.get(i), Normalizer.Form.NFKD))){
                countCorr++;
            }else{
                countErr++;
            }
        }
        System.out.println("program sorted phone: " + length + "; gold sorted phone" + this.goldToken.size() + "; well sorted " + countCorr + "; total error " + countErr);
    }

}
