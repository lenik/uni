package net.bodz.uni.typedet.type;

public abstract class AbstractSamplesType
        implements
            ISamplesType {

    public static final int DEFAULT_HISTO_MAXSIZE = 10000;

    public int sampleCount;
    public int errorCount;

    @Override
    public final void merge(ISamplesType other) {
        AbstractSamplesType o = (AbstractSamplesType) other;
        merge(o);
    }

    protected void merge(AbstractSamplesType o) {
        sampleCount += o.sampleCount;
        errorCount += o.errorCount;
    }

}
