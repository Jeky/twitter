package twitter.analysis.testors;

import twitter.analysis.converters.GramAttrConverter;
import twitter.utils.Logger;

public class GramAttrClassificationTestor extends ClassificationTestor {

	public GramAttrClassificationTestor(int gram) {
		super(new GramAttrConverter(gram));
		Logger.info("Starting Classification Test using Gram Attributes (Gram = " + gram + ")...");
	}

}
