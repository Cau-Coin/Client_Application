package com.example.cau_coin;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database_Evaluate extends SQLiteOpenHelper {
    public Database_Evaluate(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS EVALUATE(userid TEXT, subject TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS SCORE(userid TEXT, evalid TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertData_Evaluate(String userid, String subject) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO EVALUATE VALUES('" + userid + "','" + subject + "');");
        db.close();
    }

    public void insertData_Score(String userid, String evalid) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO SCORE VALUES('" + userid + "','" + evalid + "');");
        db.close();
    }

    public boolean getEvaluate(String userid, String subject) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM EVALUATE WHERE userid='" + userid + "' AND subject='" + subject + "'", null);

        if (cursor.getCount() > 0) return false;
        else return true;
    }
    public boolean getScore(String userid, String evalid) {
        String result = "";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM SCORE WHERE userid='" + userid + "' AND evalid='" + evalid + "'", null);

        if (cursor.getCount() > 0) return false;
        else return true;
    }
}
