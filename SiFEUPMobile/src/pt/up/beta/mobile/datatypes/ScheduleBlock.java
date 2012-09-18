package pt.up.beta.mobile.datatypes;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.beta.mobile.sifeup.SifeupUtils;
import pt.up.beta.mobile.utils.DateUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
/**
 * 
 * Represents a lecture.
 * Holds all data about it.
 * (time, place, teacher)
 *
 */
public class ScheduleBlock implements Parcelable{
	private int weekDay; // [0 ... 6]
	private int startTime; // seconds from midnight
	
	private String lectureCode; // EIC0036
	private String lectureAcronym; // ex: SDIS
	private String lectureType; // T|TP|P
	private double lectureDuration; // 2; 1,5 (in hours)
	private String classAcronym; // 3MIEIC1
    private String roomCod; // in case of mutiple rooms this holds a string nice to show.
	
	private final List<ScheduleTeacher> teachers;
	private final List<ScheduleRoom> rooms;
	

	private String semester; // 2S
	
	private String year; // 2010/2011

	public ScheduleBlock(){
		teachers = new ArrayList<ScheduleTeacher>();
		rooms = new ArrayList<ScheduleRoom>();
	}
	
	public int getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(int weekDay) {
		this.weekDay = weekDay;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public String getLectureCode() {
		return lectureCode;
	}

	public void setLectureCode(String lectureCode) {
		this.lectureCode = lectureCode;
	}

	public String getLectureAcronym() {
		return lectureAcronym;
	}

	public void setLectureAcronym(String lectureAcronym) {
		this.lectureAcronym = lectureAcronym;
	}

	public String getLectureType() {
		return lectureType;
	}

	public void setLectureType(String lectureType) {
		this.lectureType = lectureType;
	}

	public double getLectureDuration() {
		return lectureDuration;
	}

	public void setLectureDuration(double lectureDuration) {
		this.lectureDuration = lectureDuration;
	}

	public String getClassAcronym() {
		return classAcronym;
	}

	public void setClassAcronym(String classAcronym) {
		this.classAcronym = classAcronym;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getYear() {
		return year;
	}
	
	public String getRoomCod() {
        return roomCod;
    }

    public void setRoomCod(String roomCod) {
        this.roomCod = roomCod;
    }

    public void addTeacher(ScheduleTeacher teacher){
		teachers.add(teacher);
	}

	public List<ScheduleTeacher> getTeachers(){
		return teachers;
	}
	
	public void addRoom(ScheduleRoom room){
		rooms.add(room);
	}

	public List<ScheduleRoom> getRooms(){
		return rooms;
	}
	
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(weekDay);
		dest.writeInt(startTime);
		dest.writeInt(lectureCode!=null?1:0);
		if ( lectureCode != null ) dest.writeString(lectureCode);
		dest.writeInt(lectureAcronym!=null?1:0);
		if ( lectureAcronym != null ) dest.writeString(lectureAcronym);
		dest.writeInt(lectureType!=null?1:0);
		if ( lectureType != null ) dest.writeString(lectureType);
		dest.writeDouble(lectureDuration);
		dest.writeInt(classAcronym!=null?1:0);
		if (  classAcronym != null ) dest.writeString(classAcronym);
		dest.writeInt(semester!=null?1:0);
		if ( semester != null ) dest.writeString(semester);
		dest.writeInt(year!=null?1:0);
		if ( year != null ) dest.writeString(year);
        dest.writeInt(roomCod!=null?1:0);
        if ( roomCod != null ) dest.writeString(roomCod);
		dest.writeTypedList(teachers);
		dest.writeTypedList(rooms);
	}
	
	private ScheduleBlock(Parcel in){
		weekDay = in.readInt();
		startTime = in.readInt();
		if ( in.readInt() == 1 ) lectureCode = in.readString();
		if ( in.readInt() == 1 ) lectureAcronym = in.readString();
		if ( in.readInt() == 1 ) lectureType = in.readString();
		lectureDuration = in.readDouble();
		if ( in.readInt() == 1 ) classAcronym = in.readString();
		if ( in.readInt() == 1 ) semester = in.readString();
		if ( in.readInt() == 1 ) year = in.readString();
        if ( in.readInt() == 1 ) roomCod = in.readString();
		teachers = new ArrayList<ScheduleTeacher>();
		in.readTypedList(teachers, ScheduleTeacher.CREATOR);
		rooms = new ArrayList<ScheduleRoom>();
		in.readTypedList(rooms, ScheduleRoom.CREATOR);
	}
	

    public static final Parcelable.Creator<ScheduleBlock> CREATOR = new Parcelable.Creator<ScheduleBlock>() {
        public ScheduleBlock createFromParcel(Parcel in) {
            return new ScheduleBlock(in);
        }

        public ScheduleBlock[] newArray(int size) {
            return new ScheduleBlock[size];
        }
    };
    
    public static List<ScheduleBlock> parseListJSON(String page, long mondayMillis) throws JSONException{
		JSONObject jObject = new JSONObject(page);

        // clear old schedule
        List<ScheduleBlock> schedule = new ArrayList<ScheduleBlock>();

        if (jObject.has("horario")) {
            Log.d("JSON", "founded schedule");
            JSONArray jArray = jObject.getJSONArray("horario");

            // iterate over jArray
            for (int i = 0; i < jArray.length(); i++) {
                // new JSONObject
                JSONObject jBlock = jArray.getJSONObject(i);
                SifeupUtils.removeEmptyKeys(jBlock);
                // new Block
                ScheduleBlock block = new ScheduleBlock();

                if (jBlock.has("dia"))
                    block.setWeekDay(jBlock.getInt("dia") - 2); // Monday is
                                                                // index 0
                if (jBlock.has("hora_inicio"))
                    block.setStartTime(jBlock.getInt("hora_inicio"));
                if (jBlock.has("cad_codigo"))
                    block.setLectureCode(jBlock.getString("cad_codigo"));
                if (jBlock.has("cad_sigla"))
                    block.setLectureAcronym(jBlock.getString("cad_sigla"));
                if (jBlock.has("tipo"))
                    block.setLectureType(jBlock.getString("tipo"));
                if (jBlock.has("aula_duracao"))
                    block.setLectureDuration(jBlock.getDouble("aula_duracao"));
                if (jBlock.has("turma_sigla"))
                    block.setClassAcronym(jBlock.getString("turma_sigla"));
               
                //Adding teachers
                if ( jBlock.has("doc_codigo") ){
                	final String code = jBlock.getString("doc_codigo"); 
                	final String name = jBlock.getString("doc_nome");
                	final String acronym = jBlock.getString("doc_sigla");
                	block.addTeacher(new ScheduleTeacher(code, acronym, name));
                }
                else
                {
                	JSONArray teachers = jBlock.optJSONArray("docentes");
                	if ( teachers != null ){
                		for ( int j = 0 ; j < teachers.length(); ++j ){
                			JSONObject teacher = teachers.getJSONObject(j);
                			final String code = teacher.getString("doc_codigo"); 
		                	final String name = teacher.getString("doc_nome");
		                	final String acronym = teacher.getString("doc_sigla");
		                	block.addTeacher(new ScheduleTeacher(code, acronym, name));
                		}
                	}
                }
                // adding the rooms
                if ( jBlock.has("edi_cod") ){
                	final String code = jBlock.getString("edi_cod"); 
                	final String buildingBlock = jBlock.optString("bloco");
                	String room = jBlock.getString("sala_cod");
	                    while (room.length() < 3)
	                        room = "0" + room;
	                block.setRoomCod(code+room);
                	block.addRoom(new ScheduleRoom(code,buildingBlock, room));
                }
                else
                {
                    block.setRoomCod(jBlock.getString("sala_cod"));
                	JSONArray rooms = jBlock.optJSONArray("salas");
                	if ( rooms != null ){
                		for ( int j = 0 ; j < rooms.length(); ++j ){
                			JSONObject room = rooms.getJSONObject(j);
		                	final String code = room.getString("edi_cod"); 
		                	final String buildingBlock = room.optString("bloco");
		                	String roomStr = room.getString("sala_cod");
			                    while (roomStr.length() < 3)
			                        roomStr = "0" + roomStr;

		                	block.addRoom(new ScheduleRoom(code,buildingBlock, roomStr));
                		}
                	}
                }

                if (jBlock.has("periodo"))
                    block.setSemester(jBlock.getString("periodo"));
                int secondYear = DateUtils.secondYearOfSchoolYear(mondayMillis);
                int firstYear = secondYear - 1;
                block.setYear(firstYear + "/" + secondYear);
                // add block to schedule
                schedule.add(block);
            }
        }
        return schedule;
    }

}