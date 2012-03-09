package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.Request;

import java.util.*;
import utils.*;

import models.*;

public class Property extends Controller {
    public static void index() {
    	Application.index();
    }
    
    public static void info(Long landid) {
    	// Need a valid land ID
    	if ( null == landid ) {
    		index();
    		return;
    	}

    	Land property = Land.findById(landid);
    	render(property);
    }
    
    public static void purchase(Long landid) {
    	// Need a valid land ID
    	if ( null == landid ) {
    		index();
    		return;
    	}
    	
    	User user = User.locate();
    	if ( null == user ) {
    		index();
    		return;
    	}

    	Land land = Land.findById(landid);
    	if ( null == land ) {
    		index();
    		return;
    	}
    	
    	UserLand property = UserLand.find("user = ? and land = ?", user, land).first();
    	if ( null == property ) {
    		property = new UserLand(user, land);
    	} else {
    		property.quantity++;
    	}
    	property.save();
    	
    	render(property);
    }
}
