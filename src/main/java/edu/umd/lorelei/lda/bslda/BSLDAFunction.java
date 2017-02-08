package edu.umd.lorelei.lda.bslda;

import java.util.Arrays;

import cc.mallet.optimize.Optimizable.ByGradientValue;

public class BSLDAFunction implements ByGradientValue
{
	private double eta[],tau[];
	private double etaGrad[],tauGrad[];
	private int numTopics,numVocab;
	private double nuSq;
	private BSLDA slda;
	
	public BSLDAFunction(BSLDA SLDAInst)
	{
		this.slda=SLDAInst;
		this.numTopics=slda.param.numTopics;
		this.numVocab=slda.param.numVocab;
		this.nuSq=slda.param.nu*slda.param.nu;
		
		eta=new double[numTopics];
		etaGrad=new double[numTopics];
		for (int topic=0; topic<numTopics; topic++)
		{
			eta[topic]=slda.getTopicWeight(topic);
		}
		
		tau=new double[numVocab];
		tauGrad=new double[numVocab];
		for (int vocab=0; vocab<numVocab; vocab++)
		{
			tau[vocab]=slda.getLexWeight(vocab);
		}
	}
	
	public double getValue()
	{
		double value=0.0,weight;
		for (int doc=0; doc<slda.getNumDocs(); doc++)
		{
			if (!slda.getLabelStatus(doc)) continue;
			weight=computeWeight(doc);
			value-=Math.log(1.0+Math.exp(-weight));
			if (slda.labels[doc]==0)
			{
				value-=weight;
			}
		}
		for (int topic=0; topic<numTopics; topic++)
		{
			value-=eta[topic]*eta[topic]/(2.0*nuSq);
		}
		for (int vocab=0; vocab<numVocab; vocab++)
		{
			value-=tau[vocab]*tau[vocab]/(2.0*nuSq);
		}
		return value;
	}
	
	public void getValueGradient(double gradient[])
	{
		Arrays.fill(etaGrad, 0.0);
		Arrays.fill(tauGrad, 0.0);
		for (int doc=0; doc<slda.getNumDocs(); doc++)
		{
			if (!slda.getLabelStatus(doc)) continue;
			double weight=computeWeight(doc);
			double commonTerm=Math.exp(-weight)/(1.0+Math.exp(-weight));
			
			for (int topic=0; topic<numTopics; topic++)
			{
				etaGrad[topic]+=commonTerm*slda.getDoc(doc).getTopicCount(topic)/
						slda.getDoc(doc).docLength();
				if (slda.labels[doc]==0)
				{
					etaGrad[topic]-=1.0*slda.getDoc(doc).getTopicCount(topic)/
							slda.getDoc(doc).docLength();
				}
			}
			
			for (int vocab : slda.getDoc(doc).getWordSet())
			{
				tauGrad[vocab]+=commonTerm*slda.getDoc(doc).getWordCount(vocab)/
						slda.getDoc(doc).docLength();
				if (slda.labels[doc]==0)
				{
					tauGrad[vocab]-=1.0*slda.getDoc(doc).getWordCount(vocab)/
							slda.getDoc(doc).docLength();
				}
			}
		}
		
		for (int topic=0; topic<numTopics; topic++)
		{
			gradient[topic]=etaGrad[topic]-eta[topic]/nuSq;
		}
		for (int vocab=0; vocab<numVocab; vocab++)
		{
			gradient[numTopics+vocab]=tauGrad[vocab]-tau[vocab]/nuSq;
		}
	}
	
	private double computeWeight(int doc)
	{
		double weight=0.0;
		for (int topic=0; topic<numTopics; topic++)
		{
			weight+=eta[topic]*slda.getDoc(doc).getTopicCount(topic)/
					slda.getDoc(doc).docLength();
		}
		for (int vocab : slda.getDoc(doc).getWordSet())
		{
			weight+=tau[vocab]*slda.getDoc(doc).getWordCount(vocab)/
					slda.getDoc(doc).docLength();
		}
		return weight;
	}
	
	public int getNumParameters()
	{
		return numTopics+numVocab;
	}
	
	public double getParameter(int i)
	{
		return (i<numTopics? eta[i] : tau[i-numTopics]);
	}
	
	public void getParameters(double buffer[])
	{
		for (int topic=0; topic<numTopics; topic++)
		{
			buffer[topic]=eta[topic];
		}
		for (int vocab=0; vocab<numVocab; vocab++)
		{
			buffer[numTopics+vocab]=tau[vocab];
		}
	}
	
	public void setParameter(int i, double r)
	{
		if (i<numTopics)
		{
			eta[i]=r;
			return;
		}
		
		tau[i-numTopics]=r;
	}
	
	public void setParameters(double newParameters[])
	{
		for (int topic=0; topic<numTopics; topic++)
		{
			eta[topic]=newParameters[topic];
		}
		for (int vocab=0; vocab<numVocab; vocab++)
		{
			tau[vocab]=newParameters[numTopics+vocab];
		}
	}
}
