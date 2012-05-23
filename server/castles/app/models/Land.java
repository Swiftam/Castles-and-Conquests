package models;

import play.db.jpa.GenericModel;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Land extends GenericModel {
    public static String DEFAULT = "swamp";

    @Id
    public String id;
    public String parent;
	public String name;
	public long price;
	public long income;
	public int level;
	public String description;
    public String image;
}
