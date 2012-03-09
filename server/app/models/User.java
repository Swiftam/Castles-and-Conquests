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
    
    @ManyToOne
    //@JoinColumn(name="level_id", referencedColumnName="id")
    public Level level;
    
    public User()
    {
    	this("");
    }
    
    public User(String name)
    {
    	super();
    	this.name = name;
    	this.gold = 0L;
    	this.exp = 0L;
    	this.level = null;
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
    
    public Level getLevel() {
    	if ( null == this.level ) {
        	this.level = Level.find("rank = ?", 1).first();
    	}
    	return level;
    }
    
    public Long getExpToLevel() {
    	Level nextLevel = level.next();
    	if ( null == nextLevel )
    	{
    		return 0L;
    	}
    	return nextLevel.xp;
    }
}
