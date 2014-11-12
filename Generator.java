 /**
  * Base class for different types of parity matrix generators
  * @author Rajaraman
 */
import java.util.*;
import java.io.*;

public abstract class Generator 
{
	protected int rows;   // number of rows in H
	protected int cols;   // number of columns in H
	protected int[][] rowSet;
	protected int[][] colSet;
	protected int[] cycleArray;
	
	abstract public void generate() throws Exception;
	abstract public void init (int param1, int param2, int param3);
	abstract public void init (int rows, int cols, DegreeDistribution distribution);
	
	public void printConnections()
	{
		//if (G.silent) return;
		printRowConnections();
		G.traceln("-------------------------------------------------------");
		printColConnections();
	}
	
	public void printRowConnections()
	{
		G.traceln ("Row perspective:");
		for (int r=0; r<rows; r++)
		{
			if (rowSet[r]==null) continue;
			G.trace(r +": ");
			for (int index=0; index <rowSet[r].length; index++)
				G.trace(" "+rowSet[r][index]);
			G.traceln();
		}
	}	
	
	public void printColConnections()
	{
		G.traceln ("Col perspective:");
		for (int c=0; c<cols; c++)
		{
			if (colSet[c]==null) continue;
			G.trace(c +": ");
			for (int index=0; index <colSet[c].length; index++)
				G.trace(" "+colSet[c][index]);
			G.traceln();
		}
	}
	
	/**
	 * Export H in sparse form to a text file
	 * Each line in the file is in the form : ROW,COL  or  COL,ROW
	 * @param fileName
	 * @throws Exception
	 */
	public void exportSparseMatrix(String fileName) throws Exception
	{
		PrintWriter writer = new PrintWriter(fileName);
		String str;
		if (G.COL_EXPORT_MODE) // export H in COL, ROW format
		{
			for (int c=0; c<cols; c++)
			{
				for (int i=0; i<colSet[c].length; i++)
				{
					str = c +" " +colSet[c][i] +"\n";
					writer.write (str);
				}
			}
		}
		else  // export H in standard Matlab format
		{
			for (int r=0; r<rowSet.length; r++)
			{
				for (int c=0; c<rowSet[r].length; c++)
				{
					str = r +" " +rowSet[r][c]+ "\n";  // the new line is mandatory
					writer.write (str);
				}
			}
		}
		writer.flush();
		writer.close();
	}
	
	/**
	 * Print the sparse matrix as a 2 dimensional array of 1's
	 * The 0's are represented by dots
	 *
	 */
	public void printMatrix()
	{
		if (G.silent) return;
		G.traceln("Matrix:");
		for (int c=0; c<cols; c++)
			G.trace(c%10 +" ");
		G.traceln();
		for (int r=0; r<rows; r++)
		{
			int row_weight=0;
			for (int c=0; c<cols; c++)
			{
				if (rowSet[r][row_weight]==c)
				{
					G.trace ("1 ");
					row_weight++;
					if (row_weight==rowSet[r].length)
					{
						c++;  // we are skipping one increment in the outer loop
						for ( ; c<cols; c++)		
							G.trace (". ");
						break;
					}
				}
				else
					G.trace (". ");
			}
			G.traceln ();
		}
		G.traceln ("-------------------------------------------------------");
		int[] col_weight = new int[cols];
		for (int r=0; r<rows; r++)
		{
			for (int c=0; c<cols; c++)
			{
				if (col_weight[c] < colSet[c].length && colSet[c][col_weight[c]]==r)
				{
					G.trace ("1 ");
					++col_weight[c];
				}
				else
					G.trace (". ");
			}
			G.traceln ();
		}
		G.traceln ("-------------------------------------------------------");
	}

	/**
	 * converts the H matrix from sparse form to a BitSet array
	 * @return
	 */
	public BitSet[] exportToBitSet()
	{
		BitSet[] bs = new BitSet[rows];
		for (int r=0; r<rows; r++)
		{
			bs[r] = new BitSet(cols);
			for (int c=0; c<rowSet[r].length; c++)
				bs[r].set(rowSet[r][c]);
		}
		return bs;
	}
	
	public void printBitSet()
	{
		//if (G.silent) return;
		BitSet[] bs = exportToBitSet();
		
		G.trace("*   ");
		for (int c=0; c<cols; c++)
			G.trace(c%10 +" ");
		G.traceln();
		
		for (int r=0; r<rows; r++)
		{
			G.trace (r%10 +":  ");
			for (int c=0; c<cols; c++)
			{
				if (bs[r].get(c))
					G.trace ("1 ");
				else
					G.trace (". ");
			}
			G.traceln ();
		}
		G.traceln ("-------------------------------------------------------");
	}
	
	/**
	 * Check for and remove 4-cycles
	 *
	 */
	public void cycleCheck () throws Exception
	{
		if (detectCycles())
		{
			printCycles();
			removeCycles();
			//dump();
			detectCycles(); // cehck again
			printCycles();		
		}
	}
	
	/**
	 * Detects 4-cycles ans stores it in the member variable cycleArray
	 * Non-destructive testing : the original matrix is unchanged
	 * @return true if there is any 4-cycle, false if no 4-cycle is detected
	 * @throws Exception
	 */
	public boolean detectCycles() throws Exception
	{
		G.traceln("Checking for 4-cycles...");
		CycleDetector detector = new CycleDetector();
		BitSet[] bs = exportToBitSet();
		this.cycleArray = detector.detectCycles(bs);
		return (cycleArray.length != 0);
	}

	/**
	 * Prints the cycle nodes
	 * NOTE : you must call detectCycles() before calling this
	 */
	public void printCycles ()
	{
		G.trace("\nCycles:");
		G.traceln (cycleArray.length==0 ? " none" : " ");
		for (int i=0; i<cycleArray.length; i++)
		{
			G.trace (cycleArray[i] +"-");
			if ((i+1)%4==0 && i!=0)
				G.traceln(cycleArray[i-3] +"");
		}
	}
	
	/**
	 * Removes the first edge of every cycle from H matrix
	 * The original matrix IS CHANGED
	 * NOTE : you must call detectCycles() before calling this
	 */
	public void removeCycles()
	{
		G.itraceln ("\nRemoving 4-cycles...");
		FlexArray tmpArray = new FlexArray(G.MAXDEGREE);
		for (int k=0; k<cycleArray.length; k+=4)
		{
			// remove the first edge in the cycle
			int col = cycleArray[k];
			int row = cycleArray[k+1];
			//G.traceln ("Removing row " +row +", col " +col);
			tmpArray.clear();
			for (int i=0; i<rowSet[row].length; i++)
			{
				if (rowSet[row][i]!=col)
					tmpArray.add(rowSet[row][i]);
			}
			rowSet[row] = tmpArray.toArray();
			
			tmpArray.clear();
			for (int i=0; i<colSet[col].length; i++)
			{
				if (colSet[col][i]!=row)
					tmpArray.add(colSet[col][i]);
			}
			colSet[col] = tmpArray.toArray();
			//printBitSet();
		}
		//G.traceln ("\n..Removed cycles !");
	}	
	
	public int getRowCount()
	{
		return rows;
	}
	
	public int getColCount()
	{
		return cols;
	}
	
	public void dump ()
	{
		if (G.silent) return;
		printConnections ();
		printMatrix();
		printBitSet();
	}
}
