package rs.elfak.korka1.korkaquiz.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rs.elfak.korka1.korkaquiz.Models.Question;
import rs.elfak.korka1.korkaquiz.Models.QuestionsList;
import rs.elfak.korka1.korkaquiz.Models.UsersList;
import rs.elfak.korka1.korkaquiz.R;

public class QuestionActivity extends AppCompatActivity {
    private final String serverUrl = "http://192.168.0.101:80/korka/updateScore.php";
    private final String serverUrl1 = "http://192.168.0.101:80/korka/getQuestionImage.php";
    //private final String serverUrl = getString(R.string.serverUrl)+"updateScore.php";
    public static final int SHOW = 6;
    public static final int START = 0;
    public static final int END = 4;
    private int state;
    private int score;
    private Question question;
    ImageView viewImage;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        ListView lista = (ListView) findViewById(R.id.listView);
        TextView questionText = (TextView) findViewById(R.id.textViewQuestion);
        TextView questionTitle = (TextView) findViewById(R.id.textViewQuestionTitle);
        viewImage=(ImageView)findViewById(R.id.showQuestionImageView);
        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                state = bundle.getInt("state");
                score = bundle.getInt("score");

                Random r = new Random();
                Integer tmp = Math.abs(r.nextInt()) % QuestionsList.getInstance().getList().size();

                //UCITAVANJE SLIKE
                AsyncDataClassImage asyncRequestObjectI = new AsyncDataClassImage();
                asyncRequestObjectI.execute(serverUrl1,tmp.toString());


                question = QuestionsList.getInstance().getList().get(tmp);
                ArrayList<String> a = new ArrayList<String>();
                if (question != null) {
                    if (state == SHOW)
                        a.add(question.getAnswers().get(question.getCorrect()));
                    else
                        a = question.getAnswers();
                }
                String diff="";
                switch(question.getDifficulty())
                {
                    case 0: diff="(easy)"; break;
                    case 1: diff="(medium)"; break;
                    case 2: diff="(hard)"; break;
                }

                //NIJE PREPORUCIVA OVAKVA KONKATENACIJA
                questionTitle.setText("Question " + (state + 1) + ". out of 5 "+diff+":");
                questionText.setText(question.getQusetion());//moguc null pointer exception
                lista.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, a));

            }
        } catch (Exception e) {
            Log.d("Error", "Error reading state");
        }

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == question.getCorrect()) {
                    score+=5+5*question.getDifficulty();
                    Toast.makeText(QuestionActivity.this, "Your answer is correct. \n+"+(5+5*question.getDifficulty())+" points", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(QuestionActivity.this, "I'm sorry, but you are wrong.", Toast.LENGTH_SHORT).show();

                if(state<END)
                {
                    Intent i = new Intent(QuestionActivity.this, QuestionActivity.class);
                    i.putExtra("state", state + 1);
                    i.putExtra("score", score);
                    startActivity(i);
                    finish();
                }
                else
                {
                    AsyncDataClassNewScore asyncRequestObject = new AsyncDataClassNewScore();
                    //asyncRequestObject.execute(serverUrl,((Integer)UsersList.getInstance().getThisUser().getId()).toString(), ((Integer) score).toString());
                    asyncRequestObject.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverUrl,((Integer)UsersList.getInstance().getThisUser().getId()).toString(), ((Integer) score).toString());
                }
            }
        });
    }
    private class AsyncDataClassNewScore extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(QuestionActivity.this);
            pDialog.setMessage("Please wait. Aplication is sending your new score to server.");
            pDialog.setTitle("Connecting with server");
            pDialog.show();
        }
        @Override
        protected Integer doInBackground(String... params) {

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
            HttpConnectionParams.setSoTimeout(httpParameters, 5000);

            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost httpPost = new HttpPost(serverUrl);

            int result = 0;
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("userId", ((Integer)UsersList.getInstance().getThisUser().getId()).toString()));
                nameValuePairs.add(new BasicNameValuePair("newScore", ((Integer) score).toString()));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);

                JSONObject jsono = new JSONObject(data);
                if(jsono.has("success"))
                    result = jsono.getInt("success");

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException j){
                j.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            pDialog.cancel();
            if(result == 0){
                Toast.makeText(QuestionActivity.this, getString(R.string.server_connection_failed), Toast.LENGTH_LONG).show();
                return;
            }
            UsersList.getInstance().getThisUser().setScore(score);
            Toast.makeText(QuestionActivity.this, "Your new store is: "+score+" points", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private class AsyncDataClassImage extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
            HttpConnectionParams.setSoTimeout(httpParameters, 5000);

            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost httpPost = new HttpPost(params[0]);

            String jsonResult = "";
            try {
                //napravi listu parametara username i password i izvrsi post metodu
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("id", params[1]));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                //primi odgovor na svoj post od servera i to dalje prosledi kao rezultat funkcije do in background
                HttpResponse response = httpClient.execute(httpPost);
                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResult;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("") || result == null){
                Toast.makeText(QuestionActivity.this, getString(R.string.server_connection_failed), Toast.LENGTH_LONG).show();
                return;
            }
            else
            {
                JSONObject resultObject = null;
                try {
                    resultObject = new JSONObject(result);
                    String imgString = resultObject.getString("imgString");
                    if(imgString!="")
                    {
                        byte[] imageBytes = Base64.decode(imgString, Base64.URL_SAFE);
                        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        viewImage.setImageBitmap(decodedImage);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            try {
                while ((rLine = br.readLine()) != null) {
                    answer.append(rLine);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return answer;
        }
    }
}
