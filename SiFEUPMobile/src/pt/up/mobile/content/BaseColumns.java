package pt.up.mobile.content;

public interface BaseColumns {
	static final String COLUMN_STATE = "sync_state";
	static final String SQL_CREATE_STATE = COLUMN_STATE + " text ";
	public final static String CACHE_SELECTION = COLUMN_STATE + "=?";
}
