package pt.up.fe.mobile.sifeup;

public interface ResponseCommand {

	enum ERROR_TYPE{
		AUTHENTICATION,
		NETWORK,
		GENERAL
	};
	public void onError( ERROR_TYPE error );
	
	public void onResultReceived( Object ... results );
}
