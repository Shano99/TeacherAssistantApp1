package com.example.teacherassistantapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class StatisticsActivity extends AppCompatActivity {
    FloatingActionButton Update;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        DatabaseHelper mydb=new DatabaseHelper(this);
        Intent in=getIntent();
        final String classid=in.getStringExtra("CLASS_ID");
       final  int count=in.getIntExtra("count",0);

        Update=(FloatingActionButton)findViewById(R.id.update);
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StatisticsActivity.this, UpdateAttendance.class);
                i.putExtra("classid",classid);
                i.putExtra("count",count);
                startActivity(i);
                finish();
            }
        });
        TableLayout tab=(TableLayout)findViewById(R.id.table);
        TableRow r1=new TableRow(this);
        r1.setBackgroundColor(Color.YELLOW);

        TextView roll=new TextView(this);
        TextView Std=new TextView(this);
        roll.setText("ROLL NO.");
        roll.setTextSize(20);

        roll.setPadding(10,10,20,10);
        Std.setText("NAME");
        Std.setTextSize(20);
        Std.setPadding(10,10,20,10);
        r1.addView(roll);
        r1.addView(Std);
        TextView datehour;

        Cursor res=mydb.getLesnIds(classid);
        if(res.getCount()==0){
           Toast.makeText(StatisticsActivity.this,"Attendance not taken yet!!",Toast.LENGTH_SHORT).show();
        }
        String lsnid[]=new String[res.getCount()];
        String time[]=new String[res.getCount()];
        int i=0;
        while(res.moveToNext()){
            lsnid[i]=res.getString(0);
            time[i]=res.getString(1)+"\n(Hour:"+res.getString(2)+")";
            datehour=new TextView(this);
            datehour.setText(time[i]);
            datehour.setTextSize(20);
            datehour.setPadding(10,10,20,10);
            r1.addView(datehour);
            i++;
        }
        tab.addView(r1);
        Cursor res2=mydb.getStdIds(classid);
        String stdid;
        TextView stdroll;
        TextView stdname;
        TableRow r2;
        TextView preabs;

        Cursor res3;
        while (res2.moveToNext()){
            r2=new TableRow(this);
            r2.setBackgroundColor(Color.BLUE);
            stdid=res2.getString(0);
            stdroll=new TextView(this);
            stdroll.setText(res2.getString(2));
            stdroll.setTextSize(20);
            stdroll.setPadding(10,10,20,10);
            r2.addView(stdroll);
            stdname=new TextView(this);
            stdname.setText(res2.getString(1));
            stdname.setTextSize(20);
            stdname.setPadding(10,10,20,10);
            r2.addView(stdname);

            res3=mydb.getPreAbs(stdid);
            while(res3.moveToNext()){
                preabs=new TextView(this);
                if(res3.getString(0).equals("0")){
                    preabs.setText("A");
                    preabs.setTextColor(Color.RED);}
                else{
                    preabs.setText("P");
                    preabs.setTextColor(Color.GREEN); }

                preabs.setTextSize(20);
                preabs.setPadding(10,10,20,10);
                r2.addView(preabs);
            }

            tab.addView(r2);

        }


    }

}
