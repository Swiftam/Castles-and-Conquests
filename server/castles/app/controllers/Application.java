package controllers;

import play.*;
import play.cache.Cache;
import play.cache.CacheFor;
import play.mvc.*;
import play.mvc.Http.Request;

import java.util.*;

import play.test.Fixtures;
import results.Leader;
import utils.*;

import models.*;

public class Application extends Controller {
    @Before
    static void addHeaders() {
        response.setHeader("p3p", "CP=\"IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT\")");
    }

	@Before(unless={"register","reset","postUser","facebook"})
	static void validateUser() {
    	User user = User.locate();
    	
    	if ( null != user ) {
    		user.update();
        	renderArgs.put("user", user);
    	}
	}

	public static void index() {
    	render();
    }
    
    public static void userInfo() {
        User user = (User)renderArgs.get("user");
        Level level = Level.findById(user.level);

        notFoundIfNull(user);
        notFoundIfNull(level);

        render(user, level);
    }
    
    public static void userLands() {
        User user = (User)renderArgs.get("user");
        List<UserLand> lands  = UserLand.find("user = ?", user).fetch();
        renderJSON(lands);
    }
    
    /**
     * Display lands available for purchase and
     * already owned
     */
    public static void land() {
    	User user = (User)renderArgs.get("user");

    	List<Land> lands = Cache.get("lands", List.class);
    	if ( null == lands ) {
    		lands = Land.findAll();
    		Cache.set("lands", lands, "1h");
    	}
    	
    	// Figure out how many of each land is owned by the user
    	List<UserLand> userLands = UserLand.find("user = ?", user).fetch();
    	HashMap<String,Integer> owned = new HashMap<String,Integer>();
    	for ( Land l : lands) {
    		for ( UserLand ul : userLands ) {
    			if ( ul.land.id == l.id ) {
    				owned.put(l.id, ul.quantity);
    				break;
    			}
    		}
    		
    		if ( null == owned.get(l.id)) {
    			owned.put(l.id, 0);
    		}
    	}
    	render(lands, owned);
    }
    
    /**
     * Display quests selection
     */
    public static void quests() {
        List<Quest> quests = Quest.findAll();
        render(quests);
    }

    /**
     * Display registration form
     */
    public static void register() {
    	render();
    }

    /**
     * Process registration form
     */
    public static void postUser(String name) {
    	String snid = session.get("snid");
    	User user = new User();
    	user.name = name;
        user.snid = snid;
    	user.save();
        Logger.info("Printing session at save time...");
        for ( String key : session.all().keySet()) {
            Logger.info("%s: %s", key, session.get(key));
        }
        renderJSON(user);
    }

    static final Comparator<User> NETWORTH_ORDER =
            new Comparator<User>() {
                @Override
                public int compare(User user, User user1) {
                    return user.netWorth.compareTo(user1.netWorth);
                }
            };

    @CacheFor("1h")
    public static void leaderboard() {
        List<User> users = User.findAll();
        Collections.sort(users, NETWORTH_ORDER);
        List<Leader> leaderBoard = new ArrayList<Leader>();
        int rank = 1;
        for ( User user : users ) {
            Leader leader = new Leader();
            leader.rank = rank++;
            leader.name = user.name;
            leader.netWorth = user.netWorth;
            leaderBoard.add(leader);
        }
        renderJSON(leaderBoard);
    }
    
    public static void facebook() {
    	String req = params.get("signed_request");
    	String appId = Play.configuration.getProperty("fb.appId");
    	String secret = Play.configuration.getProperty("fb.appSecret");
    	Facebook fb = new Facebook(appId, secret, req);
    	String userId;
    	
    	try {
        	userId = fb.getUserId();
    	} catch ( FacebookRequestException frex ) {
    		userId = null;
    	}

        Boolean top = true;
    	if ( null == userId ) {
    		//String currentPath = Request.current().getBase() + Request.current().url;
    		String appPath = Play.configuration.getProperty("fb.appPath");
    		String url = fb.getAuthorizeUrl(appPath);
    		render(url, top);
    	} else {
            top = false;
    		session.put("snid", "fb_" + userId);
            String url = "/?snid=" + "fb_" + userId;
            render(url, top);
    	}
    }
}
