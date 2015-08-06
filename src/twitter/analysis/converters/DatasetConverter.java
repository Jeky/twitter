package twitter.analysis.converters;

import twitter.ml.Dataset;

public interface DatasetConverter<T> {

	String CLASS_LABEL = "__cls__";

	double SPAMMER_CLASS_LABEL = 1.0;
	double NONSPAMMER_CLASS_LABEL = 0.0;

	Dataset convert(T origin, double cls);
}
