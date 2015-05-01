package de.kauz.starcitizen.informer.fragments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.activities.Main;
import de.kauz.starcitizen.informer.model.ShipComponent;
import de.kauz.starcitizen.informer.model.ShipSubComponent;
import de.kauz.starcitizen.informer.utils.ImageDownload;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.MyApp;
import de.kauz.starcitizen.informer.utils.Translator;
import de.kauz.starcitizen.informer.utils.ViewHelper;
import de.kauz.starcitizen.informer.utils.ZoomableOverlayImage;
import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * A view containing a ship selector and ship information.
 * 
 * @author MadKauz
 * 
 */
public class Ships extends Fragment {

	private TextView shipsLoad;
	private ZoomableOverlayImage shipsImage;
	private ProgressBar shipsImageProgress;
	private Spinner shipSelectionSpinner;
	private JSONArray shipArray = null;
	private ScrollView shipsInfoContainer;

	private LinearLayout shipContainer;

	private int selectedIndex = 0;

	private Typeface font;

	/**
	 * LifeCycle Fragment onCreateView(..)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_ships, container, false);

	}

	@Override
	public void onResume() {
		super.onResume();
		MyApp.getInstance().setCurrentFragment(this);
	}

	/**
	 * LifeCycle Fragment onActivityCreated(..)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		this.shipsLoad = (TextView) getView().findViewById(R.id.shipsLoading);
		this.shipSelectionSpinner = (Spinner) getView().findViewById(
				R.id.shipsSelectionSpinner);
		this.shipsImage = (ZoomableOverlayImage) getView().findViewById(
				R.id.shipsImage);
		this.shipsImageProgress = (ProgressBar) getView().findViewById(
				R.id.shipsImageProgress);
		this.shipsInfoContainer = (ScrollView) getView().findViewById(
				R.id.shipsInfoContainer);

		this.shipContainer = (LinearLayout) getView().findViewById(
				R.id.shipsContainerLayout);

		this.font = Typeface.createFromAsset(getActivity().getAssets(),
				"Electrolize-Regular.ttf");
		this.shipsLoad.setTypeface(font);

		if (MyApp.getInstance().isOnline(getActivity())) {
			DownloadInfoFromSite download = new DownloadInfoFromSite();
			download.execute(InformerConstants.URL_SHIP_DATA);
		} else {
			// already downloaded and no internet connection
			readDownload();
		}

		Main act = (Main) getActivity();
		act.getSupportActionBar().setTitle(InformerConstants.MENU_ITEMS[7]);
		act.getSupportActionBar().setIcon(InformerConstants.MENU_ICONS[7]);
	}

	/**
	 * Initialize the ship selection spinner.
	 * 
	 * @param titles
	 *            to be selected through spinner
	 */
	private void initSpinner(String[] titles) {
		if (titles != null && getActivity() != null) {
			this.shipSelectionSpinner.setVisibility(View.VISIBLE);

			ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(
					getActivity(), R.layout.spinner_item, titles);
			spinnerAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			this.shipSelectionSpinner.setAdapter(spinnerAdapter);

			this.shipSelectionSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							selectedIndex = position;
							selectShip(selectedIndex);
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
						}
					});
		}
	}

	/**
	 * Reads the downloaded ship file from storage and parses its info.
	 */
	private void readDownload() {
		StringBuilder builder = readFileFromStorage(InformerConstants.SHIP_DATA_FILENAME);

		if (builder != null) {

			String startCriteria = "data:";
			int startShipData = builder.indexOf(startCriteria);
			builder.replace(0, startShipData + startCriteria.length(), "");

			String endCriteria = "]}],";
			int endShipData = builder.indexOf(endCriteria);
			builder.replace(endShipData + endCriteria.length() - 1,
					builder.length(), "");

			JSONTokener tokener = new JSONTokener(builder.toString());
			try {

				shipArray = new JSONArray(tokener);
				ArrayList<String> titles = new ArrayList<String>();
				for (int i = 0; i < shipArray.length(); i++) {
					try {
						JSONObject titleObject = shipArray.getJSONObject(i);
						String title = titleObject.getString("name");
						titles.add(title);
					} catch (JSONException e) {
					}
				}
				String[] spinnerTitles = new String[titles.size()];
				for (int j = 0; j < titles.size(); j++) {
					spinnerTitles[j] = titles.get(j);
				}
				if (spinnerTitles != null) {
					initSpinner(spinnerTitles);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Use to build retrieve the info of the ship file from the storage.
	 * 
	 * @param fileName
	 *            the name of the file
	 * @return the String saved in the file
	 */
	private StringBuilder readFileFromStorage(String fileName) {
		StringBuilder builder = null;
		String downloadedFileName = Environment.getExternalStorageDirectory()
				.getPath() + "/" + fileName;
		File file = new File(downloadedFileName);
		if (!file.exists()) {
			MyApp.getInstance().showError(getActivity(),
					getResources().getString(R.string.errorReadDB));
		} else {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				builder = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					try {
						builder.append(line);
					} catch (OutOfMemoryError ex) {
						ex.printStackTrace();
						MyApp.getInstance().showError(
								getActivity(),
								getResources().getString(
										R.string.errorMemoryImageProblems));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return builder;
	}

	/**
	 * Selects a ship to parse from the downloaded ship file.
	 * 
	 * @param index
	 *            of the selected ship
	 */
	private void selectShip(int index) {
		if (shipArray != null) {

			shipsInfoContainer.scrollTo(0, 0);

			this.shipsImage.setImageBitmap(null);
			ViewHelper.fadeIn(shipsInfoContainer);

			shipContainer.removeAllViews();

			try {
				JSONObject titleObject = shipArray.getJSONObject(index);

				String description = titleObject.getString("description");
				String role = titleObject.getString("focus");
				String length = titleObject.getString("length");
				String beam = titleObject.getString("beam");
				String mass = titleObject.getString("mass");
				String height = titleObject.getString("height");
				String cargocapacity = titleObject.getString("cargocapacity");
				String maxcrew = titleObject.getString("maxcrew");
				String maxpowerplant = titleObject
						.getString("maxpowerplantsize");
				String maxshield = titleObject.getString("maxshieldsize");

				JSONObject manufacturerObj = titleObject
						.getJSONObject("manufacturer");
				String manufacturer = manufacturerObj.getString("name");

				JSONArray media = titleObject.getJSONArray("media");
				JSONObject medImg = media.getJSONObject(0);
				String imageUrl = medImg.getString("source_url");

				constructDivider();
				constructInfo("Manufacturer", manufacturer, getResources()
						.getColor(R.color.yellowOrange), shipContainer);
				constructInfo("Role", role,
						getResources().getColor(R.color.white), shipContainer);
				constructInfo("Description", description, getResources()
						.getColor(R.color.bluishGrey), shipContainer);

				constructDivider();
				constructInfo("Length", length + " m",
						getResources().getColor(R.color.white), shipContainer);
				constructInfo("Beam", beam + " m",
						getResources().getColor(R.color.white), shipContainer);
				constructInfo("Height", height + " m",
						getResources().getColor(R.color.white), shipContainer);
				constructInfo("Mass", mass + " Kg",
						getResources().getColor(R.color.white), shipContainer);

				constructDivider();
				constructInfo("Cargo Capacity", cargocapacity
						+ " freight units",
						getResources().getColor(R.color.white), shipContainer);
				constructInfo("Max Crew", maxcrew + " person(s)",
						getResources().getColor(R.color.white), shipContainer);
				constructInfo("Max Power Plant Size", maxpowerplant,
						getResources().getColor(R.color.white), shipContainer);
				constructInfo("Max Shield", maxshield,
						getResources().getColor(R.color.white), shipContainer);

				JSONArray prop = titleObject.getJSONArray("propulsion");

				if (prop.length() > 0) {
					constructDivider();
					ArrayList<ShipComponent> propComponents = new ArrayList<ShipComponent>();

					for (int i = 0; i < prop.length(); i++) {
						try {
							JSONObject componentObject = prop.getJSONObject(i);
							String title = componentObject.getString("name");
							String type = componentObject.getString("type");
							String rating = componentObject.getString("rating");

							JSONObject subCompObj = componentObject
									.getJSONObject("component");
							String subtitle = subCompObj.getString("name");
							String subtype = subCompObj.getString("type");
							String subsize = subCompObj.getString("size");
							ShipSubComponent subComponent = new ShipSubComponent(
									subtitle, subtype, subsize);

							ShipComponent comp = new ShipComponent(title, type,
									rating, subComponent);

							propComponents.add(comp);
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
					constructGroup("Propulsion", propComponents);

				}

				JSONArray ord = titleObject.getJSONArray("ordnance");

				if (ord.length() > 0) {
					constructDivider();
					ArrayList<ShipComponent> ordComponents = new ArrayList<ShipComponent>();

					for (int i = 0; i < ord.length(); i++) {
						try {
							JSONObject componentObject = ord.getJSONObject(i);
							String title = componentObject.getString("name");
							String cclass = componentObject.getString("class");
							String maxSize = componentObject
									.getString("max_size");
							String quantity = componentObject
									.getString("quantity");

							JSONObject subCompObj = componentObject
									.getJSONObject("component");
							String subtitle = subCompObj.getString("name");
							String subtype = subCompObj.getString("type");
							String subsize = subCompObj.getString("size");
							ShipSubComponent subComponent = new ShipSubComponent(
									subtitle, subtype, subsize);

							ShipComponent comp = new ShipComponent(title,
									cclass, maxSize, quantity, null,
									subComponent);

							ordComponents.add(comp);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					constructGroup("Ordnance", ordComponents);

				}

				JSONArray mod = titleObject.getJSONArray("modular");

				if (mod.length() > 0) {
					constructDivider();
					ArrayList<ShipComponent> modComponents = new ArrayList<ShipComponent>();

					for (int i = 0; i < mod.length(); i++) {
						try {
							JSONObject componentObject = mod.getJSONObject(i);
							String title = componentObject.getString("name");
							String maxSize = componentObject
									.getString("max_size");

							JSONObject subCompObj = componentObject
									.getJSONObject("component");
							String subtitle = subCompObj.getString("name");
							String subtype = subCompObj.getString("type");
							String subsize = subCompObj.getString("size");
							ShipSubComponent subComponent = new ShipSubComponent(
									subtitle, subtype, subsize);

							ShipComponent comp = new ShipComponent(title,
									maxSize, null, "", subComponent);

							modComponents.add(comp);
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
					constructGroup("Modular", modComponents);

				}

				JSONArray avion = titleObject.getJSONArray("avionics");

				if (avion.length() > 0) {
					constructDivider();
					ArrayList<ShipComponent> avionComponents = new ArrayList<ShipComponent>();

					for (int i = 0; i < avion.length(); i++) {
						try {
							JSONObject componentObject = avion.getJSONObject(i);
							String title = componentObject.getString("name");
							String maxSize = componentObject
									.getString("max_size");
							String category = componentObject
									.getString("category");
							JSONObject subCompObj = null;
							try {
								subCompObj = componentObject
										.getJSONObject("component");
							} catch (JSONException ex) {
							}
							ShipComponent comp;
							if (subCompObj != null) {
								String subtitle = subCompObj.getString("name");
								String subtype = subCompObj.getString("type");
								String subsize = subCompObj.getString("size");
								ShipSubComponent subComponent = new ShipSubComponent(
										subtitle, subtype, subsize);
								comp = new ShipComponent(title, maxSize,
										category, "", subComponent);
							} else {
								comp = new ShipComponent(title, maxSize,
										category, "", null);
							}

							avionComponents.add(comp);

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
					constructGroup("Avionics", avionComponents);

				}

				if (MyApp.getInstance().isOnline(getActivity())) {
					ImageDownload imageDownload = new ImageDownload(
							getActivity(), shipsImageProgress, shipsImage);
					if (imageUrl.startsWith("http")) {
						imageDownload.execute(imageUrl);
						shipsImage.setURL(imageUrl);
					} else {
						imageDownload.execute(InformerConstants.URL_SHIP_IMAGES
								+ imageUrl);
						shipsImage.setURL(InformerConstants.URL_SHIP_IMAGES
								+ imageUrl);
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Used to construct visual groups.
	 * 
	 * @param title
	 *            of the group
	 * @param propComponents
	 *            components of the group
	 */
	private void constructGroup(String title,
			ArrayList<ShipComponent> propComponents) {

		RelativeLayout groupToggleView = new RelativeLayout(getActivity());
		final ImageView toggleImage = new ImageView(getActivity());
		TextView headText = new TextView(getActivity());
		headText.setText(title);
		headText.setTextAppearance(getActivity(), R.style.TitleTextStyle);
		headText.setTypeface(font);
		headText.setTextSize(16);
		android.widget.RelativeLayout.LayoutParams textAssignmentLeft = new android.widget.RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		textAssignmentLeft.setMargins(
				ViewHelper.convertToDP(getActivity(), 38),
				ViewHelper.convertToDP(getActivity(), 10),
				ViewHelper.convertToDP(getActivity(), 8),
				ViewHelper.convertToDP(getActivity(), 5));
		headText.setLayoutParams(textAssignmentLeft);
		groupToggleView.setLayoutParams(textAssignmentLeft);
		groupToggleView
				.setBackgroundResource(R.drawable.transparent_button_style);

		android.widget.RelativeLayout.LayoutParams imageParams = new android.widget.RelativeLayout.LayoutParams(
				ViewHelper.convertToDP(getActivity(), 10),
				ViewHelper.convertToDP(getActivity(), 10));
		imageParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		imageParams.setMargins(ViewHelper.convertToDP(getActivity(), 8),
				ViewHelper.convertToDP(getActivity(), 5),
				ViewHelper.convertToDP(getActivity(), 8),
				ViewHelper.convertToDP(getActivity(), 5));
		headText.setLayoutParams(textAssignmentLeft);

		toggleImage.setImageResource(R.drawable.ic_expand);

		groupToggleView.addView(headText);
		groupToggleView.addView(toggleImage);

		android.widget.RelativeLayout.LayoutParams params = new android.widget.RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, headText.getId());
		params.setMargins(ViewHelper.convertToDP(getActivity(), 2),
				ViewHelper.convertToDP(getActivity(), 34),
				ViewHelper.convertToDP(getActivity(), 2),
				ViewHelper.convertToDP(getActivity(), 2));
		LinearLayout groupContainer = new LinearLayout(getActivity());
		groupContainer.setLayoutParams(params);
		groupContainer.setOrientation(LinearLayout.VERTICAL);

		for (ShipComponent comp : propComponents) {
			LinearLayout infoContainer = new LinearLayout(getActivity());
			infoContainer.setOrientation(LinearLayout.VERTICAL);
			infoContainer.setBackgroundResource(R.drawable.list_item_style);

			if (comp.getName() != null) {
				constructInfo("Name", comp.getName(),
						getResources().getColor(R.color.bluishGrey),
						infoContainer);
			}
			if (comp.getQuantity() != null) {
				constructInfo("Quantity", comp.getQuantity(), getResources()
						.getColor(R.color.bluishGrey), infoContainer);
			}
			if (comp.getRating() != null) {
				constructInfo("Rating", comp.getRating(), getResources()
						.getColor(R.color.bluishGrey), infoContainer);
			}
			if (comp.getCategory() != null) {
				constructInfo("Category", comp.getCategory(), getResources()
						.getColor(R.color.bluishGrey), infoContainer);
			}
			if (comp.getMaxSize() != null) {
				constructInfo("Max Size", comp.getMaxSize(), getResources()
						.getColor(R.color.bluishGrey), infoContainer);
			}
			if (comp.getCclass() != null) {
				constructInfo("Class", comp.getCclass(), getResources()
						.getColor(R.color.bluishGrey), infoContainer);
			}
			if (comp.getSubComponent() != null) {
				if (comp.getSubComponent().getName() != null) {
					constructInfo("Component",
							comp.getSubComponent().getName(), getResources()
									.getColor(R.color.bluishGrey),
							infoContainer);
				}
				if (comp.getSubComponent().getType() != null) {
					constructInfo("Type", comp.getSubComponent().getType(),
							getResources().getColor(R.color.bluishGrey),
							infoContainer);
				}
				if (comp.getSubComponent().getSize() != null) {
					constructInfo("Size", comp.getSubComponent().getSize(),
							getResources().getColor(R.color.bluishGrey),
							infoContainer);
				}
			}

			groupContainer.addView(infoContainer);
		}
		groupContainer.setVisibility(View.GONE);
		groupToggleView.addView(groupContainer);

		groupToggleView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				RelativeLayout view = (RelativeLayout) v;

				if (view.getChildAt(2).getVisibility() == View.GONE) {
					view.getChildAt(2).setVisibility(View.VISIBLE);
					toggleImage.setImageResource(R.drawable.ic_collapse);
				} else {
					view.getChildAt(2).setVisibility(View.GONE);
					toggleImage.setImageResource(R.drawable.ic_expand);
				}
			}
		});

		shipContainer.addView(groupToggleView);

	}

	/**
	 * Used to construct info on ship properties
	 * 
	 * @param description
	 *            the info
	 * @param property
	 *            the property
	 */
	@SuppressLint("NewApi")
	private void constructInfo(String description, String property, int color,
			ViewGroup container) {
		LayoutParams textAssignmentLeft = new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		textAssignmentLeft.setMargins(ViewHelper.convertToDP(getActivity(), 8),
				ViewHelper.convertToDP(getActivity(), 5),
				ViewHelper.convertToDP(getActivity(), 8),
				ViewHelper.convertToDP(getActivity(), 5));

		LayoutParams params = new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		LinearLayout innerLayout = new LinearLayout(getActivity());
		innerLayout.setLayoutParams(params);

		TextView infoDescription = new TextView(getActivity());
		infoDescription.setWidth(ViewHelper.convertToDP(getActivity(), 115));
		infoDescription.setText(description);
		infoDescription
				.setTextAppearance(getActivity(), R.style.TitleTextStyle);
		infoDescription.setTypeface(font);
		infoDescription.setTextSize(16);
		infoDescription.setTextColor(getResources().getColor(
				R.color.fontBlueDefaultColor));
		innerLayout.addView(infoDescription, textAssignmentLeft);

		TextView infoText = new TextView(getActivity());
		infoText.setText(Translator.removeBreaks(property));
		infoText.setTextAppearance(getActivity(), R.style.TitleTextStyle);
		infoText.setTypeface(font);
		infoText.setTextSize(16);

		if (MyApp.getInstance().getCurrentApiVersion() >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			infoText.setTextIsSelectable(true);
		}
		infoText.setTextColor(color);
		innerLayout.addView(infoText, textAssignmentLeft);

		container.addView(innerLayout);
	}

	/**
	 * Used to construct a visual divider.
	 */
	private void constructDivider() {

		LayoutParams dividerAssignment = new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				ViewHelper.convertToDP(getActivity(), 1));
		dividerAssignment.setMargins(ViewHelper.convertToDP(getActivity(), 8),
				ViewHelper.convertToDP(getActivity(), 5),
				ViewHelper.convertToDP(getActivity(), 8),
				ViewHelper.convertToDP(getActivity(), 5));

		View dividerView = new View(getActivity());
		dividerView.setBackgroundColor(getActivity().getResources().getColor(
				R.color.bluishGrey));
		dividerView.setLayoutParams(dividerAssignment);

		shipContainer.addView(dividerView);

	}

	/**
	 * Starts downloading of the contents of the RSI website. When finished
	 * downloading, the infos are returned.
	 * 
	 * @author MadKauz
	 * 
	 */
	private class DownloadInfoFromSite extends
			AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			shipsLoad.setVisibility(View.VISIBLE);
			AlphaAnimation loadingAnim = new AlphaAnimation(1F, 0.0F);
			loadingAnim.setDuration(800);
			loadingAnim.setFillAfter(false);
			loadingAnim.setRepeatMode(Animation.REVERSE);
			loadingAnim.setRepeatCount(Animation.INFINITE);
			shipsLoad.startAnimation(loadingAnim);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@SuppressWarnings("resource")
		@Override
		protected String doInBackground(String... params) {
			InputStream input = null;
			OutputStream output = null;
			HttpURLConnection connection = null;
			try {
				URL url = new URL(params[0]);
				connection = (HttpURLConnection) url.openConnection();
				connection
						.setConnectTimeout(InformerConstants.TIMEOUT_CONNECTION);
				connection.setReadTimeout(InformerConstants.TIMEOUT_CONNECTION);
				connection.connect();

				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					return "error";
				}

				try {
					// download the file
					input = connection.getInputStream();

					output = new FileOutputStream(Environment
							.getExternalStorageDirectory().getPath()
							+ "/"
							+ InformerConstants.SHIP_DATA_FILENAME);
					byte data[] = new byte[4096];
					int count;
					while ((count = input.read(data)) != -1) {
						if (isCancelled()) {
							input.close();
							return "error";
						}
						output.write(data, 0, count);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return "error";
				}

			} catch (Exception e) {
				e.printStackTrace();
				return "error";
			} finally {
				try {
					if (output != null)
						output.close();
					if (input != null)
						input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (connection != null)
					connection.disconnect();
			}
			return "ok";
		}

		@Override
		protected void onPostExecute(String result) {
			shipsLoad.clearAnimation();
			shipsLoad.setVisibility(View.GONE);
			if (result != null) {
				if (result.equals("ok")) {
					readDownload();
				} else {
					MyApp.getInstance().showError(getActivity(),
							getResources().getString(R.string.errorParseFault));
				}
			}
		}
	}
}
