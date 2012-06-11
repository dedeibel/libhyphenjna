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
	private final String encoding;

	private final HyphenLibrary hunspellLibrary;

	/**
	 * Creates an instance of the dictionary.
	 * 
	 * @param hunspellLibrary
	 *          The hunspell native library inside of class Hyphen
	 * @param baseFileName
	 *          the base name of the dictionary,
	 * @throws IOException
	 *           If the dictionary file could not be read
	 */
	Dictionary(final HyphenLibrary hunspellLibrary, final String baseFileName)
			throws IOException {
		this.hunspellLibrary = hunspellLibrary;
		File dic = new File(baseFileName);

		if (!dic.canRead()) {
			throw new FileNotFoundException("The dictionary files " + baseFileName
					+ " could not be read");
		}

		hunspellDict = hunspellLibrary.hnj_hyphen_load(dic.toString());
		encoding = determineEncoding(dic);
	}

	private String determineEncoding(final File dic) throws IOException {
		InputStream fis       = null;
    InputStreamReader is  = null;
		BufferedReader br     = null;

    try {
		  fis = new FileInputStream(dic);
      is = new InputStreamReader(fis);
		  br = new BufferedReader(is);
		  String line;
		  if ((line = br.readLine()) != null) {
		  	try {
		  		return Charset.forName(line).name();
		  	} catch (UnsupportedCharsetException e) {
		  		System.err.println("Could not determine dic encoding by first line: '"
		  				+ line + "' using latin1.");
		  	}
		  }
    }
    finally {
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
			/*
			 * Case must be converted to lower case before hyphenation. The encoding
			 * must also match the dictionary's encoding. And finally we need to
			 * create a null terimated C-String.
			 */
			byte[] asciiWord = stringToBytes(word.toLowerCase(), encoding);
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
	protected byte[] stringToBytes(final String str, final String encoding)
			throws UnsupportedEncodingException {
		return (str + "\u0000").getBytes(encoding);
	}
}
