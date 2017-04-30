package whitealchemy;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.math.MathUtils;

/**
 * Static methods so we don't have the same methods in every other class - redundancy
 * @author Administrator
 * @version 1.2
 */
public final class Util
{
	private static int ram_max 		= 0;
	private static int ram_occupied = 0;
	private static StringBuilder ramStringBuilder = new StringBuilder();
	
	public static final String getRAMUsageString()
    {
		ram_occupied = (int)(Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory())/1024/1024;
		ram_max 	 = (int)(Runtime.getRuntime().maxMemory()/1024/1024);
		ramStringBuilder.delete(0, ramStringBuilder.length());
		ramStringBuilder.append("RAM:   ");
		ramStringBuilder.append(ram_occupied);
		ramStringBuilder.append(" MB / ");
		ramStringBuilder.append(ram_max);
		ramStringBuilder.append(" MB");
		
        return ramStringBuilder.toString();
    }
	
	public static final int countNotNullObjects(Object[] obj)
	{
		int out = 0;
        for (int h=0; h<obj.length; h++)
    	{
            if (obj[h] != null)  out++;
        }
        return out;
	}
	
	public static boolean isUnix()
	{
		 
		String os = System.getProperty("os.name").toLowerCase();
		// linux or unix
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
 
	}
	
	/**
	 * Random INCLUDING minimum value AND INCLUDING maximum value
	 * Both can return.
	 */
	public static final int getRandom(int minimum, int maximum)
    {
        return (int)(java.lang.Math.random()*((maximum+1)-minimum)+minimum);
    }

    public static final float getRandom(float minimum, float maximum)
    {
        return (float)(java.lang.Math.random()*((maximum+1)-minimum)+minimum);
    }
    
    public static void traceThis()
    {
    	new Exception("Trace").printStackTrace();
    }
    
    public static final DisplayMode[] getDisplayModes()
	{
		ArrayList<DisplayMode> relevantModes = new ArrayList<DisplayMode>(); 

		DisplayMode[] modes = Gdx.graphics.getDisplayModes();

        for (int i=0;i<modes.length;i++) 
        {
        	boolean foundsame = false;
        	Iterator<DisplayMode> itr = relevantModes.iterator();
        	while (itr.hasNext() && !foundsame)
        	{
        		DisplayMode cur = itr.next();
        		if (cur.width == modes[i].width && cur.height == modes[i].height)
        		{
        			foundsame = true;
        		}
        	}
        	
        	if (!foundsame)
        	{
        		relevantModes.add(modes[i]);
        	}
        }
    	
    	return relevantModes.toArray(new DisplayMode [0]);
	}
    
    public static final String numToN_Digits(int number, int n)
    {
    	StringBuilder str = new StringBuilder(""+number);
    	
    	int zeroesToAdd = n-str.length();
    	
    	if (zeroesToAdd > 0)
    	{
    		for (int i=0;i<zeroesToAdd;i++)
            {
    			str.insert(0, "0");
            }
    	}
    	
        return str.toString();
    }
	
	public static final String numToFiveDigits(int in)
    {
        StringBuilder str = new StringBuilder("");

        if (in < 10000) str.append("0");
        if (in < 1000)  str.append("0");
        if (in < 100)   str.append("0");
        if (in < 10)    str.append("0");

        str.append(in);

        return str.toString();
    }
	
	public static final String numToTwoDigits(int in)
    {
        StringBuilder str = new StringBuilder("");

        if (in < 10)    str.append("0");

        str.append(in);

        return str.toString();
    }
	
	public static final float getSmallFloatPercentageValue(int all, int yourValue)
	{
		return (float)getIntPercentage(all, yourValue)/100.0f;
	}
	
	public static final int getIntPercentage(int all, int yourValue)
	{
		return (int)MathUtils.round((float)((100.0d/(double)all)*(double)yourValue));
	}
	
	public static final double getPercentage(double all, double yourValue)
	{
		return ((100.0d/all)*yourValue);
	}
    
    public static final void writeToFile(String msg, String path)
    {
    	try{
    		BufferedWriter bw = new BufferedWriter(new FileWriter(path));
    		bw.write(msg);
    		bw.close();
    	}catch(Exception e) { e.printStackTrace(); }
    }

    public static final void writeToFile2(String msg, String path)
    {
    	try{
    		FileChannel rwChannel = new RandomAccessFile(path, "rw").getChannel();
    		ByteBuffer wrBuf = rwChannel.map(FileChannel.MapMode.READ_WRITE, 0, msg.length());

    		wrBuf.put(msg.getBytes());

    		rwChannel.close();
    	}catch(Exception e) { e.printStackTrace(); }
    }
    
    public final static String readFileWithChannel(String file)
    { 
    	try{
    		FileChannel channel = new FileInputStream(new File(file)).getChannel(); 
    		ByteBuffer buffer = ByteBuffer.allocate((int) channel.size()); 
    		channel.read(buffer); 
    		channel.close(); 
    		return new String(buffer.array(), "UTF8");
    	}catch(IOException e) { e.printStackTrace(); return "ERROR loading file"; }
    }
    
    public static final String readFromFile(String file)
    { 
    	try
    	{
	        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file)); 
	        Byter buffer = new Byter(); 
	        byte[] buf = new byte[1024]; 
	        int len; 
	        while ((len = in.read(buf)) != -1)
	        { 
	            buffer.put(buf, len); 
	        } 
	        in.close(); 
	        return new String(buffer.buffer, 0, buffer.write);
    	}catch(Exception e) { e.printStackTrace(); }
    	return "error";
    } 
      
    private static class Byter
    { 
        public byte[] buffer = new byte[256]; 
        public int write; 
        public void put(byte[] buf, int len)
        { 
            ensure(len); 
            System.arraycopy(buf, 0, buffer, write, len); 
            write += len; 
        } 
        private void ensure(int amt)
        { 
            int req = write + amt; 
            if (buffer.length <= req)
            { 
                byte[] temp = new byte[req * 2]; 
                System.arraycopy(buffer, 0, temp, 0, write); 
                buffer = temp; 
            } 
        } 
    }
    
    private static final double TWO_POW_450 = Double.longBitsToDouble(0x5C10000000000000L);
    private static final double TWO_POW_N450 = Double.longBitsToDouble(0x23D0000000000000L);
    private static final double TWO_POW_750 = Double.longBitsToDouble(0x6ED0000000000000L);
    private static final double TWO_POW_N750 = Double.longBitsToDouble(0x1110000000000000L);
    
    public static double hypot(double x, double y)
    {
        x = Math.abs(x);
        y = Math.abs(y);
        if (y < x) {
            double a = x;
            x = y;
            y = a;
        } else if (!(y >= x)) { // Testing if we have some NaN.
            if ((x == Double.POSITIVE_INFINITY) || (y == Double.POSITIVE_INFINITY)) {
                return Double.POSITIVE_INFINITY;
            } else {
                return Double.NaN;
            }
        }
        if (y-x == y) { // x too small to substract from y
            return y;
        } else {
            double factor;
            if (x > TWO_POW_450) { // 2^450 < x < y
                x *= TWO_POW_N750;
                y *= TWO_POW_N750;
                factor = TWO_POW_750;
            } else if (y < TWO_POW_N450) { // x < y < 2^-450
                x *= TWO_POW_750;
                y *= TWO_POW_750;
                factor = TWO_POW_N750;
            } else {
                factor = 1.0;
            }
            return factor * Math.sqrt(x*x+y*y);
        }
    }
}
