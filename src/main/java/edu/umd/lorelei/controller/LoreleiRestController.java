package edu.umd.lorelei.controller;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
//import javax.annotation.PostConstruct;

import com.google.gson.Gson;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import edu.umd.lorelei.services.CorpusService;
import edu.umd.lorelei.services.LDAService;
import edu.umd.lorelei.utils.Doc;
import edu.umd.lorelei.utils.InputDoc;
import edu.umd.lorelei.utils.Output;

@RestController
public class LoreleiRestController
{
	private Gson gson=new Gson();
	
//	@PostConstruct
//	public void init()
//	{
//		gson=new Gson();
//	}
	
	@RequestMapping(value="/convert-corpus", method=RequestMethod.POST)
	public InputDoc[] convertCorpus(@RequestParam("corpus") MultipartFile corpusFile,
			@RequestParam(name="domain", required=false, defaultValue="0") int domainID) throws IOException
	{
		InputDoc inputDocs[]=gson.fromJson(new InputStreamReader(corpusFile.getInputStream()), InputDoc[].class);
		return CorpusService.convertCorpus(inputDocs, domainID);
	}
	
	@RequestMapping(value="/lda", method=RequestMethod.POST)
	public Output runLDA(@RequestParam("corpus") MultipartFile corpusFile,
			@RequestParam(name="domain", required=false, defaultValue="0") int domainID,
			@RequestParam(name="modelID", required=false, defaultValue="0") int modelID,
			@RequestParam(name="numTopics", required=false, defaultValue="10") int numTopics) throws IOException
	{
		InputDoc inputDocs[]=gson.fromJson(new InputStreamReader(corpusFile.getInputStream()), InputDoc[].class);
		return LDAService.runService(inputDocs, domainID, modelID, numTopics);
	}
	
	@RequestMapping(value="/file-test", method=RequestMethod.POST)
	public Doc showContent(@RequestParam("file") MultipartFile file) throws IOException
	{
		byte[] bytes=file.getBytes();
		String output=new String(bytes);
		
		return new Doc(output);
	}
	
	@RequestMapping(value="/file-test2", method=RequestMethod.POST)
	public String showContent2(@RequestParam("file") MultipartFile file) throws IOException
	{
//		Gson gson=new Gson();
		Doc doc[]=gson.fromJson(new InputStreamReader(file.getInputStream()), Doc[].class);
		
		String output="";
		for (int i=0; i<doc.length; i++)
		{
			output+=doc[i].doc+"\n";
		}
		
		return output;
	}
	
	@RequestMapping(value="/rescource-test", method=RequestMethod.POST)
	public String rescourceTest() throws IOException
	{
		ClassPathResource rescource=new ClassPathResource("test.txt");
		BufferedReader br=new BufferedReader(new InputStreamReader(rescource.getInputStream()));
		String line,content="";
		while ((line=br.readLine())!=null)
		{
			content+=line;
		}
		br.close();
		return content;
	}
	
	@RequestMapping(value="/param-test", method=RequestMethod.POST)
	public String paramTest(
			@RequestParam(name="intParam", required=false, defaultValue="1") int intParam,
			@RequestParam(name="doubleParam", required=false, defaultValue="1.0") double doubleParam,
			@RequestParam(name="stringParam", required=false, defaultValue="s") String stringParam,
			@RequestParam(name="boolParam", required=false, defaultValue="false") boolean boolParam)
	{
		return "intParam: "+intParam+"\ndoublePaaram: "+doubleParam+"\nstringParam: "+stringParam+"\nboolParam: "+boolParam;
	}
	
	@RequestMapping(value="/call-test", method=RequestMethod.POST)
	public String callTest()
	{
		return "Hello World!";
	}
	
	@Bean
	public CommonsMultipartResolver multipartResolver()
	{
		CommonsMultipartResolver resolver=new CommonsMultipartResolver();
		resolver.setDefaultEncoding("utf-8");
//		resolver.setMaxInMemorySize(50000000);
		return resolver;
	}
}
