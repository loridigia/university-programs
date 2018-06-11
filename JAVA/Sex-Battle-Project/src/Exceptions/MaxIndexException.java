package Exceptions;

public class MaxIndexException extends Exception {
    public MaxIndexException() {
        System.out.println("ECCEZIONE: Indice più grande della dimensione dello stack");
    }
}
