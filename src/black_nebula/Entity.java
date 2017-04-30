package black_nebula;

import java.util.ArrayList;

import whitealchemy.SquareRoot;
import whitealchemy.Trigonometrics;
import whitealchemy.Util;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Entity extends NebulaSprite
{
	public static enum TYPE { PROTA, DRONE, CRAWLER, GUN, ASTEROID, GLIDER, BOSS; }
	TYPE type;
	Vector2 chasingPoint 	= new Vector2(-1,-1);
	float inertia			= 0.003f;
	float targetSaltX, targetSaltY;
	float targetRotation = 0;
	private static Vector3 calcV3 = new Vector3(0,0,1);
	
    int shootingCooldown 	= 50; // in frames
    int shootingCoolCount 	= 0; // in frames
    
    int waveMoveRot			= 0;
    float collisionDamage	= 0;
    long lastTargetted;
    long reTargetDelay 		= 0;
    long stopEvadingAt 		= 0;
    float evadingAngle		= 90;
    float brake = 1.0f;
    boolean turbo			= false;
    boolean supergun		= false;
    boolean evading			= false;
    float shieldHealth 		= 0f; // number of hits it will take
	
	public Entity(float _x, float _y, TextureRegion in, TYPE _type)
	{
		super(_x, _y, in);
		type = _type;
		speedFactor = 4.0f;
		shieldHealth = 0f;
		waveMoveRot = Util.getRandom(0, 360);
		this.setColor(1, 1, 1, 0);
		
		int r = Util.getRandom(1, 2);
		if (r == 1) evadingAngle = -90;
		
		targetSaltX = Util.getRandom(-200, 200);
		targetSaltY = Util.getRandom(-200, 200);
		
		if (type == Entity.TYPE.DRONE)
		{
			speedFactor = Util.getRandom(6, 8);
			collisionDamage = 0.10f;
			targetSaltX = Util.getRandom(-20, 20);
			targetSaltY = Util.getRandom(-20, 20);
		}
		else if (type == Entity.TYPE.CRAWLER)
		{
			targetSaltX = Util.getRandom(-600, 600);
			targetSaltY = Util.getRandom(-600, 600);
			reTargetDelay = Util.getRandom(100, 1200);
			speedFactor = Util.getRandom(0.4f, 2.5f);
			collisionDamage = 0.20f;
//			shootingCooldown = 60;
			shootingCooldown = 960; // 60 * 0.016 ms (16us)
		}
		else if (type == Entity.TYPE.GUN)
		{
			speedFactor = 0;
//			shootingCooldown = 8;
			shootingCooldown = 128; // 128 * 0.016 ms (16us) = 128us
			collisionDamage = 0.20f;
		}
		else if (type == Entity.TYPE.ASTEROID)
		{
			collisionDamage = 0.10f;
			speedX = Util.getRandom(-3.0f, 3.0f);
			speedY = Util.getRandom(-3.0f, 3.0f);
			int hr = Util.getRandom(0, 1);
			int vr = Util.getRandom(0, 1);
			flip((hr == 1), (vr == 1));
			float rangle = Util.getRandom(0, 360);
			setRotation(rangle);
		}
		else if (type == Entity.TYPE.GLIDER)
		{
			speedFactor = 6.0f;
			collisionDamage = 0.15f;
		}
		else if (type == Entity.TYPE.BOSS)
		{
			speedFactor = 4.5f;
		}
	}
	
	public boolean isWithinFrustum(OrthographicCamera camera)
	{
		calcV3.x = this.getX();
		calcV3.y = this.getY();
		if (camera.frustum.pointInFrustum(calcV3))
		{
			return true;
		}
		
		calcV3.x = this.getX()+this.getWidth();
		calcV3.y = this.getY();
		if (camera.frustum.pointInFrustum(calcV3))
		{
			return true;
		}
		
		calcV3.x = this.getX();
		calcV3.y = this.getY()+this.getHeight();
		if (camera.frustum.pointInFrustum(calcV3))
		{
			return true;
		}
		
		calcV3.x = this.getX()+this.getWidth();
		calcV3.y = this.getY()+this.getHeight();
		if (camera.frustum.pointInFrustum(calcV3))
		{
			return true;
		}
		
		return false;
	}

	public TYPE getType()
	{
		return type;
	}

	public void rotateToTargetRotation()
	{
		setRotation(Trigonometrics.getAngleWithoutOverUndershootDeg(getRotation()));
    	targetRotation = Trigonometrics.getAngleWithoutOverUndershootDeg(targetRotation);
		
		if ((int)getRotation() != (int)targetRotation )
		{
			float changeA = 0;
			float turnspeed = 7;
			
			float curAngle = getRotation();
			for (int i=0;i<180;i++)
			{
				curAngle++;
				if (curAngle < 0) curAngle+=360;
				if (curAngle > 360) curAngle-=360;
				if (Math.abs(curAngle-targetRotation) < turnspeed)//if difference isnt bigger than turnspeed
				{
					changeA = turnspeed; //TURNSPEED
					break;
				}
			}
			
			curAngle = getRotation();
			for (int i=0;i<180;i++)
			{
				curAngle--;
				if (curAngle < 0) curAngle+=360;
				if (curAngle > 360) curAngle-=360;
				if (Math.abs(curAngle-targetRotation) < turnspeed)//if difference isnt bigger than turnspeed
				{
					changeA = -turnspeed; //TURNSPEED
					break;
				}
			}
			
			setRotation(getRotation()+changeA);
			
			if (Math.abs(getRotation()-targetRotation) <= turnspeed) setRotation(targetRotation);
		}
	}
	
	public void doTarget(float dx, float dy)
	{
		dx = dx - centerX;
		dy = dy - centerY;

		double distance = SquareRoot.fastSqrt((int)(dx*dx + dy*dy));
		
		if (distance < 400)
		{
			speedX = 0;
			speedY = 0;	
		}
		else
		{
			speedX = (float)(speedFactor*dx/distance);
			speedY = (float)(speedFactor*dy/distance);
		}
		
	}
	
	private void updateShotDelay(boolean fps30)
	{
		if (shootingCoolCount > 0)
		{
			shootingCoolCount++;
			if (fps30) shootingCoolCount++;
			
			if (shootingCoolCount >= shootingCooldown)
			{
				shootingCoolCount = 0;
			}
		}
	}
	
	//howmuch points does this type of enemy get you
	public int getPoints()
	{
		if (type == Entity.TYPE.DRONE)
		{
			return 100;
		}
		else if (type == Entity.TYPE.CRAWLER)
		{
			return 200;
		}
		else if (type == Entity.TYPE.GUN)
		{
			return 450;
		}
		else if (type == Entity.TYPE.ASTEROID)
		{
			return 250;
		}
		else if (type == Entity.TYPE.GLIDER)
		{
			return 300;
		}
		else if (type == Entity.TYPE.BOSS)
		{
			//does he die ?
			return 99999;
		}
		else
		{
			//what the player ?
			return 777;
		}
	}
	
	@Override public void setScale(float in)
	{
		super.setScale(in);
	}
	
	@Override public void compute(boolean doubleCompute)
	{
		if (brake > 1)
		{
			speedX/=brake;
			speedY/=brake;
			brake-=0.05f;
		}
		
		if (this.getColor().a < 1f)
		{
			float ac = getColor().a;
			ac+=0.05f;
			
			if (ac > 1f) ac = 1f;
			
			setColor(getColor().r,getColor().g,getColor().b,ac);
		}
		
    	super.compute(doubleCompute);
    	
    	if (type != TYPE.ASTEROID && type != TYPE.BOSS && type != TYPE.CRAWLER) // these types have no inertia, hurray
    	{
	    	if (speedX != 0.0f)
	    	{
	    		if (speedX > 0.0f)
	    		{
	    			speedX-=(speedX/3/speedFactor);
	    			if (speedX < 0.0f) speedX = 0.0f;
	    		}
	    		else if (speedX < 0.0f)
	    		{
	    			speedX-=(speedX/3/speedFactor);
	    			if (speedX > 0.0f) speedX = 0.0f;
	    		}
	    	}
	    	
	    	if (speedY != 0.0f)
	    	{
	    		if (speedY > 0.0f)
	    		{
	    			speedY-=(speedY/3/speedFactor);
	    			if (speedY < 0.0f) speedY = 0.0f;
	    		}
	    		else if (speedY < 0.0f)
	    		{
	    			speedY-=(speedY/3/speedFactor);
	    			if (speedY > 0.0f) speedY = 0.0f;
	    		}
	    	}
    	}
    	
    	
    	
    	updateShotDelay(doubleCompute);
    	
    	waveMoveRot+=8;
    	
    	if (waveMoveRot > 360) waveMoveRot-=360;
	}
	
	public float getWaveOffsetSin()
	{
		return MathUtils.sinDeg(waveMoveRot)*3.0f;
	}
	
//	public void updateChasingpoint(float angleDeg, float radius)
//	{
//		chasingAngle = angleDeg;
//		chasingPoint = Trigonometrics.getOrbitLocationDeg(centerX, centerY, angleDeg, radius);
//	}
	
//	public void setRotation(float rot)
//	{
//		this.setRotation(rot);
//	}
	
	public void updateChasingpoint(float radius)
	{
		chasingPoint = Trigonometrics.getOrbitLocationDeg(centerX, centerY, getRotation(), radius);
	}
}
