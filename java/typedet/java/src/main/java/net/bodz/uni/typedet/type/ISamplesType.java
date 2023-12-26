package net.bodz.uni.typedet.type;

public interface ISamplesType {

    void merge(ISamplesType o);

    void addSample(String sample);

}
