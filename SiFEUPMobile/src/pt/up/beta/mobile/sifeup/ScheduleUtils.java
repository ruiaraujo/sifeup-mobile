package pt.up.beta.mobile.sifeup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import external.com.google.android.apps.iosched.util.UIUtils;

import pt.up.beta.mobile.datatypes.Block;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.beta.mobile.utils.DateUtils;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;

public class ScheduleUtils {
	private ScheduleUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getStudentScheduleReply(
			String code, long mondayMillis, ResponseCommand command, File cache) {
		Time monday = new Time(UIUtils.TIME_REFERENCE);
        monday.set(mondayMillis);
        monday.normalize(false);
        String firstDay = monday.format("%Y%m%d");
        // Friday
        monday.set(DateUtils.moveDayofWeek(mondayMillis, 4));
        monday.normalize(false);
        String lastDay = monday.format("%Y%m%d");
		return new FetcherTask(command, new ScheduleParser(mondayMillis),cache).execute(SifeupAPI
				.getScheduleUrl(code,firstDay,lastDay));
	}
	
	
	
	public static AsyncTask<String, Void, ERROR_TYPE> getEmployeeScheduleReply(
			String code, long mondayMillis, ResponseCommand command, File cache) {
		Time monday = new Time(UIUtils.TIME_REFERENCE);
        monday.set(mondayMillis);
        monday.normalize(false);
        String firstDay = monday.format("%Y%m%d");
        // Friday
        monday.set(DateUtils.moveDayofWeek(mondayMillis, 4));
        monday.normalize(false);
        String lastDay = monday.format("%Y%m%d");
		return new FetcherTask(command, new ScheduleParser(mondayMillis),cache).execute(SifeupAPI
				.getTeacherScheduleUrl	(code,firstDay,lastDay));
	}	
	
	public static AsyncTask<String, Void, ERROR_TYPE> getRoomScheduleReply(
			String code, long mondayMillis, ResponseCommand command, File cache) {
		Time monday = new Time(UIUtils.TIME_REFERENCE);
        monday.set(mondayMillis);
        monday.normalize(false);
        String firstDay = monday.format("%Y%m%d");
        // Friday
        monday.set(DateUtils.moveDayofWeek(mondayMillis, 4));
        monday.normalize(false);
        String lastDay = monday.format("%Y%m%d");
		return new FetcherTask(command, new ScheduleParser(mondayMillis),cache).execute(SifeupAPI
				.getRoomScheduleUrl(code.substring(0,1),code.substring(1),firstDay,lastDay));
	}
	
	
	public static AsyncTask<String, Void, ERROR_TYPE> getUcScheduleReply(
			String code, long mondayMillis, ResponseCommand command, File cache) {
		Time monday = new Time(UIUtils.TIME_REFERENCE);
        monday.set(mondayMillis);
        monday.normalize(false);
        String firstDay = monday.format("%Y%m%d");
        // Friday
        monday.set(DateUtils.moveDayofWeek(mondayMillis, 4));
        monday.normalize(false);
        String lastDay = monday.format("%Y%m%d");
		return new FetcherTask(command, new ScheduleParser(mondayMillis),cache).execute(SifeupAPI
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
		        List<Block> schedule = new ArrayList<Block>();

		        if (jObject.has("horario")) {
		            Log.e("JSON", "founded schedule");
		            JSONArray jArray = jObject.getJSONArray("horario");

		            // iterate over jArray
		            for (int i = 0; i < jArray.length(); i++) {
		                // new JSONObject
		                JSONObject jBlock = jArray.getJSONObject(i);
		                SifeupUtils.removeEmptyKeys(jBlock);
		                // new Block
		                Block block = new Block();

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
		                if (jBlock.has("doc_sigla"))
		                    block.setTeacherAcronym(jBlock.getString("doc_sigla"));
		                if (jBlock.has("doc_codigo"))
		                    block.setTeacherCode(jBlock.getString("doc_codigo"));
		                if (jBlock.has("sala_cod")) {
		                    block.setRoomCode(jBlock.getString("sala_cod"));
		                    while (block.getRoomCode().length() < 3)
		                        block.setRoomCode("0" + block.getRoomCode());
		                }

		                if (jBlock.has("edi_cod"))
		                    block.setBuildingCode(jBlock.getString("edi_cod"));
		                if (jBlock.has("periodo"))
		                    block.setSemester(jBlock.getString("periodo"));
		                int secondYear = UIUtils.secondYearOfSchoolYear(mondayMillis);
		                int firstYear = secondYear - 1;
		                block.setYear(firstYear + "/" + secondYear);
		                // add block to schedule
		                schedule.add(block);
		            }
		        }
	            return schedule;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

	}
}
