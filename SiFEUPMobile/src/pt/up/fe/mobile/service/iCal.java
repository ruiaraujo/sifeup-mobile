package pt.up.fe.mobile.service;
import java.util.ArrayList;


public class iCal {
	
	ArrayList<iCalEvent> events = new ArrayList<iCalEvent>();
	iCalTimezone timeZone = new iCalTimezone();
	
	private interface TOOLS {
		String SEPARATOR = ":";
		String NEWLINE = "\n";
	}
	
	private interface BEGIN {
		String NAME = "BEGIN";
		String VALUE = "VCALENDAR";
	}
	
	private interface METHOD {
		String NAME = "METHOD";
		String VALUE = "PUBLISH";
	}
	
	private interface PRODID {
		String NAME = "PRODID";
		String VALUE = "";
	}
	
	private interface CALSCALE {
		String NAME = "CALSCALE";
		String VALUE = "GREGORIAN";
	}
	
	private interface VERSION {
		String NAME = "VERSION";
		String VALUE = "2.0";
	}
	
	private interface END {
		String NAME = "END";
		String VALUE = "VCALENDAR";
	}

	public String getCode() {
		return generateBegin() +
		generateMethod() + 
		generateProdid() +
		generateCalscale() +
		generateVersion() +
		
		timeZone.getCode() +
		
		generateEvents() +
		generateEnd();
	}
	
	private String generateEvents() {
		String auxCode = "";
		for(iCalEvent event : events)
			auxCode += event.getCode();
			
		return auxCode;
	}
	
	public void addEvent(iCalEvent event){
		this.events.add(event);
	}

	private String generateBegin(){
		return BEGIN.NAME + TOOLS.SEPARATOR + BEGIN.VALUE + TOOLS.NEWLINE;
	}
	
	private String generateEnd(){
		return END.NAME + TOOLS.SEPARATOR + END.VALUE + TOOLS.NEWLINE;
	}
	
	private String generateVersion(){
		return VERSION.NAME + TOOLS.SEPARATOR + VERSION.VALUE + TOOLS.NEWLINE;
	}
	
	private String generateMethod(){
		return METHOD.NAME + TOOLS.SEPARATOR + METHOD.VALUE + TOOLS.NEWLINE;
	}
	
	private String generateProdid(){
		return PRODID.NAME + TOOLS.SEPARATOR + PRODID.VALUE + TOOLS.NEWLINE;
	}
	
	private String generateCalscale(){
		return CALSCALE.NAME + TOOLS.SEPARATOR + CALSCALE.VALUE + TOOLS.NEWLINE;
	}

}
