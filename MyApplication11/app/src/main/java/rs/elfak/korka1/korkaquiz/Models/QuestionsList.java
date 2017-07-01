package rs.elfak.korka1.korkaquiz.Models;

import java.util.ArrayList;

/**
 * Created by Terra on 31-May-17.
 */
public class QuestionsList {

    private ArrayList<Question> questions;
    private QuestionsList()
    {
        questions=new ArrayList<Question>();
    }
    private static class SingletonHolder
    {
        public static  final QuestionsList instance=new QuestionsList();
    }
    public static QuestionsList getInstance()
    {
        return SingletonHolder.instance;
    }
    public ArrayList<Question> getList()
    {
        return questions;
    }
    public Question getQuestion(int index)
    {
        return questions.get(index);
    }
    public void deleteQuestion(int index)
    {
        questions.remove(index);
    }


}

