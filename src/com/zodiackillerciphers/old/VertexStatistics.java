package com.zodiackillerciphers.old;

import ec.EvolutionState;
import ec.simple.SimpleStatistics;
import ec.util.Output;

import gnu.trove.*;

public class VertexStatistics extends SimpleStatistics {

	public void postEvaluationStatistics(EvolutionState state) {
		super.postEvaluationStatistics(state);
		
		
		THashMap<Double, Integer> stats = new THashMap<Double, Integer>(); 
		float sum = 0;
		double f;
		for (int i=0; i<state.population.subpops[0].individuals.length; i++) {
			f = state.population.subpops[0].individuals[i].fitness.fitness();
			sum += f;
			if (stats.get(f) == null)
				stats.put(f, 0);
			stats.put(f, stats.get(f)+1);
		}
		float mean = sum / state.population.subpops[0].individuals.length;
        state.output.println("Mean: " + mean,Output.V_NO_GENERAL,statisticslog);
        for (Double key : stats.keySet())
            state.output.println("Fit: " + key + ", total: " + stats.get(key),Output.V_NO_GENERAL,statisticslog);
        	
		
        
        
	}

}
