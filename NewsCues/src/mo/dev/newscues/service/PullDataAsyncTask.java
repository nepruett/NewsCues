package mo.dev.newscues.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import mo.dev.newscues.NewsCuesApplication;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class PullDataAsyncTask extends AsyncTask<String, Void, String> {
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
}
