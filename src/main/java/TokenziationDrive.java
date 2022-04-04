import java.io.IOException;

public class TokenziationDrive {
    public static void main(String[] agr) throws IOException, NonExistPhoneException {
        ReadPhone phone = new ReadPhone();
        System.out.println(phone.getSortedPhone());
        ReadSent na = new ReadSent();
      //  na.getSentences();
        Tokenize na_Exemple = new Tokenize("final_na_examples.txt");
        //na_Exemple.getTokenized();
        //na_Exemple.printFreqMap();
        na_Exemple.printEval();
    }
}
