import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class urlAcc {
	@PrimaryKey
	public String url;

	public double acc; 
	public urlAcc(String page, double num){
		url = page;
		acc = num;
	}
	
	public urlAcc(){};
	}

