package com.zzu.wyz.util;

import android.os.Environment;

public interface MetaStaticData {
	//���������ַ���ļ���
	public static final String FILENAME = "IP";
	// ������ҳͼ��Ϊ3��fragment
	public static final int FLAG_TAB = 3;
	// ɨ�����action
	public final static String SCAN_ACTION = "urovo.rcv.message";
	
	// �������ݵ�ַ
	public static final String URL_PDPATH = ":8098/db/PD_DB/pd.db";
	public static final String URL_CGPATH = ":8098/db/CG_DB/cg.db";
	public static final String URL_HJPATH = ":8098/db/HJ_DB/hj.db";
	//public static final String URL_PDLOADPATH = "http://192.168.0.188:8098/db/PD_DB/pd.db";
	//public static final String URL_CGLOADPATH = "http://192.168.0.188:8098/db/CG_DB/cg.db";
	//public static final String URL_HJLOADPATH = "http://192.168.0.188:8098/db/HJ_DB/hj.db";
			//"http://192.168.0.188:8012/xml/2015110316395194.db";
	// �ϴ����ݵ�ַ
	public static final String URL_UPPATh = ":8098";
	//public static final String URL_UPPATh = "http://192.168.0.188:8098";
	// �ļ�����·��
	public static final String SDCARD_PATH = Environment
			.getExternalStorageDirectory().toString() + "/Wyz/";
	public static final String SDCARD_EXPORTPATH = Environment
			.getExternalStorageDirectory().toString() + "/DaShang/";
	
	public static final String SDCARD_COPYPATH = Environment
			.getExternalStorageDirectory().toString() + "/Wyz/copy/";
	// ���ص����ݿ����֣��ļ����֣�
	public static final String DATABASE_PDNAME = "pd.db";
	public static final String DATABASE_CGNAME = "cg.db";
	public static final String DATABASE_HJNAME = "hj.db";
	// ���ݿ����
	public static final String TABLE_NAME = "pd";
	// ���ݿ��Ӧ���ֶκ�
	public static final String SQL_ID = "id";
	public static final String SQL_NAME = "name";
	public static final String SQL_CODE = "code";
	public static final String SQL_GG = "gg";
	public static final String SQL_JLDW = "jldw";
	public static final String SQL_DJ = "dj";
	public static final String SQL_SPSL = "spsl";
}
