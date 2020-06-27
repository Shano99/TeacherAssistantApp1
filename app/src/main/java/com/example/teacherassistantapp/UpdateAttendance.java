package com.example.teacherassistantapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class UpdateAttendance extends AppCompatActivity {
    Spinner spinner;
    DatabaseHelper mydb;
    String classid;
    int count;
    int[] stdIdArray;
    FloatingActionButton SaveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_attendance);
        mydb=new DatabaseHelper(this);
        SaveBtn=(FloatingActionButton)findViewById(R.id.mUpdateAttend);
        Intent in=getIntent();
        classid=in.getStringExtra("classid");
        count=in.getIntExtra("count",0);
        spinner=(Spinner)findViewById(R.id.list1);
        addItems();
        Cursor stdIdList=mydb.getStdIds(classid);
        stdIdArray=new int[count];

        int k=0;
        while(stdIdList.moveToNext()) {
            stdIdArray[k] = Integer.parseInt(stdIdList.getString(0));
            k++;
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


           private ImageButton stdicon;

           public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                      long id) {

                Toast.makeText(parent.getContext(),
                        "On Item Select : \n" + parent.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_LONG).show();

                String[] preabs=new String[count];
                String datehr=parent.getItemAtPosition(pos).toString();
                Cursor res=mydb.getAttendance(datehr,classid);
               int m=0;
               while(res.moveToNext()){
                   preabs[m]=res.getString(0);
                   m++;

               }

                final GridLayout gridview=(GridLayout)findViewById(R.id.gridview2);
                final LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
               gridview.removeAllViews();
                for(int j=1,l=0;j<=count;j++,l++)
                {
                    LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.singlestd, null);
                    TextView stdroll=(TextView)linearLayout.findViewById(R.id.stdroll);
                    stdroll.setText(Integer.toString(j));
                    gridview.addView(linearLayout);
                    stdicon=(ImageButton)findViewById(R.id.stdicon);
                    stdicon.setId(stdIdArray[l]);
                    if(preabs[l].equals("0"))
                        stdicon.setColorFilter(Color.RED);
                    else
                        stdicon.setColorFilter(Color.GREEN);
                    changeColor(this.stdicon,l,preabs);

                }
               callSaveBtn(preabs);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(UpdateAttendance.this,"choose",Toast.LENGTH_SHORT).show();
            }
       });
    }

    public void changeColor(final ImageButton stdicon, final int pos, final String preabs[])
    {
        stdicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PorterDuffColorFilter clr= (PorterDuffColorFilter) stdicon.getColorFilter();
                PorterDuffColorFilter green=new PorterDuffColorFilter( Color.GREEN, PorterDuff.Mode.SRC_ATOP);

                if(green.equals(clr)){
                    stdicon.setColorFilter(Color.RED);
                    preabs[pos]="0";}
                else{
                    stdicon.setColorFilter(Color.GREEN);
                    preabs[pos]="1";}
            }
        });
    }

    public void addItems(){
        List<String> list = new ArrayList<String>();
        Cursor res=mydb.getLesnIds(classid);
        if(res.getCount()==0){
            Toast.makeText(UpdateAttendance.this,"Attendance not taken yet!!",Toast.LENGTH_SHORT).show();
        }
        while(res.moveToNext()) {
            list.add(res.getString(1)+"--"+res.getString(2));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }
    public void callSaveBtn(final String preabs[]){
        SaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date=String.valueOf(spinner.getSelectedItem());
                if(mydb.updateAttendance(classid,date,preabs)){
                    Intent i = new Intent(UpdateAttendance.this, StatisticsActivity.class);
                    i.putExtra("CLASS_ID",classid);
                    i.putExtra("count",count);
                    startActivity(i);
                    finish();


                }else

                Toast.makeText(UpdateAttendance.this,"(error)", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void onBackPressed(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("ALERT");
        builder.setMessage("Do you want to discard the changes");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                UpdateAttendance.super.onBackPressed();

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
