
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class urlAccDA {
	public PrimaryIndex<String, urlAcc> pIndex;
	public urlAccDA(EntityStore entity) {

		pIndex = entity.getPrimaryIndex(String.class, urlAcc.class);
	}

	public void put(String url, double comp) {
		urlAcc uA= new urlAcc(url, comp); 
		pIndex.put(uA);
		}

	public urlAcc get(String key) {
		return pIndex.get(key);
	}

	public void inc(String url, double num){
		urlAcc current = get(url);
		double cacc = current.acc + num;
		put(url, cacc);
		
	}
	public EntityCursor<urlAcc> getAll() {
		return pIndex.entities();
	}

	public void delete(String key) {
		pIndex.delete(key);
	}
}

