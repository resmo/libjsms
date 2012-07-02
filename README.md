Java library sending "short messages" over HTTP
===============================================

[![Build Status](https://secure.travis-ci.org/resmo/libjsms.png?branch=master)](http://travis-ci.org/resmo/libjsms)


(c) René Moser, <mail@renemoser.net>, 2009 
This application is licenced under GNU Lesser General Public License, Version 3.0
http://www.gnu.org/licenses/lgpl-3.0-standalone.html


About:
------

Java library sending "short messages" (alias SMS) using free web service of your 
mobile operator.

Currently the following operators are supported:

* Sunrise CH
* Orange CH

Any operator of the world which allows sending SMS over HTTP, web forms, etc., can 
be added. Just send your code (patches) or mail me your credentials, so I am able 
to add support for those operators.


Background:
-----------

There are is swisssms for OS X [1] in C# and Java Swing GUI SwissSMSCient [2], but I just 
wanted to have a small library in Java, which can be used to send SMS' by scripting or 
building a GUI based on it, whatever you like.

I used Apache's Maven. The Project site is http://www.renemoser.net/projects/ and 
http://github.com/resmo/libjsms.

[1] http://code.google.com/p/swisssms/
[2] http://code.google.com/p/swisssmsclient/ 


Usage as external lib factory method:
-------------------------------------

    import net.renemoser.libjsms.service.*;

    String provider = "SunriseCH";
    try {
        ShortMessageService Service = ServiceFactory.getService(provider);
        Service.doLogin(userid, password);
        Service.sendShortMessage(phoneNumber, message);
    } catch(Exception e) {
        e.printStackTrace();
    }

or
    
    String provider = "SunriseCH";
    try {
        ShortMessageService Service = ServiceFactory.getService(provider);
        Service.setUserId(userid);
        Service.setPassword(password);
        Service.setPhoneNumber(phoneNumber);
        Service.setShortMessage(message);
        Service.doLogin();
        Service.sendShortMessage();
    } catch(Exception e) {
        e.printStackTrace();
    }


Usage as external lib:
----------------------

    import net.renemoser.libjsms.service.*;

    try {
        ShortMessageService Service  = new SunriseCH();
        Service.doLogin(userid, password);
        Service.sendShortMessage(phoneNumber, message);
        System.out.println("Message '" + 
        Service.getShortMessage() +
        "' was sent to " + 
        Service.getPhoneNumber());
    } catch(Exception e) {
        e.printStackTrace();
    }


Usage as standalone:
--------------------

    java -cp libjsms-x.y.jar net.renemoser.libjsms.App <userid> <password> <phoneNumber> <message> <provider:default=SunriseCH> 
