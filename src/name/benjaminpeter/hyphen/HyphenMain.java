package name.benjaminpeter.hyphen;

/**
 * Simple testing and native build utility class, not useful in applications.
 * 
 * The Hunspell java bindings are licensed under the same terms as Hunspell
 * itself (GPL/LGPL/MPL tri-license), see the file COPYING.txt in the root of
 * the distribution for the exact terms.
 * 
 * @author Flemming Frandsen (flfr at stibo dot com)
 */

public class HyphenMain {
	public static void main(final String[] args) {
		try {
			if (args.length == 1 && args[0].equals("-libname")) {
				System.out.println(Hyphen.libName());

			} else if (args.length == 2) {
				final String dict = args[0];
				final String word = args[1];
				System.err.println("Loading Hyphen, dict: " + dict + " word: " + word);
				Hyphen.Dictionary d = Hyphen.getInstance().getDictionary(dict);
				System.err.println("Hyphen library and dictionary loaded");

				String hyphenated = d.hyphenate(word);
				System.out.println(hyphenated);
			} else {
				System.err.println("Usage: <dict file> <word>");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed: " + e);
		}
	}
}
