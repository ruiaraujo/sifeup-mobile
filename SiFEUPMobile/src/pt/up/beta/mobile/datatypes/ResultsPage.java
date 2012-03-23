package pt.up.beta.mobile.datatypes;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Holds a Search page
 * With pageResults number
 * of students
 *
 */
public class ResultsPage{
	private int searchSize; // "total" : 583
	private int page; // "primeiro" : 1
	private int pageResults; // "tam_pagina" : 15
	private List<Student> students = new ArrayList<Student>();
	public int getSearchSize() {
		return searchSize;
	}
	public void setSearchSize(int searchSize) {
		this.searchSize = searchSize;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getPageResults() {
		return pageResults;
	}
	public void setPageResults(int pageResults) {
		this.pageResults = pageResults;
	}
	public List<Student> getStudents() {
		return students;
	}
	public void setStudents(List<Student> students) {
		this.students = students;
	}
}
