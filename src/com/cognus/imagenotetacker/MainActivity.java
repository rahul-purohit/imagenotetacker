package com.cognus.imagenotetacker;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cognus.imagegallery.data.provider.Gallery;
import com.cognus.imagegallery.data.provider.RA_Content;
import com.cognus.imagenotetacker.AddGalleryFragment.addGalleryInter;

public class MainActivity extends FragmentActivity implements addGalleryInter {
	ImageView addgallery;
	ListView gallery_list;
	GalleryAdapter galleryAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String pathToExternalStorage = Environment
				.getExternalStorageDirectory().toString();
		File appDirectory = new File(pathToExternalStorage + "/"
				+ getString(R.string.app_name));
		if (!(appDirectory.exists())) {
			appDirectory.mkdir();
		}

		addgallery = (ImageView) findViewById(R.id.add_gallery);
		gallery_list = (ListView) findViewById(R.id.listView1);

		galleryAdapter = new GalleryAdapter(getBaseContext());
		addGallery();
		gallery_list.setAdapter(galleryAdapter);
		addgallery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(),
						android.R.anim.fade_out));
				AddGalleryFragment addGalleryFragment = AddGalleryFragment
						.getInstance(MainActivity.this);
				addGalleryFragment.show(getSupportFragmentManager(),
						"addGallery");
			}
		});

		gallery_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long _id) {

				Gallery gallery = (Gallery) v.getTag();

				Intent intent = new Intent(MainActivity.this,
						ImageViewerActivity.class);
				intent.putExtra("gallery_id", gallery.get_id());
				startActivity(intent);

			}
		});

		gallery_list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int arg2, long arg3) {

				v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(),
						android.R.anim.fade_out));
				final Gallery gallery = (Gallery) v.getTag();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);

				builder.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String pathToExternalStorage = Environment
										.getExternalStorageDirectory()
										.toString();
								File appDirectory = new File(
										pathToExternalStorage + "/"
												+ getString(R.string.app_name)
												+ "/" + gallery.get_id());
								appDirectory.delete();
								// have the object build the directory
								// structure, if
								// needed.
								getContentResolver().delete(
										RA_Content.gallery.CONTENT_URI,
										"_id=?",
										new String[] { gallery.get_id() + "" });
								addGallery();
							}
						});

				builder.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
				builder.setTitle("Delete?");
				builder.setMessage("This will Delete all yourimages in this gallery.\n do you really want to continue?");
				builder.create().show();

				return true;
			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void addGallery() {
		Cursor query = getContentResolver().query(
				RA_Content.gallery.CONTENT_URI, RA_Content.gallery.PROJECTION,
				null, null, "name COLLATE NOCASE");
		galleryAdapter.arrayList.clear();
		galleryAdapter.notifyDataSetChanged();
		if (query.moveToNext()) {
			galleryAdapter.arrayList.clear();
			galleryAdapter.notifyDataSetChanged();

			do {
				Gallery gallery = new Gallery();
				gallery.set_id(query.getInt(0));
				gallery.setName(query.getString(1));
				gallery.setCreated_on(query.getString(query
						.getColumnIndex("created_on")));
				gallery.setCreated_by(query.getInt(query
						.getColumnIndex("created_by")));
				galleryAdapter.arrayList.add(gallery);
			} while (query.moveToNext());

			galleryAdapter.notifyDataSetChanged();
		}
		query.close();
		galleryAdapter.notifyDataSetInvalidated();
	}

	private class GalleryAdapter extends BaseAdapter {
		LayoutInflater inflater;
		public ArrayList<Gallery> arrayList = new ArrayList<Gallery>();

		public GalleryAdapter(Context context) {
			inflater = getLayoutInflater();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrayList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return arrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return arrayList.get(position).get_id();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = inflater.inflate(R.layout.item_layout, null);

			Gallery gallery = arrayList.get(position);

			TextView textView = (TextView) convertView
					.findViewById(R.id.textView1);
			ImageView edit = (ImageView) convertView.findViewById(R.id.edit);
			textView.setText(gallery.getName());
			edit.setTag(gallery);
			edit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					v.startAnimation(AnimationUtils.loadAnimation(
							getBaseContext(), android.R.anim.fade_out));
					Gallery gallery = (Gallery) v.getTag();

					AddGalleryFragment addGalleryFragment = AddGalleryFragment
							.getInstance(gallery, MainActivity.this);
					addGalleryFragment.show(getSupportFragmentManager(),
							"addGallery");

				}
			});
			convertView.setTag(gallery);

			return convertView;
		}

	}

}
