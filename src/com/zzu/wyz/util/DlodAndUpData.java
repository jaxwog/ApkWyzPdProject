package com.zzu.wyz.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

public class DlodAndUpData {
	public String DoloadFile(String Fileurl, String dataBaseName) {
		// NetOperator netOperator = new NetOperator();
		int barcount = 0;
		int contentLength = 0;
		int PB_Count = 0;
		Log.e("sqlError","num1"+Fileurl);
		Log.e("sqlError","num1"+MetaStaticData.SDCARD_PATH+"::::"+dataBaseName);
		File file = new File(MetaStaticData.SDCARD_PATH); 
		// 如果目标文件已经存在，则删除。产生覆盖旧文件的效果
		if (!file.exists()) {
			file.mkdirs();
		}
		String path = MetaStaticData.SDCARD_PATH + dataBaseName;
		// path += "pd.db";
		file = new File(path);

		if (file.exists()) {
			file.delete();
		}
		try {
			// 构造URL
			URL url = new URL(Fileurl);
			Log.e("SqlError","num2"+Fileurl);
			// 打开连接
			URLConnection con = url.openConnection();
			// 获得文件的长度
			contentLength = con.getContentLength();
			System.out.println("数据长度 :" + contentLength);
			// 输入流
			InputStream is = con.getInputStream();
			// 1K的数据缓冲
			byte[] bs = new byte[1024];

			PB_Count = (int) (contentLength / 1024) + 1;

			System.out.println("PB_Count" + PB_Count);
		
			// 读取到的数据长度
			int len;
			// 输出的文件流
			OutputStream os = new FileOutputStream(path);
			// 开始读取
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
				// publishProgress(barcount++);
				// if (isCancelled()) break;
			}

			// 完毕，关闭所有链接
			os.close();
			is.close();
			
			 // netOperator.operator(); if(barcount<PB_Count) {
			  //publishProgress(barcount); barcount++; }
			 

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
		try {
			URL url = new URL(uploadUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();

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
					+ filepath.substring(filepath.lastIndexOf("/") + 1)
					+ "\""
					+ end);
			dos.writeBytes(end);

			FileInputStream fis = new FileInputStream(filepath);

			int bufferSize = 8 * 1024; // The size of the buffer, 8KB.
			byte[] buffer = new byte[bufferSize];
			int length = 0;

			while ((length = fis.read(buffer)) != -1) {

				// Write data to DataOutputStream
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
			status = true;
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
}
