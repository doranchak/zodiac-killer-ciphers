package com.zodiackillerciphers.old;

import ec.EvolutionState;
import ec.vector.IntegerVectorIndividual;

public class RemoveHomophonesIndividual extends IntegerVectorIndividual {

	@Override
	public String genotypeToStringForHumans() {
		// TODO Auto-generated method stub
		return super.genotypeToStringForHumans();
	}

	@Override
	public void reset(EvolutionState state, int thread) {
        for(int x=0;x<genome.length;x++)
            genome[x] = 0; // initial individual is the original cipher with no swaps
	}

}
