package me.github.gerzytet.QuxCommands;

public class IntegerRangeParser 
{
	public static IntegerRange parse(String input) throws IntegerRangeParseException
	{
		int dotsRead = 0;
		final String NUMBERS = "0123456789";
		final String acceptable = NUMBERS + ".";
		int max = 0;
		int min = 0;
		int currentNumber = 0;
		boolean expectingNumber = true;
		for (char c: input.toCharArray())
		{
			if (acceptable.indexOf(c) == -1)
			{
				throw new IntegerRangeParseException("Illegal character: " + c);
			}
		}
		
		for (char c: input.toCharArray())
		{
			if (NUMBERS.indexOf(c) != -1)
			{
				if (dotsRead == 1)
				{
					throw new IntegerRangeParseException("Whole numbers only!");
				}
				expectingNumber = false;
				currentNumber *= 10;
				currentNumber += Character.getNumericValue(c);
				continue;
			}
			else
			{
				if (expectingNumber)
				{
					throw new IntegerRangeParseException("Expected a number.  got '.' Instead");
				}
				
				dotsRead++;
				if (dotsRead > 2)
				{
					throw new IntegerRangeParseException("Too many dots! Usage: x..y");
				}
				if (dotsRead == 2)
				{
					min = currentNumber;
					currentNumber = 0;
					expectingNumber = true;
				}
			}
			
		}
		if (expectingNumber)
		{
			throw new IntegerRangeParseException("Unexpected end of string while looking for a number");
		}
		max = currentNumber;
		if (min > max)
		{
			throw new IntegerRangeParseException("Minimum value must be less than the maximum");
		}
		
		return new IntegerRange(min, max);
		
	}

}
