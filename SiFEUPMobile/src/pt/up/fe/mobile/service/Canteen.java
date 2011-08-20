package pt.up.fe.mobile.service;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Class this. Save the name and menus of the this.
 *
 * @author Ângela Igreja
 *
 */

public class Canteen {

	private int code;
	private String description;
	private String timetable;
	private Menu[] menus;
	/**
  	 * Class Menu. Save the information of menu.
     *
  	 * @author Ângela Igreja
  	 *
  	 */
  	public class Menu implements Serializable
  	{
  		private String state;
  		private String date;
  		private Dish[] dishes;  		
  	}
  	
  	/**
  	 * Class Dish. Save the information of dish.
     *
  	 * @author Ângela Igreja
  	 *
  	 */
  	public class Dish implements Serializable
  	{
  		private String state;
  		private String description;
  		private int type;
  		private String descriptionType;
		public void setDescription(String description) {
			this.description = description;
		}
		public String getDescription() {
			return description;
		}
		public void setDescriptionType(String descriptionType) {
			this.descriptionType = descriptionType;
		}
		public String getDescriptionType() {
			return descriptionType;
		}
  		
  		
  	}

	public void setMenus(Menu[] menus) {
		this.menus = menus;
	}

	public Menu[] getMenus() {
		return menus;
	}

	public void parseJson(JSONObject jBlock) throws JSONException {
		if(jBlock.has("codigo")) this.code = jBlock.getInt("codigo"); 
			
			if(jBlock.has("descricao")) this.setDescription(jBlock.getString("descricao"));
			
			if(jBlock.has("horario")) this.timetable = jBlock.getString("horario");
			
			if(jBlock.has("ementas"))
			{
				JSONArray jArrayMenus = jBlock.getJSONArray("ementas");
				this.menus = new Menu[jArrayMenus.length()];
				for(int j = 0; j < jArrayMenus.length(); j++)
	     		{
					JSONObject jMenu = jArrayMenus.getJSONObject(j);
					
					Menu menu = new Menu();
					
					if(jMenu.has("estado")) menu.state = jMenu.getString("estado"); 
					
					if(jMenu.has("data")) menu.date = jMenu.getString("data"); 
					
					if(jMenu.has("pratos"))
					{
						JSONArray jArrayDishs = jMenu.getJSONArray("pratos");
						menu.dishes = new Dish[jArrayDishs.length()];
						for(int k = 0; k < jArrayDishs.length(); k++)
	     	     		{
	     					JSONObject jDish = jArrayDishs.getJSONObject(k);
	     					
	     					Dish dish = new Dish();
	     					
	     					if(jDish.has("estado")) dish.state = jDish.getString("estado"); 
	     					
	     					if(jDish.has("descricao")) dish.setDescription(jDish.getString("descricao"));
	     					
	     					if(jDish.has("tipo")) dish.type = jDish.getInt("tipo"); 
	     					
	     					if(jDish.has("tipo_descr")) dish.setDescriptionType(jDish.getString("tipo_descr")); 
	     					menu.dishes[k] = dish;
	     	     		}
						
					}
					this.menus[j] = menu;
	     		}
				
			}		
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public String getDate(int groupPosition) {
		return menus[groupPosition].date;
	}

	public int getMenuCount() {
		return menus.length;
	}

	public Dish getDish(int groupPosition, int childPosition) {
		return menus[groupPosition].dishes[childPosition];
	}

	public int getDishesCount(int groupPosition) {
		return menus[groupPosition].dishes.length;
	}
  	
}
