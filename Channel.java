/*
 * Simulates a Binary Erasure Channel.
 * The erasure probability can be preset, and it determines the number of bits erased.
 * Note : This follows systematic erasure pattern : that is, once you fix the probability, 
 * the number of bits erased in each iteration remains the same; only their position is 
 * randomly shuffled.
 */
import java.util.*;

public class Channel 
{
	float erasureProb = 0.0f;
	int erasureCount = 0;
	int cols = 0;
	ArrayList list = null;  // temporary object used to shuffle the bits
	
	/**
	 * Call init() every time a new code is generated, with a different N
	 * @param _cols the number of columns in H
	 */
	public void init (int _cols)  
	{
		this.cols = _cols;
		this.list = new ArrayList (cols);
		for (int i=0; i<cols; i++)
			list.add (new Integer(i));
		this.erasureProb = 0.0f;
		this.erasureCount = 0;
	}

	/**
	 * Set the erasure probability, to be used for subsequent calls of getErasures()
	 * @param _erasureProb
	 */
	public void setErasureProbability (float _erasureProb)
	{
		if (_erasureProb < 0.0f || _erasureProb > 1.0f)
			throw new RuntimeException ("Invalid erasure probability");
		this.erasureProb = _erasureProb;
		this.erasureCount = (int) (erasureProb * cols + 0.5f);
		//G.traceln ("Erasure Prob : "+erasureProb +"; Erasure count : "+erasureCount);
	}

	/**
	 * Alternatively, you can directly set the integer count
	 * @param _erasureCount
	 */
	public void setErasureCount (int _erasureCount)
	{
		if (_erasureCount < 0 || _erasureCount > cols)
			throw new RuntimeException ("Invalid erasure count");
		this.erasureCount = _erasureCount;
		this.erasureProb =  ((float)erasureCount)/cols;
		//G.traceln ("Erasure Prob : "+erasureProb +"; Erasure count : "+erasureCount);
	}

	public float getErasureProbability ()
	{
		return erasureProb;
	}
	
	public int getErasureCount()
	{
		return erasureCount;
	}
	
	/*
	 * Returns a vector representing the erasure positions.
	 * Note : The original received data is not altered; the Channel only gives an overlay mask, 
	 * indicating the erasure bits
	 */
	public BitSet getErasures()
	{
		Collections.shuffle(this.list);  // randomly shuffle integers 1 to N
		BitSet bs = new BitSet(cols);
		for (int i=0; i<this.erasureCount; i++)
			bs.set (((Integer)list.get(i)).intValue());
		return bs;
	}
}
