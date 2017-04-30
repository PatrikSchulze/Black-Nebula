package black_nebula;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextEffect
{
	Color color;
	float x,y;
	String msg;
	boolean removeMe = false;
	Color saveColor = null;
	
	public TextEffect(String in, float _x, float _y, Color c)
	{
		color = c;
		color.a = 1.0f;
		x = _x;
		y = _y;
		msg = in;
	}
	
	public void compute()
	{
		color.a-=0.01f;
		if (color.a <= 0f) removeMe = true;
	}
	
	public void render(SpriteBatch spriteBatch, BitmapFont font)
	{
		saveColor = font.getColor();
		font.setColor(color);
		
		font.draw(spriteBatch, msg, x, y);
		
		font.setColor(saveColor);
	}
}
