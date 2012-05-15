package name.benjaminpeter.hyphen;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import name.benjaminpeter.hyphen.Hyphen.Dictionary;

import org.junit.Test;

public class HyphenTest {

	@Test
	public void testInstance() {
		Hyphen firstInstance = Hyphen.getInstance();
		Hyphen secondInstance = Hyphen.getInstance();
		assertEquals(firstInstance, secondInstance);
		assertSame(firstInstance, secondInstance);
	}

	@Test
	public void testGetDictionary() throws FileNotFoundException,
			UnsupportedEncodingException {
		Hyphen hyphen = Hyphen.getInstance();
		Dictionary dic = hyphen.getDictionary("test/resources/hyph_mini_de.dic");
		assertNotNull(dic);
		assertEquals("dan=ke", dic.hyphenate("danke"));
	}
}
