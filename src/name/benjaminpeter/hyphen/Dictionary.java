package name.benjaminpeter.hyphen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

/**
 * Class representing a single dictionary.
 */
public class Dictionary {

	/**
	 * The pointer to the hunspell object as returned by the hunspell
	 * constructor.
	 */
	private Pointer hunspellDict = null;

	/**
	 * The encoding used by this dictionary
	 */
	private final String encoding;

	private final HyphenLibrary hunspellLibrary;

	/**
	 * Creates an instance of the dictionary.
	 * 
	 * @param hunspellLibrary
	 *            The hunspell native library inside of class Hyphen
	 * @param baseFileName
	 *            the base name of the dictionary,
	 * @throws IOException
	 *             If the dictionary file could not be read
	 */
	Dictionary(final HyphenLibrary hunspellLibrary, final String baseFileName)
			throws IOException {
		this.hunspellLibrary = hunspellLibrary;
		File dic = new File(baseFileName);

		if (!dic.canRead()) {
			throw new FileNotFoundException("The dictionary files "
					+ baseFileName + " could not be read");
		}

		hunspellDict = hunspellLibrary.hnj_hyphen_load(dic.toString());
		encoding = determineEncoding(dic);
	}

	private String determineEncoding(final File dic) throws IOException {
		InputStream fis = null;
		InputStreamReader is = null;
		BufferedReader br = null;

		try {
			fis = new FileInputStream(dic);
			is = new InputStreamReader(fis);
			br = new BufferedReader(is);
			String line;
			if ((line = br.readLine()) != null) {
				try {
					return Charset.forName(line).name();
				} catch (UnsupportedCharsetException e) {
					System.err
							.println("Could not determine dic encoding by first line: '"
									+ line + "' using latin1.");
				}
			}
		} finally {
			if (br != null) {
				br.close();
			}
			if (is != null) {
				is.close();
			}
			if (fis != null) {
				fis.close();
			}
		}
		return "ISO-8859-1";
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
	 * Hyphenate the word. The resulting word has "=" entered where hyphenation
	 * is allowed.
	 * 
	 * @param word
	 *            The word to check.
	 * @throws HyphenationException
	 *             Is returned if the hyphenation fails. It is thrown when the C
	 *             library does not return zero.
	 */
	public String hyphenate(final String word) throws HyphenationException {
		try {
			/*
			 * Case must be converted to lower case before hyphenation. The
			 * encoding must also match the dictionary's encoding. And finally
			 * we need to create a null terminated C-String.
			 */
			byte[] asciiWord = convertWordToCString(word, encoding);
			byte[] hyphens = createHyphensBuffer(asciiWord.length);
			byte[] hyphenated = createHyphenatedBuffer(asciiWord.length);
			/*
			 * I didn't understand how the "complementary" thing works yet, so
			 * just pass in null for now.
			 */
			int success = hunspellLibrary.hnj_hyphen_hyphenate2(hunspellDict,
					asciiWord, asciiWord.length, hyphens, hyphenated,
					newPointerRef(), newPointerRef(), newPointerRef());
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

	/**
	 * 
	 * @return A collection of the indexes of the letters after which a hyphen
	 *         must be added. For example "dan=ke" would contain { 2 }
	 */
	public Collection<Integer> hyphens(final String word)
			throws HyphenationException {
		try {
			/*
			 * Case must be converted to lower case before hyphenation. The
			 * encoding must also match the dictionary's encoding. And finally
			 * we need to create a null terminated C-String.
			 */
			byte[] asciiWord = convertWordToCString(word, encoding);
			byte[] hyphens = createHyphensBuffer(asciiWord.length);
			/*
			 * I didn't understand how the "complementary" thing works yet, so
			 * just pass in null for now.
			 */
			int success = hunspellLibrary.hnj_hyphen_hyphenate2(hunspellDict,
					asciiWord, asciiWord.length, hyphens, null,
					newPointerRef(), newPointerRef(), newPointerRef());
			if (success != 0) {
				throw new HyphenationException(
						"Hyphenation failed, please check input encoding and stderr output.");
			}

			List<Integer> hyphenIndexes = new LinkedList<Integer>();

			for (int i = 0; i < asciiWord.length + 1; ++i) {
				if ((hyphens[i] & 1) == 1) {
					hyphenIndexes.add(i);
				}
			}
			return hyphenIndexes;
		} catch (UnsupportedEncodingException e) {
			throw new HyphenationException(
					"Hyphenation failed, please check system available encodings.");
		}
	}

	/**
	 * 
	 * @return A collection of separatable syllable of the word. For example
   * "dan=ke" becomes "dan" and "ke". A non splitted word will return one
   * entry with the whole word.
	 */
	public Collection<String> syllables(final String word)
			throws HyphenationException {
		try {
			/*
			 * Case must be converted to lower case before hyphenation. The
			 * encoding must also match the dictionary's encoding. And finally
			 * we need to create a null terminated C-String.
			 */
			byte[] asciiWord = convertWordToCString(word, encoding);
			byte[] hyphens = createHyphensBuffer(asciiWord.length);
			/*
			 * I didn't understand how the "complementary" thing works yet, so
			 * just pass in null for now.
			 */
			int success = hunspellLibrary.hnj_hyphen_hyphenate2(hunspellDict,
					asciiWord, asciiWord.length, hyphens, null,
					newPointerRef(), newPointerRef(), newPointerRef());
			if (success != 0) {
				throw new HyphenationException(
						"Hyphenation failed, please check input encoding and stderr output.");
			}

			List<String> syllables = new LinkedList<String>();

      int start = 0;
      int length = 0;
			for (int i = 0; i < asciiWord.length + 1; ++i) {
				if ((hyphens[i] & 1) == 1) {
          length = i + 1;
          syllables.add(word.substring(start, length)); 
          start = length;  
				}
			}
      syllables.add(word.substring(start)); 
      return syllables;
		} catch (UnsupportedEncodingException e) {
			throw new HyphenationException(
					"Hyphenation failed, please check system available encodings.");
		}
	}

	private byte[] createHyphenatedBuffer(int length) {
		return new byte[length * 2];
	}

	private byte[] createHyphensBuffer(int length) {
		return new byte[length + 1];
	}

	private byte[] convertWordToCString(final String word, String encoding)
			throws UnsupportedEncodingException {
		return stringToBytes(word.toLowerCase(), encoding);
	}

	private PointerByReference newPointerRef() {
		return new PointerByReference(Pointer.NULL);
	}

	/*
	 * Determine the size of the string, byte is expected to be a zero
	 * terminated C-string.
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
	protected byte[] stringToBytes(final String str, final String encoding)
			throws UnsupportedEncodingException {
		return (str + "\u0000").getBytes(encoding);
	}
}
