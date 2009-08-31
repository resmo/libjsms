package net.renemoser.libjsms;

import net.renemoser.libjsms.exception.AvailableMessagesUnknownException;
import net.renemoser.libjsms.service.ShortMessageService;

/**
 * @author René Moser <mail@renemoser.net>
 */
public class App {
    public static void main(String[] args) {
	if (args.length < 4) {
	    System.err
		    .println("Use: java -cp libjsms-x.y.jar net.renemoser.libjsms.App <userid> <password> <phoneNumber> <message> <provider:default=SunriseCH>");
	    System.exit(0);
	}

	String userid = args[0];
	String password = args[1];
	String phoneNumber = args[2];
	String message = args[3];

	String operator = "SunriseCH";
	if (args.length >= 5) {
	    operator = args[4];
	}

	try {
	    ShortMessageService Service = ServiceFactory.getService(operator);

	    // Login
	    System.out.println("Logging in...");
	    Service.doLogin(userid, password);

	    // Available messages
	    System.out.println("Getting free messages left...");
	    int messagesLeft = -1;
	    try {
		messagesLeft = Service.getAvailableMessages();
		String messagesLeftString = Integer.toString(messagesLeft);
		System.out.println("You have " + messagesLeftString
			+ " messages left");

	    } catch (AvailableMessagesUnknownException ame) {
		System.out.println(ame.getMessage());
	    }

	    if (messagesLeft > 0 || messagesLeft < 0) {
		// Send SMS
		Service.sendShortMessage(phoneNumber, message);
		if (!Service.getShortMessage().equals("")) {
		    System.out.println("Message '" + Service.getShortMessage()
			    + "' was sent to " + Service.getPhoneNumber());
		}
	    } else {
		System.out.println("Gave it up, to few messages.");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
