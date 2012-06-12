package name.benjaminpeter.hyphen;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

public class DictionaryTest {

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
				Integer.valueOf(7)).equals(dic.hyphens("Versicherung")));
	}

}
