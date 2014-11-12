 /**
 * Generates an LDPC code pair by Gallager or PEG algorithm.
 * A modified PEG algorithm is introduced; this can generate irregular graphs.
 * Generator class has been made more uniform with the addition of init(int,int,int) abstract method.
 *   Simulates a BEC, and decodes the all zero code word.
 *   Computes the colum rank of H2 corresponding to the stopping set in H1.
 *   Repeats the experimet a number of times for each probability & finds cumulative/ average values.
 *   Detects the threshold of the code H1, zooms in with fine grained probability step, and repeats the trials.
 *   Logs all results in a CSV file that can be exported to XL
 * @author Rajaraman
 */
import java.util.BitSet;
import java.util.Calendar;

public class Controller 
{
	Initializer ini;
	Logger logger;
	long snapShotTime;
	float errorKneeProb; // the prob at which decoding errors start appearing for the first time
	float errorPlateuProb; // where 90% of decoding attempts fail
	
	public static void main (String[] args) throws Exception
	{
		long startTime = Calendar.getInstance().getTimeInMillis();
		Controller c = new Controller();
		c.init("settings.txt");
		c.runSimulation();
		// the time in the Calendar object is FROZEN at the time of calling getInstance()
 		long endTime = Calendar.getInstance().getTimeInMillis();		
		long elapsed = endTime-startTime;
		G.itraceln("\nTotal Duration: "+elapsed/1000 +"." +elapsed%1000 +" sec");
		c.close();
		G.itrace ("Done !");	
	}
	
	public void close()
	{
		logger.close();
	}
	
	public void init(String iniFileName) throws Exception
	{
		this.ini = new Initializer();
		ini.init(iniFileName);
		logger = new Logger();
		logger.init();
	}
	
	public void runSimulation()throws Exception
	{
		CodeFactory factory = new CodeFactory();
		factory.init (ini);
		timeStamp();
		Generator g = factory.getGenerator(0);
		g.generate();
		G.itraceln(g.toString());
		logger.logln(g.toString());
		logger.clogln(g.toString());
		timeStamp("Generated Code 1");
		
		Generator g2 = factory.getGenerator(1);
		g2.generate();
		G.itraceln(g2.toString());
		logger.logln(g2.toString());
		logger.clogln(g2.toString());
		timeStamp("Generated Code 2");
		
		logger.logln ("EPROB,ECNT,RSLT,SSIZE,COLRNK");
		logger.clogln ("EPROB,DELTA,SSIZE,COLRNK");
		runSimulation (g, g2, ini.getStartProb(),ini.getEndProb(),ini.getProbStep(),ini.getTrialCount());
		// runSimulation sets the knee and plateu probabilities
		G.itraceln ("Error knee =" +errorKneeProb);
		G.itraceln ("Error plalteau =" +errorPlateuProb);
		logger.clogln ("Error knee =" +errorKneeProb);
		logger.clogln ("Error plalteau =" +errorPlateuProb);
		if (errorPlateuProb-errorKneeProb < ini.getProbStep())
		{
			G.itraceln ("-- Default Error knee is too small --");
			errorKneeProb -= ini.getProbStep();
			errorPlateuProb += ini.getProbStep();
		}
		logger.logln("Zoom");
		logger.clogln("Zoom");
		//float step = ini.getZoomProbStep();
		float step = (errorPlateuProb-errorKneeProb)/10.0f;
		G.itraceln ("Zoom probability step = "+step);
		runSimulation (g, g2, errorKneeProb, errorPlateuProb,step,ini.getZoomTrialCount());
		// runSimulation sets the knee and plateu probabilities
		G.itraceln ("Error knee =" +errorKneeProb);
		G.itraceln ("Error plateau =" +errorPlateuProb);
		logger.clogln ("Error knee =" +errorKneeProb);
		logger.clogln ("Error plateau =" +errorPlateuProb);	
	}
	
	/**
	 * Runs one complete cycle of simulation for a range of probabilities
	 * Note: This function sets the class variables errorKneeProb and errorPlateuProb
	 * @throws Exception
	 */
	protected void runSimulation (Generator g, Generator g2, float startProb, float endProb, float probStep, int numTrials) throws Exception
	{
		Channel chl = new Channel();
		chl.init(g.getColCount());
		Matrix mat = new Matrix();
		Matrix mat2 = new Matrix();
		TannerGraph tg = new TannerGraph();
		BitSet data = new BitSet(g.getColCount());  
		
		timeStamp();
		boolean allTrialsSucceeded = true; // flags to detect threshold
		boolean allTrialsFailed = false;  
		for (float errProb=startProb; errProb < endProb; errProb = addRoundOff(errProb,probStep))
		{
			G.itraceln ("\nerror prob= " +errProb);
			int stopSetSize = 0;
			int columnRank = 0;
			int failureCount = 0;
			int repeatCount = 0;
			for (int trial=0; trial<numTrials; trial++)
			{
				// NOTE : matrix and tannergraph have to be initiated every time, 
				// because the bitset is destroyed by rank finding
				mat.init(g.getRowCount(), g.getColCount(), g.exportToBitSet());
				mat2.init(g2.getRowCount(), g2.getColCount(), g2.exportToBitSet());
				tg.init (mat);
				data.clear(); // all-zero code word
				tg.setData(data);
				chl.setErasureProbability(errProb);
				tg.setErasures(chl.getErasures());
				//tg.dumpMatrix();
				
				boolean result = tg.decode();
				
				logger.log(chl.getErasureProbability());
				logger.log(chl.getErasureCount());
				if (result) 
				{
					repeatCount = 0;
					G.traceln ("Decoded successfully");
					logger.log ("success");
				}
				else
				{
					if (G.ARQ_MODE && repeatCount < G.MAX_ARQ)  
					{
						repeatCount++;
						trial--;  // that is all needed for ARQ !
						G.itrace ("* ");
					}
					else
					{
						repeatCount=0;  // maximum ARQ attempts exceeded
						failureCount++;
						G.traceln ("Decoding failed");
						logger.log ("failure");
						BitSet colmask = tg.getErasures();
						int ssSize = colmask.cardinality();
						stopSetSize += ssSize;
						G.traceln ("Stopping set (cardinality=" +ssSize +")");
						//G.traceln (colmask.toString());
						logger.log (ssSize);
						mat2.selectColumns(colmask); // the original H is DESTROYED
						int rank = mat2.getRank();  // the original H is DESTROYED
						columnRank += rank;
						G.traceln ("Rank of stopping set colums=" +rank);
						logger.log(rank);
					}
				}
				logger.logln();
				//G.activity();
			} // for- trial 
			stopSetSize = (int)((float)stopSetSize/numTrials +0.5); // average stopping set size over the trials
			columnRank = (int)((float)columnRank/numTrials +0.5);  // average col rank 
			logger.clog(errProb);
			logger.clog(errProb-startProb);
			logger.clog(stopSetSize);
			logger.clogln (columnRank);
			if (allTrialsSucceeded && failureCount > 0)  // we got the first taste of failure now
			{
				this.errorKneeProb = errProb;
				allTrialsSucceeded = false;
			}
			if (!allTrialsFailed && failureCount > 0.9*numTrials)
			{
				this.errorPlateuProb = errProb;
				allTrialsFailed = true;
			}
		} // for- error probability
		timeStamp("End of Simulation Runs");
	}
	
	private static float addRoundOff (float a, float b)
	{
		double DELTA = 0.0005;
		double sum = a+b+DELTA;
		int tmp = (int)(sum*1000);
		return (float) tmp/1000.0f;
	}
	public void timeStamp ()
	{
		snapShotTime = Calendar.getInstance().getTimeInMillis();
	}

	public void timeStamp (String context)
	{
		long currentTime = Calendar.getInstance().getTimeInMillis();
		long elapsed = currentTime-snapShotTime;
		G.itraceln("\n" +context +": "+ elapsed/1000 +"." +elapsed%1000 +" sec");
		snapShotTime = currentTime;
	}
}
