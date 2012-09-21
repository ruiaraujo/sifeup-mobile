package pt.up.beta.mobile.sifeup;

public interface ParserCommand<T> {
	public T parse(String page);
}
