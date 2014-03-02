import java.util.ArrayList;

public class PageData {

	private String url;
	private int depth;
	private String title;
	private String content;
	private ArrayList<String> links;
	private String datetime;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ArrayList<String> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<String> links) {
		this.links = links;
	}

	public String getDateTime() {
		return datetime;
	}

	public void setDateTime(String time) {
		this.datetime = time;
	}

}
