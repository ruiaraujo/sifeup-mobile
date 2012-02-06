package pt.up.fe.mobile.utils.calendar;

public class Event {
    private final String title;
    private final String eventLocation;
    private final String description;
    private final Long timeStart;
    private final Long timeEnd;
    public Event(String title, String eventLocation, String description,
            Long timeStart, Long timeEnd) {
        super();
        this.title = title;
        this.eventLocation = eventLocation;
        this.description = description;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }
    public String getTitle() {
        return this.title;
    }
    public String getEventLocation() {
        return this.eventLocation;
    }
    public String getDescription() {
        return this.description;
    }
    public Long getTimeStart() {
        return this.timeStart;
    }
    public Long getTimeEnd() {
        return this.timeEnd;
    }
    
    
}
