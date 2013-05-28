/**
 * データベースクラス
 * 
 * @author s0921122
 * @version 1.1
 * 
 */

package org.takanolab.database.animals;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AnimalDatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "ANIMAL_DATABASE";
	public static final String TABLE_ANIMAL = "ANIMAL";
	
	// カラム名フィールド
	public static final String COLUM_ID = "_id";
	public static final String COLUM_NAME = "name";
	public static final String COLUM_CLASS = "class";
	public static final String COLUM_FAMILY = "Family";
	public static final String COLUM_ORDER = "orders";
			
	public AnimalDatabaseHelper(Context con){
		super(con, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		createTable(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
	public void createTable(SQLiteDatabase database){
        try{
        	String sql;
        	// テーブル作成
        	
//        	sql = "CREATE TABLE " + TABLE_NAME + " ("
//        			+ COLUM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//        			+ COLUM_MODEL_NAME + " TEXT UNIQUE NOT NULL,"
//        			+ COLUM_MOVE + " INTEGER,"
//        			+ COLUM_ROTATE + " INTEGER,"
//        			+ COLUM_SCALE + " INTEGER,"
//        			+ COLUM_CAPTURE + " INTEGER,"
//        			+ COLUM_MARKER + " INTEGER,"
//        			+ COLUM_USER_SELECT + " INTEGER,"
//        			+ COLUM_TIME_FRAME + " TIME,"
//        			+ COLUM_FAVORITE + " INTEGER,"
//        			+ COLUM_DATE_HOUR + " DATE"
//        			+ ")";
        	
        	StringBuilder sr = new StringBuilder()
        	.append("CREATE TABLE ").append( TABLE_ANIMAL ).append(" ( ")
        	.append( COLUM_ID ).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
        	.append( COLUM_NAME ).append(" TEXT UNIQUE NOT NULL,")
        	.append( COLUM_CLASS ).append(" TEXT ,")
        	.append( COLUM_FAMILY ).append(" TEXT ,")
        	.append( COLUM_ORDER ).append(" TEXT ")
        	.append(" )");
        	sql = new String(sr);
        	
        	database.execSQL(sql);
        }catch(Exception e){
        	// テーブル作成失敗かすでにあるとき
        	e.printStackTrace();
        }
	}

}
