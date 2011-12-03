package mo.dev.newscues;

import java.util.ArrayList;
import java.util.List;

import mo.dev.newscues.model.Article;
import mo.dev.newscues.service.PullDataAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class NewsCuesActivity extends Activity {
	
	private TextView textView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textView = (TextView) findViewById(R.id.text);
    }
    
    private class GetFeedTask extends PullDataAsyncTask {
		@Override
		protected void onPostExecute(String result) {
			List<Article> articles = new ArrayList<Article>();
			try {
				JSONArray jsonArticles = new JSONArray(result);
				for (int i = 0; i < jsonArticles.length(); i++) {
					articles.add(Article.fromJSON(jsonArticles.getJSONObject(i)));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			StringBuilder text = new StringBuilder();
			for (Article article : articles) {
				text.append(article.toString());
			}
			textView.setText(text);
		}
	}
    
    public void readFeed(View view) {
    	GetFeedTask task = new GetFeedTask();
		task.execute(new String[] { "http://www.uniquestyledrives.com/hack/index1.php" });
	}
}