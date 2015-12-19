
public class urlRank implements Comparable{

	public String url;
	public Double score;
	public urlRank(String s, double n){
		url = s;
		score = n;
	}
	public int compareTo(Object c) {
		urlRank e = (urlRank) c;

		int result = this.score.compareTo(e.score);

		if (result > 0) {
			return -1;
		} else if (result == 0) {
			return 0;
		} else {
			return 1;
		}
	}
}
	  