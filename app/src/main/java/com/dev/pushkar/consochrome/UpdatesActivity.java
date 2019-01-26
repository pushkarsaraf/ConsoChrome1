package com.dev.pushkar.consochrome;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.dev.pushkar.consochrome.R;


public class UpdatesActivity extends Activity {

	private TextView content;
	private TextView header;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updates);
		content = (TextView) findViewById(R.id.updates_content);
		header = (TextView)findViewById(R.id.updates_head);
		header.setTypeface(Data.appTypeface, Typeface.BOLD);
		content.setTypeface(Data.appTypeface);
	}
	


	public void onBackPressed() {
        super.onBackPressed();
        this.finish();
}
}
