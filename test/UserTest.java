import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class UserTest extends UnitTest {
	@Test
	public void createAndRetrieveUser() {
		// Create a new user and save it
		new User("Morlinn").save();
		
		User morlinn = User.find("byName", "Morlinn").first();
		
		// Test
		assertNotNull(morlinn);
		assertEquals("Morlinn", morlinn.name);
	}
}
