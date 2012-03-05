package models;

import play.*;
import play.db.jpa.*;
import play.mvc.Scope.Session;

import javax.persistence.*;
import java.util.*;

@Entity
public class Land extends Model {
	public String name;
	public long price;
	public long income;
	public int level;
	public String description;
}
