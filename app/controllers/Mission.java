package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.Request;

import java.util.*;
import utils.*;

import models.*;

public class Mission extends Controller {
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
    	User user = User.locate();
    	if ( null == user ) {
    		index();
    		return;
    	}

    	Random randNum = new Random();
    	int goldGained = randNum.nextInt(quest.maxGold-quest.minGold) + quest.minGold;
    	user.exp += quest.xp;
    	user.gold += goldGained;

    	// Check if player has advanced to the next level
    	nextLevel = user.level.next();
    	if ( quest.xp > 0 && null != nextLevel && user.exp >= nextLevel.xp ) {
    		user.level = nextLevel;
    		advanceLevel = true;
    	}
    	user.save();
    	
    	render(quest, user, goldGained, advanceLevel);
    }
}
