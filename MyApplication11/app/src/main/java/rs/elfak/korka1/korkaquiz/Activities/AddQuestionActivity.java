package rs.elfak.korka1.korkaquiz.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import rs.elfak.korka1.korkaquiz.Models.Question;
import rs.elfak.korka1.korkaquiz.Models.QuestionsList;
import rs.elfak.korka1.korkaquiz.R;

public class AddQuestionActivity extends AppCompatActivity {
    private final String serverUrl = "http://192.168.0.101:80/korka/addQuestion.php";
    //private final String serverUrl = "http://10.10.77.217:80/korka/addQuestion.php";
    //private final String serverUrl = getString(R.string.serverUrl)+"addQuestion.php";
    protected String placeLat, placeLon;
    protected String A,B,C,D,correct, question;
    protected Integer diff;
    private ProgressDialog pDialog;
    ImageView viewImage;
    Button b;

    String picturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        try {

            Intent mapIntent = getIntent();
            Bundle mapBundle = mapIntent.getExtras();
            if (mapBundle != null) {
                    placeLat = mapBundle.getString("lat");
                    placeLon = mapBundle.getString("lon");
            }
        } catch (Exception e) {
            Log.d("Error", "Error reading location");
            placeLat = "";
            placeLon = "";
        }

        final TextView questionText = (TextView) findViewById(R.id.editTextQuestionText);
        final TextView answerA = (TextView) findViewById(R.id.editTextAnswerA);
        final TextView answerB = (TextView) findViewById(R.id.editTextAnswerB);
        final TextView answerC = (TextView) findViewById(R.id.editTextAnswerC);
        final TextView answerD = (TextView) findViewById(R.id.editTextAnswerD);
        final RadioGroup radioGroup=(RadioGroup) findViewById(R.id.radioGroup);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);

        b=(Button)findViewById(R.id.questionPictureButton);
        viewImage=(ImageView)findViewById(R.id.questionImageView);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                question = questionText.getText().toString();
                A = answerA.getText().toString();
                B = answerB.getText().toString();
                C = answerC.getText().toString();
                D = answerD.getText().toString();
                diff = spinner.getSelectedItemPosition();

                correct = "";
                switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.radioButtonA:
                            correct = "0";
                            break;
                        case R.id.radioButtonB:
                            correct = "1";
                            break;
                        case R.id.radioButtonC:
                            correct = "2";
                            break;
                    case R.id.radioButtonD:
                        correct = "3";
                        break;
                }

                if (A.equals("") || B.equals("") || C.equals("") || D.equals("") || question.equals("")) {
                    Toast.makeText(AddQuestionActivity.this, getString(R.string.fields_empty_warning), Toast.LENGTH_LONG).show();
                    return;
                }

                String imageString="";
                if(viewImage.getDrawable() != null)
                {

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Bitmap bitmap = ((BitmapDrawable)viewImage.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    imageString = Base64.encodeToString(imageBytes, Base64.URL_SAFE);
                }
                AsyncDataClass asyncRequestObject = new AsyncDataClass();
                asyncRequestObject.execute(serverUrl, question, A, B, C, D, correct, placeLat,placeLon, imageString, diff.toString());//, ((Integer) QuestionsList.getInstance().getList().size()).toString());
            }
        });

    }

    //ZA DODAVANJE SLIKE
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));

                viewImage.setImageBitmap(thumbnail);
            }
        }
    }
    //KRAJ DODAVANJA SLIKE

    private class AsyncDataClass extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
            HttpConnectionParams.setSoTimeout(httpParameters, 5000);
            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost httpPost = new HttpPost(params[0]);
            String jsonResult = "";
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(10);
                nameValuePairs.add(new BasicNameValuePair("question", params[1]));
                nameValuePairs.add(new BasicNameValuePair("answerA", params[2]));
                nameValuePairs.add(new BasicNameValuePair("answerB", params[3]));
                nameValuePairs.add(new BasicNameValuePair("answerC", params[4]));
                nameValuePairs.add(new BasicNameValuePair("answerD", params[5]));
                nameValuePairs.add(new BasicNameValuePair("correct", params[6]));
                nameValuePairs.add(new BasicNameValuePair("latitude", params[7]));
                nameValuePairs.add(new BasicNameValuePair("longitude", params[8]));
                nameValuePairs.add(new BasicNameValuePair("imgString", params[9]));
                nameValuePairs.add(new BasicNameValuePair("difficulty", params[10]));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpClient.execute(httpPost);
                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
                System.out.println("Returned Json object " + jsonResult.toString());
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResult;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddQuestionActivity.this);
            pDialog.setMessage("Sending...");
            pDialog.show();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.cancel();
            System.out.println("Resulted Value: " + result);
            if(result.equals("") || result == null){
                Toast.makeText(AddQuestionActivity.this, getString(R.string.server_connection_failed), Toast.LENGTH_LONG).show();
                return;
            }
            try {
                    JSONObject resultObject = new JSONObject(result);
                    int id = resultObject.getInt("questionId");
                    Question qu = new Question();

                    qu.setQuestion(question);
                    qu.setCorrect(Integer.parseInt(correct));
                    qu.setDifficulty(diff);
                    ArrayList<String> answers1=new ArrayList<String>(4);
                    answers1.add(A);            answers1.add(B);
                    answers1.add(C);            answers1.add(D);
                    qu.setAnswers(answers1);
                    qu.setId(id);

                    QuestionsList.getInstance().getList().add(qu);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(AddQuestionActivity.this,"New Question added!", Toast.LENGTH_SHORT).show();
            //Intent i = new Intent(AddQuestionActivity.this, MapActivity.class);
            //startActivity(i);
            finish();
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