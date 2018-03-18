package edu.umd.lorelei.cfg;

public class Cfg
{
	public static String modelNames[]=new String[]{"LDA", "BSLDA", "SLDA"};
	public static String domainNames[]=new String[]{"Ebola", "Africom", "EI-En-Anger", "EI-En-Fear", "EI-En-Joy", "EI-En-Sadness",
			"EI-Es-Anger", "EI-Es-Fear", "EI-Es-Joy", "EI-Es-Sadness", "V-En", "V-Es"};
	
	public static String getModelFileName(int domainID, int modelID, int numTopics)
	{
		return domainNames[domainID]+"-"+modelNames[modelID]+"-"+numTopics+"topics-model.txt";
	}
	
	public static String getVocabFileName(int domainID)
	{
		return domainNames[domainID]+"-vocab.txt";
	}
	
	public static int checkDomainID(int domainID)
	{
		return (domainID<0 || domainID>=domainNames.length? 0 : domainID);
	}
	
	public static int checkModelID(int modelID)
	{
		return (modelID<0 || modelID>=modelNames.length? 0 : modelID);
	}
	
	public static int checkNumTopics(int numTopics)
	{
		return (numTopics==10 || numTopics==20? numTopics : 10);
	}
}
