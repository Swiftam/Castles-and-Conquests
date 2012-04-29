package controllers;

import models.Unit;
import play.mvc.Controller;

import java.util.List;

/**
 * Controlls the provisioning and listing of army units.
 *
 * User: mwilson
 * Date: 12-04-07
 * Time: 9:07 AM
 */
public class Army extends Controller {
    /**
     * List all units available for purchase
     */
    public static void list() {
        List<Unit> units = Unit.all().fetch();
        renderJSON(units);
    }
}
