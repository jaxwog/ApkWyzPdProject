package com.zzu.wyz.util;

import android.os.Environment;

public interface MetaStaticData {
	//保存网络地址的文件名
	public static final String FILENAME = "IP";
	// 监听主页图标为3的fragment
	public static final int FLAG_TAB = 3;
	// 扫描结束action
	public final static String SCAN_ACTION = "urovo.rcv.message";
	
	// 下载数据地址
	public static final String URL_PDPATH = ":8098/db/PD_DB/pd.db";
	public static final String URL_CGPATH = ":8098/db/CG_DB/cg.db";
	public static final String URL_HJPATH = ":8098/db/HJ_DB/hj.db";
	//public static final String URL_PDLOADPATH = "http://192.168.0.188:8098/db/PD_DB/pd.db";
	//public static final String URL_CGLOADPATH = "http://192.168.0.188:8098/db/CG_DB/cg.db";
	//public static final String URL_HJLOADPATH = "http://192.168.0.188:8098/db/HJ_DB/hj.db";
			//"http://192.168.0.188:8012/xml/2015110316395194.db";
	// 上传数据地址
	public static final String URL_UPPATh = ":8098";
	//public static final String URL_UPPATh = "http://192.168.0.188:8098";
	// 文件下载路径
	public static final String SDCARD_PATH = Environment
			.getExternalStorageDirectory().toString() + "/Wyz/";
	public static final String SDCARD_EXPORTPATH = Environment
			.getExternalStorageDirectory().toString() + "/DaShang/";
	
	public static final String SDCARD_COPYPATH = Environment
			.getExternalStorageDirectory().toString() + "/Wyz/copy/";
	// 下载的数据库名字（文件名字）
	public static final String DATABASE_PDNAME = "pd.db";
	public static final String DATABASE_CGNAME = "cg.db";
	public static final String DATABASE_HJNAME = "hj.db";
	// 数据库表名
	public static final String TABLE_NAME = "pd";
	// 数据库对应的字段号
	public static final String SQL_ID = "id";
	public static final String SQL_NAME = "name";
	public static final String SQL_CODE = "code";
	public static final String SQL_GG = "gg";
	public static final String SQL_JLDW = "jldw";
	public static final String SQL_DJ = "dj";
	public static final String SQL_SPSL = "spsl";
}
