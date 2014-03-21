package com.cognus.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.cognus.imagegallery.data.Image;
import com.cognus.imagenotetacker.NewNoteTacker;
import com.cognus.imagenotetacker.R;
import com.cognustechnology.Image.SmartImageView;

public class GridViewImageAdapter extends BaseAdapter {

	private Activity _activity;
	private ArrayList<Image> _filePaths = new ArrayList<Image>();
	private int imageWidth;
	LayoutInflater inflater;
	FragmentManager fragmentManager;

	/**
	 * INT
	 * 
	 * @author user
	 * 
	 */
	public interface OnSoicalButtonClick {
		public void onFacebookButtonClick(Image image);

		public void onTwitterClick(Image image);

	}

	private OnSoicalButtonClick onSoicalButtonClick;

	public GridViewImageAdapter(Activity activity, ArrayList<Image> filePaths,
			int imageWidth, FragmentManager fragmentManager) {
		this._activity = activity;
		this._filePaths = filePaths;
		this.imageWidth = imageWidth;
		inflater = _activity.getLayoutInflater();
		this.fragmentManager = fragmentManager;
	}

	@Override
	public int getCount() {
		return this._filePaths.size();
	}

	@Override
	public Object getItem(int position) {
		return this._filePaths.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View main;
		SmartImageView imageView;
		ImageView edit_info;
		ImageView fbClick;
		if (convertView == null) {
			main = inflater.inflate(R.layout.grid_viewadapter, null);
		} else {
			main = convertView;
		}

		// get screen dimensions
		Bitmap image = decodeFile(_filePaths.get(position)
				.getImage_path_local(), imageWidth, imageWidth);
		imageView = (SmartImageView) main.findViewById(R.id.getCamera);
		fbClick = (ImageView) main.findViewById(R.id.fb_clk);

		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		// imageView.setLayoutParams(new GridView.LayoutParams(imageWidth,
		// imageWidth));
		imageView.setImageBitmap(image);
		edit_info = (ImageView) main.findViewById(R.id.twt_clk);
		edit_info.setTag(_filePaths.get(position));
		edit_info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Image image = (Image) v.getTag();
				v.startAnimation(AnimationUtils.loadAnimation(_activity,
						android.R.anim.fade_in));

				Toast.makeText(_activity, "iMAGE ID" + image.get_id() + "",
						Toast.LENGTH_LONG).show();
				NewNoteTacker newNoteTacker = NewNoteTacker.newInstance(
						image.getGallery_id(), image.get_id());
				newNoteTacker.show(fragmentManager, image.getImage_path_local());
			}
		});

		fbClick.setTag(_filePaths.get(position));
		// image view click listener
		// imageView.setOnClickListener(new OnImageClickListener(position));
		fbClick.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Image image = (Image) v.getTag();
				v.startAnimation(AnimationUtils.loadAnimation(_activity,
						android.R.anim.fade_in));
				if (onSoicalButtonClick != null) {

					onSoicalButtonClick.onFacebookButtonClick(image);

				}

			}
		});
		return main;
	}

	class OnImageClickListener implements OnClickListener {

		int _postion;

		// constructor
		public OnImageClickListener(int position) {
			this._postion = position;
		}

		@Override
		public void onClick(View v) {
			// on selecting grid view image
			// // launch full screen activity
			// Intent i = new Intent(_activity, FullScreenViewActivity.class);
			// i.putExtra("position", _postion);
			// _activity.startActivity(i);
		}

	}

	/*
	 * Resizing image size
	 */
	public Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
		try {

			File f = new File(filePath);

			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			final int REQUIRED_WIDTH = WIDTH;
			final int REQUIRED_HIGHT = HIGHT;
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
					&& o.outHeight / scale / 2 >= REQUIRED_HIGHT)
				scale *= 2;

			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public OnSoicalButtonClick getOnSoicalButtonClick() {
		return onSoicalButtonClick;
	}

	public void setOnSoicalButtonClick(OnSoicalButtonClick onSoicalButtonClick) {
		this.onSoicalButtonClick = onSoicalButtonClick;
	}

}
