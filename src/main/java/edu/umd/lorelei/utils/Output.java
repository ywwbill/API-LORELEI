package edu.umd.lorelei.utils;

public class Output
{
	public OutputDoc docs[];
	public String topics[];
	public String info;
	public double accuracy;
	
	public Output(int numDocs, int numTopics)
	{
		docs=new OutputDoc[numDocs];
		topics=new String[numTopics];
		info="";
		accuracy=Double.NaN;
	}
}
