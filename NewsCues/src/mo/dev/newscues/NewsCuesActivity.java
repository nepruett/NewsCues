package mo.dev.newscues;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import mo.dev.newscues.model.Article;
import mo.dev.newscues.service.PullDataAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsCuesActivity extends Activity {
	
	private List<Article> articles = NewsCuesApplication.getArticles();
	private TextView textView;
	private Gallery gallery;
	private ImageAdapter imageAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textView = (TextView) findViewById(R.id.text);
        gallery = (Gallery)findViewById(R.id.gallery);
        imageAdapter = new ImageAdapter(this);
        gallery.setAdapter(imageAdapter);
    }
    
    private class GetFeedTask extends PullDataAsyncTask {
		@Override
		protected void onPostExecute(String result) {
			articles.clear();
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
    
    public class ImageAdapter extends BaseAdapter {

		private Context ctx;

		public ImageAdapter(Context c) {
			ctx = c; 
		}

		@Override
		public int getCount() {

			return articles.size();
		}

		@Override
		public Object getItem(int arg0) {

			return arg0;
		}

		@Override
		public long getItemId(int arg0) {

			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {

			ImageView iView = new ImageView(ctx);
			iView.setImageBitmap(loadBitmap(articles.get(arg0).getImages().get(0)));
			iView.setScaleType(ImageView.ScaleType.FIT_XY);
			iView.setLayoutParams(new Gallery.LayoutParams(150, 150));
			return iView;
		}
		
		public Bitmap loadBitmap(String url) {
		    Bitmap bitmap = null;
		    InputStream in = null;
		    BufferedOutputStream out = null;

		    try {
		        in = new BufferedInputStream(new URL(url).openStream(), 1024);

		        final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
		        out = new BufferedOutputStream(dataStream, 1024);
		        byte[] buffer = new byte[1024];
		        int result = in.read(buffer);
		        while (result > -1) {
		        	out.write(buffer);
		        	result = in.read(buffer);
		        }
		        out.flush();

		        final byte[] data = dataStream.toByteArray();
		        BitmapFactory.Options options = new BitmapFactory.Options();
		        //options.inSampleSize = 1;

		        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);
		    } catch (IOException e) {
		        Log.e(NewsCuesApplication.TAG, "Could not load Bitmap from: " + url);
		    } finally {
		    	try {
		    		in.close();
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }

		    return bitmap;
		}

	}
}