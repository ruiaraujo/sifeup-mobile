package pt.up.fe.mobile.service;


public class iCalEvent {
	
	private interface TOOLS {
		String SEPARATOR = ":";
		String NEWLINE = "\n";
	}
	
	private interface BEGIN {
		String NAME = "BEGIN";
		String VALUE = "VEVENT";
	}
	
	private interface SEQUENCE {
		String NAME = "SEQUENCE";
		String VALUE = "0";
	}
	
	private String dtStamp = "";
	private interface DTSTAMP {
		String NAME = "DTSTAMP";
	}
	
	private String created = "";
	private interface CREATED {
		String NAME = "CREATED";
	}
	
	private interface TRANSP {
		String NAME = "TRANSP";
		String VALUE = "OPAQUE";
	}
	
	private String summary = "";
	private interface SUMMARY {
		String NAME = "SUMMARY";
	}
	
	private String location = "";
	private interface LOCATION {
		String NAME = "LOCATION";
	}
	
	private String description = "";
	private interface DESCRIPTION {
		String NAME = "DESCRIPTION;CHARSET=ISO-8859-1";
	}
	
	private String dtStart = "";
	private interface DTSTART {
		String NAME = "DTSTART;TZID=Europe/Lisbon";
	}
	
	private String dtEnd = "";
	private interface DTEND {
		String NAME = "DTEND;TZID=Europe/Lisbon";
	}
	
	private interface RRULE {
		String NAME = "RRULE";
		String VALUE = "FREQ=WEEKLY;INTERVAL=1;UNTIL=20110620T000000Z";
	}
	
	private interface END {
		String NAME = "END";
		String VALUE = "VEVENT";
	}
	
	public iCalEvent(String dtstart, String dtend) {
		dtStart = dtstart;
		dtEnd = dtend;
	}
	
	public void setDtStamp(String dtStamp){
		this.dtStamp = dtStamp;
	}
	
	public void setCreated(String created){
		this.created = created;
	}
	
	public void setSummary(String summary){
		this.summary = summary;
	}
	
	public void setLocation(String location){
		this.location = location;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public String getCode() {
		return generateBegin()
		+ generateSequence()
		//+ generateDTStamp()
		//+ generateCreated()
		+ generateTransparency()
		+ generateSummary()
		+ generateLocation()
		+ generateDescription()
		+ generateDTStart()
		+ generateDTEnd()
		+ generateRRule()
		+ generateEnd();
	}
	
	private String generateBegin(){
		return BEGIN.NAME + TOOLS.SEPARATOR + BEGIN.VALUE + TOOLS.NEWLINE;
	}
	
	private String generateEnd(){
		return END.NAME + TOOLS.SEPARATOR + END.VALUE + TOOLS.NEWLINE;
	}
	
	private String generateSequence(){
		return SEQUENCE.NAME + TOOLS.SEPARATOR + SEQUENCE.VALUE + TOOLS.NEWLINE;
	}
	
	private String generateDTStamp(){
		return DTSTAMP.NAME + TOOLS.SEPARATOR + dtStamp + TOOLS.NEWLINE;
	}
	
	private String generateCreated(){
		return CREATED.NAME + TOOLS.SEPARATOR + created + TOOLS.NEWLINE;
	}
	
	private String generateTransparency(){
		return TRANSP.NAME + TOOLS.SEPARATOR + TRANSP.VALUE + TOOLS.NEWLINE;
	}
	
	private String generateSummary(){
		return SUMMARY.NAME + TOOLS.SEPARATOR + summary + TOOLS.NEWLINE;
	}
	
	private String generateLocation(){
		return LOCATION.NAME + TOOLS.SEPARATOR + location + TOOLS.NEWLINE;
	}
	
	private String generateDescription(){
		return DESCRIPTION.NAME + TOOLS.SEPARATOR + description + TOOLS.NEWLINE;
	}
	
	private String generateDTStart(){
		return DTSTART.NAME + TOOLS.SEPARATOR + dtStart + TOOLS.NEWLINE;
	}
	
	private String generateDTEnd(){
		return DTEND.NAME + TOOLS.SEPARATOR + dtEnd + TOOLS.NEWLINE;
	}
	
	private String generateRRule(){
		return RRULE.NAME + TOOLS.SEPARATOR + RRULE.VALUE + TOOLS.NEWLINE;
	}	
	
}
