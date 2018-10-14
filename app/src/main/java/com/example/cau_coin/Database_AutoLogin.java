package com.example.cau_coin;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database_AutoLogin extends SQLiteOpenHelper {

    public Database_AutoLogin(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS LOGIN(id TEXT, pwd TEXT, name TEXT, major TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertData(String id,String pwd, String name, String major){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO LOGIN VALUES('"+id+"','"+pwd+"','"+name+"','"+major+"');");
        db.close();
    }

    public void dropTable(){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DROP TABLE LOGIN;");
        db.execSQL("CREATE TABLE IF NOT EXISTS LOGIN(id TEXT, pwd TEXT, name TEXT, major TEXT);");
        db.close();
    }

    public boolean check_AutoLogin() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM LOGIN", null);
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public String getData() {
        String result = "";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM LOGIN", null);

        while (cursor.moveToNext()) {
            result += (cursor.getString(0) + "/" + cursor.getString(1) + "/" + cursor.getString(2) + "/" + cursor.getString(3));
        }

        return result;
    }
}
