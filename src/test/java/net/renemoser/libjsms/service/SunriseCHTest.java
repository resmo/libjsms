package net.renemoser.libjsms.service;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.renemoser.libjsms.exception.AvailableMessagesUnknownException;
import net.renemoser.libjsms.exception.LoginFailedException;
import net.renemoser.libjsms.exception.NotSentException;

/**
 * Unit test for Sunrise.
 */
public class SunriseCHTest extends TestCase {
    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
	return new TestSuite(SunriseCHTest.class);
    }

    /**
     * Create the test case
     * 
     * @param testName
     *            name of the test case
     */
    public SunriseCHTest(String testName) {
	super(testName);
    }

    public void testGetAvailableMessages() {
	try {
	    SunriseCH Service = new SunriseCH();
	    Service.getAvailableMessages();
	    fail("Should have thrown AvailableMessagesUnknownException");
	} catch (AvailableMessagesUnknownException le) {

	} catch (Exception e) {
	    fail("Should have thrown AvailableMessagesUnknownException: "
		    + e.getMessage());
	}
    }

    public void testLoginEmpty() {
	try {
	    SunriseCH Service = new SunriseCH();
	    Service.doLogin("", "");
	    fail("Should have thrown LoginFailedException");
	} catch (LoginFailedException le) {

	} catch (Exception e) {
	    fail("Should have thrown LoginFailedException: " + e.getMessage());
	}
    }

    public void testLoginWrong() {
	try {
	    SunriseCH Service = new SunriseCH();
	    Service.doLogin("unknownUser", "wrongPassword");
	    fail("Should have thrown LoginFailedException");
	} catch (LoginFailedException le) {

	} catch (Exception e) {
	    fail("Should have thrown LoginFailedException: " + e.getMessage());
	}
    }

    public void testSendMessageEmptyMessage() {
	try {
	    SunriseCH Service = new SunriseCH();
	    Service.sendShortMessage("0761234567", "");
	    fail("Should have thrown NotSentException");
	} catch (NotSentException le) {

	} catch (Exception e) {
	    fail("Should have thrown NotSentException: " + e.getMessage());
	}
    }

    public void testSendMessageMessageTooLong() {
	try {
	    SunriseCH Service = new SunriseCH();
	    Service.setLoggedIn(true);
	    String message = "";
	    for (int i = 0; i < 20; i++) {
		message += "too long ";
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
	    SunriseCH Service = new SunriseCH();
	    Service.sendShortMessage("076 123 45 67", "test message");
	    fail("Should have thrown NotSentException");
	} catch (NotSentException le) {

	} catch (Exception e) {
	    fail("Should have thrown NotSentException: " + e.getMessage());
	}
    }

    public void testSendMessagePhoneNumberWrong2() {
	try {
	    SunriseCH Service = new SunriseCH();
	    Service.sendShortMessage("+41 076 123 45 67", "test message");
	    fail("Should have thrown NotSentException");
	} catch (NotSentException le) {

	} catch (Exception e) {
	    fail("Should have thrown NotSentException: " + e.getMessage());
	}
    }
}
