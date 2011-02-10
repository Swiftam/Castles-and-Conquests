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
    	}
    	
    	return User.findById(userid);
    }
}
