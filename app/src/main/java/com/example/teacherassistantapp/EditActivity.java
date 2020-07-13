package com.example.teacherassistantapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EditActivity extends AppCompatActivity {
    private EditText Dept;
    private EditText Year;
    private EditText Division;
    private EditText Subject;
    private FloatingActionButton EditClass;
    DatabaseHelper myDb;
    String Idclass,deptname,subname,year,division;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        myDb= new DatabaseHelper(this);
        Dept=(EditText)findViewById(R.id.mEditDept);
        Year=(EditText)findViewById(R.id.mEditYear);
        Division=(EditText)findViewById(R.id.mEditDivision);
        Subject=(EditText)findViewById(R.id.mEditSubject);
        EditClass=(FloatingActionButton) findViewById(R.id.mEditClass);
        Intent i=getIntent();
        Idclass=i.getStringExtra("CLASS_ID");
        deptname=i.getStringExtra("DEPT_NAME");
        division=i.getStringExtra("DIVISION");
        year=i.getStringExtra("YEAR");
        subname=i.getStringExtra("SUB_NAME");
        Dept.setText(deptname);
        Year.setText(year);
        Subject.setText(subname);
        Division.setText(division);

        editClassDetails();
    }
    public void editClassDetails(){
        EditClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Dept.getText().toString().isEmpty() || Year.getText().toString().isEmpty() || Division.getText().toString().isEmpty() || Subject.getText().toString().isEmpty()) {
                    Toast.makeText(EditActivity.this, "Enter Details", Toast.LENGTH_SHORT).show();

                } else {
                    boolean isEdited = myDb.editClass(Idclass, Dept.getText().toString(), Year.getText().toString(), Division.getText().toString(), Subject.getText().toString());
                    if (isEdited) {

                        Intent i = new Intent(EditActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();


                    } else
                        Toast.makeText(EditActivity.this, "Class Exists already!!", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
    public void onBackPressed(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("EDIT");
        builder.setMessage("Do you want to discard changes?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                EditActivity.super.onBackPressed();
                Intent i = new Intent(EditActivity.this, MainActivity.class);
                startActivity(i);
                finish();

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
