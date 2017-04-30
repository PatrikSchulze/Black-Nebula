package black_nebula;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class NebulaSprite extends Sprite
{
	float 			centerX 		= 0.0f;
	float 			centerY			= 0.0f;
	float 			speedX 			= 0.0f;
	float 			speedY 			= 0.0f;
	float 			speedFactor 	= 12.0f;
	boolean 		removeMe 		= false;
	
	public NebulaSprite(float _x, float _y, TextureRegion in)
	{
		super(in);
		this.setPosition(_x, _y);
		centerX = getX()+(getWidth()/2);
		centerY = getY()+(getHeight()/2);
	}
	
	public void compute(boolean doubleCompute)
	{
//		if (DeltaHelper.averageDelta != -1.0f)
//		{
//			uDelta			= DeltaHelper.averageDelta;
//		}
//		else
//		{
//			uDelta			= Gdx.graphics.getDeltaTime();
//		}
		
		
		if (doubleCompute)
		{
			setX(getX()+(speedX*2));
			setY(getY()+(speedY*2));
		}
		else
		{
			setX(getX()+(speedX));
			setY(getY()+(speedY));
		}
		centerX = getX()+(getWidth()/2.0f);
		centerY = getY()+(getHeight()/2.0f);
	}
	
	public void render(SpriteBatch batch)
	{
		draw(batch);
	}
}