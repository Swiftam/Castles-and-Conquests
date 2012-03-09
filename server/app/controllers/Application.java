package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.Request;

import java.util.*;
import utils.*;

import models.*;

public class Application extends Controller {
    public static void index() {
    	User user = User.locate();
    	
    	if ( null == user ) {
    		register();
    	} else {
        	render(user);
    	}
    }
    
    public static void reset(Boolean confirm) {
    	if ( null == confirm ) {
        	render();
        	return;
    	}
    	
    	User user = User.locate();
    	if ( null != user ) {
    		user.delete();
    		session.remove("userid");
    	}
    	index();
    }
    
    public static void settings() {
    	User user = User.locate();
    	if ( null == user ) {
    		register();
    	}
    	render(user);
    }
    
    /**
     * Display lands available for purchase and
     * already owned
     */
    public static void land() {
    	User user = User.locate();
    	if ( null == user ) {
    		register();
    	}

    	List<Land> lands = Land.findAll();
    	
    	// Figure out how many of each land is owned by the user
    	List<UserLand> userLands = UserLand.find("user = ?", user).fetch();
    	HashMap<Long,Integer> owned = new HashMap<Long,Integer>(); 
    	for ( Land l : lands) {
    		for ( UserLand ul : userLands ) {
    			if ( ul.land.id == l.id ) {
    				owned.put(l.id, ul.quantity);
    				break;
    			}
    		}
    	}
    	render(lands, owned);
    }
    
    /**
     * Display quests selection
     */
    public static void quests() {
        List<Quest> quests = Quest.findAll();
        render(quests);
    }

    /**
     * Display registration form
     */
    public static void register() {
    	render();
    }

    /**
     * Process registration form
     */
    public static void postUser(String name) {
    	String snid = session.get("snid");
    	User user = new User();
    	user.name = name;
    	user.snid = snid;
    	user.save();
    	session.put("userid", user.id);
    	index();
    }
    
    public static void facebook() {
    	String req = params.get("signed_request");
    	String appId = Play.configuration.getProperty("fb.appId");
    	String secret = Play.configuration.getProperty("fb.appSecret");
    	Facebook fb = new Facebook(appId, secret, req);
    	String userId;
    	
    	try {
        	userId = fb.getUserId();
    	} catch ( FacebookRequestException frex ) {
    		userId = null;
    	}
    	
    	if ( null == userId ) {
    		//String currentPath = Request.current().getBase() + Request.current().url;
    		String appPath = Play.configuration.getProperty("fb.appPath");
    		String url = fb.getAuthorizeUrl(appPath);
    		render(url);    
    	} else {
    		session.put("snid", "fb_" + userId);
            index();
    	}
    }
}
