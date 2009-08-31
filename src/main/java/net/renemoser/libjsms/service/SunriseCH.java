package net.renemoser.libjsms.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;

import net.renemoser.libjsms.exception.AvailableMessagesUnknownException;
import net.renemoser.libjsms.exception.LoginFailedException;
import net.renemoser.libjsms.exception.NotSentException;

/**
 * Sunrise CH SMS Service
 * 
 * @author René Moser <mail@renemoser.net>
 * @version 0.2
 */
public class SunriseCH extends Operator implements ShortMessageService {

    private final static String HOST = "https://mip.sunrise.ch";
    private final static int MESSAGE_LENGHT = 160;

    /**
     * @throws Exception
     */
    public SunriseCH() throws Exception {
	super();
    }

    @Override
    public void doLogin(String userid, String password)
	    throws LoginFailedException, Exception {

	// Do some validation
	super.doLogin(userid, password);

	// Initialize connection
	URL url = new URL(
		HOST
			+ "/mip/dyn/login/login?SAMLRequest=fVLJTsMwEL0j8Q%2BW79kqQGA1qUoRohJL1AYO3FxnkrpNxsHjtPD3pGnLcoCjn5%2FfMp7h6L2u2AYsaYMxj%2FyQM0Blco1lzJ%2BzW%2B%2BSj5LTkyHJumrEuHVLnMFbC%2BRY9xJJ9Bcxby0KI0mTQFkDCafEfPxwLwZ%2BKBprnFGm4mx6E3OZy2YtscG11qpeQYFqoXGdr81KQYmrCosiL%2FOas5djrMEu1pSohSmSk%2Bg6KAyvvDDywsssOhPhhTg%2Ff%2BUsPThda9w3%2BC%2FWYk8icZdlqZc%2BzbNeYKNzsI8dO%2BalMWUFvjL1zj6VRHrTwc62wNmYCKzr8k0MUluDnYPdaAXPs%2FuYL51rSATBdrv1v1UCGVCLVlN3WgZSEU%2F6wYq%2Bm%2F0x0f%2BTy6M1T77Fh8EPqeTwYbse05vUVFp9sHFVme3EgnRfJW6NraX72y3yox7RuVf0VNEiNaB0oSHnLEj2rr83o9uXTw%3D%3D&RelayState=https%3A%2F%2Fwww.google.com%2Fa%2Fsunrise.ch%2FServiceLogin%3Fcontinue%3Dhttp%253A%252F%252Fpartnerpage.google.com%252Fsunrise.ch%252Fdefault%252Fpostlogin%253Fpid%253Dsunrise.ch%2526url%253Dhttp%253A%252F%252Fpartnerpage.google.com%252Fsunrise.ch%26followup%3Dhttp%253A%252F%252Fpartnerpage.google.com%252Fsunrise.ch%252Fdefault%252Fpostlogin%253Fpid%253Dsunrise.ch%2526url%253Dhttp%253A%252F%252Fpartnerpage.google.com%252Fsunrise.ch%26service%3Dig%26passive%3Dtrue%26cd%3DUS%26hl%3Dde%26nui%3D1%26ltmpl%3Ddefault%26go%3Dtrue%26passive_sso%3Dtrue");
	HttpURLConnection conn = buildConnection(url);
	conn.setRequestProperty("Accept-Language", "de-de");

	// Set parameters
	Hashtable<String, String> parameters = new Hashtable<String, String>();
	parameters.put("username", userid);
	parameters.put("password", password);
	parameters.put("_remember", "on");

	// Build request
	StringBuffer request = buildRequest(parameters);

	conn.setRequestProperty("Content-Length", Integer.toString(request
		.toString().getBytes().length));

	// Send request
	OutputStream outStream = conn.getOutputStream();
	outStream.write(request.toString().getBytes());
	outStream.flush();
	outStream.close();

	// Get Response
	getResponse(conn.getInputStream());
	setCookies(conn);

	// Disconnect
	if (conn != null) {
	    conn.disconnect();
	}

	// Login unsuccessful if no cookies are set
	if (!getCookies().containsKey("SMIP")
		|| !getCookies().containsKey("JSESSIONID")) {
	    throw new LoginFailedException(
		    "Login unsuccessful. Warning: Sunrise only allow 5 attempts in 10 minutes.");
	}
	setLoggedIn(true);
    }

    @Override
    public int getAvailableMessages() throws AvailableMessagesUnknownException,
	    Exception {
	if (!isLoggedIn()) {
	    throw new AvailableMessagesUnknownException(
		    "You are not logged in!");
	}

	URL url = new URL(HOST + "/mip/dyn/sms/sms?lang=de");
	HttpURLConnection conn = buildConnection(url);
	conn.setRequestMethod("GET");
	conn.setRequestProperty("Accept-Language", "de-de");
	conn
		.setRequestProperty(
			"Cookie",
			"JSESSIONID="
				+ getCookies().get("JSESSIONID")
				+ ";SMIP="
				+ getCookies().get("SMIP")
				+ "; org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE=de; mip_msg_dispContacts=0");

	int availableMessages = matchAvailableMessages("Gratis ([0-9]{2,3})");
	if (availableMessages == -1) {
	    throw new AvailableMessagesUnknownException(
		    "Available messages is unknown.");
	}
	return availableMessages;
    }

    @Override
    public void sendShortMessage(String phoneNumber, String shortMessage)
	    throws NotSentException, Exception {

	// Do some validations
	super.sendShortMessage(phoneNumber, shortMessage);

	if (shortMessage.length() > MESSAGE_LENGHT) {
	    throw new NotSentException("Your message is too long!");
	}

	// Initialize connection
	URL url = new URL(HOST + "/mip/dyn/sms/sms?lang=de");
	HttpURLConnection conn = buildConnection(url);
	conn.setRequestProperty("Accept-Language", "de-de");
	conn
		.setRequestProperty(
			"Cookie",
			"JSESSIONID="
				+ getCookies().get("JSESSIONID")
				+ ";SMIP="
				+ getCookies().get("SMIP")
				+ "; org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE=de; mip_msg_dispContacts=0");

	// Set parameters
	Hashtable<String, String> parameters = new Hashtable<String, String>();
	parameters.put("recipient", phoneNumber);
	parameters.put("message", shortMessage);
	parameters.put("type", "sms");
	parameters.put("task", "send");
	parameters.put("send", "send");

	int charsLeft = MESSAGE_LENGHT - shortMessage.length();
	parameters.put("charsLeft", Integer.toString(charsLeft) + " / 1");

	// Build request
	StringBuffer request = buildRequest(parameters);

	conn.setRequestProperty("Content-Length", Integer.toString(request
		.toString().getBytes().length));

	// Send request
	OutputStream outStream = conn.getOutputStream();
	outStream.write(request.toString().getBytes());
	outStream.flush();
	outStream.close();

	// Get Response
	InputStream is = conn.getInputStream();
	StringBuffer response = getResponse(is);

	if (response.toString().indexOf(
		"SMS wurde an " + phoneNumber + " gesendet.") <= 0) {
	    throw new NotSentException("Message has not been sent.");
	}

	setPhoneNumber(phoneNumber);
	setShortMessage(shortMessage);
    }
}