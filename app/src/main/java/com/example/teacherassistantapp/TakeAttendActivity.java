package com.example.teacherassistantapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

public class TakeAttendActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    GridLayout gridview;
    DatabaseHelper mydb;
    ImageButton stdicon;
    FloatingActionButton SaveBtn;
    int[] preabs;
    String Currentdate;
    String SetHour;
    String classid;
    EditText Hour;
    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attend);
        mydb=new DatabaseHelper(this);
        SaveBtn=(FloatingActionButton)findViewById(R.id.mSaveAttend);
        //intent
        Intent i=getIntent();
        count=i.getIntExtra("count",0);
        classid=i.getStringExtra("CLASS_ID");
        //get std ids
        Cursor stdIdList=mydb.getStdIds(classid);
        int[] stdIdArray=new int[count];
        preabs=new int[count];
        int k=0;
        while(stdIdList.moveToNext()) {
            stdIdArray[k]=Integer.parseInt(stdIdList.getString(0));
            k++;
        }

        //Toast.makeText(TakeAttendActivity.this,"stdid is"+stdIdArray[0], Toast.LENGTH_SHORT).show();
        gridview=(GridLayout)findViewById(R.id.gridview);
        final LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for(int j=1,l=0;j<=count;j++,l++)
        {//change std roll text
            LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.singlestd, null);
            TextView stdroll=(TextView)linearLayout.findViewById(R.id.stdroll);
            stdroll.setText(Integer.toString(j));
            gridview.addView(linearLayout);
            //set id
            stdicon=(ImageButton)findViewById(R.id.stdicon);
            stdicon.setId(stdIdArray[l]);
            stdicon.setColorFilter(Color.GREEN);
            preabs[l]=1;
            changeColor(this.stdicon,l);

        }
        //calender access
        Button Date=(Button)findViewById(R.id.setdate);
        Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datepicker=new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(),"date picker");
            }
        });
        //set hour
        Hour=(EditText)findViewById(R.id.sethour);

        callSaveBtn();


    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c=Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        Currentdate= simpleDateFormat.format(c.getTime());

        Toast.makeText(TakeAttendActivity.this, Currentdate, Toast.LENGTH_SHORT).show();
    }

    public void changeColor(final ImageButton stdicon,final int pos)
    {
        stdicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PorterDuffColorFilter clr= (PorterDuffColorFilter) stdicon.getColorFilter();
                PorterDuffColorFilter green=new PorterDuffColorFilter( Color.GREEN, PorterDuff.Mode.SRC_ATOP);

                if(green.equals(clr)){
                    stdicon.setColorFilter(Color.RED);
                    preabs[pos]=0;}
                else{
                    stdicon.setColorFilter(Color.GREEN);
                    preabs[pos]=1;}


            }
        });
    }

    public void callSaveBtn(){//pass date nd hour
        SaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override//!!!!!!!!!!!!!!!!!!!!!!solve if else prob!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            public void onClick(View v) {
                SetHour=Hour.getText().toString();
                if(Currentdate==null || SetHour.isEmpty())
                    Toast.makeText(TakeAttendActivity.this,"Set Date and Hour", Toast.LENGTH_SHORT).show();
                else{
                    //store to db
                    if(mydb.insertAttendance(Currentdate,SetHour,preabs,classid)){

                        startActivity(new Intent(TakeAttendActivity.this, MainActivity.class));
                        finish();
                    }
                    else
                        Toast.makeText(TakeAttendActivity.this,"Attendance for this Hour has been taken already!!Kindly choose Edit option to edit", Toast.LENGTH_SHORT).show();


                }

            }
        });
    }
    public void onBackPressed(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("ALERT");
        builder.setMessage("Do you want to skip attendance?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                TakeAttendActivity.super.onBackPressed();

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
}
