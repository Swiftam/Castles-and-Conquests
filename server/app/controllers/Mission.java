package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.Request;

import java.util.*;
import utils.*;

import models.*;

public class Mission extends Controller {
	@Before
	static void validateUser() {
    	User user = User.locate();
    	
    	if ( null == user ) {
    		Application.register();
    	} else {
        	renderArgs.put("user", user);
    	}
	}

	public static void index() {
    	Application.index();
    }
    
    public static void info(Long questid) {
    	// Need a valid quest ID
    	if ( null == questid ) {
    		index();
    		return;
    	}

    	Quest quest = Quest.findById(questid);
    	render(quest);
    }

    
    public static void run(Long questid) {
    	Boolean advanceLevel = false;
    	Level nextLevel = null;
    	
    	// Need a valid quest ID
    	if ( null == questid ) {
    		index();
    		return;
    	}

    	Quest quest = Quest.findById(questid);
    	User user = (User)renderArgs.get("user");

    	Random randNum = new Random();
    	int goldGained = randNum.nextInt(quest.maxGold-quest.minGold) + quest.minGold;
    	user.exp += quest.xp;
    	user.gold += goldGained;
    	user.health -= quest.power;

    	// Check if player has advanced to the next level
    	nextLevel = user.level.next();
    	if ( quest.xp > 0 && null != nextLevel && user.exp >= nextLevel.xp ) {
    		user.level = nextLevel;
    		advanceLevel = true;
    	}
    	
    	if ( advanceLevel ) {
    		user.gainLevel();
    	}
    	user.save();
    	
    	render(quest, user, goldGained, advanceLevel);
    }
}
