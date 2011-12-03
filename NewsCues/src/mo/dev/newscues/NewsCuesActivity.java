package mo.dev.newscues;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import mo.dev.newscues.service.PullDataAsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class NewsCuesActivity extends Activity {
	private TextView textView;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textView = (TextView) findViewById(R.id.text);
    }
    
    private class DownloadWebPageTask extends PullDataAsyncTask {
		@Override
		protected void onPostExecute(String result) {
			textView.setText(result);
		}
	}
    
    public void readFeed(View view) {
		DownloadWebPageTask task = new DownloadWebPageTask();
		task.execute(new String[] { "http://www.uniquestyledrives.com/hack/index1.php" });

	}
}