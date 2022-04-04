import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.*;

public class ReadPhone {
    private ArrayList<String> sortedPhone;
    private String content;

    public ReadPhone() throws IOException {
        this.content = readContent("na_phonemes.txt");
        this.sortedPhone = PhoneSorted();
    }
    public ArrayList<String> getSortedPhone(){
        return this.sortedPhone;
    }

    private String readContent(String filename)throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }

    private HashMap<String, String> readInventory(){
        HashMap<String, String> inventory = new HashMap<>();
        for (String line: this.content.split("\n")){
            String phone = line.split(":")[0].trim();
            String code = line.split(":")[1].trim();
            inventory.putIfAbsent(phone, code);
        }
        //System.out.println(inventory);
        return inventory;
    }

    private ArrayList<String> PhoneSorted(){
        ArrayList<String> phoneLst = new ArrayList<>(this.readInventory().keySet());
        Collections.sort(phoneLst, Comparator.comparing(String::length).reversed());
        return phoneLst;
    }

}
