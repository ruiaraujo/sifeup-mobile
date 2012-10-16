package pt.up.beta.mobile.datatypes;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StudentCourse {
	private final String courseId;
	private final String courseType;
	private final String courseTypeDesc;
	private final String courseName;
	private final String firstYear;
	private final String firstDate;
	private final String degreeId;
	private final String currentYear;
	private final String state;
	private final String stateName;
	private final String stateBegin;
	private final String stateEnd;
	private final String placeName;
	private final String placeAcronym;
	private final String curriculumYear;

	public StudentCourse(String courseId, String courseType,
			String courseTypeDesc, String courseName, String firstYear,
			String firstDate, String degreeId, String currentYear,
			String state, String stateName, String stateBegin, String stateEnd,
			String placeName, String placeAcronym, String curriculumYear) {
		super();
		this.courseId = courseId;
		this.courseType = courseType;
		this.courseTypeDesc = courseTypeDesc;
		this.courseName = courseName;
		this.firstYear = firstYear;
		this.firstDate = firstDate;
		this.degreeId = degreeId;
		this.currentYear = currentYear;
		this.state = state;
		this.stateName = stateName;
		this.stateBegin = stateBegin;
		this.stateEnd = stateEnd;
		this.placeName = placeName;
		this.placeAcronym = placeAcronym;
		this.curriculumYear = curriculumYear;
	}

	public String getCourseId() {
		return courseId;
	}

	public String getCourseType() {
		return courseType;
	}

	public String getCourseTypeDesc() {
		return courseTypeDesc;
	}

	public String getCourseName() {
		return courseName;
	}

	public String getFirstYear() {
		return firstYear;
	}

	public String getFirstDate() {
		return firstDate;
	}

	public String getDegreeId() {
		return degreeId;
	}

	public String getCurrentYear() {
		return currentYear;
	}

	public String getState() {
		return state;
	}

	public String getStateName() {
		return stateName;
	}

	public String getStateBegin() {
		return stateBegin;
	}

	public String getStateEnd() {
		return stateEnd;
	}

	public String getPlaceName() {
		return placeName;
	}

	public String getPlaceAcronym() {
		return placeAcronym;
	}

	public String getCurriculumYear() {
		return curriculumYear;
	}

	public static List<StudentCourse> parseJSON(JSONArray array) throws JSONException {
		final List<StudentCourse> arrayCourse = new ArrayList<StudentCourse>();
		for (int i = 0; i < array.length(); ++i) {
			JSONObject course = array.getJSONObject(i);
			arrayCourse.add(new StudentCourse(course.getString("fest_id"),
					course.getString("fest_tipo"), course
							.getString("fest_tipo_descr"), course
							.getString("cur_nome"), course
							.getString("fest_a_lect_1_insc"), course
							.getString("fest_d_1_insc"), course
							.getString("cur_id"), course
							.getString("est_alectivo"), course
							.getString("est_sig"),
					course.getString("est_nome"), course
							.getString("est_d_inicio"), course
							.getString("est_d_fim"), course
							.getString("inst_nome"), course
							.getString("inst_sigla"), course
							.getString("ano_curricular")));
		}
		return null;
	}
}
