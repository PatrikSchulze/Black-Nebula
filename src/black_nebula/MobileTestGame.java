package black_nebula;

import whitealchemy.Trigonometrics;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
 
public class MobileTestGame implements ApplicationListener
{
	private Vector2 curLeftFinger, curRightFinger, leftFingerSource, rightFingerSource;
	private int leftFingerIndex = -1;
	private int rightFingerIndex = -1;

	private OrthographicCamera hud_camera;
	private SpriteBatch spriteBatch;
	
	private CameraController controller;
    private MyGestureDetector gestureDetector;
    
    private TextureRegion tregStick;
    private TextureRegion tregMiniStick;
    
    private TextureRegion tregArrow;
    
    private BitmapFont font;
	 
    
    
    public static void main(String args[])
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.samples = 0;
		config.resizable = false;
		config.vSyncEnabled = true;

		config.width = 1280;
		config.height = 720;
		config.fullscreen = false;
		
//		config.audioDeviceBufferCount = 10;

		new LwjglApplication(new MobileTestGame(), config);
	}
 
	@Override
	public void create()
	{
		System.out.println("Black Nebula\n");
//		if (!isOnMobileDevice()) Gdx.input.setCursorCatched(true);
//		Gdx.input.
		spriteBatch 	= new SpriteBatch();
		Gdx.input.setCatchBackKey(true);
		

		hud_camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		hud_camera.setToOrtho(true);
		
		font 			= new BitmapFont(Gdx.files.internal("font/sansation.fnt"), true);
		
		controller 		= new CameraController();
		gestureDetector = new MyGestureDetector(20, 0.5f, 2, 0.15f, controller);
        Gdx.input.setInputProcessor(gestureDetector);
        
        tregStick = new TextureRegion(new Texture(Gdx.files.internal("content/textures/stick_med.png")));
        tregMiniStick = new TextureRegion(new Texture(Gdx.files.internal("content/textures/mini_stick.png")));
        
        tregArrow = new TextureRegion(new Texture(Gdx.files.internal("content/textures/arrow.png")));
        
        
        tregStick.flip(false, true);
        tregMiniStick.flip(false, true);
        tregArrow.flip(false, true);

    	
//    	leftFingerSource = new Vector2(200,200);
//        rightFingerSource = new Vector2(500,200);

	}

	@Override
	public void resume()
	{
	}

	public void checkKeys()
	{
		if (Gdx.input.isKeyPressed(Keys.BACK))
		{
			Gdx.app.exit();
		}

		//mobile controls
//		if (isFingerOnRightStick(fingerOne))
//		{
//			if (player.shootingDelay == 0)
//			{
//				float radiansToMouse = MathUtils.atan2(stickRightPoint.y-fingerOne.y, stickRightPoint.x-fingerOne.x);
//				float angleToMouse = 57.2957795f*radiansToMouse;
//			
//				addShot(angleToMouse, player);
//			}
//		}
//		else if (isFingerOnRightStick(fingerTwo))
//		{
//			if (player.shootingDelay == 0)
//			{
//				float radiansToMouse = MathUtils.atan2(stickRightPoint.y-fingerTwo.y, stickRightPoint.x-fingerTwo.x);
//				float angleToMouse = 57.2957795f*radiansToMouse;
//			
//				addShot(angleToMouse, player);
//			}
//		}
//
//		if (isFingerOnLeftStick(fingerOne))
//		{
//			float dAngle = 0;//getDeltaAngleFromWASD();
//			player.targetRotation = dAngle;
//			player.rotateToTargetRotation();
//			player.updateChasingpoint(player.targetRotation, 200);
//			
//			
////			player.rotation = Trigonometrics.getAngleBetweenPointsDeg(player.centerX, player.centerY, player.chasingPoint.x, player.chasingPoint.y);
//			
//			
//		    Vector2 v = Trigonometrics.getSpeedDeg(player.chasingPoint.x, player.chasingPoint.y, player.centerX, player.centerY, player.speedFactor);
//		    player.speedX = v.x;
//		    player.speedY = v.y;
//		}
//		else if (isFingerOnLeftStick(fingerTwo))
//		{
//			double dx = fingerTwo.x - stickLeftPoint.x; 
//    		double dy = fingerTwo.y - stickLeftPoint.y; 
////    		double distance = Math.hypot(dx,dy); 
//            double distance = SquareRoot.fastSqrt((int)(dx*dx + dy*dy));
//            float targetAngle = 57.2957795f*MathUtils.atan2((float)dy, (float)dx);
//            targetAngle=targetAngle+180;
//            
//            player.setRotation(targetAngle);
//		    
//		    if ( distance > player.speedFactor )
//		    { 
//		        // still got a way to go, so take a full step 
//		    	player.speedX = (float)(player.speedFactor*dx/distance); 
//		    	player.speedY = (float)(player.speedFactor*dy/distance); 
//		    }
//		}
    	
	}

//	private void handleDesktopInputs()
//	{
//		if (hp > 0f)
//		{
//			if (isWASDPressed())
//			{
//				float dAngle = getAngleFromWASD();//getDeltaAngleFromWASD();
//				player.targetRotation = dAngle;
//				player.rotateToTargetRotation();
//				player.updateChasingpoint(player.targetRotation, 200);
//				
//				
//	//			player.rotation = Trigonometrics.getAngleBetweenPointsDeg(player.centerX, player.centerY, player.chasingPoint.x, player.chasingPoint.y);
//				
//				
//			    Vector2 v = Trigonometrics.getSpeedDeg(player.chasingPoint.x, player.chasingPoint.y, player.centerX, player.centerY, player.speedFactor);
//			    
//			    player.speedX = v.x;
//			    player.speedY = v.y;
//			    
//			    if (player.turbo)
//			    {
//			    	player.speedX*=1.8f;
//			    	player.speedY*=1.8f;
//			    }
//			}
//	
//			if (Gdx.input.isTouched())
//			{
//				if (player.shootingDelay == 0)
//				{
//					addShot(Trigonometrics.getAngleBetweenPointsDeg(getRelativeMouseX(), getRelativeMouseY(), player.centerX, player.centerY), player);
//	//				addShotPlayer(getRelativeMouseX(), getRelativeMouseY());
//				}
//			}
//			else if (isArrowsPressed())
//			{
//				if (player.shootingDelay == 0)
//				{
//					addShot(getAngleFromArrows(), player);
//				}
//			}
//		}
//	}


	@Override
	public void resize(int width, int height) {
	}
 
	@Override
	public void pause()
	{
		 
	}
 
	@Override
	public void dispose() {
	}
	

	class CameraController implements GestureListener
	{
		@Override
		public boolean fling(float velocityX, float velocityY, int button)
		{
			return false;
		}

		@Override
		public boolean longPress(float x, float y)
		{
			return false;
		}

		@Override
		public boolean pan(float x, float y, float deltaX, float deltaY)
		{
			return false;
		}

		@Override
		public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2)
		{
			return false;
		}

		@Override
		public boolean tap(float x, float y, int count, int button)
		{
			if (true)
        	{
        		if (true && count > 1)
        		{
        			System.out.println("use Bomb");
        		}
        	}
            return false;
		}

		@Override
		public boolean touchDown(float x, float y, int pointer, int button)
		{
			return false;
		}

		@Override
		public boolean zoom(float initialDistance, float distance)
		{
			return false;
		}
        
	}
	
	private class MyGestureDetector extends GestureDetector
	{
		public MyGestureDetector(GestureListener listener)
		{
			super(listener);
		}

		public MyGestureDetector(int halfTapSquareSize, float tapCountInterval, float longPressDuration, float maxFlingDelay, GestureListener listener)
		{
			super(halfTapSquareSize, tapCountInterval, longPressDuration, maxFlingDelay, listener);
		}
		
		@Override
        public boolean touchUp(int x, int y, int pointer, int button)
        {
			if (pointer == leftFingerIndex)
        	{
        		leftFingerSource = null;
        		curLeftFinger = null;
        		leftFingerIndex = -1;
        		System.out.println("Upping left: "+pointer);
        	}
        	else if (pointer == rightFingerIndex)
        	{
        		rightFingerSource = null;
        		curRightFinger = null;
        		rightFingerIndex = -1;
        		System.out.println("Upping right: "+pointer);
        	}
        	else
        	{
        		System.out.println("Upping unknown: "+pointer);
        	}
			
//			if(true)//if (isOnMobileDevice())
//			{
//				numberOfFingers--;
//				
//				if (pointer == fingerOnePointer)
//				{
//					fingerOne.set(0, 0);
//				}
//				
//				if (pointer == fingerTwoPointer)
//				{
//					fingerTwo.set(0, 0);
//				}
//				 
//				// just some error prevention... clamping number of fingers (ouch! :-)
//				if(numberOfFingers<0)
//				{
//					numberOfFingers = 0;
//				}
//			}
			
        	return super.touchUp(x, y, pointer, button);
        }
		
		public boolean touchDragged(int x, int y, int pointer)
		{
			if (pointer == leftFingerIndex)
        	{
        		curLeftFinger = new Vector2(x, y);
        	}
        	else if (pointer == rightFingerIndex)
        	{
        		curRightFinger = new Vector2(x, y);
        	}
			
//			if(true)//if (isOnMobileDevice())
//			{
//				if (pointer == fingerOnePointer)
//				{
//					fingerOne.set(x, y);
//				}
//				
//				if (pointer == fingerTwoPointer)
//				{
//					fingerTwo.set(x, y);
//				}
//			}
			
			return super.touchDragged(x, y, pointer);
		}
        
        @Override
        public boolean touchDown (int x, int y, int pointer, int button)
        {
        	
        	if (x < Gdx.graphics.getWidth()*0.5f)
        	{
        		leftFingerSource = new Vector2(x, y);
        		leftFingerIndex = pointer;
        		System.out.println("Left is now index: "+pointer);
        	}
        	else
        	{
        		rightFingerSource = new Vector2(x, y);
        		rightFingerIndex = pointer;
        		System.out.println("Right is now index: "+pointer);
        	}
        	
//        	if(true)//if (isOnMobileDevice())
//        	{
//	        	numberOfFingers++;
//	        	if(numberOfFingers == 1)
//	        	{
//	        		fingerOnePointer = pointer;
//	        		fingerOne.set(x, y);
//	        		stickLeftPoint.set(x, y);
//	        	}
//	        	else if(numberOfFingers == 2)
//	        	{
//	        		fingerTwoPointer = pointer;
//	        		fingerTwo.set(x, y);
//	        		stickRightPoint.set(x, y);
//	        	}
//        	}
        	
        	return super.touchDown(x, y, pointer, button);
        }
		
		@Override public boolean scrolled(int amount) // MOUSE WHEEL
		{
			return super.scrolled(amount);
		}
		
	}

	@Override
	public void render()
	{
		checkKeys();

		Gdx.graphics.getGL10().glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		
//		//RENDERING
		spriteBatch.begin();
		spriteBatch.setProjectionMatrix(hud_camera.combined);
		
		if (leftFingerSource != null) spriteBatch.draw(tregStick, leftFingerSource.x-(tregStick.getRegionWidth()*0.5f), leftFingerSource.y-(tregStick.getRegionHeight()*0.5f));
		if (curLeftFinger != null) spriteBatch.draw(tregMiniStick, curLeftFinger.x-(tregMiniStick.getRegionWidth()*0.5f), curLeftFinger.y-(tregMiniStick.getRegionHeight()*0.5f));

		if (rightFingerSource != null) spriteBatch.draw(tregStick, rightFingerSource.x-(tregStick.getRegionWidth()*0.5f), rightFingerSource.y-(tregStick.getRegionHeight()*0.5f));
		if (curRightFinger != null) spriteBatch.draw(tregMiniStick, curRightFinger.x-(tregMiniStick.getRegionWidth()*0.5f), curRightFinger.y-(tregMiniStick.getRegionHeight()*0.5f));
		
		float angle1 = 0;
		float angle2 = 0;
		
		if (leftFingerSource != null && curLeftFinger != null)
		{
			angle1 = Trigonometrics.getAngleBetweenPointsDeg(curLeftFinger.x, curLeftFinger.y, leftFingerSource.x, leftFingerSource.y);
			font.draw(spriteBatch, ""+(int)angle1, leftFingerSource.x, leftFingerSource.y+80);
		}
		
		if (rightFingerSource != null && curRightFinger != null)
		{
			angle2 = Trigonometrics.getAngleBetweenPointsDeg(curRightFinger.x, curRightFinger.y, rightFingerSource.x, rightFingerSource.y);
			font.draw(spriteBatch, ""+(int)angle2, rightFingerSource.x, rightFingerSource.y+80);
		}
		
		spriteBatch.draw(tregArrow, Gdx.graphics.getWidth()*0.25f, Gdx.graphics.getHeight()*0.25f, 32, 32, tregArrow.getRegionWidth(), tregArrow.getRegionHeight(), 1, 1, angle1);
		spriteBatch.draw(tregArrow, Gdx.graphics.getWidth()*0.75f, Gdx.graphics.getHeight()*0.25f, 32, 32, tregArrow.getRegionWidth(), tregArrow.getRegionHeight(), 1, 1, angle2);
		
		
		spriteBatch.end();
	}
}