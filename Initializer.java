import java.io.FileInputStream;
import java.util.Properties;
import java.util.StringTokenizer;

public class Initializer 
{
	int numTrials = 10;    // each experiment will be repeated this many times
	int zoomNumTrials = 20;    // number of trials in zoom mode
	int algorithm = G.PEG;
	int rows1, rows2;    // number of rows of H1 and H2 matrices
	int cols;            // the (common) number of colums of H1 and H2
	int rowwt1, rowwt2;  // row weights of H1 and H2
	int colwt1, colwt2;  // column weights of H1 and H2
	float startProb, endProb, probStep; // channel error probability range
	float zoomProbStep;
	public int[] allowedDegrees1;
	public double[] nodeFractions1;   // fraction of total nodes with the given degree
	public int[] allowedDegrees2;
	public double[] nodeFractions2;   // fraction of total nodes with the given degree
	
	public void init (String fileName) throws Exception
	{
        Properties prop = new Properties();
        prop.load (new FileInputStream(fileName));
        String str;

        str = prop.getProperty ("block_length", "24").trim(); 
        cols = Integer.parseInt(str);
        str = prop.getProperty ("check_length1", "10").trim(); 
        rows1 = Integer.parseInt(str);
        str = prop.getProperty ("check_length2", "8").trim(); 
        rows2 = Integer.parseInt(str);
        
        str = prop.getProperty ("row_weight1", "6").trim(); 
        rowwt1 = Integer.parseInt(str);
        str = prop.getProperty ("row_weight2", "6").trim(); 
        rowwt2 = Integer.parseInt(str);
        str = prop.getProperty ("column_weight1", "3").trim(); 
        colwt1 = Integer.parseInt(str);
        str = prop.getProperty ("column_weight2", "3").trim(); 
        colwt2 = Integer.parseInt(str);
        
        str = prop.getProperty ("silent_mode", "false").trim(); 
        G.silent = (str.equalsIgnoreCase("true") ? true : false);
        str = prop.getProperty ("generate_random", "true").trim(); 
        G.generateRandomH = (str.equalsIgnoreCase("true") ? true : false);
        str = prop.getProperty ("random_seed", "13").trim(); 
        G.RAND_SEED = Integer.parseInt(str);
        
        str = prop.getProperty ("start_prob", "0.1").trim(); 
        startProb = Float.parseFloat(str);
        str = prop.getProperty ("end_prob", "0.5").trim(); 
        endProb = Float.parseFloat(str);
        str = prop.getProperty ("prob_step", "0.1").trim(); 
        probStep = Float.parseFloat(str);
        str = prop.getProperty ("num_trials", "10").trim(); 
        numTrials = Integer.parseInt(str);
        str = prop.getProperty ("zoom_prob_step", "0.05").trim(); 
        zoomProbStep = Float.parseFloat(str);
        str = prop.getProperty ("zoom_num_trials", "20").trim(); 
        zoomNumTrials = Integer.parseInt(str);
        
        str = prop.getProperty ("allowed_degrees1", "3").trim(); 
        allowedDegrees1 = parseDegrees (str);
        str = prop.getProperty ("allowed_degrees2", "3").trim(); 
        allowedDegrees2 = parseDegrees (str);
        str = prop.getProperty ("node_fractions1", "1.0").trim(); 
        nodeFractions1 = parseNodeFractions (str);
        str = prop.getProperty ("node_fractions2", "1.0").trim(); 
        nodeFractions2 = parseNodeFractions (str);
	}
	
	protected int[] parseDegrees (String str)
	{
		StringTokenizer tok = new StringTokenizer(str, ",");
		int count = tok.countTokens();
		int[] degrees = new int[count];
		for (int i=0; i<count; i++)
			degrees[i] = Integer.parseInt(tok.nextToken());
		return degrees;
	}

	protected double[] parseNodeFractions (String str)
	{
		StringTokenizer tok = new StringTokenizer(str, ",");
		int count = tok.countTokens();
		double[] fractions = new double[count];
		for (int i=0; i<count; i++)
			fractions[i] = Double.parseDouble(tok.nextToken());
		return fractions;
	}

	public int getAlgorithm()
	{ return algorithm; }
	
	public int getCols()
	{ return cols; }
	public int getRows1()
	{ return rows1; }
	public int getRows2()
	{ return rows2; }

	public int getRowwt1()
	{ return rowwt1; }
	public int getRowwt2() 
	{ return rowwt2; }
 	public int getColwt1()
	{ return colwt1; }
	public int getColwt2() 
	{ return colwt2; }

	public float getStartProb() 
	{ return startProb; }
	public float getEndProb() 
	{ return endProb; }
	public float getProbStep() 
	{ return probStep; }
	public int getTrialCount()
	{ return numTrials;	}
	
	public float getZoomProbStep() 
	{ return zoomProbStep; }
	public int getZoomTrialCount()
	{ return zoomNumTrials;	}
	
	public int[] getAllowedDegrees1()
	{ return allowedDegrees1; }
	public int[] getAllowedDegrees2()
	{ return allowedDegrees2; }
	public double[] getNodeFractions1()
	{ return nodeFractions1; }
	public double[] getNodeFractions2()
	{ return nodeFractions2; }
}




