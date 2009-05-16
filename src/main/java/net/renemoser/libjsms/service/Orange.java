package net.renemoser.libjsms.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Orange CH SMS Service
 * 
 * @author René Moser <mail@renemoser.net>
 * @version 0.2
 */
public class Orange extends Provider implements ShortMessageService {

	private static final String HOST = "https://www.orange.ch";
	private static final int MESSAGE_LENGHT = 144;

	/**
	 * @throws Exception
	 */
	public Orange() throws Exception {
		super();
	}
	
	public void doLogin(String userid, String password) throws Exception {
		// Make empty
		_shortMessage = "";
		_phoneNumber = "";
		
		// Trim inputs
		userid = userid.trim();
		password = password.trim();
		
		// Empty user id or password
	    if (userid.isEmpty() || password.isEmpty()) {
    	    throw new Exception("UserID or password is empty!");     
	    }
	    
	    URL url = new URL(HOST+"/footer/login/loginForm?ts=1242151218216");
		_conn = (HttpURLConnection) url.openConnection();
	    _conn.setUseCaches (false);
	    _conn.setDoInput(true);
	    _conn.setDoOutput(true);
	    
	    _conn.setRequestMethod("POST");
	    _conn.setRequestProperty("User-Agent", "libJSMS");	    
	    _conn.setRequestProperty("Accept-Language","en");
	    _conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

	    Hashtable<String,String> parameters = new Hashtable<String,String>();

	    parameters.put("username", userid);
	    parameters.put("password", password);
	    parameters.put("wui_target_id","loginButton");
	    parameters.put("wui_event_id","onclick");
	    parameters.put("redirect","");
	    parameters.put("loginButton","Login");
	    
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
	    	response.append(line.replaceAll("<[^>]+>","").trim());
	    	response.append('\r');
	    }
	    rd.close();
	    
	    if (response.toString().indexOf("Your username and/or password are not valid. Please try again.") > 0) {
	    	throw new Exception("Your username and/or password are not valid. Please try again.");
	    }
	    
	    // Set cookies 
	    String headerName = null;
	    for (int i = 1; (headerName = _conn.getHeaderFieldKey(i)) != null; i++) {
	     	if (headerName.equals("Set-Cookie")) {                  
	     		String cookie = _conn.getHeaderField(i);
	     		cookie = cookie.substring(0, cookie.indexOf(";"));
	            String cookieName = cookie.substring(0, cookie.indexOf("="));
	            String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
	            if (!cookieValue.isEmpty()) {
	            	_cookies.put(cookieName, cookieValue);
	            }
	     	}
	    }
	    	    
	    if(_conn != null) {
	    	_conn.disconnect(); 
	    }
	    
	    // Login unsuccessful if no cookies are set
		if(!_cookies.containsKey("ades.lb") || 
				!_cookies.containsKey("JSESSIONID") || 
				!_cookies.containsKey("user.session")
						) {
			throw new Exception("Login unsuccessful.");
		}
		
		_isLoggedIn = true;
	}

	public void sendShortMessage(String phoneNumber, String shortMessage) throws Exception {		
		// Make empty
		_shortMessage = "";
		_phoneNumber = "";
		
		shortMessage = shortMessage.trim();
		phoneNumber = phoneNumber.trim();
        
		if (shortMessage.length() > MESSAGE_LENGHT) {
            throw new Exception("Your Message is too long!");
        }
		
        if (shortMessage.isEmpty()) {
        	throw new Exception("Your Message is empty!");
        }
        
        // 0791234567 or +41791234567
        if ((phoneNumber.length() != 10 && phoneNumber.length() != 12 ) || 
        		!phoneNumber.matches("^[0-9+]+$")) {
        	throw new Exception("Phonenumber '" + phoneNumber + "' looks wrong!");
        }
        
        if(!_isLoggedIn) {
        	throw new Exception("You are not logged in!");        	
        }
             
	    URL url = new URL(HOST+"/myorange/sms/smsForm?ts=1242151653520");
		_conn = (HttpURLConnection) url.openConnection();
	    _conn.setUseCaches(false);
	    _conn.setDoInput(true);
	    _conn.setDoOutput(true);
	    
	    _conn.setRequestMethod("POST");
	    _conn.setRequestProperty("User-Agent", "libJSMS");	    
	    _conn.setRequestProperty("Accept-Language","en");
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
	    parameters.put("wui_target_id", "sendButton");
	    parameters.put("wui_event_id", "onclick");
	    parameters.put("destinationNumberInput", phoneNumber);
	    parameters.put("messageInput", shortMessage);
	    
	    int charsLeft = MESSAGE_LENGHT - shortMessage.length();
	    parameters.put("charNumberLeftOutput", Integer.toString(charsLeft));

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
	    
	    if (response.toString().indexOf("Your SMS has been sent.") <= 0) {
	    	throw new Exception("Message has not been sent.");
	    }
	    
	    _phoneNumber = phoneNumber;
	    _shortMessage = shortMessage;
	}
	
	@Override
	public int getAvailableMessages() throws Exception {
        if(!_isLoggedIn) {
        	throw new Exception("You are not logged in!");        	
        }
        
        URL url = new URL(HOST+"/myorange/sms/smsForm?ts=1242151653520");
		_conn = (HttpURLConnection) url.openConnection();
	    _conn.setUseCaches (false);
	    _conn.setDoInput(true);
	    _conn.setDoOutput(true);
	    
	    _conn.setRequestMethod("GET");
	    _conn.setRequestProperty("User-Agent", "libJSMS");	    
	    _conn.setRequestProperty("Accept-Language","en");
	    
	    StringBuffer cookieString = new StringBuffer();
	    Set<String> cookieSet = _cookies.keySet();
	    Iterator<String> itr = cookieSet.iterator();    
	    while (itr.hasNext()) {
	    	 String key = itr.next();
	    	 cookieString.append(key + "=" + _cookies.get(key)+";");
	    }
	    _conn.setRequestProperty("Cookie", cookieString.toString());
	    
	    InputStream is = _conn.getInputStream();
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	    String line;

	    _availableMessages = -1;    
	    Pattern smsCounterPattern = Pattern.compile("This month, you can still send ([0-9]{1,3}) free SMS.");
	    while((line = rd.readLine()) != null) {
	    	line = line.replaceAll("<[^>]+>","").trim();
	    	if (!line.isEmpty()) {
	    		Matcher smsCounterMatcher = smsCounterPattern.matcher(line);
	    		if (smsCounterMatcher.matches()) {
	    			String smsCounterString = smsCounterMatcher.group(1);
	    			_availableMessages = Integer.valueOf(smsCounterString);	
	    		}
	    	}
	    }
	    rd.close();

	    if (_availableMessages == -1) {
	    	throw new Exception("Available Messages is unknown.");
	    }
        
        return _availableMessages;
	}
}