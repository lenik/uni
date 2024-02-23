package net.bodz.lily.tool.daogen.dir.dao.test;

import java.util.Random;

import net.bodz.bas.codegen.EnglishTextGenerator;

public class RandomBased {

    protected final long rootSeed;
    public final Random random;
    public final EnglishTextGenerator enGen;

    public RandomBased(long rootSeed, Object seedObj) {
        this.rootSeed = rootSeed;
        random = random(seedObj);
        enGen = en(seedObj);
    }

    Random random(Object seedObj) {
        int prime = 17;
        long seed = this.rootSeed;
        if (seedObj != null)
            seed += prime * seedObj.hashCode();
        return new Random(seed);
    }

    EnglishTextGenerator en(Object seedObj) {
        Random random = random(seedObj);
        return new EnglishTextGenerator(random);
    }

    protected void randomDigits(StringBuilder sb, int len, boolean noZeroStart, Random random) {
        for (int i = 0; i < len; i++) {
            int digit;
            while (true) {
                digit = random.nextInt(10);
                if (i == 0)
                    if (noZeroStart && digit == 0)
                        continue;
                break;
            }
            char ch = (char) ('0' + digit);
            sb.append(ch);
        }
    }

}
