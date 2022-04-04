
public class NonExistPhoneException extends Exception {

    public NonExistPhoneException(String errorMessage, Throwable cause){
        super(errorMessage, cause);

    }

    public NonExistPhoneException(String message) {
        super(message);
    }
}
