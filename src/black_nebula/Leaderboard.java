package black_nebula;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class Leaderboard
{
	private static String key = "none";
	public static Score[] scores = null;
	private static String responseString = "";
	
	static TextField  textField;
	static TextButton submitButton;
	static TextButton backButton;
	static Skin skin;
	
	static
	{
		key = "__.__#~";
	}
	
	public static void pollScores()
	{
		 HttpRequest httpGet = new HttpRequest(HttpMethods.GET);
		 httpGet.setUrl("http://alchemic-tempest.com/nebula_web/getscore.php");
		 
		 Gdx.net.sendHttpRequest (httpGet, new HttpResponseListener()
		 {
	        public void handleHttpResponse(HttpResponse httpResponse)
	        {
        		String ble = httpResponse.getResultAsString();
        		
        		if (ble == null) scores = null;
        		String[] lines = ble.split("<br>");
        		Score[] _scores = new Score[lines.length];
        		
        		for (int i=0;i<lines.length;i++)
        		{
        			_scores[i] = new Score(lines[i].split(" ")[0], Integer.parseInt(lines[i].split(" ")[1]));
        		}

        		scores = _scores;
	        }
	 
	        public void failed(Throwable t)
	        {
        		t.printStackTrace();
    			scores = null;
	        }
		 });
	}
	
	public static void sendScoreToServer(String inputName, int inputScore)
	{
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("name", inputName);
		map.put("score", ""+inputScore);
		map.put("key", key);
		sendHttpRequest("http://alchemic-tempest.com/nebula_web/sendscore.php", map, HttpMethods.POST);
	}
	
	public static void sendHttpRequest(String strUrl, HashMap<String,String> data, String method)
	{
//		Map parameters = new HashMap();
	//		 parameters.put("user", "myuser");
		 
		 HttpRequest httpGet = new HttpRequest(method);
		 httpGet.setUrl(strUrl);
		 if (data != null) httpGet.setContent(HttpParametersUtils.convertHttpParameters(data));
		 
		 
		 Gdx.net.sendHttpRequest (httpGet, new HttpResponseListener()
		 {
		        public void handleHttpResponse(HttpResponse httpResponse)
		        {
		        		responseString = httpResponse.getResultAsString();
		                //do stuff here based on response
		        }
		 
		        public void failed(Throwable t) {
		        		responseString = null;
		                //do stuff here based on the failed attempt
		        }
		 });
	 }
	
	public static String getResponseString() { return responseString; }
}
