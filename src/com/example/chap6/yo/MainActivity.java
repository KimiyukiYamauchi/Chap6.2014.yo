package com.example.chap6.yo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity 
	implements OnClickListener
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		LinearLayout layout = new LinearLayout(this);
		layout.setBackgroundColor(Color.WHITE);
		layout.setOrientation(LinearLayout.VERTICAL);
		setContentView(layout);
		
		int wc = LinearLayout.LayoutParams.WRAP_CONTENT;

		Button btnFile = new Button(this);
		btnFile.setText("Write");
		btnFile.setTag("W");
		btnFile.setLayoutParams(new LinearLayout.LayoutParams(wc, wc));
		btnFile.setOnClickListener(this);
		layout.addView(btnFile);

		Button btnFile2 = new Button(this);
		btnFile2.setText("Read");
		btnFile2.setTag("R");
		btnFile2.setLayoutParams(new LinearLayout.LayoutParams(wc, wc));
		btnFile2.setOnClickListener(this);
		layout.addView(btnFile2);
		
		EditText et = new EditText(this);
		et.setText("???");
		et.setTag("et");
		layout.addView(et);
		
		TextView tv = new TextView(this);
		tv.setText("???");
		tv.setTag("tv");
		layout.addView(tv);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		if((String)v.getTag() == "W"){
			View parent = (View)v.getParent();
			EditText et = (EditText)parent.findViewWithTag("et");
			Log.v("onClick", et.getText().toString());
			TextView tv = (TextView)parent.findViewWithTag("tv");
			try {
//				writeToFile(this, et.getText().toString() + "\n");
//				writeToTable(et.getText().toString(), false);
				SharedPreferences prefs
					= getSharedPreferences("myprefs", MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("key", et.getText().toString());
				editor.commit();
			} catch (Exception e) {
				tv.setText("ERROR:" + e.getMessage());
			}
		}else if((String)v.getTag() == "R"){
			View parent = (View)v.getParent();
			TextView tv =  (TextView)parent.findViewWithTag("tv");
			try {
//				tv.setText(readFromFile(this));
//				tv.setText(readFromTable());
				SharedPreferences prefs
					= getSharedPreferences("myprefs", MODE_PRIVATE);
				String s = prefs.getString("key", "");
				tv.setText(s);
			} catch (Exception e) {
				tv.setText("ERROR:" + e.getMessage());
			}
		}
		
	}
	
	private void writeToFile(Context e, String s) throws Exception{
		byte[] data = s.getBytes();
		OutputStream stream = null;
		try {
			stream = e.openFileOutput
						("test.txt", Context.MODE_PRIVATE | Context.MODE_APPEND);
			stream.write(data);
			stream.close();
		} catch (Exception e1) {
			if(stream != null){
				try {
					stream.close();
				} catch (IOException e2) {
					throw e2;
				}
			}
		}
	}
	
	private String readFromFile(Context c) throws Exception{
		byte [] data = new byte[100];
		InputStream stream = null;
		ByteArrayOutputStream stream2 = null;
		try {
			stream = c.openFileInput("test.txt");
			
			stream2 = new ByteArrayOutputStream();
			
			int size = stream.read(data);
			while(size>0){
				stream2.write(data, 0, size);
				size = stream.read(data);
			}
			stream2.close();
			stream.close();
			
			String s = new String(stream2.toByteArray());
			return s;

		} catch (Exception e) {
			if(stream != null){
				try {
					stream.close();
				} catch (IOException e1) {
					throw e1;
				}
			}
		}
		return "";
		
	}
	
	private void writeToTable(String data, boolean overwrite){
		ContentValues vals = new ContentValues();
		vals.put("data", data);
		String whereClause = "id = 1";
		String [] whereArgs = null;
		
		DBHelper dbh = new DBHelper(this);
		SQLiteDatabase db = dbh.getWritableDatabase();
		if(overwrite){
			db.update(DBHelper.TABLENAME, vals, whereClause, whereArgs);
		}else{
			db.insert(DBHelper.TABLENAME, "", vals);
		}
	}
	
	private String readFromTable(){
		String [] columns = {"id", "data"};
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;
		
		StringBuffer sb = new StringBuffer();
		Cursor cur = null;
		
		DBHelper dbh = new DBHelper(this);
		SQLiteDatabase db = dbh.getReadableDatabase();
		cur = db.query(DBHelper.TABLENAME, columns, selection, 
				selectionArgs, groupBy, having, orderBy);
		while(cur.moveToNext()){
			sb.append(cur.getInt(0)+","+cur.getString(1)+"\n");
		}
		return sb.toString();
	}
}
