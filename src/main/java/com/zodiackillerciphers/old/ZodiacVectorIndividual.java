package com.zodiackillerciphers.old;

import ec.EvolutionState;
import ec.vector.*;

public class ZodiacVectorIndividual extends IntegerVectorIndividual implements ZodiacIndividual {

	private CipherGene _gene;
	
	/* uses the mutations implemented in Zodiac.java. */
	/*public void defaultMutate(EvolutionState state, int thread) {
		IntegerVectorSpecies s = (IntegerVectorSpecies) species;
    if (s.mutationProbability>0.0)
    	if (state.random[thread].nextBoolean(s.mutationProbability)) {
    		Gene spawn = Zodiac.mutate(getGene());
    		genome = spawn.getGenome();
    	}
	}*/
	

	/* make a zodiac gene out of a ZodiacVectorIndividual */
	public CipherGene getGene() {
		if (_gene == null)
			return getGene(genome);
		else return _gene;
	}	
	
	public static CipherGene getGene(int[] genome) {
	  String decoder = "";
	  for(int x=0; x<genome.length; x++)
	      decoder += (char)genome[x];
	  
	  CipherGene gene = new CipherGene(decoder);
	  return gene;
	}

	public void setGene(CipherGene gene) {
		_gene = gene;
	}
	
  public String genotypeToStringForHumans()
  {
  	return getGene().getDebugLine();
  }


}

