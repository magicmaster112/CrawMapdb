import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentNavigableMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;


public class Mapdb {
	 public static void main(String[] args) throws IOException {

	        //Configure and open database using builder pattern.
	        //All options are available with code auto-completion.
	        File dbFile = File.createTempFile("mapdb","db");
	        DB db = DBMaker.newFileDB(dbFile)
	                .closeOnJvmShutdown()
	                .encryptionEnable("password")
	                .make();

	        //open an collection, TreeMap has better performance then HashMap
	        ConcurrentNavigableMap<Integer,String> map = db.getTreeMap("collectionName");

	        map.put(1,"one");
	        map.put(2,"two");
	        //map.keySet() is now [1,2] even before commit

	        db.commit(); //persist changes into disk

	        map.put(3,"three");
	        //map.keySet() is now [1,2,3]
	        db.rollback(); //revert recent changes
	        //map.keySet() is now [1,2]

	        db.close();

	    }
//	 System.out
//		.println(dbTest
//				.getTreeMap("collectionName").get("http://tintuc.wada.vn/e/3966366/Apple-co-the-lan-san-sang-linh-vuc-xe-hoi-va-thiet-bi-y-te"));
}
