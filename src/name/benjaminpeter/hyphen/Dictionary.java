package name.benjaminpeter.hyphen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

/**
 * Class representing a single dictionary.
 */
public class Dictionary {
	/**
	 * The pointer to the hunspell object as returned by the hunspell constructor.
	 */
	private Pointer hunspellDict = null;

	/**
	 * The encoding used by this dictionary
	 */
	// private final String encoding = "UTF8";
	private final String encoding = "ISO-8859-1";

	private final HyphenLibrary hunspellLibrary;

	/**
	 * Creates an instance of the dictionary.
	 * 
	 * @param hunspellLibrary
	 *          The hunspell native library inside of class Hyphen
	 * @param baseFileName
	 *          the base name of the dictionary,
	 */
	Dictionary(final HyphenLibrary hunspellLibrary, final String baseFileName)
			throws FileNotFoundException, UnsupportedEncodingException {
		this.hunspellLibrary = hunspellLibrary;
		File dic = new File(baseFileName);

		if (!dic.canRead()) {
			throw new FileNotFoundException("The dictionary files " + baseFileName
					+ " could not be read");
		}

		hunspellDict = hunspellLibrary.hnj_hyphen_load(dic.toString());
		// TODO get encoding by reading the fist line of the file
		// encoding = hunspellLibrary.Hunspell_get_dic_encoding(hunspellDict);
	}

	/**
	 * Deallocate the dictionary.
	 */
	public void destroy() {
		if (hunspellLibrary != null && hunspellDict != null) {
			hunspellLibrary.hnj_hyphen_free(hunspellDict);
			hunspellDict = null;
		}
	}

	/**
	 * Check if a word is spelled correctly
	 * 
	 * @param word
	 *          The word to check.
	 * @throws HyphenationException
	 *           Is returned if the hyphenation fails. It is thrown when the C
	 *           library does not return zero.
	 */
	public String hyphenate(final String word) throws HyphenationException {
		PointerByReference rep = new PointerByReference();
		PointerByReference pos = new PointerByReference();
		PointerByReference cut = new PointerByReference();

		try {
			byte[] asciiWord = stringToBytes(word);
			byte[] hyphens = new byte[asciiWord.length + 1];
			byte[] hyphenated = new byte[asciiWord.length * 2];

			int success = hunspellLibrary.hnj_hyphen_hyphenate2(hunspellDict,
					asciiWord, asciiWord.length, hyphens, hyphenated, rep, pos, cut);
			if (success != 0) {
				throw new HyphenationException(
						"Hyphenation failed, please check input encoding and stderr output.");
			}

			return new String(hyphenated, 0, strlen(hyphenated),
					Charset.forName(encoding));
		} catch (UnsupportedEncodingException e) {
			throw new HyphenationException(
					"Hyphenation failed, please check system available encodings.");
		}
	}

	/*
	 * Determine the size of the string, byte is expected to be a zero terminated
	 * C-string.
	 */
	private int strlen(final byte[] hyphenated) {
		int i = 0;
		while (hyphenated[i] != 0) {
			i++;
		}
		return i;
	}

	/**
	 * Convert a Java string to a zero terminated byte array, in the encoding of
	 * the dictionary, as expected by the hunspell functions.
	 */
	protected byte[] stringToBytes(final String str)
			throws UnsupportedEncodingException {
		return (str + "\u0000").getBytes(encoding);
	}
}