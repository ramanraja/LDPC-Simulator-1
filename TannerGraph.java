/**
 * The Tanner graph representing an LDPC code
 * It can do self-decoding on its LDPC code
 * Binary Erasure Channel is assumed
 */
import java.util.BitSet;

public class TannerGraph 
{
	Matrix matrix = null;
	BitSet data = null;
	BitSet erasures = null;
	BitSet tmp = null;
	BitSet revealed = null; // the parity equns that have been satisfied
	
	public void init (Matrix _matrix)
	{
		this.matrix = _matrix;
		this.data = new BitSet(matrix.cols);
		this.erasures = new BitSet(matrix.cols);
		this.tmp = new BitSet (matrix.cols);
		this.revealed = new BitSet(matrix.rows);
	}
	
	public boolean decode ()
	{
	    // find which of the parity equations have been satisfied initially
		this.revealed.clear();
		for (int row=0; row<matrix.rows; row++)
		{
			this.tmp.clear();
			tmp.or (matrix.rowSet[row]);
			tmp.and (erasures);
			if (tmp.cardinality()==0) // no erasures in this row
				revealed.set(row);
		}
		//G.logln("Initially revealed bits: " +revealed.toString());
		
		int iterations = 0;
		for ( ; iterations<G.MAX_ITERATIONS; iterations++)
			if (internalDecode())
				break;
		//G.logln ("Number of iterations : "+ Math.min((iterations+1), G.MAX_ITERATIONS));
		//G.log (" "+ Math.min((iterations+1), G.MAX_ITERATIONS));
		return (iterations < G.MAX_ITERATIONS);
	}
	
	// perform one iteration
	protected boolean internalDecode ()
	{
		for (int row=0; row<matrix.rows; row++)
		{
			//G.logln ("[" +row +"] Row set : "+matrix.rowSet[row].toString());
			if (revealed.get(row)) 
				continue;
			
			// take out the erasures in the current row
			this.tmp.clear();
			tmp.or (erasures);
			tmp.and (matrix.rowSet[row]);
			//G.logln ("Row "+ row +" : No. of erasures = "+tmp.cardinality());
			
			// if there is a single bit erased in this row, we can immediately fix it
			if (tmp.cardinality()==1) // only a single erasure in this row 
			{
				int erasePos = tmp.nextSetBit(0);
				tmp.xor (matrix.rowSet[row]); // kill only the erased bit
				tmp.and (data);  // take the other bits in this row, except the erased one 
				if (tmp.cardinality()%2 == 0) // the other bits are already having even parity 
					data.clear (erasePos);  // just maintain parity
				else  // the other bits are having odd parity
					data.set (erasePos);  // make it even

				erasures.clear(erasePos); // this erasure has just been repaired
				revealed.set(row);
			}
			else   
			{
				if (tmp.cardinality()==0) // one revealed bit helped many equations to be satisfied
					revealed.set(row);
			}
			/*
			Note : now, there may be a back-propagation effect : some of the earlier
			rows may become fully revealed at this point of time; but we will never
			know it
			*/
		}
		return (erasures.cardinality()==0);
	}
	/*
	public boolean checkParity()
	{
		boolean result = true;
		if (erasures.cardinality()!=0)
		{
			G.traceln ("\nStill there are " +erasures.cardinality() +" erasures");
			result = false;
		}
		//  multiply H*X' and see if it is the zero vector
		BitSet prod = matrix.multiplyVector(data);
		//G.logln ("Hx' = " +prod);
		
		if (prod.cardinality() != 0)
		{
			G.logln ("\nParity check failed : Parity vector = " +prod.toString());
			result = false;
		}
		return result;
	}
	*/
	public BitSet getData ()
	{
		return this.data;
	}
	
	/*
	 * input argument _data is not modified
	 */
	public void setData (BitSet _data)
	{
		// TODO : validate : length of data <= matrix columns ?
		this.data.clear();
		data.or(_data);
	}
	
	public BitSet getErasures ()
	{
		return this.erasures;
	}
	
	/*
	 * input argument _erasures is not modified
	 */
	public void setErasures (BitSet _erasures)
	{
		// TODO : validate : length of erasures <= length of data ?
		this.erasures.clear();
		erasures.or (_erasures);
	}
	
	/**
	 * Will be useful to get the residual error count after decoding
	 * @return
	 */
	public int getErasureCount()
	{
		return erasures.cardinality();
	}
	
	public void printErasures()
	{
		//G.trace ("Erasures :");
		G.traceln (erasures.toString());
	}
	
	public void printData()
	{
		//G.trace ("Data :");
		G.traceln (data.toString());
	}
	
	public void dumpMatrix()
	{
		matrix.dump();
	}
}

