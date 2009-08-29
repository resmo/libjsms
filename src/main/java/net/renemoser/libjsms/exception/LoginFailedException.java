package net.renemoser.libjsms.exception;

public class LoginFailedException extends Exception {

    private static final long serialVersionUID = 1L;

    public LoginFailedException() {
	super();
    }

    public LoginFailedException(String message) {
	super(message);
    }
}
