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
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author René Moser <mail@renemoser.net>
 * @since 0.3
 * 
 */
public abstract class Operator {
    protected Hashtable<String, String> _cookies = new Hashtable<String, String>();
    protected HttpURLConnection _conn;

    protected String _shortMessage = "";
    protected String _phoneNumber = "";
    protected int _availableMessages = -1;
    protected boolean _isLoggedIn = false;

    public Operator() throws Exception {
	System.getProperties().put("java.protocol.handler.pkgs",
		"com.sun.net.ssl.internal.www.protocol");
	java.security.Security
		.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
    }

    /**
     * @param parameters
     * @return
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

    public void doLogin(String userid, String password) throws Exception {
	throw new Exception("doLogin() not implemented");
    }

    public int getAvailableMessages() throws Exception {
	if (_availableMessages < 0) {
	    throw new Exception("Available messages are unknown.");
	}
	return _availableMessages;
    }

    public String getPhoneNumber() throws Exception {
	return _phoneNumber;
    }

    public String getShortMessage() throws Exception {
	return _shortMessage;
    }

    /**
     * @throws IOException
     * @throws NumberFormatException
     */
    protected int matchAvailableMessages(String pattern) throws IOException,
	    NumberFormatException {
	InputStream is = _conn.getInputStream();
	BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	String line;

	int availableMessages = -1;
	Pattern smsCounterPattern = Pattern.compile(pattern);
	while ((line = rd.readLine()) != null) {
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

    public void sendShortMessage(String phoneNumber, String shortMessage)
	    throws Exception {
	throw new Exception("sendShortMessage() not implemented");
    }
}
