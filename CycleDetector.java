/**
 * Detects 4-cycles in H by calculating H*H'
 * The diagonal elements in H*H' can be ignored, but the off-diagonal 
 * elements should not be greater than 1 for a cycle-free graph.
 * If row r, col c in H*H' is 2, then there is a 4-cycle involving the 
 * check nodes r and c in H.
 * H*H' will be symmetric, so it is enough to inspect the triangle above the diagonal.
 */
import java.util.*;

/**
 * Detects 4 cycles and returns as an array
 * The array contains contiguous blocks of 4 integers; each block is one 4-cycle
 * @author Rajaraman
 */
public class CycleDetector
{
	public int[] detectCycles (BitSet[] H)
	{
		FlexArray arr = new FlexArray();
		int rows = H.length;
		BitSet tmp;
		for (int i=0; i<rows; i++)
		{
			for (int j=i+1; j<rows; j++)
			{
				tmp = (BitSet)H[i].clone();
				tmp.and(H[j]);
				if (tmp.cardinality() <= 1)
					continue;
				//printBitSet(tmp);
				for (int b=tmp.nextSetBit(0); b>=0; b=tmp.nextSetBit(b+1))
				{
					for (int c=tmp.nextSetBit(b+1); c>=0; c=tmp.nextSetBit(c+1))
					{
						arr.add(b);arr.add(i);arr.add(c);arr.add(j); //arr.add(b);
						//G.traceln (b +"-" +i +"-" +c +"-" +j +"-" +b);
					}
				}
			}
		}
		return arr.toArray();
	}
	
	public void printCycles (BitSet[] H)
	{
		G.trace("\nCycles :");
		int[] cycleArray = detectCycles (H);
		G.traceln (cycleArray.length==0 ? " none" : " ");
		for (int i=0; i<cycleArray.length; i++)
		{
			G.trace (cycleArray[i] +"-");
			if ((i+1)%4==0 && i!=0)
				G.traceln(cycleArray[i-3] +"");
		}
	}
	
	/*****************
	// temporary helper method for debugging
	public void printBitSet (BitSet bs)
	{
		int cols = 50; // cludge !
		for (int i=0; i<cols; i++)
		{
			if (bs.get(i))
				G.trace("1 ");
			else
				G.trace(". ");
		}
		G.traceln();
	}
	*******************/
	
	public static void main (String[] args)
	{
		BitSet[] bs = new BitSet[10];
		for (int i=0; i<10; i++)
			bs[i] = new BitSet();
		bs[2].set (3); bs[2].set (9); bs[2].set (11); bs[2].set (15);
		bs[7].set (3); bs[7].set (9); bs[7].set (11);
		bs[8].set (9); bs[8].set (11); bs[8].set (15);
		
		CycleDetector cd = new CycleDetector();
		cd.printCycles (bs);
	}
}


