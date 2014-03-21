package com.cognus.imagegallery.data;

public class Image {
	private int _id;
	private int gallery_id;
	private byte[] image;
	private String image_path_local;
	private String image_path_remote;
	private String created_on;
	private String created_by;
	private String info;

	public Image() {
		// TODO Auto-generated constructor stub
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getGallery_id() {
		return gallery_id;
	}

	public void setGallery_id(int gallery_id) {
		this.gallery_id = gallery_id;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getImage_path_local() {
		return image_path_local;
	}

	public void setImage_path_local(String image_path_local) {
		this.image_path_local = image_path_local;
	}

	public String getImage_path_remote() {
		return image_path_remote;
	}

	public void setImage_path_remote(String image_path_remote) {
		this.image_path_remote = image_path_remote;
	}

	public String getCreated_on() {
		return created_on;
	}

	public void setCreated_on(String created_on) {
		this.created_on = created_on;
	}

	public String getCreated_by() {
		return created_by;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}
