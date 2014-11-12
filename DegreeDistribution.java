
public class DegreeDistribution 
{
	public int[] allowedDegrees;
	public double[] nodeFractions;   // fraction of total nodes with the given degree
	/**
	 * @param degrees the node degrees
	 * @param fractions the fraction of nodes with the given degree : from node perspective
	 */
	public void init (int[] _allowedDegrees, double[] _nodeFractions)
	{
		if (_allowedDegrees.length != _nodeFractions.length)
			throw new IllegalArgumentException ("Invalid degree distribution polynomial");
		double sum=0.0;
		for (int i=0; i<_nodeFractions.length; i++)
			sum += _nodeFractions[i];
		if (Math.abs(1.0-sum) > G.ROUNDOFF_ERROR)
			throw new IllegalArgumentException ("Degree distribution does not add up to 1.0");
		allowedDegrees = _allowedDegrees;
		nodeFractions = _nodeFractions;
	}
	
	public int[] getNodeDegrees (int totalNodes)
	{
		G.trace ("Node Count : ");
		int[] nodeDegrees = new int[totalNodes];
		//int[] count = new int[allowedDegrees.length]; // number of nodes with the given degree
		int index=0;
		for (int i=0; i<allowedDegrees.length; i++)
		{
			 int count = (int)(totalNodes * nodeFractions[i]);
			 G.trace (count +", ");
			 for (int j=0; j<count; j++)
			 {
				 nodeDegrees[index] = allowedDegrees[i];
				 index++;
				 if (index >= totalNodes) break;
			 }
		}
		G.itraceln();
		// last few nodes may still remain because of round off error
		if (index < totalNodes)
		{
			G.traceln("Auto correcting round off error: index=" +index +", total="+totalNodes);
			for (; index< totalNodes; index++)
				nodeDegrees[index] = allowedDegrees[allowedDegrees.length-1];
		}
		return nodeDegrees;
	}
	
	public void dump()
	{
		G.trace ("Allowed degrees : ");
		for (int i=0; i<allowedDegrees.length; i++)
			G.trace (allowedDegrees[i] +", ");
		G.traceln();
		G.trace ("Fraction of degrees : ");
		for (int i=0; i<nodeFractions.length; i++)
			G.trace (nodeFractions[i] +", ");
		G.traceln();
	}
	
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		for (int i=0; i<nodeFractions.length; i++)
		{
			builder.append (G.round(nodeFractions[i]));
			builder.append (" X^");
			builder.append (allowedDegrees[i]);
			if (i != nodeFractions.length-1)
				builder.append (" + ");
		}
		return builder.toString();
	}
	

	
	public void printHistogram (int[] distribution)
	{
//		for (int k=0; k<distribution.length; k++)
//		{
//			G.trace (distribution[k] +" ");
//			if ((k+1)%10==0)
//				G.traceln();
//		}
//		G.traceln();
		
		G.traceln ("Histogram :");
		G.traceln ("Degree\tNodes");
		int count=-1;
		int i=0;
		for ( ; i<distribution.length; i++)
		{
			count++;
			if (i!=0 && (distribution[i] != distribution[i-1]))
			{
				G.traceln (distribution[i-1] +"\t" +count);
				count=0;
			}
		}
		G.traceln (distribution[i-1] +"\t" +(count+1));
	}
	
	public static void main (String[] args)
	{
		DegreeDistribution dd = new DegreeDistribution();
		int[] degrees = {3,5,7,8};
		double[] fractions = {0.123112, 0.345678,0.2223333, (1-0.123112-0.345678-0.2223333)};
		dd.init(degrees, fractions);
		System.out.println(dd.toString());
		dd.dump();
		dd.printHistogram(dd.getNodeDegrees(100));	
	}
}
