package com.example.teacherassistantapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AddStudentActivity extends AppCompatActivity {
    EditText txt;
    FloatingActionButton SaveStdDetails;
    List<EditText> alltxt;
    DatabaseHelper myDb;
    int classid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        myDb= new DatabaseHelper(this);
        Intent i=getIntent();
        String value =i.getStringExtra("count");
        classid=i.getIntExtra("classid",0);
        addEditTextViews(Integer.parseInt(value));
        addStudent(classid);
    }

    public void addEditTextViews(int n) {
        LinearLayout layout = findViewById(R.id.rootLayout);
        alltxt = new ArrayList<EditText>();
        int roll=1;
        while (n > 0) {

            txt = new EditText(this);
            alltxt.add(txt);
            txt.setHint(roll+". Name of Student ");
            txt.setId(roll);
            layout.addView(txt);
            --n;
            roll++;
        }
        SaveStdDetails=(FloatingActionButton)findViewById(R.id.mAddStudent);
        addStudent(classid);



    }
    public void addStudent(final int classid){

        SaveStdDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] strings = new String[alltxt.size()];

                for(int i=0; i < alltxt.size(); i++){
                    strings[i] = alltxt.get(i).getText().toString();
                }
                boolean isInserted=myDb.insertStudentDetails(strings,classid);
                if(isInserted){
                    startActivity(new Intent(AddStudentActivity.this, MainActivity.class));
                    finish();
                }
                else
                    Toast.makeText(AddStudentActivity.this, "Add Student Details!!", Toast.LENGTH_SHORT).show();

            }
        });
    }
    public void onBackPressed(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("ALERT");
        builder.setMessage("Class will be removed if student details are not added.Do you want to remove class?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if( myDb.deleteClass(Integer.toString(classid)))
                    startActivity(new Intent(AddStudentActivity.this, MainActivity.class));

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