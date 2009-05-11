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

/**
 * Sunrise CH SMS Service
 * 
 * @author René Moser <mail@renemoser.net>
 * @version 0.1
 */
public class Sunrise implements ShortMessageService {

	private static final String HOST = "https://mip.sunrise.ch";
	private static final int MESSAGE_LENGHT = 160;
	
	private String _shortMessage = "";
	private String _phoneNumber = "";
	
	private Hashtable<String,String> _cookies = new Hashtable<String,String>(); 
	
	protected HttpURLConnection _conn;
	
	/**
	 * @throws Exception
	 */
	public Sunrise() throws Exception {
		System.getProperties().put("java.protocol.handler.pkgs","com.sun.net.ssl.internal.www.protocol");
		java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
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
	    
	    URL url = new URL(HOST+"/mip/dyn/login/login?SAMLRequest=fVLJTsMwEL0j8Q%2BW79kqQGA1qUoRohJL1AYO3FxnkrpNxsHjtPD3pGnLcoCjn5%2FfMp7h6L2u2AYsaYMxj%2FyQM0Blco1lzJ%2BzW%2B%2BSj5LTkyHJumrEuHVLnMFbC%2BRY9xJJ9Bcxby0KI0mTQFkDCafEfPxwLwZ%2BKBprnFGm4mx6E3OZy2YtscG11qpeQYFqoXGdr81KQYmrCosiL%2FOas5djrMEu1pSohSmSk%2Bg6KAyvvDDywsssOhPhhTg%2Ff%2BUsPThda9w3%2BC%2FWYk8icZdlqZc%2BzbNeYKNzsI8dO%2BalMWUFvjL1zj6VRHrTwc62wNmYCKzr8k0MUluDnYPdaAXPs%2FuYL51rSATBdrv1v1UCGVCLVlN3WgZSEU%2F6wYq%2Bm%2F0x0f%2BTy6M1T77Fh8EPqeTwYbse05vUVFp9sHFVme3EgnRfJW6NraX72y3yox7RuVf0VNEiNaB0oSHnLEj2rr83o9uXTw%3D%3D&RelayState=https%3A%2F%2Fwww.google.com%2Fa%2Fsunrise.ch%2FServiceLogin%3Fcontinue%3Dhttp%253A%252F%252Fpartnerpage.google.com%252Fsunrise.ch%252Fdefault%252Fpostlogin%253Fpid%253Dsunrise.ch%2526url%253Dhttp%253A%252F%252Fpartnerpage.google.com%252Fsunrise.ch%26followup%3Dhttp%253A%252F%252Fpartnerpage.google.com%252Fsunrise.ch%252Fdefault%252Fpostlogin%253Fpid%253Dsunrise.ch%2526url%253Dhttp%253A%252F%252Fpartnerpage.google.com%252Fsunrise.ch%26service%3Dig%26passive%3Dtrue%26cd%3DUS%26hl%3Dde%26nui%3D1%26ltmpl%3Ddefault%26go%3Dtrue%26passive_sso%3Dtrue");
		_conn = (HttpURLConnection) url.openConnection();
	    _conn.setUseCaches (false);
	    _conn.setDoInput(true);
	    _conn.setDoOutput(true);
	    
	    _conn.setRequestMethod("POST");
	    _conn.setRequestProperty("User-Agent", "libJSwissSMS");	    
	    _conn.setRequestProperty("Accept-Language","de-de");
	    _conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
	    _conn.setRequestProperty("Referer","https://mip.sunrise.ch/mip/dyn/login/login?SAMLRequest=fVLJTsMwEL0j8Q%2BW79kqQGA1qUoRohJL1AYO3FxnkrpNxsHjtPD3pGnLcoCjn5%2FfMp7h6L2u2AYsaYMxj%2FyQM0Blco1lzJ%2BzW%2B%2BSj5LTkyHJumrEuHVLnMFbC%2BRY9xJJ9Bcxby0KI0mTQFkDCafEfPxwLwZ%2BKBprnFGm4mx6E3OZy2YtscG11qpeQYFqoXGdr81KQYmrCosiL%2FOas5djrMEu1pSohSmSk%2Bg6KAyvvDDywsssOhPhhTg%2Ff%2BUsPThda9w3%2BC%2FWYk8icZdlqZc%2BzbNeYKNzsI8dO%2BalMWUFvjL1zj6VRHrTwc62wNmYCKzr8k0MUluDnYPdaAXPs%2FuYL51rSATBdrv1v1UCGVCLVlN3WgZSEU%2F6wYq%2Bm%2F0x0f%2BTy6M1T77Fh8EPqeTwYbse05vUVFp9sHFVme3EgnRfJW6NraX72y3yox7RuVf0VNEiNaB0oSHnLEj2rr83o9uXTw%3D%3D&RelayState=https%3A%2F%2Fwww.google.com%2Fa%2Fsunrise.ch%2FServiceLogin%3Fcontinue%3Dhttp%253A%252F%252Fpartnerpage.google.com%252Fsunrise.ch%252Fdefault%252Fpostlogin%253Fpid%253Dsunrise.ch%2526url%253Dhttp%253A%252F%252Fpartnerpage.google.com%252Fsunrise.ch%26followup%3Dhttp%253A%252F%252Fpartnerpage.google.com%252Fsunrise.ch%252Fdefault%252Fpostlogin%253Fpid%253Dsunrise.ch%2526url%253Dhttp%253A%252F%252Fpartnerpage.google.com%252Fsunrise.ch%26service%3Dig%26passive%3Dtrue%26cd%3DUS%26hl%3Dde%26nui%3D1%26ltmpl%3Ddefault%26go%3Dtrue%26passive_sso%3Dtrue");

	    Hashtable<String,String> parameters = new Hashtable<String,String>();

	    parameters.put("username", userid);
	    parameters.put("password", password);
	    parameters.put("_remember","on");

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
		if(!_cookies.containsKey("SMIP") || !_cookies.containsKey("JSESSIONID")) {
			throw new Exception("Login unsuccessful. Warning: Sunrise only allow 5 attempts in 10 minutes.");
		}
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
        
        if(_cookies.get("SMIP").isEmpty() || _cookies.get("JSESSIONID").isEmpty()) {
        	throw new Exception("You are not logged in!");        	
        }
             
	    URL url = new URL(HOST+"/mip/dyn/sms/sms?lang=de");
		_conn = (HttpURLConnection) url.openConnection();
	    _conn.setUseCaches (false);
	    _conn.setDoInput(true);
	    _conn.setDoOutput(true);
	    
	    _conn.setRequestMethod("POST");
	    _conn.setRequestProperty("User-Agent", "libJSwissSMS");	    
	    _conn.setRequestProperty("Accept-Language","de-de");
	    _conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
	    _conn.setRequestProperty("Cookie","JSESSIONID=" + _cookies.get("JSESSIONID") + ";SMIP=" + _cookies.get("SMIP") + "; org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE=de; mip_msg_dispContacts=0");
	    _conn.setRequestProperty("Referer","https://mip.sunrise.ch/mip/dyn/login/login?SAMLRequest=fVLJTsMwEL0j8Q%2BW79kqQGA1qUoRohJL1AYO3FxnkrpNxsHjtPD3pGnLcoCjn5%2FfMp7h6L2u2AYsaYMxj%2FyQM0Blco1lzJ%2BzW%2B%2BSj5LTkyHJumrEuHVLnMFbC%2BRY9xJJ9Bcxby0KI0mTQFkDCafEfPxwLwZ%2BKBprnFGm4mx6E3OZy2YtscG11qpeQYFqoXGdr81KQYmrCosiL%2FOas5djrMEu1pSohSmSk%2Bg6KAyvvDDywsssOhPhhTg%2Ff%2BUsPThda9w3%2BC%2FWYk8icZdlqZc%2BzbNeYKNzsI8dO%2BalMWUFvjL1zj6VRHrTwc62wNmYCKzr8k0MUluDnYPdaAXPs%2FuYL51rSATBdrv1v1UCGVCLVlN3WgZSEU%2F6wYq%2Bm%2F0x0f%2BTy6M1T77Fh8EPqeTwYbse05vUVFp9sHFVme3EgnRfJW6NraX72y3yox7RuVf0VNEiNaB0oSHnLEj2rr83o9uXTw%3D%3D&RelayState=https%3A%2F%2Fwww.google.com%2Fa%2Fsunrise.ch%2FServiceLogin%3Fcontinue%3Dhttp%253A%252F%252Fpartnerpage.google.com%252Fsunrise.ch%252Fdefault%252Fpostlogin%253Fpid%253Dsunrise.ch%2526url%253Dhttp%253A%252F%252Fpartnerpage.google.com%252Fsunrise.ch%26followup%3Dhttp%253A%252F%252Fpartnerpage.google.com%252Fsunrise.ch%252Fdefault%252Fpostlogin%253Fpid%253Dsunrise.ch%2526url%253Dhttp%253A%252F%252Fpartnerpage.google.com%252Fsunrise.ch%26service%3Dig%26passive%3Dtrue%26cd%3DUS%26hl%3Dde%26nui%3D1%26ltmpl%3Ddefault%26go%3Dtrue%26passive_sso%3Dtrue");
	    
	    Hashtable<String,String> parameters = new Hashtable<String,String>();

	    parameters.put("recipient", phoneNumber);
	    parameters.put("message", shortMessage);
	    parameters.put("type", "sms");
	    parameters.put("task", "send");
	    parameters.put("send", "send");
	    
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
	    
	    if (response.toString().indexOf("SMS wurde an "+phoneNumber+" gesendet.") <= 0) {
	    	throw new Exception("Message was not sent: "+response.toString());
	    }
	    
	    _phoneNumber = phoneNumber;
	    _shortMessage = shortMessage;
	}
		
	public String getShortMessage() {
	    return _shortMessage;
	}
	
	public String getPhoneNumber() {
	    return _phoneNumber;
	}
}