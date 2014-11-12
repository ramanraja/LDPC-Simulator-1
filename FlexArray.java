
public class FlexArray 
{
	protected int[] array;
	protected int capacity;
	protected int index;
	protected int growBy;
	
	public FlexArray()
	{
		this(100);
	}
	
	public FlexArray(int initialCapacity)
	{
		this (initialCapacity, initialCapacity/2);
	}
	
	public FlexArray(int initialCapacity, int growthSize)
	{
		capacity = initialCapacity;
		growBy = growthSize;
		index=0;
		array = new int[initialCapacity];
	}

	public void add (int num)
	{
		if (index == capacity)
			grow(growBy);
		array[index++] = num;
	}
	
	protected void grow(int growthSize)
	{
		capacity += growthSize;
		int[] tmp = new int[capacity];
//		for (int i=0; i<index; i++)
//			tmp[i] = array[i];
		System.arraycopy (array, 0, tmp, 0, index);
		array = tmp;
	}

	public void append (FlexArray newArray)
	{
		if (this.index+newArray.index > this.capacity)
			grow (newArray.index);
//		for (int i=0; i<newArray.index; i++)
//			array[index++] = newArray.array[i];
		System.arraycopy (newArray.array, 0, this.array, this.index, newArray.index);
		this.index += newArray.index;
	}
	
	public int get(int pos)
	{
		return array[pos];
	}
	
	public void set(int pos, int value)
	{
		if (pos >= index || pos < 0)
			throw new RuntimeException ("Array index out of bounds");
		array[pos] = value;
	}
	
//	public void increment(int pos)
//	{
//		if (pos >= index || pos < 0)
//			throw new RuntimeException ("Array index out of bounds");
//		++array[pos];
//	}

	public int length()
	{
		return index;
	}
	
	public int[] toArray()
	{
		int[] retArray = new int[index];
		for (int i=0; i<index; i++)
			retArray[i] = array[i];
		return retArray;
	}
	
	public String toString()
	{
		 return("FlexArray : capacity=" +capacity +", grows by=" +growBy +", current size=" +index);
	}
	
	public void clear()
	{
		index = 0; // TODO : reset the size to initial capacity ?
	}
	
	public void dump()
	{
		//System.out.println(toString());
		for (int i=0; i<index; i++)
		{
			System.out.print (array[i] +" ");
			if ((i+1)%25==0)
				System.out.println();
		}
		System.out.println();
	}
	
	public static void main(String[] args)
	{
//		int[] a=new int[10];
//		a[0]=1; a[1]=2; a[2]=3;
//		int[] b=new int[10];
//		b[0]=11; b[1]=12; b[2]=13;b[3]=14;
//		System.arraycopy(b, 0, a, 3, 4);
//		for (int i=0; i<10; i++)
//			System.out.print(a[i]+" ");
		
		FlexArray f = new FlexArray();
		System.out.println (f);
		for (int i=0; i<60; i++)
			f.add(i+1);
		f.dump();
		for (int i=0; i<60; i++)
			f.add(i+101);
		f.dump();
		
		FlexArray f2 = new FlexArray(30, 10);
		System.out.println (f2);
		for (int i=0; i<45; i++)
			f2.add(i+201);
		f2.dump();
		
		f.append(f2);
		
		f.dump();
		System.out.println("Export to array :");
		int[] a = f.toArray();
		for (int i=0; i<a.length; i++)
		{
			System.out.print(a[i]+ " ");
			if ((i+1)%25==0) System.out.println();
		}
	}
}
