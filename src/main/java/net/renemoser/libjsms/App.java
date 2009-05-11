package net.renemoser.libjsms;

import net.renemoser.libjsms.service.*;

/**
 * @author René Moser <mail@renemoser.net>
 */
public class App {
	public static void main(String[] args) {
		if (args.length < 4) {
			System.err.println("Use: java -cp net.renemoser.libjsms.App <userid> <password> <phoneNumber> <message>");
			System.exit(0);
		}
		
		String userid = args[0]+"";
		String password = args[1]+"";
		String phoneNumber = args[2]+"";
		String message = args[3]+"";
				
	    try {    	
	    	ShortMessageService Sunrise = new Sunrise();
    	    Sunrise.doLogin(userid, password);
    	    Sunrise.sendShortMessage(phoneNumber, message);
    	    if (!Sunrise.getShortMessage().isEmpty()) {
    	    	System.out.println("Message '" + Sunrise.getShortMessage() + "' was sent to " + Sunrise.getPhoneNumber());
    	    }
    	} catch(Exception e) {
    	    e.printStackTrace();
    	}
	}
}
