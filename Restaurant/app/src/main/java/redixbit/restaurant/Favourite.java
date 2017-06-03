package redixbit.restaurant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import redixbit.getset.Getsetfav;
import redixbit.getset.Restgetset;
import redixbit.util.AlertDialogManager;
import redixbit.util.ConnectionDetector;
import redixbit.util.DBAdapter;
import redixbit.util.GPSTracker;

public class Favourite extends Activity {
	ListView list_fav;
	SQLiteDatabase db;
	ArrayList<Getsetfav> FileList;
	Cursor cur = null;
	String id;
	String name, id1, address, latitude, longitude;
	static double d;
	Button btn_more, btn_map, btn_more1;
	Button btn_fvrts;
	int start = 0;
	GPSTracker gps;
	static double latitudecu;
	static double longitudecu;
	Vector<String> bgName;
	int pos;
	static double miles;
	ProgressDialog progressDialog;
	ArrayList<Restgetset> rest1;
	String rattting, restname;
	String state;
	
	public static final String MY_PREFS_NAME = "Restaurant";
	Boolean isInternetPresent = false;
	ConnectionDetector cd;
	View layout12;
	InterstitialAd mInterstitialAd;
	boolean interstitialCanceled;
	AlertDialogManager alert = new AlertDialogManager();
	int MainPosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

		setContentView(R.layout.activity_favourite);

		cd = new ConnectionDetector(getApplicationContext());

		isInternetPresent = cd.isConnectingToInternet();
		// check for Internet status
		if (isInternetPresent) {
			if (getString(R.string.favourite_banner).equals("yes")) {
				AdView mAdView = (AdView) findViewById(R.id.adView);
				AdRequest adRequest = new AdRequest.Builder().build();
				mAdView.loadAd(adRequest);
			} else {

				AdView mAdView = (AdView) findViewById(R.id.adView);
				mAdView.setVisibility(View.GONE);
			}
			gps = new GPSTracker(Favourite.this);
			// check if GPS enabled
			if (gps.canGetLocation()) {
				latitudecu = gps.getLatitude();
				longitudecu = gps.getLongitude();

				// \n is for new line

			} else {
				// can't get location
				// GPS or Network is not enabled
				// Ask user to enable GPS/network in settings
				gps.showSettingsAlert();
			}
			initialize();
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
						Favourite.this, R.anim.popup));
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
	}

	private void initialize() {
		// TODO Auto-generated method stub
		rest1 = Home.rest;
		FileList = new ArrayList<Getsetfav>();
		bgName = new Vector<String>();
		new getList().execute();
		// new getrestaudetail().execute();

		Log.d("LOG", "" + bgName);

	}

	public class getrestaudetail extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {
			getdetailforNearMe();
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);

		}

	}

	private void getdetailforNearMe() {
		// TODO Auto-generated method stub

		URL hp = null;
		try {
			rest1.clear();
			hp = new URL(getString(R.string.liveurl) + "restaurantdetail.php");

			// hp = new
			// URL("http://192.168.1.106/restourant/restaurantdetail.php");

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
			JSONObject jObject = new JSONObject(total);
			JSONArray j = jObject.getJSONArray("Restaurant");
			// JSONArray j = new JSONArray(total);

			Log.d("URL1", "" + j);
			for (int i = 0; i < j.length(); i++) {

				JSONObject Obj;
				Obj = j.getJSONObject(i);

				Restgetset temp = new Restgetset();
				temp.setName(Obj.getString("name"));
				temp.setId(Obj.getString("id"));
				temp.setAddress(Obj.getString("address"));
				temp.setLatitude(Obj.getString("latitude"));
				temp.setLongitude(Obj.getString("longitude"));
				temp.setRatting(Obj.getString("ratting"));
				temp.setZipcode(Obj.getString("zipcode"));
				temp.setThubnailimage(Obj.getString("thumbnailimage"));
				temp.setVegtype(Obj.getString("vegtype"));

				rest1.add(temp);
			}
			Collections.sort(rest1, new Comparator<Restgetset>() {

				@Override
				public int compare(Restgetset lhs, Restgetset rhs) {
					// TODO Auto-generated method stub
					return Double.compare(lhs.getMiles(), rhs.getMiles());
				}
			});

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO: handle exception
		}
	}

	public static double convertmiles(String value, String places) {

		Log.d("latitudenew", "" + Double.parseDouble(value));
		Log.d("longitudenew", "" + Double.parseDouble(places));
		Location selected_location = new Location("locationA");
		selected_location.setLatitude(Double.parseDouble(value));
		selected_location.setLongitude(Double.parseDouble(places));
		Location near_locations = new Location("locationA");
		near_locations.setLatitude(latitudecu);
		near_locations.setLongitude(longitudecu);
		double dist = selected_location.distanceTo(near_locations);

		double km = dist / 1000.0;
		Log.d("Distance in KM", "" + km);

		double rounded = (double) Math.round(km * 100) / 100;
		Log.d("rounded", "" + rounded);

		round(rounded, 0);
		miles = d / 1.6;
		return miles;
	}

	// data display from database
	private class getList extends AsyncTask<String, Integer, Integer> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(Favourite.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub

			FileList.clear();
			DBAdapter myDbHelper = new DBAdapter(Favourite.this);
			myDbHelper = new DBAdapter(Favourite.this);
			try {
				myDbHelper.createDataBase();
			} catch (IOException e) {

				e.printStackTrace();
			}

			try {

				myDbHelper.openDataBase();

			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}

			int i = 1;
			db = myDbHelper.getReadableDatabase();

			try {
				cur = db.rawQuery("select * from favourite;", null);
				Log.d("SIZWA", "" + cur.getCount());
				if (cur.getCount() != 0) {
					if (cur.moveToFirst()) {
						do {
							Getsetfav obj = new Getsetfav();
							id1 = cur.getString(cur.getColumnIndex("id"));

							name = cur.getString(cur.getColumnIndex("name"));
							address = cur.getString(cur
									.getColumnIndex("address"));

							latitude = cur.getString(cur
									.getColumnIndex("latitude"));
							longitude = cur.getString(cur
									.getColumnIndex("longitude"));

							obj.setName(name);
							obj.setAddress(address);
							obj.setLatitude(latitude);
							obj.setId(id1);
							obj.setLongitude(longitude);
							convertmiles(latitude, longitude);

							Log.d("dd", "" + miles);
							obj.setMiles(miles);
							FileList.add(obj);

						} while (cur.moveToNext());

					}

				}
				Collections.sort(FileList, new Comparator<Getsetfav>() {

					@Override
					public int compare(Getsetfav lhs, Getsetfav rhs) {
						// TODO Auto-generated method stub
						return Double.compare(lhs.getMiles(), rhs.getMiles());
					}
				});
				cur.close();
				db.close();
				myDbHelper.close();
			} catch (Exception e) {
				// TODO: handle exception
			}

			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {

			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
				list_fav = (ListView) findViewById(R.id.list_detail);
				Log.d("file", "" + FileList.size());
				if (FileList.size() == 0) {
					Toast.makeText(getApplicationContext(),
							"No Record in Favourites", Toast.LENGTH_LONG)
							.show();
					list_fav.setVisibility(View.INVISIBLE);
				} else {
					int temp = 1;
					for (int i = 0; i < FileList.size(); i++) {

						if (temp == 1) {
							bgName.add("RED");
							temp++;
						} else if (temp == 2) {
							bgName.add("BLUE");
							temp++;
						}
						if (temp == 3) {
							bgName.add("GREEN");
							temp = 1;
						}
					}
					final Getsetfav tempobj = FileList.get(start);

					list_fav = (ListView) findViewById(R.id.list_detail);
					list_fav.setVisibility(View.VISIBLE);
					LazyAdapter lazy = new LazyAdapter(Favourite.this, FileList);
					lazy.notifyDataSetChanged();
					list_fav.setAdapter(lazy);
					list_fav.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub

							pos = position;
							// MainPosition = position;
							if (getString(R.string.Favourite_show_ads).equals(
									"yes")) {
								if (interstitialCanceled) {

								} else {

									if (mInterstitialAd != null
											&& mInterstitialAd.isLoaded()) {
										mInterstitialAd.show();
									} else {
										ContinueIntent();
									}
								}
							} else {
								ContinueIntent();
							}
						}
					});
				}

			}

		}
	}

	public class LazyAdapter extends BaseAdapter {

		private Activity activity;
		private ArrayList<Getsetfav> data;
		private LayoutInflater inflater = null;
		Typeface tf2 = Typeface.createFromAsset(Favourite.this.getAssets(),
				"fonts/OpenSans-Regular.ttf");

		public LazyAdapter(Activity a, ArrayList<Getsetfav> d) {
			activity = a;
			data = d;
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

				String temp = bgName.get(position);
				if (temp.equals("RED")) {
					vi = inflater.inflate(R.layout.cellfav, null);
				} else if (temp.equals("BLUE")) {
					vi = inflater.inflate(R.layout.cellfav1, null);
				} else if (temp.equals("GREEN")) {
					vi = inflater.inflate(R.layout.cellfav2, null);
				}

			}
			try {
				String namefirst = data.get(position).getName();
				String s = namefirst.substring(0, 1).toUpperCase();

				TextView txt_first = (TextView) vi.findViewById(R.id.txt_first);
				txt_first.setText("" + s);
			} catch (StringIndexOutOfBoundsException e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			TextView txt_name = (TextView) vi.findViewById(R.id.txt_rest_name);
			txt_name.setText(data.get(position).getName());
			txt_name.setTypeface(tf2);

			TextView txt_address = (TextView) vi.findViewById(R.id.txt_address);
			txt_address.setText(data.get(position).getAddress());
			txt_address.setTypeface(tf2);

			double earthRadius = 6371000; // meters
			double dLat = Math.toRadians(21.2049 - Double.parseDouble(data.get(
					position).getLatitude()));
			double dLng = Math.toRadians(72.8406 - Double.parseDouble(data.get(
					position).getLongitude()));
			double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
					+ Math.cos(Math.toRadians(21.2305574))
					* Math.cos(Math.toRadians(21.2049)) * Math.sin(dLng / 2)
					* Math.sin(dLng / 2);
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
			float dist = (float) (earthRadius * c);
			Log.d("Distance", "" + dist);

			double km = dist / 1000.0;
			Log.d("Distance in KM", "" + km);

			double rounded = (double) Math.round(km * 100) / 100;
			Log.d("rounded", "" + rounded);

			round(rounded, 0);
			double miles = d / 1.6;
			TextView txt_km = (TextView) vi.findViewById(R.id.txt_km);
			txt_km.setText(data.get(position).getMiles() + " " + "miles" + " "
					+ "away");
			txt_km.setTypeface(tf2);

			return vi;
		}
	}

	// counting km from latitude and longitude
	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		d = tmp / factor;
		Log.d("temp", "" + d);
		return (double) tmp / factor;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		/*
		 * Intent iv = new Intent(Favourite.this, Home.class);
		 * 
		 * iv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
		 * Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(iv);
		 */

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.favourite, menu);
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

	@Override
	public void onRestart() {
		super.onRestart();
		// When BACK BUTTON is pressed, the activity on the stack is restarted
		// Do what you want on the refresh procedure here
		new getList().execute();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		super.onResume();

		interstitialCanceled = false;
		CallNewInsertial();

	}

	private void CallNewInsertial() {
		cd = new ConnectionDetector(Favourite.this);

		if (!cd.isConnectingToInternet()) {
			alert.showAlertDialog(Favourite.this, "Internet Connection Error",
					"Please connect to working Internet connection", false);
			return;
		} else {
			// AdView mAdView = (AdView) findViewById(R.id.adView);
			// AdRequest adRequest = new AdRequest.Builder().build();
			// mAdView.loadAd(adRequest);
			if (getString(R.string.Favourite_show_ads).equals("yes")) {
				Log.d("ad", "" + getString(R.string.Home_show_ads));
				mInterstitialAd = new InterstitialAd(this);
				mInterstitialAd
						.setAdUnitId(getString(R.string.insertial_ad_key));

				requestNewInterstitial();

				mInterstitialAd.setAdListener(new AdListener() {
					@Override
					public void onAdClosed() {
						ContinueIntent();
					}

				});
			} else {
				// ContinueIntent();
			}

		}
	}

	private void ContinueIntent() {
		Intent iv = new Intent(Favourite.this, Detailpage.class);
		iv.putExtra("rating", "" + rest1.get(pos).getRatting());
		iv.putExtra("name", "" + rest1.get(pos).getName());

		iv.putExtra("id", "" + FileList.get(pos).getId());
		iv.putExtra("state", "" + state);
		startActivity(iv);
	}

	private void requestNewInterstitial() {
		AdRequest adRequest = new AdRequest.Builder().build();

		mInterstitialAd.loadAd(adRequest);

	}

	@Override
	public void onPause() {
		mInterstitialAd = null;
		interstitialCanceled = true;
		super.onPause();
	}
}
