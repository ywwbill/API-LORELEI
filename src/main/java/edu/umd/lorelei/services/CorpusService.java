package edu.umd.lorelei.services;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;

import org.springframework.core.io.ClassPathResource;

import edu.umd.lorelei.cfg.Cfg;
import edu.umd.lorelei.utils.InputDoc;

public class CorpusService
{
	public static InputDoc[] convertCorpus(InputDoc inputDocs[]) throws IOException
	{	
		int numDocs=inputDocs.length;
		InputDoc outputDocs[]=new InputDoc[numDocs];
		
		ClassPathResource rescource=new ClassPathResource(Cfg.vocabFileName);
		BufferedReader br=new BufferedReader(new InputStreamReader(rescource.getInputStream()));
		String line;
		HashMap<String, Integer> vocabMap=new HashMap<String, Integer>();
		while ((line=br.readLine())!=null)
		{
			vocabMap.put(line, vocabMap.size());
		}
		br.close();
		int numVocab=vocabMap.size();
		
		int vocabCount[]=new int[numVocab];
		String seg[];
		for (int doc=0; doc<numDocs; doc++)
		{
			outputDocs[doc]=new InputDoc();
			outputDocs[doc].ID=inputDocs[doc].ID;
			outputDocs[doc].label=inputDocs[doc].label;
			outputDocs[doc].isIndexed=true;
			if (inputDocs[doc].isIndexed)
			{
				outputDocs[doc].content=inputDocs[doc].content;
				continue;
			}
			
			Arrays.fill(vocabCount, 0);
			seg=inputDocs[doc].content.split(" ");
			int length=0;
			for (int i=0; i<seg.length; i++)
			{
				if (!vocabMap.containsKey(seg[i])) continue;
				vocabCount[vocabMap.get(seg[i])]++;
				length++;
			}
			
			StringBuilder sb=new StringBuilder(""+length);
			for (int vocab=0; vocab<numVocab; vocab++)
			{
				if (vocabCount[vocab]==0) continue;
				sb.append(" "+vocab+":"+vocabCount[vocab]);
			}
			outputDocs[doc].content=sb.toString();
		}
		
		return outputDocs;
	}
}
