package net.renemoser.libjsms.service;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.renemoser.libjsms.exception.AvailableMessagesUnknownException;
import net.renemoser.libjsms.exception.LoginFailedException;
import net.renemoser.libjsms.exception.NotSentException;

/**
 * Unit test for Orange.
 */
public class OrangeCHTest extends TestCase {
    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
	return new TestSuite(OrangeCHTest.class);
    }

    /**
     * Create the test case
     * 
     * @param testName
     *            name of the test case
     */
    public OrangeCHTest(String testName) {
	super(testName);
    }

    public void testGetAvailableMessages() {
	try {
	    OrangeCH Service = new OrangeCH();
	    Service.getAvailableMessages();
	    fail("Should have thrown AvailableMessagesUnknownException");
	} catch (AvailableMessagesUnknownException le) {

	} catch (Exception e) {
	    fail("Should have thrown AvailableMessagesUnknownException");
	}
    }

    public void testLoginEmpty() {
	try {
	    OrangeCH Service = new OrangeCH();
	    Service.doLogin("", "");
	    fail("Should have thrown LoginFailedException");
	} catch (LoginFailedException le) {

	} catch (Exception e) {
	    fail("Should have thrown LoginFailedException");
	}
    }

    public void testLoginWrong() {
	try {
	    OrangeCH Service = new OrangeCH();
	    Service.doLogin("unknownUser", "wrongPassword");
	    fail("Should have thrown LoginFailedException");
	} catch (LoginFailedException le) {

	} catch (Exception e) {
	    fail("Should have thrown LoginFailedException");
	}
    }

    public void testSendMessageEmptyMessage() {
	try {
	    OrangeCH Service = new OrangeCH();
	    Service.sendShortMessage("0761234567", "");
	    fail("Should have thrown NotSentException");
	} catch (NotSentException le) {

	} catch (Exception e) {
	    fail("Should have thrown NotSentException: " + e.getMessage());
	}
    }

    public void testSendMessageMessageTooLong() {
	try {
	    OrangeCH Service = new OrangeCH();
	    Service.setLoggedIn(true);
	    String message = "";
	    for (int i = 0; i < 145; i++) {
		message += "x";
	    }
	    Service.sendShortMessage("0761234567", message);
	    fail("Should have thrown NotSentException");
	} catch (NotSentException le) {

	} catch (Exception e) {
	    fail("Should have thrown NotSentException: " + e.getMessage());
	}
    }

    public void testSendMessagePhoneNumberWrong1() {
	try {
	    OrangeCH Service = new OrangeCH();
	    Service.sendShortMessage("076 123 45 67", "test message");
	    fail("Should have thrown NotSentException");
	} catch (NotSentException le) {

	} catch (Exception e) {
	    fail("Should have thrown NotSentException: " + e.getMessage());
	}
    }

    public void testSendMessagePhoneNumberWrong2() {
	try {
	    OrangeCH Service = new OrangeCH();
	    Service.sendShortMessage("+41 076 123 45 67", "test message");
	    fail("Should have thrown NotSentException");
	} catch (NotSentException le) {

	} catch (Exception e) {
	    fail("Should have thrown NotSentException: " + e.getMessage());
	}
    }
}
