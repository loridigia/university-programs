package Exceptions;

public class EmptyListException extends Exception {
    public EmptyListException() {
        System.out.println("ERRORE: Stack vuoto");
    }
}
