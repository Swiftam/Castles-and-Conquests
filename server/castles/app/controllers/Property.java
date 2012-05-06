package controllers;

import play.*;
import play.cache.Cache;
import play.mvc.*;
import play.mvc.Http.Request;

import java.util.*;
import utils.*;

import models.*;

public class Property extends Controller {
	@Before
	static void validateUser() {
    	User user = User.locate();
    	
    	if ( null == user ) {
            error(401, "User not found");
        } else {
    		user.update();
        	renderArgs.put("user", user);
    	}
	}

    public static void index() {
    	Application.index();
    }

    public static void list() {
        User user = (User)renderArgs.get("user");

        List<Land> lands = Cache.get("lands", List.class);
        if ( null == lands ) {
            lands = Land.findAll();
            Cache.set("lands", lands, "1h");
        }
        renderJSON(lands);
    }
    
    public static void info(String landId) {
    	// Need a valid land ID
    	if ( null == landId ) {
    		index();
    		return;
    	}

    	Land property = Land.findById(landId);
    	render(property);
    }
    
    public static void purchase(String landId) {
    	// Need a valid land ID
    	if ( null == landId ) {
    		index();
    		return;
    	}
    	
    	User user = (User)renderArgs.get("user");

    	Land land = Land.findById(landId);
    	if ( null == land ) {
    		index();
    		return;
    	}

        if ( land.price > user.gold ) {
            error(403, "Not enough gold");
        }
    	
    	UserLand property = UserLand.find("user = ? and land = ?", user, land).first();
    	if ( null == property ) {
    		property = new UserLand(user, land);
    	} else {
    		property.quantity++;
    	}
    	property.save();

        user.gold -= land.price;
        user.calculateNetWorth();
        user.save();

        render(property);
    }
}
