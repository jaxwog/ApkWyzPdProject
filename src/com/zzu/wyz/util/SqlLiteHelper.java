package com.zzu.wyz.util;


import java.util.ArrayList;
import java.util.Currency;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase; 
import android.util.Log;

public class SqlLiteHelper {
	SQLiteDatabase db;
	Cursor cursor;
	private String pathString;
	/*
	 * 初始化操作
	 */
	public SqlLiteHelper(String path)
	{ 
//		db = SQLiteDatabase.openOrCreateDatabase(WYZMetaData.SDCARD_PATH
//				+ WYZMetaData.DATABASE_NAME, null); 
		pathString=path;
		db=SQLiteDatabase.openOrCreateDatabase(path, null);  
		 Log.e("SqlError", "握手协议第一步："); 
	} 
    public void Close()
    {
    	if(cursor!=null)
			cursor.close(); 
    	if(db!=null&&db.isOpen())
		db.close();
    }
    public void Open()
    {
    	if(db==null||!db.isOpen()) 
    	 db=SQLiteDatabase.openOrCreateDatabase(pathString, null);
    }
	// 定义查询到的数据通过get方法得到数据，并在扫描框中显示
	 private String dataName, dataId, dataCode, dataSiteno,  dataDj;
				int	dataSpsl;
			// 定义查询返回值，get，set方法判断数据库中是否查询到数据
	 private String resultString = null;
			
			/*
			 * 数据操作类
			 */
	 WYZMetaData wyzMetaData=new WYZMetaData();
			
	
	 public String selectNumber(String siteno){
		 String temp;
		 Open();
		 String sql = "select count(1) as count from goodspddetail where siteno='"+siteno+"'";
		 cursor =  db.rawQuery(sql, null);
		 Cursor cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			return cursor.getString(0);
		  
	 }
	 
	/**
	 * 
	 * @param db
	 *            管理和操作SQLite数据库
	 * @param id
	 *            扫描到的条码号
	 * @return 判断是否查询到数据（提供在修改时候的条件）
	 */
	public WYZMetaData query(Boolean booType,String id,String siteno) {
		// 打开或者创建数据库 
		 WYZMetaData modelData;
		 Open();
		try {
			String Where=" 1=1 ";
			if(booType)//条码
				Where=" and b.sptm='"+id+"'";
			else //编码
				Where=" and b.spbm='"+id+"'";
			
			String sql = "select siteno,b.spbm,b.sptm,b.spsl,a.spmc,a.spdj  from goodspddetail b,goods a" +//
                             " WHERE  b.sptm=a.sptm  and siteno='"+siteno+"' "
					   +Where; 
			 cursor= db.rawQuery(sql, null);
	Log.e("SqlError", "SqlError>>>>"+cursor.getCount()+"sql:【"+sql+"】");
		  if(cursor!=null&&cursor.getCount()>0)
		   { 
			  modelData= getWyzMetaData(cursor,true);
		  }
		  else{
			  if(booType)//条码
					Where="  sptm='"+id+"'";
				else //编码
					Where="  spbm='"+id+"'";
			  sql = "select  spbm, sptm, spmc, spdj  from  goods " +//
                      " WHERE  "+Where;
			  cursor=db.rawQuery(sql, null);
			  Log.e("SqlError", "SqlError>>>>"+sql);
			  if(cursor!=null&&cursor.getCount()>0)
			   { 
			  modelData=getWyzMetaData(cursor, false); 
			   }
			  else{
				return null;  
			   
			  }
			 }
			
		} catch (Exception e) {
			 Log.e("SqlError", "查询错误："+e.toString()); 
			return null;
		}
		return modelData;
	}
    public WYZMetaData getWyzMetaData(Cursor cursor,Boolean tempbool )
    {
    	 WYZMetaData modelData=new WYZMetaData();
    	 Open();
    	cursor.moveToFirst(); 
		// 查询到的数据保存到字符串中
		dataId = cursor
				.getString(cursor.getColumnIndex("sptm")); 
		dataName = cursor.getString(cursor
				.getColumnIndex("spmc")); 
		dataCode = cursor.getString(cursor
				.getColumnIndex("spbm"));  
		dataDj = cursor
				.getString(cursor.getColumnIndex("spdj"));
//		Log.e("SqlError", "SqlError>>>>"+dataDj); 
		modelData.SQL_Id=dataId;
		modelData.SQL_Code=dataCode;
		modelData.SQL_Name=dataName;
		modelData.SQL_Dj=dataDj;
		if(tempbool)
		{
		 dataSiteno = cursor.getString(cursor
					.getColumnIndex("siteno"));
		 //Log.e("SqlError", "SqlError>>>>"+dataSiteno);
		 dataSpsl = cursor.getInt(cursor
					.getColumnIndex("spsl"));
		 Log.e("SqlError", "SqlError>>>>"+dataSpsl);
		modelData.SQL_Siteno=dataSiteno;
		modelData.SQL_Spsl=dataSpsl;
		}
		else {
			modelData.SQL_Siteno="";
			modelData.SQL_Spsl=0;
		}
		
		
		return modelData;
    }
	/**
	 *  
	 * @param Stype
	 *             商品条码或者编码
	 *            如果已经有该商品，更新数量，否则添加此条数据
	 * @param model
	 *            商品类
	 */
	public String update(Boolean Stype,WYZMetaData model) {
		 
		try {
			String Where=" siteno='"+model.SQL_Siteno+"'";
			if(Stype)//条码
				Where+=" and sptm='"+model.SQL_Id+"'";
			else //编码
				Where+=" and spbm='"+model.SQL_Code.toString().trim()+"'";
			
			String sql=" select count(1) from goodspddetail where "+Where; 
			Cursor cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			if(cursor.getLong(0)>0)
			   sql = "update goodspddetail set spsl = '"+model.SQL_Spsl+"' where "+Where;  
			else
			   sql=" insert into goodspddetail (spbm,sptm,spsl,siteno) values ('"+model.SQL_Code+"','"+model.SQL_Id+"',"+model.SQL_Spsl+",'"+model.SQL_Siteno+"')";
			Log.e("SqlError", "SqlError>>>>"+sql);
			db.execSQL(sql);//Android无返回值 
			return "条码："+model.SQL_Id +" ;商品：" + model.SQL_Name + "添加成功,数量："+model.SQL_Spsl;
				 
		} catch (Exception e) {
			Log.e("SqlError", "查询错误："+e.toString()); 
			return "请检查扫描的条码是否正确";
		}
	}
	
	
	public WYZMetaData QueryGoodsInfo(Boolean booType,String id){
		Boolean resultBoolean=false;
		 Open();
		try {
			String Where=" 1=1 ";
			if(booType)//条码
				Where=" sptm= "+id;
			else //编码
				Where=" spbm= "+id;
			
			String sql = "select  spbm, sptm, spmc, spdj  from  goods " +//
                             " WHERE "
					   +Where; 
		 cursor= db.rawQuery(sql, null);
		 if(cursor!=null&&cursor.getCount()>0)
		 { 
			 WYZMetaData wyzMetaData=new WYZMetaData();
			 wyzMetaData=getWyzMetaData(cursor,false);
			 return wyzMetaData;   
		 }
		 else{
			return null;
	         }
		}
		catch (Exception e) {
			 Log.e("SqlError", "查询错误："+e.toString()); 
			return null;
		} 
	}
	/*
	 * 查询某条码已经盘点的信息【货架，数量】集合
	 */
	public ArrayList<WYZMetaData> GetSiteNoList(Boolean Stype,String id){ 
	 
		ArrayList<WYZMetaData>  modelData=new ArrayList<WYZMetaData>();
		 Open();
		try {
			String Where=" 1=1 ";
			if(Stype)//条码
				Where="  sptm='"+id+"'";
			else //编码
				Where="  spbm='"+id+"'";
			
			String sql = "select siteno,spsl from goodspddetail  " +//
                            " WHERE "
					   +Where; 
		 cursor= db.rawQuery(sql, null);
		 while(cursor.moveToNext())
	     {
		   WYZMetaData wyzMetaData=new WYZMetaData();
		      
		   wyzMetaData.SQL_Siteno= cursor.getString(cursor.getColumnIndex("siteno")); 
		   wyzMetaData.SQL_Spsl= cursor.getInt(cursor.getColumnIndex("spsl")); 
		   

		   modelData.add(wyzMetaData);
		  
		  // System.out.println( "#####modelData=="+modelData.get(0).toString());
	      }
		 return modelData;
		}
		catch(Exception exception){
			 Log.e("SqlError", "查询错误："+exception.toString()); 
				return null;
		}
	}
	/**
	 * 
	 * @param Stype
	 * @param id
	 * @return
	 */
	public Boolean delete() {
		 
		try {
		
			
			long result=0; 
			result= db.delete("goodspddetail", null, null) ; 
			Log.e("SqlError", "SqlError>>>>");
			//db.execSQL(sql);//Android无返回值 
			if(result>0)
			return true;
			else {
				
				return false;
			} 
		} catch (Exception e) {
			Log.e("SqlError", "查询错误："+e.toString()); 
			return false;
		}
	}
	
	/**
     * 更新采购信息
     * @param Stype 类型 true 条码 false 编码
     * @param model 类
     * @return
     */
    public Boolean updateCG(Boolean Stype,WYZCGData model)
    {
    	try {
			String Where="";
			if(Stype)//条码
				Where+=" sptm='"+model.SQL_Id+"'";
			else //编码
				Where+=" spbm='"+model.SQL_Code.toString().trim()+"'";
			 
			long result=0; 
			   ContentValues values=new ContentValues(); 
			   values.put("cgsl", model.SQL_CGSL); 
			  result= db.update("goods", values, Where, null);
			  if(result>0)
				  return true;
			  else 
				  return false;
			  
			}
    	catch(Exception exception){
    		Log.e("SqlError", "查询错误："+exception.toString()); 
    		return false;
    	}
    }
    /**
     * 查询采购信息
     * @param Stype 类型 true 条码 false 编码
     * @param id 编码或者条码
     * @return
     */
    public WYZCGData QueryCGData(Boolean Stype,String id)
    {
    	Boolean resultBoolean=false;
		 Open();
		try {
			String Where=" 1=1 ";
			if(Stype)//条码
				Where=" sptm="+id;
			else //编码
				Where="  spbm='"+id;
			
			String sql = "select spbm,sptm,spmc,spsl,xssl,zxsl,cgsl  from  goods " +//
                            " WHERE "
					   +Where; 
		 cursor= db.rawQuery(sql, null);
		 if(cursor!=null&&cursor.getCount()>0)
		 { 
			 WYZCGData wyzMetaData=getCGData(cursor);
			 return wyzMetaData;   
		 }
		 else{
			return null;
	         }
		}
		catch (Exception e) {
			 Log.e("SqlError", "查询错误："+e.toString()); 
			return null;
		} 
    }
    /**
     * 获取采购信息
     * @param cursor 数据库返回指针
     * @return
     */
    public WYZCGData getCGData(Cursor cursor)
    {
    	WYZCGData modelData=new WYZCGData(); 
    	cursor.moveToFirst(); 
		// 查询到的数据保存到字符串中
    	modelData.SQL_Id = cursor
				.getString(cursor.getColumnIndex("sptm")); 
    	modelData.SQL_Name= cursor.getString(cursor
				.getColumnIndex("spmc")); 
		modelData.SQL_Code = cursor.getString(cursor
				.getColumnIndex("spbm"));  
		modelData.SQL_SPSL= cursor
				.getInt(cursor.getColumnIndex("spsl"));
		modelData.SQL_XSSL= cursor
				.getInt(cursor.getColumnIndex("xssl"));
		modelData.SQL_ZXSL= cursor
				.getInt(cursor.getColumnIndex("zxsl"));
		modelData.SQL_CGSL= cursor
				.getInt(cursor.getColumnIndex("cgsl"));
//		Log.e("SqlError", "SqlError>>>>"+dataDj);  
		 Log.e("SqlError", "SqlError>>>>"+modelData.SQL_CGSL ); 
		return modelData;
    }
    /**
     * 删除采购信息
     * @return 
     */
    public Boolean deleteCG() {
		 
		try {
			
			long result=0; 
			   ContentValues values=new ContentValues(); 
			   values.put("cgsl", 0); 
			  result= db.update("goods", values, null, null);
			  if(result>0)
				  return true;
			  else 
				  return false;
		} catch (Exception e) {
			Log.e("SqlError", "查询错误："+e.toString()); 
			return false;
		}
	}
    
    /**
     * 
     * @param Stype
     * @param id
     * @return
     */
    public WYZHJData QueryhjData(Boolean Stype,String id)
    {
    	Boolean resultBoolean=false;
		 Open();
		try {
			String Where=" 1=1 ";
			if(Stype)//条码
				Where=" sptm="+id;
			else //编码
				Where="  spbm='"+id;
			
			String sql = "select spbm,sptm,spmc,spdj  from  goods " +//
                            " WHERE "
					   +Where; 
		 cursor= db.rawQuery(sql, null);
		 if(cursor!=null&&cursor.getCount()>0)
		 { 
			 WYZHJData wyzHJData=getHJData(cursor);
			 return wyzHJData;   
		 }
		 else{
			return null;
	         }
		}
		catch (Exception e) {
			 Log.e("SqlError", "查询错误："+e.toString()); 
			return null;
		} 
    }
    
    public WYZHJData getHJData(Cursor cursor)
    {
    	WYZHJData modelData=new WYZHJData(); 
    	cursor.moveToFirst(); 
		// 查询到的数据保存到字符串中
    	modelData.SQL_Id = cursor
				.getString(cursor.getColumnIndex("sptm")); 
    	modelData.SQL_Name= cursor.getString(cursor
				.getColumnIndex("spmc")); 
		modelData.SQL_Code = cursor.getString(cursor
				.getColumnIndex("spbm"));  
		
		modelData.SQL_HJ= cursor
				.getDouble(cursor.getColumnIndex("spdj"));

		return modelData;
    }
    
}
