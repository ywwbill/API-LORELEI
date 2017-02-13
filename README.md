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
- `topics` (String array): The top 10 words in each topic, along with their probability in that topic. If the user is calling binary supervised LDA, each topic has a weight for classification.
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
        			0.07333333333333333,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.006666666666666667,
       				0.006666666666666667,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.8066666666666666,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.006666666666666667,
        			0.006666666666666667
      			],
      			"confidence": 0.813989946252492,
      			"regression": -1.4761472902267212,
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
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.43571428571428567,
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.0071428571428571435,
        			0.43571428571428567,
        			0.0071428571428571435
      			],
      			"confidence": 0.8474419768439863,
      			"regression": -1.7146773696149633,
      			"prediction": 0,
      			"label": 0
    		},
  		"topics": [
    		"Topic 0 (Weight: -4.1391): Topic 0:   whore:0.1112   return:0.0232   fucking:0.0178   make:0.0124   fuck:0.0117   jessica:0.0109   bitch:0.0101   time:0.0093   disappear:0.0093   mother:0.0093",
    		"Topic 1 (Weight: -1.5510): Topic 1:   #kebetu:0.0377   catch:0.0317   #senegal:0.0268   happen:0.0139   face:0.0130   senegal:0.0110   hate:0.0110   #dakar:0.0110   ://:0.0090   mind:0.0090",
    		"Topic 2 (Weight: 3.4979): Topic 2:   good:0.0360   love:0.0215   great:0.0163   catch:0.0155   nice:0.0121   peace:0.0121   flight:0.0112   night:0.0112   make:0.0103   stop:0.0095",
    		"Topic 3 (Weight: -0.1414): Topic 3:   catch:0.0272   person:0.0225   work:0.0197   happen:0.0179   contact:0.0151   response:0.0123   fire:0.0123   ://:0.0113   black:0.0094   money:0.0094",
    		"Topic 4 (Weight: -2.2176): Topic 4:   return:0.0521   today:0.0188   day:0.0179   person:0.0179   leave:0.0123   school:0.0123   nigga:0.0106   yesterday:0.0098   catch:0.0090   tomorrow:0.0090",
    		"Topic 5 (Weight: 3.3123): Topic 5:   return:0.0770   happen:0.0624   good:0.0367   love:0.0206   thing:0.0177   happy:0.0162   life:0.0155   day:0.0147   make:0.0140   home:0.0140",
    		"Topic 6 (Weight: -3.7771): Topic 6:   pm:0.0574   fear:0.0482   crisis:0.0334   talk:0.0186   case:0.0177   ebola:0.0167   comment:0.0112   attack:0.0093   newspaper:0.0093   leave:0.0084",
    		"Topic 7 (Weight: -3.6972): Topic 7:   ebola:0.0937   #ebola:0.0528   virus:0.0305   outbreak:0.0186   africa:0.0153   senegal:0.0147   #liberia:0.0137   liberia:0.0137   west:0.0137   country:0.0126",
    		"Topic 8 (Weight: 3.8742): Topic 8:   good:0.0569   pm:0.0371   return:0.0322   make:0.0207   play:0.0174   big:0.0141   beautiful:0.0141   world:0.0124   guinea:0.0124   team:0.0124",
    		"Topic 9 (Weight: -1.3498): Topic 9:   case:0.0678   ebola:0.0224   lol:0.0166   di:0.0150   mom:0.0117   dem:0.0117   good:0.0100   bi:0.0100   yi:0.0092   mo:0.0083",
    		"Topic 10 (Weight: -1.5939): Topic 10:   happen:0.0227   appeal:0.0168   man:0.0160   year:0.0143   time:0.0135   woman:0.0127   pass:0.0118   person:0.0110   give:0.0110   contact:0.0085",
    		"Topic 11 (Weight: 0.7350): Topic 11:   ://:0.0497   鈾�:0.0319   position:0.0300   person:0.0235   flight:0.0132   story:0.0123   #adidas:0.0113   #guinea:0.0104   sleep:0.0095   blood:0.0095",
    		"Topic 12 (Weight: 0.5386): Topic 12:   flight:0.0618   airport:0.0537   ://:0.0173   home:0.0146   mh17:0.0137   wait:0.0110   crash:0.0101   month:0.0092   plane:0.0092   poor:0.0083",
    		"Topic 13 (Weight: -1.3934): Topic 13:   day:0.0374   happen:0.0356   ://:0.0252   childhood:0.0226   bad:0.0140   cnn:0.0140   waaah:0.0131   mddr:0.0122   contact:0.0105   lose:0.0105",
    		"Topic 14 (Weight: -1.2038): Topic 14:   fear:0.0987   cc:0.0223   love:0.0190   god:0.0182   life:0.0173   give:0.0116   limited:0.0091   time:0.0083   shot:0.0083   hurt:0.0083",
    		"Topic 15 (Weight: -2.3061): Topic 15:   case:0.2815   post:0.0648   subjective:0.0454   tweeted:0.0419   intend:0.0266   tweet:0.0207   belong:0.0189   compare:0.0148   hear:0.0142   good:0.0118",
    		"Topic 16 (Weight: -0.9423): Topic 16:   #ebola:0.0396   ebola:0.0396   hospital:0.0266   sierra:0.0238   leone:0.0216   #sierraleone:0.0180   die:0.0144   liberia:0.0108   hit:0.0101   today:0.0080",
    		"Topic 17 (Weight: -1.0105): Topic 17:   time:0.0226   sweat:0.0218   home:0.0166   mm:0.0166   person:0.0157   vomit:0.0148   congo:0.0148   spit:0.0140   fever:0.0131   back:0.0131",
    		"Topic 18 (Weight: 0.3601): Topic 18:   ://:0.0488   crisis:0.0323   blood:0.0203   family:0.0148   world:0.0139   neighbourhood:0.0130   continuation:0.0130   pakistan:0.0130   easy:0.0120   true:0.0102",
    		"Topic 19 (Weight: -3.2689): Topic 19:   disease:0.0614   ://:0.0505   blood:0.0164   heart:0.0156   health:0.0125   eric:0.0117   man:0.0109   malaria:0.0109   thin:0.0109   death:0.0102"
 	 	],
  		"info": "",
  		"accuracy": 1
	}

You might have noticed that there exists some unreadable characters in Topic 11. It is actually emojis which can only be displayed properly in Mac OS.

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