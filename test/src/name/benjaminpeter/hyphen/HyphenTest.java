package name.benjaminpeter.hyphen;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class HyphenTest {

	private static final String DIC_PATH = "/Users/bpeter/Projects/libhyphenjna/data/hyph_de_DE.dic";

	@Test
	public void testInstance() {
		Hyphen firstInstance = Hyphen.getInstance();
		Hyphen secondInstance = Hyphen.getInstance();
		assertEquals(firstInstance, secondInstance);
		assertSame(firstInstance, secondInstance);
	}

	@Test
	public void testGetDictionary() throws FileNotFoundException,
			UnsupportedEncodingException, HyphenationException {
		Hyphen hyphen = Hyphen.getInstance();
		Dictionary dic = hyphen.getDictionary(DIC_PATH);
		assertNotNull(dic);
	}

	@Test
	public void testHyphenateSimple() throws FileNotFoundException,
			UnsupportedEncodingException, HyphenationException {
		Dictionary dic = Hyphen.getInstance().getDictionary(DIC_PATH);
		assertEquals("dan=ke", dic.hyphenate("danke"));
		assertEquals("ver=si=che=rung", dic.hyphenate("versicherung"));
	}

	@Test
	public void testHyphenateUmlaut() throws FileNotFoundException,
			UnsupportedEncodingException, HyphenationException {
		Dictionary dic = Hyphen.getInstance().getDictionary(DIC_PATH);
		assertEquals("MŸh=le", dic.hyphenate("MŸhle"));
	}
}
