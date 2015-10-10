package pt.up.mobile.sifeup;

public interface ResponseCommand<T> {

	enum ERROR_TYPE{
		CANCELLED,
		AUTHENTICATION,
		NETWORK,
		GENERAL
	};
	public void onError( ERROR_TYPE error );
	
	public void onResultReceived( T results );
}
