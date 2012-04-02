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
    public Long health;
    public Long healthMax;
    public Calendar created;
    public Calendar lastUpdate;
    
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
    	this.health = 20L;
    	this.healthMax = 20L;
    	this.level = null;
    	this.lastUpdate = Calendar.getInstance();
    }
    
    public static long getUpdateInterval()
    {
    	long updateInterval = new Long(Play.configuration.getProperty("game.timespan.health"));
    	return updateInterval;
    }
    
    private long secondsSinceLastUpdate()
    {
    	long diffInSeconds = (Calendar.getInstance().getTimeInMillis() - lastUpdate.getTimeInMillis())/1000;
    	return diffInSeconds;
    }
    
    public void update() {
    	long diffInSeconds = secondsSinceLastUpdate();
    	long updateInterval = User.getUpdateInterval();
    	long updateSpans = diffInSeconds / (updateInterval*60);
    	// Gain 1 health every 5 minutes
    	if ( updateSpans > 0 && health < healthMax ) {
	    	health = Math.max(0, Math.min(health+updateSpans, healthMax));
	    	lastUpdate.add(Calendar.MINUTE, (int)(updateInterval * updateSpans));
	    	save();
    	} else if ( health >= healthMax ) {
    		// Reset lastUpdate, any change in account status will
    		// cause this to start ticking. Otherwise any damage taken
    		// will get healed as of the last time this value was
    		// saved.
    		lastUpdate = Calendar.getInstance();
    	}
    }
    
    /**
     * Returns the next update in seconds
     * 
     * @return
     */
    public long nextUpdate() {
    	if ( health >= healthMax ) {
    		return -1;
    	}
    	
    	long diffInSeconds = secondsSinceLastUpdate();
    	long updateInterval = User.getUpdateInterval();
    	return updateInterval*60-diffInSeconds;
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

    /**
     * Increase health and other statistics as a result of
     * a user gaining a level.
     */
    public void gainLevel() {
    	// Add more health and give the user maximum health
    	healthMax += Math.max(1, Math.abs(new Random().nextLong() % 5));
    	health = healthMax;
    }
}