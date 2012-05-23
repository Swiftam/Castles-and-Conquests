package controllers;

import models.Level;
import models.Quest;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;

import java.util.List;
import java.util.Random;

public class Mission extends Controller {
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
        List<Quest> quests = Quest.all().fetch();
        
        renderJSON(quests);
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
    
    public static void dead() {
    	User user = (User)renderArgs.get("user");
    	if ( user.health > 0 ) {
    		index();
    		return;
    	}
    	render();
    }

    
    public static void run(Long questid) {
    	Boolean advanceLevel = false;
    	Level nextLevel = null;
    	
    	// Need a valid quest ID
    	if ( null == questid ) {
    		index();
    		return;
    	}

    	User user = (User)renderArgs.get("user");
    	if ( user.health <= 0 ) {
            if ( request.format.equals("json")) {
                error(600, "Dead");
            }
    		dead();
    		return;
    	}

    	Quest quest = Quest.findById(questid);

    	Random randNum = new Random();
    	int goldGained = randNum.nextInt(quest.maxGold-quest.minGold) + quest.minGold;
    	user.exp += quest.xp;
    	user.gold += goldGained;
    	user.health -= quest.power;

    	// Check if player has advanced to the next level
    	Level level = Level.findById(user.level);
    	if ( quest.xp > 0 && null != level && user.exp >= level.xp ) {
    		user.level++;
            user.exp -= level.xp;
    		advanceLevel = true;
    	}
    	
    	if ( advanceLevel ) {
    		user.gainLevel();
    	}
    	user.save();

    	render(quest, user, goldGained, advanceLevel, level);
    }
}
