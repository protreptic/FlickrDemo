package skbkonturcontest.flickr.activity;

import skbkonturcontest.flickr.R;
import skbkonturcontest.flickr.storage.Storage;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class LauncherActivity extends ActionBarActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.launcher_activity);
		
		new PrepareStorage().execute();
	}
	
	public class PrepareStorage extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Storage.dropStorage(getApplicationContext());
			Storage.initialize(getApplicationContext()); 
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Intent intent = new Intent(getBaseContext(), MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
			
			startActivity(intent); 
		}
		
	}
	
}
