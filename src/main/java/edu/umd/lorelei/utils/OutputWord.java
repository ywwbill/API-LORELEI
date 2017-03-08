package edu.umd.lorelei.utils;

public class OutputWord implements Comparable<OutputWord>
{
	public String word;
	public double weight;
	public double termScore;
	
	public OutputWord(String word, double weight, double termScore)
	{
		this.word=word;
		this.weight=weight;
		this.termScore=termScore;
	}
	
	public OutputWord()
	{
		this("", 0.0, 0.0);
	}
	
	public int compareTo(OutputWord o)
	{
		return -Double.compare(this.termScore, o.termScore);
	}
}
