package com.cognus.imagenotetacker;

import java.io.File;
import java.util.Date;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.cognus.imagegallery.data.provider.Gallery;
import com.cognus.imagegallery.data.provider.RA_Content;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class AddGalleryFragment extends DialogFragment {

	public AddGalleryFragment() {
		// Required empty public constructor
	}

	View main;
	EditText gallery_name;
	Button addGallery;

	/**
	 * This Method Update MainAcitivity Update.
	 * 
	 * @author user
	 * 
	 */
	public interface addGalleryInter {

		public void addGallery();
	}

	private addGalleryInter addgallery;
	private Gallery gallery;

	public static AddGalleryFragment getInstance(addGalleryInter addGalleryInter) {

		AddGalleryFragment addGalleryFragment = new AddGalleryFragment();
		addGalleryFragment.setAddgallery(addGalleryInter);
		return addGalleryFragment;
	}

	public static AddGalleryFragment getInstance(Gallery gallery,
			addGalleryInter addGalleryInter) {

		AddGalleryFragment addGalleryFragment = new AddGalleryFragment();
		addGalleryFragment.setAddgallery(addGalleryInter);

		addGalleryFragment.setGallery(gallery);
		return addGalleryFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		main = inflater
				.inflate(R.layout.fragment_add_gallery, container, false);
		gallery_name = (EditText) main.findViewById(R.id.editText1);
		addGallery = (Button) main.findViewById(R.id.addGallery);

		if (gallery != null) {
			gallery_name.setText(gallery.getName());
			gallery_name.selectAll();
			addGallery.setText("Update");
		}
		addGallery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ContentValues values = new ContentValues();
				if (gallery_name.getText().toString().length() > 0) {
					values.put(RA_Content.gallery.Columns.NAME.getName(),
							gallery_name.getText().toString());
					values.put(RA_Content.gallery.Columns.CREATED_ON.getName(),
							new Date().toString());
					values.put(RA_Content.gallery.Columns.CREATED_BY.getName(),
							0 + "");
					try {
						if (gallery == null) {

						
							Uri insert = getActivity().getContentResolver()
									.insert(RA_Content.gallery.CONTENT_URI,
											values);
							
							int id = Integer.valueOf(insert.getLastPathSegment());
							String pathToExternalStorage = Environment
									.getExternalStorageDirectory().toString();
							File appDirectory = new File(pathToExternalStorage
									+ "/"
									+ getString(R.string.app_name)
									+ "/"
									+ id);
							// have the object build the directory structure, if
							// needed.
							appDirectory.mkdirs();
						} else {
							getActivity().getContentResolver().update(
									RA_Content.gallery.CONTENT_URI, values,
									"_id=?",
									new String[] { gallery.get_id() + "" });
						}

						if (addgallery != null) {

							addgallery.addGallery();
						}

						getDialog().cancel();
					} catch (SQLiteConstraintException exception) {

						gallery_name.setError("Name "
								+ gallery_name.getText().toString()
								+ " name already in use ");
						gallery_name.requestFocus();

						gallery_name.selectAll();
					}
				} else {

					gallery_name.setError("please enter some name");

					gallery_name.startAnimation(AnimationUtils.loadAnimation(
							getActivity(), android.R.anim.slide_in_left
									| android.R.anim.slide_out_right));
					gallery_name.requestFocus();

				}
			}

		});

		return main;
	}

	public addGalleryInter getAddgallery() {
		return addgallery;
	}

	public void setAddgallery(addGalleryInter addgallery) {
		this.addgallery = addgallery;
	}

	@Override
	public Dialog getDialog() {
		// TODO Auto-generated method stub
		return super.getDialog();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity());
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		Drawable drawable = getResources()
				.getDrawable(R.drawable.no_image_back);

		dialog.getWindow().setBackgroundDrawable(drawable);
		dialog.show();

		return dialog;
	}

	public Gallery getGallery() {
		return gallery;
	}

	public void setGallery(Gallery gallery) {
		this.gallery = gallery;
	}

}
