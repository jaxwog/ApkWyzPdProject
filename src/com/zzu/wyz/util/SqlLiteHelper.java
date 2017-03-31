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
	 * ��ʼ������
	 */
	public SqlLiteHelper(String path)
	{ 
//		db = SQLiteDatabase.openOrCreateDatabase(WYZMetaData.SDCARD_PATH
//				+ WYZMetaData.DATABASE_NAME, null); 
		pathString=path;
		db=SQLiteDatabase.openOrCreateDatabase(path, null);  
		 Log.e("SqlError", "����Э���һ����"); 
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
	// �����ѯ��������ͨ��get�����õ����ݣ�����ɨ�������ʾ
	 private String dataName, dataId, dataCode, dataSiteno,  dataDj;
				int	dataSpsl;
			// �����ѯ����ֵ��get��set�����ж����ݿ����Ƿ��ѯ������
	 private String resultString = null;
			
			/*
			 * ���ݲ�����
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
	 *            ����Ͳ���SQLite���ݿ�
	 * @param id
	 *            ɨ�赽�������
	 * @return �ж��Ƿ��ѯ�����ݣ��ṩ���޸�ʱ���������
	 */
	public WYZMetaData query(Boolean booType,String id,String siteno) {
		// �򿪻��ߴ������ݿ� 
		 WYZMetaData modelData;
		 Open();
		try {
			String Where=" 1=1 ";
			if(booType)//����
				Where=" and b.sptm='"+id+"'";
			else //����
				Where=" and b.spbm='"+id+"'";
			
			String sql = "select siteno,b.spbm,b.sptm,b.spsl,a.spmc,a.spdj  from goodspddetail b,goods a" +//
                             " WHERE  b.sptm=a.sptm  and siteno='"+siteno+"' "
					   +Where; 
			 cursor= db.rawQuery(sql, null);
	Log.e("SqlError", "SqlError>>>>"+cursor.getCount()+"sql:��"+sql+"��");
		  if(cursor!=null&&cursor.getCount()>0)
		   { 
			  modelData= getWyzMetaData(cursor,true);
		  }
		  else{
			  if(booType)//����
					Where="  sptm='"+id+"'";
				else //����
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
			 Log.e("SqlError", "��ѯ����"+e.toString()); 
			return null;
		}
		return modelData;
	}
    public WYZMetaData getWyzMetaData(Cursor cursor,Boolean tempbool )
    {
    	 WYZMetaData modelData=new WYZMetaData();
    	 Open();
    	cursor.moveToFirst(); 
		// ��ѯ�������ݱ��浽�ַ�����
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
	 *             ��Ʒ������߱���
	 *            ����Ѿ��и���Ʒ������������������Ӵ�������
	 * @param model
	 *            ��Ʒ��
	 */
	public String update(Boolean Stype,WYZMetaData model) {
		 
		try {
			String Where=" siteno='"+model.SQL_Siteno+"'";
			if(Stype)//����
				Where+=" and sptm='"+model.SQL_Id+"'";
			else //����
				Where+=" and spbm='"+model.SQL_Code.toString().trim()+"'";
			
			String sql=" select count(1) from goodspddetail where "+Where; 
			Cursor cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			if(cursor.getLong(0)>0)
			   sql = "update goodspddetail set spsl = '"+model.SQL_Spsl+"' where "+Where;  
			else
			   sql=" insert into goodspddetail (spbm,sptm,spsl,siteno) values ('"+model.SQL_Code+"','"+model.SQL_Id+"',"+model.SQL_Spsl+",'"+model.SQL_Siteno+"')";
			Log.e("SqlError", "SqlError>>>>"+sql);
			db.execSQL(sql);//Android�޷���ֵ 
			return "���룺"+model.SQL_Id +" ;��Ʒ��" + model.SQL_Name + "��ӳɹ�,������"+model.SQL_Spsl;
				 
		} catch (Exception e) {
			Log.e("SqlError", "��ѯ����"+e.toString()); 
			return "����ɨ��������Ƿ���ȷ";
		}
	}
	
	
	public WYZMetaData QueryGoodsInfo(Boolean booType,String id){
		Boolean resultBoolean=false;
		 Open();
		try {
			String Where=" 1=1 ";
			if(booType)//����
				Where=" sptm= "+id;
			else //����
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
			 Log.e("SqlError", "��ѯ����"+e.toString()); 
			return null;
		} 
	}
	/*
	 * ��ѯĳ�����Ѿ��̵����Ϣ�����ܣ�����������
	 */
	public ArrayList<WYZMetaData> GetSiteNoList(Boolean Stype,String id){ 
	 
		ArrayList<WYZMetaData>  modelData=new ArrayList<WYZMetaData>();
		 Open();
		try {
			String Where=" 1=1 ";
			if(Stype)//����
				Where="  sptm='"+id+"'";
			else //����
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
			 Log.e("SqlError", "��ѯ����"+exception.toString()); 
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
			//db.execSQL(sql);//Android�޷���ֵ 
			if(result>0)
			return true;
			else {
				
				return false;
			} 
		} catch (Exception e) {
			Log.e("SqlError", "��ѯ����"+e.toString()); 
			return false;
		}
	}
	
	/**
     * ���²ɹ���Ϣ
     * @param Stype ���� true ���� false ����
     * @param model ��
     * @return
     */
    public Boolean updateCG(Boolean Stype,WYZCGData model)
    {
    	try {
			String Where="";
			if(Stype)//����
				Where+=" sptm='"+model.SQL_Id+"'";
			else //����
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
    		Log.e("SqlError", "��ѯ����"+exception.toString()); 
    		return false;
    	}
    }
    /**
     * ��ѯ�ɹ���Ϣ
     * @param Stype ���� true ���� false ����
     * @param id �����������
     * @return
     */
    public WYZCGData QueryCGData(Boolean Stype,String id)
    {
    	Boolean resultBoolean=false;
		 Open();
		try {
			String Where=" 1=1 ";
			if(Stype)//����
				Where=" sptm="+id;
			else //����
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
			 Log.e("SqlError", "��ѯ����"+e.toString()); 
			return null;
		} 
    }
    /**
     * ��ȡ�ɹ���Ϣ
     * @param cursor ���ݿⷵ��ָ��
     * @return
     */
    public WYZCGData getCGData(Cursor cursor)
    {
    	WYZCGData modelData=new WYZCGData(); 
    	cursor.moveToFirst(); 
		// ��ѯ�������ݱ��浽�ַ�����
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
     * ɾ���ɹ���Ϣ
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
			Log.e("SqlError", "��ѯ����"+e.toString()); 
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
			if(Stype)//����
				Where=" sptm="+id;
			else //����
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
			 Log.e("SqlError", "��ѯ����"+e.toString()); 
			return null;
		} 
    }
    
    public WYZHJData getHJData(Cursor cursor)
    {
    	WYZHJData modelData=new WYZHJData(); 
    	cursor.moveToFirst(); 
		// ��ѯ�������ݱ��浽�ַ�����
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
