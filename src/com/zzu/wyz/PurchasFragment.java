package com.zzu.wyz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import com.zzu.wyz.util.MetaStaticData;
import com.zzu.wyz.util.SqlLiteHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class PurchasFragment extends Fragment {
EditText txt;
	SqlLiteHelper dbhelper;
private String strResult;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.tab_cg1, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		ImageButton purBut = (ImageButton) getActivity().findViewById(
				R.id.pcs_pcs);
		// Button but = (Button) getActivity().findViewById(R.id.pd_pd);
		purBut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				strResult = PurchasFragment.this.selectFile();
				if (strResult.equals("NotFile!")) {
					PurchasFragment.this.noFileDialog();
				} else {
				Intent it = new Intent(getActivity(), PurchasActivity.class);

				startActivity(it);

			}}
		});

		ImageButton exproBut = (ImageButton) getActivity().findViewById(
				R.id.pcs_export);
		// Button but = (Button) getActivity().findViewById(R.id.pd_pd);
		exproBut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PurchasFragment.this.fileRode();

				PurchasFragment.this.exportDialog();
			}
		});

		ImageButton queBut = (ImageButton) getActivity().findViewById(
				R.id.pcs_query);
		// Button but = (Button) getActivity().findViewById(R.id.pd_pd);
		queBut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PurchasFragment.this.fileRode();
				Intent it = new Intent(getActivity(),
						PurchasQueryActivity.class);

				startActivity(it);

			}
		});

		ImageButton transBut = (ImageButton) getActivity().findViewById(
				R.id.pcs_trans);
		// Button but = (Button) getActivity().findViewById(R.id.pd_pd);
		transBut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PurchasFragment.this.fileRode();
				PurchasFragment.this.transDialog();

			}
		});
	}

	private void transDialog() {
		Dialog dialog = new AlertDialog.Builder(getActivity())
				.setIcon(R.drawable.pic_trash).setTitle("��ղɹ���")
				.setMessage("��ȷ��Ҫ��ղɹ�������")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// getActivity().finish();
						// ��ղɹ��Ĳ�������
						PurchasFragment.this.delSQLData();

						/*
						 * if (PurchasFragment.this.delFile() == "success") {
						 * Toast.makeText(getActivity(), "����ɾ���ɹ�",
						 * Toast.LENGTH_SHORT).show(); } else {
						 * Toast.makeText(getActivity(), "���ݲ����ڣ�",
						 * Toast.LENGTH_SHORT).show(); }
						 */
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).create();
		dialog.show();
	}

	private void exportDialog() {
		 txt = new EditText(getActivity());
			txt.setBackgroundResource(R.drawable.bg_edittext);
			txt.setTextSize(20);
			txt.setHint("��������Ա�����");
			txt.setGravity(Gravity.CENTER);
		Dialog dialog = new AlertDialog.Builder(getActivity())
				.setIcon(R.drawable.pic_exportt)
				.setTitle("�������ݣ�")
				.setView(txt)
				//.setMessage("��ȷ��Ҫ�����ɹ�������")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String id = txt.getText().toString().trim(); 
						if(id.length()>0){
							PurchasFragment.this
									.copyFile(id,MetaStaticData.DATABASE_CGNAME);
							}else{
								Toast.makeText(getActivity(), "��������Ա�����", Toast.LENGTH_SHORT).show();
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

	private String delFile() {
		String path = MetaStaticData.SDCARD_PATH
				+ MetaStaticData.DATABASE_CGNAME;
		File file = new File(path);
		if (!file.exists()) {
			return "NotFile!";
		}

		file.delete();
		return "success";

	}
	
	private String selectFile() {
		String path = MetaStaticData.SDCARD_PATH
				+ MetaStaticData.DATABASE_CGNAME;
		File file = new File(path);
		if (file.exists()) {

			return "success";
		}

		return "NotFile!";

	}
	private void noFileDialog() {
		Dialog dialog = new AlertDialog.Builder(getActivity())
				.setIcon(R.drawable.exit_log_out).setTitle("����")
				.setMessage("�ɹ����ݲ����ڣ������أ�")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).create();
		dialog.show();
	}

	private void delSQLData() {
		dbhelper = new SqlLiteHelper(MetaStaticData.SDCARD_PATH
				+ MetaStaticData.DATABASE_CGNAME);
		if (dbhelper.deleteCG()) {
			Toast.makeText(getActivity(), "�ɹ�����ɾ���ɹ�", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(getActivity(), "ɾ��ʧ��", Toast.LENGTH_SHORT).show();
		}
		dbhelper.Close();

	}

	/**
	 * �����ַ���
	 * 
	
	 * @param string
	 *            �û����
	 * @return
	 */
	public static String GetUpString( String string) {
		String string2 = "cg";
		SimpleDateFormat strdate = new SimpleDateFormat("ddHHmm");//
		java.util.Date currentdate = new java.util.Date();// ��ǰʱ��
		String date = strdate.format(currentdate);
		
		return string2 + "_" + string + "_" + date + ".db";
	}
	
	
	public void copyFile(String id,String databasename) {
		int b = 0;
		InputStream in = null;
		OutputStream out = null;
		File file = new File(MetaStaticData.SDCARD_EXPORTPATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		String path = MetaStaticData.SDCARD_EXPORTPATH
				+ databasename;
		file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		try {
			in = new FileInputStream(MetaStaticData.SDCARD_PATH
					+ databasename);

			String s = PurchasFragment.this.GetUpString(id);
			
			out = new FileOutputStream(MetaStaticData.SDCARD_EXPORTPATH
					+ s);
			byte[] bs = new byte[8 * 1024];

			while ((b = in.read(bs)) != -1) {
				out.write(bs, 0, b);
			}
			out.close();
			in.close();

		} catch (FileNotFoundException e2) {
			System.out.println("�Ҳ���ָ���ļ�");
			Toast.makeText(getActivity(), "�ɹ����ݵ���ʧ��", Toast.LENGTH_SHORT)
			.show();
		} catch (IOException e1) {
			System.out.println("�ļ����ƴ���");
			Toast.makeText(getActivity(), "�ɹ����ݵ���ʧ��", Toast.LENGTH_SHORT)
			.show();
		}
		Toast.makeText(getActivity(), "�ɹ����ݵ����ɹ�", Toast.LENGTH_SHORT)
		.show();
		System.out.println("�ļ��Ѹ���");
	}
	
	private void fileRode(){
		File file = new File(MetaStaticData.SDCARD_PATH);
		
		if (!file.exists()) {
			file.mkdirs();
		}
		}

}
