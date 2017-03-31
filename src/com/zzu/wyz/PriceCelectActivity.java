package com.zzu.wyz;

import java.io.File;

import com.zzu.wyz.util.MetaStaticData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PriceCelectActivity extends Activity {

	private Button hjBut;
	private EditText pic_shelf;
	private String str,resultstr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.pic_celect);
		
	

		hjBut = (Button) super.findViewById(R.id.pic_start_scan);
		pic_shelf = (EditText) super.findViewById(R.id.pic_shelf);

		hjBut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				str = pic_shelf.getText().toString().trim();
				 //resultstr = PriceCelectActivity.this.selectFile();

				if (str.length() == 0) {
					Toast.makeText(PriceCelectActivity.this, "请输入货架号！",
							Toast.LENGTH_SHORT).show();
				/*}

				else if (resultstr.equals("NotFile!")) {
					Toast.makeText(PriceCelectActivity.this, "请下载数据库！",
							Toast.LENGTH_SHORT).show();*/
				} else {
					Intent it = new Intent(PriceCelectActivity.this,
							PriceActivity.class);
					it.putExtra("hjExtra", str);
					startActivity(it);
				}
			}
		});
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
}
