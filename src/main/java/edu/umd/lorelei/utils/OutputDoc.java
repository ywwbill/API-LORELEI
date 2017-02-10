package edu.umd.lorelei.utils;

public class OutputDoc
{
	public final String ID;
	
	public double topicProb[];
	public double confidence,regression;
	public int prediction;
	public int label;
	
	public OutputDoc(String id, int numTopics)
	{
		this.ID=id;
		topicProb=new double[numTopics];
		confidence=Double.NaN;
		regression=Double.NaN;
		prediction=-1;
		label=-1;
	}
}
