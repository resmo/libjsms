package net.renemoser.libjsms;

import net.renemoser.libjsms.service.*;

/**
 * @author René Moser <mail@renemoser.net>
 */
public class App {
	public static void main(String[] args) {
		if (args.length < 4) {
			System.err.println("Use: java -cp LibJSMS-x.y.jar net.renemoser.libjsms.App <userid> <password> <phoneNumber> <message> <provider:default=sunrise>");
			System.exit(0);
		}
		
		String userid = args[0] + "";
		String password = args[1] + "";
		String phoneNumber = args[2] + "";
		String message = args[3] + "";
		String provider = args[4].toLowerCase() + "";
						
	    try { 
	    	ShortMessageService Service;
	    	if (provider.equals("orange")) {
	    		Service = new Orange();
	    	} else {
	    		Service = new Sunrise();
	    	}
	    	
	    	Service.doLogin(userid, password);
	    	Service.sendShortMessage(phoneNumber, message);
    	    if (!Service.getShortMessage().isEmpty()) {
    	    	System.out.println("Message '" + 
    	    			Service.getShortMessage() + 
    	    			"' was sent to " + 
    	    			Service.getPhoneNumber());
    	    }
    	} catch(Exception e) {
    	    e.printStackTrace();
    	}
	}
}
