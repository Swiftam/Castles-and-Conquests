import java.util.ArrayList;
import java.util.List;

import play.*;
import play.jobs.*;
import play.test.*;
 
import models.*;
 
@OnApplicationStart
public class Bootstrap extends Job {
 
	public void doJob() {
        Fixtures.delete(Level.class);
        if ( Level.count() == 0) {
            Fixtures.loadModels("data-level.yml");
        }

        // Check if the database is empty
        if(Quest.count() == 0) {
        	Fixtures.loadModels("data-quest.yml");
        }
    }
 
}

