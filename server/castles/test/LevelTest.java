import org.junit.*;

import java.util.*;
import play.test.*;
import models.*;

public class LevelTest extends UnitTest {
	@Before
	public void setUp() {
		Fixtures.deleteAllModels();
		Fixtures.loadModels("data.yml");
	}

	@Test
	public void verifyRank2() {
		Level level = Level.find("rank = ?", 1).first();
		
		assertNotNull(level);
		Level nextLevel = level.next();
		assertNotNull(nextLevel);
		
		int expected = 2;
		int result = nextLevel.rank;
		assertEquals(expected, result);

		// Repeat the load and try again
		nextLevel = level.next();
		assertNotNull(nextLevel);
		
		result = nextLevel.rank;
		assertEquals(expected, result);
	}

	@Test
	public void verifyLastRank() {
		int rankCount = (int)Level.count();
		Level level = Level.find("rank = ?", rankCount).first();
		
		assertNotNull(level);
		Level nextLevel = level.next();
		assertNull(nextLevel);
	}
}
