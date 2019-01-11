package ga.config;

public final class ConfigurationsGA {
	public final static int SIZE_CHROMOSOME = 10;
	public final static int SIZE_POPULATION = 100;
	public final static int NUMBER_JOBS = 39;
	public final static int SIZE_ELITE = 10;
	public final static int K_TOURNMENT = 25;
	public final static int SIZE_PARENTSFORCROSSOVER = 40;
	public final static double MUTATION_RATE = 0.1;
	public final static double MUTATION_RATE_RULE = 0.1;
	public final static double MUTATION_ORDER_RATE= 0.1;
	public final static boolean INCREASING_INDEX=false;
	public final static double INCREASING_RATE = 0.2;
	public final static double DECREASING_RATE = 0.2;
	public final static int QTD_ENEMIES_SAMPLE_RANDOM=5;
	public final static int QTD_ENEMIES_SAMPLE_ELITE=10;
	//---------------------------------------------------------------------------------
	//Parameters for the script table
	//public final static int QTD_RULES = 2363793;
	public final static int QTD_RULES = 60088;	
	public final static int SIZE_CHROMOSOME_SCRIPT = 10;
	public final static int SIZE_TABLE_SCRIPTS = 1000;
	
	//---------------------------------------------------------------------------------
	//Controle do dispositivo de parada do GA
	//Parametro: 0 = Tempo; 1 = Gerações
	public final static int TYPE_CONTROL = 1;
	//tempo, em horas, que o GA será executado
	public final static int TIME_GA_EXEC = 13;
	//número de gerações que serão executadas
	public final static int QTD_GENERATIONS = 1000;
	//---------------------------------------------------------------------------------
	public final static boolean RESET_ENABLED = true;
	public final static boolean MUTATION_ORDER_ENABLED = true;
	public final static boolean curriculum=true;
	
}
