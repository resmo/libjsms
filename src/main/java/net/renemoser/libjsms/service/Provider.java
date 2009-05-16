/**
 * 
 */
package net.renemoser.libjsms.service;

import java.net.HttpURLConnection;
import java.util.Hashtable;

/**
 * @author René Moser <mail@renemoser.net>
 * @since 0.3
 *
 */
public abstract class Provider {
	protected Hashtable<String,String> _cookies = new Hashtable<String,String>(); 
	protected HttpURLConnection _conn;
	
	protected String _shortMessage = "";
	protected String _phoneNumber = "";
	protected int _availableMessages = -1;
	protected boolean _isLoggedIn = false;
	
	public Provider() throws Exception {
		System.getProperties().put("java.protocol.handler.pkgs","com.sun.net.ssl.internal.www.protocol");
		java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
	}
	
	public String getPhoneNumber() throws Exception {
		return _phoneNumber;
	}
	
	public String getShortMessage() throws Exception {
		return _shortMessage;
	}
	
	public int getAvailableMessages() throws Exception {
		if (_availableMessages < 0) {
			throw new Exception("Available messages are unknown.");
		}
		return _availableMessages;
	}
}
