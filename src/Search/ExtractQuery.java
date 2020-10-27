package Search;

import Classes.Path;
import Classes.Query;

import java.io.*;
import java.util.*;

public class ExtractQuery {
	private Queue<Query> queries = new LinkedList<>();
	private HashSet<String> stopWords = new HashSet<>();

	public ExtractQuery() {
		try{
			// stop words
			File file = new File(Path.StopwordDir);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String readLine = bufferedReader.readLine();
			while (readLine != null){
				stopWords.add(readLine.trim());
				readLine = bufferedReader.readLine();
			}

			//  topics
			File file1 = new File(Path.TopicDir);
			fileReader = new FileReader(file1);
			bufferedReader = new BufferedReader(fileReader);
			readLine = bufferedReader.readLine();
			while (readLine != null){
				if(readLine.equals("<top>")){
					Query query = new Query();
					StringBuilder stringBuilder = new StringBuilder();
					readLine = bufferedReader.readLine();
					query.SetTopicId(readLine.split(" ")[2]);
					readLine = bufferedReader.readLine();
					String[] title = readLine.toLowerCase().split(" ");
					for(String x: title){
						if(!stopWords.contains(x) && !x.equals("<title>")){
							stringBuilder.append(x).append(" ");
						}
					}
					query.SetQueryContent(stringBuilder.toString().trim());
					queries.add(query);
				}
				readLine = bufferedReader.readLine();
			}
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	public boolean hasNext()
	{
		return !queries.isEmpty();
	}

	public Query next()
	{
		if(!this.queries.isEmpty()){
			return queries.poll();
		}else {
			return null;
		}

	}
}
