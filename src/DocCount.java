import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Scanner;

public class DocCount {
	static int total = 0;
	static String file = "";
	static LinkedHashSet<String> documents = new LinkedHashSet<String>(100);
	static HashMap<String, Integer> myQuery = new HashMap<String, Integer>(10);
	static HashMap<String, Double> vqvd = new HashMap<String, Double>(10);
	static ArrayList<urlRank> finalList = new ArrayList<urlRank>();
	static int corpSize;
	static DBsetup db = new DBsetup();

	@SuppressWarnings("resource")
	public static void input() throws IOException {

		Scanner input = new Scanner(System.in);
		System.out.print("Enter file path: ");
		String text = input.nextLine();
		file = text;
		BufferedReader fr = null;
		String fline = "";
		fr = new BufferedReader(new FileReader("info.txt"));
		String lastFile = fr.readLine();
		System.out.println(lastFile);
		int lastSize = Integer.parseInt(fr.readLine());
		System.out.println(lastSize);
		if (text.equals(lastFile)) {
			file = lastFile;
			corpSize = lastSize;
			return;
		} else {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("info.txt")));

			writer.write(file+"\n");
			BufferedReader br = null;
			String line = "";

			br = new BufferedReader(new FileReader(text));
			while ((line = br.readLine()) != null) {

				String[] temp = line.toString().split("\t");
				String docs = temp[1];
				String[] allDocs = docs.split(",");
				for (String s : allDocs) {
					if (s.contains("http")) {
						s = s.replace("[", "");
						if (documents.contains(s) == false) {
							documents.add(s);
							db.urlAccTable.put(s, 0);
						}
					}
				}
			}

			corpSize = documents.size();
			writer.write(corpSize+"\n");
			br.close();
			writer.close();
		}

	}

	public static void vectScore() {
		BufferedReader br = null;
		String line = "";
		try {

			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				String[] temp = line.toString().split("\t");
				String term = temp[0];
				String docs = temp[1];

				String[] docCountPair = docs.split("],");
				double docFreq = docCountPair.length;

				for (String s : docCountPair) {
					String[] pair = s.split(",");
					String url = pair[0];
					String rawtf = pair[1];
					url = url.replace("[", "");
					rawtf = rawtf.replace("]", "");
					rawtf = rawtf.substring(1, rawtf.length() - 1);
					double wtf = 1 + Math.log10((double) Integer
							.parseInt(rawtf));
					double idf = Math.log10(corpSize / docFreq);
					double wtfidf = Math.pow(wtf * idf, 2);
					db.urlAccTable.inc(url, wtfidf);

				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void query() {
		while (true) {
			Scanner input = new Scanner(System.in);
			String[] q;
			System.out.print("Enter query, or type '*quit' to exit: ");
			String text = input.nextLine();
			if (text.equals("*quit"))
				break;
			q = text.split(" ");
			for (String s : q) {
				if (myQuery.containsKey(s) == false) {
					myQuery.put(s, 1);
				} else {
					Integer val = myQuery.get(s);
					myQuery.put(s, val + 1);
				}
			}

			BufferedReader br = null;
			String line = "";
			try {

				br = new BufferedReader(new FileReader(file));
				while ((line = br.readLine()) != null) {

					String[] temp = line.toString().split("\t");
					String term = temp[0];
					for (String st : myQuery.keySet()) {
						if (st.equals(term)) {

							String docs = temp[1];
							String[] docCountPair = docs.split("],");
							double docFreq = docCountPair.length + 1;
							for (String s : docCountPair) {
								String[] pair = s.split(",");
								String url = pair[0];
								String rawtf = pair[1];
								url = url.replace("[", "");
								rawtf = rawtf.replace("]", "");
								rawtf = rawtf.substring(1, rawtf.length() - 1);
								double qwtf = 1 + Math.log10(myQuery.get(st));
								double wtf = 1 + Math.log10((double) Integer
										.parseInt(rawtf));
								double idf = Math.log10((corpSize + 1)
										/ docFreq);
								double wtfidf = wtf * idf * qwtf * idf;
								if (vqvd.containsKey(url)) {
									vqvd.put(url, vqvd.get(url) + wtfidf);
								} else
									vqvd.put(url, wtfidf);

							}
						}
					}
				}
				for (String st : vqvd.keySet()) {
					double denom = db.urlAccTable.get(st).acc;
					double numer = vqvd.get(st);
					double cosSim = numer / Math.sqrt(denom);
					finalList.add(new urlRank(st, cosSim));
				}
				Collections.sort(finalList);
				int i = 0;
				for (urlRank r : finalList) {
					System.out.println(r.url + "\t" + r.score);
					i++;
					if (i == 5)
						break;

				}
				myQuery.clear();
				vqvd.clear();
				finalList.clear();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	public static void main(String[] args) throws IOException {
		db.setup();
		input();
		vectScore();
		query();
		db.shutdown();

	}
}