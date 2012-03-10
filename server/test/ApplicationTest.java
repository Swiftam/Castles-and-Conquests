import org.junit.*;
import org.junit.Before;

import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import play.mvc.Scope.Session;
import models.*;

public class ApplicationTest extends FunctionalTest {
	@Before
	public void setUp() {
		Session.current().clear();
		Fixtures.deleteAllModels();
		Fixtures.loadModels("data.yml");
	}
	
	@Test
    public void testIndexPageRedirectsWhenUnauthenticated() {
		Response response = GET("/");
		this.assertStatus(302, response);
    }
    
    @Test
    public void testIndexPageWorksWhenAuthenticated() {
		Session.current().put("snid", "asjisoe");
        Response response = GET("/");
        assertIsOk(response);
        assertContentType("text/html", response);
        assertCharset("utf-8", response);
    }
    
}