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
    	// Need a valid quest ID
    	if ( null == questid ) {
    		index();
    		return;
    	}

    	Quest quest = Quest.findById(questid);
    	User user = User.locate();
    	
    	user.exp += quest.xp;
    	user.save();
    	
    	render(quest, user);
    }
}
