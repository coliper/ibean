package org.coliper.ibean.util;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CycleSafeHashCodeBuilder extends HashCodeBuilder {

    private static final RecursionCycleDetector<HashCodeBuilder> cycleDetector =
            new RecursionCycleDetector<HashCodeBuilder>(null);

    @Override
    public HashCodeBuilder append(Object object) {
        cycleDetector.executeWithCycleDetection(object, () -> super.append(object));
        return this;
    }

}
