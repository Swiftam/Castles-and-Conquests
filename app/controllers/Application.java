package controllers;

import play.*;
import play.modules.*;
import play.mvc.*;
import play.mvc.Http.Request;

import java.util.*;
import utils.*;

import models.*;

public class Application extends Controller {
    public static void index() {
    	User user = null;
    	String snid = session.get("snid");
    	if ( null != snid ) {
    		user = User.find("snid = ?", snid).first();
    	}
    	render();
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