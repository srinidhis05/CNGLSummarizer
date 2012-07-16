package ie.dcu.cngl.summarizer;

import java.io.IOException;

public class SkimmingFeature extends LuceneFeature {
	
	public SkimmingFeature() throws IOException {
		super();
	}

	// these two values are used to implement a simple linear deboost. If 
	// a different algorithm is desired, these variables are likely to be
	// no longer required.
	private float sentenceDeboost;
	private float sentenceDeboostBase = 0.5F;

	public void setSentenceDeboost(float sentenceDeboost) {
		if (sentenceDeboost < 0.0F || sentenceDeboost > 1.0F) {
			throw new IllegalArgumentException(
			"Invalid value: 0.0F <= sentenceDeboost <= 1.0F");
		}
		this.sentenceDeboost = sentenceDeboost;
	}

	public void setSentenceDeboostBase(float sentenceDeboostBase) {
		if (sentenceDeboostBase < 0.0F || sentenceDeboostBase > 1.0F) {
			throw new IllegalArgumentException(
			"Invalid value: 0.0F <= sentenceDeboostBase <= 1.0F");
		}
		this.sentenceDeboostBase = sentenceDeboostBase;
	}

	@Override
	protected float computeDeboost(int paragraphNumber, int sentenceNumber) {
		if (paragraphNumber > 0) {
			if (sentenceNumber > 0) {
				float deboost = 1.0F - (sentenceNumber * sentenceDeboost);
				deboost = (deboost < sentenceDeboostBase) ? sentenceDeboostBase : deboost; 
				return deboost;
			}
		}
		
		return 1.0F;
	}

	@Override
	public double getMultiplier() {
		return SummarizerUtils.skimmingMultiplier;
	}

}
