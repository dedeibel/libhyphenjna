package name.benjaminpeter.hyphen;

import static org.junit.Assert.*;

import java.io.IOException;
import org.junit.Test;

public class HyphenTest {

	private static final String DIC_PATH = "test/resources/hyph_mini_de_iso.dic";

	private static final String DIC_PATH_UTF8 = "test/resources/hyph_mini_de_utf8.dic";

	@Test
	public void testInstance() {
		Hyphen firstInstance = Hyphen.getInstance();
		Hyphen secondInstance = Hyphen.getInstance();
		assertEquals(firstInstance, secondInstance);
		assertSame(firstInstance, secondInstance);
	}

	@Test
	public void testGetDictionary() throws HyphenationException, IOException {
		Hyphen hyphen = Hyphen.getInstance();
		Dictionary dic = hyphen.getDictionary(DIC_PATH);
		assertNotNull(dic);
	}

	@Test
	public void testHyphenateSimple() throws HyphenationException,
			UnsatisfiedLinkError, UnsupportedOperationException, IOException {
		Dictionary dic = Hyphen.getInstance().getDictionary(DIC_PATH);
		assertEquals("dan=ke", dic.hyphenate("danke"));
		assertEquals("ver=si=che=rung", dic.hyphenate("Versicherung"));
	}

	@Test
	public void testHyphenateUmlaut() throws HyphenationException,
			UnsatisfiedLinkError, UnsupportedOperationException, IOException {
		Dictionary dic = Hyphen.getInstance().getDictionary(DIC_PATH);
		assertEquals("müh=le", dic.hyphenate("Mühle"));
	}

	@Test
	public void testHyphenateUmlautUTF8Dict() throws HyphenationException,
			UnsatisfiedLinkError, UnsupportedOperationException, IOException {
		Dictionary dic = Hyphen.getInstance().getDictionary(DIC_PATH_UTF8);
		assertEquals("müh=le", dic.hyphenate("Mühle"));
	}
}
