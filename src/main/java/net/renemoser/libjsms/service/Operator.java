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

import net.renemoser.libjsms.exception.LoginFailedException;
import net.renemoser.libjsms.exception.NotSentException;

/**
 * @author René Moser <mail@renemoser.net>
 * @since 0.3
 * 
 */
public abstract class Operator {
    private final Hashtable<String, String> cookies = new Hashtable<String, String>();
    private HttpURLConnection conn;
    private String shortMessage;
    private String phoneNumber;
    private boolean isLoggedIn;

    /**
     * Constructor
     * 
     * @throws Exception
     */
    public Operator() throws Exception {
	System.getProperties().put("java.protocol.handler.pkgs",
		"com.sun.net.ssl.internal.www.protocol");
	java.security.Security
		.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
    }

    /**
     * Returns available messages left
     * 
     * @return availableMessages
     * @throws Exception
     */
    public int getAvailableMessages() throws Exception {
	throw new Exception("Available messages are unknown.");
    }

    /**
     * Returns phone number
     * 
     * @return phoneNumber
     */
    public String getPhoneNumber() {
	return phoneNumber;
    }

    /**
     * Returns short message
     * 
     * @return shortMessage
     */
    public String getShortMessage() {
	return shortMessage;
    }

    /**
     * Sets short message
     * 
     * @param shortMessage
     */
    protected void setShortMessage(String shortMessage) {
	this.shortMessage = shortMessage;
    }

    /**
     * @param userid
     * @param password
     * @throws LoginFailedException
     * @throws Exception
     */
    protected void doLogin(String userid, String password)
	    throws LoginFailedException, Exception {
	// Trim inputs
	userid = userid.trim();
	password = password.trim();

	// Empty user id or password
	if (userid == null || userid.equals("") || password == null
		|| password.equals("")) {
	    throw new LoginFailedException("UserID or password is empty!");
	}
    }

    /**
     * Validates phone number and short message
     * 
     * @param phoneNumber
     * @param shortMessage
     * @throws Exception
     */
    protected void sendShortMessage(String phoneNumber, String shortMessage)
	    throws Exception {
	shortMessage = shortMessage.trim();
	phoneNumber = phoneNumber.trim();

	if (shortMessage == null || shortMessage.equals("")) {
	    throw new NotSentException("Your message is empty!");
	}

	// 0791234567 or +41791234567
	if ((phoneNumber.length() != 10 && phoneNumber.length() != 12)
		|| !phoneNumber.matches("^[0-9+]+$")) {
	    throw new NotSentException("Phone number '" + phoneNumber
		    + "' looks wrong!");
	}

	if (!isLoggedIn()) {
	    throw new Exception("You are not logged in!");
	}
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
     * Sets the phone number
     * 
     * @param phoneNumber
     */
    protected void setPhoneNumber(String phoneNumber) {
	this.phoneNumber = phoneNumber;
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
