package com.zzu.wyz;

import java.util.HashSet;
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
import com.zzu.wyz.util.WYZHJData;
import com.zzu.wyz.util.WYZMetaData;

public class PriceActivity extends Activity {

	// �����ѯ��������ͨ��get�����õ����ݣ�����ɨ�������ʾ
	private String dataName, dataId, dataCode, dataDj, dataSpsl;
	// �����ѯ����ֵ��get��set�����ж����ݿ����Ƿ��ѯ������
	// private String resultString = null;
	private boolean booType;
	private String tempStr;
	private String tempQueryId;
	
	private Set<String> numberSet= new HashSet<String>();
	//private  int number;

	private int spslUpdate;

	 private TextView picExtra;

	SqlLiteHelper dbhelper;
	private String strTmString = "";
	private String strBMString = "";

	private EditText pic_id,  pic_code,pic_update;
private TextView pic_name,pic_dj,pic_spsl;
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
			pic_id.setText("");
			mVibrator.vibrate(100);

			byte[] barcode = intent.getByteArrayExtra("barocode");

			int barocodelen = intent.getIntExtra("length", 0);
			byte temp = intent.getByteExtra("barcodeType", (byte) 0);
			android.util.Log.i("debug", "----codetype--" + temp);
			barcodeStr = new String(barcode, 0, barocodelen);

			booType = true;

			tempQueryId = barcodeStr;
			
			entIdQuery();
			pic_update.setText("1");
			pic_update.requestFocus();
			pic_update.selectAll();

			// �õ�ɨ�������

			/*
			 * query(db, barcodeStr); showTable();
			 * pd_scan_update.requestFocus(); pd_scan_update.selectAll();
			 * Log.e("SLError", pd_scan_update.getText().toString()); //
			 * �����༭��ť�¼�
			 */
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
		super.setContentView(R.layout.pic_picscanning);
		dbhelper = new SqlLiteHelper(MetaStaticData.SDCARD_PATH
				+ MetaStaticData.DATABASE_HJNAME);

		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		setupView();
		pic_spsl.setFocusable(false);
		Intent intent = getIntent();
		tempStr = intent.getStringExtra("hjExtra");

		picExtra.setText("�˼ۻ��ܺţ�" + tempStr);
		
		pic_id.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					// ����ENTER��
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						booType = true;

						tempQueryId = pic_id.getText().toString().trim();
						// pd_update.clearFocus();
						entIdQuery();
						soundpool.play(soundid, 1, 1, 0, 0, 1);
						pic_code.setFocusable(false);
						pic_update.clearFocus();
						pic_update.setText("1"); 
						pic_update.selectAll();
						pic_id.selectAll();
						pic_code.selectAll();
						pic_update.requestFocus();
						
					}
				}
				return false;
			}
		});

		/*pic_spsl.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					// ����ENTER��
					if (keyCode == KeyEvent.KEYCODE_ENTER
							&& !pic_id.getText().toString().isEmpty()
							&& !pic_spsl.getText().toString().trim().isEmpty()) {
						spslUpdate = Integer.parseInt(pic_spsl.getText()
								.toString().trim());
						entIdUpdata();
						pic_spsl.clearFocus();
						entIdQuery();
						pic_update.setText("1");
						soundpool.play(soundid, 1, 1, 0, 0, 1);
					}
				}
				return false;
			}
		});*/
		
		pic_update.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					// ����ENTER��
					if (keyCode == KeyEvent.KEYCODE_ENTER
							&& !pic_id.getText().toString().isEmpty()
							&& !pic_update.getText().toString().isEmpty()) {
						spslUpdate = Integer.parseInt(pic_spsl.getText()
								.toString().trim())
								+ Integer.parseInt(pic_update.getText()
										.toString().trim());
						entIdUpdata();
						pic_update.setText("1");
						//Log.e("sqlError",spslUpdate);
						pic_spsl.setText(spslUpdate + "");
						soundpool.play(soundid, 1, 1, 0, 0, 1);
						//numberSet.add(tempQueryId);
						//number = numberSet.size();
						 pic_code.setFocusableInTouchMode(true);
						pic_id.selectAll();
						//pd_spsl.selectAll();
						pic_update.selectAll();
						
						pic_id.requestFocus();
						
					}
				}
				return false;
			}
		});


		pic_code.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					// ����ENTER��
					if (keyCode == KeyEvent.KEYCODE_ENTER) {

						booType = false;

						tempQueryId = pic_code.getText().toString().trim();
						// pd_scan_code.clearFocus();
						entIdQuery();
						soundpool.play(soundid, 1, 1, 0, 0, 1);
						pic_update.setText("1");
						pic_update.selectAll();
						pic_id.selectAll();
						pic_code.selectAll();
						pic_update.requestFocus(); 

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
		pic_id = (EditText) findViewById(R.id.pic_id);
		pic_code = (EditText) findViewById(R.id.pic_code);
		pic_name = (TextView) findViewById(R.id.pic_name);
		pic_dj = (TextView) findViewById(R.id.pic_dj);
		pic_update = (EditText) findViewById(R.id.pic_update);
		pic_spsl = (TextView) findViewById(R.id.pic_spsl);
		picExtra = (TextView) findViewById(R.id.picExtra);
		mScan = (Button) findViewById(R.id.scan2);
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
		pic_id.setText("");
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

	private void entIdUpdata() {
		WYZMetaData data = new WYZMetaData();
		if (strTmString != "") {
			data.SQL_Id = pic_id.getText().toString().trim();
			data.SQL_Code = pic_code.getText().toString();
			data.SQL_Siteno = tempStr;
			data.SQL_Spsl = spslUpdate;

			String error = dbhelper.update(true, data);

			Toast.makeText(PriceActivity.this, error, Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(PriceActivity.this, "���Ȳ�ѯ--=======",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void entIdQuery() {
		Log.e("SqlError", "���ݲ�����һ����");
		if (tempQueryId.length() == 0) {
			Toast.makeText(PriceActivity.this, "���������", Toast.LENGTH_SHORT)
					.show();
		} else {
			WYZMetaData data = dbhelper.query(booType, tempQueryId, tempStr);
			// txtId.setText(data.SQL_Id);
			if (data != null) {
				strBMString = data.SQL_Code;
				strTmString = data.SQL_Id;
				pic_id.setText(strTmString);
				pic_code.setText(data.SQL_Code);
				pic_name.setText(data.SQL_Name);
				pic_dj.setText(data.SQL_Dj);
				pic_spsl.setText(data.SQL_Spsl + "");
				/*
				 * Toast.makeText(ScanActivity.this, data.SQL_Name,
				 * Toast.LENGTH_SHORT).show();
				 */

			} else {
				strBMString = "";
				strTmString = "";
				pic_code.setText("");
				pic_name.setText("");
				pic_dj.setText("");
				pic_spsl.setText("");
				Toast.makeText(PriceActivity.this, "����Ʒ�����ڣ����������룡",
						Toast.LENGTH_SHORT).show();

			}// v.setVisibility(View.GONE);
		}
	}
	
	/**
	 * �˳�ʱ��ѯ�ж�������
	 * @param siteno ���ܺ�
	 * @return
	 */
	private String ExitQueryCount(String siteno)
	{
		try{
		return dbhelper.selectNumber(siteno);
		}catch(Exception exception)
		{
			Toast.makeText(PriceActivity.this, exception+"��",
					Toast.LENGTH_SHORT).show();
		return "";
		}
	}
	

	private void exitDialog() {
		String number=ExitQueryCount(tempStr);
		Dialog dialog = new AlertDialog.Builder(this)
				.setIcon(R.drawable.tab_exit_pressed).setTitle("�˳��˼ۣ�")
				.setMessage("�˳���Ʒ�˼���"+"\n"+
				"����ͳ���ˣ�"+number+"����ͬ����Ʒ��")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dbhelper.Close();
						PriceActivity.this.finish();
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
