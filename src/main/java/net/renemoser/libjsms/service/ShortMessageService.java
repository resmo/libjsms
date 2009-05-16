package net.renemoser.libjsms.service;

/**
 * Interface for ShortMessageService
 * 
 * @author René Moser <mail@renemoser.net>
 * @version 0.1
 */

public interface ShortMessageService {
	
	/**
	 * @param userid
	 * @param password
	 * @throws Exception
	 */
	public void doLogin(String userid, String password) throws Exception;
	
	/**
	 * @param phoneNumber
	 * @param shortMessage
	 * @throws Exception
	 */
	public void sendShortMessage(String phoneNumber, String shortMessage) throws Exception;
	
	/**
	 * @return
	 * @throws Exception
	 */
	public String getShortMessage() throws Exception;
	
	/**
	 * @return
	 * @throws Exception
	 */
	public String getPhoneNumber() throws Exception;
	
	/**
	 * @return
	 * @throws Exception
	 */
	public int getAvailableMessages() throws Exception;
}
