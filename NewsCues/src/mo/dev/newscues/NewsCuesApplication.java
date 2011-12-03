package mo.dev.newscues;

import java.util.ArrayList;
import java.util.List;

import mo.dev.newscues.model.Article;

import android.app.Application;

public class NewsCuesApplication extends Application {
	public static final String TAG = "NewsCues";
	
	public static final List<Article> ARTICLES = new ArrayList<Article>(); 
	
	public static final List<Article> getArticles() {
		return ARTICLES;
	}
	
	public static final Article getArticle(int index) {
		return ARTICLES.get(index);
	}
}
