package com.zzu.wyz;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zzu.wyz.util.MetaStaticData;
import com.zzu.wyz.util.SqlLiteHelper;

public class InventoryFragment extends Fragment {

	ImageButton uploadBut;
	EditText txt;
	String dloadresult, checkFile, messageDlod, strResult;
	Handler handler1, handler2;

	private SqlLiteHelper dbhelper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.tab_pd1, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		// 盘点监听对话框
		ImageButton pdBut = (ImageButton) getActivity()
				.findViewById(R.id.pd_pd);
		// Button but = (Button) getActivity().findViewById(R.id.pd_pd);
		pdBut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				strResult = InventoryFragment.this.selectFile();
				// 添加对话框，如果数据不存在，显示出来下载文件
				if (strResult.equals("NotFile!")) {
					InventoryFragment.this.noFileDialog();
				} else {

					Intent it = new Intent(getActivity(),
							WarehouseActivity.class);

					startActivity(it);
				}
			}
		});
		// 盘点监听对话框
		ImageButton queBut = (ImageButton) getActivity().findViewById(
				R.id.pd_query);
		// Button but = (Button) getActivity().findViewById(R.id.pd_pd);
		queBut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InventoryFragment.this.fileRode();
				Intent it = new Intent(getActivity(),
						InventoryQueryActivity.class);

				startActivity(it);
			}
		});

		ImageButton exproBut = (ImageButton) getActivity().findViewById(
				R.id.pd_export);
		// Button but = (Button) getActivity().findViewById(R.id.pd_pd);
		exproBut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InventoryFragment.this.fileRode();
				InventoryFragment.this.exportDialog();
			}
		});

		/*
		 * // 下载数据监听对话框 ImageButton dlodBut = (ImageButton)
		 * getActivity().findViewById( R.id.pd_download);
		 * dlodBut.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { dloadresult = null; //
		 * 调用下载对话框 checkFile = InventoryFragment.this.selectFile();
		 * if(checkFile=="success"){ messageDlod = "文件已经存在，是否下载文件？"; }else{
		 * messageDlod = "文件不存在，是否下载？"; } InventoryFragment.this.dlodDialog();
		 * handler2 = new Handler() {
		 * 
		 * @Override public void handleMessage(Message msg) {
		 * 
		 * super.handleMessage(msg);
		 * 
		 * switch (msg.what) { case 3: Toast.makeText(getActivity(),
		 * "文件下载成功，请继续下一步操作", Toast.LENGTH_SHORT).show(); break; case 4:
		 * Toast.makeText(getActivity(), "文件下载失败，请检测网络！",
		 * Toast.LENGTH_SHORT).show(); break;
		 * 
		 * } } };
		 * 
		 * } });
		 * 
		 * // 上传数据按钮监听事件 uploadBut = (ImageButton)
		 * getActivity().findViewById(R.id.pd_upload);
		 * uploadBut.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { checkFile =
		 * InventoryFragment.this.selectFile(); if(checkFile=="success"){
		 * messageDlod = "文件存在，是否上传文件？"; }else{ messageDlod = "文件不存在，无法上传文件！"; }
		 * 
		 * // 调用下载对话框 InventoryFragment.this.uploadDialog(); handler1 = new
		 * Handler() {
		 * 
		 * @Override public void handleMessage(Message msg) { // TODO
		 * Auto-generated method stub super.handleMessage(msg); switch
		 * (msg.what) { case 1: // uploadBut.setBackgroundColor(Color.GRAY); //
		 * uploadBut.setClickable(true); Toast.makeText(getActivity(),
		 * "上传文件信息成功！", Toast.LENGTH_SHORT).show(); break; case 0:
		 * Toast.makeText(getActivity(), "上传文件信息失败！",
		 * Toast.LENGTH_SHORT).show(); break; default: break; } }
		 * 
		 * };
		 * 
		 * } });
		 */

		ImageButton transBut = (ImageButton) getActivity().findViewById(
				R.id.pd_trans);
		transBut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 调用下载对话框

				InventoryFragment.this.delDialog();

			}
		});

	}

	/*
	 * // 监听下载对话框，当点击确定的时候开始下载 private void dlodDialog() { Dialog dialog = new
	 * AlertDialog.Builder(getActivity()) .setIcon(R.drawable.pic_dlod)
	 * .setTitle("下载数据") .setMessage(messageDlod) .setPositiveButton("确定", new
	 * DialogInterface.OnClickListener() {
	 * 
	 * // 确定按钮进行事件的处理用于下载
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { final
	 * Message msg2 = Message.obtain();
	 * 
	 * new Thread(new Runnable() {
	 * 
	 * @Override public void run() {
	 * 
	 * dloadresult = InventoryFragment.this
	 * .DoloadFile(MetaStaticData.URL_PDLOADPATH); if (dloadresult == "success")
	 * { msg2.what = 3;
	 * 
	 * } else { msg2.what = 4;
	 * 
	 * } handler2.sendMessage(msg2);
	 * 
	 * } }).start();
	 * 
	 * }
	 * 
	 * }) .setNegativeButton("取消", new DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * 
	 * } }).create(); dialog.show();
	 * 
	 * }
	 * 
	 * // 监听上传对话框，当点击确定的时候开始上传 private void uploadDialog() { Dialog dialog = new
	 * AlertDialog.Builder(getActivity()) .setIcon(R.drawable.pic_upload)
	 * .setTitle("上传数据") .setMessage(messageDlod) .setPositiveButton("确定", new
	 * DialogInterface.OnClickListener() { // 确定按钮进行事件的处理用于上传数据
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * 
	 * final Message msg = Message.obtain(); // Get the Message // object //
	 * Create a new thread to do the upload new Thread(new Runnable() {
	 * 
	 * @Override public void run() { // TODO Auto-generated method stub boolean
	 * flag = uploadFile( MetaStaticData.SDCARD_PATH +
	 * MetaStaticData.DATABASE_PDNAME, MetaStaticData.URL_UPPATh); // Call the
	 * // upload // file // function
	 * 
	 * System.out.println("^^^^^^^^^^^^^^^^^^" + flag);
	 * 
	 * if (flag) { msg.what = 1; // Upload file succeeded. } else { msg.what =
	 * 0; // Upload file failed. } handler1.sendMessage(msg); } }).start();
	 * 
	 * 
	 * InventoryFragment.this.startUploadFile( WYZMetaData.SDCARD_PATH +
	 * WYZMetaData.DATABASE_NAME, WYZMetaData.URL_UPPATh);
	 * 
	 * } }) .setNegativeButton("取消", new DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * 
	 * } }).create(); dialog.show(); }
	 */

	// 监听删除对话框，当点击确定的时候删除本地数据
	private void delDialog() {
		Dialog dialog = new AlertDialog.Builder(getActivity())
				.setIcon(R.drawable.pic_trash)
				.setTitle("清空盘点")
				.setMessage("您确定要清空盘点数据吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					// 确定按钮进行事件的处理用于上传数据
					@Override
					public void onClick(DialogInterface dialog, int which) {
						InventoryFragment.this.fileRode();
						dbhelper = new SqlLiteHelper(MetaStaticData.SDCARD_PATH
								+ MetaStaticData.DATABASE_PDNAME);
						if (dbhelper.delete() == true) {
							Toast.makeText(getActivity(), "盘点数据清除成功",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getActivity(), "盘点数据不存在！",
									Toast.LENGTH_SHORT).show();
						}

						/*
						 * if (InventoryFragment.this.delFile() == "success") {
						 * Toast.makeText(getActivity(), "数据删除成功",
						 * Toast.LENGTH_SHORT).show(); } else {
						 * Toast.makeText(getActivity(), "数据不存在！",
						 * Toast.LENGTH_SHORT).show(); }
						 */

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).create();
		dialog.show();
	}

	/*
	*//**
	 * Create a thread to upload the file.
	 * 
	 * @param filepath
	 * @param uploadUrl
	 */
	/*
	 * public void startUploadFile(final String filepath, final String
	 * uploadUrl) {
	 * 
	 * final Message msg = Message.obtain(); // Get the Message object // Create
	 * a new thread to do the upload Thread thread = new Thread(new Runnable() {
	 * 
	 * @Override public void run() { // TODO Auto-generated method stub boolean
	 * flag = uploadFile(filepath, uploadUrl); // Call the // upload file //
	 * function
	 * 
	 * System.out.println("^^^^^^^^^^^^^^^^^^" + flag);
	 * 
	 * if (flag) { msg.what = 1; // Upload file succeeded. } else { msg.what =
	 * 0; // Upload file failed. } handler1.sendMessage(msg); } }); }
	 */

	/**
	 * Upload the specified file to remote server.
	 * 
	 * @param filepath
	 *            The path of the local file.
	 * @param uploadUrl
	 *            The server url.
	 * @return The upload status.
	 */
	/*
	 * public boolean uploadFile(String filepath, String uploadUrl) { boolean
	 * status = true;
	 * 
	 * String end = "\r\n"; String twoHyphens = "--"; String boundary =
	 * "******"; try { URL url = new URL(uploadUrl); HttpURLConnection
	 * httpURLConnection = (HttpURLConnection) url .openConnection();
	 * 
	 * // Set the size of the transfer stream, in case that the application //
	 * collapses due to small memory, this method is used when we don't // know
	 * the size of the content, we use HTTP request without cache
	 * httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
	 * 
	 * // Set the input and output httpURLConnection.setDoInput(true);
	 * httpURLConnection.setDoOutput(true);
	 * httpURLConnection.setUseCaches(false);
	 * 
	 * // Set the HTTP method httpURLConnection.setRequestMethod("POST");
	 * httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
	 * httpURLConnection.setRequestProperty("Charset", "UTF-8");
	 * httpURLConnection.setRequestProperty("Content-Type",
	 * "multipart/form-data;boundary=" + boundary);
	 * 
	 * // Get outputstream according to the url connection DataOutputStream dos
	 * = new DataOutputStream( httpURLConnection.getOutputStream());
	 * 
	 * // Write the HTTP POST header dos.writeBytes(twoHyphens + boundary +
	 * end); dos.writeBytes(
	 * "Content-Disposition: form-data; name=\"uploadedfile\"; filename=\"" +
	 * filepath.substring(filepath.lastIndexOf("/") + 1) + "\"" + end);
	 * dos.writeBytes(end);
	 * 
	 * FileInputStream fis = new FileInputStream(filepath);
	 * 
	 * int bufferSize = 8 * 1024; // The size of the buffer, 8KB. byte[] buffer
	 * = new byte[bufferSize]; int length = 0;
	 * 
	 * while ((length = fis.read(buffer)) != -1) {
	 * 
	 * // Write data to DataOutputStream dos.write(buffer, 0, length); }
	 * 
	 * dos.writeBytes(end); dos.writeBytes(twoHyphens + boundary + twoHyphens +
	 * end);
	 * 
	 * fis.close(); // Close the FileInputStream. dos.flush(); // Flush the data
	 * to DataOutputStream.
	 * 
	 * // Get the content of the response InputStream is =
	 * httpURLConnection.getInputStream();
	 * 
	 * InputStreamReader isr = new InputStreamReader(is, "utf-8");
	 * BufferedReader br = new BufferedReader(isr, 8 * 1024); String result =
	 * br.readLine();
	 * 
	 * // Log.d(Tag, result);
	 * 
	 * // dos.close(); // Will respond I/O exception if closes. } catch
	 * (Exception e) { e.printStackTrace(); status = true; } return status; }
	 * 
	 * public String DoloadFile(String Fileurl) { // NetOperator netOperator =
	 * new NetOperator(); int barcount = 0; int contentLength = 0; int PB_Count
	 * = 0;
	 * 
	 * File file = new File(MetaStaticData.SDCARD_PATH); //
	 * 如果目标文件已经存在，则删除。产生覆盖旧文件的效果 if (!file.exists()) { file.mkdirs(); } String
	 * path = MetaStaticData.SDCARD_PATH + "pd.db"; // path += "pd.db"; file =
	 * new File(path);
	 * 
	 * if (file.exists()) { file.delete(); } try { // 构造URL URL url = new
	 * URL(Fileurl);
	 * 
	 * // 打开连接 URLConnection con = url.openConnection(); // 获得文件的长度
	 * contentLength = con.getContentLength(); System.out.println("数据长度 :" +
	 * contentLength); // 输入流 InputStream is = con.getInputStream(); // 1K的数据缓冲
	 * byte[] bs = new byte[1024];
	 * 
	 * PB_Count = (int) (contentLength / 1024) + 1;
	 * 
	 * System.out.println("PB_Count" + PB_Count); //
	 * MainActivity.this.bar.setMax(PB_Count); // 读取到的数据长度 int len; // 输出的文件流
	 * OutputStream os = new FileOutputStream(path); // 开始读取 while ((len =
	 * is.read(bs)) != -1) { os.write(bs, 0, len); //
	 * publishProgress(barcount++); // if (isCancelled()) break; }
	 * 
	 * // 完毕，关闭所有链接 os.close(); is.close();
	 * 
	 * netOperator.operator(); if(barcount<PB_Count) {
	 * publishProgress(barcount); barcount++; }
	 * 
	 * 
	 * } catch (Exception e) { e.printStackTrace(); return "defeat"; } // for (i
	 * = 10; i <= 100; i+=10) { // // //publishProgress(i); // } return
	 * "success"; }
	 */

	private String delFile() {
		String path = MetaStaticData.SDCARD_PATH + "pd.db";
		File file = new File(path);
		if (!file.exists()) {
			return "NotFile!";
		}

		file.delete();
		return "success";

	}

	private String selectFile() {
		String path = MetaStaticData.SDCARD_PATH
				+ MetaStaticData.DATABASE_PDNAME;
		File file = new File(path);
		if (file.exists()) {

			return "success";
		}

		return "NotFile!";

	}
	private void noFileDialog() {
		Dialog dialog = new AlertDialog.Builder(getActivity())
				.setIcon(R.drawable.exit_log_out).setTitle("警告")
				.setMessage("盘点数据不存在！请下载！")
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
				//.setMessage("您确定要导出盘点数据吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String id = txt.getText().toString().trim(); 
						if(id.length()>0){
						InventoryFragment.this
								.copyFile(id,MetaStaticData.DATABASE_PDNAME);
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
	
	 * @param string
	 *            用户编号
	 * @return
	 */
	public static String GetUpString( String string) {
		String string2 = "pd";
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
		String path = MetaStaticData.SDCARD_EXPORTPATH + databasename;
		file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		try {
			in = new FileInputStream(MetaStaticData.SDCARD_PATH + databasename);

			String s = InventoryFragment.this.GetUpString(id);
			
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
			Toast.makeText(getActivity(), "盘点数据导出失败", Toast.LENGTH_SHORT)
					.show();
		} catch (IOException e1) {
			System.out.println("文件复制错误");
			Toast.makeText(getActivity(), "盘点数据导出失败", Toast.LENGTH_SHORT)
					.show();
		}
		Toast.makeText(getActivity(), "盘点数据导出成功", Toast.LENGTH_SHORT).show();
		System.out.println("文件已复制");
	}
	
	private void fileRode(){
	File file = new File(MetaStaticData.SDCARD_PATH);
	
	if (!file.exists()) {
		file.mkdirs();
	}
	}

}
