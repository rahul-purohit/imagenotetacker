package com.cognus.imagenotetacker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.cognus.adapter.GridViewImageAdapter;
import com.cognus.adapter.GridViewImageAdapter.OnSoicalButtonClick;
import com.cognus.imagegallery.data.Image;
import com.cognus.imagegallery.data.provider.Gallery;
import com.cognus.imagegallery.data.provider.RA_Content;
import com.cognus.imagegallery.data.provider.util.Utils;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.ipaulpro.afilechooser.utils.FileUtils;

public class ImageViewerActivity extends FragmentActivity implements
		OnSoicalButtonClick {

	GridView gridView;
	View view;
	Gallery gallery;
	private static final int REQUEST_CODE = 6384;
	ImageView file;
	ArrayList<String> filePaths;
	private static final int REAUTH_ACTIVITY_CODE = 100;
	// ProfilePictureView profilePictureView;
	private UiLifecycleHelper uiHelper;
	GridViewImageAdapter adapter;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, final SessionState state,
				final Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_viewer);
		gridView = (GridView) findViewById(R.id.gridView1);
		file = (ImageView) findViewById(R.id.file);
		Intent i = getIntent();
		int intExtra = i.getIntExtra("gallery_id", 0);

		Cursor query = getContentResolver().query(
				RA_Content.gallery.CONTENT_URI, RA_Content.gallery.PROJECTION,
				"_id=?", new String[] { intExtra + "" }, "_id");
		query.moveToFirst();
		gallery = new Gallery();
		gallery.set_id(query.getInt(0));
		gallery.setName(query.getString(1));
		gallery.setCreated_on(query.getString(query
				.getColumnIndex("created_on")));
		gallery.setCreated_by(query.getInt(query.getColumnIndex("created_by")));

		final Utils utils = new Utils(getBaseContext());

		filePaths = utils.getFilePaths(getBaseContext(), gallery);

		// getContentResolver().delete(RA_Content.image.CONTENT_URI,
		// "gallery_id=?", new String[] { gallery.get_id() + "" });
		Cursor getStorefile = getContentResolver().query(
				RA_Content.image.CONTENT_URI, RA_Content.image.PROJECTION,
				"gallery_id=?", new String[] { gallery.get_id() + "" }, null);

		for (int k = 0; k < filePaths.size(); k++) {
			try {
				ContentValues values = new ContentValues();
				values.put(RA_Content.image.Columns.CREATED_BY.getName(),
						gallery.getCreated_by() + "");
				values.put(RA_Content.image.Columns.CRETED_ON.getName(),
						new Date() + "");
				values.put(RA_Content.image.Columns.GALLERY_ID.getName(),
						gallery.get_id() + "");
				values.put(RA_Content.image.Columns.IMAGE.getName(),
						Utils.getDecodeFile(filePaths.get(k)));
				values.put(RA_Content.image.Columns.IMAGE_PATH_LOCAL.getName(),
						filePaths.get(k) + "");
				values.put(
						RA_Content.image.Columns.FILE_LAST_MODIFIED.getName(),
						Utils.getLastModifiedOfFile(filePaths.get(k)));
				getContentResolver().insert(RA_Content.image.CONTENT_URI,
						values);
			} catch (Exception exception) {
			}
		}
		if (getStorefile.moveToNext()) {
			if (getStorefile.getCount() == filePaths.size()) {
				Toast.makeText(getBaseContext(), "No New File Added",
						Toast.LENGTH_LONG).show();
			} else if (getStorefile.getCount() >= filePaths.size()) {
				Toast.makeText(
						getBaseContext(),
						"Some files were deleted Don't worry  we will recover them :-)",
						Toast.LENGTH_LONG).show();
				if (getStorefile.moveToFirst()) {
					do {
						try {
							byte img[] = getStorefile
									.getBlob(getStorefile
											.getColumnIndex(RA_Content.image.Columns.IMAGE
													.getName()));
							FileOutputStream fileOuputStream = new FileOutputStream(
									getStorefile.getString(getStorefile
											.getColumnIndex(RA_Content.image.Columns.IMAGE_PATH_LOCAL
													.getName())));
							fileOuputStream.write(img);
							fileOuputStream.close();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					} while (getStorefile.moveToNext());
					filePaths = utils.getFilePaths(getBaseContext(), gallery);
				}

			} else {
				Toast.makeText(
						getBaseContext(),
						" Found "
								+ (filePaths.size() - getStorefile.getCount())
								+ " new file(s).", Toast.LENGTH_LONG).show();
			}

		}
		ArrayList<Image> images = new ArrayList<Image>();

		if (getStorefile.moveToFirst()) {

			do {
				Image image = new Image();
				image.set_id(getStorefile.getInt(getStorefile
						.getColumnIndex(RA_Content.image.Columns.IMAGE_ID
								.getName())));
				image.setGallery_id(getStorefile.getInt(getStorefile
						.getColumnIndex(RA_Content.image.Columns.GALLERY_ID
								.getName())));

				image.setImage_path_local(getStorefile.getString(getStorefile
						.getColumnIndex(RA_Content.image.Columns.IMAGE_PATH_LOCAL
								.getName())));

				images.add(image);

			} while (getStorefile.moveToNext());
		}

		adapter = new GridViewImageAdapter(this, images, 500,
				getSupportFragmentManager());
		adapter.setOnSoicalButtonClick(this);
		gridView.setAdapter(adapter);
		view = findViewById(R.id.getCamera);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(),
						android.R.anim.fade_out));
				String pathToExternalStorage = Environment
						.getExternalStorageDirectory().toString();

				String path = pathToExternalStorage + "/"
						+ getString(R.string.app_name) + "/" + gallery.get_id()
						+ "/image" + (filePaths.size() + 1) + ".jpg";
				File directory = new File(path);
				Intent camera = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				Uri uriSavedImage = Uri.fromFile(directory);
				camera.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
				startActivityForResult(camera, 1);
			}
		});

		file.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent target = FileUtils.createGetContentIntent();
				// Create the chooser Intent
				Intent intent = Intent.createChooser(target,
						getString(R.string.gallery_add));
				try {
					startActivityForResult(intent, REQUEST_CODE);
				} catch (ActivityNotFoundException e) {
					// The reason for the existence of aFileChooser
				}
			}
		});

		Session.openActiveSession(this, true, new Session.StatusCallback() {

			// callback when session changes state
			@SuppressWarnings("deprecation")
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				if (session.isOpened()) {

					// make request to the /me API
					Request.executeMeRequestAsync(session,
							new Request.GraphUserCallback() {

								// callback after Graph API response with user
								// object
								@Override
								public void onCompleted(GraphUser user,
										Response response) {
									if (user != null) {
										Toast.makeText(
												getBaseContext(),
												"Hello " + user.getName() + "!",
												Toast.LENGTH_SHORT).show();
										System.out.println(user
												.getInnerJSONObject());

									}
								}
							});
				}
			}
		});

		// profilePictureView = (ProfilePictureView)
		// findViewById(R.id.profile_pic);
		// profilePictureView.setCropped(true);
		//
		// // Check for an open session
		// Session session = Session.getActiveSession();
		// if (session != null && session.isOpened()) {
		// // Get the user's data
		// makeMeRequest(session);
		// }

	}

	private void onSessionStateChange(final Session session,
			SessionState state, Exception exception) {
		if (session != null && session.isOpened()) {
			// Get the user's data.
			makeMeRequest(session);
		}
	}

	private void makeMeRequest(final Session session) {
		// Make an API call to get user data and define a
		// new callback to handle the response.
		Request request = Request.newMeRequest(session,
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						// If the response is successful
						if (session == Session.getActiveSession()) {
							if (user != null) {
								// Set the id for the ProfilePictureView
								// view that in turn displays the profile
								// picture.
								// profilePictureView.setProfileId(user.getId());
							}
						}
						if (response.getError() != null) {
							// Handle errors, will do so later.
						}
					}
				});
		request.executeAsync();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_viewer, menu);
		return true;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Utils utils = new Utils(getBaseContext());
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
		if (requestCode == REAUTH_ACTIVITY_CODE) {
			uiHelper.onActivityResult(requestCode, resultCode, data);
		}
		if (requestCode == REQUEST_CODE) {
			if (resultCode == RESULT_OK) {

				final Uri uri = data.getData();

				// Get the File path from the Uri
				String path = com.ipaulpro.afilechooser.utils.FileUtils
						.getPath(this, uri);

				if (!(utils.IsSupportedFile(path))) {
					Toast.makeText(getBaseContext(),
							"file format not supported", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				// Alternatively, use FileUtils.getFile(Context, Uri)
				if (path != null && FileUtils.isLocal(path)) {
					File source = new File(path);
					String pathToExternalStorage = Environment
							.getExternalStorageDirectory().toString();

					final String dst = pathToExternalStorage
							+ "/"
							+ getString(R.string.app_name)
							+ "/"
							+ gallery.get_id()
							+ "/image"
							+ (utils.getFilePaths(getBaseContext(), gallery)
									.size() + 1) + ".jpg";

					File desc = new File(dst);
					try {
						org.apache.commons.io.FileUtils.copyFile(source, desc);

						ContentValues values = new ContentValues();
						values.put(
								RA_Content.image.Columns.CREATED_BY.getName(),
								gallery.getCreated_by() + "");
						values.put(
								RA_Content.image.Columns.CRETED_ON.getName(),
								new Date() + "");
						values.put(
								RA_Content.image.Columns.GALLERY_ID.getName(),
								gallery.get_id() + "");
						values.put(RA_Content.image.Columns.IMAGE.getName(),
								dst);
						values.put(RA_Content.image.Columns.IMAGE_PATH_LOCAL
								.getName(), dst);
						values.put(RA_Content.image.Columns.FILE_LAST_MODIFIED
								.getName(), Utils
								.getLastModifiedOfFile(new Date().toString()));
						getContentResolver().insert(
								RA_Content.image.CONTENT_URI, values);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		Cursor getStorefile = getContentResolver().query(
				RA_Content.image.CONTENT_URI, RA_Content.image.PROJECTION,
				"gallery_id=?", new String[] { gallery.get_id() + "" }, null);
		ArrayList<Image> images = new ArrayList<Image>();

		if (getStorefile.moveToFirst()) {

			do {
				Image image = new Image();
				image.set_id(getStorefile.getInt(getStorefile
						.getColumnIndex(RA_Content.image.Columns.IMAGE_ID
								.getName())));
				image.setGallery_id(getStorefile.getInt(getStorefile
						.getColumnIndex(RA_Content.image.Columns.GALLERY_ID
								.getName())));

				image.setImage_path_local(getStorefile.getString(getStorefile
						.getColumnIndex(RA_Content.image.Columns.IMAGE_PATH_LOCAL
								.getName())));

				images.add(image);

			} while (getStorefile.moveToNext());
		}
		adapter = new GridViewImageAdapter(this, images, 300,
				getSupportFragmentManager());
		adapter.setOnSoicalButtonClick(this);
		gridView.setAdapter(adapter);

	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onFacebookButtonClick(Image image) {
		// Facebook mFacebook = ((this)getApplicationContext()).facebook;
		// mAsyncFbRunner = new AsyncFacebookRunner(mFacebook);

		publishFeedDialog(image.getImage_path_local(), image.getInfo(),
				image.getImage());
	}

	private void publishFeedDialog(String path, String info, byte[] imagebyte) {
		Bundle params = new Bundle();
		if (info != null)
			params.putString("name", info);
		else
			params.putString("name", getString(R.string.app_name));

		params.putByteArray("picture", imagebyte);
		params.putString("caption", "Share With. "
				+ getString(R.string.app_name));
		params.putString("description", "Share Photos Any Time With "
				+ getString(R.string.app_name));
		// params.putString("link", "https://developers.facebook.com/android");
		// params.putString("picture", path);
		// mAsyncRunner.request("me/photos", params, "POST", null);
		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(
				ImageViewerActivity.this, Session.getActiveSession(), params)).setPicture("http://images.cdn.bigcartel.com/bigcartel/product_images/93954035/max_h-1000+max_w-1000/r1.jpg").setLink("http://192.168.1.98/WALL2.JPG")
				.setOnCompleteListener(new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error == null) {
							// When the story is posted, echo the success
							// and the post Id.
							final String postId = values.getString("post_id");
							if (postId != null) {
								Toast.makeText(ImageViewerActivity.this,
										"Posted story, id: " + postId,
										Toast.LENGTH_SHORT).show();
							} else {
								// User clicked the Cancel button
								Toast.makeText(
										ImageViewerActivity.this
												.getApplicationContext(),
										"Publish cancelled", Toast.LENGTH_SHORT)
										.show();
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							// User clicked the "x" button
							Toast.makeText(
									ImageViewerActivity.this
											.getApplicationContext(),
									"Publish cancelled", Toast.LENGTH_SHORT)
									.show();
						} else {
							// Generic, ex: network error
							Toast.makeText(
									ImageViewerActivity.this
											.getApplicationContext(),
									"Error posting story", Toast.LENGTH_SHORT)
									.show();
						}
					}

				}).build();
		feedDialog.show();
	}

	@Override
	public void onTwitterClick(Image image) {
		// TODO Auto-generated method stub

	}

	public abstract class BaseDialogListener implements DialogListener {

		public void onFacebookError(FacebookError e) {
			e.printStackTrace();
		}

		public void onError(DialogError e) {
			e.printStackTrace();
		}

		public void onCancel() {
		}
	}

	public class SampleDialogListener extends BaseDialogListener {

		public void onComplete(Bundle values) {
			final String postId = values.getString("post_id");
			if (postId != null) {
				Log.d("Facebook-Example", "Dialog Success! post_id=" + postId);
				// mAsyncRunner.request(postId, new WallPostRequestListener());
				//
				// mDeleteButton.setVisibility(View.VISIBLE);
			} else {
				Log.d("Facebook-Example", "No wall post made");
			}
		}
	}
}
