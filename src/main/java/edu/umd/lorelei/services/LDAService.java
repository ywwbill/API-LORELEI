package edu.umd.lorelei.services;

import java.io.IOException;

import edu.umd.lorelei.utils.InputDoc;
import edu.umd.lorelei.utils.Output;
import edu.umd.lorelei.utils.OutputDoc;
import edu.umd.lorelei.cfg.Cfg;
import edu.umd.lorelei.lda.LDA;
import edu.umd.lorelei.lda.LDAParam;
import edu.umd.lorelei.lda.bslda.BSLDA;

public class LDAService
{
	public static final int LDA=0,BSLDA=1;
	public static final int NUM_DOCS_UPPER_LIMIT=50000;
	
	public static Output runService(InputDoc inputDocs[], int domainID, int modelID, int numTopics) throws IOException
	{
		domainID=Cfg.checkDomainID(domainID);
		modelID=Cfg.checkModelID(modelID);
		numTopics=Cfg.checkNumTopics(numTopics);
		
		int numDocs=inputDocs.length;
		if (numDocs>NUM_DOCS_UPPER_LIMIT)
		{
			Output output=new Output(0, 0, 0);
			output.info="#Docs is over upper limit";
			return output;
		}
		LDAParam param=new LDAParam(Cfg.getVocabFileName(domainID));
		param.numTopics=numTopics;
		
		LDA lda;
		switch (modelID)
		{
		case BSLDA: lda=new BSLDA(Cfg.getModelFileName(domainID, modelID, numTopics), param); break;
		case LDA:
		default: lda=new LDA(Cfg.getModelFileName(domainID, modelID, numTopics), param); break;
		}
		lda.readCorpus(inputDocs);
		lda.initialize();
		lda.sample(100);
		
		Output output=new Output(numDocs, numTopics, param.numVocab);
		for (int doc=0; doc<numDocs; doc++)
		{
			output.docs[doc]=new OutputDoc(inputDocs[doc].ID, numTopics);
			for (int topic=0; topic<numTopics; topic++)
			{
				output.docs[doc].topicProb[topic]=lda.getDocTopicProb(doc, topic);
			}
			
			if (lda instanceof BSLDA)
			{
				output.docs[doc].label=inputDocs[doc].label;
				output.docs[doc].regression=((BSLDA)lda).computeWeight(doc);
				double posProb=1.0/(1.0+Math.exp(-output.docs[doc].regression));
				output.docs[doc].prediction=(posProb>0.5? 1 : 0);
				output.docs[doc].confidence=(output.docs[doc].prediction==1? posProb : 1.0-posProb);
			}
		}
		
		for (int topic=0; topic<numTopics; topic++)
		{
			output.topics[topic]=lda.wordsByWeight(topic);
		}
		
		if (lda instanceof BSLDA)
		{
			output.accuracy=((BSLDA)lda).computeAccuracy();
		}
		
		return output;
	}
}
