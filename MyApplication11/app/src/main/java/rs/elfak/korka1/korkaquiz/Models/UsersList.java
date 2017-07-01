package rs.elfak.korka1.korkaquiz.Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Terra on 22-May-17.
 */
public class UsersList {

    private Integer myId;
    private User thisUser;
    private ArrayList<User> users;
    private UsersList()
    {
        users=new ArrayList<User>();
    }
    private static class SingletonHolder
    {
        public static  final UsersList instance=new UsersList();
    }
    public static UsersList getInstance()
    {
        return SingletonHolder.instance;
    }
    public ArrayList<User> getList()
    {
        return users;
    }
    public void sort()
    {
        Collections.sort(users, new UserScoreComparator());
    }
    public User getThisUser() {
        return thisUser;
    }

    public void setThisUser(User thisUser) {
        this.thisUser = thisUser;
    }
    public void setThisUserById(int id)
    {
        setThisUser(getUserById(id));
    }
    public Integer getMyId() {
        return myId;
    }
    public void setMyId(Integer myId) { this.myId = myId; }
    public  User getUserById(int id)
    {
        for (User u : users)
        {
            if(u.getId()==id)
                return u;
        }
        return null;
    }

    class UserScoreComparator implements Comparator<User> {
        public int compare(User usr1, User usr2) {
            return usr2.getScore() - usr1.getScore();
        }
    }
}


