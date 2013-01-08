package pt.up.beta.mobile.utils;

import java.lang.reflect.Type;

import pt.up.beta.mobile.datatypes.Exam;
import pt.up.beta.mobile.datatypes.Park;
import pt.up.beta.mobile.datatypes.Schedule;
import pt.up.beta.mobile.datatypes.ScheduleBlock;
import pt.up.beta.mobile.datatypes.Student;
import pt.up.beta.mobile.datatypes.StudentCourse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;

public class GsonUtils {
	private static Gson parser;

	public synchronized static Gson getGson() {
		if (parser == null) {
			final GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(Exam.class,
					new InstanceCreator<Exam>() {
						@Override
						public Exam createInstance(Type type) {
							return Exam.CREATOR.createFromParcel(null);
						}
					});
			gsonBuilder.registerTypeAdapter(Schedule.class,
					new InstanceCreator<Schedule>() {
						@Override
						public Schedule createInstance(Type type) {
							return Schedule.CREATOR.createFromParcel(null);
						}
					});
			gsonBuilder.registerTypeAdapter(ScheduleBlock.class,
					new InstanceCreator<ScheduleBlock>() {
						@Override
						public ScheduleBlock createInstance(Type type) {
							return ScheduleBlock.CREATOR.createFromParcel(null);
						}
					});
			gsonBuilder.registerTypeAdapter(StudentCourse.class,
					new InstanceCreator<StudentCourse>() {
						@Override
						public StudentCourse createInstance(Type type) {
							return StudentCourse.CREATOR.createFromParcel(null);
						}
					});
			gsonBuilder.registerTypeAdapter(Student.class,
					new InstanceCreator<Student>() {
						@Override
						public Student createInstance(Type type) {
							return Student.CREATOR.createFromParcel(null);
						}
					});
			gsonBuilder.registerTypeAdapter(StudentCourse.class,
					new InstanceCreator<StudentCourse>() {
						@Override
						public StudentCourse createInstance(Type type) {
							return StudentCourse.CREATOR.createFromParcel(null);
						}
					});
			gsonBuilder.registerTypeAdapter(Park.class,
					new InstanceCreator<Park>() {
						@Override
						public Park createInstance(Type type) {
							return Park.CREATOR.createFromParcel(null);
						}
					});
			parser = gsonBuilder.create();
		}
		return parser;
	}
}
