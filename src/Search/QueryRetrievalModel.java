package Search;

import java.io.IOException;
import java.util.*;

import Classes.Query;
import Classes.Document;
import IndexingLucene.MyIndexReader;

public class QueryRetrievalModel {

	protected MyIndexReader indexReader;

	protected long totalLength;

	protected int mu = 2000;

	public QueryRetrievalModel(MyIndexReader indexReader) {
		this.indexReader = indexReader;
		totalLength = this.indexReader.totalLength();
	}

	/**
	 * Search for the topic information.
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * TopN specifies the maximum number of results to be returned.
	 *
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @return
	 */

	public List<Document> retrieveQuery( Query aQuery, int TopN ) throws IOException {
		String[] tokens = aQuery.GetQueryContent().split(" ");
		List<Document> documentArrayList = new ArrayList<>();
		HashMap<Integer,Document> documentHashMap = new HashMap<>();


		PriorityQueue<Document> queue = new PriorityQueue<>(TopN, new Comparator<Document>() {
			@Override
			public int compare(Document document, Document document1) {
				if(document.score()>document1.score()){
					return -1;
				}else {
					return 1;
				}
			}
		});


		for(String x: tokens){
			int[][] postingList = this.indexReader.getPostingList(x);
			if(postingList == null){
				continue;
			}
			for(int[] post:postingList){
				double prob = (post[1]+(double)this.mu *this.indexReader.CollectionFreq(x)/this.totalLength)/(this.indexReader.docLength(post[0])+(double)this.mu);
				if(!documentHashMap.containsKey(post[0])){
					Document document = new Document(Integer.toString(post[0]),this.indexReader.getDocno(post[0]),prob);
					documentHashMap.put(post[0],document);
				}else {
					Document document = documentHashMap.get(post[0]);
					document.setScore(document.score()*prob);
					documentHashMap.put(post[0],document);
				}
			}
		}

		for(Integer key: documentHashMap.keySet()){
			queue.add(documentHashMap.get(key));
		}

		for(int i=0;i<TopN && i<queue.size();i++){
			documentArrayList.add(queue.poll());
		}
		return documentArrayList;
	}

}
