# <h1 id="top">API for LORELEI</h1>

This repository holds LORELEI API source code. The documentation is for using the API, not the technical details in source code. Feel free to email <wwyang@cs.umd.edu> with any questions.

* [Input File Format](#input)
* [Output File Format](#output)
* [Command Format](#cmd)
	* [Corpus Convertion](#convertion)
	* [LDA and its Extensions](#lda)
	* [Supported Models and Domains](#support)
* [Remarks](#remarks)

## <h2 id="input">Input File Format</h2>

The input contains an array of `InputDoc` objects. Each `InputDoc` object represents an input document and has 3 fields:

- `ID` (String): Defined by user.
- `label` (Integer): 0 for negative example and 1 for positive example. If the label is not available, simply assign it with any other number.
- `content` (String): The content of document in the following format

	```
	<doc-len> <word-type-1>:<frequency-1> <word-type-2>:<frequency-2> ... <word-type-n>:<frequency-n>
	```
	
	`<doc-len>` is the total number of *tokens* in this document. `<word-type-i>` denotes the i-th word in `<vocab-file>`, starting from 0. Words with zero frequency can be omitted.

However, in order to represent a document in the above format, one should have the vocabulary. For users' convenience, we provide an [API to convert documents](#convertion) into above indexed format. When calling this [convertion API](#convertion), users could simply put <a name="raw-doc">raw document</a> in the `content` field. For instance, 

	[
  		{
    		"ID": "483898242594136065",
    		"content": "jump hotel pass pass town obscene woman side side bus show butt passenger ",
    		"label": 0
  		},
  		{
    		"ID": "483925670448218112",
    		"content": "deputy director enteric enteric diarrheal diseases vaccine seattle wa united states #jobs ",
    		"label": 0
  		}
	]

By calling our conversion service, users will be able to get the <a name="indexed-doc">indexed documents</a> as

	[
  		{
    		"ID": "483898242594136065",
    		"content": "13 7:1 8:1 9:2 10:1 11:1 12:1 13:2 14:1 15:1 16:1 17:1",
    		"label": 0
  		},
  		{
    		"ID": "483925670448218112",
    		"content": "12 23:1 24:1 25:2 26:1 27:1 28:1 29:1 30:1 31:1 32:1 33:1",
    		"label": 0
  		}
	]

## <h2 id="output">Output File Format</h2>

The output file contains an `Output` object which has following fields:

- `info` (String): Error information. Empty if the program exits normally.
- `accuracy` (Double): The accuracy on given corpus. `NaN` if using LDA.
- `topics` (two-dimensional String array): The words in each topic sorted in descending order by their weights.
- `docs` (`OutputDoc` object array): Each element `OutputDoc` corresponds to a document in the input corpus and has following fields:
	- `ID`: Same as the input ID.
	- `topicProb` (Double array): The probabilities of topics in this document.
	- `regression` (Double): The regression value of the document. `NaN` if using LDA.
	- `prediction` (Integer): Predicted label. `-1` if using LDA.
	- `confidence` (Double): The confidence of predicting the predicted label. `NaN` if using LDA.
	- `label` (Integer): True label. Same as input.

If the [input file](#indexed-doc) in is fed to our web service and the user selects binary supervised LDA with 20 topics, the service will return information as

	{
		"docs": [
    		{
      			"ID": "483898242594136065",
      			"topicProb": [
        			0.006666666666666667,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.07333333333333333,
        			0.74,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.07333333333333333,
        			0.006666666666666667,
        			0.006666666666666667
      				],
      			"confidence": 0.8211592679487474,
      			"regression": -1.5242214385796562,
      			"prediction": 0,
      			"label": 0
    		},
    		{
      			"ID": "483925670448218112",
      			"topicProb": [
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.07857142857142858,
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.07857142857142858,
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.2928571428571428,
        			0.0071428571428571435,
        			0.43571428571428567,
        			0.0071428571428571435
      				],
      			"confidence": 0.5105309486540602,
      			"regression": -0.04213002502912222,
      			"prediction": 0,
      			"label": 0
    		}
  		],
  		"topics": [
    		["whore", "return", "fucking", "make", "fuck", "jessica", "bitch", "time", "disappear", "mother"],
    		["#kebetu", "catch", "#senegal", "happen", "face", "senegal", "hate", "#dakar", "://", "mind"],
    		["good", "love", "great", "catch", "nice", "peace", "flight", "night", "make", "stop"],
    		["catch","person","work","happen","contact","response","fire","://","black","money"],
			["return","today","day","person","leave","school","nigga","yesterday","catch","tomorrow"],
			["return","happen","good","love","thing","happy","life","day","make","home"],
			["pm","fear","crisis","talk","case","ebola","comment","attack","newspaper","leave"],
			["ebola","#ebola","virus","outbreak","africa","senegal","#liberia","liberia","west","country"],
			["good","pm","return","make","play","big","beautiful","world","guinea","team"],
			["case","ebola","lol","di","mom","dem","good","bi","yi","mo"],
			["happen","appeal","man","year","time","woman","pass","person","give","contact"],
			["://","鈾�","position","person","flight","story","#adidas","#guinea","sleep","blood"],
			["flight","airport","://","home","mh17","wait","crash","month","plane","poor"],
			["day","happen","://","childhood","bad","cnn","waaah","mddr","contact","lose"],
			["fear","cc","love","god","life","give","limited","time","shot","hurt"],
			["case","post","subjective","tweeted","intend","tweet","belong","compare","hear","good"],
			["#ebola","ebola","hospital","sierra","leone","#sierraleone","die","liberia","hit","today"],
			["time","sweat","home","mm","person","vomit","congo","spit","fever","back"],
			["://","crisis","blood","family","world","neighbourhood","continuation","pakistan","easy","true"],
			["disease","://","blood","heart","health","eric","man","malaria","thin","death"]
  		],
  		"info": "",
  		"accuracy": 1
	}


You might have noticed that there exists some unreadable characters in Topic 11. It is actually emojis which can only be displayed properly in Mac OS.

In the example output above, I'm only showing the top 10 words for each topic (`topics` element) due to space limit. The actual `topics` element will contain all the words in descending order by their weights.

## <h2 id="cmd">Command Format</h2>

Our web service is located at `lorelei.umiacs.umd.edu`. Currently we provide the following APIs.

### <h3 id="convertion">Corpus Convertion</h3>

This service could convert a corpus represented by [words](#raw-doc) into [indices](#indexed-doc), as shown in [Input File Format](#input) section. The command format is

```
curl lorelei.umiacs.umd.edu/lorelei/convert-corpus -X POST -v -F corpus=@input-file -F domain=DomainID 
```

It has two parameters:

- `@input-file`: The file which contains the input. Note that the `@` symbol should not be omitted.
- `DomainID` (Integer, Optional): Corpus domain ID. See [Supportings](#support) for more information.

For example, assuming that the input file is named `doc-test.txt`, users could convert it into [indexed format](#indexed-doc) by calling

```
curl lorelei.umiacs.umd.edu/lorelei/convert-corpus -X POST -v -F corpus=@doc-test.txt -F domain=0 > indexed-doc-test.txt
```

### <h3 id="lda">LDA and its Extensions</h3>

This service supports to run LDA and its extension(s) on given corpus with a pre-trained model. The command format is

```
curl lorelei.umiacs.umd.edu/lorelei/lda -X POST -v -F corpus=@corpus-file -F domain=DomainID -F modelID=ModelID -F numTopics=NumTopics
```

This command has four parameters:

- `@corpus-file`: The file which contains input documents. The `@` symbol can not be omitted.
- `DomainID` (Integer, Optional): Corpus domain ID.
- `ModelID` (Integer, Optional): The LDA model ID.
- `NumTopics` (Integer, Optional): Number of topics.

Please see [Supportings](#support) for values of `DomainID`, `ModelID`, and `NumTopics`.

For example, assuming that the input corpus file is named `indexed-doc-test.txt`, and we want to run binary supervised LDA trained on Ebola corpus with 20 topics, the command is

```
curl lorelei.umiacs.umd.edu/lorelei/lda -X POST -v -F corpus=@indexed-doc-test -F domain=0 -F modelID=1 -F numTopics=20 > output.txt
```

### <h3 id="support">Supportings</h3>

- `ModelID`:
	- 0: Vanilla LDA (default).
	- 1: Binary supervised LDA.
- `NumTopics`:
	- 10 (default).
	- 20.
- `DomainID`:
	- 0: Ebola (default).

## <h2 id="remarks">Remarks</h2>

Currently, I can not figure out how to return the JSON string in pretty print format. This problem can now only be solved at client end by using external tools like [jq](https://stedolan.github.io/jq/).

The example command of [LDA](#lda) is then as follows

```
curl lorelei.umiacs.umd.edu/lorelei/lda -X POST -v -F corpus=@indexed-doc-test -F domain=0 -F modelID=1 -F numTopics=20 | ./jq '.' > output.txt
```