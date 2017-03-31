package com.zzu.wyz.util;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class WyzDataDb extends Activity {
	// �����ѯ��������ͨ��get�����õ����ݣ�����ɨ�������ʾ
	private String dataName, dataId, dataCode, dataGg, dataJldw, dataDj,
			dataSpsl;
	// �����ѯ����ֵ��get��set�����ж����ݿ����Ƿ��ѯ������
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
	 *            ����Ͳ���SQLite���ݿ�
	 * @param id
	 *            ɨ�赽�������
	 * @return �ж��Ƿ��ѯ�����ݣ��ṩ���޸�ʱ���������
	 */
	private String query(SQLiteDatabase db, String id) {
		// �򿪻��ߴ������ݿ�
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
			// ��ѯ�������ݱ��浽�ַ�����
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
			Toast.makeText(this, "��������ı���Ƿ���ȷ", Toast.LENGTH_SHORT).show();
		}
		return resultString;
	}

	/**
	 * 
	 * @param db
	 *            ����Ͳ���SQLite���ݿ�
	 * @param id
	 *            ����ʱ�����Ʒ����
	 * @param spslTemp
	 *            ��Ʒ������
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
			Toast.makeText(this, "��Ʒ��" + id + "��ӳɹ�", Toast.LENGTH_SHORT)
					.show();
		} catch (Exception e) {
			Toast.makeText(this, "����ɨ��������Ƿ���ȷ", Toast.LENGTH_SHORT).show();
		}
	}

}
