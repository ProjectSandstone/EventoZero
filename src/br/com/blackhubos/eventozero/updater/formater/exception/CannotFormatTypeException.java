package br.com.blackhubos.eventozero.updater.formater.exception;

/**
 * Created by jonathan on 03/01/16.
 */
public class CannotFormatTypeException extends RuntimeException {

    public CannotFormatTypeException() {
        super();
    }

    public CannotFormatTypeException(String message) {
        super(message);
    }

    public CannotFormatTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotFormatTypeException(Throwable cause) {
        super(cause);
    }

}
