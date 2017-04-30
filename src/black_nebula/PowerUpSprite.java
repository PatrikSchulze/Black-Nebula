package black_nebula;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class PowerUpSprite extends NebulaSprite
{
//	private int OFFSET_LIMIT = 3;
//	float offsetCycle = 0;
//	float offsetDelta = -0.1f;
	public static int ATTRACTION_DISTANCE = 200;
	float offsetRotation = 0;
	private float rot = 0;
	float cycleSpeed = 0.055f;
	float speedAdd = 1.0f;
	PowerUp.TYPE type;
	
	public PowerUpSprite(float _x, float _y, TextureRegion in, PowerUp.TYPE _type)
	{
		super(_x, _y, in);
		type = _type;
	}
	
	@Override
	public void compute(boolean mobileCompute)
	{
		compute(mobileCompute, false);
	}
	
	public void compute(boolean mobileCompute, boolean dance)
	{
		if (type == PowerUp.TYPE.POINTS) dance = false;
		
		if (dance)
		{
			offsetRotation+=cycleSpeed;
			if (offsetRotation > 2*MathUtils.PI) offsetRotation -= 2*MathUtils.PI;
			
			setY(getY()+MathUtils.cos(offsetRotation));
			setX(getX()+MathUtils.sin(offsetRotation));
			
			if (speedX > 0f) //when moving it should get faster
			{
				speedAdd+=0.1f;
			}
			
			speedX*=speedAdd;
			speedY*=speedAdd;
		}
		else
		{
			speedX*=2f;
			speedY*=2f;
		}
		
		if (type == PowerUp.TYPE.POINTS)
		{
			rot = this.getRotation();
			rot+=3;
			if (rot > 360) rot -=360;
			this.setRotation(rot);
		}
		

//		offsetCycle+=offsetDelta;
//		if (Math.abs(offsetCycle) >= OFFSET_LIMIT) offsetDelta = -offsetDelta;
//		this.setY(getY()+offsetCycle);
		
		super.compute(mobileCompute);
	}

}
