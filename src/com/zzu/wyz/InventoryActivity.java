package com.zzu.wyz;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.device.ScanManager;
import android.device.scanner.configuration.Triggering;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zzu.wyz.util.MetaStaticData;
import com.zzu.wyz.util.SqlLiteHelper;
import com.zzu.wyz.util.WYZMetaData;

public class InventoryActivity extends Activity {

	// 定义查询到的数据通过get方法得到数据，并在扫描框中显示
	private String dataName, dataId, dataCode, dataDj, dataSpsl;
	// 定义查询返回值，get，set方法判断数据库中是否查询到数据
	private String resultString = null;
	private boolean booType;
	private String tempSiteno;
	private String tempQueryId;

	private int spslUpdate;

	private TextView showExtra;
    //private List<String>numberList = new ArrayList<String>();
	private Set<String> numberSet= new HashSet<String>();
	private static int scanNumber;
	//private  int number;
	SqlLiteHelper dbhelper;
	private String strTmString = "";
	private String strBMString = "";

	private EditText pd_scan_id, pd_scan_code, pd_update;
	private TextView pd_scan_name, pd_scan_dj, pd_spsl;
	SQLiteDatabase db;

	private Button mScan, pd_scan_exit, pd_scan_bt;
	// 3.定义扫描相关变量
	private int type;
	private int outPut;

	private Vibrator mVibrator;
	private ScanManager mScanManager;
	private SoundPool soundpool = null;
	private int soundid;
	private String barcodeStr;
	private boolean isScaning = false;

	// 4.实现接收广播方法
	private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			isScaning = false;
			soundpool.play(soundid, 1, 1, 0, 0, 1);
			pd_scan_id.setText("");
			mVibrator.vibrate(100);

			byte[] barcode = intent.getByteArrayExtra("barocode");

			int barocodelen = intent.getIntExtra("length", 0);
			byte temp = intent.getByteExtra("barcodeType", (byte) 0);
			android.util.Log.i("debug", "----codetype--" + temp);
			barcodeStr = new String(barcode, 0, barocodelen);

			booType = true;

			tempQueryId = barcodeStr;
			// pd_update.clearFocus();
				
			if(entIdQuery())
			{
			pd_spsl.setFocusableInTouchMode(true);
			pd_spsl.clearFocus();
			pd_update.setText("1");
			pd_update.requestFocus();
			pd_update.selectAll();
			pd_update.setFocusableInTouchMode(true);
			}
			else{
				pd_scan_id.selectAll();
				pd_scan_id.requestFocus(); 
			}
			// 得到扫描的条码

			/*
			 * query(db, barcodeStr); showTable();
			 * pd_scan_update.requestFocus(); pd_scan_update.selectAll();
			 * Log.e("SLError", pd_scan_update.getText().toString()); //
			 * 监听编辑框按钮事件
			 */
			if (barcodeStr != null) {
				// onPause();
				mScanManager.stopDecode();
			}
		}

	};

	/*
	 * // 在表格中显示从数据库查询到的列表 private void showTable() {
	 * 
	 * pd_scan_id.setText(dataId); pd_scan_name.setText(dataName);
	 * pd_scan_code.setText(dataCode);
	 * 
	 * pd_scan_dj.setText(dataDj + "元");
	 * 
	 * pd_scan_spsl.setText(dataSpsl);
	 * 
	 * }
	 * 
	 * // 清空本地已经保存的数据避免赋值造成数据不清空 private void notShowTable() { dataId = null;
	 * dataCode = null; dataName = null;
	 * 
	 * 
	 * dataDj = null; dataSpsl = null; }
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.pd_pdscanning);
		dbhelper = new SqlLiteHelper(MetaStaticData.SDCARD_PATH
				+ MetaStaticData.DATABASE_PDNAME);

		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		setupView();
		// 接受盘点页面穿回来的参数，并显示在顶部位置
		Intent intent = getIntent();
		tempSiteno = intent.getStringExtra("siteno");

		showExtra.setText("盘点货架号：" + tempSiteno);
		// showExtra.setTextSize(20);

		// pd_update.requestFocus();
		pd_scan_id.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					// 监听ENTER键
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						booType = true;

						tempQueryId = pd_scan_id.getText().toString().trim();
						// pd_update.clearFocus();
						if(entIdQuery())
						{
						soundpool.play(soundid, 1, 1, 0, 0, 1);
						pd_scan_code.setFocusable(false);
						pd_spsl.setFocusableInTouchMode(true);
						pd_spsl.clearFocus();
						pd_update.clearFocus();
						pd_update.setText("1"); 
						pd_update.selectAll();
						pd_scan_id.selectAll();
						pd_scan_code.selectAll();
						pd_update.requestFocus();
						pd_update.setFocusableInTouchMode(true);
						}
						else{
							pd_scan_code.setFocusable(false);
							pd_scan_id.selectAll();
							pd_scan_id.requestFocus(); 
							pd_scan_id.setFocusableInTouchMode(true);
						}
						

						// pd_scan_id.clearFocus();
						/*
						 * if(pd_update.isFocused()){ pd_update.selectAll();
						 * }else{ pd_update.requestFocus();//获得焦点
						 * pd_update.selectAll(); }
						 */

						/*
						 * String temp =
						 * pd_scan_update.getText().toString().trim();
						 * System.out.println("$$$$$$$$$$$$$$$$$$" + temp);
						 * 
						 * update(db, dataId, temp);
						 * pd_scan_update.clearFocus(); query(db, barcodeStr);
						 * showTable();
						 */

					}
				}
				return false;
			}
		});

		/*
		 *  商品总量改变
		 */
		pd_spsl.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					// 监听ENTER键
					if (keyCode == KeyEvent.KEYCODE_ENTER
							&& !pd_scan_id.getText().toString().isEmpty()
							&& !pd_spsl.getText().toString().trim().isEmpty()) {
						spslUpdate = Integer.parseInt(pd_spsl.getText()
								.toString().trim());
						entIdUpdata();
						pd_spsl.setText(spslUpdate+"");
						//entIdQuery();
						//pd_update.setText("1");
						soundpool.play(soundid, 1, 1, 0, 0, 1);
					    pd_spsl.clearFocus();
					    pd_scan_code.setFocusableInTouchMode(true);
						pd_update.setFocusable(false);
						pd_scan_id.selectAll();
                       // pd_spsl.selectAll();
						pd_update.selectAll();
						pd_scan_id.requestFocus();
						
						
					}
				}
				return false;
			}
		});
         /**
          * 商品更新数量
          */
		pd_update.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					// 监听ENTER键
					if (keyCode == KeyEvent.KEYCODE_ENTER
							&& !pd_scan_id.getText().toString().isEmpty()
							&& !pd_update.getText().toString().isEmpty()) {
						try{
							int dString=0;
							if(!pd_update.getText().toString().trim().isEmpty())
							dString=Integer.parseInt(pd_update.getText()
									.toString().trim());
						spslUpdate = Integer.parseInt(pd_spsl.getText()
								.toString().trim())
								+ dString;
						}
						catch(Exception e){
							
						}
						entIdUpdata();
						pd_update.clearFocus();
						pd_spsl.setText(spslUpdate + "");
						//entIdQuery();
						pd_update.setText("1");
						soundpool.play(soundid, 1, 1, 0, 0, 1);
						//numberSet = new HashSet<String>();
//						numberSet.add(tempQueryId);
//						number = numberSet.size(); 
						 pd_scan_code.setFocusableInTouchMode(true);
//						 pd_spsl.setFocusableInTouchMode(true);
 						 pd_spsl.setFocusable(false);
						pd_update.selectAll(); 
						pd_scan_id.selectAll();
						//pd_spsl.selectAll();
						pd_scan_id.requestFocus(); 
						pd_scan_id.setFocusableInTouchMode(true);
						
					}
				}
				return false;
			}
		});

		pd_scan_code.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					// 监听ENTER键
					if (keyCode == KeyEvent.KEYCODE_ENTER) {

						booType = false;

						tempQueryId = pd_scan_code.getText().toString().trim();
						// pd_scan_code.clearFocus();
						if(entIdQuery())
						{
						soundpool.play(soundid, 1, 1, 0, 0, 1);
						pd_update.setText("1");
						pd_update.selectAll();
						pd_scan_id.selectAll();
						pd_scan_code.selectAll();
						pd_update.requestFocus(); 
						pd_update.setFocusableInTouchMode(true);
						}
						else{
							pd_scan_code.selectAll();
							pd_scan_code.requestFocus(); 
						}
					}
				}
				return false;
			}
		});
	}

	// 6.初始化扫描头
	private void initScan() {

		mScanManager = new ScanManager();
		mScanManager.openScanner();
		// 单次扫描
		mScanManager.switchOutputMode(0);
		soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
		soundid = soundpool.load("/etc/Scan_new.ogg", 1);
	}

	private void setupView() {
		// 取得EditText的组件
		pd_scan_id = (EditText) findViewById(R.id.pd_scan_id);
		pd_scan_name = (TextView) findViewById(R.id.pd_scan_name);
		pd_scan_code = (EditText) findViewById(R.id.pd_scan_code);
		pd_scan_dj = (TextView) findViewById(R.id.pd_scan_dj);
		pd_spsl = (TextView) findViewById(R.id.pd_spsl);
		pd_update = (EditText) findViewById(R.id.pd_update);
		showExtra = (TextView) findViewById(R.id.showExtra);
		mScan = (Button) findViewById(R.id.scan);
		/*
		 * pd_scan_exit = (Button) findViewById(R.id.pd_scan_exit);
		 * pd_scan_exit.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * ScanActivity.this.exitDialog();
		 * 
		 * } });
		 */
		mScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 扫描模式切换为连续扫描
				if (mScanManager.getTriggerMode() != Triggering.HOST)

					mScanManager.setTriggerMode(Triggering.HOST);

				mScanManager.stopDecode();
				isScaning = true;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}

				mScanManager.startDecode();
			}
		});
	

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	// 8.在onPause()方法里关闭扫描头，解除注册
	@Override
	protected void onPause() {

		super.onPause();
		if (mScanManager != null) {
			mScanManager.stopDecode();
			isScaning = false;
		}
		unregisterReceiver(mScanReceiver);
	}

	// 7.注册intent过滤准则
	@Override
	protected void onResume() {

		super.onResume();
		initScan();
		pd_scan_id.setText("");
		IntentFilter filter = new IntentFilter();
		filter.addAction(MetaStaticData.SCAN_ACTION);
		registerReceiver(mScanReceiver, filter);
	}

	@Override
	protected void onStart() {

		super.onStart();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
//System.out.println("################number = "+number);
			this.exitDialog();
		}
		// return false;
		return super.onKeyDown(keyCode, event);
	}

	private void entIdUpdata() {
		WYZMetaData data = new WYZMetaData();
		if (strTmString != "") {
			data.SQL_Id = pd_scan_id.getText().toString().trim();
			data.SQL_Code = pd_scan_code.getText().toString();
			data.SQL_Siteno = tempSiteno;
			data.SQL_Spsl = spslUpdate;

			String error = dbhelper.update(true, data);

			Toast.makeText(InventoryActivity.this, error, Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(InventoryActivity.this, "请先查询--=======",
					Toast.LENGTH_SHORT).show();
		}
	}

	private Boolean entIdQuery() {
		Log.e("SqlError", "数据操作第一步：");
		if (tempQueryId.length() == 0) {
			Toast.makeText(InventoryActivity.this, "请输入编码", Toast.LENGTH_SHORT)
					.show();
			return false;
		} else {
			WYZMetaData data = dbhelper.query(booType, tempQueryId, tempSiteno);
			//System.out.println("@@@@@@@@@@@@@@@@==="+dbhelper.selectNumber(tempSiteno));
			// txtId.setText(data.SQL_Id);
			if (data != null) {
				strBMString = data.SQL_Code;
				strTmString = data.SQL_Id;
				pd_scan_id.setText(strTmString);
				pd_scan_code.setText(data.SQL_Code);
				pd_scan_name.setText(data.SQL_Name);
				pd_scan_dj.setText(data.SQL_Dj);
				pd_spsl.setText(data.SQL_Spsl + "");
				
				//System.out.println("@@@@@@@@@@@@@@@@==="+dbhelper.selectNumber(tempSiteno));
				/*
				 * Toast.makeText(ScanActivity.this, data.SQL_Name,
				 * Toast.LENGTH_SHORT).show();
				 */
			 scanNumber++;
			 return true;
			} else {
				strBMString = "";
				strTmString = "";
				pd_scan_code.setText("");
				pd_scan_name.setText("");
				pd_scan_dj.setText("");
				pd_spsl.setText("");
				Toast.makeText(InventoryActivity.this, "该商品不存在，请重新输入！",
						Toast.LENGTH_SHORT).show();
				return false;
			}// v.setVisibility(View.GONE);
		}
	}
	/**
	 * 退出时查询有多少数据
	 * @param siteno 货架号
	 * @return
	 */
	private String ExitQueryCount(String siteno)
	{
		try{
		return dbhelper.selectNumber(siteno);
		}catch(Exception exception)
		{
			Toast.makeText(InventoryActivity.this, exception+"！",
					Toast.LENGTH_SHORT).show();
		return "";
		}
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

			/*
			 * String sql = "Select  " + WYZMetaData.SQL_ID + "," +
			 * WYZMetaData.SQL_NAME + "," + WYZMetaData.SQL_CODE + "," +
			 * WYZMetaData.SQL_NAME + "," + WYZMetaData.SQL_NAME + "," +
			 * WYZMetaData.SQL_NAME + "," + WYZMetaData.SQL_NAME + "  from  " +
			 * WYZMetaData.TABLE_NAME + " where" + WYZMetaData.SQL_ID + " = " +
			 * id;
			 */
			String sql = "Select name,id,code,gg,jldw,dj,spsl from pd where id="
					+ id;
			Cursor cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			// 查询到的数据保存到字符串中
			dataId = cursor.getString(cursor
					.getColumnIndex(MetaStaticData.SQL_ID));
			dataName = cursor.getString(cursor
					.getColumnIndex(MetaStaticData.SQL_NAME));
			dataCode = cursor.getString(cursor
					.getColumnIndex(MetaStaticData.SQL_CODE));

			dataDj = cursor.getString(cursor
					.getColumnIndex(MetaStaticData.SQL_DJ));

			dataSpsl = cursor.getString(cursor
					.getColumnIndex(MetaStaticData.SQL_SPSL));
			Log.e("PDError", dataSpsl);
			resultString = dataId;

			db.close();
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
			System.out.println("^^^^^^^^^^^^" + spslTemp);
			String sql = "update pd set spsl = spsl+" + spslTemp
					+ " where id =" + id;

			db.execSQL(sql);

			// spslTemp = null;
			Toast.makeText(this, "商品：" + id + "添加成功", Toast.LENGTH_SHORT)
					.show();
			db.close();
		} catch (Exception e) {
			Toast.makeText(this, "请检查扫描的条码是否正确", Toast.LENGTH_SHORT).show();
		}
	}

	private void exitDialog() {
		String number=ExitQueryCount(tempSiteno);
		String message = "退出货架：" + tempSiteno + "的盘点吗？";
		Dialog dialog = new AlertDialog.Builder(this)
				.setIcon(R.drawable.tab_exit_pressed).setTitle("退出盘点：")
				.setMessage(message+
						"\n"+"货架统计了："+number+"件不同的商品"+
						"\n"+"货架扫描了："+scanNumber+"次")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dbhelper.Close();
						scanNumber=0;
						InventoryActivity.this.finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					//System.out.println("$$$$$$$$$$$$$$$$$$取消number="+number);
					}
				}).create();
		dialog.show();
	}
}
