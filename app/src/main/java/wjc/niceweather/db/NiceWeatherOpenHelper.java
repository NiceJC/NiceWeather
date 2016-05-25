package wjc.niceweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wjc on 16/5/21.
 *
 * 创建数据库的同时创建三张表，分别用于保存 省，市，县 的各种数据信息
 *
 *
 */
public class NiceWeatherOpenHelper extends SQLiteOpenHelper{
    //三张表的建表语句
    public static final String CREATE_PROVINCE="CREATE TABLE province(_id INTEGER PRIMARY KEY AUTOINCREMENT ,province_name TEXT ,province_code TEXT )";
    public static final String CREATE_CITY="CREATE TABLE city(_id INTEGER PRIMARY KEY AUTOINCREMENT ,city_name TEXT ,city_code TEXT,province_id INTEGER)";
    public static final String CREATE_COUNTY="CREATE TABLE county(_id INTEGER PRIMARY KEY AUTOINCREMENT ,county_name TEXT,county_code TEXT,city_id INTEGER)";

    public NiceWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);   //创建province表
        db.execSQL(CREATE_CITY);       //创建city表
        db.execSQL(CREATE_COUNTY);     //创建county表


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
