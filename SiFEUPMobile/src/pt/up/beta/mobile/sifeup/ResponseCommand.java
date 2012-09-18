package pt.up.beta.mobile.sifeup;

public interface ResponseCommand {

	enum ERROR_TYPE{
		CANCELLED,
		AUTHENTICATION,
		NETWORK,
		GENERAL
	};
	public void onError( ERROR_TYPE error );
	
	public void onResultReceived( Object ... results );
}
