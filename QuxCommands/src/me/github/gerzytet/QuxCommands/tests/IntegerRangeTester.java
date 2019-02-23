package me.github.gerzytet.QuxCommands.tests;

import me.github.gerzytet.QuxCommands.IntegerRange;
import me.github.gerzytet.QuxCommands.IntegerRangeParseException;
import me.github.gerzytet.QuxCommands.IntegerRangeParser;

public class IntegerRangeTester
{
	public static void print(Object o)
	{
		System.out.println(o);
	}
	
	public static void test(String testcase)
	{
		try {
			IntegerRange range = IntegerRangeParser.parse(testcase);
			print("Result: parsed");
			print("min: " + range.getMin());
			print("max: " + range.getMax());
		} catch (IntegerRangeParseException e) {
			print("Result: falied");
			e.printStackTrace();
		}
		print("\n");
		
	}
	
	public static void testAll()
	{
		for (String string : testcases)
		{
			test(string);
		}
	}
	
	public static final String[] testcases = 
		{
			"2..1",
			"1..2",
			"jlkasjkdlas",
			"2..j",
			"1.2..2.3",
			"2..",
			"67823..898032",
			"..34",
			"..",
			"",
			".",
			"2...3"
		};
	
	public static void main(String[] args)
	{
		testAll();
	}
}
