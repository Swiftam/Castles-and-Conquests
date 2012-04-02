import org.junit.*;
import java.util.*;

import play.mvc.Scope.Session;
import play.test.*;
import models.*;

public class UserTest extends UnitTest {
	@Before
	public void setUp() {
		Session.current().clear();
		Fixtures.deleteAllModels();
		Fixtures.loadModels("data.yml");
	}

	@Test
	public void createAndRetrieveUser() {
		// Create a new user and save it
		new User("Morlinn").save();
		
		User morlinn = User.find("byName", "Morlinn").first();
		
		// Test
		assertNotNull(morlinn);
		assertEquals("Morlinn", morlinn.name);
	}
	
	@Test
	public void locateInvalidUser() {
		long userId = 230984;
		User user = User.locate(userId);
		
		// Test
		assertNull(user);
	}
	
	@Test
	public void userLevel() {
		// Create a new user and save it
		new User("Morlinn").save();
		
		User morlinn = User.find("byName", "Morlinn").first();
		
		// Test
		assertNotNull(morlinn);
		assertEquals("Morlinn", morlinn.name);

		Level level = morlinn.getLevel();
		assertNotNull(level);
	}
	
	@Test
	public void userMaxLevel() {
		User user = User.find("snid = ?", "oijrgeam").first();
		assertNotNull(user);
		assertEquals(user.name, "Tero");
		
		Long expected = 0L;
		Long result = user.getExpToLevel();
		assertEquals(expected, result);
	}
	
	@Test
	public void locateUserEmptyUserAndSnid() {
		User user = User.locate();
		
		// Test
		assertNull(user);
	}
	
	@Test
	public void locateUserValidSnid() {
		Session.current().put("snid", "asjisoe");
		User user = User.locate();
		
		// Test
		assertNotNull(user);
		
		String expected = "Maximus";
		String result = user.name;
		
		assertEquals(expected, result);
	}
	
	@Test
	public void locateUserValidSessionUserId() {
		// Create a user
		// Create a new user and save it
		new User("Morlinn").save();
		
		User morlinn = User.find("byName", "Morlinn").first();
		
		// Test
		assertNotNull(morlinn);
		assertEquals("Morlinn", morlinn.name);

		// Set the session
		Session.current().put("userid", morlinn.id);
		
		// Try getting the user
		User result = User.locate();
		assertEquals(morlinn, result);
	}
}