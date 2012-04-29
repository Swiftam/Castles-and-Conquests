package models;

import play.db.jpa.GenericModel;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Definition for army units in the game
 * User: mwilson
 * Date: 12-04-07
 * Time: 9:04 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Unit extends GenericModel {
    @Id
    public String id;
    public String name;
    public Integer defense;
    public Integer offense;
    public Integer price;
    public Integer upkeep;
    public String image;
}
