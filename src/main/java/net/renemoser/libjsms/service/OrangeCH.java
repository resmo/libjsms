package net.renemoser.libjsms.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import net.renemoser.libjsms.exception.AvailableMessagesUnknownException;
import net.renemoser.libjsms.exception.LoginFailedException;
import net.renemoser.libjsms.exception.NotSentException;

/**
 * Orange CH SMS Service
 * 
 * @author René Moser <mail@renemoser.net>
 * @version 0.3
 */
public class OrangeCH extends Operator implements ShortMessageService {

    private static final String HOST = "https://www.orange.ch";
    private static final int MESSAGE_LENGHT = 144;

    /**
     * @throws Exception
     */
    public OrangeCH() throws Exception {
	super();
    }

    @Override
    public void doLogin(String userId, String password)
	    throws LoginFailedException, Exception {

	super.doLogin(userId, password);

	URL url = new URL(HOST + "/footer/login/loginForm?ts=1242151218216");
	HttpURLConnection conn = buildConnection(url);

	Hashtable<String, String> parameters = new Hashtable<String, String>();
	parameters.put("username", getUserId());
	parameters.put("password", getPassword());
	parameters.put("wui_target_id", "loginButton");
	parameters.put("wui_event_id", "onclick");
	parameters.put("redirect", "");
	parameters.put("loginButton", "Login");

	StringBuffer request = buildRequest(parameters);

	conn.setRequestProperty("Content-Length", Integer.toString(request
		.toString().getBytes().length));

	OutputStream outStream = conn.getOutputStream();
	outStream.write(request.toString().getBytes());
	outStream.flush();
	outStream.close();

	// Get Response
	StringBuffer response = getResponse(conn.getInputStream());
	if (response
		.toString()
		.indexOf(
			"Your username and/or password are not valid. Please try again.") > 0) {
	    throw new LoginFailedException(
		    "Your username and/or password are not valid. Please try again.");
	}

	setCookies(conn);

	if (conn != null) {
	    conn.disconnect();
	}

	// Login unsuccessful if no cookies are set
	if (!getCookies().containsKey("ades.lb")
		|| !getCookies().containsKey("JSESSIONID")
		|| !getCookies().containsKey("user.session")) {
	    throw new LoginFailedException("Login unsuccessful.");
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

	URL url = new URL(HOST + "/myorange/sms/smsForm?ts=1242151653520");
	HttpURLConnection conn = buildConnection(url);
	conn.setRequestMethod("GET");

	StringBuffer cookieString = new StringBuffer();
	Set<String> cookieSet = getCookies().keySet();
	Iterator<String> itr = cookieSet.iterator();
	while (itr.hasNext()) {
	    String key = itr.next();
	    cookieString.append(key + "=" + getCookies().get(key) + ";");
	}

	conn.setRequestProperty("Cookie", cookieString.toString());

	int availableMessages = matchAvailableMessages("This month, you can still send ([0-9]{1,3}) free SMS.");
	if (availableMessages == -1) {
	    throw new AvailableMessagesUnknownException(
		    "Available messages is unknown.");
	}
	return availableMessages;
    }

    @Override
    public void setShortMessage(String shortMessage) throws NotSentException {
	super.setShortMessage(shortMessage);
	if (getShortMessage().length() > MESSAGE_LENGHT) {
	    throw new NotSentException("Your message is too long!");
	}
    }

    @Override
    public void sendShortMessage(String phoneNumber, String shortMessage)
	    throws NotSentException, Exception {

	super.sendShortMessage(phoneNumber, shortMessage);

	URL url = new URL(HOST + "/myorange/sms/smsForm?ts=1242151653520");
	HttpURLConnection conn = buildConnection(url);

	StringBuffer cookieString = new StringBuffer();
	Set<String> cookieSet = getCookies().keySet();
	Iterator<String> itr = cookieSet.iterator();
	while (itr.hasNext()) {
	    String key = itr.next();
	    cookieString.append(key + "=" + getCookies().get(key) + ";");
	}

	conn.setRequestProperty("Cookie", cookieString.toString());

	Hashtable<String, String> parameters = new Hashtable<String, String>();
	parameters.put("wui_target_id", "sendButton");
	parameters.put("wui_event_id", "onclick");
	parameters.put("destinationNumberInput", phoneNumber);
	parameters.put("messageInput", shortMessage);

	int charsLeft = MESSAGE_LENGHT - shortMessage.length();
	parameters.put("charNumberLeftOutput", Integer.toString(charsLeft));

	StringBuffer request = buildRequest(parameters);

	conn.setRequestProperty("Content-Length", Integer.toString(request
		.toString().getBytes().length));

	OutputStream outStream = conn.getOutputStream();
	outStream.write(request.toString().getBytes());
	outStream.flush();
	outStream.close();

	// Get Response
	InputStream is = conn.getInputStream();
	StringBuffer response = getResponse(is);

	if (response.toString().indexOf("Your SMS has been sent.") <= 0) {
	    throw new NotSentException("Message has not been sent.");
	}

    }
}