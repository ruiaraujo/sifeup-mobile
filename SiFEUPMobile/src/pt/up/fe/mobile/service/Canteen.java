package pt.up.fe.mobile.service;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class this. Save the name and menus of the this.
 * 
 * @author Ângela Igreja
 * 
 */

public class Canteen implements Parcelable {

    private int code;
    private String description;
    private String timetable;
    private Menu[] menus;
    

    public Canteen() {
    }


    public void setMenus(Menu[] menus) {
        this.menus = menus;
    }

    public Menu[] getMenus() {
        return menus;
    }

    public void parseJson(JSONObject jBlock) throws JSONException {
        if (jBlock.has("codigo"))
            this.code = jBlock.getInt("codigo");

        if (jBlock.has("descricao"))
            this.setDescription(jBlock.getString("descricao"));

        if (jBlock.has("horario"))
            this.timetable = jBlock.getString("horario");

        if (jBlock.has("ementas")) {
            JSONArray jArrayMenus = jBlock.getJSONArray("ementas");
            this.menus = new Menu[jArrayMenus.length()];
            for (int j = 0; j < jArrayMenus.length(); j++) {
                JSONObject jMenu = jArrayMenus.getJSONObject(j);

                Menu menu = new Menu();

                if (jMenu.has("estado"))
                    menu.setState(jMenu.getString("estado"));

                if (jMenu.has("data"))
                    menu.setDate(jMenu.getString("data"));

                if (jMenu.has("pratos")) {
                    JSONArray jArrayDishs = jMenu.getJSONArray("pratos");
                    final Dish[] dishes = new Dish[jArrayDishs.length()];
                    for (int k = 0; k < jArrayDishs.length(); k++) {
                        JSONObject jDish = jArrayDishs.getJSONObject(k);

                        Dish dish = new Dish();

                        if (jDish.has("estado"))
                            dish.setState(jDish.getString("estado"));

                        if (jDish.has("descricao"))
                            dish.setDescription(jDish.getString("descricao"));

                        if (jDish.has("tipo"))
                            dish.setType(jDish.getInt("tipo"));

                        if (jDish.has("tipo_descr"))
                            dish.setDescriptionType(jDish
                                    .getString("tipo_descr"));
                        dishes[k] = dish;
                    }
                    menu.setDishes(dishes);

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
        return menus[groupPosition].getDate();
    }

    public int getMenuCount() {
        return menus.length;
    }

    public Dish getDish(int groupPosition, int childPosition) {
        return menus[groupPosition].getDishes()[childPosition];
    }

    public int getDishesCount(int groupPosition) {
        return menus[groupPosition].getDishes().length;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(code);
        out.writeString(description);
        out.writeString(timetable);
        out.writeValue(menus);
    }

    public static final Parcelable.Creator<Canteen> CREATOR = new Parcelable.Creator<Canteen>() {
        public Canteen createFromParcel(Parcel in) {
            return new Canteen(in);
        }

        public Canteen[] newArray(int size) {
            return new Canteen[size];
        }
    };

    private Canteen(Parcel in) {
        code = in.readInt();
        description = in.readString();
        timetable = in.readString();
        menus = (Menu[]) in.readValue(Menu.class.getClassLoader());
    }

}
