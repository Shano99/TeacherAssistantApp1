package com.example.teacherassistantapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.nfc.cardemulation.CardEmulation;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton Add;
    LinearLayout l1;
    ImageButton Edit,TakeAttend,Delete,Statistics;
    DatabaseHelper myDb=new DatabaseHelper(this);
    String CLASS_ID, YEAR, DIVISION, DEPT_NAME, COUNT, SUB_NAME;
    ImageButton Attend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        l1= (LinearLayout) findViewById(R.id.linear1);
        int IdTakeAttend,IdEdit,IdStatistics,IdDelete;

        if (myDb.isEmptyDb()) {
            //display empty
            TextView t1 = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            t1.setLayoutParams(params);
            t1.setText(R.string.empty);
            t1.setTextSize(30);
            t1.setGravity(Gravity.CENTER);
            l1.addView(t1);
        }
        else{
            final LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Cursor res=myDb.getAllDetail();
            int rows=res.getCount();

            while(res.moveToNext()) {
                CLASS_ID = res.getString(0);
                YEAR =  res.getString(1);
                DIVISION = res.getString(2);
                DEPT_NAME = res.getString(3);
                COUNT = res.getString(4);
                SUB_NAME = res.getString(6);

                CardView cv = (CardView)inflater.inflate(R.layout.cardview,null);
                TextView subname=(TextView)cv.findViewById(R.id.sub);
                TextView deptname=(TextView)cv.findViewById(R.id.dept);
                TextView year=(TextView)cv.findViewById(R.id.year);
                TextView div=(TextView)cv.findViewById(R.id.div);

                subname.setText(SUB_NAME);
                deptname.setText(DEPT_NAME);
                year.setText("YEAR: "+YEAR);
                div.setText("DIV: "+DIVISION);

                l1.addView(cv);

                IdTakeAttend=Integer.parseInt(CLASS_ID)+5000;
                IdEdit=Integer.parseInt(CLASS_ID)+6000;
                IdStatistics=Integer.parseInt(CLASS_ID)+7000;
                IdDelete=Integer.parseInt(CLASS_ID)+8000;

                Edit=(ImageButton) findViewById(R.id.edit);
                Edit.setId(IdEdit);
                edit(this.Edit,CLASS_ID,YEAR,DEPT_NAME,DIVISION,SUB_NAME);

                TakeAttend=(ImageButton) findViewById(R.id.attend);
                TakeAttend.setId(IdTakeAttend);
                takeattend(this.TakeAttend,CLASS_ID,COUNT);

                Statistics=(ImageButton) findViewById(R.id.record);
                Statistics.setId(IdStatistics);
                stats(this.Statistics,CLASS_ID,COUNT);

                Delete=(ImageButton) findViewById(R.id.delete);
                Delete.setId(IdDelete);
                del(this.Delete,CLASS_ID);


            }

        }
        Add = (FloatingActionButton) findViewById(R.id.mADD);
        addData();
    }
    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("EXIT");
        builder.setMessage("Do you really want to exit app?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                MainActivity.super.onBackPressed();

            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();


            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
    public void stats(ImageButton Statis,final String ClassId,final String c){
        Statis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, StatisticsActivity.class);
                i.putExtra("CLASS_ID",ClassId);
                i.putExtra("count",Integer.parseInt(c));
                startActivity(i);


            }
        });

    }
    public void addData(){
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddClassActivity.class));

            }
        });
    }
    public void edit(ImageButton Edit,final String classid,final String year,final String dept,final String div,final String sub){
        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                i.putExtra("CLASS_ID",classid);
                i.putExtra("YEAR",year);
                i.putExtra("DEPT_NAME",dept);
                i.putExtra("DIVISION",div);
                i.putExtra("SUB_NAME",sub);
                //YEAR,DEPT_NAME,DIVISION,SUB_NAME
                finish();
                startActivity(i);


            }
        });
    }

    public void takeattend(ImageButton Attend, final String classid, final String Count)
    {
        Attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, TakeAttendActivity.class);
                i.putExtra("count",Integer.parseInt(Count));
                i.putExtra("CLASS_ID",classid);
                startActivity(i);

            }
        });
    }
    public void del(ImageButton Delete, final String classid){
        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertshow(classid);
                            }
        });
    }
    public void alertshow(final String classid){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete");
        builder.setMessage("Do you really want to delete?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // call del fun
               // dialog.dismiss();
               if( myDb.deleteClass(classid)){
                   startActivity(new Intent(MainActivity.this, MainActivity.class));
                   finish();
               }
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

}
