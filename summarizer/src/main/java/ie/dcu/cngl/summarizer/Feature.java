package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.PageStructure;
import ie.dcu.cngl.tokenizer.TokenInfo;
import ie.dcu.cngl.tokenizer.TokenizerUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.TokenStream;

public abstract class Feature {
	
	protected SummaryAnalyzer analyzer;
	protected PageStructure structure;
	
	public Feature() throws IOException {
		this.analyzer = new SummaryAnalyzer();
	}
	
	public void setStructure(PageStructure structure) {
		this.structure = structure;
	}
	
	public abstract double getMultiplier();
	
	public abstract Double[] calculateRawWeights(Double[] weights);
	
	public Double[] getWeights() {
		Double [] weights = new Double[structure.getNumSentences()];
		for(int i = 0; i < weights.length; i++) {
			weights[i] = 0.0;
		}
		weights = calculateRawWeights(weights);
		normalise(weights);
		applyMultiplier(weights);
		return weights;
	}
	
	protected int getNumOccurences(String str, String longerStr) {
		final int len = str.length();
		int numOccurences = 0;
		if (len > 0) {  
			int start = longerStr.indexOf(str);
			while (start != -1) {
				numOccurences++;
				start = longerStr.indexOf(str, start+len);
			}
		}
		return numOccurences;
	}
	
	protected double numberOfTerms(ArrayList<TokenInfo> sentence) {
		double numTerms = 0;
		
		StringReader reader = new StringReader(TokenizerUtils.recombineTokens1d(sentence));
		TokenStream tokenStream = analyzer.tokenStream(null, reader);

		try {
			while (tokenStream.incrementToken()) {
				numTerms++;
			}
		} catch (IOException e) {}
		
		return numTerms;
	}
	
	private void normalise(Double[] weights) {
		double max = getMax(weights);
		if(max != 0) {
			for(int i = 0; i < weights.length; i++) {
				weights[i]/=max;
			}
		}
	}
	
	private void applyMultiplier(Double[] weights) {
		final double multiplier = getMultiplier();
		for(int i = 0; i < weights.length; i++) {
			weights[i]*=multiplier;
		}
	}

	private double getMax(Double[] weights) {
		double max = weights[0];
		for(int i = 1; i < weights.length; i++) {
			if(weights[i] > max) {
				max = weights[i];
			}
		}
		return max;
	}
	
}
