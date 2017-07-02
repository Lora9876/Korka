package rs.elfak.korka1.korkaquiz.Activities;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rs.elfak.korka1.korkaquiz.Models.Question;
import rs.elfak.korka1.korkaquiz.Models.QuestionsList;
import rs.elfak.korka1.korkaquiz.Models.User;
import rs.elfak.korka1.korkaquiz.Models.UsersList;
import rs.elfak.korka1.korkaquiz.R;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final float FRIEND = 1;
    public static final float OTHER_USER = 2;
    public static final float QUESTION_FAR = 3;
    public static final float QUESTION_NEAR = 4;

    private final String serverUrl = "http://10.10.77.217:80/korka/updateLocation.php";
    protected int radius;
    protected boolean easy, medium, hard, allUsers; //variables used to control searching options

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    LatLng latLng; //this users location
    GoogleMap mGoogleMap;
    SupportMapFragment mFragment;
    Marker currLocationMarker;
    ArrayList<Marker> otherUsers, questions;
    protected Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        radius=1500;//inicijalizacija
        easy=medium=hard=allUsers=true;
        questions= new ArrayList<Marker>();
        otherUsers = new ArrayList<Marker>();

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_radius);
        dialog.setTitle("Set radius");
        Button dialogButton = (Button) dialog.findViewById(R.id.buttonOk);
        Button dialogCancelButton = (Button) dialog.findViewById(R.id.buttonCancel);
        final TextView radiusTextView =(TextView)dialog.findViewById(R.id.textViewRadious);
        final SeekBar seekbar = (SeekBar) dialog.findViewById(R.id.seekBar);
        final RadioGroup radioGroup=(RadioGroup) dialog.findViewById(R.id.radioGroupRadius);
        final CheckBox easyCB = (CheckBox) dialog.findViewById(R.id.checkBoxEasy);
        final CheckBox mediumCB = (CheckBox) dialog.findViewById(R.id.checkBoxMedium);
        final CheckBox hardCB = (CheckBox) dialog.findViewById(R.id.checkBoxHard);

        radiusTextView.setText(String.valueOf(radius)+"m");
        seekbar.setProgress(100);

        seekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        radius = (progress * 15);//progres ide od 0 do 100, tako da sam ovim dobio od 0 do 1.5km
                        radiusTextView.setText(String.valueOf(radius) + "m");
                    }
                });

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    easy = easyCB.isChecked();
                    medium = mediumCB.isChecked();
                    hard = hardCB.isChecked();
                    radius=(seekbar.getProgress())* 15;//progres ide od 0 do 100, tako da sam ovim dobio od 0 do 1.5km
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.radioButtonAllUsers:
                            allUsers = true;
                            break;
                        case R.id.radioButtonFriends:
                            allUsers = false;
                            break;
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(MapActivity.this, "fault!", Toast.LENGTH_LONG).show();
                    return;
                }

                //azurira markere korisnika
                for (Marker m:otherUsers)
                    m.remove();
                otherUsers.clear();
                addUserMarkers();
                //azurira markere pitanja
                for (Marker m:questions)
                    m.remove();
                questions.clear();
                addQuestionsMarkers();

                dialog.dismiss();
            }
        });

        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //dialog.show(); --stavio sam da se ovaj dijalog pokrece iz padajuceg menija

        mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mFragment.getMapAsync(this);
    }

    //PADAJUCI MENI
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manu_map, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.search_item)
        {
            dialog.show();
        }
        else  if(id==R.id.new_item)
        {
            Intent i = new Intent(this, AddQuestionActivity.class);
            i.putExtra("lat", ((Double)latLng.latitude).toString());
            i.putExtra("lon", ((Double)latLng.longitude).toString());
            startActivity(i);
            //finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //GOOGLE MAPS CONNECTION
    protected synchronized void buildGoogleApiClient() {
        Toast.makeText(this,"buildGoogleApiClient",Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        mGoogleMap = gMap;
        mGoogleMap.setMyLocationEnabled(true);

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                if(arg0.getAlpha()==OTHER_USER)
                    Toast.makeText(MapActivity.this, arg0.getTitle(), Toast.LENGTH_LONG).show();// display toast

                if(arg0.getAlpha()==FRIEND)
                {
                    new AlertDialog.Builder(MapActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("Friends info")
                            .setMessage(arg0.getTitle())
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .create().show();
                }

                if(arg0.getAlpha()==QUESTION_FAR)
                    Toast.makeText(MapActivity.this, arg0.getTitle(), Toast.LENGTH_LONG).show();// display toast

                if(arg0.getAlpha()==QUESTION_NEAR)
                {
                    new AlertDialog.Builder(MapActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("Questions info")
                            .setMessage(arg0.getTitle())
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .create().show();
                }

                return true;
            }
        });
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this,"onConnected",Toast.LENGTH_SHORT).show();
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }

        addUserMarkers();
        addQuestionsMarkers();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

        //place marker at current position
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currLocationMarker = mGoogleMap.addMarker(markerOptions);

        //UPDATE LOCATION DATA FROM SERVER AND THEN UPDATE MARKERS
        AsyncDataClass asyncRequestObject = new AsyncDataClass();
        asyncRequestObject.execute(serverUrl);
    }

    //DODAVANJE MARKERA - KORISNICI
    private void addUserMarkers(){
        User me = UsersList.getInstance().getThisUser();
        Location myLoc=new Location("locationA");
        myLoc.setLatitude(latLng.latitude);
        myLoc.setLongitude(latLng.longitude);

        for (User user: UsersList.getInstance().getList()) {

            if(user.isStatus() && me.getId()!=user.getId() && (allUsers || me.checkIfFriends(user.getId())))
            {
                //float[] distance = new float[2];
                //Location.distanceBetween(Double.parseDouble(user.getLatitude()), Double.parseDouble(user.getLongitude()), latLng.latitude, latLng.longitude, distance);

                Location otherLoc=new Location("locationA");
                otherLoc.setLatitude(Double.parseDouble(user.getLatitude()));
                otherLoc.setLongitude(Double.parseDouble(user.getLongitude()));

                double distance=myLoc.distanceTo(otherLoc);

                if(distance <= radius) {
                    LatLng loc = new LatLng(Double.parseDouble(user.getLatitude()), Double.parseDouble(user.getLongitude()));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(loc);
                    if(!me.checkIfFriends(user.getId()))
                    {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.user1));
                        markerOptions.title("Username: "+user.getUsername());
                        markerOptions.alpha(OTHER_USER);
                    }
                    else
                    {
                        if(user.getImgString()==null)
                        {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.user1));
                        }
                        else
                        {
                            if(user.getImgString().equals(""))
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.user1));
                            else {
                                byte[] imageBytes = Base64.decode(user.getImgString(), Base64.URL_SAFE);
                                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(decodedImage));
                            }
                        }
                        markerOptions.title("Username: "+user.getUsername()+"\n"+"Name and surname:"+user.getName() + " " + user.getSurname()+"\nEmail: "+ user.getEmail()+"\nScore: "+ user.getScore());
                        markerOptions.alpha(FRIEND);
                    }
                    Marker marker = mGoogleMap.addMarker(markerOptions);
                    otherUsers.add(marker);
                }
            }
        }
    }
    //DODAVANJE MARKERA - PITANJA
    private void addQuestionsMarkers(){
        Location myLoc=new Location("locationA");
        myLoc.setLatitude(latLng.latitude);
        myLoc.setLongitude(latLng.longitude);
        for (Question qu:QuestionsList.getInstance().getList()) {

            Location otherLoc=new Location("locationA");
            otherLoc.setLatitude(Double.parseDouble(qu.getLatitude()));
            otherLoc.setLongitude(Double.parseDouble(qu.getLongitude()));

            double distance=myLoc.distanceTo(otherLoc);

            if(distance <= radius) {
                if(easy&&(qu.getDifficulty()==0) || medium&&(qu.getDifficulty()==1) || hard&&(qu.getDifficulty()==2)) {
                    LatLng loc = new LatLng(Double.parseDouble(qu.getLatitude()), Double.parseDouble(qu.getLongitude()));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(loc);
                    switch (qu.getDifficulty()) {
                        case 0:
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.qr1));
                            break;
                        case 1:
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.qr2));
                            break;
                        case 2:
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.qr3));
                            break;
                    }
                    if(distance<=40) {
                        markerOptions.title(qu.getQusetion() + "\ncorrect answer: " + qu.getAnswers().get(qu.getCorrect()));
                        markerOptions.alpha(QUESTION_NEAR);
                    }
                    else {
                        markerOptions.title("You have to be closer to the question to see its details");
                        markerOptions.alpha(QUESTION_FAR);
                    }
                    Marker marker = mGoogleMap.addMarker(markerOptions);
                    questions.add(marker);
                }
            }
        }
    }

    //SERVER
    private class AsyncDataClass extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
            HttpConnectionParams.setSoTimeout(httpParameters, 10000);

            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost httpPost = new HttpPost(serverUrl);

            String message = "";
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("id", "1" /*userId*/));
                nameValuePairs.add(new BasicNameValuePair("longitude", ((Double) latLng.longitude).toString()));
                nameValuePairs.add(new BasicNameValuePair("latitude", ((Double) latLng.latitude).toString()));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                //primi odgovor na svoj post od servera
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);

                JSONObject jsono = new JSONObject(data);
                JSONArray jarray = jsono.getJSONArray("users");

                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject jsonObject = jarray.getJSONObject(i);
                    if (jsonObject.has("id"))
                        UsersList.getInstance().getUserById(Integer.parseInt(jsonObject.get("id").toString())).setFromJSON(jsonObject);
                }

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            for (Marker m:otherUsers)
                m.remove();
            otherUsers.clear();
            addUserMarkers();

            for (Marker m:questions)
                m.remove();
            questions.clear();
            addQuestionsMarkers();
        }
    }

}