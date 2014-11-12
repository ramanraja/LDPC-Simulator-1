
/**
This class represents the parity check matrix; 
The class is enhanced : it can find the rank of itself, or a selected
subset of its columns
**/

import java.util.BitSet;
import java.util.Random;

public class Matrix
{
    protected int rows;
    protected int cols;
    public BitSet[] rowSet;
   
    public void init (int _rows, int _cols, BitSet[] _rowSet) 
    {
    	this.rows = _rows;  // to enable re-initialization with a different matrix
    	this.cols = _cols;
    	this.rowSet = _rowSet;
    }
    
	/**
	 * reduce this matrix to triangular row echelon form
	 * Note : The original matrix is DESTROYED
	 *
	 */
	public void triangulate()
	{
		for (int c=0; c<cols; c++)
		{
			if (cols > rows && c==rows) break; // we have run out of rows
			if (!rowSet[c].get(c))
				if (!swapRows (c,c)) 
					if (!swapCols(c,c))
						continue;  
			makePivotColumZero(c,c);
		}
		//G.traceln ("triangulate() completed:");
		//dump();
	}

	/**
	 * Swaps the candidate pivot row with another row of matrix
	 * @param pivotRow
	 * @param pivotCol
	 * @return true if a suitable row was found and swapped
	 * @return false if all the column elements below the pivot element are zeros
	 */
	private boolean swapRows (int pivotRow, int pivotCol)
	{
		for (int r=pivotRow+1; r<rows; r++)
		{
			if (rowSet[r].get(pivotCol)) 
			{
				//G.traceln ("swapping row "+pivotRow +" with " +r);
				rowSet[r].xor(rowSet[pivotRow]);
				rowSet[pivotRow].xor(rowSet[r]);
				rowSet[r].xor(rowSet[pivotRow]);
				return true;
			}
		}
		return false;
	}

	/**
	 * Swaps the candidate pivot column with another column of the matrix
	 * @param pivotRow
	 * @param pivotCol
	 * @return true if a suitable column was found and swapped
	 * @return false if all the elements to the right of the pivot element in that row are zero
	 */
	private boolean swapCols (int pivotRow, int pivotCol)
	{
		for (int c=pivotCol+1; c<cols; c++)
		{
			if (!rowSet[pivotRow].get(c)) continue;
			//G.traceln ("swapping col "+pivotCol +" with " +c);
			boolean tmp;
			for (int r=0; r<rows; r++)  
			{
				tmp = rowSet[r].get(c);
				rowSet[r].set (c, rowSet[r].get(pivotCol));
				rowSet[r].set(pivotCol, tmp);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Makes all elements of the pivot column, below the pivot element, to be zero
	 */
	private void makePivotColumZero(int pivotRow, int pivotCol)
	{
		for (int r=pivotRow+1; r<rows; r++)
		{
			if (rowSet[r].get(pivotCol))
				rowSet[r].xor(rowSet[pivotRow]);
		}
		//G.traceln ("makePivotColumZero(): " +pivotRow+ ","+pivotCol);
		//dump();
	}

	/**
	 * Note : The original matrix is DESTROYED
	 * @return rank of this matrix
	 */
	public int getRank()
	{
		triangulate();
		int rank=0;
		for (int r=0; r<rows; r++)
		{
			if (rowSet[r].cardinality() != 0)
				rank++;
		}
		return rank;
	}
	
	public void fillRandom (int _rows, int _cols)
	{
		if (_rows <=0 || _cols <=0) throw new RuntimeException ("Invalid matrix dimensions");
		
		rows = _rows;
		cols = _cols;
		int weight = cols/4;
		Random rand = new Random();
		
		this.rowSet = new BitSet[rows];
		for (int r=0; r<rows; r++)
		{
			rowSet[r]= new BitSet(cols);
			for (int i=0; i<weight; i++)
				rowSet[r].set(rand.nextInt(cols));
		}
		//G.traceln ("fillRandom():");
		dump();
	}
	
	/*
	 * Keep only a subset of the columns, and zero out the rest.
	 * The columns to be retained is given by the bitset mask.
	 * Note : The original matrix is DESTROYED
	 */
	public void selectColumns (BitSet mask)
	{
		for (int r=0; r<rows; r++)
			rowSet[r].and(mask);
	}
	
	public void dump()
	{
		if (G.silent) return;
		for (int r=0; r<rows; r++)
		{
			for (int c=0; c<cols; c++)
			{
				if (rowSet[r].get(c))
					System.out.print ("1 ");
				else
					System.out.print ("0 ");
			}
			System.out.println ();
		}
		System.out.println ("-------------------------------------");
	}
}
