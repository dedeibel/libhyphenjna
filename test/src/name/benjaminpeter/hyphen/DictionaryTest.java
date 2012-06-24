package name.benjaminpeter.hyphen;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

public class DictionaryTest {
	@Test
	public void testHypensEmpty() throws UnsatisfiedLinkError,
			UnsupportedOperationException, IOException, HyphenationException {
		Dictionary dic = Hyphen.getInstance().getDictionary(
				TestConstants.DIC_PATH_LATIN1);
		Collection<Integer> hyphens = dic.hyphens("");
		assertNotNull(hyphens);
		assertEquals(0, hyphens.size());
	}

	@Test
	public void testHypensSimple() throws UnsatisfiedLinkError,
			UnsupportedOperationException, IOException, HyphenationException {
		Dictionary dic = Hyphen.getInstance().getDictionary(
				TestConstants.DIC_PATH_LATIN1);
		Collection<Integer> hyphens = dic.hyphens("danke");
		assertNotNull(hyphens);
		assertEquals(1, hyphens.size());
		assertEquals(Integer.valueOf(2), hyphens.iterator().next());
	}

	@Test
	public void testHypensMultiple() throws UnsatisfiedLinkError,
			UnsupportedOperationException, IOException, HyphenationException {
		Dictionary dic = Hyphen.getInstance().getDictionary(
				TestConstants.DIC_PATH_LATIN1);
		Collection<Integer> hyphens = dic.hyphens("Versicherung");
		assertNotNull(hyphens);
		assertEquals(3, hyphens.size());
		assertTrue(Arrays.asList(Integer.valueOf(2), Integer.valueOf(4),
				Integer.valueOf(7)).equals(hyphens));
	}

	@Test
	public void testSyllablesEmpty() throws UnsatisfiedLinkError,
			UnsupportedOperationException, IOException, HyphenationException {
		Dictionary dic = Hyphen.getInstance().getDictionary(
				TestConstants.DIC_PATH_LATIN1);
		Collection<String> syllables = dic.syllables("");
		assertNotNull(syllables);
		assertEquals(1, syllables.size());
		assertEquals("", syllables.iterator().next());
	}
   
	@Test
	public void testSyllablesSimple() throws UnsatisfiedLinkError,
			UnsupportedOperationException, IOException, HyphenationException {
		Dictionary dic = Hyphen.getInstance().getDictionary(
				TestConstants.DIC_PATH_LATIN1);
		Collection<String> syllables = dic.syllables("danke");
		assertNotNull(syllables);
		assertEquals(2, syllables.size());
    Iterator<String> it = syllables.iterator();
		assertEquals("dan", it.next());
		assertEquals("ke", it.next());
	}

	@Test
	public void testSyllablesMultiple() throws UnsatisfiedLinkError,
			UnsupportedOperationException, IOException, HyphenationException {
		Dictionary dic = Hyphen.getInstance().getDictionary(
				TestConstants.DIC_PATH_LATIN1);
		Collection<String> syllables = dic.syllables("versicherung");
		assertNotNull(syllables);
		assertEquals(4, syllables.size());
    Iterator<String> it = syllables.iterator();
		assertEquals("ver", it.next());
		assertEquals("si", it.next());
		assertEquals("che", it.next());
		assertEquals("rung", it.next());
	}
}
