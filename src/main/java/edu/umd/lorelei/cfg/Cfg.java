package edu.umd.lorelei.cfg;

public class Cfg
{
	public static String vocabFileName="vocab.txt";
	
	public static String modelNames[]=new String[]{"LDA", "BSLDA"};
	
	public static String getModelFileName(int modelID, int numTopics)
	{
		return modelNames[modelID]+"-"+numTopics+"topics-model.txt";
	}
	
//	public static String getVocabFileName(int modelID)
//	{
//		return modelNames[modelID]+"-vocab.txt";
//	}
	
	public static int checkModelID(int modelID)
	{
		return (modelID<0 || modelID>=modelNames.length? 0 : modelID);
	}
	
	public static int checkNumTopics(int numTopics)
	{
		return (numTopics==10 || numTopics==20 || numTopics==50? numTopics : 20);
	}
}
