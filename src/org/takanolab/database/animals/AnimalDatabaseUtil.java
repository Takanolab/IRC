/**
 * データベースの操作をするクラスを作ってみた
 * 
 * @author s0921122
 * @version 1.4
 */

package org.takanolab.database.animals;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AnimalDatabaseUtil {
	
	private static final String TAG = "AnimalDatabase";
	AnimalDatabaseHelper helper = null;
	SQLiteDatabase db = null;
	private static final Boolean Logflag = true;

	/**
	 * 何もないです
	 * 
	 * @version 1.0
	 */
	public AnimalDatabaseUtil(){
	}
	
	/**
	 * コンテキストを受け取りHelperを作成します．
	 * 
	 * @version 1.0
	 * @param con コンテキスト
	 */
	public AnimalDatabaseUtil(Context con){
		startUp(con);
	}

	/**
	 * 初期化
	 * @version 1.4
	 * @param con
	 */
	protected void startUp(Context con){
		if(Logflag) Log.d(TAG,"DatabaseUtil Create!");
		helper = new AnimalDatabaseHelper(con);
	}

	/**
	 * 
	 * データベースより検索を行います．
	 * @version 1.4
	 * @param searchColum 検索するカラム名
	 * @param searchValue 検索する値
	 * @param getColum 取得するカラム名
	 * @return 
	 */
	public Cursor search(String searchColum, String searchValue, String... getColum){
		if(Logflag) Log.d(TAG,"search : " + searchValue);
		db = helper.getReadableDatabase();
		
		String colums = "";
		for(String str : getColum){
			colums += str + ",";
		}
		String colum = colums.substring(0,colums.length()-1);
		
		Cursor csr = db.rawQuery("select " + colum + " from " + AnimalDatabaseHelper.TABLE_ANIMAL 
				+ " where " + searchColum + " = '" + searchValue + "'", null);
		return csr;
	}
	
	/**
	 * 
	 * データベースより検索を行います．
	 * 
	 * @version 1.4
	 * @param query SQL文
	 * @return 結果のカーソル
	 */
	public Cursor search(String query){
		if(Logflag) Log.d(TAG,"search : " + query);
		db = helper.getReadableDatabase();
		Cursor csr = db.rawQuery(query, null);
		return csr;
	}
	
	public String[] parsecsr(Cursor csr){
		String[] str = new String[csr.getCount()];
		int i=0;
		while(csr.moveToNext()){
			str[i] = csr.getString(1);
			i++;
		}
		return str;
	}
	
	/**
	 * 
	 * 指定したカラムのスコアを取得します．
	 * 
	 * @version 1.4
	 * @param name モデル名
	 * @param colum 取得するカラム
	 * @return スコア
	 */
	public int getScore(String name, String colum){
		Cursor csr = search(AnimalDatabaseHelper.COLUM_NAME, name, colum);
		int score = csr.getInt(0);
		csr.close();
		return score;
	}
	
	/**
	 * 
	 * データベースへ挿入します．
	 * 
	 * @version 1.1
	 * @param name モデル名
	 * @param colum カラム名
	 * @param num 数値
	 * @return 成功ならばLowid,失敗なら-1
	 */
	private long insert(String name, String colum, int num){
		if(Logflag) Log.d(TAG,"insertData : " + name);
		db = helper.getWritableDatabase();
		db.beginTransaction();
		
		long re = 0;
		ContentValues val = new ContentValues();
		try{
			val.put(AnimalDatabaseHelper.COLUM_NAME, name);
			val.put(colum, num);
			re = db.insert(AnimalDatabaseHelper.TABLE_ANIMAL, null,val);
			db.setTransactionSuccessful();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.endTransaction();
			val.clear();
		}
		return re;
	}
	
	public long insert(String name, String colum, String str){
		if(Logflag) Log.d(TAG,"insertData : " + name);
		db = helper.getWritableDatabase();
		db.beginTransaction();
		
		long re = 0;
		ContentValues val = new ContentValues();
		try{
			val.put(AnimalDatabaseHelper.COLUM_NAME, name);
			val.put(colum, str);
			re = db.insert(AnimalDatabaseHelper.TABLE_ANIMAL, null,val);
			db.setTransactionSuccessful();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.endTransaction();
			val.clear();
		}
		return re;
	}
	
	/**
	 * データベースへ挿入します
	 * @param val
	 * @return
	 */
	private long insert(ContentValues val){
		if(Logflag) Log.d(TAG,"insertData" + val.getAsString(AnimalDatabaseHelper.COLUM_NAME));
		db = helper.getWritableDatabase();
		db.beginTransaction();

		long re = 0;
		try{
			re = db.insert(AnimalDatabaseHelper.TABLE_ANIMAL, null,val);
			db.setTransactionSuccessful();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.endTransaction();
			val.clear();
		}
		return re;
	}

	/**
	 * 指定したレコードを削除します．
	 * 
	 * @version 1.1
	 * @param name モデルの名前
	 * @return 適用レコード数
	 */
	public int delete(String name){
		if(Logflag) Log.d(TAG,"delete : " + name);
		db = helper.getWritableDatabase();
		db.beginTransaction();
		
		int re=0;
		try{
			re =  db.delete(AnimalDatabaseHelper.TABLE_ANIMAL, AnimalDatabaseHelper.COLUM_NAME + " = '" + name + "'", null);
		db.setTransactionSuccessful();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
		return re;
	}
	
	/**
	 * 指定したテーブルを削除します．<br>
	 * 
	 * @version 1.1
	 * @param tableName 削除するテーブル名
	 */
	private void deleteTable(String tableName){
		if(Logflag) Log.d(TAG,"Delete Table");
		db = helper.getWritableDatabase();
		db.beginTransaction();
		db.execSQL("delete from " + tableName);
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	/**
	 * 現在時間をアップデートします．<br>
	 * 格納例  「2012-10-11 22:46:13」
	 * 
	 * @version 1.1
	 * @param name モデル名
	 */
	private void setTimeStamp(String name){
		/*
		StringBuilder str = new StringBuilder();
		str.append("UPDATE ").append(AnimalDatabaseHelper.TABLE_MANIPULATION).append(" SET ")
		.append(AnimalDatabaseHelper.COLUM_DATE_HOUR).append(" = datetime('now', 'localtime') WHERE ")
		.append(AnimalDatabaseHelper.COLUM_NAME).append(" = '").append(name).append("'");
		
		db.execSQL(new String(str));
		*/
	}
	
	/**
	 * 終了処理．<br>
	 * DatabaseHelperとSQLiteDatabaseをcloseします．
	 * 
	 * @version 1.0
	 */
	public void close(){
		db.close();
		helper.close();
	}
	
	/**
	 * テーブル削除して、新しくテーブル作成します．<br>
	 * ついでにオートインクリメントもリセット．
	 * 
	 * @version 1.2
	 * @param tableName 再生成するテーブル名
	 */
	public void reCreateTable(String tableName){
		deleteTable(tableName);
		helper.createTable(db);
		resetAutoincrement(tableName);
	}
	
	/**
	 * テーブルのオートインクリメントをリセットします．
	 * 
	 * @version 1.2
	 * @param tableName リセットしたいテーブル名
	 */
	private void resetAutoincrement(String tableName){
		db.execSQL("update sqlite_sequence set seq = 0 where name='" + tableName + "'");
	}

	public void exportCsv(){
		CsvUtilforAnimal writer = new CsvUtilforAnimal(CsvUtilforAnimal.WRITE_MODE);
		Cursor csr = search("select * from " + AnimalDatabaseHelper.TABLE_ANIMAL);
		while(csr.moveToNext()){
			writer.add(createCsvLine(csr));
		}
		writer.close();
		Log.d("CSV","CSV Export!");
	}
	
	private String createCsvLine(Cursor csr){
		StringBuilder builder = new StringBuilder();
		builder.append(csr.getInt(0)).append(",")
		.append(csr.getString(1)).append(",")
		.append(csr.getInt(2)).append(",")
		.append(csr.getInt(3)).append(",")
		.append(csr.getInt(4));
		return builder.toString();
	}
	
	public void importCsv(){
		CsvUtilforAnimal reader = new CsvUtilforAnimal(CsvUtilforAnimal.READ_MODE);
		ArrayList<String> line = reader.importCSV();
		String[] colums;
		for(String str : line){
			Log.d("CSV","Loop:" + str);
			colums = str.split(",", -1);
			insert(setValues(colums));	
		}
		reader.close();
		Log.d("CSV","CSV Import!" + line.size());
	}

	private ContentValues setValues(String[] strs){
		ContentValues val = new ContentValues();
		val.put(AnimalDatabaseHelper.COLUM_ID, strs[0]);
		val.put(AnimalDatabaseHelper.COLUM_NAME, strs[1]);
		val.put(AnimalDatabaseHelper.COLUM_CLASS, strs[2]);
		val.put(AnimalDatabaseHelper.COLUM_FAMILY, strs[3]);
		val.put(AnimalDatabaseHelper.COLUM_ORDER, strs[4]);
		return val;
	}

	
}
