package controllers;

import models.Land;
import models.Level;
import models.Quest;
import models.User;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.libs.WS;
import play.mvc.Before;
import play.mvc.Controller;
import results.Leader;
import utils.Facebook;
import utils.FacebookRequestException;

import java.util.*;

public class Application extends Controller {
    @Before
    static void addHeaders() {
        response.setHeader("p3p", "CP=\"IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT\")");
    }

	@Before(unless={"register","postUser","facebook"})
	static void validateUser() {
    	User user = User.locate();
    	
    	if ( null != user ) {
    		user.update();
        	renderArgs.put("user", user);
    	}
	}

	public static void index() {
        User user = null;
        String snid = null;
        String sessionId = params.get("sessionId");
        if ( null != sessionId ) {
            snid = play.cache.Cache.get(sessionId).toString();
            if ( null != snid ) {
                user = User.find("snid = ?", snid).first();
            }
        }
    	render(sessionId, user);
    }
    
    public static void userInfo() {
        User user = (User)renderArgs.get("user");
        if ( null == user ) {
            error(401, "Invalid user");
            return;
        }
        Level level = Level.findById(user.level);

        notFoundIfNull(user);
        notFoundIfNull(level);

        render(user, level);
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
    	
    	render(lands);
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
    public static void postUser(String name, String sessionId) {
        String snid = null;
        if ( null == name || name.trim().isEmpty() ) {
            error(400, "You need to supply a name");
        }
        if ( null != sessionId && !sessionId.equals("null") ) {
            snid = play.cache.Cache.get(sessionId).toString();
        }

        if ( null == snid || snid.trim().isEmpty() || snid.equals("null") ) {
            snid = "direct_" + UUID.randomUUID().toString();
            sessionId = UUID.randomUUID().toString();
            play.cache.Cache.set(sessionId, snid);
        }

        User user = new User();
    	user.name = name;
        user.snid = snid;
    	user.save();
        Logger.info("Printing session at save time...");
        for ( String key : session.all().keySet()) {
            Logger.info("%s: %s", key, session.get(key));
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("snid", sessionId);
        renderJSON(map);
    }

    static final Comparator<User> NETWORTH_ORDER =
            new Comparator<User>() {
                @Override
                public int compare(User user, User user1) {
                    return user.netWorth.compareTo(user1.netWorth);
                }
            };

    //@CacheFor("1h")
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
            Logger.error(frex.getMessage());
    		userId = null;
    	}

        Boolean top = true;
    	if ( null == userId ) {
    		String appPath = Play.configuration.getProperty("fb.appPath");
    		String url = fb.getAuthorizeUrl(appPath);
    		render(url, top);
    	} else {
            Logger.info("Logging application add to Kontagent");
            String kApi = Play.configuration.getProperty("kontagent.apikey");
            String kServer = Play.configuration.getProperty("kontagent.server");
            String kAprUrl = String.format("%s/api/v1/%s/apa/", kServer, kApi);
            WS.WSRequest wsRequest = WS.url(kAprUrl);
            wsRequest.parameters.put("s", userId);
            wsRequest.getAsync();

            top = false;
            String sessionId = UUID.randomUUID().toString();
            Cache.set(sessionId, "fb_" + userId);
            String url = "/?sessionId=" + sessionId;
            render(url, top);
    	}
    }

    public static void facebookUninstall()
    {
        Logger.info("Facebook deauthorize request");
        String req = params.get("signed_request");
        String appId = Play.configuration.getProperty("fb.appId");
        String secret = Play.configuration.getProperty("fb.appSecret");
        Facebook fb = new Facebook(appId, secret, req);
        String userId;

        try {
            userId = fb.getUserId();
        } catch ( FacebookRequestException frex ) {
            Logger.error(frex.getMessage());
            userId = null;
        }

        if ( null != userId ) {
            Logger.info("De-authorization accepted");
            String kApi = Play.configuration.getProperty("kontagent.apikey");
            String kServer = Play.configuration.getProperty("kontagent.server");
            String kAprUrl = String.format("%s/api/v1/%s/apr/", kServer, kApi);
            WS.WSRequest wsRequest = WS.url(kAprUrl);
            wsRequest.parameters.put("s", userId);
            wsRequest.getAsync();

            ok();
        }
        error(400, "Could not understand");
    }
}
