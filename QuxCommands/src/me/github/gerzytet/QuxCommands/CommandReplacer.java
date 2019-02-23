package me.github.gerzytet.QuxCommands;

public class CommandReplacer 
{
	@SuppressWarnings("unused")
	public static String replace(String command, String identifier, Object replacement)
	{
		return command.replace("$" + identifier, replacement.toString());
	}
	
	
}
