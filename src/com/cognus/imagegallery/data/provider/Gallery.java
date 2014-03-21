package com.cognus.imagegallery.data.provider;

public class Gallery {
private int _id;
private String name;
private String created_on;
private int created_by;


public int get_id() {
	return _id;
}
public void set_id(int _id) {
	this._id = _id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getCreated_on() {
	return created_on;
}
public void setCreated_on(String created_on) {
	this.created_on = created_on;
}
public int getCreated_by() {
	return created_by;
}
public void setCreated_by(int created_by) {
	this.created_by = created_by;
}




}
