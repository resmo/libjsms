package net.renemoser.libjsms.exception;

public class NotSentException extends Exception {

    private static final long serialVersionUID = 1L;

    public NotSentException() {
	super();
    }

    public NotSentException(String message) {
	super(message);
    }
}
