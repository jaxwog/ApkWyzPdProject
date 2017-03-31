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

public class PriceFragment extends Fragment {
	
	private SqlLiteHelper dbhelper;
 private String strResult;
 EditText txt;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.tab_hj1, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		
		ImageButton picBut = (ImageButton) getActivity().findViewById(
				R.id.pic_pic);
		// Button but = (Button) getActivity().findViewById(R.id.pd_pd);
		picBut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				strResult = PriceFragment.this.selectFile();
				// 添加对话框，如果数据不存在，显示出来下载文件
				if (strResult.equals("NotFile!")) {
					PriceFragment.this.noFileDialog();
				} else {

				Intent it = new Intent(getActivity(), PriceCelectActivity.class);

				startActivity(it);
				}
			}
		});
		ImageButton queBut = (ImageButton) getActivity().findViewById(
				R.id.pic_query);
		// Button but = (Button) getActivity().findViewById(R.id.pd_pd);
		queBut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PriceFragment.this.fileRode();
				Intent it = new Intent(getActivity(), PriceQueryActivity.class);

				startActivity(it);
				
			}
		});
		
		ImageButton exproBut = (ImageButton) getActivity().findViewById(
				R.id.pic_export);
		// Button but = (Button) getActivity().findViewById(R.id.pd_pd);
		exproBut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PriceFragment.this.fileRode();

				PriceFragment.this.exportDialog();
			}
		});

		
		ImageButton transBut = (ImageButton) getActivity().findViewById(
				R.id.pic_trans);
		// Button but = (Button) getActivity().findViewById(R.id.pd_pd);
		transBut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PriceFragment.this.fileRode();
				PriceFragment.this.transDialog();
				
			}
		});
	}

	private void transDialog() {
		Dialog dialog = new AlertDialog.Builder(getActivity())
				.setIcon(R.drawable.pic_trash).setTitle("清空核价：")
				.setMessage("您确定要清空核价数据吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// getActivity().finish();
						// 清空核价的操作方法
						
						dbhelper = new SqlLiteHelper(MetaStaticData.SDCARD_PATH+MetaStaticData.DATABASE_PDNAME);
						if(dbhelper.delete()==true){
							Toast.makeText(getActivity(), "核价数据删除成功",
									Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(getActivity(), "核价数据不存在！",
									Toast.LENGTH_SHORT).show();
						}
						
						/*if (PriceFragment.this.delFile() == "success") {
							Toast.makeText(getActivity(), "数据删除成功",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getActivity(), "数据不存在！",
									Toast.LENGTH_SHORT).show();
						}*/
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).create();
		dialog.show();
	}
	private String delFile() {
		String path = MetaStaticData.SDCARD_PATH + MetaStaticData.DATABASE_HJNAME;
		File file = new File(path);
		if (!file.exists()) {
			return "NotFile!";
		}

		file.delete();
		return "success";

	}
	
	
	private String selectFile() {
		String path = MetaStaticData.SDCARD_PATH
				+ MetaStaticData.DATABASE_HJNAME;
		File file = new File(path);
		if (file.exists()) {

			return "success";
		}

		return "NotFile!";

	}
	private void noFileDialog() {
		Dialog dialog = new AlertDialog.Builder(getActivity())
				.setIcon(R.drawable.exit_log_out).setTitle("警告")
				.setMessage("核价数据不存在！请下载！")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

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
			txt.setHint("请你输入员工编号");
			txt.setGravity(Gravity.CENTER);
		
		Dialog dialog = new AlertDialog.Builder(getActivity())
				.setIcon(R.drawable.pic_exportt)
				.setTitle("导出数据：")
				.setView(txt)
				//.setMessage("您确定要导出核价数据吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String id = txt.getText().toString().trim(); 
						if(id.length()>0){
							PriceFragment.this
									.copyFile(id,MetaStaticData.DATABASE_HJNAME);
							}else{
								Toast.makeText(getActivity(), "请你输入员工编号", Toast.LENGTH_SHORT).show();
							}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).create();
		dialog.show();
	}
	
	
	/**
	 * 返回字符串
	 * 
	 * @param a
	 *            类型
	 * @param string
	 *            用户编号
	 * @return
	 */
	public static String GetUpString( String string) {
		String string2 = "hj";
		SimpleDateFormat strdate = new SimpleDateFormat("ddHHmm");//
		java.util.Date currentdate = new java.util.Date();// 当前时间
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
			String s = PriceFragment.this.GetUpString(id);
			out = new FileOutputStream(MetaStaticData.SDCARD_EXPORTPATH
					+ s);
			byte[] bs = new byte[8 * 1024];

			while ((b = in.read(bs)) != -1) {
				out.write(bs, 0, b);
			}
			out.close();
			in.close();

		} catch (FileNotFoundException e2) {
			System.out.println("找不到指定文件");
			Toast.makeText(getActivity(), "核价数据导出失败", Toast.LENGTH_SHORT)
			.show();
		} catch (IOException e1) {
			System.out.println("文件复制错误");
			Toast.makeText(getActivity(), "采购数据导出失败", Toast.LENGTH_SHORT)
			.show();
		}
		Toast.makeText(getActivity(), "核价数据导出成功", Toast.LENGTH_SHORT)
		.show();
		System.out.println("文件已复制");
	}
	
	
	private void fileRode(){
		File file = new File(MetaStaticData.SDCARD_PATH);
		
		if (!file.exists()) {
			file.mkdirs();
		}
		}

}
