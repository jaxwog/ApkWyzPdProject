package com.zzu.wyz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zzu.wyz.util.MetaStaticData;
import com.zzu.wyz.util.SqlLiteHelper;
import com.zzu.wyz.util.WYZMetaData;

public class InventoryQueryActivity extends Activity {

	// 定义查询到的数据通过get方法得到数据，并在扫描框中显示
	private String dataName, dataId, dataCode, dataDj, dataSpsl;
	// 定义查询返回值，get，set方法判断数据库中是否查询到数据
	// private String resultString = null;
	private boolean booType;
	private String tempSiteno = "1111";
	private String tempQueryId;
	private Spinner check_stieno;
	private List<WYZMetaData> list;
	private ArrayAdapter<String> spinnerAdapter;
	List<String> listdata;
	List<String> listspsl;
	Map<String, String> map;
	// private TextView showExtra;

	SqlLiteHelper dbhelper;
	private String strTmString = "";
	private String strBMString = "";

	private EditText pd_qid, pd_qcode;
	private TextView pd_qname, pd_qdj, pd_qspsl;
	SQLiteDatabase db;

	private Button mScan;
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
			pd_qid.setText("");
			mVibrator.vibrate(100);

			byte[] barcode = intent.getByteArrayExtra("barocode");

			int barocodelen = intent.getIntExtra("length", 0);
			byte temp = intent.getByteExtra("barcodeType", (byte) 0);
			android.util.Log.i("debug", "----codetype--" + temp);
			barcodeStr = new String(barcode, 0, barocodelen);

			booType = true;

			tempQueryId = barcodeStr;
			// pd_update.clearFocus();
			entIdQuery();

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
		super.setContentView(R.layout.pd_query);
		dbhelper = new SqlLiteHelper(MetaStaticData.SDCARD_PATH
				+ MetaStaticData.DATABASE_PDNAME);

		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		setupView();
		// 接受盘点页面穿回来的参数，并显示在顶部位置
		/*
		 * Intent intent = getIntent(); tempSiteno =
		 * intent.getStringExtra("siteno");
		 * 
		 * showExtra.setText("货架号：" + tempSiteno);
		 */
		// showExtra.setTextSize(20);

		// pd_update.requestFocus();
		pd_qid.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					// 监听ENTER键
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						booType = true;

						tempQueryId = pd_qid.getText().toString().trim();
						// pd_update.clearFocus();
						entIdQuery();
						soundpool.play(soundid, 1, 1, 0, 0, 1);
					}
				}
				return false;
			}
		});

		pd_qcode.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					// 监听ENTER键
					if (keyCode == KeyEvent.KEYCODE_ENTER) {

						booType = false;

						tempQueryId = pd_qcode.getText().toString().trim();
						// pd_scan_code.clearFocus();
						entIdQuery();
						// pic_update.setText("1");
						soundpool.play(soundid, 1, 1, 0, 0, 1);
					}
				}
				return false;
			}
		});

		check_stieno
				.setOnItemSelectedListener(new OnItemSelectedListenerImpl());
	}

	private class OnItemSelectedListenerImpl implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) { // 表示选项改变的时候触发
			String value = listspsl.get(position).toString();
			//String value = parent.getItemAtPosition(position).toString(); // 取得选中的选项
			InventoryQueryActivity.this.pd_qspsl.setText(value); // 设置文本组件内容
			//soundpool.play(soundid, 1, 1, 0, 0, 1);
		}

		public void onNothingSelected(AdapterView<?> arg0) { // 表示没有选项的时候触发
			// 一般此方法现在不关心
		}
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
		pd_qid = (EditText) findViewById(R.id.pd_qid);
		pd_qcode = (EditText) findViewById(R.id.pd_qcode);
		pd_qname = (TextView) findViewById(R.id.pd_qname);
		pd_qdj = (TextView) findViewById(R.id.pd_qdj);
		pd_qspsl = (TextView) findViewById(R.id.pd_qspsl);
		check_stieno = (Spinner) findViewById(R.id.check_stieno);
		// pic_update = (EditText) findViewById(R.id.pic_updata);
		// showExtra = (TextView) findViewById(R.id.showExtra);
		mScan = (Button) findViewById(R.id.scan7);
		/*
		 * pd_scan_exit = (Button) findViewById(R.id.pd_scan_exit);
		 * pd_scan_exit.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * PurchasActivity.this.exitDialog();
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
		pd_qid.setText("");
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
			this.exitDialog();
		}
		// return false;
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * private void entIdUpdata() { WYZMetaData data = new WYZMetaData(); if
	 * (strTmString != "") { data.SQL_Id = pic_qid.getText().toString().trim();
	 * data.SQL_Code = pic_qcode.getText().toString(); data.SQL_Siteno =
	 * tempSiteno; data.SQL_Spsl = spslUpdate;
	 * 
	 * String error = dbhelper.update(true, data);
	 * 
	 * Toast.makeText(PriceQueryActivity.this, error,
	 * Toast.LENGTH_SHORT).show(); } else {
	 * Toast.makeText(PriceQueryActivity.this, "请先查询--=======",
	 * Toast.LENGTH_SHORT).show(); } }
	 */

	private void entIdQuery() {
		Log.e("SqlError", "数据操作第一步：");
		if (tempQueryId.length() == 0) {
			Toast.makeText(InventoryQueryActivity.this, "请输入编码",
					Toast.LENGTH_SHORT).show();
		} else {
			WYZMetaData data = dbhelper.QueryGoodsInfo(booType, tempQueryId);
			// txtId.setText(data.SQL_Id);
			if (data != null) {
			//	System.out.println("###############qspsl:" + data.SQL_Spsl);
				strBMString = data.SQL_Code;
				strTmString = data.SQL_Id;
				pd_qid.setText(strTmString);
				pd_qcode.setText(data.SQL_Code);
				pd_qname.setText(data.SQL_Name);
				pd_qdj.setText(data.SQL_Dj);
				pd_qspsl.setText("");
				
				//自定义货架号下拉列表框
				
				
				list = dbhelper.GetSiteNoList(booType, tempQueryId);
				//list.addAll(dbhelper.GetSiteNoList(booType, tempQueryId));
				Iterator<WYZMetaData> ite = list.iterator();
				listdata = new ArrayList<String>();
				listspsl = new ArrayList<String>();
			map = new HashMap<String, String>();
				
				while(ite.hasNext()){
                   WYZMetaData  meta = new WYZMetaData();
                   
                  
                   meta = ite.next();
                   
                   listdata.add(meta.SQL_Siteno); 
                   listspsl.add(String.valueOf(meta.SQL_Spsl));
				}
				
				
				/*Iterator<String> itera = listdata.iterator();
				while (itera.hasNext()) {
System.out.println("###################=="+itera.next());
					
				}*/
				
				// list=dbhelper.GetSiteNoList(booType, tempQueryId);
				this.check_stieno.setPrompt("请选择你的货架号");
				InventoryQueryActivity.this.spinnerAdapter = new ArrayAdapter<String>(
						this, android.R.layout.simple_list_item_1, this.listdata);
				this.check_stieno.setAdapter(spinnerAdapter);
				// pd_qspsl.setText(data.SQL_Spsl +"");
				/*
				 * Toast.makeText(ScanActivity.this, data.SQL_Name,
				 * Toast.LENGTH_SHORT).show();
				 */

			} else {
				strBMString = "";
				strTmString = "";
				pd_qcode.setText("");
				pd_qname.setText("");
				pd_qdj.setText("");
				pd_qspsl.setText("");
				Toast.makeText(InventoryQueryActivity.this, "该商品不存在，请重新输入！",
						Toast.LENGTH_SHORT).show();

			}// v.setVisibility(View.GONE);
		}
	}

	private void exitDialog() {
		Dialog dialog = new AlertDialog.Builder(this)
				.setIcon(R.drawable.tab_exit_pressed).setTitle("退出查询：")
				.setMessage("退出商品盘点查询吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dbhelper.Close();
						InventoryQueryActivity.this.finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).create();
		dialog.show();
	}
}
