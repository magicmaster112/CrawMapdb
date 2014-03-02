import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentNavigableMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.exceptions.JedisConnectionException;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import com.google.gson.Gson;

public class Crawler {
	private static Gson gson = new Gson();
	private int depthLimit;
	private static int count = 0;
	private static int p = 0;
	private static File dbFile = new File("D://Data.db");
	private static DB db = DBMaker.newFileDB(dbFile).closeOnJvmShutdown()
			.encryptionEnable("password").make();

	ConcurrentNavigableMap<String, String> map = db
			.getTreeMap("collectionName");

	Queue<MyUrl> links = new LinkedList<MyUrl>();

	// main
	public static void main(String[] args) throws IOException {

		// open an collection, TreeMap has better performance then HashMap

		String orgUrl = null;
		int depth = 100;
		Crawler mCrawler = new Crawler();

		orgUrl = "http://tintuc.wada.vn/khoa-hoc-cong-nghe/?p=" + p;
		mCrawler.startCrawl(orgUrl, depth, 100);

		db.commit();
		db.close();

		// DB dbTest = DBMaker.newFileDB(dbFile).closeOnJvmShutdown()
		// .encryptionEnable("password").make();

		// System.out
		// .println(dbTest
		// .getTreeMap("collectionName").get("http://tintuc.wada.vn/e/3966366/Apple-co-the-lan-san-sang-linh-vuc-xe-hoi-va-thiet-bi-y-te"));

		System.out.println("finish!");
	}

	// startCrawl
	public void startCrawl(String url, int depth, int nitem_limit) {

		System.out.println("Craw: url=" + url + " depth: " + depth);

		depthLimit = depth;
		MyUrl currentUrl = new MyUrl(url, 0);

		PageData pageData;
		int nItem = 0;

		while (currentUrl != null) {
			System.out.println("url=" + currentUrl.url + " depth="
					+ currentUrl.depth);
			pageData = getContent(currentUrl);

			if (currentUrl.url.contains("//tintuc.wada.vn/e")) {
				System.out.println(pageData.getDateTime());
				storePageData(pageData);
				nItem++;
			}

			else {
				if (pageData.getDepth() < depthLimit) {
					for (String link : pageData.getLinks()) {
						addLink(new MyUrl(link, pageData.getDepth() + 1));
					}
				}
			}
			currentUrl = getNextLink();
			if (currentUrl.url.contains("?copies=1")) {
				currentUrl = getNextLink();
			}

		}
	}

	private void storePageData(PageData pageData) {
		// TODO Auto-generated method stub
		map.put(pageData.getUrl(),
				pageData.getDateTime() + " " + pageData.getContent());
		count++;
		System.out.println(count);
	}

	private MyUrl getNextLink() {
		// TODO Auto-generated method stub
		MyUrl url = null;
		// do {
		url = links.poll();
		if (url == null) {
			p = p + 10;
			url = new MyUrl("http://tintuc.wada.vn/khoa-hoc-cong-nghe/?p=" + p,
					0);
			return url;
		}
		return url;
		// } while (links.isEmpty());
	}

	public PageData getContent(MyUrl curUrl) {
		String url = curUrl.url;
		int depth = curUrl.depth;
		PageData data = new PageData();
		data.setUrl(url);
		data.setDepth(depth);
		ArrayList<String> links = new ArrayList<String>();
		try {
			Document doc = Jsoup.connect(url).get();
			if (url.contains("http://tintuc.wada.vn/e")) {
				Elements eTitles = doc
						.getElementsByClass("b-wasen_newselement-header");
				data.setTitle(eTitles.get(0).text());

				Elements eContents = doc
						.getElementsByClass("b-wasen_newselement-content");
				data.setContent(eContents.get(0).text());

				Elements eTime = doc.getElementsByClass(
						"b-wasen_newselement-pub").select("time");
				data.setDateTime(eTime.attr("datetime"));

			} else {
				Elements eLinks = doc.getElementsByClass("b-wres").select(
						"a[href~=//tintuc.wada.vn/e/");
				for (Element e : eLinks) {
					String link = e.attr("href");
					links.add("http:" + link);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Page " + url + "error when parsing content!");
			data.setContent("Content Format Error!");
		}

		data.setLinks(links);
		return data;
	}

	public void addLink(MyUrl url) {
		links.add(url);
	}
}
