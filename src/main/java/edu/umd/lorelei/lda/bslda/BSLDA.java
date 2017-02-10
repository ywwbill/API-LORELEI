package edu.umd.lorelei.lda.bslda;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import com.google.gson.annotations.Expose;

import cc.mallet.optimize.LimitedMemoryBFGS;
import edu.umd.lorelei.lda.LDA;
import edu.umd.lorelei.lda.LDAParam;
import edu.umd.lorelei.utils.IOUtil;
import edu.umd.lorelei.utils.InputDoc;
import edu.umd.lorelei.utils.MathUtil;

public class BSLDA extends LDA
{
	@Expose protected double eta[];
	@Expose protected double tau[];
	
	protected int numLabels;
	protected int labels[];
	protected int predLabels[];
	protected boolean labelStatuses[];
	
	protected double weight;
	protected double error;
	
	public void readCorpus(InputDoc inputDocs[])
	{
		super.readCorpus(inputDocs);
		labels=new int[numDocs];
		predLabels=new int[numDocs];
		labelStatuses=new boolean[numDocs];
		numLabels=0;
		Arrays.fill(labelStatuses, false);
		for (int doc=0; doc<numDocs; doc++)
		{
			if (inputDocs[doc].label==0 || inputDocs[doc].label==1)
			{
				labels[doc]=inputDocs[doc].label;
				labelStatuses[doc]=true;
				numLabels++;
			}
		}
	}
	
	protected void printParam()
	{
		super.printParam();
		param.printSLDAParam("\t");
		IOUtil.println("\t#labels: "+numLabels);
	}
	
	public void sample(int numIters)
	{
		for (int iteration=1; iteration<=numIters; iteration++)
		{
			for (int doc=0; doc<numDocs; doc++)
			{
				sampleDoc(doc);
			}
			computeLogLikelihood();
			perplexity=Math.exp(-logLikelihood/numTestWords);
			
			if (type==TRAIN)
			{
				optimize();
			}
			
			if (param.verbose)
			{
				if (type==TRAIN)
				{
					IOUtil.print("<"+iteration+">"+"\tLog-LLD: "+format(logLikelihood)+
							"\tPPX: "+format(perplexity));
					computeError();
					if (numLabels>0)
					{
						IOUtil.print("\tError: "+format(error));
					}
					IOUtil.println();
				}
				else
				{
					if (iteration%5==0)
					{
						IOUtil.print("..."+iteration);
					}
				}
			}
			
			if (param.updateAlpha && iteration%param.updateAlphaInterval==0 && type==TRAIN)
			{
				updateHyperParam();
			}
		}
		
		if (param.verbose)
		{
			if (type==TRAIN)
			{
				for (int topic=0; topic<param.numTopics; topic++)
				{
					IOUtil.println(topWordsByFreq(topic, 10));
				}
			}
			else
			{
				IOUtil.println();
			}
		}
	}
	
	protected void sampleDoc(int doc)
	{
		int oldTopic,newTopic;
		weight=computeWeight(doc);
		for (int token=0; token<corpus.get(doc).docLength(); token++)
		{			
			oldTopic=unassignTopic(doc, token);
			if (type==TRAIN && labelStatuses[doc])
			{
				weight-=eta[oldTopic]/corpus.get(doc).docLength();
			}
			
			newTopic=sampleTopic(doc, token, oldTopic);
			
			assignTopic(doc, token, newTopic);
			if (type==TRAIN && labelStatuses[doc])
			{
				weight+=eta[newTopic]/corpus.get(doc).docLength();
			}
		}
	}
	
	protected double topicUpdating(int doc, int topic, int vocab)
	{
		double score=0.0;
		if (type==TRAIN)
		{
			score=Math.log((alpha[topic]+corpus.get(doc).getTopicCount(topic))*
					(param.beta+topics[topic].getVocabCount(vocab))/
					(param.beta*param.numVocab+topics[topic].getTotalTokens()));
		}
		else
		{
			score=Math.log((alpha[topic]+corpus.get(doc).getTopicCount(topic))*phi[topic][vocab]);
		}
		
		if (type==TRAIN && labelStatuses[doc])
		{
			double temp=MathUtil.sigmoid(weight+eta[topic]/corpus.get(doc).docLength());
			score+=Math.log(labels[doc]>0? temp : 1.0-temp);
		}
		
		return score;
	}
	
	public double computeWeight(int doc)
	{
		double weight=0.0;
		for (int topic=0; topic<param.numTopics; topic++)
		{
			weight+=eta[topic]*corpus.get(doc).getTopicCount(topic)/corpus.get(doc).docLength();
		}
		for (int vocab : corpus.get(doc).getWordSet())
		{
			weight+=tau[vocab]*corpus.get(doc).getWordCount(vocab)/corpus.get(doc).docLength();
		}
		return weight;
	}
	
	protected void optimize()
	{
		BSLDAFunction optimizable=new BSLDAFunction(this);
		LimitedMemoryBFGS lbfgs=new LimitedMemoryBFGS(optimizable);
		try
		{
			lbfgs.optimize();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		for (int topic=0; topic<param.numTopics; topic++)
		{
			eta[topic]=optimizable.getParameter(topic);
		}
		for (int vocab=0; vocab<param.numVocab; vocab++)
		{
			tau[vocab]=optimizable.getParameter(param.numTopics+vocab);
		}
	}
	
	protected void computeError()
	{
		error=0.0;
		if (numLabels==0) return;
		for (int doc=0; doc<numDocs; doc++)
		{
			if (!labelStatuses[doc]) continue;
			error+=MathUtil.sqr(labels[doc]-computeDocLabelProb(doc));
		}
		error=Math.sqrt(error/(double)numLabels);
	}
	
	protected double computeDocLabelProb(int doc)
	{
		return MathUtil.sigmoid(computeWeight(doc));
	}
	
	protected void computePredLabels()
	{
		for (int doc=0; doc<numDocs; doc++)
		{
			predLabels[doc]=(computeDocLabelProb(doc)>0.5? 1 : 0);
		}
	}
	
	public int[] getPredLabels()
	{
		computePredLabels();
		return predLabels.clone();
	}
	
	public int[] getLabels()
	{
		return labels.clone();
	}
	
	public double computeAccuracy()
	{
		if (numLabels==0) return 0.0;
		computePredLabels();
		int correctCount=0;
		for (int doc=0; doc<numDocs; doc++)
		{
			if (labelStatuses[doc] && labels[doc]==predLabels[doc])
			{
				correctCount++;
			}
		}
		return (double)correctCount/(double)numLabels;
	}
	
	public void writePredLabels(String predLabelFileName) throws IOException
	{
		computePredLabels();
		BufferedWriter bw=new BufferedWriter(new FileWriter(predLabelFileName));
		IOUtil.writeVector(bw, predLabels);
		bw.close();
	}
	
	public void writeRegValues(String regFileName) throws IOException
	{
		BufferedWriter bw=new BufferedWriter(new FileWriter(regFileName));
		for (int doc=0; doc<numDocs; doc++)
		{
			double reg=computeWeight(doc);
			bw.write(reg+"");
			bw.newLine();
		}
		bw.close();
	}
	
	public int getLabel(int doc)
	{
		return labels[doc];
	}
	
	public double getTopicWeight(int topic)
	{
		return eta[topic];
	}
	
	public double[] getTopicWeights()
	{
		return eta.clone();
	}
	
	public double getLexWeight(int vocab)
	{
		return tau[vocab];
	}
	
	public double[] getLexWeights()
	{
		return tau.clone();
	}
	
	public boolean getLabelStatus(int doc)
	{
		return labelStatuses[doc];
	}
	
	public double getError()
	{
		return error;
	}
	
	protected void initVariables()
	{
		super.initVariables();
		eta=new double[param.numTopics];
		tau=new double[param.numVocab];
	}
	
	protected void copyModel(LDA LDAModel)
	{
		super.copyModel(LDAModel);
		eta=((BSLDA)LDAModel).eta.clone();
		tau=((BSLDA)LDAModel).tau.clone();
	}
	
	public BSLDA(LDAParam parameters)
	{
		super(parameters);
	}
	
	public BSLDA(BSLDA LDATrain, LDAParam parameters)
	{
		super(LDATrain, parameters);
	}
	
	public BSLDA(String modelFileName, LDAParam parameters) throws IOException
	{
		super(modelFileName, parameters);
	}
}
