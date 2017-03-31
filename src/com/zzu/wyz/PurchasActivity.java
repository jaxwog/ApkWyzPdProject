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
import com.zzu.wyz.util.WYZCGData;
import com.zzu.wyz.util.WYZMetaData;

public class PurchasActivity extends Activity {

	// �����ѯ��������ͨ��get�����õ����ݣ�����ɨ�������ʾ
	private String dataName, dataId, dataCode, dataDj, dataSpsl;
	// �����ѯ����ֵ��get��set�����ж����ݿ����Ƿ��ѯ������
	private String resultString = null;
	private boolean booType;
	private String tempSiteno;
	private String tempQueryId;

	private int cgslUpdate;

	private Set<String> numberSet= new HashSet<String>();
	private  int number;
	
	
	private TextView pcs_zxsl, pcs_spsl,pcs_xssl,pcs_name;

	SqlLiteHelper dbhelper;
	private String strTmString = "";
	private String strBMString = "";

	private EditText pcs_id,pcs_code,pcs_cgsl;
	

	SQLiteDatabase db;

	private Button mScan, pd_scan_exit, pd_scan_bt;
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
			pcs_id.setText("");
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
			pcs_cgsl.requestFocus();
			pcs_cgsl.selectAll();

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

	/*
	 * // �ڱ������ʾ�����ݿ��ѯ�����б� private void showTable() {
	 * 
	 * pd_scan_id.setText(dataId); pd_scan_name.setText(dataName);
	 * pd_scan_code.setText(dataCode);
	 * 
	 * pd_scan_dj.setText(dataDj + "Ԫ");
	 * 
	 * pd_scan_spsl.setText(dataSpsl);
	 * 
	 * }
	 * 
	 * // ��ձ����Ѿ���������ݱ��⸳ֵ������ݲ���� private void notShowTable() { dataId = null;
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
		super.setContentView(R.layout.pcs_pcsscanning);
		dbhelper = new SqlLiteHelper(MetaStaticData.SDCARD_PATH
				+ MetaStaticData.DATABASE_CGNAME);

		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		setupView();
		// �����̵�ҳ�洩�����Ĳ���������ʾ�ڶ���λ��
		/*
		 * Intent intent = getIntent(); tempSiteno =
		 * intent.getStringExtra("siteno");
		 * 
		 * showExtra.setText("���ܺţ�" + tempSiteno);
		 */
		// showExtra.setTextSize(20);

		// pd_update.requestFocus();
		pcs_id.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					// ����ENTER��
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						booType = true;

						tempQueryId = pcs_id.getText().toString().trim();
						// pd_update.clearFocus();
						entIdQuery();
						pcs_cgsl.clearFocus();
						//pcs_update.setText("1");
						pcs_cgsl.requestFocus();
						pcs_cgsl.selectAll();

						

					}
				}
				return false;
			}
		});

		pcs_cgsl.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					// ����ENTER��
					if (keyCode == KeyEvent.KEYCODE_ENTER
							&& !pcs_id.getText().toString().isEmpty()
							&& !pcs_cgsl.getText().toString().trim()
									.isEmpty()) {
						cgslUpdate = Integer.parseInt(pcs_cgsl.getText()
								.toString().trim());
						entIdUpdata();
						pcs_spsl.clearFocus();
						//entIdQuery();
						pcs_cgsl.setText(String.valueOf(cgslUpdate));
						numberSet.add(tempQueryId);
						number = numberSet.size();
						//pcs_update.setText("1");

					}
				}
				return false;
			}
		});

		/*pcs_update.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					// ����ENTER��
					if (keyCode == KeyEvent.KEYCODE_ENTER&& !pcs_id.getText().toString().isEmpty()) {
						spslUpdate = Integer.parseInt(pcs_spsl.getText()
								.toString().trim())
								+ Integer.parseInt(pcs_update.getText()
										.toString().trim());
						entIdUpdata();
						pcs_update.clearFocus();
						entIdQuery();
						pcs_update.setText("1");

					}
				}
				return false;
			}
		});*/

		pcs_code.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					// ����ENTER��
					if (keyCode == KeyEvent.KEYCODE_ENTER) {

						booType = false;

						tempQueryId = pcs_code.getText().toString().trim();
						// pd_scan_code.clearFocus();
						entIdQuery();
						//pcs_update.setText("1");

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
		pcs_id = (EditText) findViewById(R.id.pcs_id);
		pcs_code = (EditText) findViewById(R.id.pcs_code);
		pcs_cgsl = (EditText) findViewById(R.id.pcs_cgsl);
		pcs_name = (TextView) findViewById(R.id.pcs_name);
		pcs_spsl = (TextView) findViewById(R.id.pcs_spsl);
		pcs_zxsl = (TextView) findViewById(R.id.pcs_zxsl);
		pcs_xssl= (TextView) findViewById(R.id.pcs_xssl);
		// showExtra = (TextView) findViewById(R.id.showExtra);
		mScan = (Button) findViewById(R.id.scan1);
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
		pcs_id.setText("");
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
		WYZCGData data = new WYZCGData();
		if (strTmString != "") {
			data.SQL_Id = pcs_id.getText().toString().trim();
			data.SQL_Code = pcs_code.getText().toString();
		
			data.SQL_CGSL = cgslUpdate;

			Boolean error = dbhelper.updateCG(true, data);

			Toast.makeText(PurchasActivity.this, "���ݸ��³ɹ�", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(PurchasActivity.this, "���Ȳ�ѯ--=======",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void entIdQuery() {
		Log.e("SqlError", "���ݲ�����һ����");
		if (tempQueryId.length() == 0) {
			Toast.makeText(PurchasActivity.this, "���������", Toast.LENGTH_SHORT)
					.show();
		} else {
			WYZCGData data = dbhelper.QueryCGData(booType, tempQueryId);
			// txtId.setText(data.SQL_Id);
			if (data != null) {
				strBMString = data.SQL_Code;
				strTmString = data.SQL_Id;
				pcs_id.setText(strTmString);
				pcs_code.setText(data.SQL_Code);
				pcs_name.setText(data.SQL_Name);
				// pcs_stock.setText(data.SQL_Dj);
				

				pcs_spsl.setText(String.valueOf(data.SQL_SPSL));
				pcs_cgsl.setText(String.valueOf(data.SQL_CGSL));
				pcs_zxsl.setText(String.valueOf(data.SQL_ZXSL));
				pcs_xssl.setText(String.valueOf(data.SQL_XSSL));
				
				
				
				/*
				 * Toast.makeText(ScanActivity.this, data.SQL_Name,
				 * Toast.LENGTH_SHORT).show();
				 */

			} else {
				strBMString = "";
				strTmString = "";
				pcs_code.setText("");
				pcs_name.setText("");
				pcs_spsl.setText("");
				pcs_cgsl.setText("");
				pcs_zxsl.setText("");
				pcs_xssl.setText("");
				Toast.makeText(PurchasActivity.this, "����Ʒ�����ڣ����������룡",
						Toast.LENGTH_SHORT).show();

			}// v.setVisibility(View.GONE);
		}
	}
	
	
	

	private void exitDialog() {
		Dialog dialog = new AlertDialog.Builder(this)
				.setIcon(R.drawable.tab_exit_pressed).setTitle("�˳��ɹ���")
				.setMessage("�˳���Ʒ�ɹ���"+"\n"
				+"����ͳ���ˣ�"+number+"����ͬ����Ʒ")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dbhelper.Close();
						PurchasActivity.this.finish();
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
