package com.cognus.imagenotetacker;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cognus.imagegallery.data.provider.RA_Content;

public class NewNoteTacker extends DialogFragment {
	// TODO: Rename parameter arguments, choose names that match
	private static final String ARG_PARAM1 = "gallery_id";
	private static final String ARG_PARAM2 = "image_id";

	// TODO: Rename and change types of parameters
	private int gallery_id;
	private int image_id;
	EditText editor = null;
	Button add_in_memo;

	public static NewNoteTacker newInstance(int gallery_id, int image_id) {
		NewNoteTacker fragment = new NewNoteTacker();
		Bundle args = new Bundle();
		args.putInt(ARG_PARAM1, gallery_id);
		args.putInt(ARG_PARAM2, image_id);
		fragment.setArguments(args);
		return fragment;
	}

	public NewNoteTacker() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			gallery_id = getArguments().getInt(ARG_PARAM1);
			image_id = getArguments().getInt(ARG_PARAM2);
		}
	}

	View main;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		main = inflater.inflate(R.layout.fragment_new_note_tacker, container,
				false);

		editor = (EditText) main.findViewById(R.id.editor);

		Cursor query = getActivity().getContentResolver().query(
				RA_Content.image.CONTENT_URI, RA_Content.image.PROJECTION,
				"_id=?", new String[] { image_id + "" }, "");
		if (query.moveToFirst()) {
			editor.setText(query.getString(query
					.getColumnIndex(RA_Content.image.Columns.INFO.getName())));
		}

		add_in_memo = (Button) main.findViewById(R.id.store_this);

		add_in_memo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Toast.makeText(getActivity(), "Comment Saved",
						Toast.LENGTH_LONG).show();
				ContentValues values = new ContentValues();
				values.put(RA_Content.image.Columns.INFO.getName(), editor
						.getText().toString().trim());
			 getActivity().getContentResolver().update(
						RA_Content.image.CONTENT_URI, values, "_id=?",
						new String[] { String.valueOf(image_id) });
//				TOAST.MAKETEXT(GETACTIVITY(),
//						UPDATE + ":" + STRING.VALUEOF(IMAGE_ID),
//						TOAST.LENGTH_LONG).SHOW();

			}
		});

		return main;
	}

	// TODO: Rename method, update argument and hook method into UI event

}
