package rs.elfak.korka1.korkaquiz.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Terra on 21-May-17.
 */
public class User {

    private int id;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String email;
    private boolean status;
    private int score;
    private String imgString;
    private ArrayList<Integer> friendsIds;
    private String longitude;
    private String latitude;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public ArrayList<Integer> getFriendsIds() {
        return friendsIds;
    }

    public void setFriendsIds(ArrayList<Integer> friendsIds) {
        this.friendsIds = friendsIds;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

   public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

   public String getImgString() {
        return imgString;
    }

    public void setImgString(String imgString) {
        this.imgString = imgString;
    }

    private static class SingletonHolder{
        public static final User instance = new User();

    }

    public static User getInstance()
    {
        return SingletonHolder.instance;
    }

    public void setFromJSON(JSONObject jsonObject)
    {

        try {
            if(jsonObject.has("id"))
                this.setId(Integer.parseInt(jsonObject.get("id").toString()));
            if(jsonObject.has("score"))
                this.setScore(Integer.parseInt(jsonObject.get("score").toString()));
            if(jsonObject.has("status"))
                if((jsonObject.get("status").toString()).equals("1"))
                {
                    this.setStatus(true);
                }
                else
                {
                    this.setStatus(false);
                }
            if(jsonObject.has("username"))
                this.setUsername(jsonObject.get("username").toString());
            if(jsonObject.has("name"))
                this.setName(jsonObject.get("name").toString());
            if(jsonObject.has("surname"))
                this.setSurname(jsonObject.get("surname").toString());
            if(jsonObject.has("imgString"))
                this.setImgString(jsonObject.get("imgString").toString());
            if(jsonObject.has("password"))
                this.setPassword(jsonObject.get("password").toString());
            if(jsonObject.has("latitude"))
                this.setLatitude(jsonObject.get("latitude").toString());
            if(jsonObject.has("longitude"))
                this.setLongitude(jsonObject.get("longitude").toString());
            if(jsonObject.has("email"))
                this.setEmail(jsonObject.get("email").toString());

            if(jsonObject.has("friends"))
            {
                JSONArray jarray = jsonObject.getJSONArray("friends");
                ArrayList<Integer> listdata = new ArrayList<Integer>();

                if (jarray != null) {
                    for (int i = 0; i < jarray.length(); i++) {
                        listdata.add(Integer.parseInt(jarray.getJSONObject(i).get("friendid").toString()));
                    }
                    this.setFriendsIds(listdata);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean checkIfFriends(Integer friendId){ return friendsIds.contains(friendId);}
    public void addFriend(Integer friendId){ friendsIds.add(friendId);}

    @Override
    public String toString() { return  score+" points, "+name+" "+surname+" ("+ username +")";}

}
