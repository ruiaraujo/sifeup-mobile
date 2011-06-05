package pt.up.fe.mobile.service;


public class iCalTimezone {
	
	private interface TOOLS {
		String SEPARATOR = ":";
		String NEWLINE = "\n";
	}
	
	private interface BEGIN {
		String NAME = "BEGIN";
		String VALUE = "VTIMEZONE";
	}
	
	private interface TZID {
		String NAME = "TZID";
		String VALUE = "Europe/Lisbon";
	}
	
	private interface END {
		String NAME = "END";
		String VALUE = "VTIMEZONE";
	}
	
	public String getCode(){
		return generateBegin() +
		generateTzid() + 
		generateEnd();
	}
	
	private String generateBegin(){
		return BEGIN.NAME + TOOLS.SEPARATOR + BEGIN.VALUE + TOOLS.NEWLINE;
	}
	
	private String generateEnd(){
		return END.NAME + TOOLS.SEPARATOR + END.VALUE + TOOLS.NEWLINE;
	}
	
	private String generateTzid(){
		return TZID.NAME + TOOLS.SEPARATOR + TZID.VALUE + TOOLS.NEWLINE;
	}

}
