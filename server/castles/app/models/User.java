package models;

import com.google.gson.Gson;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import play.Play;
import play.db.jpa.Model;
import play.mvc.Http;
import play.mvc.Scope;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Random;
import java.util.UUID;

@Entity
public class User extends Model {
    @Column(unique = true)
    public String snid;
    public String name;
    public Long gold;
    public Long exp;
    public Long health;
    public Long healthMax;
    @Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
    public DateTime created;
    @Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
    public DateTime lastIncome;
    @Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
    public DateTime lastUpdate;
    public Integer level;
    public Long netWorth;

    public String lands;
    
    public User()
    {
    	this("");
    }
    
    public User(String name)
    {
    	super();
        this.snid = UUID.randomUUID().toString();
    	this.name = name;
    	this.gold = 1500L;
    	this.exp = 0L;
    	this.health = 20L;
    	this.healthMax = 20L;
    	this.level = 1;
    	this.lastUpdate = DateTime.now();
        this.lastIncome = DateTime.now();
        this.created = DateTime.now();
        this.netWorth = 34L;
        this.lands = new Gson().toJson(new String[] {
                Land.DEFAULT, Land.DEFAULT, Land.DEFAULT,
                Land.DEFAULT, Land.DEFAULT, Land.DEFAULT,
                Land.DEFAULT, Land.DEFAULT, Land.DEFAULT
        });
    }

    public Long calculateNetWorth() {
        Long income = getIncome();
        this.netWorth = income * level + this.gold;
        return this.netWorth;
    }

    private Long getIncome() {
        Long income = 0L;
        return income;
    }

    public static long getUpdateInterval(String updateSetting)
    {
    	long updateInterval = new Long(Play.configuration.getProperty(updateSetting));
    	return updateInterval;
    }
    
    private long secondsSinceLastUpdate()
    {
    	long diffInSeconds = (DateTime.now().getMillis() - lastUpdate.getMillis())/1000;
    	return diffInSeconds;
    }

    private long secondsSinceLastIncome()
    {
        long diffInSeconds = (DateTime.now().getMillis() - lastIncome.getMillis())/1000;
        return diffInSeconds;
    }
    
    public void update() {
        updateHealth();
        updateIncome();
    }

    private void updateHealth() {
        long diffInSeconds = secondsSinceLastUpdate();
        long updateInterval = User.getUpdateInterval("game.timespan.health");
        long updateSpans = diffInSeconds / (updateInterval*60);
        // Gain 1 health every 5 minutes
        if ( updateSpans > 0 && health < healthMax ) {
            health = Math.max(0, Math.min(health+updateSpans, healthMax));
            lastUpdate = new DateTime(lastUpdate).plusMinutes((int)(updateInterval * updateSpans));
            save();
        } else if ( health >= healthMax ) {
            // Reset lastUpdate, any change in account status will
            // cause this to start ticking. Otherwise any damage taken
            // will get healed as of the last time this value was
            // saved.
            lastUpdate = DateTime.now();
        }
    }

    private void updateIncome() {
        long diffInSeconds = secondsSinceLastIncome();
        long updateInterval = User.getUpdateInterval("game.timespan.income");
        long updateSpans = diffInSeconds / (updateInterval*60);
        // Gain 1 health every 5 minutes
        if ( updateSpans > 0 && health < healthMax ) {
            Long income = getIncome();
            gold += updateSpans * income;
            lastIncome = new DateTime(lastIncome).plusMinutes((int) (updateInterval * updateSpans));
            save();
        }
    }

    /**
     * Returns the next update in seconds
     *
     * @return Time in seconds until then next update
     */
    @SuppressWarnings("UnusedDeclaration")
    public long nextUpdate() {
        if ( health >= healthMax ) {
            return -1;
        }

        long diffInSeconds = secondsSinceLastUpdate();
        long updateInterval = User.getUpdateInterval("game.timespan.health");
        return updateInterval*60-diffInSeconds;
    }

    /**
     * Returns the next income gain in seconds
     *
     * @return Time in seconds until the next income gain
     */
    @SuppressWarnings("UnusedDeclaration")
    public long nextIncome() {
        long diffInSeconds = secondsSinceLastIncome();
        long updateInterval = User.getUpdateInterval("game.timespan.income");
        return updateInterval*60-diffInSeconds;
    }

    public static User locate() {
        Scope.Params params = Http.Request.current().params;
        String sessionId = params.get("sessionId");
        if ( null != sessionId ) {
            Object snid = play.cache.Cache.get(sessionId);
            if ( null != snid ) {
                return User.find("snid = ?", snid.toString()).first();
            }
        }

        return null;
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
