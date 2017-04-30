package black_nebula;

import whitealchemy.Util;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Boss extends Entity
{
	long WAKEUP_TIME = 20000;
	int BASE_POINTS = 5000;
	
	boolean sleeping;
	long wentToSleep;
	int hp = 100;
	int max_hp = 100;
	int iteration = 1;
	float alphaChange = 0f;
	private Rectangle innerRect;
	
	/*
	 * max_hp increases by 10%
	 * BASE_POINTS increases by 30%
	 * WAKEUP_TIME reduces by 10%
	 * 
	 */
	
	public Boss(float _x, float _y, TextureRegion in, TYPE _type)
	{
		super(_x, _y, in, _type);
		collisionDamage = 0.25f;
//		shootingCooldown = 8; // 8 * 0.016 ms (16us) = 128us
		shootingCooldown = 128; // 128 * 0.016 ms (16us) = 128us
		setColor(1,1,1,0);
		hp = max_hp;
		sleeping = true;
		alphaChange = -0.05f;
		setColor(getColor().r, getColor().g, getColor().b, 1.0f+alphaChange);
		wentToSleep = System.currentTimeMillis();
		innerRect = new Rectangle(getX(), getY(), getWidth(), getHeight());
	}
	
	public void updateInnerBox()
	{
		innerRect.set(getBoundingRectangle().x+(getBoundingRectangle().width*0.2f), getBoundingRectangle().y+(getBoundingRectangle().height*0.2f), getBoundingRectangle().width*0.6f, getBoundingRectangle().height*0.6f);
	}
	
	public boolean isInnerBoxOverlap(Rectangle rect)
	{
		return innerRect.overlaps(rect);
	}
	
	public boolean isInnerBoxContains(Rectangle rect)
	{
		return innerRect.contains(rect);
	}
	
	public void compute(float dx, float dy, boolean mobileCompute)
	{
		if (!sleeping)
		{
			if (System.currentTimeMillis() > lastTargetted+reTargetDelay)
			{
				lastTargetted = System.currentTimeMillis();
				doTarget(dx, dy);
			}
		}
		
		if (alphaChange != 0f && getColor().a <= 0f || getColor().a >= 1.0f)
		{
			alphaChange = 0f;
		}
		
		float newAlpha = getColor().a+alphaChange;
		if (newAlpha < 0f) newAlpha = 0f;
		if (newAlpha > 1f) newAlpha = 1f;
		
		setColor(getColor().r, getColor().g, getColor().b, newAlpha);
		
		super.compute(mobileCompute);
		updateInnerBox();
	}
	
	public float getAlpha()
	{
		return getColor().a;
	}
	
	public void wakeUp(SpaceMap map, Entity player)
	{
		wakeUp(Util.getRandom(player.centerX-(NebulaGame.SPAWN_RANGE*3), player.centerX+(NebulaGame.SPAWN_RANGE*3)),
				Util.getRandom(player.centerY-(NebulaGame.SPAWN_RANGE*3), player.centerY+(NebulaGame.SPAWN_RANGE*3)));
	}
	
	public void wakeUp(float _x, float _y)
	{
		setX(_x);
		setY(_y);
		sleeping = false;
		alphaChange = 0.05f;
		setColor(getColor().r, getColor().g, getColor().b, 0.0f+alphaChange);
	}
	
	public void resetParameters()
	{
		WAKEUP_TIME = 20000;
		BASE_POINTS = 5000;
		max_hp = 100;
		iteration = 1;
	}
	
	public void goToSleep()
	{
		max_hp*=1.1f;
		BASE_POINTS *= 1.3f;
		WAKEUP_TIME*=0.95f;
//		speedFactor*=1.1f;
		
		hp = max_hp;
		iteration++;
		sleeping = true;
		alphaChange = -0.05f;
		setColor(getColor().r, getColor().g, getColor().b, 1.0f+alphaChange);
		wentToSleep = System.currentTimeMillis();
//		setColor(1,1,1,0);
	}

}
