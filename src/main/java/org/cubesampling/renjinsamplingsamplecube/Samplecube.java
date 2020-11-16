package org.cubesampling.renjinsamplingsamplecube;

import java.util.List;

/**
 * Calculates cube sampling
 */
public interface Samplecube {
    List<Double> cubeSampling(List<List<Double>> variableConstraints,List<Double> weightList,String order, String method );
}
