package com.zzu.wyz;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.device.DeviceManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zzu.wyz.util.ActionSheet;
import com.zzu.wyz.util.ActionSheet.MenuItemClickListener;
import com.zzu.wyz.util.MetaStaticData;

public class MainActivity extends FragmentActivity implements OnClickListener,
		MenuItemClickListener {
	// �󶨵����к�
	final static private String[] deviceIDs = { "67211546752807",
			"67211546752783", "67211546752744", "67211546752767",
			"67221538701699", "67211546752781", "67211546752772",
			"67211546752801", "67211546752778", "67211546752755" ,
			"67221511246764"//�Լ���������
			};

	private int index = -1;
	// final static private String[] deviceIDs = {"67221538701699",};
	private ViewPager mViewPager;
	private FragmentPagerAdapter mAdapter;
	private List<Fragment> mFragments;
	private static int currentItem;
	private LinearLayout mTabWeixin;
	private LinearLayout mTabFrd;
	private LinearLayout mTabAddress;
	private LinearLayout mTabSettings;
	String dloadresult, checkFile, messageDlod;
	Handler handler1, handler2;
	private ImageButton mImgWeixin;
	private ImageButton mImgFrd;
	private ImageButton mImgAddress;
	private ImageButton mImgSettings;
	// �豸������
	DeviceManager deviceManager;
	private TextView newsinfo;
	String strId;
	EditText employeeId;

	private ProgressBar bar;
	// private EditText ipShow;
	int number = 0;
	private String ipConfig;

	// ���浽�����ļ�
	SharedPreferences share;
	SharedPreferences.Editor edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		// ��ʼ���豸������
		deviceManager = new DeviceManager();
		// ��ȡ�豸���к�
		String deviceID = deviceManager.getDeviceId();

		System.out.println("#######�豸���к�=" + deviceID);
		// id�s67221538701699
		// System.out.println("#####deviceIDs=" + deviceIDs[0].toString());
		// if(deviceID==deviceIDs[0]){

		for (int i = 0; i < deviceIDs.length; i++) {
			if (deviceIDs[i].toString().equals(deviceID)) {
				index = i;
				break;
			}
		}

		if (index != -1) {
			// System.out.println("---------------index="+index);
			initView();
			initEvent();

			setSelect(currentItem);

			share = getSharedPreferences(MetaStaticData.FILENAME, MODE_PRIVATE);
			 edit=share.edit();
			if (ipConfig== null) {
				ipConfig=share.getString("ip", "192.168.0.186"); 
			}

		} else {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			builder.setIcon(R.drawable.tab_exit_pressed);
			builder.setTitle("ϵͳ��ʾ");
			builder.setMessage("���豸�쳣������ϵ��ͿƼ���" + "\n"
					+ "       0371--63831686");
			builder.setCancelable(false);

			builder.create().show();
			// showToast("�豸���кŲ���ȷ������ϵ��ͿƼ���");

		}

	}

	private void initEvent() {
		mTabWeixin.setOnClickListener(this);
		mTabFrd.setOnClickListener(this);
		mTabAddress.setOnClickListener(this);
		mTabSettings.setOnClickListener(this);
	}

	LayoutInflater factory;
	View progressView;
	Dialog dialog;

	private void initView() {
		factory = LayoutInflater.from(MainActivity.this);

		progressView = factory.inflate(R.layout.progressbar, null);
		bar = (ProgressBar) progressView.findViewById(R.id.bar);
		newsinfo = (TextView) progressView.findViewById(R.id.info);

		dialog = new AlertDialog.Builder(MainActivity.this)
				.setIcon(R.drawable.pic_dlod).setTitle("").setCancelable(false)
				.setView(progressView).create();

		mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

		mTabWeixin = (LinearLayout) findViewById(R.id.id_tab_weixin);
		mTabFrd = (LinearLayout) findViewById(R.id.id_tab_frd);
		mTabAddress = (LinearLayout) findViewById(R.id.id_tab_address);
		mTabSettings = (LinearLayout) findViewById(R.id.id_tab_settings);

		mImgWeixin = (ImageButton) findViewById(R.id.id_tab_weixin_img);
		mImgFrd = (ImageButton) findViewById(R.id.id_tab_frd_img);
		mImgAddress = (ImageButton) findViewById(R.id.id_tab_address_img);
		mImgSettings = (ImageButton) findViewById(R.id.id_tab_settings_img);
		// ipShow = (EditText) findViewById(R.id.all_ip);
		mFragments = new ArrayList<Fragment>();
		Fragment mTab01 = new InventoryFragment();
		Fragment mTab02 = new PurchasFragment();
		Fragment mTab03 = new PriceFragment();
		Fragment mTab04 = new ExitFragment();
		mFragments.add(mTab01);
		mFragments.add(mTab02);
		mFragments.add(mTab03);
		mFragments.add(mTab04);

		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return mFragments.size();
			}

			@Override
			public Fragment getItem(int arg0) {

				return mFragments.get(arg0);
			}
		};
		mViewPager.setAdapter(mAdapter);

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				currentItem = mViewPager.getCurrentItem();
				// System.out.println("===================="+currentItem);
				if (MetaStaticData.FLAG_TAB == currentItem) {
					MainActivity.this.exitDialog();
				} else {
					setTab(currentItem);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_tab_weixin:
			setSelect(0);
			break;
		case R.id.id_tab_frd:
			setSelect(1);
			break;
		case R.id.id_tab_address:
			setSelect(2);
			break;
		case R.id.id_tab_settings:
			setSelect(3);
			break;

		default:
			break;
		}
	}

	private void setSelect(int i) {
		setTab(i);
		mViewPager.setCurrentItem(i);
	}

	private void setTab(int i) {
		resetImgs();
		// ����ͼƬΪ��ɫ
		// �л���������
		switch (i) {
		case 0:
			mImgWeixin.setImageResource(R.drawable.tab_pd_pressed);
			break;
		case 1:
			mImgFrd.setImageResource(R.drawable.tab_cg_pressed);
			break;
		case 2:
			mImgAddress.setImageResource(R.drawable.tab_hj_pressed);
			break;
		case 3:
			mImgSettings.setImageResource(R.drawable.tab_exit_pressed);
			break;
		}
	}

	/**
	 * �л�ͼƬ����ɫ
	 */
	private void resetImgs() {
		mImgWeixin.setImageResource(R.drawable.tab_pd_normal);
		mImgFrd.setImageResource(R.drawable.tab_cg_normal);
		mImgAddress.setImageResource(R.drawable.tab_hj_normal);
		mImgSettings.setImageResource(R.drawable.tab_exit_normal);
	}

	/*
	 * // menu�˵�ѡ��
	 * 
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * menu.add(Menu.NONE, Menu.FIRST + 1, 1, "��ѯ"); // menu.add(Menu.NONE,
	 * Menu.FIRST + 2, 2, "����"); menu.add(Menu.NONE, Menu.FIRST + 2, 2, "����");
	 * 
	 * return true; }
	 * 
	 * // �����˵���ÿһ�������
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { // �жϲ����Ĳ˵�ID case Menu.FIRST + 1: startActivity(new
	 * Intent(MainActivity.this, QueryMenu.class)); // Toast.makeText(this,
	 * "��ѡ����ǡ�ɾ���˵�����", Toast.LENGTH_SHORT).show(); break;
	 * 
	 * case Menu.FIRST + 2: Toast.makeText(this, "��ѡ����ǡ�����˵�����",
	 * Toast.LENGTH_SHORT).show(); break;
	 * 
	 * case Menu.FIRST + 2:
	 * 
	 * startActivity(new Intent(MainActivity.this, HelpAndQueit.class)); //
	 * Toast.makeText(this, "��ѡ����ǡ������˵�����", Toast.LENGTH_SHORT).show(); break;
	 * 
	 * } return false; }
	 * 
	 * // �رղ˵�ʱ����
	 * 
	 * @Override public void onOptionsMenuClosed(Menu menu) { //
	 * Toast.makeText(this, "ѡ��˵��ر���", Toast.LENGTH_SHORT).show(); }
	 * 
	 * // �򿪲˵�ʱ�����
	 * 
	 * @Override public boolean onPrepareOptionsMenu(Menu menu) {
	 * 
	 * Toast.makeText(this,
	 * "�ڲ˵���ʾ��onCreateOptionsMenu()����֮ǰ����ô˲����������ڴ˲���֮�����һЩԤ�����ܡ���",
	 * Toast.LENGTH_SHORT).show();
	 * 
	 * return true; }
	 */

	private void exitDialog() {
		Dialog dialog = new AlertDialog.Builder(this)
				.setIcon(R.drawable.tab_exit_pressed).setTitle("�����˳���")
				.setMessage("��ȷ��Ҫ�˳���������").setCancelable(false)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						MainActivity.this.finish();
						System.exit(0);
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						setSelect(0);

					}
				}).create();
		dialog.show();
	}

	private void ipDialog() {
		LayoutInflater factory = LayoutInflater.from(MainActivity.this);

		View myView = factory.inflate(R.layout.ip, null);

		final EditText ipShow = (EditText) myView.findViewById(R.id.all_ip);
		ipShow.setText(share.getString("ip", "������"));
		// System.out.println("###################ipshow=="+share.getString("ip",
		// "������"));
		// ipConfig =ipShow.getText().toString().trim();

		Dialog dialog = new AlertDialog.Builder(MainActivity.this)
				.setIcon(R.drawable.exit_log_out)
				.setTitle("��������ַ")
				.setView(myView)
				.setPositiveButton("����", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						// SharedPreferences.Editor edit = share.edit();
						ipConfig = ipShow.getText().toString().trim();
						if (isIP(ipConfig)) {
							edit.putString("ip", ipConfig);
							edit.commit();// �ύ����
							Toast.makeText(getApplicationContext(), "����ɹ�",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), "����ʧ��",
									Toast.LENGTH_SHORT).show();

						}

					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).create();
		dialog.show();
	}

	/*
	 * private void employeeidDialog() { LayoutInflater factory =
	 * LayoutInflater.from(MainActivity.this);
	 * 
	 * View myView = factory.inflate(R.layout.employeeid, null);
	 * 
	 * final EditText ipShow = (EditText) myView.findViewById(R.id.all_ip);
	 * ipShow.setText(share.getString("ip", "������"));
	 * //System.out.println("###################ipshow=="+share.getString("ip",
	 * "������")); // ipConfig =ipShow.getText().toString().trim();
	 * 
	 * Dialog dialog = new AlertDialog.Builder(MainActivity.this)
	 * .setIcon(R.drawable.pic_upload) .setTitle("���Ĺ��ţ�") .setView(myView)
	 * .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * 
	 * //SharedPreferences.Editor edit = share.edit(); //ipConfig
	 * =ipShow.getText().toString().trim(); if(isIP(ipConfig)){
	 * edit.putString("ip", ipConfig); edit.commit();// �ύ����
	 * Toast.makeText(getApplicationContext(), "����ɹ�",
	 * Toast.LENGTH_SHORT).show(); }else{
	 * Toast.makeText(getApplicationContext(), "����ʧ��",
	 * Toast.LENGTH_SHORT).show();
	 * 
	 * }
	 * 
	 * 
	 * 
	 * } }) .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * 
	 * } }).create(); dialog.show(); }
	 */

	private void progressDialog() {

		dialog.show();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.exitDialog();
		}
		return false;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		showActionSheet();
		return super.onMenuOpened(featureId, menu);
	}

	public void showActionSheet() {
		// ��������ť�ı䣬������Ӧ�ķ����仯
		String strupload = null;
		String strdlod = null;
		if (currentItem == 0) {
			strupload = "�̵�����";
			strdlod = "�̵��ϴ�";
		} else if (currentItem == 1) {
			strupload = "�ɹ�����";
			strdlod = "�ɹ��ϴ�";
		} else if (currentItem == 2) {
			strupload = "�˼�����";
			strdlod = "�˼��ϴ�";
		} else {
			strupload = "�������";
			strdlod = "���̼���";
		}
		setTheme(R.style.ActionSheetStyleIOS7);

		ActionSheet menuView = new ActionSheet(this);
		menuView.setCancelButtonTitle("ȡ���˵�");// before add items
		menuView.addItems(strupload, strdlod, "��������ַ", "���ڰ���");
		menuView.setItemClickListener(this);
		menuView.setCancelableOnTouchMenuOutside(true);
		menuView.showMenu();
	}

	@Override
	public void onItemClick(int itemPosition) {
		switch (itemPosition) {
		case 0:
			MainActivity.this.checkDlodAddress();
			// startActivity(new Intent(MainActivity.this, QueryMenu.class));
			break;
		case 1:
			MainActivity.this.checkUploadAddress();
			// startActivity(new Intent(MainActivity.this, QueryMenu.class));
			break;
		case 2:
			this.ipDialog();
			// startActivity(new Intent(MainActivity.this, HelpAndQueit.class));
			break;
		case 3:
			startActivity(new Intent(MainActivity.this, HelpAndQueit.class));
			// startActivity(new Intent(MainActivity.this, HelpAndQueit.class));
			break;
		default:
			break;
		}

		// Toast.makeText(this, (itemPosition + 1) + " click", 0).show();
	}

	// ���ص�����·��
	private void checkDlodAddress() {
		String s = share.getString("ip", "������").trim();
		if (currentItem == 0) {
			MainActivity.this
					.handlerDlod("http://" + s + MetaStaticData.URL_PDPATH,
							MetaStaticData.DATABASE_PDNAME);
			// System.out.println("currentitem=="+share.getString("ip",
			// "������").trim());
			// System.out.println("http://"+share.getString("ip",
			// "������").trim()+MetaStaticData.URL_PDPATH);
		} else if (currentItem == 1) {
			MainActivity.this
					.handlerDlod("http://" + s + MetaStaticData.URL_CGPATH,
							MetaStaticData.DATABASE_CGNAME);
		} else if (currentItem == 2) {
			MainActivity.this
					.handlerDlod("http://" + s + MetaStaticData.URL_HJPATH,
							MetaStaticData.DATABASE_HJNAME);
		}
	}

	// �ϴ�·���Լ��ϴ��ĵ�ַ
	private void checkUploadAddress() {
		String s = share.getString("ip", "������").trim();
		if (currentItem == 0) {
			MainActivity.this.handlerUpload(MetaStaticData.SDCARD_PATH,
					MetaStaticData.DATABASE_PDNAME, "http://" + s
							+ MetaStaticData.URL_UPPATh);
		} else if (currentItem == 1) {
			MainActivity.this.handlerUpload(MetaStaticData.SDCARD_PATH,
					MetaStaticData.DATABASE_CGNAME, "http://" + s
							+ MetaStaticData.URL_UPPATh);
		} else if (currentItem == 2) {
			MainActivity.this.handlerUpload(MetaStaticData.SDCARD_PATH,
					MetaStaticData.DATABASE_HJNAME, "http://" + s
							+ MetaStaticData.URL_UPPATh);
		}
	}

	/**
	 * �����ַ���
	 * 
	 * @param a
	 *            ����
	 * @param string
	 *            �û����
	 * @return
	 */
	public static String GetUpString(int a, String string) {
		String string2 = "pd";
		SimpleDateFormat strdate = new SimpleDateFormat("ddHHmm");//
		java.util.Date currentdate = new java.util.Date();// ��ǰʱ��
		String date = strdate.format(currentdate);
		if (a == 0)// �̵�
		{
			string2 = "pd";
		} else if (a == 1) {// �ɹ�
			string2 = "cg";
		} else if (a == 2) {// �˼�
			string2 = "hj";
		}
		return string2 + "_" + string + "_" + date + ".db";
	}

	// �������ضԻ��򣬵����ȷ����ʱ��ʼ����
	private void dlodDialog(final String Fileurl, final String dataBaseName) {

		// employeeId.setText(share.getString("ip", "������"));

		Dialog dialog = new AlertDialog.Builder(MainActivity.this)
				.setIcon(R.drawable.pic_dlod)
				.setTitle("��������")
				// .setView(myIdView)
				.setMessage(messageDlod)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					// ȷ����ť�����¼��Ĵ�����������
					@Override
					public void onClick(DialogInterface dialog, int which) {

						MainActivity.this.newsinfo
								.setText(R.string.progressbar_info);
						final Message msg2 = Message.obtain();

						new Thread(new Runnable() {

							@Override
							public void run() {
								/*
								 * DlodAndUpData dlodData = new DlodAndUpData();
								 * dloadresult = dlodData.DoloadFile(Fileurl,
								 * dataBaseName);
								 */
								dloadresult = MainActivity.this.DoloadFile(
										Fileurl, dataBaseName);
								if (dloadresult == "success") {
									msg2.what = 3;

								} else {
									msg2.what = 4;

								}
								handler2.sendMessage(msg2);

							}

						}).start();

						MainActivity.this.progressDialog();
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).create();
		dialog.show();

	}

	private void handlerUpload(String path, String dataBaseName,
			String uploadUrl) {
		/*
		 * DlodAndUpData upload = new DlodAndUpData(); checkFile =
		 * upload.selectFile(dataBaseName);
		 */
		checkFile = MainActivity.this.selectFile(dataBaseName);
		if (checkFile == "success") {
			messageDlod = "�ļ����ڣ��Ƿ��ϴ��ļ���";
		} else {
			messageDlod = "�ļ������ڣ��޷��ϴ��ļ���";
		}

		// �������ضԻ���
		MainActivity.this.uploadDialog(path, dataBaseName, uploadUrl);
		handler1 = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					// uploadBut.setBackgroundColor(Color.GRAY);
					// uploadBut.setClickable(true);
					Toast.makeText(MainActivity.this, "�ϴ��ļ���Ϣ�ɹ���",
							Toast.LENGTH_SHORT).show();
					number = 0;
					newsinfo.setText("�ϴ��ɹ���");
					MainActivity.this.bar.setProgress(number);
					dialog.dismiss();
					break;
				case 0:
					Toast.makeText(MainActivity.this, "�ϴ��ļ���Ϣʧ�ܣ�",
							Toast.LENGTH_SHORT).show();
					number = 0;
					newsinfo.setText("�ϴ�ʧ�ܣ�");
					MainActivity.this.bar.setProgress(number);
					dialog.dismiss();
					break;
				default:
					break;
				}
			}

		};
		// �����ϴ�����
	}

	private void handlerDlod(String url, final String dataBaseName) {
		/*
		 * DlodAndUpData dloadData = new DlodAndUpData(); checkFile =
		 * dloadData.selectFile(dataBaseName);
		 */

		checkFile = MainActivity.this.selectFile(dataBaseName);
		if (checkFile == "success") {
			// messageDlod = "�ļ��Ѿ����ڣ��Ƿ������ļ���";
			if (currentItem == 0) {
				messageDlod = "�����̵����ݴ��ڣ��Ƿ񸲸����أ�";

			} else if (currentItem == 1) {
				messageDlod = "���زɹ����ݴ��ڣ��Ƿ񸲸����أ�";

			} else if (currentItem == 2) {
				messageDlod = "���غ˼����ݴ��ڣ��Ƿ񸲸����أ�";

			} else {
				messageDlod = "�������";

			}
		} else {
			// messageDlod = "�ļ������ڣ��Ƿ����أ�";
			if (currentItem == 0) {
				messageDlod = "�̵����ݲ����ڣ��Ƿ����أ�";

			} else if (currentItem == 1) {
				messageDlod = "�ɹ����ݲ����ڣ��Ƿ����أ�";

			} else if (currentItem == 2) {
				messageDlod = "�˼����ݲ����ڣ��Ƿ����أ�";

			} else {
				messageDlod = "�������";

			}
		}
		MainActivity.this.dlodDialog(url, dataBaseName);
		handler2 = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				super.handleMessage(msg);

				switch (msg.what) {
				case 3:
					MainActivity.this.copyFile(dataBaseName);

					Toast.makeText(MainActivity.this, "�ļ����سɹ����������һ������",
							Toast.LENGTH_SHORT).show();
					number = 0;
					newsinfo.setText("���سɹ���");
					MainActivity.this.delFile(dataBaseName);
					MainActivity.this.bar.setProgress(number);
					dialog.dismiss();
					// MainActivity.this.copyFile(dataBaseName);
					// �˷�����������������
					/*
					 * startActivity(new Intent(MainActivity.this,
					 * MainActivity.class));
					 */
					break;
				case 4:
					Toast.makeText(MainActivity.this, "�ļ�����ʧ�ܣ��������磡",
							Toast.LENGTH_SHORT).show();
					number = 0;
					newsinfo.setText("����ʧ�ܣ�");
					MainActivity.this.bar.setProgress(number);
					dialog.dismiss();
					/*
					 * startActivity(new Intent(MainActivity.this,
					 * MainActivity.class));
					 */
					break;

				}
			}
		};
	}

	// �����ϴ��Ի��򣬵����ȷ����ʱ��ʼ�ϴ�
	private void uploadDialog(final String path, final String dataBaseName,
			final String uploadUrl) {

		LayoutInflater factory = LayoutInflater.from(MainActivity.this);

		View myIdView = factory.inflate(R.layout.employeeid, null);

		employeeId = (EditText) myIdView.findViewById(R.id.all_employee);

		Dialog dialog = new AlertDialog.Builder(MainActivity.this)
				.setIcon(R.drawable.pic_upload)
				.setTitle("�ϴ�����")
				.setView(myIdView)
				// .setMessage(messageDlod)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					// ȷ����ť�����¼��Ĵ��������ϴ�����
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// �ж�һ�¹��ŵĳ���
						strId = MainActivity.this.GetUpString(currentItem,
								employeeId.getText().toString().trim());
						System.out.println("%%%%%%%%%strId=" + strId);
						MainActivity.this.newsinfo
								.setText(R.string.progressbar_info);
						final Message msg = Message.obtain(); // Get the Message
																// object
						// Create a new thread to do the upload
						new Thread(new Runnable() {
							// DlodAndUpData updata = new DlodAndUpData();

							@Override
							public void run() {
								// TODO Auto-generated method stub
								boolean flag = MainActivity.this.uploadFile(
										path, dataBaseName, uploadUrl);

								if (flag) {
									msg.what = 1; // Upload file succeeded.
								} else {
									msg.what = 0; // Upload file failed.
								}
								handler1.sendMessage(msg);
							}
						}).start();

						MainActivity.this.progressDialog();

					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).create();
		dialog.show();
	}

	public void copyFile(String databasename) {
		int b = 0;
		InputStream in = null;
		OutputStream out = null;
		File file = new File(MetaStaticData.SDCARD_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		try {
			in = new FileInputStream(MetaStaticData.SDCARD_COPYPATH
					+ databasename);

			out = new FileOutputStream(MetaStaticData.SDCARD_PATH
					+ databasename);
			byte[] bs = new byte[8 * 1024];

			while ((b = in.read(bs)) != -1) {
				out.write(bs, 0, b);
			}
			out.close();
			in.close();

		} catch (FileNotFoundException e2) {
			System.out.println("�Ҳ���ָ���ļ�");
		} catch (IOException e1) {
			System.out.println("�ļ����ƴ���");
		}
		System.out.println("�ļ��Ѹ���");
	}

	public String DoloadFile(String Fileurl, String dataBaseName) {
		// NetOperator netOperator = new NetOperator();
		int barcount = 0;
		int contentLength = 0;
		int PB_Count = 0;
		Log.e("sqlError", "url1:" + Fileurl);
		File file = new File(MetaStaticData.SDCARD_COPYPATH);
		// ���Ŀ���ļ��Ѿ����ڣ���ɾ�����������Ǿ��ļ���Ч��
		if (!file.exists()) {
			file.mkdirs();
		}
		String path = MetaStaticData.SDCARD_COPYPATH + dataBaseName;
		// path += "pd.db";
		file = new File(path);

		if (file.exists()) {
			file.delete();
		}
		try {
			// ����URL
			URL url = new URL(Fileurl);

			// ������
			URLConnection con = url.openConnection();
			// ����ʱ�䲻�ܳ���10��
			con.setConnectTimeout(10000);
			// д����ʱ�䲻�ܴ���60��
			con.setReadTimeout(60000);
			// ����ļ��ĳ���
			contentLength = con.getContentLength();
			System.out.println("���ݳ��� :" + contentLength);
			// ������
			InputStream is = con.getInputStream();
			// 1K�����ݻ���
			byte[] bs = new byte[1024];

			PB_Count = (int) (contentLength / 1024) + 1;

			System.out.println("PB_Count" + PB_Count);
			MainActivity.this.bar.setMax(PB_Count + 500);
			// System.out.println("@@@@@@@@@@@@@"+MainActivity.this.bar.getMax());
			// ��ȡ�������ݳ���
			int len;
			// ������ļ���
			OutputStream os = new FileOutputStream(path);
			// ��ʼ��ȡ
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
				// publishProgress(barcount++);
				// if (isCancelled()) break;
				number++;
				// System.out.println("##########################" + number);
				MainActivity.this.bar.setProgress(number);
			}

			// ��ϣ��ر���������
			os.close();
			is.close();
			/*
			 * netOperator.operator(); if(barcount<PB_Count) {
			 * publishProgress(barcount); barcount++; }
			 */

		} catch (Exception e) {
			e.printStackTrace();
			return "defeat";
		}
		// for (i = 10; i <= 100; i+=10) {
		//
		// //publishProgress(i);
		// }
		return "success";
	}

	/**
	 * Upload the specified file to remote server.
	 * 
	 * @param filepath
	 *            The path of the local file.
	 * @param uploadUrl
	 *            The server url.
	 * @return The upload status.
	 */
	public boolean uploadFile(String path, String dataBaseName, String uploadUrl) {
		boolean status = true;
		String filepath = path + dataBaseName;
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "******";
		int count, up_count;
		try {
			URL url = new URL(uploadUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setConnectTimeout(10000);
			httpURLConnection.setReadTimeout(60000);

			// Set the size of the transfer stream, in case that the application
			// collapses due to small memory, this method is used when we don't
			// know the size of the content, we use HTTP request without cache
			httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K

			// Set the input and output
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);

			// Set the HTTP method
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			// Get outputstream according to the url connection
			DataOutputStream dos = new DataOutputStream(
					httpURLConnection.getOutputStream());

			// Write the HTTP POST header
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
					+ strId + "\"" + end);
			Log.e("sqlError", filepath.substring(filepath.lastIndexOf("/") + 1));
			dos.writeBytes(end);

			FileInputStream fis = new FileInputStream(filepath);
			count = fis.available();
			// System.out.println("filesize"+count);
			up_count = (count / 1024) / 8 + 1;
			// System.out.println("up_count="+up_count);
			MainActivity.this.bar.setMax(up_count + 500);
			// System.out.println("***hello"+MainActivity.this.bar.getMax());
			int bufferSize = 8 * 1024; // The size of the buffer, 8KB.
			byte[] buffer = new byte[bufferSize];
			// MainActivity.this.bar.setMax(count);
			// System.out.println("&&&&&&&&&&&&&&&&&&&"+MainActivity.this.bar.getMax());
			int length = 0;

			while ((length = fis.read(buffer)) != -1) {
				// System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+length);
				// Write data to DataOutputStream
				number++;
				MainActivity.this.bar.setProgress(number);
				// System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@"+number);
				dos.write(buffer, 0, length);
			}

			dos.writeBytes(end);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);

			fis.close(); // Close the FileInputStream.
			dos.flush(); // Flush the data to DataOutputStream.

			// Get the content of the response
			InputStream is = httpURLConnection.getInputStream();

			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr, 8 * 1024);
			String result = br.readLine();

			// Log.d(Tag, result);

			// dos.close(); // Will respond I/O exception if closes.
		} catch (Exception e) {
			e.printStackTrace();
			status = false;
		}
		return status;
	}

	public String selectFile(String dataBaseName) {
		String path = MetaStaticData.SDCARD_PATH + dataBaseName;
		File file = new File(path);
		if (file.exists()) {

			return "success";
		}

		return "NotFile!";

	}

	/**
	 * IP��ַУ��
	 * 
	 * @param ip
	 *            ��У���Ƿ���IP��ַ���ַ���
	 * @return �Ƿ���IP��ַ
	 */
	public static boolean isIP(String ip) {
		Pattern pattern = Pattern
				.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	private String delFile(String databasename) {
		String path = MetaStaticData.SDCARD_COPYPATH + databasename;
		File file = new File(path);
		if (!file.exists()) {
			return "NotFile!";
		}

		file.delete();
		return "success";

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		MainActivity.this.finish();
		// System.out.println("dddddddddddddddd==="+"onDestroy()");
	}

}
