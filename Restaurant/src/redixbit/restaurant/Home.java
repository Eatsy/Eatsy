package redixbit.restaurant;

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
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import redixbit.getset.Restgetset;
import redixbit.getset.Restgetset1;
import redixbit.restaurant.Review.getratedetail;
import redixbit.restaurant.Review.getuserdetail;
import redixbit.util.AlertDialogManager;
import redixbit.util.ConnectionDetector;
import android.R.anim;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class Home extends Activity {
	ListView list_detail;
	Context context = this;
	int MAINPOSITION = 0;
	DrawerLayout mDrawerLayout;
	EditText edit_search, edt_nameuser, edt_usermail, edt_comment,
			edt_enameuser, edt_emailuser;
	public static ArrayList<Restgetset> rest;
	ArrayList<Restgetset1> rest1;
	ArrayList<Restgetset> restmap;
	static double d;
	static double miles;
	String rating, search;
	Float rate = 0.0f;
	LinearLayout lldisplay;
	Button btn_fvrts;
	int pos;
	RelativeLayout rl_user_dialog, rl_add_dialog;
	public static final String MY_PREFS_NAME = "Restaurant";
	// ListView represents Navigation Drawer
	ListView mDrawerList;
	String username, usermail, createusername, createusermail;
	String updatename, updateemail;
	// ActionBarDrawerToggle indicates the presence of Navigation Drawer in the
	// action bar
	ActionBarDrawerToggle mDrawerToggle;
	Button btn_more, btn_map, btn_more1;
	EditText edt_name, edt_mail, createnameuser, createemailuser;
	ProgressDialog progressDialog;
	String foodtype;
	String user2, uservalue;
	View layout12;
	RelativeLayout rl_home, rl_update, rl_create;
	String input, map;
	String emailpattern;
	LinearLayout ll_data;
	String value;
	Boolean isInternetPresent = false;
	ConnectionDetector cd;
	private String Error = null;
	InterstitialAd mInterstitialAd;
	boolean interstitialCanceled;
	AlertDialogManager alert = new AlertDialogManager();
	int MainPosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		cd = new ConnectionDetector(getApplicationContext());

		isInternetPresent = cd.isConnectingToInternet();
		// check for Internet status
		if (isInternetPresent) {

			if (getString(R.string.Home_banner).equals("yes")) {
				AdView mAdView = (AdView) findViewById(R.id.adView);
				AdRequest adRequest = new AdRequest.Builder().build();
				mAdView.loadAd(adRequest);
			}

			emailpattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
			// initialize
			initialize();

			Intent iv = getIntent();
			foodtype = iv.getStringExtra("foodname");
			map = iv.getStringExtra("map");
			Log.d("map", "" + map);

			// check data is category page or not
			if (foodtype != null) {
				new getrestaudetail1().execute();
			} else {
				new getrestaudetail().execute();
			}

			buttonclick();
			drawer();

			// TODO Auto-generated method stub

			try {
				rating = String.valueOf(rate);
			} catch (NumberFormatException e) {
				// TODO: handle exception
			}

			rating = "3";

			edit_search = (EditText) findViewById(R.id.edit_search);
			// search on home page method
			edit_search.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable s) { // TODO
															// Auto-generated
															// method stub
					search = s.toString();
					if (search.equals("")) {
						new getrestaudetail().execute();
						Log.d("search", "" + search);
					} else {
						new getrestaudetail1().execute();
					}

				}
			});
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
						Home.this, R.anim.popup));
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

	private void drawer() {
		// TODO Auto-generated method stub
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_launcher, R.string.drawer_open,
				R.string.drawer_close) {

			/** Called when drawer is closed */
			@Override
			public void onDrawerClosed(View view) {
				// getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();

			}

			/** Called when a drawer is opened */
			@Override
			public void onDrawerOpened(View drawerView) {
				// getActionBar().setTitle("Select a river");
				invalidateOptionsMenu();
			}

		};

		// Setting DrawerToggle on DrawerLayout
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// all linear layout from slider menu
		LinearLayout ll_home = (LinearLayout) findViewById(R.id.ll_home);
		ll_home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDrawerLayout.closeDrawer(GravityCompat.START);
				mDrawerLayout.setVisibility(View.INVISIBLE);

			}
		});

		// category buuton click
		LinearLayout ll_cat = (LinearLayout) findViewById(R.id.ll_cat);
		ll_cat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDrawerLayout.setVisibility(View.INVISIBLE);
				mDrawerLayout.closeDrawer(GravityCompat.START);
				Intent iv = new Intent(getApplicationContext(), Category.class);
				startActivity(iv);
			}
		});

		// favourite button click
		LinearLayout ll_fvrt = (LinearLayout) findViewById(R.id.ll_fav);
		ll_fvrt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				mDrawerLayout.setVisibility(View.INVISIBLE);
				mDrawerLayout.closeDrawer(GravityCompat.START);
				Intent iv = new Intent(getApplicationContext(), Favourite.class);
				startActivity(iv);
			}
		});

		// special offer button click
		LinearLayout ll_special = (LinearLayout) findViewById(R.id.ll_special);
		ll_special.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDrawerLayout.setVisibility(View.INVISIBLE);
				mDrawerLayout.closeDrawer(GravityCompat.START);
				Intent iv = new Intent(getApplicationContext(), Offer.class);
				startActivity(iv);
			}
		});

		// about us button click
		LinearLayout ll_about = (LinearLayout) findViewById(R.id.ll_about);
		ll_about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDrawerLayout.setVisibility(View.INVISIBLE);
				mDrawerLayout.closeDrawer(GravityCompat.START);
				Intent iv = new Intent(getApplicationContext(), About.class);
				startActivity(iv);
			}
		});

		// social sharing button click
		LinearLayout ll_social = (LinearLayout) findViewById(R.id.ll_social);
		ll_social.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDrawerLayout.setVisibility(View.INVISIBLE);
				Intent share = new Intent(android.content.Intent.ACTION_SEND);
				share.setType("text/plain");

				share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				share.putExtra(Intent.EXTRA_SUBJECT, "Restaurant");

				share.putExtra(
						Intent.EXTRA_TEXT,
						"https://play.google.com/store/apps/details?id="
								+ Home.this.getPackageName()
								+ "\n"
								+ "The great advantage of a restaurant is that it's a refuge from home life. ");
				startActivity(Intent.createChooser(share, "Share Link!"));
			}
		});

		// terms and condtion button click
		LinearLayout ll_terms = (LinearLayout) findViewById(R.id.ll_term);
		ll_terms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDrawerLayout.setVisibility(View.INVISIBLE);
				mDrawerLayout.closeDrawer(GravityCompat.START);
				Intent iv = new Intent(getApplicationContext(),
						Termcondition.class);
				startActivity(iv);
			}
		});

		// profile button click
		LinearLayout ll_profile = (LinearLayout) findViewById(R.id.ll_profile);
		ll_profile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				list_detail.setEnabled(false);
				// create view
				layout12 = v;
				mDrawerLayout.closeDrawer(GravityCompat.START);
				mDrawerLayout.setVisibility(View.INVISIBLE);
				SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME,
						MODE_PRIVATE);

				// check user is created or not
				if (prefs.getString("score", null) != null) {

					uservalue = prefs.getString("score", null);
					Log.d("user3", uservalue);

					if (prefs.getString("username", null) != null
							&& prefs.getString("usermailid", null) != null) {

						Log.d("first", "first");
						RelativeLayout rl_back = (RelativeLayout) findViewById(R.id.rl_back);

						if (rl_back == null) {
							Log.d("second", "second");
							RelativeLayout rl_dialoguser = (RelativeLayout) findViewById(R.id.rl_infodialog);

							layout12 = getLayoutInflater().inflate(
									R.layout.update_dialog, rl_dialoguser,
									false);

							rl_dialoguser.addView(layout12);

							ImageView img_back = (ImageView) findViewById(R.id.img_back);
							rl_dialoguser.setAlpha(1);

							img_back.setAlpha(0.8f);

							edt_enameuser = (EditText) layout12
									.findViewById(R.id.edt_enameuser);
							edt_emailuser = (EditText) layout12
									.findViewById(R.id.edt_emailuser);
							username = prefs.getString("username", null);
							Log.d("user3", username);
							edt_enameuser.setText(username);
							usermail = prefs.getString("usermailid", null);
							edt_emailuser.setText(usermail);

							// updateuser button click
							Button btn_updateuser = (Button) layout12
									.findViewById(R.id.btn_updateuser);
							btn_updateuser
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											// rl_adddialog.setVisibility(View.GONE);
											layout12 = v;
											list_detail.setEnabled(true);
											updatename = edt_enameuser
													.getText().toString()
													.replace(" ", "%20");
											updateemail = edt_emailuser
													.getText().toString();

											if (updateemail
													.matches(emailpattern)
													&& updateemail.length() > 0) {
												if (username.equals("")) {
													edt_nameuser
															.setError("Enter User Name");
												} else {
													SharedPreferences.Editor editor = getSharedPreferences(
															MY_PREFS_NAME,
															MODE_PRIVATE)
															.edit();
													editor.putString(
															"username",
															"" + updatename);
													editor.putString(
															"usermailid",
															"" + updateemail);
													editor.commit();
													new getupdatedetail()
															.execute();

													AlertDialog.Builder builder = new AlertDialog.Builder(
															Home.this);
													builder.setMessage(
															"Successfully updated your profile")
															.setTitle("Updated");

													builder.setNeutralButton(
															android.R.string.ok,
															new DialogInterface.OnClickListener() {
																@Override
																public void onClick(
																		DialogInterface dialog,
																		int id) {
																	dialog.cancel();
																}
															});
													AlertDialog alert = builder
															.create();
													alert.show();
													// rl_user_dialog.setVisibility(View.GONE);
													mDrawerLayout
															.setVisibility(View.INVISIBLE);
													View myView = findViewById(R.id.rl_back);
													ViewGroup parent = (ViewGroup) myView
															.getParent();
													parent.removeView(myView);
													ImageView img_back = (ImageView) findViewById(R.id.img_back);
												}
											} else {
												edt_emailuser
														.setError("Enter Valid Email");
											}

											// img_back.setAlpha(0.0f);
											// new getreviewdetail().execute();
										}
									});

							// cancel button click
							Button btn_cancel = (Button) layout12
									.findViewById(R.id.btn_canceluser);
							btn_cancel
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											list_detail.setEnabled(true);
											mDrawerLayout
													.setVisibility(View.INVISIBLE);

											ImageView img_back = (ImageView) findViewById(R.id.img_back);
											img_back.setAlpha(0.0f);
											View myView = findViewById(R.id.rl_back);
											ViewGroup parent = (ViewGroup) myView
													.getParent();
											parent.removeView(myView);
										}
									});

						}
					} else {

					}
				} else {

					mDrawerLayout.setVisibility(View.INVISIBLE);

					RelativeLayout rl_back = (RelativeLayout) findViewById(R.id.rl_back);

					if (rl_back == null) {
						RelativeLayout rl_dialog = (RelativeLayout) findViewById(R.id.rl_infodialog);

						layout12 = getLayoutInflater().inflate(
								R.layout.dialog_create, rl_dialog, false);
						rl_dialog.addView(layout12);
						ImageView img_back = (ImageView) findViewById(R.id.img_back);
						img_back.setAlpha(0.8f);
						rl_dialog.setAlpha(1);

						edt_nameuser = (EditText) layout12
								.findViewById(R.id.edt_name);
						edt_usermail = (EditText) layout12
								.findViewById(R.id.edt_email12);

						Button btn_usersubmit = (Button) layout12
								.findViewById(R.id.btn_submituser);
						btn_usersubmit
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										list_detail.setEnabled(true);
										String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
										createusername = edt_nameuser.getText()
												.toString();

										createusermail = edt_usermail.getText()
												.toString();
										if (createusermail
												.matches(emailPattern)
												&& createusermail.length() > 0) {
											if (createusername.equals("")) {
												edt_nameuser
														.setError("Enter User Name");
											} else {
												SharedPreferences.Editor editor = getSharedPreferences(
														MY_PREFS_NAME,
														MODE_PRIVATE).edit();
												editor.putString("username", ""
														+ createusername);
												editor.putString("usermailid",
														"" + createusermail);
												editor.commit();
												new getuserdetail().execute();

												View myView = findViewById(R.id.rl_back);

												ViewGroup parent = (ViewGroup) myView
														.getParent();
												parent.removeView(myView);
												ImageView img_back = (ImageView) findViewById(R.id.img_back);
											}
										} else {
											edt_usermail
													.setError("Enter valid email address");
										}

										// img_back.setAlpha(0.0f);
									}
								});

					}

				}

			}
		});
	}

	private void buttonclick() {
		// TODO Auto-generated method stub
		btn_map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent iv = new Intent(Home.this, Gmap.class);
				iv.putExtra("map", "yes");
				startActivity(iv);
				overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
				// finish();
			}
		});
		btn_more = (Button) findViewById(R.id.img_more);

		btn_more1 = (Button) findViewById(R.id.img_more1);
		btn_more1.setVisibility(View.INVISIBLE);

		// drawer open
		btn_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btn_more1.setVisibility(View.VISIBLE);
				btn_more.setVisibility(View.INVISIBLE);
				mDrawerLayout.setVisibility(View.VISIBLE);
				mDrawerLayout.openDrawer(Gravity.LEFT);
			}

		});

		// close drawer
		btn_more1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// mDrawerLayout.setVisibility(View.VISIBLE);
				btn_more1.setVisibility(View.INVISIBLE);
				btn_more.setVisibility(View.VISIBLE);
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				mDrawerLayout.setVisibility(View.INVISIBLE);
			}

		});
	}

	private void initialize() {
		// TODO Auto-generated method stub
		rl_home = (RelativeLayout) findViewById(R.id.rl_home);
		rest = new ArrayList<Restgetset>();
		rest1 = new ArrayList<Restgetset1>();
		list_detail = (ListView) findViewById(R.id.list_detail);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setVisibility(View.INVISIBLE);
		// btn_fvrts = (Button) findViewById(R.id.btn_fvrts);
		btn_map = (Button) findViewById(R.id.btn_map);
	}

	// User update class
	public class getupdatedetail extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {
			URL hp = null;
			try {

				hp = new URL(getString(R.string.liveurl)
						+ "updateuser.php?username=" + updatename + "&&email="
						+ updateemail);

				// hp = new URL(
				// "http://192.168.1.106/restourant/updateuser.php?username="
				// + updatename + "&&email=" + updateemail);

				Log.d("userurl", "" + hp);
				URLConnection hpCon = hp.openConnection();
				hpCon.connect();
				InputStream input = hpCon.getInputStream();
				Log.d("input", "" + input);

				BufferedReader r = new BufferedReader(new InputStreamReader(
						input));

				String x = "";
				// x = r.readLine();
				String total = "";

				while (x != null) {
					total += x;
					x = r.readLine();
				}
				Log.d("totalid", "" + total);

				JSONArray j = new JSONArray(total);
				Log.d("j", "" + j);
				for (int i = 0; i < j.length(); i++) {
					JSONObject Obj;
					Obj = j.getJSONObject(i);
					Restgetset temp = new Restgetset();
					temp.setId(Obj.getString("id"));

					rest.add(temp);
				}
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
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);

		}

	}

	// restaurant detail class
	public class getrestaudetail extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(Home.this);
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
							Home.this, R.anim.popup));
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

					LazyAdapter lazy = new LazyAdapter(Home.this, rest);
					lazy.notifyDataSetChanged();
					list_detail.setAdapter(lazy);

				}
				list_detail.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						// Toast.makeText(Home.this, "hi",
						// Toast.LENGTH_LONG).show();
						MainPosition = 1;
						pos = position;
						if (interstitialCanceled) {

						} else {

							if (mInterstitialAd != null
									&& mInterstitialAd.isLoaded()) {
								mInterstitialAd.show();
							} else {

								ContinueIntent();
							}
						}

					}
				});
			}

		}

	}

	// getting data for home page(restaurant detail)
	private void getdetailforNearMe() {
		// TODO Auto-generated method stub

		URL hp = null;
		try {
			rest.clear();
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
				// temp.setFoodtype(Obj.getString("foodtype"));
				convertmiles(Obj.getString("latitude"),
						Obj.getString("longitude"));

				Log.d("dd", "" + miles);
				temp.setMiles(miles);
				rest.add(temp);

			}
			// sorting data from miles wise in home page list
			Collections.sort(rest, new Comparator<Restgetset>() {

				@Override
				public int compare(Restgetset lhs, Restgetset rhs) {
					// TODO Auto-generated method stub
					return Double.compare(lhs.getMiles(), rhs.getMiles());
				}
			});
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	// binding data in listview usind adapter class
	public class LazyAdapter extends BaseAdapter {

		private Activity activity;
		private ArrayList<Restgetset> data;
		private LayoutInflater inflater = null;
		Typeface tf = Typeface.createFromAsset(Home.this.getAssets(),
				"fonts/OpenSans-Regular.ttf");
		private int lastPosition = -1;

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

				vi = inflater.inflate(R.layout.restcell, null);
			}

			// Animation animation = AnimationUtils.loadAnimation(Home.this,
			// (position > lastPosition) ? R.anim.listview1
			// : R.anim.listview);
			// vi.startAnimation(animation);
			// lastPosition = position;

			TextView txt_km = (TextView) vi.findViewById(R.id.txt_km);
			txt_km.setText(data.get(position).getMiles() + " " + "miles");
			txt_km.setTypeface(tf);

			TextView txt_rname = (TextView) vi.findViewById(R.id.txt_rest_name);
			txt_rname.setText(data.get(position).getName());
			txt_rname.setTypeface(tf);

			TextView txt_add = (TextView) vi.findViewById(R.id.txt_address);
			txt_add.setText(data.get(position).getAddress()
					+ data.get(position).getZipcode());
			txt_add.setTypeface(tf);

			ImageView img_veg = (ImageView) vi.findViewById(R.id.img_veg);
			ImageView img_nonveg = (ImageView) vi.findViewById(R.id.img_nonveg);
			img_nonveg.setVisibility(View.INVISIBLE);
			img_veg.setVisibility(View.INVISIBLE);
			String veg = data.get(position).getVegtype();
			if (veg.equals("Veg")) {
				img_nonveg.setVisibility(View.VISIBLE);
				img_nonveg.setBackgroundResource(R.drawable.veg_icon);
			} else if (veg.equals("Nonveg")) {
				img_nonveg.setVisibility(View.VISIBLE);
				img_nonveg.setBackgroundResource(R.drawable.nonveg_icon);
			} else {
				img_nonveg.setVisibility(View.VISIBLE);
				img_veg.setVisibility(View.VISIBLE);
				img_nonveg.setBackgroundResource(R.drawable.nonveg_icon);
				img_veg.setBackgroundResource(R.drawable.veg_icon);
			}

			String image = data.get(position).getThubnailimage();
			Log.d("image", "" + image);
			ImageView programImage = (ImageView) vi
					.findViewById(R.id.img_restau);
			programImage.setImageResource(R.drawable.home_page_cell_img);
			new DownloadImageTask(programImage)
					.execute(getString(R.string.liveurl) + "uploads/" + image);

			// new DownloadImageTask(programImage)
			// .execute("http://192.168.1.106/restourant/uploads/" + image);

			RatingBar ratb = (RatingBar) vi.findViewById(R.id.rate1);
			ratb.setFocusable(false);
			ratb.setRating(Float.parseFloat(data.get(position).getRatting()));
			ratb.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

				@Override
				public void onRatingChanged(RatingBar ratingBar, float rating,
						boolean fromUser) { // TODO Auto-generated method stub
					rate = rating;
					Log.d("rate", "" + rating);
				}

			});

			return vi;
		}
	}

	// convert latitude and longitude to km
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

	// convert km to miles
	public static double convertmiles(String value, String places) {
		double earthRadius = 6371000; // meters
		double dLat = Math.toRadians(21.2049 - Double.parseDouble(value));
		double dLng = Math.toRadians(72.8406 - Double.parseDouble(places));
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
		miles = d / 1.6;
		return miles;
	}

	// download image from url
	class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;
		Bitmap mIcon11;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
			// viewReference = new WeakReference<ImageView>( view );
		}

		@Override
		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];

			try {
				// BitmapFactory.Options options = new BitmapFactory.Options();
				// options.inSampleSize = 8;
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
				mIcon11 = Bitmap.createScaledBitmap(mIcon11, 72, 55, true);
			} catch (Exception e) {
				Log.e("Error", "" + e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (bmImage != null) {
				bmImage.setImageBitmap(result);
			}

		}
	}

	// category serach class
	public class getrestaudetail1 extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(Home.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			URL hp = null;
			try {
				rest1.clear();
				if (foodtype != null) {
					hp = new URL(getString(R.string.liveurl)
							+ "restaurantdetail.php?search=" + foodtype);
					// hp = new URL(
					// "http://192.168.1.106/restourant/restaurantdetail.php?search="
					// + foodtype);
				} else {
					hp = new URL(getString(R.string.liveurl)
							+ "restaurantdetail.php?search=" + search);
					// hp = new URL(
					// "http://192.168.1.106/restourant/restaurantdetail.php?search="
					// + search);
				}

				Log.d("URL", "" + hp);
				URLConnection hpCon = hp.openConnection();
				hpCon.connect();
				InputStream input = hpCon.getInputStream();
				Log.d("input", "" + input);

				BufferedReader r = new BufferedReader(new InputStreamReader(
						input));

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

					Restgetset1 temp = new Restgetset1();
					String idobj = temp.getId();
					Log.d("idobj", "" + idobj);

					temp.setId(Obj.getString("id"));
					temp.setName(Obj.getString("name"));

					temp.setAddress(Obj.getString("address"));
					temp.setLatitude(Obj.getString("latitude"));
					temp.setLongitude(Obj.getString("longitude"));
					temp.setRatting(Obj.getString("ratting"));
					temp.setZipcode(Obj.getString("zipcode"));
					temp.setThumbnailimage(Obj.getString("thumbnailimage"));
					temp.setVegtype(Obj.getString("vegtype"));
					temp.setTotalreview(Obj.getString("totalreview"));
					rest1.add(temp);

				}

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

			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			list_detail = (ListView) findViewById(R.id.list_detail);

			LazyAdapter1 lazy = new LazyAdapter1(Home.this, rest1);
			lazy.notifyDataSetChanged();
			list_detail.setAdapter(lazy);

			list_detail.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					// Toast.makeText(Home.this, "hi",
					// Toast.LENGTH_LONG).show();
					MainPosition = 2;
					pos = position;
					if (interstitialCanceled) {

					} else {

						if (mInterstitialAd != null
								&& mInterstitialAd.isLoaded()) {
							mInterstitialAd.show();
						} else {

							ContinueIntent();
						}
					}
					// Intent iv = new Intent(Home.this, Detailpage.class);
					// iv.putExtra("rating", "" +
					// rest1.get(position).getRatting());
					// iv.putExtra("name", "" + rest1.get(position).getName());

					// iv.putExtra("id", "" + rest1.get(position).getId());
					// startActivity(iv);
				}
			});

		}

	}

	// search category bind in listview
	public class LazyAdapter1 extends BaseAdapter {

		private Activity activity;
		private ArrayList<Restgetset1> data;
		private LayoutInflater inflater = null;
		Typeface tf = Typeface.createFromAsset(Home.this.getAssets(),
				"fonts/OpenSans-Regular.ttf");

		public LazyAdapter1(Activity a, ArrayList<Restgetset1> str) {
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

				vi = inflater.inflate(R.layout.restcell, null);
			}
			// Getsetlist Obj = FileList.get(position);
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
			miles = d / 1.6;
			TextView txt_km = (TextView) vi.findViewById(R.id.txt_km);
			txt_km.setText(miles + " " + "miles");
			txt_km.setTypeface(tf);

			TextView txt_rname = (TextView) vi.findViewById(R.id.txt_rest_name);
			txt_rname.setText(data.get(position).getName());
			txt_rname.setTypeface(tf);

			TextView txt_add = (TextView) vi.findViewById(R.id.txt_address);
			txt_add.setText(data.get(position).getAddress() + " "
					+ data.get(position).getZipcode());
			txt_add.setTypeface(tf);

			ImageView img_veg = (ImageView) vi.findViewById(R.id.img_veg);
			ImageView img_nonveg = (ImageView) vi.findViewById(R.id.img_nonveg);
			img_nonveg.setVisibility(View.INVISIBLE);
			img_veg.setVisibility(View.INVISIBLE);
			String veg = data.get(position).getVegtype();
			if (veg.equals("Veg")) {
				img_nonveg.setVisibility(View.VISIBLE);
				img_nonveg.setBackgroundResource(R.drawable.veg_icon);
			} else if (veg.equals("Nonveg")) {
				img_nonveg.setVisibility(View.VISIBLE);
				img_nonveg.setBackgroundResource(R.drawable.nonveg_icon);
			} else {
				img_nonveg.setVisibility(View.VISIBLE);
				img_veg.setVisibility(View.VISIBLE);
				img_nonveg.setBackgroundResource(R.drawable.nonveg_icon);
				img_veg.setBackgroundResource(R.drawable.veg_icon);
			}

			String image = data.get(position).getThumbnailimage();
			Log.d("image", "" + image);

			new DownloadImageTask((ImageView) vi.findViewById(R.id.img_restau))
					.execute(getString(R.string.liveurl) + "uploads/" + image);

			// new DownloadImageTask((ImageView)
			// vi.findViewById(R.id.img_restau))
			// .execute("http://192.168.1.106/restourant/uploads/" + image);

			RatingBar ratb = (RatingBar) vi.findViewById(R.id.rate1);
			ratb.setFocusable(false);
			ratb.setRating(Float.parseFloat(data.get(position).getRatting()));
			ratb.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

				@Override
				public void onRatingChanged(RatingBar ratingBar, float rating,
						boolean fromUser) { // TODO Auto-generated method stub
					rate = rating;
					Log.d("rate", "" + rating);
				}

			});

			return vi;
		}
	}

	/*
	 * @Override protected void onPostCreate(Bundle savedInstanceState) {
	 * super.onPostCreate(savedInstanceState); mDrawerToggle.syncState(); }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// class for create user
	public class getuserdetail extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {
			URL hp = null;
			try {

				hp = new URL(getString(R.string.liveurl)
						+ "adduser.php?username=" + createusername + "&&email="
						+ createusermail);

				// hp = new URL(
				// "http://192.168.1.106/restourant/adduser.php?username="
				// + createusername + "&&email=" + createusermail);

				Log.d("userurl", "" + hp);
				URLConnection hpCon = hp.openConnection();
				hpCon.connect();
				InputStream input = hpCon.getInputStream();
				Log.d("input", "" + input);

				BufferedReader r = new BufferedReader(new InputStreamReader(
						input));

				String x = "";
				// x = r.readLine();
				String total = "";

				while (x != null) {
					total += x;
					x = r.readLine();
				}
				Log.d("totalid", "" + total);

				JSONArray j = new JSONArray(total);
				Log.d("j", "" + j);
				for (int i = 0; i < j.length(); i++) {
					JSONObject Obj;
					Obj = j.getJSONObject(i);
					Restgetset temp = new Restgetset();
					temp.setId(Obj.getString("id"));
					user2 = Obj.getString("id");
					rest.add(temp);
				}
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
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);

			Log.d("user1", "" + user2);

			SharedPreferences.Editor editor = getSharedPreferences(
					MY_PREFS_NAME, MODE_PRIVATE).edit();
			editor.putString("score", "" + user2);
			editor.commit();
		}

	}

	@Override
	public void onBackPressed() {

		/*
		 * if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
		 * this.mDrawerLayout.closeDrawer(GravityCompat.START); // finish();
		 * 
		 * 
		 * } else { super.onBackPressed();
		 * 
		 * }
		 */

		AlertDialog.Builder builder1 = new AlertDialog.Builder(Home.this);
		builder1.setTitle("Quit?");
		builder1.setMessage("Are you sure you want to quit?.");
		builder1.setCancelable(true);
		builder1.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// Home.this.finish();
						finishAffinity();

					}
				});
		builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		AlertDialog alert11 = builder1.create();
		alert11.show();

		// super.onBackPressed();

	}

	private void showAlertDialog(Context applicationContext, String title,
			String message, boolean status) {
		// TODO Auto-generated method stub
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(message);

		// Setting alert dialog icon
		// alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		super.onResume();

		interstitialCanceled = false;
		CallNewInsertial();

	}

	private void CallNewInsertial() {
		cd = new ConnectionDetector(Home.this);

		if (!cd.isConnectingToInternet()) {
			alert.showAlertDialog(Home.this, "Internet Connection Error",
					"Please connect to working Internet connection", false);
			return;
		} else {
			// AdView mAdView = (AdView) findViewById(R.id.adView);
			// AdRequest adRequest = new AdRequest.Builder().build();
			// mAdView.loadAd(adRequest);
			if (getString(R.string.Home_show_ads).equals("yes")) {
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
				ContinueIntent();
			}

		}
	}

	private void ContinueIntent() {
		if (MainPosition == 1) {
			Intent iv = new Intent(Home.this, Detailpage.class);
			iv.putExtra("rating", "" + rest.get(pos).getRatting());
			iv.putExtra("name", "" + rest.get(pos).getName());
			// iv.putExtra("foodtype", "" + rest.get(pos).getFoodtype());
			iv.putExtra("id", "" + rest.get(pos).getId());
			startActivity(iv);
		} else if (MainPosition == 2) {
			Intent iv = new Intent(Home.this, Detailpage.class);
			iv.putExtra("rating", "" + rest1.get(pos).getRatting());
			iv.putExtra("name", "" + rest1.get(pos).getName());
			// iv.putExtra("foodtype", "" + rest.get(pos).getFoodtype());
			iv.putExtra("id", "" + rest1.get(pos).getId());
			startActivity(iv);
		}

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
