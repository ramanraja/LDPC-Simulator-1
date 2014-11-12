/**
 * Modified PEG algorithm for building LDPC codes
 * This can generate irregular graphs.
 * Subclassed from Generator
 * Large girth is built into the construction
 */
import java.io.*;
import java.util.BitSet;

public class ModifiedPeg extends Generator
{
	DegreeDistribution distribution;
	int[] leftDistance;  // the distance function is the longest cycle-free path from a root node
	int[] rightDistance;
	
	public void init (int _rows, int _cols, int _colwt)
	{
		DegreeDistribution dist = new DegreeDistribution();
		int[] wt = {_colwt};
		double[] fract = {1.0};
		dist.init(wt, fract);
		init (_rows, _cols, dist);
	}
	
	public void init (int _rows, int _cols, DegreeDistribution _distribution)
	{
		// base class fields
		rows = _rows;
		cols = _cols;
		rowSet = new int[rows][];
		colSet = new int[cols][];
		// sub class fields
		distribution = _distribution;
		leftDistance = new int[cols];
		rightDistance = new int[rows];	
	}

	/**
	 * This is the entry point for making all connections
	 */
	public void generate() throws Exception
	{
		G.itraceln ("Making connections...");
		int[] colwts = distribution.getNodeDegrees(cols);
		for (int c=0; c<cols; c++)
		{
			//G.traceln ("\nNode "+ c +" :");
			//printConnections();
			int row = findLowestDegreeCheckNode(); // first edge of this node
			//G.trace("Lowest degree=" +row +" ");
			connect (c, row);
			for (int w=1; w<colwts[c]; w++) // the remaining edges
			{
				buildTree(c);
				//printDistances();
				int row2 = findMaximumGirthCheckNode();  
				connect (c, row2);			
			}
		}
		cycleCheck(); // this is normally not needed for PEG of large n
	}
	
	public void buildTree (int rootNode)
	{
		BitSet leftCompleted = new BitSet(cols);
		BitSet rightCompleted = new BitSet(rows);
		
		for (int r=0; r<rows; r++)
			rightDistance[r] = -1;
		for (int c=0; c<cols; c++)
			leftDistance[c] = -1;
		
		leftDistance[rootNode]=0;
 		// stop after reaching a fixed depth - necessary for large graphs
		for (int depth=0; depth<G.MAX_DEPTH; depth++)
		{
			for (int c=0; c<cols; c++)
			{
				if (leftCompleted.get(c) || leftDistance[c] < 0) 
					continue;
				for (int i=0; i<colSet[c].length; i++)
				{
					if (rightDistance[colSet[c][i]] < 0)
						rightDistance[colSet[c][i]] = leftDistance[c]+1;
				}
				leftCompleted.set(c);
			}
			
			for (int r=0; r<rows; r++)
			{
				if (rightCompleted.get(r) || rightDistance[r] < 0) 
					continue;
				for (int i=0; i<rowSet[r].length; i++)
				{
					if (leftDistance[rowSet[r][i]] < 0)
						leftDistance[rowSet[r][i]] = rightDistance[r]+1;
				}
				rightCompleted.set(r);
			}
		}	
	}
	
	protected int findLowestDegreeCheckNode ()
	{
		int node=0;
		for (int r=0; r<rows; r++)
		{
			if (rowSet[r] == null) // no connections yet
				return r;
			if (rowSet[r].length < rowSet[node].length)
				node = r;
		}
		return node;
	}
	
	protected int findMaximumGirthCheckNode()
	{
		// Find if there is any node unreachable from the root
		int unconnectedNode = -1;
		for (int r=0; r<rows; r++)
		{
			if (rightDistance[r] < 0)  // -1 indicates virgin node unreachable from the current root node
			{
				unconnectedNode = r;
				break;
			}
		}
		if (unconnectedNode >= 0)
		{
			for (int r=unconnectedNode; r<rows; r++)
			{
				if (rightDistance[r] < 0)  // another unconnected node 
				{
					if (rowSet[r]==null) // zero degree node
					{
						//G.trace("Virgin node=" +r +" ");
						return r;
					}
					if (rowSet[r].length < rowSet[unconnectedNode].length)
						unconnectedNode = r;
				}
			}
			//G.trace("Unconnected node=" +unconnectedNode +" ");
			return unconnectedNode;
		}
		
		// find the node with the largest girth value
		int maxGirthNode = 0;
		for (int r=0; r<rows; r++)
		{
			if (rightDistance[r] > rightDistance[maxGirthNode])
				maxGirthNode = r;
		}
		int minWeightNode = maxGirthNode;
		for (int r=0; r<rows; r++)
		{
			if (rightDistance[r] != rightDistance[maxGirthNode])
				continue;
			if (rowSet[r].length < rowSet[minWeightNode].length)
				minWeightNode = r;
		}
		//G.traceln("Max girth=" +rightDistance[minWeightNode] +" for node " +minWeightNode +" ");
		if (rightDistance[minWeightNode] < 4)
		{
			G.traceln ("****** NOT ENOUGH GIRTH !  ******");
			//throw new RuntimeException ("Girth is too low");
		}
		return minWeightNode;
	}
	
	protected void connect (int col, int row)
	{
		//G.trace("\nConnecting:"+col +"-" +row +"; ");
		
		if (rowSet[row] == null)
		{
			rowSet[row] = new int[1];
			rowSet[row][0] = col;
		}
		else
		{
			int[] tmp = new int[rowSet[row].length+1];
			int i=0;
			for ( ; i<rowSet[row].length; i++)
				tmp[i] = rowSet[row][i]; // TODO : make this efficient
			tmp[i] = col;
			rowSet[row] = tmp;
		}
		
		if (colSet[col] == null)
		{
			colSet[col] = new int[1];
			colSet[col][0] = row;
		}
		else
		{
			int[] tmp = new int[colSet[col].length+1];
			int i=0;
			for ( ; i<colSet[col].length; i++)
				tmp[i] = colSet[col][i];
			tmp[i] = row;
			colSet[col] = tmp;
		}
	}
	
	public void printDistances ()
	{
		if (G.silent) return;
		G.traceln ("\nright distances:");
		for (int r=0; r<rows; r++)
			G.trace(rightDistance[r]+ " ");
		G.traceln ("\nleft distances:");
		for (int c=0; c<cols; c++)
			G.trace(leftDistance[c]+ " ");
		G.traceln();
	}

	public void printDegrees()
	{
		if (G.silent) return;
		G.traceln ("\n " +rows +" rows, " +cols +" cols");
		G.traceln("\nDistribution : " +distribution.toString());
		
		int rowEdges=0;
		G.traceln("\nRow  weights :");
		for (int r=0; r<rows; r++)
		{
			G.trace(rowSet[r].length +" ");
			rowEdges += rowSet[r].length;
			if ((r+1)%25==0)
				G.traceln();
		}
		G.traceln ("\nEdges : " +rowEdges);
	}
	
	public int[] getHistogram()
	{
		int maxDegreeRow=0;
		for (int r=0; r<rows; r++)
		{
			if (rowSet[r].length > rowSet[maxDegreeRow].length)
				maxDegreeRow = r;
		}
		int[] degrees = new int[rowSet[maxDegreeRow].length+1];
		for (int r=0; r<rows; r++)
			++degrees[rowSet[r].length];
		return degrees;
	}
	
	public void printHistogram()
	{	
		if (G.silent) return;
		int[] degrees = getHistogram();
		G.traceln ("\nRight distribution :");
		for (int i=0; i<degrees.length; i++)
			G.traceln ("degree "+i +": " +degrees[i]);
	}
	
	public void exportHistogram (String fileName) throws Exception
	{
		PrintWriter writer = new PrintWriter(fileName);
		int[] degrees = getHistogram();
		String str;
		for (int i=0; i<degrees.length; i++)
		{
			str = i +" " +degrees[i]+ "\n";  // the new line is mandatory
			writer.write (str);
		}
		writer.flush();
		writer.close();
	}
	
	public String toString()
	{
		return ("PEG: " +rows +" rows; " +cols +" cols; Distribution : "+distribution.toString());
	}
	
	public static void main (String[] args) throws Exception
	{
		ModifiedPeg p = new ModifiedPeg();
		p.init(600,1000,4);
		p.generate();
		p.printConnections();
		//p.printDegrees();
		p.printHistogram();
		//p.exportHistogram("histo.txt");
		p.exportSparseMatrix ("H.txt");
		G.traceln ("Done !");
	}
}
