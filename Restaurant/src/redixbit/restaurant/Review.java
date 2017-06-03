package redixbit.restaurant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import redixbit.getset.Restgetset;
import redixbit.getset.User;
import redixbit.util.ConnectionDetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Review extends Activity {
	ListView list_review;
	String[] str = { "abc", "def", "pqr" };
	Float rate = 0.0f;
	Button btn_add;
	RelativeLayout rl_adddialog;
	RelativeLayout rl_userdialog;
	String id;
	ArrayList<Restgetset> rest;
	ArrayList<User> rest1;
	public static final String MY_PREFS_NAME = "Restaurant";
	Vector<String> bgName;
	EditText edt_nameuser, edt_usermail, edt_comment;
	String username, useremailid, userrate, usercomment;
	RatingBar rb1234;
	String user1, user2, uservalue, updateuname, updateumail;
	ProgressDialog progressDialog;
	LayoutInflater inflater = null;
	View layout12;
	RelativeLayout rl_home, rl_back;
	ImageView img_back;
	String emailpattern;
	Boolean isInternetPresent = false;
	ConnectionDetector cd;
	private String Error = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_review);

		cd = new ConnectionDetector(getApplicationContext());

		isInternetPresent = cd.isConnectingToInternet();
		// check for Internet status
		if (isInternetPresent) {
			if (getString(R.string.review_banner).equals("yes")) {
				AdView mAdView = (AdView) findViewById(R.id.adView);
				AdRequest adRequest = new AdRequest.Builder().build();
				mAdView.loadAd(adRequest);
			}
			emailpattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
			initialize();

			Intent iv = getIntent();
			id = iv.getStringExtra("id");
			Log.d("id123", "" + id);

			new getreviewdetail().execute();
			initialisedialog();
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
						Review.this, R.anim.popup));
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
		rl_home = (RelativeLayout) findViewById(R.id.rl_home);
		rl_back = (RelativeLayout) findViewById(R.id.rl_back);
		img_back = (ImageView) findViewById(R.id.img_back);
		bgName = new Vector<String>();
		rest = new ArrayList<Restgetset>();
		rest1 = new ArrayList<User>();
	}

	public void initialisedialog() {

		btn_add = (Button) findViewById(R.id.btn_add);

		// on click of add button
		btn_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layout12 = v;

				list_review.setEnabled(false);
				SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME,
						MODE_PRIVATE);

				// check user is created or not
				if (prefs.getString("score", null) != null) {
					uservalue = prefs.getString("score", null);
					Log.d("user3", uservalue);

					if (rl_back == null) {
						RelativeLayout rl_dialoguser = (RelativeLayout) findViewById(R.id.rl_infodialog);
						layout12 = getLayoutInflater().inflate(
								R.layout.giverate, rl_dialoguser, false);
						rl_dialoguser.addView(layout12);
						rl_dialoguser.setAlpha(1);
						img_back = (ImageView) findViewById(R.id.img_back);
						img_back.setAlpha(0.8f);
						// rl_home.setAlpha(0.5f);

						edt_comment = (EditText) layout12
								.findViewById(R.id.txt_description);
						rb1234 = (RatingBar) layout12
								.findViewById(R.id.rate1234);
						Button btn_submit = (Button) layout12
								.findViewById(R.id.btn_submit);
						btn_submit.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								// rl_adddialog.setVisibility(View.GONE);
								layout12 = v;
								list_review.setEnabled(true);
								// rl_home.setAlpha(1.0f);

								try {
									usercomment = edt_comment.getText()
											.toString().replace(" ", "%20");
									userrate = String.valueOf(rb1234
											.getRating());
									if (usercomment.equals(null)) {
										usercomment = "";
									}
								} catch (NullPointerException e) {
									// TODO: handle exception
								}

								Log.d("comment", "" + usercomment);
								Log.d("rate", "" + userrate);

								new getratedetail().execute();

								AlertDialog.Builder builder = new AlertDialog.Builder(
										Review.this);
								builder.setMessage(
										"Thank you for your feedback")
										.setTitle("Thanks..");

								builder.setNeutralButton(android.R.string.ok,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int id) {
												dialog.cancel();
												new getreviewdetail().execute();
											}
										});
								AlertDialog alert = builder.create();
								alert.show();
								View myView = findViewById(R.id.rl_back);
								ViewGroup parent = (ViewGroup) myView
										.getParent();
								parent.removeView(myView);
								img_back = (ImageView) findViewById(R.id.img_back);

							}
						});

						Button btn_cancel = (Button) layout12
								.findViewById(R.id.btn_cancel);
						btn_cancel.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								list_review.setEnabled(true);
								View myView = findViewById(R.id.rl_back);
								ViewGroup parent = (ViewGroup) myView
										.getParent();
								parent.removeView(myView);
								img_back = (ImageView) findViewById(R.id.img_back);

							}
						});

					}

				} else {

					if (rl_back == null) {
						RelativeLayout rl_dialog = (RelativeLayout) findViewById(R.id.rl_infodialog);
						layout12 = getLayoutInflater().inflate(
								R.layout.dialog_create, rl_dialog, false);
						rl_dialog.addView(layout12);

						img_back = (ImageView) findViewById(R.id.img_back);
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

										username = edt_nameuser.getText()
												.toString();

										useremailid = edt_usermail.getText()
												.toString();

										if (useremailid.matches(emailpattern)
												&& useremailid.length() > 0) {
											if (username.equals("")) {
												edt_nameuser
														.setError("Enter User Name");
											} else {
												SharedPreferences.Editor editor = getSharedPreferences(
														MY_PREFS_NAME,
														MODE_PRIVATE).edit();
												editor.putString("username", ""
														+ username);
												editor.putString("usermailid",
														"" + useremailid);
												editor.commit();

												new getuserdetail().execute();
												View myView = findViewById(R.id.rl_back);
												ViewGroup parent = (ViewGroup) myView
														.getParent();
												parent.removeView(myView);
												img_back = (ImageView) findViewById(R.id.img_back);

												rl_back = (RelativeLayout) findViewById(R.id.rl_back);
												if (rl_back == null) {
													RelativeLayout rl_dialog = (RelativeLayout) findViewById(R.id.rl_infodialog);
													layout12 = getLayoutInflater()
															.inflate(
																	R.layout.giverate,
																	rl_dialog,
																	false);
													rl_dialog.addView(layout12);
													rl_dialog.setAlpha(1);

													img_back = (ImageView) findViewById(R.id.img_back);
													img_back.setAlpha(0.8f);
												}
												edt_comment = (EditText) layout12
														.findViewById(R.id.txt_description);
												rb1234 = (RatingBar) layout12
														.findViewById(R.id.rate1234);
												Button btn_submit = (Button) layout12
														.findViewById(R.id.btn_submit);
												btn_submit
														.setOnClickListener(new OnClickListener() {

															@Override
															public void onClick(
																	View v) {
																// TODO
																// Auto-generated
																// method stub

																layout12 = v;

																View myView = findViewById(R.id.rl_back);
																ViewGroup parent = (ViewGroup) myView
																		.getParent();
																parent.removeView(myView);
																img_back = (ImageView) findViewById(R.id.img_back);

																try {
																	usercomment = edt_comment
																			.getText()
																			.toString()
																			.replace(
																					" ",
																					"%20");
																	if (usercomment
																			.equals(null)) {
																		usercomment = "";
																	}
																	userrate = String
																			.valueOf(rb1234
																					.getRating());
																} catch (NullPointerException e) {
																	// TODO:
																	// handle
																	// exception
																	e.printStackTrace();
																}

																Log.d("comment",
																		""
																				+ usercomment);
																Log.d("rate",
																		""
																				+ userrate);

																new getratedetail()
																		.execute();

																AlertDialog.Builder builder = new AlertDialog.Builder(
																		Review.this);
																builder.setMessage(
																		"Thank you for giving your feedback")
																		.setTitle(
																				"Thanks");

																builder.setNeutralButton(
																		android.R.string.ok,
																		new DialogInterface.OnClickListener() {
																			@Override
																			public void onClick(
																					DialogInterface dialog,
																					int id) {
																				dialog.cancel();

																				new getreviewdetail()
																						.execute();
																			}
																		});
																AlertDialog alert = builder
																		.create();
																alert.show();

															}

														});

												Button btn_cancel = (Button) layout12
														.findViewById(R.id.btn_cancel);
												btn_cancel
														.setOnClickListener(new OnClickListener() {

															@Override
															public void onClick(
																	View v) {
																// TODO
																// Auto-generated
																// method stub

																View myView = findViewById(R.id.rl_back);
																ViewGroup parent = (ViewGroup) myView
																		.getParent();
																parent.removeView(myView);
																img_back = (ImageView) findViewById(R.id.img_back);
																// img_back.setAlpha(0.0f);
															}
														});
											}

										} else {
											edt_usermail
													.setError("Enter valid email address");
										}

									}
								});

					}

				}

			}

		});

	}

	public class getratedetail extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {
			URL hp = null;
			try {
				if (uservalue != null) {
					 hp = new URL(getString(R.string.liveurl)+
					 "userfeedback.php?res_id="
					 + id + "&&user_id=" + uservalue
					 + "&&ratting=" + userrate + "&&comment="
					 + usercomment);

					//hp = new URL(
					//		"http://192.168.1.106/restourant/userfeedback.php?res_id="
					//				+ id + "&&user_id=" + uservalue
					//				+ "&&ratting=" + userrate + "&&comment="
					//				+ usercomment);
				} else {
					 hp = new URL(getString(R.string.liveurl)+
					 "userfeedback.php?res_id="
					 + id + "&&user_id=" + user2 + "&&ratting="
					 + userrate + "&&comment=" + usercomment);

					//hp = new URL(
					//		"http://192.168.1.106/restourant/userfeedback.php?res_id="
					//				+ id + "&&user_id=" + user2 + "&&ratting="
					//				+ userrate + "&&comment=" + usercomment);
				}

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

				JSONObject j = new JSONObject("Massage");

				Log.d("j", "" + j);
				for (int i = 0; i < j.length(); i++) {
					JSONObject Obj;
					Obj = j.getJSONObject(String.valueOf(i));
					User temp = new User();
					temp.setId(Obj.getString("id"));

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

		}

	}

	public class getuserdetail extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {
			URL hp = null;
			try {

				 hp = new URL(getString(R.string.liveurl)+
				 "adduser.php?username="
				 + username + "&&email=" + useremailid);

				//hp = new URL(
				//		"http://192.168.1.106/restourant/adduser.php?username="
				//				+ username + "&&email=" + useremailid);

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
					User temp = new User();
					temp.setId(Obj.getString("id"));

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
			// if (progressDialog.isShowing()) {
			// progressDialog.dismiss();
			int start = 0;
			User user = rest1.get(start);
			user2 = user.getId();
			Log.d("user1", "" + user2);

			SharedPreferences.Editor editor = getSharedPreferences(
					MY_PREFS_NAME, MODE_PRIVATE).edit();
			editor.putString("score", "" + user2);
			editor.commit();
		}

	}

	public class getreviewdetail extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(Review.this);
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

			// }
			// else {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
				int temp = 1;
				for (int i = 0; i < rest.size(); i++) {

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
				list_review = (ListView) findViewById(R.id.list_review);
				LazyAdapter lazy = new LazyAdapter(Review.this, rest);
				list_review.setAdapter(lazy);

				list_review.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						list_review.setEnabled(false);
						if (rl_back == null) {
							RelativeLayout rl_dialog = (RelativeLayout) findViewById(R.id.rl_infodialog);
							layout12 = getLayoutInflater().inflate(
									R.layout.reviewclick_dialog, rl_dialog,
									false);
							rl_dialog.addView(layout12);

							img_back = (ImageView) findViewById(R.id.img_back);
							img_back.setAlpha(0.8f);
							rl_dialog.setAlpha(1);

							TextView txt_name_comment = (TextView) layout12
									.findViewById(R.id.txt_nameuser);
							txt_name_comment.setText(""
									+ rest.get(position).getTitle());
							try {
								RatingBar rb = (RatingBar) layout12
										.findViewById(R.id.rate1234);
								rb.setRating(Float.parseFloat(rest
										.get(position).getRatting()));
							} catch (NumberFormatException e) {
								// TODO: handle exception
							}

							TextView txt_comment_desc = (TextView) layout12
									.findViewById(R.id.txt_comment_desc);
							txt_comment_desc.setText(""
									+ rest.get(position).getAddress());

							Button btn_ok = (Button) layout12
									.findViewById(R.id.btn_ok);
							btn_ok.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									list_review.setEnabled(true);
									ImageView img_back = (ImageView) findViewById(R.id.img_back);
									img_back.setAlpha(0.0f);
									View myView = findViewById(R.id.rl_back);
									ViewGroup parent = (ViewGroup) myView
											.getParent();
									parent.removeView(myView);
								}
							});
						}
					}
				});
			}
			// }

		}

	}

	private void getdetailforNearMe() {
		// TODO Auto-generated method stub

		URL hp = null;
		try {
			rest.clear();
			 hp = new URL(getString(R.string.liveurl)+
			 "userfeedback.php?value="
			 + id);

			//hp = new URL(
			//		"http://192.168.1.106/restourant/userfeedback.php?value="
			//				+ id);

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
			JSONArray j = jObject.getJSONArray("userfeedback");

			Log.d("URL1", "" + j);
			for (int i = 0; i < j.length(); i++) {

				JSONObject Obj;
				Obj = j.getJSONObject(i);

				Restgetset temp = new Restgetset();
				temp.setTitle(Obj.getString("username"));
				temp.setAddress(Obj.getString("comment"));
				temp.setRatting(Obj.getString("ratting"));

				rest.add(temp);
			}

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

	public class LazyAdapter extends BaseAdapter {

		private Activity activity;
		private ArrayList<Restgetset> data;
		private LayoutInflater inflater = null;
		Typeface tf1 = Typeface.createFromAsset(Review.this.getAssets(),
				"fonts/OpenSans-Regular.ttf");
		String s;

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

				// vi = inflater.inflate(R.layout.reviewcell, null);
				String temp = bgName.get(position);
				if (temp.equals("RED")) {
					vi = inflater.inflate(R.layout.reviewcell, null);
				} else if (temp.equals("BLUE")) {
					vi = inflater.inflate(R.layout.reviewcell1, null);
				} else if (temp.equals("GREEN")) {
					vi = inflater.inflate(R.layout.reviewcell2, null);
				}
			}

			try {
				String namefirst = data.get(position).getTitle();
				s = namefirst.substring(0, 1).toUpperCase();

				TextView txt_first = (TextView) vi.findViewById(R.id.txt_first);
				txt_first.setText("" + s);
			} catch (StringIndexOutOfBoundsException e) {
				// TODO: handle exception
			}

			TextView txt_name = (TextView) vi.findViewById(R.id.text_name);
			txt_name.setText(data.get(position).getTitle());
			txt_name.setTypeface(tf1);

			TextView txt_comment = (TextView) vi.findViewById(R.id.txt_review);
			txt_comment.setText(data.get(position).getAddress());
			txt_comment.setTypeface(tf1);
			try {
				RatingBar rb = (RatingBar) vi.findViewById(R.id.rate1);
				rb.setRating(Float.parseFloat(data.get(position).getRatting()));
			} catch (NumberFormatException e) {
				// TODO: handle exception
			}

			return vi;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.review, menu);
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
