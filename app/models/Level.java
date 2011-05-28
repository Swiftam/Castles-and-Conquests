package models;

import play.*;
import play.db.jpa.*;
import play.mvc.Scope.Session;

import javax.persistence.*;
import java.util.*;

@Entity
public class Level extends Model {
	int order;
	int xp;
	String name;
}