package net.renemoser.libjsms.exception;

public class NoMessageException extends Exception {

    private static final long serialVersionUID = 1L;

    public NoMessageException() {
	super();
    }

    public NoMessageException(String message) {
	super(message);
    }
}
