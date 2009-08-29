package net.renemoser.libjsms;

import net.renemoser.libjsms.service.ShortMessageService;

/**
 * @author René Moser <mail@renemoser.net>
 */
public class App {
    public static void main(String[] args) {
	if (args.length < 4) {
	    System.err
		    .println("Use: java -cp LibJSMS-x.y.jar net.renemoser.libjsms.App <userid> <password> <phoneNumber> <message> <provider:default=sunrise>");
	    System.exit(0);
	}

	String userid = args[0];
	String password = args[1];
	String phoneNumber = args[2];
	String message = args[3];

	String provider = "SunriseCH";
	if (args.length >= 5) {
	    provider = args[4].toLowerCase();
	}

	try {
	    ShortMessageService Service = ServiceFactory.getService(provider);

	    // Login
	    System.out.println("Logging in...");
	    Service.doLogin(userid, password);

	    // Available messages
	    System.out.println("Getting free messages left...");
	    int messagesLeft = Service.getAvailableMessages();
	    String messagesLeftString = Integer.toString(messagesLeft);
	    System.out.println("You have " + messagesLeftString
		    + " messages left");

	    if (messagesLeft > 0) {
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
