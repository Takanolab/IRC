package org.takanolab.cache.irc;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class CacheHelper extends CacheDatabaseUtils{

	// ログ出力用
	private static final String TAG = "CacheHelper";
	// キャッシュファイルの入出力先のパス
	private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/modelcache/" + "models.obj";
	// 保持するキャッシュの数
	private static final int CACHE_MAX = 5;
	// ファイルから読み込むマップ
	HashMap<String,byte[]> importCache;
	
	private int reCastNum = 1;

	
	/**
	 * コンストラクタ
	 * 
	 * @param con
	 */
	public CacheHelper(Context con){
		startup(con);
		fileInput();
	}
	
	public void close(){
		write_object(importCache, PATH);
		super.close();
		clearCacheTable();
	}
	
	/**
	 * コンストラクタ
	 * すでにキャッシュファイルが存在するとき読み込みます
	 * 
	 * @author s0921122
	 * @version 1.2
	 */
	@SuppressWarnings("unchecked")
	public void fileInput(){
		File file = new File(PATH);
		if(file.exists()){
			// ファイルが存在する
			Log.d(TAG,"cache file true");
			if(file.length() > 0){
				// 内容がある
				try{
					Log.d(TAG,"cache import");
					// データ読み込み
					importCache = (HashMap<String, byte[]>) read_object(PATH);
				}catch(Exception e){
					e.printStackTrace();
				}
			}else{
				// 内容がない
				importCache = new HashMap<String, byte[]>();
			}
		}else{
			// ファイルが存在しない
			Log.d(TAG,"cache file false");
			try{
				// ファイル作成
				Log.d(TAG,"cache file create");
				file.createNewFile();
				importCache = new HashMap<String, byte[]>();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setCacheData(String name, InputStream is){
		setCacheData(name, "unknown", 100, is);
	}
	public void setCacheData(String name, String category, InputStream is){
		setCacheData(name, category, 100, is);
	}
	public void setCacheData(String name, int limit, InputStream is){
		setCacheData(name, "unknown", limit, is);
	}
	
	/**
	 * キャッシュデータをセットする
	 * 
	 * @author s0921122
	 * @version 1.1
	 * @param name
	 * @param is
	 */	
	public void setCacheData(String name, String category, int limit, InputStream is){
		Log.d(TAG,"Cache Set " + name);
		reCastLimit(reCastNum);
		updateLimitCategory(category, limit/2);

		if(importCache.size() >= CACHE_MAX){
			Log.d(TAG,"Remove Low Priority Cache");
			removeLowLimitCache();
		}

		insertorUpdate(name, category, limit);
		importCache.put(name, converttoBytes(is));
	}

	public InputStream getCacheData(String name){
		update(CacheDatabase.COLUMN_NAME, name, CacheDatabase.COLUMN_LIMIT, 50);
		return converttoStream(importCache.get(name));
	}

	/**
	 * キャッシュデータが存在するか
	 * 
	 * @author s0921122
	 * @version 1.0
	 * @param name modelname
	 * @return 
	 */
	public boolean isCacheData(String name){
		if(importCache.containsKey(name)){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * キャッシュマップの要素数を返す
	 * 
	 * @author s0921122
	 * @version 1.0
	 * @return
	 */
	public int getSize(){
		return importCache.size();
	}

	/**
	 * キャッシュマップのイテレータを返す
	 * 
	 * @author s0921122
	 * @version 1.0
	 * @return
	 */
	public Iterator<String> getMapIterator(){
		return importCache.keySet().iterator();
	}

	/**
	 * キャッシュの名前のリストを返す
	 * @return
	 */
	public String[] getCacheList(){
		Iterator<String> itr = importCache.keySet().iterator();
		String[] list = new String[importCache.size()];
		int i = 0;
		while(itr.hasNext()){
			list[i] = itr.next();
		}
		return list;
	}
	
	private void removeLowLimitCache(){
		String item = getLimitLowerItemNameforFirst();
		importCache.remove(item);
		dead(item);
		Log.d(TAG,item + " is Dead");
	}
	
	/**
	 * キャッシュデータを削除
	 * 
	 * @author s0921122
	 * @version 1.0
	 * @param name
	 * @return
	 */
	public boolean DeleteCacheData(String name){
		try{
			importCache.remove(name);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * キャッシュ全体を削除
	 * 
	 * @author s0921122
	 * @version 1.0
	 * 
	 */
	public void clearCacheTable(){
		importCache.clear();
	}

	/**
	 * キャッシュデータをファイルに書き出す
	 * 
	 * @author s0921122
	 * @version 1.0
	 */
	public void OutPutCache(){
		write_object(importCache, PATH);
	}

	/**
	 * オブジェクトをファイルに書き出す
	 * 
	 * @version 1.0
	 * @param obj Object
	 * @param file FilePath
	 * @return
	 */
	private static boolean write_object(Object obj,String file){
		try {
			FileOutputStream outFile = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(outFile);
			out.writeObject(obj);
			out.close();
			outFile.close();
		} catch(Exception e) {
			Log.d(TAG,"FileOutput");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * ファイルを読みこみオブジェクトを生成
	 * 
	 * @version 1.0
	 * @param file FilePath
	 * @return
	 */
	private static Object read_object(String file){
		Object obj=new Object();
		try {
			FileInputStream inFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(inFile);
			obj = in.readObject();
			in.close();
			inFile.close();
		} catch(Exception e) {
			Log.d(TAG,"FileInput");
			e.printStackTrace();
		}
		return obj;
	}
	
	/**
	 * InputStreamをバイト配列に変換する
	 *
	 * @param is
	 * @return バイト配列
	 */
	private byte[] converttoBytes(InputStream is) {
		long start = System.currentTimeMillis();
	Log.d(TAG,"Start : "+ start);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStream os = new BufferedOutputStream(baos);

		int c;
		try {
			while ((c = is.read()) != -1) {
				os.write(c);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.flush();
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		long end = System.currentTimeMillis();
		Log.d(TAG,"End : "+ end);
		Log.d(TAG,"InputStream → byte : " + (end - start));

		return baos.toByteArray();
	}

	/**
	 * Byte配列からInputStreamに変換する
	 * 
	 * @param bytes
	 * @return InputStream
	 */
	private InputStream converttoStream(byte[] bytes){
		long start = System.currentTimeMillis();
		Log.d(TAG,"Start : " + start);

		InputStream bais = new ByteArrayInputStream(bytes);

		long end = System.currentTimeMillis();
		Log.d(TAG,"End : " + end);
		Log.d(TAG,"Byte → InputStream : " + (end - start));

		return bais;
	}
}