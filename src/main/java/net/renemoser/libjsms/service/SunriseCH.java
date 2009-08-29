package net.renemoser.libjsms.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	// Make empty
	_shortMessage = "";
	_phoneNumber = "";

	// Trim inputs
	userid = userid.trim();
	password = password.trim();

	// Empty user id or password
	if (userid == null || userid.equals("") || password == null
		|| password.equals("")) {
	    throw new LoginFailedException("UserID or password is empty!");
	}

	URL url = new URL(
		HOST
			+ "/mip/dyn/login/login?SAMLRequest=fVLJTsMwEL0j8Q%2BW79kqQGA1qUoRohJL1AYO3FxnkrpNxsHjtPD3pGnLcoCjn5%2FfMp7h6L2u2AYsaYMxj%2FyQM0Blco1lzJ%2BzW%2B%2BSj5LTkyHJumrEuHVLnMFbC%2BRY9xJJ9Bcxby0KI0mTQFkDCafEfPxwLwZ%2BKBprnFGm4mx6E3OZy2YtscG11qpeQYFqoXGdr81KQYmrCosiL%2FOas5djrMEu1pSohSmSk%2Bg6KAyvvDDywsssOhPhhTg%2Ff%2BUsPThda9w3%2BC%2FWYk8icZdlqZc%2BzbNeYKNzsI8dO%2BalMWUFvjL1zj6VRHrTwc62wNmYCKzr8k0MUluDnYPdaAXPs%2FuYL51rSATBdrv1v1UCGVCLVlN3WgZSEU%2F6wYq%2Bm%2F0x0f%2BTy6M1T77Fh8EPqeTwYbse05vUVFp9sHFVme3EgnRfJW6NraX72y3yox7RuVf0VNEiNaB0oSHnLEj2rr83o9uXTw%3D%3D&RelayState=https%3A%2F%2Fwww.google.com%2Fa%2Fsunrise.ch%2FServiceLogin%3Fcontinue%3Dhttp%253A%252F%252Fpartnerpage.google.com%252Fsunrise.ch%252Fdefault%252Fpostlogin%253Fpid%253Dsunrise.ch%2526url%253Dhttp%253A%252F%252Fpartnerpage.google.com%252Fsunrise.ch%26followup%3Dhttp%253A%252F%252Fpartnerpage.google.com%252Fsunrise.ch%252Fdefault%252Fpostlogin%253Fpid%253Dsunrise.ch%2526url%253Dhttp%253A%252F%252Fpartnerpage.google.com%252Fsunrise.ch%26service%3Dig%26passive%3Dtrue%26cd%3DUS%26hl%3Dde%26nui%3D1%26ltmpl%3Ddefault%26go%3Dtrue%26passive_sso%3Dtrue");
	_conn = (HttpURLConnection) url.openConnection();
	_conn.setUseCaches(false);
	_conn.setDoInput(true);
	_conn.setDoOutput(true);

	_conn.setRequestMethod("POST");
	_conn.setRequestProperty("User-Agent", "libJSMS");
	_conn.setRequestProperty("Accept-Language", "de-de");
	_conn.setRequestProperty("Content-Type",
		"application/x-www-form-urlencoded");

	Hashtable<String, String> parameters = new Hashtable<String, String>();

	parameters.put("username", userid);
	parameters.put("password", password);
	parameters.put("_remember", "on");

	StringBuffer request = buildRequest(parameters);

	_conn.setRequestProperty("Content-Length", Integer.toString(request
		.toString().getBytes().length));

	OutputStream outStream = _conn.getOutputStream();
	outStream.write(request.toString().getBytes());
	outStream.flush();
	outStream.close();

	// Get Response
	InputStream is = _conn.getInputStream();

	BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	String line;
	StringBuffer response = new StringBuffer();
	while ((line = rd.readLine()) != null) {
	    response.append(line);
	    response.append('\r');
	}
	rd.close();

	// Set cookies
	String headerName = null;
	for (int i = 1; (headerName = _conn.getHeaderFieldKey(i)) != null; i++) {
	    if (headerName.equals("Set-Cookie")) {
		String cookie = _conn.getHeaderField(i);
		cookie = cookie.substring(0, cookie.indexOf(";"));
		String cookieName = ""
			+ cookie.substring(0, cookie.indexOf("="));
		String cookieValue = ""
			+ cookie.substring(cookie.indexOf("=") + 1, cookie
				.length());
		if (!cookieValue.equals("")) {
		    _cookies.put(cookieName, cookieValue);
		}
	    }
	}

	if (_conn != null) {
	    _conn.disconnect();
	}

	// Login unsuccessful if no cookies are set
	if (!_cookies.containsKey("SMIP")
		|| !_cookies.containsKey("JSESSIONID")) {
	    throw new LoginFailedException(
		    "Login unsuccessful. Warning: Sunrise only allow 5 attempts in 10 minutes.");
	}
	_isLoggedIn = true;
    }

    @Override
    public int getAvailableMessages() throws AvailableMessagesUnknownException,
	    Exception {
	if (!_isLoggedIn) {
	    throw new AvailableMessagesUnknownException(
		    "You are not logged in!");
	}

	URL url = new URL(HOST + "/mip/dyn/sms/sms?lang=de");
	_conn = (HttpURLConnection) url.openConnection();
	_conn.setUseCaches(false);
	_conn.setDoInput(true);
	_conn.setDoOutput(true);

	_conn.setRequestMethod("GET");
	_conn.setRequestProperty("User-Agent", "libJSMS");
	_conn.setRequestProperty("Accept-Language", "de-de");
	_conn
		.setRequestProperty(
			"Cookie",
			"JSESSIONID="
				+ _cookies.get("JSESSIONID")
				+ ";SMIP="
				+ _cookies.get("SMIP")
				+ "; org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE=de; mip_msg_dispContacts=0");

	_availableMessages = matchAvailableMessages("Gratis ([0-9]{2,3})");
	if (_availableMessages == -1) {
	    throw new AvailableMessagesUnknownException(
		    "Available messages is unknown.");
	}
	return _availableMessages;
    }

    @Override
    public void sendShortMessage(String phoneNumber, String shortMessage)
	    throws NotSentException, Exception {
	// Make empty
	_shortMessage = "";
	_phoneNumber = "";

	shortMessage = shortMessage.trim();
	phoneNumber = phoneNumber.trim();

	if (shortMessage.length() > MESSAGE_LENGHT) {
	    throw new NotSentException("Your message is too long!");
	}

	if (shortMessage == null || shortMessage.equals("")) {
	    throw new NotSentException("Your message is empty!");
	}

	// 0791234567 or +41791234567
	if ((phoneNumber.length() != 10 && phoneNumber.length() != 12)
		|| !phoneNumber.matches("^[0-9+]+$")) {
	    throw new NotSentException("Phone number '" + phoneNumber
		    + "' looks wrong!");
	}

	if (!_isLoggedIn) {
	    throw new Exception("You are not logged in!");
	}

	URL url = new URL(HOST + "/mip/dyn/sms/sms?lang=de");
	_conn = (HttpURLConnection) url.openConnection();
	_conn.setUseCaches(false);
	_conn.setDoInput(true);
	_conn.setDoOutput(true);

	_conn.setRequestMethod("POST");
	_conn.setRequestProperty("User-Agent", "libJSMS");
	_conn.setRequestProperty("Accept-Language", "de-de");
	_conn.setRequestProperty("Content-Type",
		"application/x-www-form-urlencoded");
	_conn
		.setRequestProperty(
			"Cookie",
			"JSESSIONID="
				+ _cookies.get("JSESSIONID")
				+ ";SMIP="
				+ _cookies.get("SMIP")
				+ "; org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE=de; mip_msg_dispContacts=0");

	Hashtable<String, String> parameters = new Hashtable<String, String>();

	parameters.put("recipient", phoneNumber);
	parameters.put("message", shortMessage);
	parameters.put("type", "sms");
	parameters.put("task", "send");
	parameters.put("send", "send");

	int charsLeft = MESSAGE_LENGHT - shortMessage.length();
	parameters.put("charsLeft", Integer.toString(charsLeft) + " / 1");

	StringBuffer request = buildRequest(parameters);

	_conn.setRequestProperty("Content-Length", Integer.toString(request
		.toString().getBytes().length));

	OutputStream outStream = _conn.getOutputStream();
	outStream.write(request.toString().getBytes());
	outStream.flush();
	outStream.close();

	// Get Response
	InputStream is = _conn.getInputStream();
	BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	String line;
	StringBuffer response = new StringBuffer();
	while ((line = rd.readLine()) != null) {
	    response.append(line.replaceAll("<[^>]+>", "").trim());
	    response.append('\r');
	}
	rd.close();

	if (response.toString().indexOf(
		"SMS wurde an " + phoneNumber + " gesendet.") <= 0) {
	    throw new NotSentException("Message has not been sent.");
	}

	_phoneNumber = phoneNumber;
	_shortMessage = shortMessage;
    }
}