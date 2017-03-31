package com.zzu.wyz;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

public class PriceQueryActivity extends Activity {

	// �����ѯ��������ͨ��get�����õ����ݣ�����ɨ�������ʾ
	private String dataName, dataId, dataCode, dataDj, dataSpsl;
	// �����ѯ����ֵ��get��set�����ж����ݿ����Ƿ��ѯ������
	// private String resultString = null;
	private boolean booType;
	private String tempSiteno;
	private String tempQueryId;

	private int spslUpdate;
	private Spinner check_picstr;
	private List<WYZMetaData> list;
	private ArrayAdapter<String> spinnerAdapter;
	List<String> listdata;
	List<String> listspsl;
	// private TextView showExtra;

	SqlLiteHelper dbhelper;
	private String strTmString = "";
	private String strBMString = "";

	private EditText pic_qid, pic_qcode;
	private TextView pic_qname, pic_qdj, pic_qspsl;
	SQLiteDatabase db;

	private Button mScan;
	// 3.����ɨ����ر���
	private int type;
	private int outPut;

	private Vibrator mVibrator;
	private ScanManager mScanManager;
	private SoundPool soundpool = null;
	private int soundid;
	private String barcodeStr;
	private boolean isScaning = false;

	// 4.ʵ�ֽ��չ㲥����
	private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			isScaning = false;
			soundpool.play(soundid, 1, 1, 0, 0, 1);
			pic_qid.setText("");
			mVibrator.vibrate(100);

			byte[] barcode = intent.getByteArrayExtra("barocode");

			int barocodelen = intent.getIntExtra("length", 0);
			byte temp = intent.getByteExtra("barcodeType", (byte) 0);
			android.util.Log.i("debug", "----codetype--" + temp);
			barcodeStr = new String(barcode, 0, barocodelen);

			booType = true;

			tempQueryId = barcodeStr;
			//������
System.out.println("!!!!!!!!!!!!!!!!!"+tempQueryId);
			entIdQuery();

			if (barcodeStr != null) {
				// onPause();
				mScanManager.stopDecode();
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.pic_query);
		dbhelper = new SqlLiteHelper(MetaStaticData.SDCARD_PATH
				+ MetaStaticData.DATABASE_HJNAME);

		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		setupView();

		pic_qid.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					// ����ENTER��
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						booType = true;

						tempQueryId = pic_qid.getText().toString().trim();
						// pd_update.clearFocus();
						entIdQuery();

					}
				}
				return false;
			}
		});

		pic_qcode.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					// ����ENTER��
					if (keyCode == KeyEvent.KEYCODE_ENTER) {

						booType = false;

						tempQueryId = pic_qcode.getText().toString().trim();

						entIdQuery();

					}
				}
				return false;
			}
		});

		check_picstr
				.setOnItemSelectedListener(new OnItemSelectedListenerImpl());
	}

	private class OnItemSelectedListenerImpl implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) { // ��ʾѡ��ı��ʱ�򴥷�
			String value = listspsl.get(position).toString();
			// String value = parent.getItemAtPosition(position).toString(); //
			// ȡ��ѡ�е�ѡ��
			PriceQueryActivity.this.pic_qspsl.setText(value); // �����ı��������
		}

		public void onNothingSelected(AdapterView<?> arg0) { // ��ʾû��ѡ���ʱ�򴥷�
			// һ��˷������ڲ�����
		}

	}

	// 6.��ʼ��ɨ��ͷ
	private void initScan() {

		mScanManager = new ScanManager();
		mScanManager.openScanner();
		// ����ɨ��
		mScanManager.switchOutputMode(0);
		soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
		soundid = soundpool.load("/etc/Scan_new.ogg", 1);
	}

	private void setupView() {
		// ȡ��EditText�����
		pic_qid = (EditText) findViewById(R.id.pic_qid);
		pic_qcode = (EditText) findViewById(R.id.pic_qcode);
		pic_qname = (TextView) findViewById(R.id.pic_qname);
		pic_qdj = (TextView) findViewById(R.id.pic_qdj);
		pic_qspsl = (TextView) findViewById(R.id.pic_qspsl);
		check_picstr = (Spinner) findViewById(R.id.check_picstr);

		mScan = (Button) findViewById(R.id.scan4);
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
				// ɨ��ģʽ�л�Ϊ����ɨ��
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

	// 8.��onPause()������ر�ɨ��ͷ�����ע��
	@Override
	protected void onPause() {

		super.onPause();
		if (mScanManager != null) {
			mScanManager.stopDecode();
			isScaning = false;
		}
		unregisterReceiver(mScanReceiver);
	}

	// 7.ע��intent����׼��
	@Override
	protected void onResume() {

		super.onResume();
		initScan();
		pic_qid.setText("");
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
	 * Toast.makeText(PriceQueryActivity.this, "���Ȳ�ѯ--=======",
	 * Toast.LENGTH_SHORT).show(); } }
	 */

	private void entIdQuery() {
		Log.e("SqlError", "���ݲ�����һ����");
		if (tempQueryId.length() == 0) {
			Toast.makeText(PriceQueryActivity.this, "���������",
					Toast.LENGTH_SHORT).show();
		} else {
			WYZMetaData data = dbhelper.QueryGoodsInfo(booType, tempQueryId);
			// txtId.setText(data.SQL_Id);
			if (data != null) {
			
				strBMString = data.SQL_Code;
				strTmString = data.SQL_Id;
				pic_qid.setText(strTmString);
				pic_qcode.setText(data.SQL_Code);
				pic_qname.setText(data.SQL_Name);
				pic_qdj.setText(data.SQL_Dj);
				pic_qspsl.setText("");
				
				//�Զ�����ܺ������б��
				
				
				list = dbhelper.GetSiteNoList(booType, tempQueryId);
				//list.addAll(dbhelper.GetSiteNoList(booType, tempQueryId));
				Iterator<WYZMetaData> ite = list.iterator();
				listdata = new ArrayList<String>();
				listspsl = new ArrayList<String>();
			
				
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
				this.check_picstr.setPrompt("��ѡ����Ļ��ܺ�");
				PriceQueryActivity.this.spinnerAdapter = new ArrayAdapter<String>(
						this, android.R.layout.simple_list_item_1, this.listdata);
				this.check_picstr.setAdapter(spinnerAdapter);
				// pd_qspsl.setText(data.SQL_Spsl +"");
				/*
				 * Toast.makeText(ScanActivity.this, data.SQL_Name,
				 * Toast.LENGTH_SHORT).show();
				 */

			} else {
				strBMString = "";
				strTmString = "";
				pic_qcode.setText("");
				pic_qname.setText("");
				pic_qdj.setText("");
				pic_qspsl.setText("");
				Toast.makeText(PriceQueryActivity.this, "����Ʒ�����ڣ����������룡",
						Toast.LENGTH_SHORT).show();

			}// v.setVisibility(View.GONE);
		}
	}

	private void exitDialog() {
		Dialog dialog = new AlertDialog.Builder(this)
				.setIcon(R.drawable.tab_exit_pressed).setTitle("�˳��˼۲�ѯ��")
				.setMessage("�˳���Ʒ�˼۲�ѯ��")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dbhelper.Close();
						PriceQueryActivity.this.finish();
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).create();
		dialog.show();
	}
}
