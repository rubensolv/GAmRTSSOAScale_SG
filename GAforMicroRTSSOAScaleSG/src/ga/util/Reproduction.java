package ga.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import ai.ScriptsGenerator.TableGenerator.FunctionsforGrammar;
import ga.ScriptTableGenerator.ChromosomeScript;
import ga.ScriptTableGenerator.ScriptsTable;
import ga.config.ConfigurationsGA;
import ga.model.Chromosome;
import ga.model.Population;
import util.sqlLite.UCB_Facade;

public class Reproduction {

	static Random rand = new Random();

	List<Map.Entry<Chromosome, BigDecimal>> parents;
	ScriptsTable scrTable;
	FunctionsforGrammar functions;
	
	private String pathTableScripts;
	public Reproduction(List<Map.Entry<Chromosome, BigDecimal>> parents,ScriptsTable scrTable, String pathTableScripts)
	{
		this.parents=parents;
		this.scrTable=scrTable;
		this.pathTableScripts=pathTableScripts;
	}
	public Population UniformCrossover()
	{
		Population newGeneration;
		HashMap<Chromosome, BigDecimal> newChromosomes =new HashMap<Chromosome, BigDecimal>();		
		while(newChromosomes.size()<ConfigurationsGA.SIZE_POPULATION-ConfigurationsGA.SIZE_ELITE)
		{
			//here we shuffle the list of parents in order to select always two different parents to reproduce
			Collections.shuffle(parents);
			Chromosome parent1=parents.get(0).getKey();
			Chromosome parent2=parents.get(1).getKey();
			Chromosome child= new Chromosome();

			//The uniform crossover add to the son one of the parents gene for each position (selected randomly)
			int sizeParent1=parent1.getGenes().size();
			int sizeParent2=parent2.getGenes().size();
			int maxSize=Math.max(sizeParent1, sizeParent2);

			for(int i=0;i<maxSize;i++)
			{
				int newGen=rand.nextInt(2)+1;
				//if random was 1, add the parent1 gene, if was 2 add the parent2 gene.
				if(newGen==1)
				{
					if(i<sizeParent1)
						child.addGene(parent1.getGenes().get(i));
				}
				else
				{
					if(i<sizeParent2)
						child.addGene(parent2.getGenes().get(i));
				}
			}
			
			//The next method is just for avoiding infinite loops, adding a random element if
			//one with the same key was already added (this can happen because sometimes the resulting
			//element has the same KEY, and produce that the size of the map be always the same) 
			if(newChromosomes.containsKey(child))
			{
				Chromosome tChom = new Chromosome();
				int sizeCh=rand.nextInt(ConfigurationsGA.SIZE_CHROMOSOME)+1;
				for (int j = 0; j < sizeCh; j++) {
					tChom.addGene(rand.nextInt(scrTable.getCurrentSizeTable()));
				}
				newChromosomes.put(tChom, BigDecimal.ZERO);
			}

			//here is added the child!
			newChromosomes.put(child, BigDecimal.ZERO);
		}
		newGeneration=new Population(newChromosomes);
		return newGeneration;
	}
	
	public Population Crossover()
	{
		Population newGeneration;
		HashMap<Chromosome, BigDecimal> newChromosomes =new HashMap<Chromosome, BigDecimal>();		
		while(newChromosomes.size()<(ConfigurationsGA.SIZE_POPULATION-ConfigurationsGA.SIZE_ELITE-ConfigurationsGA.SIZE_INVADERS))
		{
			//here we shuffle the list of parents in order to select always two different parents to reproduce
			Collections.shuffle(parents);
			Chromosome parent1=parents.get(0).getKey();
			Chromosome parent2=parents.get(1).getKey();
			Chromosome child1= new Chromosome();
			Chromosome child2= new Chromosome();

			//The uniform crossover add to the son one of the parents gene for each position (selected randomly)
			int sizeParent1=parent1.getGenes().size();
			int sizeParent2=parent2.getGenes().size();
			
			int breakParent1;
			int breakParent2;
			
			if(sizeParent1>1)
			{
				breakParent1=rand.nextInt(sizeParent1+1);
			}
			else
			{
				breakParent1=0;
			}
			if(sizeParent2>1)
			{
				breakParent2=rand.nextInt(sizeParent2+1);
			}
			else
			{
				breakParent2=0;
			}
			
			ArrayList<Integer> p1sub1= new ArrayList<>();
			ArrayList<Integer> p1sub2= new ArrayList<>();
			ArrayList<Integer> p2sub1= new ArrayList<>();
			ArrayList<Integer> p2sub2= new ArrayList<>();

			
			
			for(int i=0;i<breakParent1;i++)
			{
				p1sub1.add(parent1.getGenes().get(i));
			}
			for(int i=breakParent1;i<sizeParent1;i++)
			{
				p1sub2.add(parent1.getGenes().get(i));
			}
			
			for(int i=0;i<breakParent2;i++)
			{
				p2sub1.add(parent2.getGenes().get(i));
			}
			for(int i=breakParent2;i<sizeParent2;i++)
			{
				p2sub2.add(parent2.getGenes().get(i));
			}	

			child1.getGenes().addAll(p1sub1);
			child1.getGenes().addAll(p2sub2);
			
			child2.getGenes().addAll(p2sub1);
			child2.getGenes().addAll(p1sub2);

			//The next method is just for avoiding infinite loops, adding a random element if
			//one with the same key was already added (this can happen because sometimes the resulting
			//element has the same KEY, and produce that the size of the map be always the same) 
			if(newChromosomes.containsKey(child1))
			{
				Chromosome tChom = new Chromosome();
				int sizeCh=rand.nextInt(ConfigurationsGA.SIZE_CHROMOSOME)+1;
				for (int j = 0; j < sizeCh; j++) {
					tChom.addGene(rand.nextInt(scrTable.getCurrentSizeTable()));
				}
				newChromosomes.put(tChom, BigDecimal.ZERO);
			}
			
			if(newChromosomes.containsKey(child2))
			{
				Chromosome tChom = new Chromosome();
				int sizeCh=rand.nextInt(ConfigurationsGA.SIZE_CHROMOSOME)+1;
				for (int j = 0; j < sizeCh; j++) {
					tChom.addGene(rand.nextInt(scrTable.getCurrentSizeTable()));
				}
				newChromosomes.put(tChom, BigDecimal.ZERO);
			}

			//here is added the child!
			if(child1.getGenes().size()!=0)
				newChromosomes.put(child1, BigDecimal.ZERO);
			
			if(child2.getGenes().size()!=0)
				newChromosomes.put(child2, BigDecimal.ZERO);
		}
		newGeneration=new Population(newChromosomes);
		return newGeneration;
	}

	@SuppressWarnings("unchecked")
	public Population mutation(Population p)
	{
		//This method replace each gene with a random script with a probability of 10%
		HashMap<Chromosome, BigDecimal> chromosomesMutated = new HashMap<>();
		for(Chromosome c : p.getChromosomes().keySet()){

			Chromosome newCh=new Chromosome();
			newCh.setGenes((ArrayList<Integer>) c.getGenes().clone());
			for(int i=0; i<newCh.getGenes().size();i++)
			{
				double mutatePercent = ConfigurationsGA.MUTATION_RATE;
				boolean m = rand.nextFloat() <= mutatePercent;

				if(m)
				{
					//newCh.getGenes().set(i, rand.nextInt(scrTable.getCurrentSizeTable()));
					
					//The next line is added in order to keep mutation of rules
					newCh.getGenes().set(i, mutationScript(p, newCh.getGenes().get(i)));
				}
			}
			chromosomesMutated.put(newCh, BigDecimal.ZERO);
		}
		p.setChromosomes(chromosomesMutated);
		return p;
	}
	
	public Population invaders(Population p)
	{
		HashMap<Chromosome, BigDecimal> newChromosomes = p.getChromosomes();
		
		Chromosome tChom;
		while (newChromosomes.size()< ConfigurationsGA.SIZE_POPULATION-ConfigurationsGA.SIZE_ELITE) {
			//gerar o novo cromossomo com base no tamanho
			tChom = new Chromosome();
			int sizeCh=rand.nextInt(ConfigurationsGA.SIZE_CHROMOSOME)+1;
			for (int j = 0; j < sizeCh; j++) {
				tChom.addGene(rand.nextInt(scrTable.getCurrentSizeTable()));
			}
			newChromosomes.put(tChom, BigDecimal.ZERO);
		}
		Population pop = new Population(newChromosomes);
		return pop;
	}
	
	//This method will return the new id script for mutate the porfolio o fscripts
	public int mutationScript(Population p, int genidScript)
	{
		functions=new FunctionsforGrammar();
		List<FunctionsforGrammar> basicFunctions=functions.getBasicFunctionsForGrammar();
		List<FunctionsforGrammar> conditionalFunctions=functions.getConditionalsForGrammar();
		
		
		String cromScript=cromosomeById(genidScript);
		String cromScriptAux=cromScript;
		cromScriptAux=cromScriptAux.replace("(for(u)", "");
		cromScriptAux=cromScriptAux.replace("for(u)", "");
		
		
		//cromScriptAux=cromScriptAux.replaceAll("[^0-9!]", " ");
	    String[] parts = cromScriptAux.trim().split("\\s+");
	    String[] news = new String[parts.length];
	       
	    for(int i=0;i<parts.length;i++)
	    {
		    parts[i]=removeFromBeggining(parts[i]);
		    parts[i]=removeFromLast(parts[i]);
	    }
		   
//	    for(int i=0;i<parts.length;i++)
//	    {
//	    	System.out.println(parts[i]);
//	    }
	    
	    news=chossingFromBag(news,parts,basicFunctions,conditionalFunctions);
	    
	    for(int i=0;i<parts.length;i++)
	    {
	    	double mutatePercent = ConfigurationsGA.MUTATION_RATE_RULE;
	    	boolean m = rand.nextFloat() <= mutatePercent;
	    	
	    	if(m)
	    		cromScript=replaceFromCompleteGrammar(parts[i], news[i], cromScript );
		
	    }
	    
	    cromScript=removingTrashFromGrammar(cromScript);
	    
		if(scrTable.getScriptTable().containsKey(cromScript))
		{
			return scrTable.getScriptTable().get(cromScript).intValue();			
		}
		else
		{
			int newId=scrTable.getScriptTable().size();
			scrTable.getScriptTable().put(cromScript, BigDecimal.valueOf(newId));
			scrTable.setCurrentSizeTable(scrTable.getScriptTable().size());
			addLineFile(newId+" "+cromScript);
			return newId;
		}
		
	}
	
	public static String removeFromBeggining(String s)
	{
		String cloneS = s;
		  
		while (cloneS.charAt(0)=='(' )
		{
			cloneS=cloneS.replaceFirst("\\(", "");
		}
		
		if(cloneS.startsWith("if"))
		{
			cloneS=cloneS.replaceFirst("if", "");
			if(cloneS.charAt(0)=='(')
			{
				cloneS=cloneS.replaceFirst("\\(", "");
			}
		}		

		return cloneS;
	}
	
	public String removeFromLast(String s)
	{
		String cloneS = s;
		while (cloneS.endsWith("))")) 
		{
			cloneS=cloneS.replaceFirst("\\)", "");
		}
		return cloneS;
	}
	
	public String removingTrashFromGrammar(String originalGrammar)
	{
		originalGrammar=originalGrammar.replace("NEW", "");		
		return originalGrammar;
	}
	
	public String replaceFromCompleteGrammar(String oldFunction, String newFunction, String originalGrammar)
	{
		originalGrammar=originalGrammar.replace(oldFunction, newFunction+"NEW");
		return originalGrammar;
	}
	
	public String[]  chossingFromBag(String[]  candidates,String[] originals, List<FunctionsforGrammar>basicFunctions, List<FunctionsforGrammar>conditionalFunctions)
	{
		ScriptsTable objScriptTable=new ScriptsTable("");
		boolean found=false;
		for (int i=0; i<originals.length;i++)
		{
			found=false;
			for (FunctionsforGrammar function:basicFunctions)
			{
				
				if(originals[i].startsWith(function.getNameFunction()))
				{
					
					//change with other basicFunction
					if(originals[i].contains(",u,") || originals[i].contains(",u)") || originals[i].contains("(u,"))
					{
						candidates[i]=objScriptTable.returnBasicFunctionClean(true);
					}
					else
					{
						candidates[i]=objScriptTable.returnBasicFunctionClean(false);
					}
					found=true;
					break;
				}
			}
			if(found==false)
			{
				for (FunctionsforGrammar function:conditionalFunctions)
				{
					if(originals[i].startsWith(function.getNameFunction()))
					{
						//change with other basicFunction
						if(originals[i].contains(",u,") || originals[i].contains(",u)") || originals[i].contains("(u,"))
						{
							candidates[i]=objScriptTable.returnConditionalClean(true);
						}
						else
						{
							candidates[i]=objScriptTable.returnConditionalClean(false);
						}
						break;
					}
				}
				
			}
			
		}
		
		return candidates;
	}
	
	//This method will be expensive if the hashmap its too big
	public String cromosomeById(int genidScript)
	{
        for (Entry<String, BigDecimal> entry : scrTable.getScriptTable().entrySet()) {
            if (entry.getValue().equals(BigDecimal.valueOf(genidScript))) {
                return entry.getKey();
            }
        }
        return null;
	}
	
	public Population IncreasePopulation(Population pop){

		HashMap<Chromosome, BigDecimal> chromosomesMutated = new HashMap<>();
		for(Chromosome c : pop.getChromosomes().keySet()){

			Chromosome newCh=new Chromosome();
			newCh.setGenes((ArrayList<Integer>) c.getGenes().clone());
			Chromosome origCh=new Chromosome();
			origCh.setGenes((ArrayList<Integer>) c.getGenes().clone());

			double IncreasePercent = ConfigurationsGA.INCREASING_RATE;
			boolean m = rand.nextFloat() <= IncreasePercent;

			if(m)
			{
				//newCh.getGenes().set(i, rand.nextInt(ConfigurationsGA.QTD_SCRIPTS));
				newCh.getGenes().add(rand.nextInt(scrTable.getCurrentSizeTable()));
				chromosomesMutated.put(newCh, BigDecimal.ZERO);
			}
			
			chromosomesMutated.put(origCh, BigDecimal.ZERO);
		}
		pop.setChromosomes(chromosomesMutated);
		return pop;
		
	}
	
	public int UCB1(){	
		
		double bestUCB1=Double.NEGATIVE_INFINITY;
		int bestidRule=0;
		for(int i=0;i<ConfigurationsGA.QTD_RULES;i++)
		{
			//weight/reward
			double reward=UCB_Facade.getAverageValueFromRule(i);
			//total of matches
			double ntotalMatches=RunGA.numberCallsUCB11;
			//Number of calls
			double numberCallsRule=RunGA.frequencyIdsRulesForUCB[i];
			
			double UCB1Rule=reward+Math.sqrt((2*Math.log(ntotalMatches))/numberCallsRule);

			if(UCB1Rule>bestUCB1)
			{
				bestUCB1=UCB1Rule;
				bestidRule=i;
			}
		}
		RunGA.frequencyIdsRulesForUCB[bestidRule]=RunGA.frequencyIdsRulesForUCB[bestidRule]+1;
		RunGA.numberCallsUCB11++;
		return bestidRule;
		
	}
	
	public Population DecreasePopulation(Population pop){

		HashMap<Chromosome, BigDecimal> chromosomesMutated = new HashMap<>();
		for(Chromosome c : pop.getChromosomes().keySet()){

			Chromosome newCh=new Chromosome();
			newCh.setGenes((ArrayList<Integer>) c.getGenes().clone());
			Chromosome origCh=new Chromosome();
			origCh.setGenes((ArrayList<Integer>) c.getGenes().clone());

			double decreasePercent = ConfigurationsGA.DECREASING_RATE;
			boolean m = rand.nextFloat() <= decreasePercent;

			if(m && newCh.getGenes().size()>=2)
			{
				//newCh.getGenes().set(i, rand.nextInt(ConfigurationsGA.QTD_SCRIPTS));
				newCh.getGenes().remove(rand.nextInt(newCh.getGenes().size()));
				chromosomesMutated.put(newCh, BigDecimal.ZERO);
			}
			
			chromosomesMutated.put(origCh, BigDecimal.ZERO);
		}
		pop.setChromosomes(chromosomesMutated);
		return pop;
		
	}
	
	public Population RemoveCopies(Population p){ 
		
		//This method replace each gene with a random script with a probability of 10%
		HashMap<Chromosome, BigDecimal> chromosomesMutated = new HashMap<>();
		for(Chromosome c : p.getChromosomes().keySet()){

			Chromosome newCh=new Chromosome();
			newCh.setGenes((ArrayList<Integer>) c.getGenes().clone());
			// The next code block is for removing duplicates in the cromosome.
			//List<String> al = new ArrayList<>();
			// add elements to al, including duplicates
			
			newCh.setGenes(new ArrayList<Integer>(new LinkedHashSet<Integer>(newCh.getGenes())));
//			Set<Integer> hs = new HashSet<>();
//			hs.addAll(newCh.getGenes());
//			newCh.getGenes().clear();
//			newCh.getGenes().addAll(hs);	
			
			//The next method is just for avoiding infinite loops, adding a random element if
			//one with the same key was already added (this can happen because sometimes the resulting
			//element has the same KEY, and produce that the size of the map be always the same) 
			if(chromosomesMutated.containsKey(newCh))
			{
				Chromosome tChom = new Chromosome();
				int sizeCh=rand.nextInt(ConfigurationsGA.SIZE_CHROMOSOME)+1;
				for (int j = 0; j < sizeCh; j++) {
					tChom.addGene(rand.nextInt(scrTable.getCurrentSizeTable()));
				}
				chromosomesMutated.put(tChom, BigDecimal.ZERO);
			}
			else
			{
				chromosomesMutated.put(newCh, BigDecimal.ZERO);
			}			
			
		}
		p.setChromosomes(chromosomesMutated);
		return p;
		
	}
	
	public void addLineFile(String data) {
    try{    

        File file =new File(pathTableScripts+"ScriptsTable.txt");    

        //if file doesnt exists, then create it    
        if(!file.exists()){    
            file.createNewFile();      
        }    

        //true = append file    
            FileWriter fileWritter = new FileWriter(file,true);        
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(data);
            bufferWritter.newLine();
            bufferWritter.close();
            fileWritter.close();  

    }catch(Exception e){    
        e.printStackTrace();    
    } 
	}

}
