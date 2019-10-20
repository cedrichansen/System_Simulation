public class IllegalInputException extends Exception {

    String message;

    final static String explanation = "The input entered into the model is not properly formatted and/or invalid. The input received was: ";

    IllegalInputException(String message) {
        super(message);
        this.message = explanation + " " + message;
    }

}