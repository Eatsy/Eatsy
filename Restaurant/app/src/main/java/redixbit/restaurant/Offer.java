package redixbit.restaurant;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import redixbit.getset.Restgetset;
import redixbit.util.ConnectionDetector;
import redixbit.util.ImageLoader;

public class Offer extends Activity {
	ListView list_detail;
	String[] str = { "abc", "def", "pqr" };
	ProgressDialog progressDialog;
	ArrayList<Restgetset> rest;
	Button btn_more, btn_map, btn_more1;
	Boolean isInternetPresent = false;
	ConnectionDetector cd;
	View layout12;
	private String Error = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offer);

		cd = new ConnectionDetector(getApplicationContext());

		isInternetPresent = cd.isConnectingToInternet();
		// check for Internet status
		if (isInternetPresent) {
			// Internet Connection is Present
			// make HTTP requests
			// showAlertDialog(getApplicationContext(), "Internet Connection",
			// "You have internet connection", true);
			rest = new ArrayList<Restgetset>();
			new getofferdetail().execute();
		} else {
			// Internet connection is not present
			// Ask user to connect to Internet
			RelativeLayout rl_back = (RelativeLayout) findViewById(R.id.rl_back);
			if (rl_back == null) {
				Log.d("second", "second");
				RelativeLayout rl_dialoguser = (RelativeLayout) findViewById(R.id.rl_infodialog);

				layout12 = getLayoutInflater().inflate(
						R.layout.connectiondialog, rl_dialoguser, false);

				rl_dialoguser.addView(layout12);
				rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(
						Offer.this, R.anim.popup));
				Button btn_yes = (Button) layout12.findViewById(R.id.btn_yes);
				btn_yes.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
					}
				});
			}
			// showAlertDialog(getApplicationContext(),
			// "No Internet Connection",
			// "You don't have internet connection.", false);

		}
		// Initialize

	}

	// getting special offer detail
	public class getofferdetail extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(Offer.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			getdetailforNearMe();
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);

			if (Error != null) {
				RelativeLayout rl_back = (RelativeLayout) findViewById(R.id.rl_back);
				if (rl_back == null) {
					Log.d("second", "second");
					RelativeLayout rl_dialoguser = (RelativeLayout) findViewById(R.id.rl_infodialog);

					layout12 = getLayoutInflater().inflate(
							R.layout.json_dilaog, rl_dialoguser, false);

					rl_dialoguser.addView(layout12);
					rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(
							Offer.this, R.anim.popup));
					Button btn_yes = (Button) layout12
							.findViewById(R.id.btn_yes);
					btn_yes.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							finish();
						}
					});
				}
			} else {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();

					list_detail = (ListView) findViewById(R.id.list_detail);

					LazyAdapter lazy = new LazyAdapter(Offer.this, rest);
					list_detail.setAdapter(lazy);
				}
			}

		}

	}

	private void getdetailforNearMe() {
		// TODO Auto-generated method stub

		URL hp = null;
		try {

			 hp = new URL(getString(R.string.liveurl)+
			 "offersdetail.php");

			//hp = new URL("http://192.168.1.106/restourant/offersdetail.php");

			Log.d("URL", "" + hp);
			URLConnection hpCon = hp.openConnection();
			hpCon.connect();
			InputStream input = hpCon.getInputStream();
			Log.d("input", "" + input);

			BufferedReader r = new BufferedReader(new InputStreamReader(input));

			String x = "";
			x = r.readLine();
			String total = "";

			while (x != null) {
				total += x;
				x = r.readLine();
			}
			Log.d("URL", "" + total);
			// JSONObject jObject = new JSONObject(total);
			// JSONArray j = jObject.getJSONArray("Restaurant");
			JSONArray j = new JSONArray(total);

			Log.d("URL1", "" + j);
			for (int i = 0; i < j.length(); i++) {

				JSONObject Obj;
				Obj = j.getJSONObject(i);

				Restgetset temp = new Restgetset();
				temp.setTitle(Obj.getString("title"));
				temp.setImage(Obj.getString("image"));

				rest.add(temp);
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			Error = e.getMessage();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Error = e.getMessage();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Error = e.getMessage();
		} catch (NullPointerException e) {
			// TODO: handle exception
			Error = e.getMessage();
		}
	}

	public class LazyAdapter extends BaseAdapter {

		private Activity activity;
		private ArrayList<Restgetset> data;
		private LayoutInflater inflater = null;
		Typeface tf2 = Typeface.createFromAsset(Offer.this.getAssets(),
				"fonts/calibri.ttf");

		public LazyAdapter(Activity a, ArrayList<Restgetset> str) {
			activity = a;
			data = str;
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View vi = convertView;

			if (convertView == null) {

				vi = inflater.inflate(R.layout.offercell, null);
			}

			TextView txt_name = (TextView) vi.findViewById(R.id.txt_head_offer);
			txt_name.setText(data.get(position).getTitle());
			txt_name.setTypeface(tf2);

			String image = data.get(position).getImage();
			Log.d("image", "" + image);

			ImageView img_offer = (ImageView) vi.findViewById(R.id.img_offer);
			img_offer.setImageResource(R.drawable.offer_page_img);

			AnimatorSet set = new AnimatorSet();

			set.playTogether(
					ObjectAnimator.ofFloat(img_offer, "scaleX", 0.0f, 1.0f)
							.setDuration(1500),
					ObjectAnimator.ofFloat(img_offer, "scaleY", 0.0f, 1.0f)
							.setDuration(1500));
			set.start();
			ImageLoader imgLoader = new ImageLoader(Offer.this);
			imgLoader.DisplayImage(getString(R.string.liveurl)+"uploads/offers/"
					+ image, img_offer);

			return vi;
		}
	}

	class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;
		Bitmap mIcon11;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		@Override
		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];

			try {
				// BitmapFactory.Options options = new BitmapFactory.Options();
				// options.inSampleSize = 8;
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", "" + e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// pd.dismiss();
			bmImage.setImageBitmap(result);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		/*
		 * Intent iv = new Intent(Offer.this, Home.class);
		 * iv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
		 * Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(iv);
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.offer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
