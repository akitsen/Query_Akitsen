
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

public class DBsetup {
		public Environment myEnvironment;
		public EntityStore myStore;
		public urlAccDA urlAccTable;
		
public DBsetup(){}		

public void whatsInTheDB() {

	EntityCursor<urlAcc> urlAccs = null;
	try {
		urlAccs = urlAccTable.getAll();
		for (urlAcc ua : urlAccs) {
			System.out.println("url: " + ua.url);
			System.out.println("Acc: "+ua.acc);
		}
		
	} finally {
		if (urlAccs != null) {
			urlAccs.close();
		}
	}
}

//public void printData() {
//
//	EntityCursor<urlAcc> urls = urlAccTable.getAll();
//	
//	int wordTotal = 0, documentTotal = 0;
//	
//	for (urlMapper url : documents) {
//		documentTotal++;
//	}
//	for (termMapper term : terms) {
//		wordTotal++;
//	}
//	
//	System.out.println("The total amount of terms in the Gutenberg Database is: " + wordTotal);
//	System.out.println("The total amount of documents in the Gutenberg Database is: " + documentTotal);
//	
//	
//	documents.close();
//	terms.close();
//}

public void setup() {
			EnvironmentConfig eConfig = new EnvironmentConfig();
			eConfig.setAllowCreate(true);

			File dbd = new File("databaseDirectory");
			
			
			if (!dbd.exists()) { dbd.mkdir(); }
			
			myEnvironment = new Environment(dbd, eConfig);
			
			StoreConfig sConfig = new StoreConfig();
			sConfig.setAllowCreate(true);

			myStore = new EntityStore(myEnvironment, "EntityStore", sConfig);

			urlAccTable = new urlAccDA(myStore);
		}

	public	void shutdown() {

			if (myStore != null) {
				myStore.close();
			}

			if (myEnvironment != null) {
				myEnvironment.cleanLog();
				myEnvironment.close();
			}

		}

	}

