package name.benjaminpeter.hyphen;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

/**
 * Functions from $hyphen/src/hyphen/hyphen.h
 * 
 * @author Benjamin Peter <BenjaminPeter@arcor.de>
 */

public interface HyphenLibrary extends Library {

	/**
	 * Create the hyphen lib instance
	 * 
	 * @param fn
	 *          The hyphenation file path
	 * @return The hyphen library object
	 */
	public Pointer hnj_hyphen_load(String fn);

	/**
	 * Free the hyphen lib
	 * 
	 * @param dict
	 *          The hyphen library object returned by Hyphen_load
	 */
	public void hnj_hyphen_free(Pointer dict);

	public int hnj_hyphen_hyphenate2(Pointer dict, byte[] word, int word_size,
			byte[] hyphens, byte[] hyphenated_word, PointerByReference rep,
			PointerByReference pos, PointerByReference cut);
}
