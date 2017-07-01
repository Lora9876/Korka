package rs.elfak.korka1.korkaquiz.Activities;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
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

import rs.elfak.korka1.korkaquiz.Bluetooth.BluetoothChatActivity;
import rs.elfak.korka1.korkaquiz.Models.Question;
import rs.elfak.korka1.korkaquiz.Models.QuestionsList;
import rs.elfak.korka1.korkaquiz.Models.User;
import rs.elfak.korka1.korkaquiz.Models.UsersList;
import rs.elfak.korka1.korkaquiz.Notifications.NotificationEventReceiver;
import rs.elfak.korka1.korkaquiz.R;

public class MainActivity extends AppCompatActivity {
    ComponentName service;
    private final String serverUrl = "http://192.168.0.101:80/korka/getUsers.php";
    private final String serverUrl2 = "http://192.168.0.101:80/korka/onExit.php";
    private final String serverUrl1 = "http://192.168.0.101:80/korka/getFriendsImages.php";
    //private final String serverUrl = "http://10.10.77.217:80/korka/getUsers.php";
    //private final String serverUrl = getString(R.string.serverUrl)+"getUsers.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button play = (Button) findViewById(R.id.button_play);
        Button score = (Button) findViewById(R.id.button_score);
        Button friends = (Button) findViewById(R.id.button_friends);
        Button map = (Button) findViewById(R.id.button_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, BluetoothChatActivity.class);
                startActivity(i);
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(this, PlayActivity.class);
                Intent i = new Intent(MainActivity.this, QuestionActivity.class);
                i.putExtra("state",QuestionActivity.START );
                i.putExtra("score", UsersList.getInstance().getThisUser().getScore());
                startActivity(i);
            }
        });

        score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RankActivity.class);
                startActivity(i);
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncDataClassImage asyncRequestObject = new AsyncDataClassImage();
                asyncRequestObject.execute(serverUrl1, UsersList.getInstance().getMyId().toString());

                //Intent i = new Intent(MainActivity.this, MapActivity.class);
                //startActivity(i);
            }
        });

        if(UsersList.getInstance().getList().isEmpty()) {
            //UCITAVA SVE KORISNIKE I PITANJA
            JSONAsyncTask asyncRequestObject = new JSONAsyncTask();
            asyncRequestObject.execute(serverUrl);
        }

        //pretpostavljam da je ovo za asinhronu razmenu lokacije
        //Intent intentMyService = new Intent(this, MyLocationService.class);
        //service = startService(intentMyService);
    }

    //PADAJUCI MENI
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.start_item)
            NotificationEventReceiver.setupAlarm(getApplicationContext());
        else  if(id==R.id.stop_item)
            NotificationEventReceiver.cancelAlarm(getApplicationContext());

        return super.onOptionsItemSelected(item);
    }
    //IZLAZAK IZ APLIKACIJE
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exiting application")
                .setMessage("Are you sure that you want to exit Korka Quiz?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONAsyncTaskExit asyncRequestObject = new JSONAsyncTaskExit();
                        asyncRequestObject.execute(serverUrl2,UsersList.getInstance().getMyId().toString());
                    }
                })
                .setNegativeButton("No", null)
                .create().show();
    }
    //SERVER
    class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Please wait, loading in progress.");
            dialog.setTitle("Connecting with server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                HttpGet httppost = new HttpGet(urls[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    JSONObject jsono = new JSONObject(data);
                    JSONArray jarray = jsono.getJSONArray("users");

                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject object = jarray.getJSONObject(i);
                        User user = new User();
                        user.setFromJSON(object);
                        UsersList.getInstance().getList().add(user);
                    }
                    JSONObject jsonoQ = new JSONObject(data);
                    JSONArray jarrayQ = jsonoQ.getJSONArray("questions");

                    for (int i = 0; i < jarrayQ.length(); i++) {
                        JSONObject object = jarrayQ.getJSONObject(i);
                        Question question = new Question();
                        question.setFromJSON(object);
                        QuestionsList.getInstance().getList().add(question);
                    }

                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            dialog.cancel();

            //postavkja ovog korisnika
            UsersList.getInstance().setThisUserById(UsersList.getInstance().getMyId());

            if(result == false) {
                Toast.makeText(getApplicationContext(), getString(R.string.server_connection_failed), Toast.LENGTH_LONG).show();
                return;
            }

        }
    }
    private class AsyncDataClassImage extends AsyncTask<String, Void, String> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait, loading in progress.");
            pDialog.setTitle("Connecting with server");
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
            HttpConnectionParams.setSoTimeout(httpParameters, 5000);

            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost httpPost = new HttpPost(params[0]);

            try {
                //napravi listu parametara username i password i izvrsi post metodu
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("id", params[1]));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                //primi odgovor na svoj post od servera
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);

                if(data.equals("") || data == null)
                    return null;

                JSONObject jsono = new JSONObject(data);
                JSONArray jarray = jsono.getJSONArray("friends");

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
            return "ok";
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.cancel();
            if(result.equals("") || result == null){
                Toast.makeText(MainActivity.this, getString(R.string.server_connection_failed), Toast.LENGTH_LONG).show();
                return;
            }
            else
            {
                Intent i = new Intent(MainActivity.this, MapActivity.class);
                startActivity(i);
            }
        }
    }
    class JSONAsyncTaskExit extends AsyncTask<String, Void, Boolean> {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("The application is now shutting down.");
            dialog.setTitle("Exiting Korka");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
            HttpConnectionParams.setSoTimeout(httpParameters, 5000);

            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost httpPost = new HttpPost(params[0]);
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("id", params[1]));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(httpPost);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Boolean result) {
            dialog.cancel();
            finish();
        }
    }
}
