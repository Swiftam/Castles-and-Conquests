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
        Fixtures.delete(Quest.class);
        Fixtures.delete(Land.class);
        Fixtures.loadModels("data-level.yml");
        Fixtures.loadModels("data-quest.yml");
        Fixtures.loadModels("data-land.yml");
    }
 
}

