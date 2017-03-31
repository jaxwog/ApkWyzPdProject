package com.zzu.wyz;

import java.io.File;

import com.zzu.wyz.util.MetaStaticData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WarehouseActivity extends Activity {

	private Button startBut;
private EditText pd_shelf;
private String siteno,resultstr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.pd_pdcelect);
		
		resultstr = WarehouseActivity.this.selectFile();
		
		pd_shelf = (EditText) super.findViewById(R.id.pd_shelf);
		startBut = (Button) super.findViewById(R.id.pd_start_scan);
		startBut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				siteno = pd_shelf.getText().toString().trim();
//System.out.println("******************"+siteno);				
				if(siteno.length()==0){

					Toast.makeText(WarehouseActivity.this, "请输入货架号！", Toast.LENGTH_SHORT).show();
				}
				/*else if (resultstr.equals("NotFile!")) {
					Toast.makeText(WarehouseActivity.this, "请下载数据库！",
							Toast.LENGTH_SHORT).show();
				}*/
				else{
				Intent _intent = new Intent(WarehouseActivity.this,
						InventoryActivity.class);
				_intent.putExtra("siteno", siteno);
				startActivity(_intent);
				}
			}
		});
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
}
