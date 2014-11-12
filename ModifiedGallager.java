
public class ModifiedGallager extends Gallager
{
	/**
	 * Small modification of Gallager's construction:
	 * Instead of the first block of L rows being constructed deterministically, all rows are random
	 */
	public void generate()
	{
		int[] col_weight = new int[cols];
		shuffle (0, col_weight);
	}
	
	public String toString()
	{
		return "Modified Gallager: " +rows +" rows; " +cols +" cols; colwt=" +colwt +"; rowwt=" +rowwt;
	}
}
 