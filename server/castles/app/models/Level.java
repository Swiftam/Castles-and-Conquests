package models;

import play.*;
import play.db.jpa.*;
import play.mvc.Scope.Session;

import javax.persistence.*;
import java.util.*;

@Entity
public class Level extends GenericModel {
    @Id
    public Integer id;
	public Long xp;
	public String name;
}