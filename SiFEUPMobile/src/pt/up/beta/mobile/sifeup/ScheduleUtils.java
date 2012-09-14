package pt.up.beta.mobile.sifeup;

import java.util.ArrayList;
import java.util.List;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.beta.mobile.datatypes.ScheduleBlock;
import pt.up.beta.mobile.datatypes.ScheduleRoom;
import pt.up.beta.mobile.datatypes.ScheduleTeacher;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.beta.mobile.utils.DateUtils;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;

public class ScheduleUtils {
	private ScheduleUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getStudentScheduleReply(
			String code, long mondayMillis, ResponseCommand command) {
		Time monday = new Time(DateUtils.TIME_REFERENCE);
        monday.set(mondayMillis);
        monday.normalize(false);
        String firstDay = monday.format("%Y%m%d");
        // Friday
        monday.set(DateUtils.moveDayofWeek(mondayMillis, 4));
        monday.normalize(false);
        String lastDay = monday.format("%Y%m%d");
		return new FetcherTask(command, new ScheduleParser(mondayMillis)).execute(SifeupAPI
				.getScheduleUrl(code,firstDay,lastDay));
	}
	
	
	
	public static AsyncTask<String, Void, ERROR_TYPE> getEmployeeScheduleReply(
			String code, long mondayMillis, ResponseCommand command) {
		Time monday = new Time(DateUtils.TIME_REFERENCE);
        monday.set(mondayMillis);
        monday.normalize(false);
        String firstDay = monday.format("%Y%m%d");
        // Friday
        monday.set(DateUtils.moveDayofWeek(mondayMillis, 4));
        monday.normalize(false);
        String lastDay = monday.format("%Y%m%d");
		return new FetcherTask(command, new ScheduleParser(mondayMillis)).execute(SifeupAPI
				.getTeacherScheduleUrl	(code,firstDay,lastDay));
	}	
	
	public static AsyncTask<String, Void, ERROR_TYPE> getRoomScheduleReply(
			String code, long mondayMillis, ResponseCommand command) {
		Time monday = new Time(DateUtils.TIME_REFERENCE);
        monday.set(mondayMillis);
        monday.normalize(false);
        String firstDay = monday.format("%Y%m%d");
        // Friday
        monday.set(DateUtils.moveDayofWeek(mondayMillis, 4));
        monday.normalize(false);
        String lastDay = monday.format("%Y%m%d");
		return new FetcherTask(command, new ScheduleParser(mondayMillis)).execute(SifeupAPI
				.getRoomScheduleUrl(code.substring(0,1),code.substring(1),firstDay,lastDay));
	}
	
	
	public static AsyncTask<String, Void, ERROR_TYPE> getUcScheduleReply(
			String code, long mondayMillis, ResponseCommand command) {
		Time monday = new Time(DateUtils.TIME_REFERENCE);
        monday.set(mondayMillis);
        monday.normalize(false);
        String firstDay = monday.format("%Y%m%d");
        // Friday
        monday.set(DateUtils.moveDayofWeek(mondayMillis, 4));
        monday.normalize(false);
        String lastDay = monday.format("%Y%m%d");
		return new FetcherTask(command, new ScheduleParser(mondayMillis)).execute(SifeupAPI
				.getUcScheduleUrl(code,firstDay,lastDay));
	}


	/**
	 * Parses a JSON String containing Exams info, Stores that info at
	 * Collection exams.
	 */

	private static class ScheduleParser implements ParserCommand {
		private final long mondayMillis;
		private ScheduleParser(long milli){
			mondayMillis = milli;
		}
		
		public Object parse(String page) {
			try {
				JSONObject jObject = new JSONObject(page);

		        // clear old schedule
		        List<ScheduleBlock> schedule = new ArrayList<ScheduleBlock>();

		        if (jObject.has("horario")) {
		            Log.e("JSON", "founded schedule");
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
			} catch (JSONException e) {
				e.printStackTrace();
				ACRA.getErrorReporter().handleSilentException(e);
				ACRA.getErrorReporter().handleSilentException(
						new RuntimeException("Id:"
								+ AccountUtils.getActiveUserCode(null) + "\n\n" + page));
			}
			return null;
		}

	}
}
