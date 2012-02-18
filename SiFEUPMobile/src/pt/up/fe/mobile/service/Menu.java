package pt.up.fe.mobile.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class Menu. Save the information of menu.
 * 
 * @author Ângela Igreja
 * 
 */
public class Menu implements Parcelable {
    private String state;
    private String date;
    private Dish[] dishes;

    public Menu() {
    }
    
    
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public Dish[] getDishes() {
        return dishes;
    }
    public void setDishes(Dish[] dishes) {
        this.dishes = dishes;
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(state);
        dest.writeString(date);
        dest.writeValue(dishes);
    }
    
    public static final Parcelable.Creator<Menu> CREATOR = new Parcelable.Creator<Menu>() {
        public Menu createFromParcel(Parcel in) {
            return new Menu(in);
        }

        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };

    private Menu(Parcel in) {
        state = in.readString();
        date = in.readString();
        dishes = (Dish[]) in.readValue(Dish.class.getClassLoader());
    }
}