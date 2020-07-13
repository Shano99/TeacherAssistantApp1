package com.example.teacherassistantapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class AddClassActivity extends AppCompatActivity {
    private EditText Dept;
    private EditText Year;
    private EditText Division;
    private EditText Count;
    private EditText Subject;
    private ImageButton AddClass;
    String Deptval;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        myDb= new DatabaseHelper(this);
        Dept=(EditText)findViewById(R.id.mDept);
        Year=(EditText)findViewById(R.id.mYear);
        Division=(EditText)findViewById(R.id.mDivision);
        Count=(EditText)findViewById(R.id.mCount);
        Subject=(EditText)findViewById(R.id.mSubject);
        AddClass=(ImageButton) findViewById(R.id.mAddClass);
        addClassDetails();
    }

    public void addClassDetails(){
        AddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Deptval=Dept.getText().toString();
                if(Deptval.isEmpty() || Year.getText().toString().isEmpty() || Division.getText().toString().isEmpty() || Count.getText().toString().isEmpty()){
                    Toast.makeText(AddClassActivity.this, "Enter all details", Toast.LENGTH_SHORT).show();
                }else {
                    boolean isInserted = myDb.insertClassDetails(Dept.getText().toString(), Year.getText().toString(), Division.getText().toString(), Count.getText().toString());
                    if (isInserted) {
                        int Classid = myDb.getClassID(Dept.getText().toString(), Year.getText().toString(), Division.getText().toString());
                        myDb.insertSubject(Subject.getText().toString(), Classid);
                        Intent i = new Intent(AddClassActivity.this, AddStudentActivity.class);
                        i.putExtra("count", Count.getText().toString());

                        i.putExtra("classid", Classid);
                        startActivity(i);
                        finish();


                    } else
                        Toast.makeText(AddClassActivity.this, "Class Exists already!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void onBackPressed(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("DISCARD");
        builder.setMessage("Do you want to discard this class?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                AddClassActivity.super.onBackPressed();
                startActivity(new Intent(AddClassActivity.this, MainActivity.class));
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
