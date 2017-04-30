package black_nebula;

import com.badlogic.gdx.backends.lwjgl.LwjglApplet;

public class AppletLauncher extends LwjglApplet
{
	private static final long serialVersionUID = 1L;
	
	public AppletLauncher()
    {
        super(new NebulaGame(60), false);
    }
}
