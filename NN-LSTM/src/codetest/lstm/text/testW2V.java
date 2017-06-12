package codetest.lstm.text;

import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.ui.UiServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Vector;

/**
 * Created by agibsonccc on 10/9/14.
 */
public class testW2V {



    public static void main(String[] args) throws Exception {

        String filePath = "Shakespeare.txt";

  // Strip white space before and after for each line
        SentenceIterator iter = new BasicLineIterator(filePath);
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());

        CommonPreprocessor cmp = new CommonPreprocessor();
        
 
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .iterations(1)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();

   
        vec.fit();



        // Write word vectors
     //   WordVectorSerializer.writeWordVectors(vec, "pathToWriteto.txt");


        Collection<String> lst = vec.wordsNearest("day", 10);
        System.out.println(lst);
    
       double[] fv = vec.getWordVector("day");
       double[] fv2 = vec.getWordVector("day");
       
       
       double sum = 0;
       
       for (int i = 0; i < fv.length; i++)
       {
    	   System.out.println(fv[i]);
    	   if (i%10 == 0) { System.out.println("\n"); }
    	   
    	   sum+=fv[i];
    	   
    	   if (fv[i] != fv2[i])
    	   {
    		   System.out.println("WRONG");
    	   }
       }
       
       System.out.println();
       System.out.println(sum);
       
       
       System.out.println("FV length: " + fv.length);
       
       Collection <String> tmp = new Vector<String>();
       tmp.add("day");
        lst = vec.wordsNearest(vec.getWordVectors(tmp), 2);
        System.out.println(lst);
       
        VocabCache <VocabWord> vocab = vec.getVocab();
        
     
    }
}
