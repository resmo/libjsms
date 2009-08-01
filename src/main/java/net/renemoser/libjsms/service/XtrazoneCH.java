/**
 * 
 */
package net.renemoser.libjsms.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Rene Moser <mail@renemoser.net>
 *
 */
public class XtrazoneCH extends Provider implements ShortMessageService {

	private final static String HOST = "https://www.swisscom-mobile.ch";
	private final static int MESSAGE_LENGHT = 160;
	
	/**
	 * @throws Exception
	 */
	public XtrazoneCH() throws Exception {
		super();
	}

	@Override
	public void doLogin(String userid, String password) throws Exception {
		// Make empty
		_shortMessage = "";
		_phoneNumber = "";
		
		// Trim inputs
		userid = userid.trim();
		password = password.trim();
		
		// Empty user id or password
	    if (userid == null || userid.equals("") || password == null || password.equals("")) {
    	    throw new Exception("UserID or password is empty!");     
	    }
	    URL url = new URL(HOST+"/youth/youth_zone_home-de.aspx?login");
		_conn = (HttpURLConnection) url.openConnection();
	    _conn.setUseCaches (false);
	    _conn.setDoInput(true);
	    _conn.setDoOutput(true);
	    
	    _conn.setRequestMethod("POST");
	    _conn.setRequestProperty("User-Agent", "libJSMS");	    
	    _conn.setRequestProperty("Accept-Language","de-de");
	    _conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

	    Hashtable<String,String> parameters = new Hashtable<String,String>();

	    parameters.put("isiwebuserid", userid);
	    parameters.put("isiwebpasswd", password);
	    parameters.put("isiwebjavascript","No");
	    parameters.put("isiwebappid","mobile");
	    parameters.put("isiwebmethod","authenticate");
	    parameters.put("isiweburi", "/youth/youth_zone_home-de.aspx");
	    parameters.put("isiwebargs","");
	    parameters.put("login.x", "24");
	    parameters.put("login.y", "7");

	    StringBuffer request = new StringBuffer();
	    Enumeration<String> keys = parameters.keys();

	    while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            request.append(key);
            request.append("=");
            request.append(URLEncoder.encode(parameters.get(key),"UTF8"));
            request.append("&");
        }
	    
	    _conn.setRequestProperty("Content-Length", Integer.toString(request.toString().getBytes().length));	      

	    OutputStream outStream = _conn.getOutputStream();
	    outStream.write(request.toString().getBytes());
	    outStream.flush();
	    outStream.close();	    
	    
	    //Get Response	
	    InputStream is = _conn.getInputStream();
	    	    
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	    String line;
	    StringBuffer response = new StringBuffer(); 
	    while((line = rd.readLine()) != null) {
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
	            String cookieName = "" + cookie.substring(0, cookie.indexOf("="));
	            String cookieValue = "" + cookie.substring(cookie.indexOf("=") + 1, cookie.length());
	            if (!cookieValue.equals("")) {
	            	_cookies.put(cookieName, cookieValue);
	            }
	     	}
	    }
	    	    
	    if(_conn != null) {
	    	_conn.disconnect(); 
	    }
	    
	    // Login unsuccessful if no cookies are set
		if(!_cookies.containsKey("Navajo")) {
			throw new Exception("Login unsuccessful.");
		}
		_isLoggedIn = true;
	}

	@Override
	public int getAvailableMessages() throws Exception {
		// TODO Find out what implement here
		return super.getAvailableMessages();
	}

	@Override
	public void sendShortMessage(String phoneNumber, String shortMessage)
			throws Exception {
		// Make empty
		_shortMessage = "";
		_phoneNumber = "";
		
		shortMessage = shortMessage.trim();
		phoneNumber = phoneNumber.trim();
        
		if (shortMessage.length() > MESSAGE_LENGHT) {
            throw new Exception("Your message is too long!");
        }
		
        if (shortMessage == null || shortMessage.equals("")) {
        	throw new Exception("Your message is empty!");
        }
        
        // 0791234567 or +41791234567
        if ((phoneNumber.length() != 10 && phoneNumber.length() != 12 ) || 
        		!phoneNumber.matches("^[0-9+]+$")) {
        	throw new Exception("Phone number '" + phoneNumber + "' looks wrong!");
        }
        
        if(!_isLoggedIn) {
        	throw new Exception("You are not logged in!");        	
        }
        
	    URL url = new URL(HOST+"");
		_conn = (HttpURLConnection) url.openConnection();
	    _conn.setUseCaches (false);
	    _conn.setDoInput(true);
	    _conn.setDoOutput(true);
	    
	    _conn.setRequestMethod("POST");
	    _conn.setRequestProperty("User-Agent", "libJSMS");	    
	    _conn.setRequestProperty("Accept-Language","de-de");
	    _conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
	    
	    StringBuffer cookieString = new StringBuffer();
	    Set<String> cookieSet = _cookies.keySet();
	    Iterator<String> itr = cookieSet.iterator();    
	    while (itr.hasNext()) {
	    	 String key = itr.next();
	    	 cookieString.append(key + "=" + _cookies.get(key)+";");
	    }
	    
	    _conn.setRequestProperty("Cookie", cookieString.toString());
	    
	    Hashtable<String,String> parameters = new Hashtable<String,String>();

	    parameters.put("CobYouthSMSSenden%3AtxtNewReceiver", phoneNumber);
	    parameters.put("CobYouthSMSSenden%3AtxtMessage", shortMessage);
	    parameters.put("CobYouthSMSSenden%3AbtnSend", "Senden");
	    
	    int charsLeft = MESSAGE_LENGHT - shortMessage.length();
	    parameters.put("charsLeft", Integer.toString(charsLeft) + " / 1");

	    StringBuffer request = new StringBuffer();
	    Enumeration<String> keys = parameters.keys();

	    while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            request.append(key);
            request.append("=");
            request.append(URLEncoder.encode(parameters.get(key),"UTF8"));
            request.append("&");
        }
	    
	    _conn.setRequestProperty("Content-Length", Integer.toString(request.toString().getBytes().length));
	    
	    OutputStream outStream = _conn.getOutputStream();
	    outStream.write(request.toString().getBytes());
	    outStream.flush();
	    outStream.close();
	    
	    // Get Response	
	    InputStream is = _conn.getInputStream();
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	    String line;
	    StringBuffer response = new StringBuffer(); 
	    while((line = rd.readLine()) != null) {
	    	response.append(line.replaceAll("<[^>]+>","").trim());
	    	response.append('\r');
	    }
	    rd.close();
	    
	    // TODO: Find out what the response is
	    if (response.toString().indexOf("") <= 0) {
	    	throw new Exception("Message has not been sent.");
	    }
	    
	    _phoneNumber = phoneNumber;
	    _shortMessage = shortMessage;
	}
}
