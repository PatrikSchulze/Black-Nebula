package desktop;

import java.awt.GraphicsEnvironment;

import black_nebula.NebulaGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Run
{
	
	public static void main(String args[])
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.samples = 0;
		config.resizable = false;
		config.vSyncEnabled = true;
//		config.useGL20 = true;
		
		
//		if (!NebulaGame.DEVKIT)
//		{
//			DisplayMode[] dm = LwjglApplicationConfiguration.getDisplayModes();
//    		
//        	boolean foundOne = false;
//    		for (int i=0;i<dm.length;i++)
//    		{
//    			if (dm[i].width == 1024 && dm[i].height == 768)
//    			{
//    				config.width  = 1024;
//    				config.height = 768;
//    				foundOne = true;
//    				break;
//    			}
//    			else if (dm[i].width == 1024 && dm[i].height == 600)
//    			{
//    				config.width  = 1024;
//    				config.height = 600;
//    				foundOne = true;
//    				break;
//    			}
//    		}
//    		
//    		if (!foundOne)
//    		{
//    			config.width  = 800;
//				config.height = 600;
//    		}
//    		
//    		config.fullscreen = true;
//		}
//		else
//		{
//			config.width = 1024;
//			config.height = 768;
//			config.fullscreen = false;
//		}

		
//		config.width  = (int)(800*1.0f);
//		config.height = (int)(480*1.0f);
		config.width  = 1024;
		config.height = 600;
		
		
		int goWidth  = (int)GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth();
		int goHeight = (int)GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight();
		
		
		if (goWidth > 1280)
			goWidth = 1280;
		
		if (goHeight > 720)
			goHeight = 720;
		
		
		config.width  = (int)goWidth;
		config.height = (int)goHeight;
		
		config.fullscreen = false;
		
		config.addIcon("content/grafx/icon128.png", FileType.Internal);
		config.addIcon("content/grafx/icon64.png",  FileType.Internal);
		config.addIcon("content/grafx/icon32.png",  FileType.Internal);
		config.addIcon("content/grafx/icon16.png",  FileType.Internal);
		
		config.title = "Black Nebula";
		
		new LwjglApplication(new NebulaGame(60f), config);
	}
}
