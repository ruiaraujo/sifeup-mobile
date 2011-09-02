package pt.up.fe.mobile.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.service.Subject.EvaluationComponent;
import pt.up.fe.mobile.service.Subject.Software;
       
import android.util.Log;

/**
 * @author Ângela Igreja
 *
 */
@SuppressWarnings("serial")
public class SubjectContent  implements Serializable {

	/** List of folders */
	private List<Folder> folders = new ArrayList<Folder>();
	
	public List<Folder> getFolders() {
		return folders;
	}

	public void setFolders(List<Folder> folders) {
		this.folders = folders;
	}

	/** Class Folder*/
	public class Folder implements Serializable{
		/** */
		private int code;

		/** */
		private String name;
		
		/** */
		private String level;
		
		/** */
		private List<File> files = new ArrayList<File>();
		
		
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

		public String getLevel() {
			return level;
		}

		public void setLevel(String level) {
			this.level = level;
		}

		public List<File> getFiles() {
			return files;
		}

		public void setFiles(List<File> files) {
			this.files = files;
		}
	}
	

	/** Class File*/
	private class File implements Serializable{
		
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
		private int size;
		
		/** */
		private String updateDate;
		
		/** */
		private String comment;
		
		/** */
		private String description;

	}

	/** 
	 * Subject Content Parser
	 * Returns true in case of correct parsing.
	 * 
	 * @param page
	 * @return Subject
	 */
    public boolean JSONSubjectContent(String page){
    	JSONObject jObject;
		try {
			jObject = new JSONObject(page);
			
			if(jObject.has("pastas"))
			{
	    		JSONArray jFolders = jObject.getJSONArray("pastas");
	    		for(int i = 0; i < jFolders.length(); i++)
	    		{
	    			Folder folder = new Folder();
	    			JSONObject jFolder = jFolders.getJSONObject(i);
	    			if(jFolder.has("codigo")) folder.code = jFolder.getInt("codigo");
	    			if(jFolder.has("nome")) folder.name = jFolder.getString("nome");
	    			if(jFolder.has("nivel")) folder.level = jFolder.getString("nivel");
	    			
	    			if ( jFolder.has("ficheiros") )
	    			{
	    				JSONArray jFiles = jFolder.getJSONArray("ficheiros");
	    				
	    				for(int j = 0; j < jFiles.length(); j++)
	    				{
	    					File file = new File();
	    					JSONObject jFile = jFiles.getJSONObject(j);
	    					if ( jFile.has("codigo") ) file.code = jFile.getInt("codigo");
	    					if ( jFile.has("nome") ) file.name = jFile.getString("nome");
	    					if ( jFile.has("tipo") ) file.type = jFile.getString("tipo");
	    					if ( jFile.has("url") ) file.url = jFile.getString("url");
	    					if ( jFile.has("filename") ) file.filename = jFile.getString("filename");
	    					if ( jFile.has("tamanho") )file.size = jFile.getInt("tamanho");
	    					if ( jFile.has("data_actualizacao") ) file.updateDate = jFile.getString("data_actualizacao");
	    					if ( jFile.has("comentario") ) file.comment = jFile.getString("comentario");
	    					if ( jFile.has("descricao") ) file.description = jFile.getString("descricao");
	    			
	    					folder.files.add(file);
	    				}
	    			}
	    			
	    			this.folders.add(folder);
	    		}
			}
			
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	Log.e("JSON", "subject content not found");
    	return false;
    }
    
}
