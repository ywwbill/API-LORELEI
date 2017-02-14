package edu.umd.lorelei.utils;

public class Output
{
	public OutputDoc docs[];
	public OutputWord topics[][];
	public String info;
	public double accuracy;
	
	public Output(int numDocs, int numTopics, int numVocab)
	{
		docs=new OutputDoc[numDocs];
		topics=new OutputWord[numTopics][numVocab];
		info="";
		accuracy=Double.NaN;
	}
}
