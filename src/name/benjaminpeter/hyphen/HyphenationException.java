package name.benjaminpeter.hyphen;

public class HyphenationException extends Exception {

	private static final long serialVersionUID = 6871376136469406947L;

	public HyphenationException() {
	}

	public HyphenationException(final String arg0) {
		super(arg0);
	}

	public HyphenationException(final Throwable arg0) {
		super(arg0);
	}

	public HyphenationException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

}
