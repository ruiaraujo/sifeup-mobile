package pt.up.beta.mobile.datatypes;

import java.util.ArrayList;
import java.util.List;

import pt.up.beta.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author Rui Araújo
 * 
 */
public class Subject implements Parcelable {
	/** Subject code - EIC0083 */
	@SerializedName("codigo")
	private final String code;

	/** Subject Portuguese name - Arquitectura e Organização de Computadores */
	@SerializedName("nome")
	private final String namePt;

	/** Subject English name - */
	@SerializedName("name")
	private final String nameEn;

	/** Subject acronym - */
	@SerializedName("sigla")
	private final String acronym;

	@SerializedName("unidade_nome")
	private final String unitName;

	@SerializedName("unidade_id")
	private final String unitCode;

	@SerializedName("pagina_web")
	private final String webPage;

	@SerializedName("pagina_elearning")
	private final String eLearningPage;

	@SerializedName("lingua")
	private final String language;

	@SerializedName("estado")
	private final String state;

	@SerializedName("ano_lectivo")
	private final String year;

	@SerializedName("conteudo")
	private final String content;

	@SerializedName("objectivos")
	private final String objectives;

	@SerializedName("metodologia")
	private final String metodology;

	@SerializedName("for_avaliacao")
	private final String evaluationFormula;

	@SerializedName("cond_frequencia")
	private final String frequenceCond;

	@SerializedName("observacoes")
	private final String observations;

	@SerializedName("forma_avaliacao")
	private final String evaluationProc;

	@SerializedName("forma_melhoria")
	private final String improvementProc;

	@SerializedName("provas_avaliacao")
	private final String evaluationExams;

	@SerializedName("software_desc")
	private final String softwareDesc;

	@SerializedName("comp_avaliacao_desc")
	private final String evalutionDesc;

	@SerializedName("bibliografia")
	private final Book[] bibliography;

	@SerializedName("carga_horaria")
	private final Workload[] workload;

	@SerializedName("comp_avaliacao")
	private final EvaluationComponent[] evaluation;

	@SerializedName("responsabilidades")
	private final Responsible[] responsibles;

	@SerializedName("ds")
	private final WorkloadDesc[] worloadDesc;

	@SerializedName("software")
	private final Software[] software;

	@SerializedName("keywords")
	private final Keyword[] keywords;

	@SerializedName("areas")
	private final Area[] areas;

	//added later
	private SubjectFiles files;

	private Subject(Parcel in) {
		code = ParcelUtils.readString(in);
		namePt = ParcelUtils.readString(in);
		nameEn = ParcelUtils.readString(in);
		acronym = ParcelUtils.readString(in);
		unitName = ParcelUtils.readString(in);
		unitCode = ParcelUtils.readString(in);
		webPage = ParcelUtils.readString(in);
		eLearningPage = ParcelUtils.readString(in);
		language = ParcelUtils.readString(in);
		state = ParcelUtils.readString(in);
		year = ParcelUtils.readString(in);
		content = ParcelUtils.readString(in);
		objectives = ParcelUtils.readString(in);
		metodology = ParcelUtils.readString(in);
		evaluationFormula = ParcelUtils.readString(in);
		frequenceCond = ParcelUtils.readString(in);
		observations = ParcelUtils.readString(in);
		evaluationProc = ParcelUtils.readString(in);
		improvementProc = ParcelUtils.readString(in);
		evaluationExams = ParcelUtils.readString(in);
		softwareDesc = ParcelUtils.readString(in);
		evalutionDesc = ParcelUtils.readString(in);
		bibliography = (Book[]) in.readParcelableArray(Book.class
				.getClassLoader());
		workload = (Workload[]) in.readParcelableArray(Workload.class
				.getClassLoader());
		evaluation = (EvaluationComponent[]) in
				.readParcelableArray(EvaluationComponent.class.getClassLoader());
		responsibles = (Responsible[]) in.readParcelableArray(Responsible.class
				.getClassLoader());
		worloadDesc = (WorkloadDesc[]) in.readParcelableArray(WorkloadDesc.class
				.getClassLoader());
		software = (Software[]) in.readParcelableArray(Software.class
				.getClassLoader());
		keywords = (Keyword[]) in.readParcelableArray(Keyword.class
				.getClassLoader());
		areas = (Area[]) in.readParcelableArray(Area.class
				.getClassLoader());
		files = in.readParcelable(SubjectFiles.class.getClassLoader());

	}

	public String getCode() {
		return code;
	}

	public String getNamePt() {
		return namePt;
	}

	public String getNameEn() {
		return nameEn;
	}

	public String getAcronym() {
		return acronym;
	}

	public String getUnitName() {
		return unitName;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public String getYear() {
		return year;
	}

	public Book[] getBibliography() {
		return bibliography;
	}

	public Workload[] getWorkload() {
		return workload;
	}

	public String getContent() {
		return content;
	}

	public String getObjectives() {
		return objectives;
	}

	public String getMetodology() {
		return metodology;
	}

	public EvaluationComponent[] getEvaluation() {
		return evaluation;
	}

	public String getEvaluationFormula() {
		return evaluationFormula;
	}

	public String getFrequenceCond() {
		return frequenceCond;
	}

	public String getObservations() {
		return observations;
	}

	public String getEvaluationProc() {
		return evaluationProc;
	}

	public String getImprovementProc() {
		return improvementProc;
	}

	public String getEvaluationExams() {
		return evaluationExams;
	}

	public Responsible[] getResponsibles() {
		return responsibles;
	}

	public WorkloadDesc[] getWorloadDesc() {
		return worloadDesc;
	}

	public Software[] getSoftware() {
		return software;
	}

	public SubjectFiles getFiles() {
		return files;
	}

	public void setFiles(SubjectFiles files) {
		this.files = files;
	}

	public static class Book implements Parcelable {
		@SerializedName("tipo")
		private final String type;

		@SerializedName("tipo_descr")
		private final String typeDescription;

		@SerializedName("autores")
		private final String authors;

		@SerializedName("titulo")
		private final String title;

		@SerializedName("link")
		private final String link;

		@SerializedName("isbn")
		private final String isbn;

		@SerializedName("editor")
		private final String editor;

		@SerializedName("obs")
		private final String observations;

		@SerializedName("ano")
		private final String year;

		private Book(Parcel in) {
			type = ParcelUtils.readString(in);
			typeDescription = ParcelUtils.readString(in);
			authors = ParcelUtils.readString(in);
			title = ParcelUtils.readString(in);
			link = ParcelUtils.readString(in);
			isbn = ParcelUtils.readString(in);
			editor = ParcelUtils.readString(in);
			observations = ParcelUtils.readString(in);
			year = ParcelUtils.readString(in);
		}
		

		public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
			public Book createFromParcel(Parcel in) {
				return new Book(in);
			}

			public Book[] newArray(int size) {
				return new Book[size];
			}
		};

		public String getType() {
			return type;
		}

		public String getEditor() {
			return editor;
		}

		public String getObservations() {
			return observations;
		}

		public String getYear() {
			return year;
		}

		public String getTypeDescription() {
			return typeDescription;
		}

		public String getAuthors() {
			return authors;
		}

		public String getTitle() {
			return title;
		}

		public String getLink() {
			return link;
		}

		public String getIsbn() {
			return isbn;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			ParcelUtils.writeString(dest, type);
			ParcelUtils.writeString(dest, typeDescription);
			ParcelUtils.writeString(dest, authors);
			ParcelUtils.writeString(dest, title);
			ParcelUtils.writeString(dest, link);
			ParcelUtils.writeString(dest, isbn);
			ParcelUtils.writeString(dest, editor);
			ParcelUtils.writeString(dest, observations);
			ParcelUtils.writeString(dest, year);
		}
	}

	public static class Workload implements Parcelable {

		@SerializedName("tipo")
		private final String type;

		@SerializedName("descricao")
		private final String description;

		@SerializedName("horas")
		private final String lenght;

		private Workload(Parcel in) {
			type = ParcelUtils.readString(in);
			description = ParcelUtils.readString(in);
			lenght = ParcelUtils.readString(in);
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			ParcelUtils.writeString(dest, type);
			ParcelUtils.writeString(dest, description);
			ParcelUtils.writeString(dest, lenght);
		}
		

		public static final Parcelable.Creator<Workload> CREATOR = new Parcelable.Creator<Workload>() {
			public Workload createFromParcel(Parcel in) {
				return new Workload(in);
			}

			public Workload[] newArray(int size) {
				return new Workload[size];
			}
		};
	}

	public static class EvaluationComponent implements Parcelable {
		@SerializedName("descricao")
		private final String description;

		@SerializedName("peso")
		private final String weight;

		@SerializedName("tipo")
		private final String type;

		@SerializedName("tipo_descr")
		private final String typeDesc;

		@SerializedName("duracao")
		private final String length;

		@SerializedName("data_conclusao")
		private final String conclusionDate;

		private EvaluationComponent(Parcel in) {
			description = ParcelUtils.readString(in);
			weight = ParcelUtils.readString(in);
			type = ParcelUtils.readString(in);
			typeDesc = ParcelUtils.readString(in);
			length = ParcelUtils.readString(in);
			conclusionDate = ParcelUtils.readString(in);
		}

		public String getDescription() {
			return description;
		}

		public String getDescriptionEn() {
			return weight;
		}

		public String getType() {
			return type;
		}

		public String getTypeDesc() {
			return typeDesc;
		}

		public String getLength() {
			return length;
		}

		public String getConclusionDate() {
			return conclusionDate;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			ParcelUtils.writeString(dest, description);
			ParcelUtils.writeString(dest, weight);
			ParcelUtils.writeString(dest, type);
			ParcelUtils.writeString(dest, typeDesc);
			ParcelUtils.writeString(dest, length);
			ParcelUtils.writeString(dest, conclusionDate);
		}

		public static final Parcelable.Creator<EvaluationComponent> CREATOR = new Parcelable.Creator<EvaluationComponent>() {
			public EvaluationComponent createFromParcel(Parcel in) {
				return new EvaluationComponent(in);
			}

			public EvaluationComponent[] newArray(int size) {
				return new EvaluationComponent[size];
			}
		};
	}

	public static class Responsible implements Parcelable {

		@SerializedName("codigo")
		private final String code;

		@SerializedName("nome")
		private final String name;

		@SerializedName("papel")
		private final String job;

		private Responsible(Parcel in) {
			code = ParcelUtils.readString(in);
			name = ParcelUtils.readString(in);
			job = ParcelUtils.readString(in);

		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			ParcelUtils.writeString(dest, code);
			ParcelUtils.writeString(dest, name);
			ParcelUtils.writeString(dest, job);

		}

		public static final Parcelable.Creator<Responsible> CREATOR = new Parcelable.Creator<Responsible>() {
			public Responsible createFromParcel(Parcel in) {
				return new Responsible(in);
			}

			public Responsible[] newArray(int size) {
				return new Responsible[size];
			}
		};
	}

	public static class WorkloadDesc implements Parcelable {
		@SerializedName("tipo")
		private final String type;

		@SerializedName("tipo_descricao")
		private final String typeDesc;

		@SerializedName("num_turmas")
		private final String numClasses;

		@SerializedName("num_horas")
		private final String numHours;

		@SerializedName("docentes")
		private final Teacher[] teachers;

		private WorkloadDesc(Parcel in) {
			type = ParcelUtils.readString(in);
			typeDesc = ParcelUtils.readString(in);
			numClasses = ParcelUtils.readString(in);
			numHours = ParcelUtils.readString(in);
			teachers = (Teacher[]) in.readParcelableArray(Teacher.class
					.getClassLoader());
		}

		public Teacher[] getTeachers() {
			return teachers;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {

			ParcelUtils.writeString(dest, type);
			ParcelUtils.writeString(dest, typeDesc);
			ParcelUtils.writeString(dest, numClasses);
			ParcelUtils.writeString(dest, numHours);
			dest.writeParcelableArray(teachers, flags);
		}

		public static final Parcelable.Creator<WorkloadDesc> CREATOR = new Parcelable.Creator<WorkloadDesc>() {
			public WorkloadDesc createFromParcel(Parcel in) {
				return new WorkloadDesc(in);
			}

			public WorkloadDesc[] newArray(int size) {
				return new WorkloadDesc[size];
			}
		};
	}

	public static class Teacher implements Parcelable {

		@SerializedName("doc_codigo")
		private final String code;

		@SerializedName("nome")
		private final String name;

		@SerializedName("horas")
		private final String time;

		private Teacher(Parcel in) {
			code = ParcelUtils.readString(in);
			name = ParcelUtils.readString(in);
			time = ParcelUtils.readString(in);
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public String getTime() {
			return time;
		}

		public boolean equals(Object t) {
			if (t instanceof Teacher) {
				return ((Teacher) t).code.equals(code);
			}
			return false;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			ParcelUtils.writeString(dest, code);
			ParcelUtils.writeString(dest, name);
			ParcelUtils.writeString(dest, time);
		}
		public static final Parcelable.Creator<Teacher> CREATOR = new Parcelable.Creator<Teacher>() {
			public Teacher createFromParcel(Parcel in) {
				return new Teacher(in);
			}

			public Teacher[] newArray(int size) {
				return new Teacher[size];
			}
		};
	}

	public static class Software implements Parcelable {

		@SerializedName("descricao")
		private final String description;

		@SerializedName("nome")
		private final String name;

		private Software(Parcel in) {
			description = ParcelUtils.readString(in);
			name = ParcelUtils.readString(in);
		}

		public String getDescription() {
			return description;
		}

		public String getName() {
			return name;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			ParcelUtils.writeString(dest, description);
			ParcelUtils.writeString(dest, name);
		}

		public static final Parcelable.Creator<Software> CREATOR = new Parcelable.Creator<Software>() {
			public Software createFromParcel(Parcel in) {
				return new Software(in);
			}

			public Software[] newArray(int size) {
				return new Software[size];
			}
		};
	}

	public static class Area implements Parcelable {

		@SerializedName("name")
		private final String nameEn;

		@SerializedName("nome")
		private final String name;

		private Area(Parcel in) {
			nameEn = ParcelUtils.readString(in);
			name = ParcelUtils.readString(in);
		}

		public String getNameEn() {
			return nameEn;
		}

		public String getName() {
			return name;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			ParcelUtils.writeString(dest, nameEn);
			ParcelUtils.writeString(dest, name);
		}

		public static final Parcelable.Creator<Area> CREATOR = new Parcelable.Creator<Area>() {
			public Area createFromParcel(Parcel in) {
				return new Area(in);
			}

			public Area[] newArray(int size) {
				return new Area[size];
			}
		};
	}
	


	public static class Keyword implements Parcelable {

		@SerializedName("descricao")
		private final String description;

		private Keyword(Parcel in) {
			description = ParcelUtils.readString(in);
		}

		public String getDescription() {
			return description;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			ParcelUtils.writeString(dest, description);
		}

		public static final Parcelable.Creator<Keyword> CREATOR = new Parcelable.Creator<Keyword>() {
			public Keyword createFromParcel(Parcel in) {
				return new Keyword(in);
			}

			public Keyword[] newArray(int size) {
				return new Keyword[size];
			}
		};
	}

	public Teacher[] getTeachers() {
		List<Teacher> res = new ArrayList<Teacher>();
		for (WorkloadDesc wd : this.worloadDesc) {
			for (Teacher t : wd.teachers)
				if (!res.contains(t))
					res.add(t);
		}

		return res.toArray(new Teacher[0]);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, code);
		ParcelUtils.writeString(dest, namePt);
		ParcelUtils.writeString(dest, nameEn);
		ParcelUtils.writeString(dest, acronym);
		ParcelUtils.writeString(dest, unitName);
		ParcelUtils.writeString(dest, unitCode);
		ParcelUtils.writeString(dest, webPage);
		ParcelUtils.writeString(dest, eLearningPage);
		ParcelUtils.writeString(dest, language);
		ParcelUtils.writeString(dest, state);
		ParcelUtils.writeString(dest, year);
		ParcelUtils.writeString(dest, content);
		ParcelUtils.writeString(dest, objectives);
		ParcelUtils.writeString(dest, metodology);
		ParcelUtils.writeString(dest, evaluationFormula);
		ParcelUtils.writeString(dest, frequenceCond);
		ParcelUtils.writeString(dest, observations);
		ParcelUtils.writeString(dest, evaluationProc);
		ParcelUtils.writeString(dest, improvementProc);
		ParcelUtils.writeString(dest, evaluationExams);
		ParcelUtils.writeString(dest, softwareDesc);
		ParcelUtils.writeString(dest, evalutionDesc);
		dest.writeParcelableArray(bibliography, flags);
		dest.writeParcelableArray(workload, flags);
		dest.writeParcelableArray(evaluation, flags);
		dest.writeParcelableArray(responsibles, flags);
		dest.writeParcelableArray(worloadDesc, flags);
		dest.writeParcelableArray(software, flags);
		dest.writeParcelableArray(keywords, flags);
		dest.writeParcelableArray(areas, flags);
		dest.writeParcelable(files, flags);
	}

	public static final Parcelable.Creator<Subject> CREATOR = new Parcelable.Creator<Subject>() {
		public Subject createFromParcel(Parcel in) {
			return new Subject(in);
		}

		public Subject[] newArray(int size) {
			return new Subject[size];
		}
	};
}
