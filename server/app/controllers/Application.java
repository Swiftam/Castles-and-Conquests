package controllers;

import play.*;
import play.cache.Cache;
import play.mvc.*;
import play.mvc.Http.Request;

import java.util.*;
import utils.*;

import models.*;

public class Application extends Controller {
	@Before(unless={"register","reset","postUser","facebook"})
	static void validateUser() {
    	User user = User.locate();
    	
    	if ( null == user ) {
    		register();
    	} else {
        	renderArgs.put("user", user);
    	}
	}

	public static void index() {
    	render();
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
    	render();
    }
    
    /**
     * Display lands available for purchase and
     * already owned
     */
    public static void land() {
    	User user = (User)renderArgs.get("user");

    	List<Land> lands = Cache.get("lands", List.class);
    	if ( null == lands ) {
    		lands = Land.findAll();
    		Cache.set("lands", lands, "1h");
    	}
    	
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
    		
    		if ( null == owned.get(l.id)) {
    			owned.put(l.id, 0);
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
