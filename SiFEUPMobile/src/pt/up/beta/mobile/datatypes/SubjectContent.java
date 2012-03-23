package pt.up.beta.mobile.datatypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.beta.mobile.sifeup.SifeupUtils;

/**
 * @author Ângela Igreja
 * 
 */
@SuppressWarnings("serial")
public class SubjectContent implements Serializable {

	public SubjectContent() {
		root = new Folder();
		root.level = 0;
		current = root;
	}

	/** Current Folder */
	private Folder root;

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
	public class Folder implements Serializable {
		/** */
		private int code;

		/** */
		private String name;

		/** */
		private int level;

		/** */
		private List<File> files = new ArrayList<File>();

		/** List of folders */
		private List<Folder> folders = new ArrayList<Folder>();

		/** Parent Folder */
		private Folder parent;

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public List<File> getFiles() {
			return files;
		}

		public void setFiles(List<File> files) {
			this.files = files;
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
	}

	/** Class File */
	public class File implements Serializable {

		/** */
		private int code;

		/** */
		private String name;

		/** */
		private String type;

		/** */
		private String url;

		/** */
		private String filename;

		/** */
		private long size;

		/** */
		private String updateDate;

		/** */
		private String comment;

		/** */
		private String description;

		public void setCode(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUrl() {
			return url;
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}

		public String getFilename() {
			return filename;
		}

		public void setSize(long size) {
			this.size = size;
		}

		public long getSize() {
			return size;
		}

		public void setUpdateDate(String updateDate) {
			this.updateDate = updateDate;
		}

		public String getUpdateDate() {
			return updateDate;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String getComment() {
			return comment;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

	}

	/**
	 * Subject Content Parser Returns true in case of correct parsing.
	 * 
	 * @param page
	 * @return Subject
	 * @throws JSONException 
	 */
	public SubjectContent JSONSubjectContent(String page) throws JSONException {
		JSONObject jObject = new JSONObject(page);

		if (jObject.has("pastas")) {
			JSONArray jFolders = jObject.getJSONArray("pastas");
			for (int i = 0; i < jFolders.length(); i++) {
				Folder folder = new Folder();
				JSONObject jFolder = jFolders.getJSONObject(i);
				if (jFolder.has("codigo"))
					folder.code = jFolder.getInt("codigo");
				if (jFolder.has("nome"))
					folder.name = jFolder.getString("nome");
				if (jFolder.has("nivel"))
					folder.level = jFolder.getInt("nivel");

				if (jFolder.has("ficheiros")) {
					JSONArray jFiles = jFolder.getJSONArray("ficheiros");

					for (int j = 0; j < jFiles.length(); j++) {
						File file = new File();
						JSONObject jFile = jFiles.getJSONObject(j);
						SifeupUtils.removeEmptyKeys(jFile);
						if (jFile.has("codigo"))
							file.setCode(jFile.getInt("codigo"));
						if (jFile.has("nome"))
							file.setName(jFile.getString("nome"));
						if (jFile.has("tipo"))
							file.setType(jFile.getString("tipo"));
						if (jFile.has("url"))
							file.setUrl(jFile.getString("url"));
						if (jFile.has("filename"))
							file.setFilename(jFile.getString("filename"));
						if (jFile.has("tamanho"))
							file.setSize(jFile.getLong("tamanho"));
						if (jFile.has("data_actualizacao"))
							file.setUpdateDate(jFile
									.getString("data_actualizacao"));
						if (jFile.has("comentario"))
							file.setComment(jFile.getString("comentario"));
						if (jFile.has("descricao"))
							file.setDescription(jFile.getString("descricao"));

						folder.files.add(file);
					}
				}
				folder.parent = root;
				if (root.folders.isEmpty()) {
					root.folders.add(folder);
					continue; // first folder
				}
				Folder lastFolder = root.folders.get(root.folders.size() - 1);
				if (lastFolder.level == folder.level)
					root.folders.add(folder); // first level
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
							lastFolder = lastFolder.folders
									.get(lastFolder.folders.size() - 1);
						else
							break;
					}
					folder.parent = lastFolder;
					lastFolder.folders.add(folder);
				}

			}
		}

		return this;
	}

}
