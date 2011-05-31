package models;

import play.*;
import play.db.jpa.*;
import play.mvc.Scope.Session;

import javax.persistence.*;
import java.util.*;

@Entity
public class Level extends Model {
	public int rank;
	public int xp;
	public String name;
}