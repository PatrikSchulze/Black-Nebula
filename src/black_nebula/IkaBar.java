package black_nebula;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class IkaBar
{
	static Sprite[] sprites = new Sprite[10];
	
	static void init(TextureAtlas atlas)
	{
		TextureRegion tr = atlas.findRegion("ika-hp-box-filled");
		float ika_height = tr.getRegionHeight()*1;
		float ika_width  = tr.getRegionWidth()*1;
		for (int ii = 0;ii< 10;ii++)
		{
			sprites[ii] = new Sprite(tr);
			sprites[ii].setBounds(10+(ika_width*ii), 10, ika_width, ika_height);
			sprites[ii].setColor(new Color(0.72f, 0.75f, 1f, 0.97f));
		}
	}
	
	static void render(SpriteBatch batch, float hp)
	{
		for (int ii = 0;ii< 10;ii++)
		{
			if ((int)Math.floor((int)(hp*10)) == ii)
			{
				float sx = 1f;
				if (ii > 0) sx = ((hp*10)%ii);
				else sx = (hp*10);
//				sprites[ii].setScale(sx, 1f);
				sprites[ii].setColor(0.72f, 0.75f*sx, 1f*sx, sx);
				sprites[ii].draw(batch);
			}
			else if (ii > (int)Math.floor((int)(hp*10)))
			{
				//dont render
			}
			else
			{
				sprites[ii].setColor(0.72f, 0.75f, 1f, 1f);
				sprites[ii].draw(batch);
			}
		}
	}
}
