package com.example.teacherassistantapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="DATABASE";
    private static final String TABLE_1="STUDENT";
    private static final String TABLE_2="CLASS";
    private static final String TABLE_3="ATTENDANCE";
    private static final String TABLE_4="LESSON";
    private static final String TABLE_5="SUBJECT";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_1+"(STD_ID INTEGER PRIMARY KEY AUTOINCREMENT, STD_NAME TEXT NOT NULL, ROLL_NO INTEGER, CLASS_ID INTEGER, FOREIGN KEY(CLASS_ID) REFERENCES "+TABLE_2+"(CLASS_ID))");
        db.execSQL("create table "+TABLE_2+"(CLASS_ID INTEGER PRIMARY KEY AUTOINCREMENT, YEAR INTEGER, DIVISION INTEGER, DEPT_NAME TEXT, COUNT INTEGER)");
        db.execSQL("create table "+TABLE_3+"(ATT_ID INTEGER PRIMARY KEY AUTOINCREMENT, LESSON_ID INTEGER, STD_ID INTEGER, PRE_ABS INTEGER, FOREIGN KEY(STD_ID) REFERENCES "+TABLE_1+"(STD_ID), FOREIGN KEY(LESSON_ID) REFERENCES "+TABLE_4+"(LESSON_ID))");
        db.execSQL("create table "+TABLE_4+"(LESSON_ID INTEGER PRIMARY KEY AUTOINCREMENT, DATE DATE, HOUR TIME, CLASS_ID INTEGER, FOREIGN KEY(CLASS_ID) REFERENCES "+TABLE_2+"(CLASS_ID))");
        db.execSQL("create table "+TABLE_5+"(SUB_ID INTEGER PRIMARY KEY AUTOINCREMENT, SUB_NAME TEXT,CLASS_ID INTEGER,FOREIGN KEY(CLASS_ID) REFERENCES "+TABLE_2+"(CLASS_ID))");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+ TABLE_1);
        db.execSQL("drop table if exists "+ TABLE_2);
        db.execSQL("drop table if exists "+ TABLE_3);
        db.execSQL("drop table if exists "+ TABLE_4);
        db.execSQL("drop table if exists "+ TABLE_5);
        onCreate(db);
    }
    boolean insertClassDetails(String Dept, String Year, String Division, String Count)
    {
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("YEAR",Integer.parseInt(Year));
        cv.put("DIVISION",Integer.parseInt(Division));
        cv.put("DEPT_NAME",Dept);
        cv.put("COUNT",Integer.parseInt(Count));
        Cursor rest=db.rawQuery("select * from "+TABLE_2+" where DEPT_NAME=? and YEAR=? and DIVISION=? ",new String[]{Dept,Year,Division});
        if(rest.getCount()>0)
            return false;
        long res=db.insert(TABLE_2,null,cv);
        return res != -1;
    }
    boolean insertStudentDetails(String arr[],int classid){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        int roll;
        for(int i=0;i<arr.length;i++) {
            roll=i+1;
            if(arr[i].isEmpty()){
                return false;
            }
            Cursor chk=db.rawQuery("select * from "+TABLE_1+" where STD_NAME=? and ROLL_NO=? and CLASS_ID=? ",new String[]{arr[i],Integer.toString(roll),Integer.toString(classid)});
            if(chk.getCount()>0){
                continue;
            }
            cv.put("STD_NAME",arr[i] );
            cv.put("ROLL_NO", roll);
            cv.put("CLASS_ID",classid);
            db.insert(TABLE_1, null, cv);
        }
        return true;

    }
    int getClassID(String Dept, String Year, String Division){
        SQLiteDatabase db= this.getWritableDatabase();
        Cursor res=db.rawQuery("select CLASS_ID from "+TABLE_2+" where DEPT_NAME=? and YEAR=? and DIVISION=? ",new String[]{Dept,Year,Division});
        res.moveToNext();
        return Integer.parseInt(res.getString(0));
    }
    void insertSubject(String Sub,int Classid){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("SUB_NAME",Sub);
        cv.put("CLASS_ID",Classid);
        db.insert(TABLE_5, null, cv);

    }
   boolean isEmptyDb(){
        SQLiteDatabase db= this.getWritableDatabase();
        Cursor res=db.rawQuery("select * from "+TABLE_2,null);
        if(res.getCount()>0){
            return false;
        }
        return true;
    }
    Cursor getAllDetail()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM CLASS INNER JOIN SUBJECT ON CLASS.CLASS_ID = SUBJECT.CLASS_ID;",null);
       // return db.rawQuery("select * from CLASS",null);
    }
    Cursor getStdIds(String ClassId)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM STUDENT WHERE CLASS_ID = ?",new String[]{ ClassId});
    }
    boolean insertAttendance(String date,String hour,int preabs[],String classid){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("DATE",date);
        cv.put("HOUR",hour);
        cv.put("CLASS_ID",classid);
        Cursor res= db.rawQuery("SELECT * FROM LESSON WHERE DATE = ? and HOUR=? and CLASS_ID=?",new String[]{ date,hour,classid});
        if(res.getCount()>0)
            return false;
        res.close();
        db.insert(TABLE_4, null, cv);
        Cursor res2=db.rawQuery("SELECT * from LESSON where DATE= ? and HOUR= ? and CLASS_ID= ?",new String[]{date,hour,classid});
        res2.moveToNext();
        String LesId=res2.getString(0);
        res2.close();

        res=db.rawQuery("select STD_ID from STUDENT where CLASS_ID=?",new String[]{classid});
        int count=res.getCount();
        int stdid[]=new int[count];
        int m=0;
        while(res.moveToNext()){
            stdid[m]=res.getInt(0);
            m++;
        }
        ContentValues cv2=new ContentValues();
        for(int i=0;i<count;i++){
            cv2.put("LESSON_ID",LesId);
            cv2.put("STD_ID",stdid[i]);
            cv2.put("PRE_ABS",preabs[i]);
            db.insert(TABLE_3,null,cv2);
        }
        return true;
    }
    boolean deleteClass(String classid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_2,"CLASS_ID=?",new String[] {classid});
        db.delete(TABLE_1,"CLASS_ID=?",new String[] {classid});
        db.delete(TABLE_5,"CLASS_ID=?",new String[] {classid});
        Cursor res=db.rawQuery("select LESSON_ID from LESSON where CLASS_ID=?",new String[]{classid});
        String lesId;
        while (res.moveToNext()){
            lesId=res.getString(0);
            db.delete(TABLE_3,"LESSON_ID=?",new String[] {lesId});

        }
        db.delete(TABLE_4,"CLASS_ID=?",new String[] {classid});

        return  true;
    }
    boolean editClass(String classid,String Dept,String Year,String Div,String sub){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("YEAR",Year);
        cv.put("DIVISION",Div);
        cv.put("DEPT_NAME",Dept);
        Cursor res=db.rawQuery("select * from CLASS INNER JOIN SUBJECT where YEAR=? AND DIVISION=? AND DEPT_NAME=? AND SUB_NAME=? and CLASS.CLASS_ID<>? AND SUBJECT.CLASS_ID<>?",new String[]{Year,Div,Dept,sub,classid});
        if(res.getCount()>0)
            return false;
        db.update(TABLE_2, cv, "CLASS_ID= ?", new String[]{classid});
        ContentValues cv2=new ContentValues();
        cv2.put("SUB_NAME",sub);
        db.update(TABLE_5, cv2, "CLASS_ID= ?", new String[]{classid});
        return true;
    }
    Cursor getLesnIds(String classid){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("select * from LESSON where CLASS_ID=?",new String[]{classid});
        return res;
    }
    Cursor getPreAbs(String Stdid){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("select PRE_ABS from ATTENDANCE where STD_ID=?",new String[]{Stdid});
        return res;
    }
    boolean updateAttendance(String classid,String datehr,String preabs[]) {
        String date = datehr.substring(0, 10);
        String hour = datehr.substring(12);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from LESSON where DATE=? and HOUR=? and CLASS_ID=?", new String[]{date,hour,classid});
        res.moveToNext();
        String lsnid = res.getString(0);
        res.close();
        res = db.rawQuery("select * from ATTENDANCE where LESSON_ID=?", new String[]{lsnid});

        ContentValues cv = new ContentValues();
        int i = 0;
        while (res.moveToNext()) {
            cv.put("PRE_ABS", preabs[i]);
            db.update(TABLE_3, cv, "STD_ID= ? and LESSON_ID=?", new String[]{res.getString(2), lsnid});
            i++;
        }
        return true;
    }
    Cursor getAttendance(String datehr,String classid){
        SQLiteDatabase db = this.getWritableDatabase();
        String date = datehr.substring(0, 10);
        String hour = datehr.substring(12);
        Cursor res=db.rawQuery("select LESSON_ID from LESSON where DATE=? and HOUR=? and CLASS_ID=?",new String[]{date,hour,classid});
        res.moveToNext();
        String lsnid=res.getString(0);
        return db.rawQuery("select PRE_ABS from ATTENDANCE where LESSON_ID=?",new String[]{lsnid});
    }
}
