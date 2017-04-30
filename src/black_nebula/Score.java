package black_nebula;

public class Score
{
	private String name;
	private int points;
	private String paddedPointsString;
	
	public Score(String _n, int _p)
	{
		name = _n;
		points = _p;
		paddedPointsString = addCommasToNumericString(""+points);
		
		
	}
	
	public String getPaddedPointsString() {
		return paddedPointsString;
	}
	
	public String getName() {
		return name;
	}
	public int getPoints() {
		return points;
	}
	
	public String toString()
	{
		return name+"->"+points;
	}
	
	public static String addCommasToNumericString (String digits)
	{
	    String result = "";
	    int len = digits.length();
	    int nDigits = 0;

	    for (int i = len - 1; i >= 0; i--)                      
	    {
	        result = digits.charAt(i) + result;                 
	        nDigits++;                                          
	        if (((nDigits % 3) == 0) && (i > 0))                
	        {
	            result = "," + result;
	        }
	    }
	    return (result);
	}
}