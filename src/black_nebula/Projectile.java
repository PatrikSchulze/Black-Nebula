package black_nebula;

import java.util.Random;

import whitealchemy.SquareRoot;
import whitealchemy.Trigonometrics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Projectile extends NebulaSprite
{
	/*
	 * THIN is used by crawlers where they shoot those all around them
	 * ANGULAR change direction often, fiddle around
	 * HOMING, do exactly that
	 * SCYTHE are these crosses that rotate
	 * DEFAULT just a normal shot
	 * THICK_DOT pretty much default too, just looks a little different
	 * BALL well round projectiles, they can wave as a shot
	 */
	public static enum TYPE { THIN, ANGULAR, HOMING, SCYTHE, DEFAULT, THICK_DOT, BALL; }
	TYPE type;
	
	Entity reRouteTarget = null;
	float wave = 1;
	Entity owner = null;
	Vector2 speedVector;
	boolean rotating = false;
	private long timer = 0;
	private Random r = new Random();
	private int randomLifeSpanOffset = 0;
	int lifespan = 0;
	boolean orbiter = false;
	float orbitDeg = 0;
	
	public Projectile(float _x, float _y, TextureRegion in, Entity en, TYPE _t)
	{
		super(_x, _y, in);
		type = _t;
		owner = en;
//		this.setColor(1f,1f,1f,0f);
		speedFactor = 16f;
		speedVector = new Vector2(0,0);
	}
	
	public void setDirectionPoint(float dx, float dy, Array<Entity> enemies, Entity player, Boss boss)
	{
		dx = dx - centerX;
		dy = dy - centerY;

		double distance = SquareRoot.fastSqrt((int)(dx*dx + dy*dy));
		
		float targetXSpeed = (float)(speedFactor*dx/distance);
		float targetYSpeed = (float)(speedFactor*dy/distance);
		
		speedVector.set(targetXSpeed, targetYSpeed);
		setRotation(speedVector.angle());
		
		timer = System.currentTimeMillis();
		wave = 10;
		
		if (type == TYPE.HOMING)
		{
			speedVector.set(targetXSpeed/1.5f, targetYSpeed/1.5f);
			setRotation(speedVector.angle());
			
			randomLifeSpanOffset = r.nextInt(300)-150;
			lifespan = 1600;
			
			if (owner == player && !boss.sleeping && (int)(Trigonometrics.getDistanceFast(this.centerX, this.centerY, boss.centerX, boss.centerY)) < NebulaGame.SPAWN_RANGE*2)
			{
				reRouteTarget = boss;
			}
			else if (owner == boss && (int)(Trigonometrics.getDistanceFast(this.centerX, this.centerY, player.centerX, player.centerY)) < NebulaGame.SPAWN_RANGE*2)
			{
				reRouteTarget = player;
			}
			else
			{
				for (int i = enemies.size - 1; i >= 0; i--)
				{
					Entity e = enemies.get(i);
//					if (e.getType() == Entity.TYPE.ASTEROID) continue;
					int d = (int)(Trigonometrics.getDistanceFast(this.centerX, this.centerY, e.centerX, e.centerY));
					if (d < NebulaGame.SPAWN_RANGE*2)
					{
						if (reRouteTarget == null)
							reRouteTarget = e;
						else if (((int)Trigonometrics.getDistanceFast(this.centerX, this.centerY, reRouteTarget.centerX, reRouteTarget.centerY)) > d)
						{
							reRouteTarget = e;
						}
					}
				}
			}
		}
		else if(this.type == TYPE.ANGULAR)
		{
			lifespan = 900;
		}
		else if(this.type == TYPE.DEFAULT)
		{
			lifespan = 400;
		}
		else if(this.type == TYPE.BALL)
		{
			speedVector.rotate(-36);
			setRotation(speedVector.angle());
		}
	}
	
	public void compute(boolean fps30Compute)
	{
		if (rotating)
		{
			this.setRotation(getRotation()+25f);
			if (getRotation() < 360) setRotation(getRotation()-360);
		}
		
		if (getColor().a < 1.0f)
		{
			float a = getColor().a+0.05f;
			if (a > 1.0f) a = 1.0f;
			setColor(getColor().r, getColor().g, getColor().b, a);
		}
		
		speedX = speedVector.x;
		speedY = speedVector.y;
		
		if (owner != null)
		{
			speedX+=owner.speedX;
			speedY+=owner.speedY;
			
			boolean oppoDirsX = ((speedX > 0 && owner.speedX < 0) || (speedX < 0 && owner.speedX > 0));
			boolean oppoDirsY = ((speedY > 0 && owner.speedY < 0) || (speedY < 0 && owner.speedY > 0));
			if (Math.abs(owner.speedX) >= 0.00f && Math.abs(owner.speedX) <= 1.05f) oppoDirsX = false;
			if (Math.abs(owner.speedY) >= 0.00f && Math.abs(owner.speedY) <= 1.05f) oppoDirsY = false;
			
			if (oppoDirsX)	speedX-=owner.speedX*0.5f;
			if (oppoDirsY)	speedY-=owner.speedY*0.5f;
		}
		
		super.compute(fps30Compute);
		
		if (this.type == TYPE.BALL)
		{
			if (!orbiter)
			{
				if (timer > 0)
				{
					if (System.currentTimeMillis() - timer > 100)
					{
						wave  = -wave;
						timer = System.currentTimeMillis();
					}
				}
				speedVector.rotate(wave);
			}
		}
		else if (this.type == TYPE.HOMING)
		{
			if (reRouteTarget != null && !reRouteTarget.removeMe && (System.currentTimeMillis() - timer > 220) )
			{
				float dx = reRouteTarget.centerX;
				float dy = reRouteTarget.centerY;
				
				dx = dx - centerX;
				dy = dy - centerY;
				int targetAngle = (int)(MathUtils.radiansToDegrees*MathUtils.atan2(dy, dx));
				if (targetAngle < 0) targetAngle+=360;
				if (targetAngle > 360) targetAngle-=360;
				
				float angleDelta = targetAngle - (int)speedVector.angle();
				
//				System.out.println("curA: "+(int)speedVector.angle()+"   targA: "+targetAngle+"    Delta: "+angleDelta);
				
				if (Math.abs(angleDelta) > 3)
				{
					float rota = angleDelta;
					if (rota > 10f) rota = 10f;
					if (rota < -10f) rota = -10f;
					if (Math.abs(angleDelta) > 180) rota=-rota;
//					System.out.println("Doing course change: "+rota);
					speedVector.rotate(rota);
//					System.out.println("CurA now: "+speedVector.angle());
				}
				
				setRotation(speedVector.angle());
			}
		}
		else if (this.type == TYPE.ANGULAR)
		{
			if (System.currentTimeMillis() - timer > 100)
			{
				speedVector.rotate(r.nextInt(120)-60);
				setRotation(speedVector.angle());
				lifespan-=(System.currentTimeMillis() - timer);
				timer = System.currentTimeMillis();
				if (lifespan < 0) lifespan = 1;
			}
			
		}
		
		if (lifespan > 0)
		{
			if ((System.currentTimeMillis() - timer > lifespan+randomLifeSpanOffset))
			{
				if (getColor().a > 0f)
				{
					if (getColor().a >= 0.1f) setColor(getColor().r,getColor().g,getColor().b,getColor().a-0.1f);
					else 				      setColor(getColor().r,getColor().g,getColor().b, 0);
					if (getColor().a < 0f) setColor(getColor().r,getColor().g,getColor().b, 0f);
				}
				else
				{
					removeMe = true;
				}
			}
		}
		
	}
}
