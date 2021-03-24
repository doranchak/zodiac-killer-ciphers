package com.zodiackillerciphers.old;

import ec.select.BestSelection;
import ec.steadystate.SteadyStateBSourceForm;
import ec.steadystate.SteadyStateEvolutionState;
import ec.multiobjective.*;

public class SteadyStateBestSelection extends BestSelection implements SteadyStateBSourceForm {
  public void individualReplaced(final SteadyStateEvolutionState state,
      final int subpopulation,
      final int thread,
      final int individual)
  { 
  	//state.output.message("individualreplaced " + subpopulation + "/" + individual + ", fit " + ((MultiObjectiveFitness) state.population.subpops[0].individuals[individual].fitness).multifitness[0]);
  	
  }

public void sourcesAreProperForm(final SteadyStateEvolutionState state)
	{ return; }

}
