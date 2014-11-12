
public class CodeFactory 
{
	int m1,m2, n;
	int colwt1, colwt2;
	int rowwt1, rowwt2;
	int[] allowedDegrees1;
	int[] allowedDegrees2;
	double[] nodeFractions1;
	double[] nodeFractions2;
	
	public void init(Initializer ini)
	{
		n = ini.getCols();
		m1 = ini.getRows1();  // TODO : parameterize these two functions into one
		m2 = ini.getRows2();
		colwt1 = ini.getColwt1();
		colwt2 = ini.getColwt2();
		rowwt1 = ini.getRowwt1();
		rowwt2 = ini.getRowwt2();		
		allowedDegrees1 = ini.getAllowedDegrees1();
		allowedDegrees2 = ini.getAllowedDegrees2();
		nodeFractions1 = ini.getNodeFractions1();
		nodeFractions2 = ini.getNodeFractions2();
	}
	
	/**
	 * Gets the generator for the top matrix H1 or the bottom matrix H2
	 * @param hIndex if 0, generator for H1. If 1, generator for H2.
	 * @return the generator object that can produce the parity matrix 
	 */
	public Generator getGenerator(int hIndex) throws Exception
	{
		Generator g;
		if (hIndex == 0)
		{
//			g = new Gallager();
//			g.init (n, colwt1, rowwt1);
//			g = new Peg();
//			g.init(m1, n, colwt1);
			g = new ModifiedPeg ();
			DegreeDistribution dist = new DegreeDistribution();
			dist.init (allowedDegrees1, nodeFractions1);
			g.init (m1, n, dist);
		}
		else
		{
//			g = new ModifiedGallager();
//			g.init (n, colwt2, rowwt2);
//			g = new Peg();
//			g.init(m2, n, colwt2);
			g = new ModifiedPeg ();
			DegreeDistribution dist = new DegreeDistribution();
			dist.init (allowedDegrees2, nodeFractions2);
			g.init (m2, n, dist);
		}
		return g;
	}

}
