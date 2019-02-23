package me.github.gerzytet.QuxCommands;

import java.util.Iterator;

public class IntegerRange implements Iterator<Integer>
{
	private int max;
	private int current;
	private int min;
	
	public IntegerRange(int min, int max)
	{
		this.min = min;
		this.max = max;
		this.current = min-1;
	}
	
	public boolean has(int x)
	{
		return x >= min && x <= max;
	}
	
	@Override
	public boolean hasNext() 
	{
		return current <= max;
	}

	@Override
	public Integer next() 
	{
		if (hasNext())
		{
			current++;
			return current;
		}
		return null;
	}
	
	public int getMin()
	{
		return min;
	}
	
	public int getMax()
	{
		return max;
	}
	
	public int getCurrent()
	{
		return current;
	}
}
