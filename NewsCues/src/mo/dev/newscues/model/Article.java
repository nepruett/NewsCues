package mo.dev.newscues.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Article {
	private String title;
	private String link;
	private String description;
	private List<String> imageUrls = new ArrayList<String>();
	
	public static final Article fromJSON(JSONObject json) throws JSONException {
		Article article = new Article();
		article.title = json.optString("title");
		article.link = json.optString("link");
		article.description = json.optString("description");
		JSONArray images = json.getJSONArray("image");
		for (int i = 0; i < images.length(); i++) {
			article.addImage(images.getString(i));
		}
		return article;
	}
	
	public List<String> getImages() {
		return imageUrls;
	}
	
	public void addImage(String imageUrl) {
		imageUrls.add(imageUrl);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Article:\n");
		sb.append("\tTitle: " + title + "\n");
		sb.append("\tDesc:  " + description + "\n");
		sb.append("\tLink:  " + link + "\n");
		sb.append("\tImages:\n");
		for (String imageURL : imageUrls) {
			sb.append("\t\t" + imageURL + "\n");
		}
		return sb.toString();
	}
}
