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
    		renderTemplate("Application/fbRedirect.html", url);    
    	} else {
            render();
    	}
    }
    
    public static void facebook() {
    	render();
    }

    public static void facebookLogin() {
    	render();
    }
}