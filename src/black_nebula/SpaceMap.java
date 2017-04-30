package black_nebula;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class SpaceMap
{
	private Vector3 calcV3 = new Vector3(0,0,0);
	TextureRegion imgCloudSrc = null;
	TextureRegion imgBlueSpace = null;
	private static Color tempColor = new Color(1,1,1,1);
//	ArrayList<Sprite> bgImgs;
	
	public SpaceMap(TextureRegion clouds, TextureRegion blue)
	{
		imgCloudSrc = clouds;
		imgBlueSpace = blue;
//		bgImgs = new ArrayList<Sprite>();
	}
	
//	public void tryToAddBgImg(TextureRegion treg, float x, float y)
//	{
//		Sprite jo = new Sprite(treg);
//		
//		jo.setPosition(x, y);
//		
//		for (Sprite jap : bgImgs)
//		{
//			if (jap.getBoundingRectangle().overlaps(jo.getBoundingRectangle()))
//			{
//				return;
//			}
//		}
//		
//		bgImgs.add(jo);
//	}
	
	private boolean isTextureWithinFrustum(float x, float y, OrthographicCamera cam, float tex_size)
	{
		calcV3.x = x;
		calcV3.y = y;
		if (cam.frustum.pointInFrustum(calcV3))
		{
			return true;
		}
		
		calcV3.x = x+tex_size;
		calcV3.y = y;
		if (cam.frustum.pointInFrustum(calcV3))
		{
			return true;
		}
		
		calcV3.x = x;
		calcV3.y = y+tex_size;
		if (cam.frustum.pointInFrustum(calcV3))
		{
			return true;
		}
		
		calcV3.x = x+tex_size;
		calcV3.y = y+tex_size;
		if (cam.frustum.pointInFrustum(calcV3))
		{
			return true;
		}		
		
		return false;
	}
	
//	public void renderBgImgs(SpriteBatch spriteBatch, OrthographicCamera cam)
//	{
//		for (Sprite jap : bgImgs)
//		{
//			if (isTextureWithinFrustum(jap.getX(), jap.getY(), cam, jap.getWidth()))
//			{
//				jap.draw(spriteBatch);
//			}
//		}
//	}
	
	public void render(SpriteBatch spriteBatch, OrthographicCamera cam, Entity player, Color c)
	{
		tempColor = spriteBatch.getColor();
		spriteBatch.setColor(c);
		
		int leOffsetX = -(imgBlueSpace.getRegionWidth()*2);
		if (cam.position.x < 0)
		{
			leOffsetX = -(imgBlueSpace.getRegionWidth()*3);
		}
		
		int leOffsetY = -(imgBlueSpace.getRegionHeight()*2);
		if (cam.position.y < 0)
		{
			leOffsetY = -(imgBlueSpace.getRegionHeight()*3);
		}
		
		for (int offsetY = leOffsetY; offsetY < cam.viewportHeight+(imgBlueSpace.getRegionHeight()*2); offsetY+=imgBlueSpace.getRegionHeight())
		{
			for (int offsetX = leOffsetX; offsetX < cam.viewportWidth+(imgBlueSpace.getRegionWidth()*2); offsetX+=imgBlueSpace.getRegionWidth())
			{
				float x = cam.position.x-(cam.viewportWidth /2f);
				float y = cam.position.y-(cam.viewportHeight/2f);
				float realX = x+offsetX-((player.centerX/NebulaGame.PARALLAX_FACTOR)%imgBlueSpace.getRegionWidth());
				float realY = y+offsetY-((player.centerY/NebulaGame.PARALLAX_FACTOR)%imgBlueSpace.getRegionHeight());
				if (isTextureWithinFrustum(realX, realY, cam, imgBlueSpace.getRegionWidth()))
				{
					spriteBatch.draw(imgBlueSpace, realX, realY);
				}
			}
		}
		
		spriteBatch.setColor(tempColor);
	}
	
	public void renderClouds(SpriteBatch spriteBatch, OrthographicCamera cam, Entity player, Color c)
	{	
		tempColor = spriteBatch.getColor();
		spriteBatch.setColor(c);
		
		int leOffsetX = -(imgCloudSrc.getRegionWidth()*1);
		if (cam.position.x < 0)
		{
			leOffsetX = -(imgCloudSrc.getRegionWidth()*2);
		}
		
		int leOffsetY = -(imgCloudSrc.getRegionHeight()*1);
		if (cam.position.y < 0)
		{
			leOffsetY = -(imgCloudSrc.getRegionHeight()*2);
		}
		
		for (int offsetY = leOffsetY; offsetY < cam.viewportHeight+(imgCloudSrc.getRegionHeight()*2); offsetY+=imgCloudSrc.getRegionHeight())
		{
			for (int offsetX = leOffsetX; offsetX < cam.viewportWidth+(imgCloudSrc.getRegionWidth()*2); offsetX+=imgCloudSrc.getRegionWidth())
			{
				float x = cam.position.x-(cam.viewportWidth /2f);
				float y = cam.position.y-(cam.viewportHeight/2f);
				float realX = x+offsetX-(player.centerX%imgCloudSrc.getRegionWidth());
				float realY = y+offsetY-(player.centerY%imgCloudSrc.getRegionHeight());
				if (isTextureWithinFrustum(realX, realY, cam, imgCloudSrc.getRegionWidth()))
				{
					spriteBatch.draw(imgCloudSrc, realX, realY);
				}
			}
		}
		
		spriteBatch.setColor(tempColor);
	}
}
