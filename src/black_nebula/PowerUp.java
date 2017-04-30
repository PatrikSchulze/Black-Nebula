package black_nebula;

import com.badlogic.gdx.graphics.Color;

public class PowerUp
{
	public static final String lifeStr 			= "Life x1";
	public static final String shieldStr 		= "Shield";
	public static final String scytheStr 		= "Scythe";
	public static final String angularStr 		= "Snake";
	public static final String homingStr 		= "Homing";
	public static final String ballStr 			= "Sphere";
	public static final String supergunStr 		= "Shot Enhance";
	public static final String turboStr 		= "Turbo";
	public static final String ringStr 			= "Ring";
	
	public static final Color lifeColor 		= Color.RED;
	public static final Color shieldColor 		= Color.BLUE;
	public static final Color scytheColor 		= new Color(0.6f, 0.1f, 0.6f, 0.9f);
	public static final Color angularColor 		= new Color(0.2f, 0.8f, 0.2f, 0.93f);
	public static final Color homingColor 		= new Color(0.2f, 0.6f, 0.7f, 1f);
	public static final Color ballColor 		= new Color(0.1f, 0.4f, 0.9f, 0.9f);
	public static final Color supergunColor 	= new Color(0.9f, 0.5f, 0f, 0.9f);
	public static final Color turboColor 		= new Color(1f, 1f, 0.35f, 0.9f);
	public static final Color ringColor 		= new Color(0.8f, 0.8f, 1f, 0.9f);
	
	public static enum TYPE { LIFE, SCYTHE, ANGULAR, HOMING, BALL, SHIELD, SUPER_GUN, TURBO, POINTS, RING; }
	TYPE type = null;
	String str = null;
	Color color = null;
	
	static
	{
//		lifeColor.mul(NebulaGame.GAMMA_FACTOR);
//		shieldColor.mul(NebulaGame.GAMMA_FACTOR);
//		scytheColor.mul(NebulaGame.GAMMA_FACTOR);
//		angularColor.mul(NebulaGame.GAMMA_FACTOR);
//		homingColor.mul(NebulaGame.GAMMA_FACTOR);
//		ballColor.mul(NebulaGame.GAMMA_FACTOR);
//		supergunColor.mul(NebulaGame.GAMMA_FACTOR);
//		turboColor.mul(NebulaGame.GAMMA_FACTOR);
//		ringColor.mul(NebulaGame.GAMMA_FACTOR);
	}
	
	public PowerUp(TYPE _type)
	{
		type = _type;
		
		if (type == TYPE.LIFE)
		{
			str = lifeStr;
			color = lifeColor;
		}
		else if (type == TYPE.SHIELD)
		{
			str = shieldStr;
			color = shieldColor;
		}
		
		else if (type == TYPE.BALL)
		{
			str = ballStr;
			color = ballColor;
		}
		else if (type == TYPE.ANGULAR)
		{
			str = angularStr;
			color = angularColor;
		}
		else if (type == TYPE.HOMING)
		{
			str = homingStr;
			color = homingColor;
		}
		else if (type == TYPE.SCYTHE)
		{
			str = scytheStr;
			color = scytheColor;
		}
		else if (type == TYPE.SUPER_GUN)
		{
			str = supergunStr;
			color = supergunColor;
		}
		else if (type == TYPE.TURBO)
		{
			str = turboStr;
			color = turboColor;
		}
		else if (type == TYPE.RING)
		{
			str = ringStr;
			color = ringColor;
		}
		else
		{
			color = Color.WHITE;
		}
		color.a = 1.0f;
	}
	
	public void compute()
	{
		color.a = color.a - 0.01f;
	}
	
	public float getAlpha()
	{
		return color.a;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public String getTypeString()
	{
		return str;
	}
}
