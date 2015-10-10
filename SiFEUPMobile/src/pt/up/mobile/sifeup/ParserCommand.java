package pt.up.mobile.sifeup;

public interface ParserCommand<T> {
	public T parse(String page);
}
