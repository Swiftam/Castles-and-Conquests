package models;

import play.*;
import play.db.jpa.*;
import play.mvc.Scope.Session;

import javax.persistence.*;
import java.util.*;

@Entity
public class Quest extends Model {
    public String name;
    public String description;
    public int xp;
    public int minGold;
    public int maxGold;
}
