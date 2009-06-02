package net.renemoser.libjsms;

import net.renemoser.libjsms.service.*;

public class ServiceFactory {
	public static ShortMessageService getService(String serviceName) throws Exception {
		
		if (serviceName.equals("SunriseCH")) {
	    	return new SunriseCH();
	    }
	    
	    if (serviceName.equals("OrangeCH")) {
	      return new OrangeCH();
		}
	    
	    throw new RuntimeException("Unknown Service: " + serviceName);
	}	
}