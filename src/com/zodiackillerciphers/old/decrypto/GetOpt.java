package com.zodiackillerciphers.old.decrypto;

import java.util.*;
import java.io.*;

/** A basic GetOpt implementation. Add your options, parse the command
 * line arguments, then get the modified value of your arguments.
 **/
public class GetOpt
{
    public int helpMessageWidth=50;
    
    // helper class to help control consumption of arguments.
    class ArgFeeder
    {
	String[] args;
	int pos;

	ArgFeeder(String[] args)
	{
	    this.args=args;
	    pos=0;
	}

	String peek()
	{
	    if (hasNext())
		return args[pos+1];
	    return null;
	}

	String next()
	{
	    if (pos<args.length)
		return args[pos++];
	    return null;
	}

	boolean hasNext()
	{
	    return pos<args.length;
	}
	
	int size()
	{
	    return args.length;
	}
    }

    // base class for our options
    abstract class GetOptOpt
    {
	char   sname;
	String lname;

	String helpMessage;

	// set to true if the user specified the option.
	boolean specified = false;

	/** Return null if no error. Return an error message if an error occured. **/
	abstract String eval(String equalString, ArgFeeder feeder);

	/** Return a string representation of the value of this option. */
	abstract String stringValue();
	abstract String stringDefaultValue();
	String getHelpMessage()
	{
	    return helpMessage;
	}
    }

    class StringOpt extends GetOptOpt
    {
	String value;
	String defaultValue;

	StringOpt(char sname, String lname, String defaultValue, String helpMessage)
	{
	    this.sname=sname;
	    this.lname=lname;
	    this.value=defaultValue;
	    this.helpMessage=helpMessage;
	    this.defaultValue=defaultValue;
	}

	String eval(String equalString, ArgFeeder feeder)
	{
	    String s=equalString;
	    if (s==null)
		s=feeder.next();
	    if (s==null)
		return "Option "+lname+" requires a string argument.";

	    value=s;
	    return null;
	}

	String stringDefaultValue()
	{
	    if (defaultValue==null)
		return "";
	    return defaultValue;
	}

	String stringValue()
	{
	    return value;
	}

	String getValue()
	{
	    return value;
	}
    }

    class IntOpt extends GetOptOpt
    {
	int value;
	int defaultValue;

	IntOpt (char sname, String lname, int defaultValue, String helpMessage)
	{
	    this.sname=sname;
	    this.lname=lname;
	    this.value=defaultValue;
	    this.defaultValue=defaultValue;
	    this.helpMessage=helpMessage;
	}

	String eval(String equalString, ArgFeeder feeder)
	{
	    String s=equalString;
	    if (s==null)
		s=feeder.next();
	    if (s==null)
		return "Option "+lname+" requires an integer argument.";

	    try {
		value=Integer.parseInt(s);
	    } catch (NumberFormatException ex) {
		return "Invalid integer format '"+s+"'";
	    }

	    return null;
	}

	String stringDefaultValue()
	{
	    return ""+defaultValue;
	}

	String stringValue()
	{
	    return ""+value;
	}

	int getValue()
	{
	    return value;
	}
    }

    class DoubleOpt extends GetOptOpt
    {
	double value;
	double defaultValue;

	DoubleOpt (char sname, String lname, double defaultValue, String helpMessage)
	{
	    this.sname=sname;
	    this.lname=lname;
	    this.value=defaultValue;
	    this.defaultValue=defaultValue;
	    this.helpMessage=helpMessage;
	}

	String eval(String equalString, ArgFeeder feeder)
	{
	    String s=equalString;
	    if (s==null)
		s=feeder.next();
	    if (s==null)
		return "Option "+lname+" requires an integer argument.";

	    try {
		value=Double.parseDouble(s);
	    } catch (NumberFormatException ex) {
		return "Invalid integer format '"+s+"'";
	    }

	    return null;
	}

	String stringDefaultValue()
	{
	    return ""+defaultValue;
	}

	String stringValue()
	{
	    return ""+value;
	}

	double getValue()
	{
	    return value;
	}
    }

    class BooleanOpt extends GetOptOpt
    {
	boolean value;
	boolean defaultValue;

	BooleanOpt(char sname, String lname, boolean defaultValue, String helpMessage)
	{
	    this.sname=sname;
	    this.lname=lname;
	    this.value=defaultValue;
	    this.defaultValue=defaultValue;
	    this.helpMessage=helpMessage;
	}

	String eval(String equalString, ArgFeeder feeder)
	{
	    String s=equalString;

	    // if they don't provide an --flag=value for a boolean,
	    // we won't suck one up otherwise!
	    /*	    if (s==null)
		s=feeder.next();
	    */

	    if (s==null)
		{
		    value=true;
		    return null;
		}

	    value=s.equalsIgnoreCase("TRUE");
	    if (!value && !s.equalsIgnoreCase("FALSE"))
		return "Invalid boolean format '"+s+"'";
	    
	    return null;
	}

	String stringDefaultValue()
	{
	    return ""+defaultValue;
	}

	String stringValue()
	{
	    return ""+value;
	}
	
	boolean getValue()
	{
	    return value;
	}
    }

    HashMap<String, GetOptOpt> optsLong=new HashMap<String, GetOptOpt>();
    HashMap<String, GetOptOpt>  optsShort=new HashMap<String, GetOptOpt>();
    ArrayList<GetOptOpt> optsHelp=new ArrayList<GetOptOpt>();

    ArrayList<String> extraArgs=new ArrayList<String>();
    String reason;

    /** Create an empty GetOpt object. You should add options, then
     * parse, then get the values. 
     **/
    public GetOpt()
    {
	reason=null;
    }

    /** Get the reason for an error. The error is "user friendly" **/
    public String getReason()
    {
	return reason;
    }

    /** Add a string-type option. The value can be determined (after
     * parsing) by the matching getValue method.
     *
     * @param sname A one letter short name or 0 if none.
     * @param lname A multi-letter option name
     * @param defaultValue The default value of the option
     * @param helpMessage A brief description of the option
     **/
    public void addString(char sname, String lname, String defaultValue,
				String helpMessage)
    {
	assert(sname!=0 | !lname.equals(""));
	StringOpt opt=new StringOpt(sname, lname, defaultValue, helpMessage);

	if (!lname.equals(""))
	    {
		assert(optsLong.get(lname)==null);
		optsLong.put(lname, opt);
	    }

	if (sname!=0)
	    {
		assert(optsShort.get(""+sname)==null);
		optsShort.put(""+sname, opt);
	    }

	optsHelp.add(opt);
    }

    /** Determine the value of an option.
     *
     * @param lname The long name specified by the matching addOption
     * method.
     **/
    public String getString(String lname)
    {
	// we intentionally don't check for the type conversion
	// or for null; we want the program to halt abruptly if
	// the programmer has gone stupid on us.
	StringOpt opt=(StringOpt) optsLong.get(lname);
	return opt.value;
    }

    /** Add an integer-type option. The value can be determined (after
     * parsing) by the matching getValue method.
     *
     * @param sname A one letter short name or 0 if none.
     * @param lname A multi-letter option name
     * @param defaultValue The default value of the option
     * @param helpMessage A brief description of the option
     **/
    public void addInt(char sname, String lname, int defaultValue, 
			     String helpMessage)
    {
	assert(sname!=0 | !lname.equals(""));
	IntOpt opt=new IntOpt(sname, lname, defaultValue, helpMessage);

	if (!lname.equals(""))
	    {
		assert(optsLong.get(lname)==null);
		optsLong.put(lname, opt);
	    }

	if (sname!=0)
	    {
		assert(optsShort.get(""+sname)==null);
		optsShort.put(""+sname, opt);
	    }

	optsHelp.add(opt);
    }

    /** Add an double-type option. The value can be determined (after
     * parsing) by the matching getValue method.
     *
     * @param sname A one letter short name or 0 if none.
     * @param lname A multi-letter option name
     * @param defaultValue The default value of the option
     * @param helpMessage A brief description of the option
     **/
    public void addDouble(char sname, String lname, double defaultValue, 
				String helpMessage)
    {
	assert(sname!=0 | !lname.equals(""));
	DoubleOpt opt=new DoubleOpt(sname, lname, defaultValue, helpMessage);

	if (!lname.equals(""))
	    {
		assert(optsLong.get(lname)==null);
		optsLong.put(lname, opt);
	    }

	if (sname!=0)
	    {
		assert(optsShort.get(""+sname)==null);
		optsShort.put(""+sname, opt);
	    }

	optsHelp.add(opt);
    }

    /** Determine the value of an option.
     *
     * @param lname The long name specified by the matching addOption
     * method.
     **/
    public int getInt(String lname)
    {
	IntOpt opt=(IntOpt) optsLong.get(lname);
	return opt.value;
    }


    /** Determine the value of an option.
     *
     * @param lname The long name specified by the matching addOption
     * method.
     **/
    public double getDouble(String lname)
    {
	DoubleOpt opt=(DoubleOpt) optsLong.get(lname);
	return opt.value;
    }

    /** Returns true if the option was specified on the command line.
     * @param lname The long name specified by the matching addOption
     * method.
     **/
    public boolean wasSpecified(String lname)
    {
	GetOptOpt opt=optsLong.get(lname);
	return opt.specified;
    }

    /** Add a boolean-type option. The value can be determined (after
     * parsing) by the matching getValue method.
     *
     * @param sname A one letter short name or 0 if none.
     * @param lname A multi-letter option name
     * @param defaultValue The default value of the option
     * @param helpMessage A brief description of the option
     **/
    public void addBoolean(char sname, String lname, boolean defaultValue, 
				 String helpMessage)
    {
	assert(sname!=0 | !lname.equals(""));
	BooleanOpt opt=new BooleanOpt(sname, lname, defaultValue, helpMessage);

	if (!lname.equals(""))
	    {
		assert(optsLong.get(lname)==null);
		optsLong.put(lname, opt);
	    }

	if (sname!=0)
	    {
		assert(optsShort.get(""+sname)==null);
		optsShort.put(""+sname, opt);
	    }

	optsHelp.add(opt);
    }

    /** Determine the value of an option.
     *
     * @param lname The long name specified by the matching addOption
     * method.
     **/
    public boolean getBoolean(String lname)
    {
	BooleanOpt opt=(BooleanOpt) optsLong.get(lname);
	return opt.value;
    }

    /** Process command line arguments, updating the options we know about.
     * @param args The args passed into the main() function.
     * @return success. On failure, false is returned and getReason() will
     * tell you about the error. 
     **/
    public boolean parse(String[] args)
    {
	if (args==null)
	    return true;

	ArgFeeder feeder=new ArgFeeder(args);

	while(feeder.hasNext())
	    {
		String s=feeder.next();

		// is it a long option?
		if (s.startsWith("--"))
		    {
			String optString=s.substring(2);
			String equalString=null;

			int equalIdx=optString.indexOf("=");

			// it has an equal sign. extract parts opt=equal.
			if (equalIdx>=0)
			    {
				equalString=optString.substring(equalIdx+1);
				optString=optString.substring(0,equalIdx);
			    }

			// look up the option
			GetOptOpt goo=optsLong.get(optString);

			if (goo==null)
			    {
				reason="Unknown option '"+optString+"'";
				return false;
			    }

			// evaluate the option
			reason=goo.eval(equalString, feeder);
			goo.specified=true;
			if (reason!=null)
			    return false;
		    }
		else if (s.startsWith("-"))
		    {
			// it's a short option. How exciting!

			// loop over each character in the string. Each one
			// gets to be evaluated.
			for (int j=1;j<s.length();j++)
			    {
				// look up the option
				GetOptOpt goo=optsShort.get(""+s.charAt(j));

				if (goo==null)
				    {
					reason="Unknown option '"+s.charAt(j)+"'";
					return false;
				    }
				
				// evaluate the option
				reason=goo.eval(null, feeder);
				goo.specified=true;
				if (reason!=null)
				    return false;
			    }
		    }
		else
		    {
			extraArgs.add(s);
		    }
	    }

	return true;
    }

    /** Return arguments that were not consumed by flag processing. */
    public ArrayList<String> getExtraArgs()
    {
	return extraArgs;
    }

    /** As a debugging aid, dump the values of all options to stdout. **/
    public void dump()
    {
	try {
	    dumpInternal(new OutputStreamWriter(System.out));
	} catch (IOException ex) {
	}
    }

    /** As a debugging aid, dump the values of all options to the
     * specified Writer.. **/
    public void dumpInternal(Writer outs) throws IOException
    {
	int longNameFieldSize=0;
	int defaultValueFieldSize=0;

	for (GetOptOpt goo : optsHelp)
	    {
		if (goo.lname.length()>longNameFieldSize)
		    longNameFieldSize=goo.lname.length();

		if (goo.stringDefaultValue().length()>defaultValueFieldSize)
		    defaultValueFieldSize=goo.stringDefaultValue().length();
	    }

	longNameFieldSize+=2;

	for (GetOptOpt goo : optsHelp)
	    {
		// write the long option
		outs.write("--"+goo.lname);
		fill(outs,longNameFieldSize-goo.lname.length());

		// write the default value
		outs.write("= "+goo.stringValue());
		fill(outs,defaultValueFieldSize-goo.stringDefaultValue().length());

		outs.write("\n");
	    }

	outs.write("\nExtra Arguments:\n");
	for (String s : extraArgs)
	    {
		outs.write(s);
		outs.write("  ");
	    }

	outs.write("\n");
	outs.flush();
    }

    /** Output a "pretty" usage display based on the helpMessages of
     * each option.
     **/
    public void doHelp()
    {
	try {
	    doHelp(new OutputStreamWriter(System.out));
	} catch (IOException ex) {
	}
    }
    

    /** Output a "pretty" usage display based on the helpMessages of
     * each option; sends the output to the specified Writer.
     **/
    public void doHelp(Writer outs) throws IOException
    {
	int longNameFieldSize=0;
	int defaultValueFieldSize=0;

	for (GetOptOpt goo : optsHelp)
	    {
		if (goo.lname.length()>longNameFieldSize)
		    longNameFieldSize=goo.lname.length();

		if (goo.stringDefaultValue().length()>defaultValueFieldSize)
		    defaultValueFieldSize=goo.stringDefaultValue().length();
	    }

	longNameFieldSize+=2;
	longNameFieldSize=Math.max(longNameFieldSize, 8);

	defaultValueFieldSize=Math.max(defaultValueFieldSize,8);

	outs.write("option flags");
	fill(outs,longNameFieldSize-6);

	outs.write("default");
	fill(outs,defaultValueFieldSize-3);

	outs.write("description\n");

	outs.write("------------");
	fill(outs,longNameFieldSize-6);

	outs.write("-------");
	fill(outs,defaultValueFieldSize-3);

	outs.write("-----------\n");

	for (GetOptOpt goo : optsHelp)
	    {
		// write the short option
		if (goo.sname!=0)
		    outs.write("-"+goo.sname);
		else
		    outs.write("  ");

		// (SPACER)
		outs.write("  ");

		// write the long option
		outs.write("--"+goo.lname);
		fill(outs,longNameFieldSize-goo.lname.length());

		// write the default value
		outs.write("["+goo.stringDefaultValue()+"]");
		fill(outs,defaultValueFieldSize-goo.stringDefaultValue().length());

		// (SPACER)
		outs.write("  ");

		// write the help description
		ArrayList<String> lines=wrapText(goo.getHelpMessage(),helpMessageWidth);
		for (int i=0;i<lines.size();i++)
		    {
			if (i==0)
			    outs.write(lines.get(i)+"\n");
			else
			    {
				fill(outs,longNameFieldSize+defaultValueFieldSize+2+2+2+4+2);
				outs.write(lines.get(i)+"\n");
			    }
		    }
	    }
	outs.flush();
    }

    protected ArrayList<String> wrapText(String in, int width)
    {
	ArrayList<String> outs=new ArrayList<String>();

	String[] ins=in.split(" ");

	String currentLine="";
	for (String word : ins)
	    {
		if (currentLine.length()+1+word.length()<width)
		    {
			if (currentLine.length()>0)
			    currentLine=currentLine+" ";
			currentLine=currentLine+word;
			continue;
		    }
		else
		    {
			outs.add(currentLine);
			currentLine=word;
		    }
	    }
	if (currentLine.length()>0)
	    outs.add(currentLine);

	return outs;

    }

    protected void fill(Writer outs, int len) throws IOException
    {
	for (int i=0;i<len;i++)
	    outs.write(" ");
    }

    /** Simple sample code to show how to use the library. **/
    public static void main(String[] args)
    {
	GetOpt opts=new GetOpt();

	opts.addString('f',"file","stdout","Specify the input file");
	opts.addBoolean('v',"verbose",false,"Enable verbose mode");
	opts.addBoolean('h',"help",false,"See this help screen");
	opts.addInt('p',"port",25,"Set default port");

	if (!opts.parse(args))
	    {
		System.out.println("option error: "+opts.getReason());
	    }

	if (opts.getBoolean("help"))
	    opts.doHelp();

	opts.dump();
    }
}
