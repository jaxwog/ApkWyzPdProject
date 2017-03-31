package com.zzu.wyz.util;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class WyzDataDb extends Activity {
	// 定义查询到的数据通过get方法得到数据，并在扫描框中显示
	private String dataName, dataId, dataCode, dataGg, dataJldw, dataDj,
			dataSpsl;
	// 定义查询返回值，get，set方法判断数据库中是否查询到数据
	private String resultString = null;
	
	

	public String getResultString() {
		return resultString;
	}

	public void setResultString(String resultString) {
		this.resultString = resultString;
	}

	public String getDataName() {
		return dataName;
	}

	public String getDataId() {
		return dataId;
	}

	public String getDataCode() {
		return dataCode;
	}

	public String getDataGg() {
		return dataGg;
	}

	public String getDataJldw() {
		return dataJldw;
	}

	public String getDataDj() {
		return dataDj;
	}

	public String getDataSpsl() {
		return dataSpsl;
	}

	/**
	 * 
	 * @param db
	 *            管理和操作SQLite数据库
	 * @param id
	 *            扫描到的条码号
	 * @return 判断是否查询到数据（提供在修改时候的条件）
	 */
	private String query(SQLiteDatabase db, String id) {
		// 打开或者创建数据库
		db = SQLiteDatabase.openOrCreateDatabase(MetaStaticData.SDCARD_PATH
				+ MetaStaticData.DATABASE_PDNAME, null);

		try {

			String sql = "Select  " + MetaStaticData.SQL_ID + ","
					+ MetaStaticData.SQL_NAME + "," + MetaStaticData.SQL_CODE + ","
					+ MetaStaticData.SQL_NAME + "," + MetaStaticData.SQL_NAME + ","
					+ MetaStaticData.SQL_NAME + "," + MetaStaticData.SQL_NAME
					+ "  from  " + MetaStaticData.TABLE_NAME + " where"
					+ MetaStaticData.SQL_ID + " = " + id;
			Cursor cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			// 查询到的数据保存到字符串中
			dataId = cursor
					.getString(cursor.getColumnIndex(MetaStaticData.SQL_ID));
			dataName = cursor.getString(cursor
					.getColumnIndex(MetaStaticData.SQL_NAME));
			dataCode = cursor.getString(cursor
					.getColumnIndex(MetaStaticData.SQL_CODE));
			dataGg = cursor
					.getString(cursor.getColumnIndex(MetaStaticData.SQL_GG));
			dataDj = cursor
					.getString(cursor.getColumnIndex(MetaStaticData.SQL_DJ));
			dataJldw = cursor.getString(cursor
					.getColumnIndex(MetaStaticData.SQL_JLDW));
			dataSpsl = cursor.getString(cursor
					.getColumnIndex(MetaStaticData.SQL_SPSL));
			resultString = dataId;
		} catch (Exception e) {
			Toast.makeText(this, "请检查输入的编号是否正确", Toast.LENGTH_SHORT).show();
		}
		return resultString;
	}

	/**
	 * 
	 * @param db
	 *            管理和操作SQLite数据库
	 * @param id
	 *            更新时候的商品编码
	 * @param spslTemp
	 *            商品的数量
	 */
	private void update(SQLiteDatabase db, String id, String spslTemp) {
		db = SQLiteDatabase.openOrCreateDatabase(MetaStaticData.SDCARD_PATH
				+ MetaStaticData.DATABASE_PDNAME, null);

		try {

			String sql = "update  " + MetaStaticData.TABLE_NAME + "  set  "
					+ MetaStaticData.SQL_SPSL + "=" + MetaStaticData.SQL_SPSL + " + "
					+ spslTemp + "  where  " + MetaStaticData.SQL_ID + " = " + id;

			db.execSQL(sql);

			// spslTemp = null;
			Toast.makeText(this, "商品：" + id + "添加成功", Toast.LENGTH_SHORT)
					.show();
		} catch (Exception e) {
			Toast.makeText(this, "请检查扫描的条码是否正确", Toast.LENGTH_SHORT).show();
		}
	}

}
