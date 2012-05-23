import org.junit.Before;
import org.junit.Test;
import play.mvc.Http.Response;
import play.mvc.Scope.Session;
import play.test.Fixtures;
import play.test.FunctionalTest;

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
		assertIsOk(response);
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