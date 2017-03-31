package com.zzu.wyz;

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
import com.zzu.wyz.util.WYZCGData;
import com.zzu.wyz.util.WYZMetaData;

public class PurchasQueryActivity extends Activity {

	// �����ѯ��������ͨ��get�����õ����ݣ�����ɨ�������ʾ
	private String dataName, dataId, dataCode, dataDj, dataSpsl;
	// �����ѯ����ֵ��get��set�����ж����ݿ����Ƿ��ѯ������
//	private String resultString = null;
	private boolean booType;
	private String tempSiteno;
	private String tempQueryId;

	private int spslUpdate;

	//private TextView showExtra;

	SqlLiteHelper dbhelper;
	private String strTmString = "";
	private String strBMString = "";
	private TextView pcs_qzxsl, pcs_qspsl,pcs_qxssl,pcs_qname,pcs_qcgsl;
	private EditText pcs_qid,pcs_qcode;

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
			pcs_qid.setText("");
			mVibrator.vibrate(100);

			byte[] barcode = intent.getByteArrayExtra("barocode");

			int barocodelen = intent.getIntExtra("length", 0);
			byte temp = intent.getByteExtra("barcodeType", (byte) 0);
			android.util.Log.i("debug", "----codetype--" + temp);
			barcodeStr = new String(barcode, 0, barocodelen);

			booType = true;

			tempQueryId = barcodeStr;
			
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
		super.setContentView(R.layout.pcs_query);
		dbhelper = new SqlLiteHelper(MetaStaticData.SDCARD_PATH
				+ MetaStaticData.DATABASE_CGNAME);

		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		setupView();
		
		pcs_qid.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					// ����ENTER��
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						booType = true;

						tempQueryId = pcs_qid.getText().toString().trim();
						// pd_update.clearFocus();
						entIdQuery();
						
						

						
					}
				}
				return false;
			}
		});

	

		

		pcs_qcode.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					// ����ENTER��
					if (keyCode == KeyEvent.KEYCODE_ENTER) {

						booType = false;

						tempQueryId = pcs_qcode.getText().toString().trim();
						// pd_scan_code.clearFocus();
						entIdQuery();
						//pic_update.setText("1");

					}
				}
				return false;
			}
		});
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
		pcs_qid = (EditText) findViewById(R.id.pcs_qid);
		pcs_qcode = (EditText) findViewById(R.id.pcs_qcode);
		pcs_qcgsl = (TextView) findViewById(R.id.pcs_qcgsl);
		pcs_qname = (TextView) findViewById(R.id.pcs_qname);
		pcs_qspsl = (TextView) findViewById(R.id.pcs_qspsl);
		pcs_qzxsl = (TextView) findViewById(R.id.pcs_qzxsl);
		pcs_qxssl= (TextView) findViewById(R.id.pcs_qxssl);
		mScan = (Button) findViewById(R.id.scan5);
		/*pd_scan_exit = (Button) findViewById(R.id.pd_scan_exit);
		pd_scan_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PurchasActivity.this.exitDialog();

			}
		});*/
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
          pcs_qid.setText("");
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

	/*private void entIdUpdata() {
		WYZMetaData data = new WYZMetaData();
		if (strTmString != "") {
			data.SQL_Id = pic_qid.getText().toString().trim();
			data.SQL_Code = pic_qcode.getText().toString();
			data.SQL_Siteno = tempSiteno;
			data.SQL_Spsl = spslUpdate;

			String error = dbhelper.update(true, data);

			Toast.makeText(PriceQueryActivity.this, error, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(PriceQueryActivity.this, "���Ȳ�ѯ--=======",
					Toast.LENGTH_SHORT).show();
		}
	}*/

	private void entIdQuery() {
		Log.e("SqlError", "���ݲ�����һ����");
		if (tempQueryId.length() == 0) {
			Toast.makeText(PurchasQueryActivity.this, "���������", Toast.LENGTH_SHORT)
					.show();
		} else {
			WYZCGData data = dbhelper.QueryCGData(booType, tempQueryId);
			// txtId.setText(data.SQL_Id);
			if (data != null) {
				strBMString = data.SQL_Code;
				strTmString = data.SQL_Id;
				pcs_qid.setText(strTmString);
				pcs_qcode.setText(data.SQL_Code);
				pcs_qname.setText(data.SQL_Name);
				// pcs_stock.setText(data.SQL_Dj);
				

				pcs_qspsl.setText(String.valueOf(data.SQL_SPSL));
				pcs_qcgsl.setText(String.valueOf(data.SQL_CGSL));
				pcs_qzxsl.setText(String.valueOf(data.SQL_ZXSL));
				pcs_qxssl.setText(String.valueOf(data.SQL_XSSL));
				
				
				
				/*
				 * Toast.makeText(ScanActivity.this, data.SQL_Name,
				 * Toast.LENGTH_SHORT).show();
				 */

			} else {
				strBMString = "";
				strTmString = "";
				pcs_qcode.setText("");
				pcs_qname.setText("");
				pcs_qspsl.setText("");
				pcs_qcgsl.setText("");
				pcs_qzxsl.setText("");
				pcs_qxssl.setText("");
				Toast.makeText(PurchasQueryActivity.this, "����Ʒ�����ڣ����������룡",
						Toast.LENGTH_SHORT).show();

			}// v.setVisibility(View.GONE);
		}
	}


	private void exitDialog() {
		Dialog dialog = new AlertDialog.Builder(this)
				.setIcon(R.drawable.tab_exit_pressed).setTitle("�˳��ɹ���ѯ��")
				.setMessage("�˳���Ʒ�ɹ���ѯ��")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dbhelper.Close();
						PurchasQueryActivity.this.finish();
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
