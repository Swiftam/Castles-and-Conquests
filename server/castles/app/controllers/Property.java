package controllers;

import com.google.gson.Gson;
import models.Land;
import models.User;
import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Controller;

import java.util.List;

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
    
    public static void purchase(String landId, Integer indexNum) {
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

        String[] lands = new Gson().fromJson(user.lands, String[].class);
        String parentLand = lands[indexNum];
        if ( !land.parent.equals(parentLand)) {
            error(403, "Parent land is not a match");
        }

        lands[indexNum] = land.id;
        user.lands = new Gson().toJson(lands);
        user.gold -= land.price;
        user.calculateNetWorth();
        user.save();

        render(land);
    }
}
