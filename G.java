
public class G 
{
	public static final int PEG = 0;
	public static final int GALLAGER = 1;
	public static final int MODGALLAGER = 2;  // modified gallager algorithm
	public static boolean silent = false;  // verbose output
	public static int MAXDEGREE = 25;      // maximum node degree
	public static boolean generateRandomH = false;  // if false, the same H matrix will be generated in every run
	public static int RAND_SEED = 13; // even when H deterministically generated, you can change the seed to get different H
	public static int MAX_DEPTH = 10;  // minimum acceptable girth - needed (for large N) to terminate PEG algorithm
	public static boolean COL_EXPORT_MODE = true;  // save the H file in Matlab row,col format or col,row format
	public static int MAX_ITERATIONS = 20;  // decoder iterations before giving up 
	public static String LOG_PREFIX = "..\\Logs\\Log-";
	public static String LOG_SUFFIX = ".csv";	
	public static int activityNumber = 0;
	public static int MAX_ARQ = 10;
	public static boolean ARQ_MODE = true;
	public static double ROUNDOFF_ERROR = 0.001;  
	public static int PRECISION = 10000;
	
	public static double round (double d)
	{
		int tmp = (int) (d*PRECISION + 0.5);
		return (double) tmp/ PRECISION;
	}
	
	public static void trace (String str)
	{
		if (!silent)
			System.out.print(str);
	}
	public static void traceln (String str)
	{
		if (!silent)
			System.out.println(str);
	}
	public static void traceln ()
	{
		if (!silent)
			System.out.println();
	}
	public static void itrace (String str)
	{
		System.out.print(str);
	}
	public static void itraceln (String str)
	{
		System.out.println(str);
	}
	public static void itraceln ()
	{
		System.out.println();
	}
	/**
	 * Activity indicator :
	 * For long running programs, keeps the console active, to reassure user that 
	 * the program has not frozen !
	 */
	public static void activity()
	{
		if (!silent)	
			return;
		//System.out.print(activityNumber%10);
		System.out.print(".");
		activityNumber++;
		if ((activityNumber+1)%12==0)
		{
			System.out.println();
			activityNumber=0;
		}
	}
}
