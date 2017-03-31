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

	// �����ѯ��������ͨ��get�����õ����ݣ�����ɨ�������ʾ
	private String dataName, dataId, dataCode, dataDj, dataSpsl;
	// �����ѯ����ֵ��get��set�����ж����ݿ����Ƿ��ѯ������
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
		super.setContentView(R.layout.pd_query);
		dbhelper = new SqlLiteHelper(MetaStaticData.SDCARD_PATH
				+ MetaStaticData.DATABASE_PDNAME);

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
		pd_qid.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					// ����ENTER��
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
					// ����ENTER��
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
				int position, long id) { // ��ʾѡ��ı��ʱ�򴥷�
			String value = listspsl.get(position).toString();
			//String value = parent.getItemAtPosition(position).toString(); // ȡ��ѡ�е�ѡ��
			InventoryQueryActivity.this.pd_qspsl.setText(value); // �����ı��������
			//soundpool.play(soundid, 1, 1, 0, 0, 1);
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
	 * Toast.makeText(PriceQueryActivity.this, "���Ȳ�ѯ--=======",
	 * Toast.LENGTH_SHORT).show(); } }
	 */

	private void entIdQuery() {
		Log.e("SqlError", "���ݲ�����һ����");
		if (tempQueryId.length() == 0) {
			Toast.makeText(InventoryQueryActivity.this, "���������",
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
				
				//�Զ�����ܺ������б��
				
				
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
				this.check_stieno.setPrompt("��ѡ����Ļ��ܺ�");
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
				Toast.makeText(InventoryQueryActivity.this, "����Ʒ�����ڣ����������룡",
						Toast.LENGTH_SHORT).show();

			}// v.setVisibility(View.GONE);
		}
	}

	private void exitDialog() {
		Dialog dialog = new AlertDialog.Builder(this)
				.setIcon(R.drawable.tab_exit_pressed).setTitle("�˳���ѯ��")
				.setMessage("�˳���Ʒ�̵��ѯ��")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dbhelper.Close();
						InventoryQueryActivity.this.finish();
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
