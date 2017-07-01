package rs.elfak.korka1.korkaquiz.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import rs.elfak.korka1.korkaquiz.Models.User;
import rs.elfak.korka1.korkaquiz.Models.UsersList;
import rs.elfak.korka1.korkaquiz.R;

public class RankActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        UsersList.getInstance().sort();
        ListView scoreList = (ListView) findViewById(R.id.rank_list);
        scoreList.setAdapter(new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, UsersList.getInstance().getList()));

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
