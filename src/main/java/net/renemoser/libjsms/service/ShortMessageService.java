package net.renemoser.libjsms.service;

/**
 * Interface for ShortMessageService
 * 
 * @author René Moser <mail@renemoser.net>
 * @version 0.2
 */

public interface ShortMessageService {

    /**
     * Sets the user id for login procedure
     * 
     * @param userId
     * @throws Exception
     */
    void setUserId(String userId) throws Exception;

    /**
     * Returns the user id for login procedure
     * 
     * @return userId
     * @throws Exception
     */
    String getUserId() throws Exception;

    /**
     * Sets the user password for login procedure
     * 
     * @param password
     * @throws Exception
     */
    void setPassword(String password) throws Exception;

    /**
     * Returns the user password of login procedure
     * 
     * @return
     * @throws Exception
     */
    String getPassword() throws Exception;

    /**
     * Login with given user ID and password into operators web interface
     * 
     * @param userid
     * @param password
     * @throws Exception
     */
    void doLogin(String userId, String password) throws Exception;

    /**
     * Login into operators web interface
     * 
     * @throws Exception
     */
    void doLogin() throws Exception;

    /**
     * Returns the amount of left free available messages which can be sent
     * 
     * @return availabeMessages
     * @throws Exception
     */
    public int getAvailableMessages() throws Exception;

    /**
     * Sets the phone number to which the short message will be sent
     * 
     * @param phoneNumber
     * @throws Exception
     */
    void setPhoneNumber(String phoneNumber) throws Exception;

    /**
     * 
     * Returns the phone number to which the short message will be sent
     * 
     * @return phone number
     * @throws Exception
     */
    String getPhoneNumber() throws Exception;

    /**
     * Sets the short message
     * 
     * @param shortmessage
     * @throws Exception
     */
    void setShortMessage(String shortmessage) throws Exception;

    /**
     * Returns the short message
     * 
     * @return
     * @throws Exception
     */
    String getShortMessage() throws Exception;

    /**
     * Sends the given short message to the given phone number
     * 
     * @param phoneNumber
     * @param shortMessage
     * @throws Exception
     */
    void sendShortMessage(String phoneNumber, String shortMessage)
	    throws Exception;

    /**
     * Sends the short message
     * 
     * @throws Exception
     */
    void sendShortMessage() throws Exception;
}
