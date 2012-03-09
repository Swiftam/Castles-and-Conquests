package models;

import play.*;
import play.db.jpa.*;
import play.mvc.Scope.Session;

import javax.persistence.*;
import java.util.*;

@Entity
public class UserLand extends Model {
	@ManyToOne
	public User user = null;
	
	@ManyToOne
	public Land land = null;
	
	public int quantity = 0;
	
	public UserLand() {
		this(null, null);
	}
	
	public UserLand(User user, Land land) {
		this(user, land, 1);
	}
	
	public UserLand(User user, Land land, int quantity) {
		this.user = user;
		this.land = land;
		this.quantity = quantity;
	}
}
