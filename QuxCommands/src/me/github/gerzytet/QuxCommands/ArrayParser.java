package me.github.gerzytet.QuxCommands;

import java.util.ArrayList;

public class ArrayParser
{
	public static String[] parse(String input) throws ArrayParseException
	{
		if (!input.startsWith("["))
		{
			throw new ArrayParseException("Expected [ at the start of the array");
		}
		if (!input.startsWith("]"))
		{
			throw new ArrayParseException("Expected ] at the end of the array");
		}
		ArrayList<String> tokens = new ArrayList<String>();
		input = input.substring(1, input.length()-1);
		
		boolean escape = false;
		boolean inString = false;
		boolean commaProcessed = false;
		StringBuilder current = new StringBuilder();
		for (char c: input.toCharArray())
		{
			if (!inString)
			{
				if (commaProcessed)
				{
					if (!(c == '"'))
					{
						throw new ArrayParseException("Unexpected " + c);
					}
					inString = true;
					continue;
				}
				if (!(c == ','))
				{
					throw new ArrayParseException("Expected ,");
				}
				commaProcessed = true;
				continue;
			}
			
			if (escape)
			{
				current.append(c);
				escape = false;
				continue;
			}
			
			if (c == '"')
			{
				tokens.add(current.toString());
				current = new StringBuilder();
				inString = false;
				commaProcessed = false;
				continue;
			}
			
			if (c == '\\')
			{
				escape = true;
				continue;
			}
			
			current.append(c);
		}
		
		return (String[]) tokens.toArray();
	}
}
