
/**
 * Gallager's algorithm for generating regular LDPC codes
 * Can automatically adjust row and column numbers to suit the given pair of degress
 * Can generate deterministically shuffled H : so it can be repeated
 * Restriction : row weight must divide number of columns
 * @author Rajaraman
 */
import java.util.*;

public class Gallager extends Generator
{
	int rowwt;  	// row weight
	int colwt;  	// column weight
 	
	public void init (int rows, int cols, DegreeDistribution distribution)
	{
		throw new RuntimeException ("Undefined initializer");
	}
	
	public void init (int _suggestedCols, int _colwt, int _rowwt)
	{
		int _cols = _suggestedCols - _suggestedCols % _rowwt; // round off to the nearest multiple of _rowwt
		if ((_cols*_colwt) %_rowwt !=0) 
			throw new IllegalArgumentException ("Node degrees are not consistent with number of columns");
		init (_cols, (_cols*_colwt)/_rowwt, _colwt, _rowwt);
	}
	
	/**
	 * Initialize H matrix
	 * @param _cols number of columns in H
	 * @param _rows number of rows in H
	 * @param _colwt column weight
	 * @param _rowwt row weight
	 */
	public void init (int _cols, int _rows, int _colwt, int _rowwt)
	{
		if (_cols*_colwt != _rows*_rowwt) 		// number of edges must tally	
			throw new IllegalArgumentException ("Row and column weighs are not consistent");
		if (_cols % _rowwt != 0) 
			throw new IllegalArgumentException ("Row weight must divide number of columns");
		
		rows = _rows;
		cols = _cols;
		rowwt = _rowwt;
		colwt = _colwt;
		
		rowSet = new int[rows][];
		for (int r=0; r<rows; r++)
			rowSet[r] = new int[rowwt];
		colSet = new int[cols][];
		for (int c=0; c<cols; c++)
			colSet[c] = new int[colwt];
	}
	
	/**
	 * Gallager's construction:
	 * The first block of L rows are constructed deterministically, other rows are random
	 * The sparse matrix H is stored in two copies : row-wise and column-wise
	 */	
	public void generate() throws Exception
	{
		G.itraceln ("Making connections...");
		int L = cols/rowwt;        // the block size; note: rows = L*colwt
		int[] col_weight = new int[cols];
		// the first block of L rows have a stair case appearance
		for (int r=0; r<L; r++)
		{
			int row_weight=0;
			for (int c=r*rowwt; c<(r+1)*rowwt; c++)
			{
				rowSet[r][row_weight] = c;
				colSet[c][col_weight[c]] = r;
				++row_weight;
				++col_weight[c];
			}
		}
		shuffle(1, col_weight);		// generate the rest of the blocks 
		cycleCheck();
	}
	
	protected void shuffle(int startingBlock, int[] col_weight)
	{
		int L = cols/rowwt;        // the block size; note: rows = L*colwt
		 
		ArrayList list = new ArrayList(cols);
		for (int i=0; i<cols; i++)
			list.add (new Integer(i));

		for (int block=startingBlock; block<colwt; block++) 	// we know rows/L = colwt
		{
			if (G.generateRandomH)
				Collections.shuffle(list); // randomly shuffle integers 1 to n
			else
				Collections.shuffle(list, new Random(G.RAND_SEED));  	
			int row_weight[] = new int[L];
			int index;  	// row index within the block
			int current_row;
			for (int c=0; c<cols; c++)
			{
				index = ((Integer)list.get(c)).intValue()/rowwt;    // which step of the stair case we are in
				current_row = block*L + index;
				rowSet[current_row][row_weight[index]] = c;
				colSet[c][col_weight[c]] = current_row;
				++row_weight[index];
				++col_weight[c];
			}
		}
	}
	
	public void printMatrix()
	{
		System.out.println ("\nGallager : cols=" +cols +"; rows=" +rows +"; colwt=" +colwt +"; rowwt=" +rowwt +"\n");
		super.printMatrix();
	}
	
	public String toString()
	{
		return "Gallager: " +rows +" rows; " +cols +" cols; colwt=" +colwt +"; rowwt=" +rowwt;
	}
}
