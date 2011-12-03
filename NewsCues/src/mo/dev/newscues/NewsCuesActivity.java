package mo.dev.newscues;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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
    
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				DefaultHttpClient client = new DefaultHttpClient();
				Log.d(NewsCuesApplication.TAG, "getting " + url);
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse execute = client.execute(httpGet);
					Log.d(NewsCuesApplication.TAG, "Response code: " + execute.getStatusLine().getStatusCode());
					InputStream content = execute.getEntity().getContent();

					BufferedReader buffer = new BufferedReader(
							new InputStreamReader(content));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						response += s;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			textView.setText(result);
		}
	}
    
    public void readFeed(View view) {
		DownloadWebPageTask task = new DownloadWebPageTask();
		task.execute(new String[] { "http://www.uniquestyledrives.com/hack/index.php" });

	}
    
    public void readArticles(View view) {
		DownloadWebPageTask task = new DownloadWebPageTask();
		task.execute("http://api.usatoday.com/open/articles/mobile/topnews?api_key=" + ApiKeys.USA_TODAY_ARTICLES);

	}
}