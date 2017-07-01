package rs.elfak.korka1.korkaquiz.Models;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Terra on 21-May-17.
 */
public class Question {

    private int id;
    private String question;
    private ArrayList<String> answers;
    private String longitude;
    private String latitude;
    private Bitmap questionImage;
    private Integer correct;
    private Integer difficulty;

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public String getImgString() {
        return imgString;
    }

    public void setImgString(String imgString) {
        this.imgString = imgString;
    }

    private String imgString;

    public Integer getCorrect() {
        return correct;
    }

    public void setCorrect(Integer correct) {
        this.correct = correct;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getQusetion() {
        return question;
    }

    public void setQuestion(String txt) {
        this.question = txt;
    }

    public void addAnswer(String a) {
        answers.add(a);
    }
    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> ans) {
        this.answers = ans;
    }

    private static class SingletonHolder{
        public static final User instance = new User();

    }

    public void setFromJSON(JSONObject jsonObject)
    {

        try {
            if(jsonObject.has("question"))
                this.setQuestion(jsonObject.get("question").toString());
            if(jsonObject.has("correct"))
                this.setCorrect(Integer.parseInt(jsonObject.get("correct").toString()));
            if(jsonObject.has("difficulty"))
                this.setDifficulty(Integer.parseInt(jsonObject.get("difficulty").toString()));

            ArrayList<String> answers1=new ArrayList<String>();

            if(jsonObject.has("answerA"))
                answers1.add(jsonObject.get("answerA").toString());
            if(jsonObject.has("answerB"))
                answers1.add(jsonObject.get("answerB").toString());
            if(jsonObject.has("answerC"))
                answers1.add(jsonObject.get("answerC").toString());
            if(jsonObject.has("answerD"))
                answers1.add(jsonObject.get("answerD").toString());
            this.setAnswers(answers1);
            //this.setAnswers(jsonObject.get("answers").toString());
            //this.setImgString(jsonObject.get("img_uri").toString());

            if(jsonObject.has("id"))
                this.setId(Integer.parseInt(jsonObject.get("id").toString()));
            if(jsonObject.has("latitude"))
                this.setLatitude(jsonObject.get("latitude").toString());
            if(jsonObject.has("longitude"))
                this.setLongitude(jsonObject.get("longitude").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getQuestionImage() {
        return questionImage;
    }

    public void setQuestionImage(Bitmap qImage) {
        this.questionImage = qImage;
    }
}
