package sg.edu.rp.c347.p09_gettingmylocations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 15017171 on 21/7/2017.
 */

public class check_records_main_activity extends AppCompatActivity {

    //declare object
    ListView lvRecords;
    TextView tvRecords;

    //declare arraylist and arrayAdaptor variables
    ArrayList<String> alRecordList;
    ArrayAdapter<String> aaRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_records);

        tvRecords = (TextView) this.findViewById(R.id.textViewRecords);

        Intent intentReceived = getIntent();
        String records_received = intentReceived.getStringExtra("records");

        //Bind all the ui elements to layout file
        lvRecords = (ListView)findViewById(R.id.listViewRecords);
        //create instance for arraylist
        alRecordList = new ArrayList<String>();
        //Create instance for arrayAdapter , bind it to ArrayList
        //simple_list_item_1 is a reference to an built-in XML layout document that is part of the Android OS, rather than one of your own XML layouts.
        aaRecord = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, alRecordList);
        //bind arrayAdapter to ListView variable
        lvRecords.setAdapter(aaRecord);

        //populate ListView with data
        alRecordList.add(records_received);

        tvRecords.setText("Number of Records : " + alRecordList.size());
        Log.d("this is my array", "arr: " + alRecordList.size());




    }
}
