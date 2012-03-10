package models;

import play.*;
import play.db.jpa.*;
import play.mvc.Scope.Session;

import javax.persistence.*;
import java.util.*;

@Entity
public class Level extends Model {
	public int rank;
	public Long xp;
	public String name;
	private Level _nextLevel;
	private Boolean _nextLevelLoaded = false;
	
	public Level next()
	{
		if ( !_nextLevelLoaded )
		{
			_nextLevel = Level.find("rank = ?", (rank+1)).first();
			_nextLevelLoaded = (null != _nextLevel);
		}

		return _nextLevel;
	}
}