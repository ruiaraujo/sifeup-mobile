package pt.up.beta.mobile.datatypes;

import java.lang.reflect.Array;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * Holds a Search page With pageResults number of students
 * 
 */
public class ResultsPage<T extends Parcelable> implements Parcelable {
	@SerializedName("total")
	private final int searchSize; // "total" : 583
	@SerializedName("pagina")
	private final int page; // "primeiro" : 1
	@SerializedName("tam_pagina")
	private final int pageResults; // "tam_pagina" : 15
	@SerializedName("resultados")
	private final T[] results;

	@SuppressWarnings("unchecked")
	private ResultsPage(Parcel in) {
		searchSize = in.readInt();
		page = in.readInt();
		pageResults = in.readInt();
		Class<T> c = null;
		Parcelable.Creator<T> creator = null;
		try {
			c = (Class<T>) Class.forName(in.readString());
			creator = (Parcelable.Creator<T>) c.getDeclaredField("CREATOR")
					.get(null);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		if (c != null && creator != null) {
			results = (T[]) Array.newInstance(c, in.readInt());
			in.readTypedArray(results, creator);
		} else
			results = null;

	}

	public int getSearchSize() {
		return searchSize;
	}

	public int getPage() {
		return page;
	}

	public int getPageResults() {
		return pageResults;
	}

	public T[] getResults() {
		return results;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(searchSize);
		dest.writeInt(page);
		dest.writeInt(pageResults);
		dest.writeString(results.getClass().getName());
		dest.writeInt(results.length);
		dest.writeParcelableArray(results, flags);
	}
}
