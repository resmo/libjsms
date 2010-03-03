/**
 * 
 */
package net.renemoser.libjsms.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.renemoser.libjsms.exception.AvailableMessagesUnknownException;
import net.renemoser.libjsms.exception.LoginFailedException;
import net.renemoser.libjsms.exception.NotSentException;

/**
 * @author René Moser <mail@renemoser.net>
 * @since 0.3
 * 
 */
public abstract class Operator implements ShortMessageService {

    private final Hashtable<String, String> cookies = new Hashtable<String, String>();
    private HttpURLConnection conn;
    private String shortMessage, phoneNumber, password, userId;
    private boolean isLoggedIn;

    public Operator() throws Exception {
	System.getProperties().put("java.protocol.handler.pkgs",
		"com.sun.net.ssl.internal.www.protocol");
	java.security.Security
		.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
    }

    public void setPassword(String password) throws LoginFailedException {
	password = password.trim();
	if (password == null || password.equals("")) {
	    throw new LoginFailedException("Password is empty!");
	}
	this.password = password;
    }

    public String getPassword() throws LoginFailedException {
	if (password == null) {
	    throw new LoginFailedException("Password not set");
	}
	return password;
    }

    public void setUserId(String userId) throws LoginFailedException {
	userId = userId.trim();
	if (userId.length() == 0) {
	    throw new LoginFailedException("User id is empty");
	}
	this.userId = userId;
    }

    public String getUserId() throws LoginFailedException {
	if (userId == null) {
	    throw new LoginFailedException("User ID not set");
	}
	return userId;
    }

    public void setPhoneNumber(String phoneNumber) throws NotSentException {
	phoneNumber = phoneNumber.trim();

	// 0791234567 or +41791234567
	if ((phoneNumber.length() != 10 && phoneNumber.length() != 12)
		|| !phoneNumber.matches("^[0-9+]+$")) {
	    throw new NotSentException("Phone number '" + phoneNumber
		    + "' looks wrong!");
	}
	this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() throws NotSentException {
	if (phoneNumber == null) {
	    throw new NotSentException("Phone number not set");
	}
	return phoneNumber;
    }

    public String getShortMessage() throws NotSentException {
	if (shortMessage == null) {
	    throw new NotSentException("Short message not set");
	}
	return shortMessage;
    }

    public void setShortMessage(String shortMessage) throws NotSentException {
	shortMessage = shortMessage.trim();
	if (shortMessage == null || shortMessage.equals("")) {
	    throw new NotSentException("Your message is empty!");
	}
	this.shortMessage = shortMessage;
    }

    public void doLogin() throws LoginFailedException, Exception {
	doLogin(getUserId(), getPassword());
    }

    public void doLogin(String userId, String password)
	    throws LoginFailedException, Exception {
	setUserId(userId);
	setPassword(password);
    }

    public void sendShortMessage() throws NotSentException, Exception {
	this.sendShortMessage(getPhoneNumber(), getShortMessage());
    }

    public void sendShortMessage(String phoneNumber, String shortMessage)
	    throws NotSentException, Exception {

	setPhoneNumber(phoneNumber);
	setShortMessage(shortMessage);

	if (!isLoggedIn()) {
	    throw new NotSentException("You are not logged in!");
	}
    }

    public int getAvailableMessages() throws Exception {
	throw new AvailableMessagesUnknownException(
		"Available messages are unknown.");
    }

    /**
     * Initialize the HTTP URL connection
     * 
     * @param url
     * @return connection
     * @throws IOException
     */
    protected HttpURLConnection buildConnection(URL url) throws IOException {
	conn = (HttpURLConnection) url.openConnection();
	conn.setUseCaches(false);
	conn.setDoInput(true);
	conn.setDoOutput(true);

	conn.setRequestMethod("POST");
	conn.setRequestProperty("User-Agent", "libjsms");
	conn.setRequestProperty("Accept-Language", "en");
	conn.setRequestProperty("Content-Type",
		"application/x-www-form-urlencoded");
	return conn;
    }

    /**
     * Return HashMap of added cookies
     * 
     * @return cookies
     */
    protected Hashtable<String, String> getCookies() {
	return cookies;
    }

    /**
     * Builds a request string of given parameters (Hashtable)
     * 
     * @param parameters
     * @return request
     * @throws UnsupportedEncodingException
     */
    protected StringBuffer buildRequest(Hashtable<String, String> parameters)
	    throws UnsupportedEncodingException {
	StringBuffer request = new StringBuffer();
	Enumeration<String> keys = parameters.keys();

	while (keys.hasMoreElements()) {
	    String key = keys.nextElement();
	    request.append(key);
	    request.append("=");
	    request.append(URLEncoder.encode(parameters.get(key), "UTF8"));
	    request.append("&");
	}
	return request;
    }

    /**
     * Returns true if logged in
     * 
     * @return isLoggedIn
     */
    protected boolean isLoggedIn() {
	return isLoggedIn;
    }

    /**
     * Sets login state
     * 
     * @param loggedIn
     */
    protected void setLoggedIn(boolean loggedIn) {
	isLoggedIn = loggedIn;
    }

    /**
     * Returns the integer value of input stream by given pattern
     * 
     * @throws IOException
     * @throws NumberFormatException
     */
    protected int matchAvailableMessages(String pattern) throws IOException,
	    NumberFormatException {
	InputStream is = conn.getInputStream();
	BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	String line;

	int availableMessages = -1;
	Pattern smsCounterPattern = Pattern.compile(pattern);
	while ((line = rd.readLine()) != null) {
	    // Strip html tags
	    line = "" + line.replaceAll("<[^>]+>", "").trim();
	    if (!line.equals("")) {
		Matcher smsCounterMatcher = smsCounterPattern.matcher(line);
		if (smsCounterMatcher.matches()) {
		    String smsCounterString = smsCounterMatcher.group(1);
		    availableMessages = Integer.valueOf(smsCounterString);
		}
	    }
	}
	rd.close();
	return availableMessages;
    }

    /**
     * Grabs cookies of HTTP response and add them to Hashtable
     * 
     * @param conn
     */
    protected void setCookies(HttpURLConnection conn) {
	// Set cookies
	String headerName = null;
	for (int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++) {
	    if (headerName.equals("Set-Cookie")) {
		String cookie = conn.getHeaderField(i);
		cookie = cookie.substring(0, cookie.indexOf(";"));
		String cookieName = ""
			+ cookie.substring(0, cookie.indexOf("="));
		String cookieValue = ""
			+ cookie.substring(cookie.indexOf("=") + 1, cookie
				.length());
		if (!cookieValue.equals("")) {
		    cookies.put(cookieName, cookieValue);
		}
	    }
	}
    }

    /**
     * Get HTTP response
     * 
     * @param is
     * @return response
     * @throws IOException
     */
    protected StringBuffer getResponse(InputStream is) throws IOException {
	BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	String line;
	StringBuffer response = new StringBuffer();
	while ((line = rd.readLine()) != null) {
	    response.append(line.replaceAll("<[^>]+>", "").trim());
	    response.append('\r');
	}
	rd.close();
	return response;
    }
}
