package pt.up.beta.mobile.datatypes;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import pt.up.beta.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * @author Ângela Igreja
 * 
 */
public class SubjectFiles implements Parcelable {

	public SubjectFiles() {
		current = root = new Folder(0);
	}

	/** Root Folder */
	private final Folder root;

	/** Current Folder */
	private Folder current;

	public void setCurrentFolder(Folder current) {
		if (current == null)
			return;
		this.current = current;
	}

	public Folder getCurrentFolder() {
		return current;
	}

	/** Class Folder */
	public static class Folder implements Parcelable {
		@SerializedName("codigo")
		private final int code;

		@SerializedName("nome")
		private final String name;

		@SerializedName("name")
		private final String nameEn;

		@SerializedName("nivel")
		private final int level;

		@SerializedName("ficheiros")
		private final File[] files;

		/** List of folders */
		private List<Folder> folders = new ArrayList<Folder>();

		/** Parent Folder */
		private Folder parent;

		private Folder(int level) {
			this.level = level;
			name = null;
			nameEn = null;
			files = new File[0];
			code = 0;
		}

		private Folder(Parcel in) {
			code = in.readInt();
			name = ParcelUtils.readString(in);
			nameEn = ParcelUtils.readString(in);
			level = in.readInt();
			files = (File[]) in
					.readParcelableArray(File.class.getClassLoader());
			in.readTypedList(folders, CREATOR);
			parent = in.readParcelable(Folder.class.getClassLoader());
		}

		public int getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public String getNameEn() {
			return nameEn;
		}

		public int getLevel() {
			return level;
		}

		public File[] getFiles() {
			return files;
		}

		public void setParent(Folder parent) {
			this.parent = parent;
		}

		public Folder getParent() {
			return parent;
		}

		public List<Folder> getFolders() {
			return folders;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(code);
			ParcelUtils.writeString(dest, name);
			ParcelUtils.writeString(dest, nameEn);
			dest.writeInt(level);
			dest.writeParcelableArray(files, flags);
			dest.writeTypedList(folders);
			dest.writeParcelable(parent, flags);
		}

		public static final Parcelable.Creator<Folder> CREATOR = new Parcelable.Creator<Folder>() {
			public Folder createFromParcel(Parcel in) {
				return new Folder(in);
			}

			public Folder[] newArray(int size) {
				return new Folder[size];
			}
		};
	}

	/** Class File */
	public static class File implements Parcelable {
		@SerializedName("codigo")
		private final int code;
		
		@SerializedName("nome")
		private final String name;

		@SerializedName("tipo")
		private final String type;

		@SerializedName("url")
		private final String url;

		@SerializedName("filename")
		private final String filename;

		@SerializedName("tamanho")
		private final long size;

		@SerializedName("data_actualizacao")
		private final String updateDate;

		@SerializedName("comentario")
		private final String comment;

		@SerializedName("descricao")
		private final String description;

		private File(Parcel in) {
			name = ParcelUtils.readString(in);
			type = ParcelUtils.readString(in);
			url = ParcelUtils.readString(in);
			filename = ParcelUtils.readString(in);
			updateDate = ParcelUtils.readString(in);
			comment = ParcelUtils.readString(in);
			description = ParcelUtils.readString(in);
			code = in.readInt();
			size = in.readLong();
		}

		public int getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public String getType() {
			return type;
		}

		public String getUrl() {
			return url;
		}

		public String getFilename() {
			return filename;
		}

		public long getSize() {
			return size;
		}

		public String getUpdateDate() {
			return updateDate;
		}

		public String getComment() {
			return comment;
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
			ParcelUtils.writeString(dest, name);
			ParcelUtils.writeString(dest, type);
			ParcelUtils.writeString(dest, url);
			ParcelUtils.writeString(dest, filename);
			ParcelUtils.writeString(dest, updateDate);
			ParcelUtils.writeString(dest, comment);
			ParcelUtils.writeString(dest, description);
			dest.writeInt(code);
			dest.writeLong(size);
		}

		public static final Parcelable.Creator<File> CREATOR = new Parcelable.Creator<File>() {
			public File createFromParcel(Parcel in) {
				return new File(in);
			}

			public File[] newArray(int size) {
				return new File[size];
			}
		};

	}

	/**
	 * Subject Content Parser Returns true in case of correct parsing.
	 * 
	 * @param page
	 * @return Subject
	 * @throws JSONException
	 */
	public static SubjectFiles JSONSubjectContent(String page) {
		final SubjectFiles subjectFiles = new SubjectFiles();
		final Gson gson = new Gson();
		Folder[] folders = gson.fromJson(page, Folder[].class);
		for (Folder folder : folders) {
			folder.parent = subjectFiles.root;
			folder.folders = new ArrayList<SubjectFiles.Folder>();
			if (subjectFiles.root.folders.isEmpty()) {
				subjectFiles.root.folders.add(folder);
				continue; // first folder
			}
			Folder lastFolder = subjectFiles.root.folders
					.get(subjectFiles.root.folders.size() - 1);
			if (lastFolder.level == folder.level)
				subjectFiles.root.folders.add(folder); // first level
			else {
				while (folder.level != lastFolder.level + 1) { // if it is
																// empty, we
																// cannot go
																// any
																// further
																// so we
																// stop here
					// otherwise you continue until the level difference is
					// one.
					if (!lastFolder.folders.isEmpty())
						lastFolder = lastFolder.folders.get(lastFolder.folders
								.size() - 1);
					else
						break;
				}
				folder.parent = lastFolder;
				lastFolder.folders.add(folder);
			}

		}
		return subjectFiles;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(root, flags);
		dest.writeParcelable(current, flags);
	}

	private SubjectFiles(Parcel in) {
		root = in.readParcelable(Folder.class.getClassLoader());
		current = in.readParcelable(Folder.class.getClassLoader());
	}

	public static final Parcelable.Creator<SubjectFiles> CREATOR = new Parcelable.Creator<SubjectFiles>() {
		public SubjectFiles createFromParcel(Parcel in) {
			return new SubjectFiles(in);
		}

		public SubjectFiles[] newArray(int size) {
			return new SubjectFiles[size];
		}
	};
}
