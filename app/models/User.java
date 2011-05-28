package models;

import play.*;
import play.db.jpa.*;
import play.mvc.Scope.Session;

import javax.persistence.*;
import java.util.*;

@Entity
public class User extends Model {
    public String snid;
    public String name;
    public Long gold;
    public Long exp;
    private Level level;
    
    public User()
    {
    	super();
    	this.gold = 0L;
    	this.exp = 0L;
    	this.level = Level.find("order = ?", 1).first();
    }
    
    public static User locate() {
    	return locate(null);
    }
    
    public static User locate(Long userid) {
    	Session session = Session.current();
    	
    	if ( null == userid ) {
        	String sUserid = session.get("userid");
        	if ( null != sUserid ) {
        		return User.findById(Long.decode(sUserid));
        	}

	    	String snid = session.get("snid");
	    	if ( null != snid ) {
	    		return User.find("snid = ?", snid).first();
	    	}
	    	
	    	return null;
    	}
    	
    	return User.findById(userid);
    }
    
    public int getLevel() {
    	if ( null == this.level ) {
    		return 0;
    	}
    	return level.order;
    }
    
    public Long getExpToLevel() {
    	return 0L;
    }
}
