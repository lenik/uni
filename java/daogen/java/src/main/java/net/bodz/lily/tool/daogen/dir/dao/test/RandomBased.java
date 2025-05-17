package net.bodz.lily.tool.daogen.dir.dao.test;

import java.util.Random;

import net.bodz.bas.codegen.EnglishTextGenerator;

public class RandomBased {

    protected final long rootSeed;
    public final Random random;
    public final EnglishTextGenerator enGen;

    public RandomBased(long... seeds) {
        this.rootSeed = computeSeed(seeds);
        random = random();
        enGen = en();
    }

    long computeSeed(long... seeds) {
        long seed = rootSeed;
        for (long s : seeds)
            seed = seed * 251 + s;
        return seed;
    }

    Random random(long... seeds) {
        return new Random(computeSeed(seeds));
    }

    EnglishTextGenerator en(long... seeds) {
        Random random = random(seeds);
        return new EnglishTextGenerator(random);
    }

}
