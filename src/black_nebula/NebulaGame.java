package black_nebula;

import java.util.HashMap;
import java.util.Random;

import whitealchemy.FrameSkipper;
import whitealchemy.Primitives;
import whitealchemy.SquareRoot;
import whitealchemy.Trigonometrics;
import whitealchemy.Util;
import black_nebula.PowerUp.TYPE;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
 
public class NebulaGame implements ApplicationListener
{
	public static final int PREFFERED_SCREEN_WIDTH  = 800;
	public static final int PREFFERED_SCREEN_HEIGHT = 480;
	
	public static final String scoreErrorMsg = "Error retrieving scores. Leaderboard needs an Internet connection.";
	public static final String stringSlot = " ";
	public static final float GAMMA_FACTOR		= 5.0f;
	public static final boolean DEMO_VERSION	= false;
	public static final boolean BOSS_ONLY_MODE	= false;
	public static final boolean VERBOSE			= false;
	public static final boolean audio			= true;
	public static final boolean AI				= true;
	public static final boolean BOSSAI			= true;
	public static final boolean DEVKIT			= false;
	public static final boolean EMPTY_START		= false;
	public static final boolean TEST_FPS30		= false;
	public static final Color bossPointsColor = new Color(0.7f,0,0.7f,1);
	public static final float PARALLAX_FACTOR	= 2.0f;
	public static final float musicLowFactor	= 4f;
	public static final int POWERUP_CHANCE = 4;//percent
	public static final int TOTAL_EXPLOSION_EFFECTS = 16;
	public static final int MAX_ENEMIES = 20;
	public static final int BONUS_POINTS = 300;
	public static int SPAWN_RANGE = 0;
	
	static enum GAMESTATE { TITLESCREEN, INGAME, LEADERBOARD }
	
	private GAMESTATE gameState = GAMESTATE.TITLESCREEN;
	
	public static boolean PAUSE	= false;

	//INPUT
	private Vector2 curLeftFinger, curRightFinger, leftFingerSource, rightFingerSource;
	private int leftFingerIndex = -1;
	private int rightFingerIndex = -1;
	private boolean gamepadControl = false;
	private float[][] padStickValue;
    
	private OrthographicCamera camera;
	private OrthographicCamera hud_camera;
	private SpriteBatch batch;
	private BitmapFont font, monoFont, medFont, bigFont;
	private StringBuilder strb = new StringBuilder();
	 
	private boolean fps30;
    private float exh 				= 0.0f;
    private float hp				= 1.0f;
    private float musicVolume		= 0.26f;
    private float soundVolume		= 0.7f;
    private float currentShotAnglePlayer = 0;
    private long supergunTimer		= 0;
    private int stickBigSize, stickSmallSize;
    private int points				= 0;
    private int	lifes				= 0;
    private int	killCount			= 0; //uninterrupted ones for multiplyer
    private float flashAlpha		= 0;
    private float scaleAmount 		= 1f;
    private long respawnTime		= 0;
    
    private DisplayMode[] desktopDisplayModes = null;
    private Projectile.TYPE currentShotType = Projectile.TYPE.THICK_DOT;
    private Color tempShotColor = new Color(1f,1f,1f,1f);
    private Color bgSpaceMapColor = new Color(1,1,1,1);
    private Color cloudMapColor = new Color(1,1,1,1);
    private Entity player;
    private Entity player2; //networking play, unused currently
    private Boss boss;
    private PowerUp powerup;
    private TextureAtlas atlas;
    private HashMap<String, Sound> sounds;
    private HashMap<Projectile.TYPE, TextureRegion> shotMap;
    private TextureRegion imgBoss;
    private Music music;
    private SpaceMap spaceMap;
    private Array<Projectile> projectiles = new Array<Projectile>();
    private Array<Entity> enemies = new Array<Entity>();
    private Vector3 calcV3 = new Vector3(0,0,1);
    private MyInputProcessor myInputProcessor;
    private Array<ParticleEffect> partEffs = new Array<ParticleEffect>();
    private Array<PowerUpSprite> powerupSprites = new Array<PowerUpSprite>();
    private Array<TextEffect> texts = new Array<TextEffect>();
    private ParticleEffect[] peExplosions;
    private ParticleEffect[] bossHitExplosions;
    private TextEffect bossPointsEffect;
    private int pexplIndex;
    private int bosspexplIndex;
    private ParticleEffect playerPropuEffect;
    private FrameSkipper skip;
    private Random random;
    
    //UI
    private Skin titleSkin;
    private BitmapFont bigUiFont;
    
	private MyStage uiStage;
	private Window quitWindow;
	
	private Table startMenuTable;
	
	boolean haveToSubmitHighscore = false;
	boolean markScore = false;
	
	
	Controller gamePad = null;
	
	//random color party
	float bgColorChangeSpeedFactor = 30f;
	float[][] spaceColor = new float[2][3]; //2 colors, 3 parameters
	boolean[][] spaceColorChangeDirection = new boolean[2][3]; //2 colors, 3 parameters; false is + and true -
	
    public NebulaGame(float fps)
    {
    	super();
    	fps30 = (fps > 51) ? false : true;
    	if (TEST_FPS30) fps30 = true;
    }
 
	@Override
	public void create()
	{
		System.out.println("Inital Size: "+Gdx.graphics.getWidth()+" x "+Gdx.graphics.getHeight());
		System.out.println("CREATE");
		System.out.println("Black Nebula\n");
		random = new Random();
		bossPointsColor.mul(GAMMA_FACTOR);
		
		if (!isMobilePlatform())
		{
			for (int i = 0; i<Controllers.getControllers().size;i++)
			{
				Controller controller = Controllers.getControllers().get(i);
				
				if (controller.getName().toLowerCase().contains("xbox") && controller.getName().contains("360"))
				{
					gamepadControl = true;
					// first index is STICK. left = 0; right = 1;
					// second index is axis. x = 0; y = 1;
					padStickValue = new float[2][2];
					gamePad = controller;
					
//					controller.addListener(new NebulaGamePadHandler());
					gamePad.addListener(new NebulaGamePadHandler());
					
					System.out.println("Gamepad name: "+controller.getName());
				}
			}
		}
		
		//TODO
		//Test if this screws up controller input, as per known bug
		if (!DEVKIT && !isMobilePlatform())
		{
//			desktopDisplayModes = Util.getDisplayModes();
//			
//			DisplayMode prevMode = Gdx.graphics.getDesktopDisplayMode();
//			
//			int goWidth = (int)(prevMode.width*0.7f);
//			
//			float factor = (goWidth/PREFFERED_SCREEN_WIDTH);
//			int goHeight = (int)(PREFFERED_SCREEN_HEIGHT*factor);
			
//			int goWidth =  (int)(prevMode.width*0.9f);
//			int goHeight = (int)(prevMode.height*0.8f);
//			
//			Gdx.graphics.setDisplayMode(goWidth, goHeight, false);
		}
		
		Gdx.input.setCatchBackKey(true);
		
		SPAWN_RANGE = Gdx.graphics.getWidth();
		
		batch 	 = new SpriteBatch();
		font 	 = new BitmapFont(Gdx.files.internal("font/sansation.fnt"), true);
		bigFont  = new BitmapFont(Gdx.files.internal("font/big.fnt"), true);
		medFont  = new BitmapFont(Gdx.files.internal("font/medium_style.fnt"), true);
		monoFont = new BitmapFont(Gdx.files.internal("font/simple/monop.fnt"), true);
		
		bigFont.	getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		font.		getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		medFont.	getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		monoFont.	getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		bigUiFont = new BitmapFont(Gdx.files.internal("font/big.fnt"), false);
		
		
		initScaleAmount(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		
//		Gdx.graphics.setVSync(false);
		if (is30FPSMode()) skip = new FrameSkipper(30);
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(true);
		
		hud_camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		hud_camera.setToOrtho(true);
		
		// if scale amount  is big, zoom shall go down
		camera.zoom = 1f+(2f-(scaleAmount));
		System.out.println("Camera zoom: "+camera.zoom);
		
		myInputProcessor = new MyInputProcessor();
		
        
        atlas = new TextureAtlas(Gdx.files.internal("content/atlas/big.atlas"), true);
        atlas.findRegion("boss_icon_arrow").getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        
        IkaBar.init(atlas);
        
        Primitives.init(atlas);

        shotMap = new HashMap<Projectile.TYPE, TextureRegion>();
        
        shotMap.put(Projectile.TYPE.THICK_DOT, 	atlas.findRegion("thickdot-shot"));
        shotMap.put(Projectile.TYPE.HOMING, 	atlas.findRegion("double-shot"));
        shotMap.put(Projectile.TYPE.THIN, 		atlas.findRegion("thin-shot"));
        shotMap.put(Projectile.TYPE.ANGULAR, 	atlas.findRegion("angular-shot"));
        shotMap.put(Projectile.TYPE.DEFAULT, 	atlas.findRegion("default-shot"));
        shotMap.put(Projectile.TYPE.SCYTHE, 	atlas.findRegion("scythe-shot"));
        shotMap.put(Projectile.TYPE.BALL, 		atlas.findRegion("ball-shot"));

    	imgBoss = atlas.findRegion("boss");
        
    	sounds = new HashMap<String, Sound>();
    			
    	if (audio)
		{
    		sounds.put("life", 			Gdx.audio.newSound(Gdx.files.internal("content/audio/sound/life.ogg")));
    		sounds.put("bomb", 			Gdx.audio.newSound(Gdx.files.internal("content/audio/sound/bomb.ogg")));
    		sounds.put("enemyShot", 	Gdx.audio.newSound(Gdx.files.internal("content/audio/sound/enemyShot.ogg")));
    		sounds.put("bossShot", 		Gdx.audio.newSound(Gdx.files.internal("content/audio/sound/bossShot.ogg")));
    		sounds.put("explosion_big", Gdx.audio.newSound(Gdx.files.internal("content/audio/sound/explosion_big.ogg")));
    		sounds.put("hitEnemy", 		Gdx.audio.newSound(Gdx.files.internal("content/audio/sound/hitEnemy.ogg")));
    		sounds.put("hitPlayer", 	Gdx.audio.newSound(Gdx.files.internal("content/audio/sound/hitPlayer.ogg")));
    		sounds.put("playerShot", 	Gdx.audio.newSound(Gdx.files.internal("content/audio/sound/playerShot.ogg")));
    		sounds.put("points", 		Gdx.audio.newSound(Gdx.files.internal("content/audio/sound/points.ogg")));
    		sounds.put("shield", 		Gdx.audio.newSound(Gdx.files.internal("content/audio/sound/shield.ogg")));
    		sounds.put("supergun", 		Gdx.audio.newSound(Gdx.files.internal("content/audio/sound/supergun.ogg")));
    		sounds.put("wuush", 		Gdx.audio.newSound(Gdx.files.internal("content/audio/sound/wuush.ogg")));
		}
    	
    	player = new Entity(0, 0, atlas.findRegion("player"), Entity.TYPE.PROTA);
    	player.speedFactor = 10.0f;
    	player.setScale(0.75f);
    	
    	playerPropuEffect = new ParticleEffect();
    	playerPropuEffect.load(Gdx.files.internal("content/particles/propu.p"), Gdx.files.internal("content/particles"));
    	playerPropuEffect.start();
    	playerPropuEffect.setPosition(player.centerX, player.centerY);
		partEffs.add(playerPropuEffect);
		
		
		peExplosions = new ParticleEffect[TOTAL_EXPLOSION_EFFECTS];
		pexplIndex = 0;
		for (int i = 0;i<TOTAL_EXPLOSION_EFFECTS;i++)
		{
			peExplosions[i] = new ParticleEffect();
			peExplosions[i].load(Gdx.files.internal("content/particles/expl_mini.p"), Gdx.files.internal("content/particles"));
		}
		
		bossHitExplosions = new ParticleEffect[TOTAL_EXPLOSION_EFFECTS];
		bosspexplIndex = 0;
		for (int i = 0;i<TOTAL_EXPLOSION_EFFECTS;i++)
		{
			bossHitExplosions[i] = new ParticleEffect();
			bossHitExplosions[i].load(Gdx.files.internal("content/particles/boss_hit.p"), Gdx.files.internal("content/particles"));
		}
    	
		stickBigSize 	= (int)(Gdx.graphics.getHeight()*0.35f);
		stickSmallSize 	= (int)((Gdx.graphics.getHeight()*0.35f)/2);
		float stickScreenPadding = Gdx.graphics.getHeight()*0.05f;
		leftFingerSource  = new Vector2(0+stickScreenPadding+(stickBigSize/2), Gdx.graphics.getHeight()-(stickBigSize/2)-stickScreenPadding);
		rightFingerSource = new Vector2(Gdx.graphics.getWidth()-(stickBigSize/2)-stickScreenPadding, Gdx.graphics.getHeight()-(stickBigSize/2)-stickScreenPadding);
		curLeftFinger 	= leftFingerSource.cpy();
		curRightFinger 	= rightFingerSource.cpy();
    	
    	boss = new Boss(1200, 1200, imgBoss, Entity.TYPE.BOSS);
    	
		
		
//		smallFont  = new BitmapFont(Gdx.files.internal("font/small.fnt"), true);
		
		Leaderboard.pollScores();

		if (audio)
		{
	    	music = Gdx.audio.newMusic(Gdx.files.internal("content/audio/music/song.ogg"));
	    	music.setLooping(true);
	    	music.setVolume(musicVolume/musicLowFactor);
	    	music.play();
		}
		
		spaceMap = new SpaceMap(atlas.findRegion("clouds1"), atlas.findRegion("spacebg"));
		
		
		
		//UI
		// Holo Dark Theme, created by Carsten Engelke
		uiStage = new MyStage();
		titleSkin = new Skin(Gdx.files.internal("content/ui/ui.json"));
		
		
		startMenuTable = new Table(titleSkin);
		startMenuTable.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		

		
		
		float bwidth = bigUiFont.getBounds("LEADERBOARD").width*1.5f;
		startMenuTable.defaults().width(bwidth);
		
		TextButtonStyle tbs = titleSkin.get(TextButtonStyle.class);
		tbs.font = bigUiFont;
		tbs.fontColor = Color.WHITE;
//		tbs.up = null;
		
		final TextButton butStart = new TextButton("START", titleSkin);
		startMenuTable.row().padTop(bigUiFont.getLineHeight()*1.6f);
		startMenuTable.add(butStart);
		
		final TextButton butLeader = new TextButton("LEADERBOARD", titleSkin);
		startMenuTable.row();
		startMenuTable.add(butLeader);
		
		butStart.addListener(new ClickListener()
		{
			public void clicked(InputEvent event, float x, float y) 
			{
				butStart.setChecked(false);
				startNewGame();
			}
		});
		
		butLeader.addListener(new ClickListener(){public void clicked(InputEvent event, float x, float y){
				butLeader.setChecked(false);
				gotoLeaderboard();    }});
		
		startMenuTable.setVisible(true);
		
		
		uiStage.addActor(startMenuTable);
		
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		titleSkin.add("pixel", new Texture(pixmap));
		
		
		float qww = bigUiFont.getBounds("Yes").width*2.2f;
		
		quitWindow = new Window("Do you really want to quit ?", titleSkin);
		
		final TextButton butQuitYes = new TextButton("Yes", titleSkin);
		final TextButton butQuitNo = new TextButton("No", titleSkin);
		
		butQuitNo.addListener(new ClickListener()
		{
			public void clicked(InputEvent event, float x, float y) 
			{
				quitWindow.setVisible(false);
			}
		});
		
		butQuitYes.addListener(new ClickListener()
		{
			public void clicked(InputEvent event, float x, float y) 
			{
				Gdx.app.exit();
			}
		});
		
		
		quitWindow.setModal(true);
		quitWindow.setMovable(false);


		quitWindow.setSize(Gdx.graphics.getWidth()/2f, butQuitNo.getHeight()*2.4f);
		
		float qwx = (Gdx.graphics.getWidth() -quitWindow.getWidth())/2f;
		float qwy = (Gdx.graphics.getHeight() -quitWindow.getHeight())/2f;
		quitWindow.setPosition(qwx, qwy);
		
		float padding = quitWindow.getHeight()/10f;
		
		butQuitNo.setWidth(butQuitNo.getWidth()*2f);
		butQuitYes.setWidth(butQuitNo.getWidth());
		//coordinates inside are relative to parent, not screen
		butQuitYes.setPosition(padding, butQuitYes.getHeight()/2f);
		butQuitNo.setPosition(quitWindow.getWidth()-butQuitNo.getWidth()-padding , butQuitNo.getHeight()/2f);
		quitWindow.addActor(butQuitNo);
		quitWindow.addActor(butQuitYes);
		
		quitWindow.setVisible(false);

		uiStage.addActor(quitWindow);
		
		
		Leaderboard.skin = new Skin(Gdx.files.internal("content/ui/ui.json"));
		

		Leaderboard.textField = new TextField("", Leaderboard.skin);
		Leaderboard.textField.setPosition(Gdx.graphics.getWidth()*0.15f, Math.abs((monoFont.getLineHeight()*3.1f)-Gdx.graphics.getHeight()));
		Leaderboard.textField.setWidth(300);
		Leaderboard.textField.setMessageText("Type in your Name");
		Leaderboard.textField.setBlinkTime(0.55f); 
		
		Leaderboard.submitButton = new TextButton("SUBMIT", Leaderboard.skin);
		Leaderboard.submitButton.setWidth(120);
		Leaderboard.submitButton.setPosition(Leaderboard.textField.getX()+Leaderboard.textField.getWidth()+10,Leaderboard.textField.getY());
		Leaderboard.submitButton.setHeight(Leaderboard.textField.getHeight());
		
		Leaderboard.backButton = new TextButton("BACK", Leaderboard.skin);
		Leaderboard.backButton.setWidth(Gdx.graphics.getWidth()/3f);
		Leaderboard.backButton.setPosition((Gdx.graphics.getWidth()/2f)-(Leaderboard.backButton.getWidth()/2f), Leaderboard.backButton.getHeight()*0.1f);
		Leaderboard.backButton.setHeight(Leaderboard.textField.getHeight());
		
		Leaderboard.backButton.addListener(new ClickListener()
		{
			public void clicked(InputEvent event, float x, float y) 
			{
				Leaderboard.backButton.setChecked(false);
				
				//get android back does this too
				//state = title screen
				//TODO
				goBack();
			}
		});
		
		Leaderboard.submitButton.addListener(new ClickListener()
		{
			public void clicked(InputEvent event, float x, float y) 
			{
				Leaderboard.submitButton.setChecked(false);
				
				String name = Leaderboard.textField.getText();
				
				//potential sql injection
				
				Leaderboard.sendScoreToServer(name, points);
				Leaderboard.pollScores();
				
				haveToSubmitHighscore = false;
				markScore = true;
				
				Leaderboard.textField.setVisible(false);
				Leaderboard.submitButton.setVisible(false);
			}
		});
		
		Leaderboard.textField.setVisible(false);
		Leaderboard.submitButton.setVisible(false);
		Leaderboard.backButton.setVisible(false);
		
		uiStage.addActor(Leaderboard.textField);
		uiStage.addActor(Leaderboard.submitButton);
		uiStage.addActor(Leaderboard.backButton);
		
		
		Gdx.input.setInputProcessor(uiStage);
		
	}
	
	private void initScaleAmount(int w, int h)
	{
		float timesWidth  = (float)w/(float)PREFFERED_SCREEN_WIDTH;
		float timesHeight = (float)h/(float)PREFFERED_SCREEN_HEIGHT;
		scaleAmount = timesWidth;
		if (timesHeight > timesWidth) scaleAmount = timesHeight;
		
		medFont.	setScale(scaleAmount);
		font.		setScale(scaleAmount);
		bigFont.	setScale(scaleAmount);
		monoFont.	setScale(scaleAmount*0.8f);
		bigUiFont.setScale(scaleAmount/1.5f);
		
		System.out.println("ScaleAmount: "+scaleAmount);
	}
	
	private void goBack()
	{
		if (gameState == GAMESTATE.LEADERBOARD)
		{
			gameState = GAMESTATE.TITLESCREEN;
			startMenuTable.setVisible(true);
			points = 0;
			Leaderboard.textField.setText("");
			
			Leaderboard.textField.setVisible(false);
			Leaderboard.submitButton.setVisible(false);
			Leaderboard.backButton.setVisible(false);
			
			Gdx.input.setInputProcessor(uiStage);
		}
		else if (gameState == GAMESTATE.TITLESCREEN)
		{
			quitWindow.setVisible(!quitWindow.isVisible());
		}
		else if (gameState == GAMESTATE.INGAME)
		{
			gameState = GAMESTATE.TITLESCREEN;
			Gdx.input.setInputProcessor(uiStage);
			startMenuTable.setVisible(true);
			if (audio)
			{
				music.setVolume(musicVolume/musicLowFactor);
			}
		}
		
		System.out.println("Back");
	}
	
	private void addPowerUpSprite(float x, float y, PowerUp.TYPE type)
	{
		TextureRegion treg = atlas.findRegion("powerup_points");
		
		if (type == TYPE.LIFE)
		{
			treg = atlas.findRegion("powerup_life");
		}
		else if (type == TYPE.BALL)
		{
			treg = atlas.findRegion("powerup_ball");
		}
		else if (type == TYPE.ANGULAR)
		{
			treg = atlas.findRegion("powerup_angular");
		}
		else if (type == TYPE.HOMING)
		{
			treg = atlas.findRegion("powerup_homing");
		}
		else if (type == TYPE.SCYTHE)
		{
			treg = atlas.findRegion("powerup_scythe");
		}
		else if (type == TYPE.SHIELD)
		{
			treg = atlas.findRegion("powerup_shield");
		}
		else if (type == TYPE.SUPER_GUN)
		{
			treg = atlas.findRegion("powerup_gun");
		}
		else if (type == TYPE.TURBO)
		{
			treg = atlas.findRegion("powerup_turbo");
		}
		else if (type == TYPE.POINTS)
		{
			treg = atlas.findRegion("powerup_points");
		}
		else
		{
			treg = atlas.findRegion("powerup_points");
		}
		
		PowerUpSprite powSpr = new PowerUpSprite(x,y, treg, type);
		powSpr.scale(0.3f);
		powerupSprites.add(powSpr);
	}
	
	private void addPowerUp(TYPE type)
	{
		powerup = new PowerUp(type);
		
		if (type == TYPE.LIFE)
		{
			lifes++;
			if (audio) sounds.get("life").play(soundVolume);
		}
		else if (type == TYPE.BALL)
		{
			currentShotType = Projectile.TYPE.BALL;
			if (audio) sounds.get("bomb").play(soundVolume);
		}
		else if (type == TYPE.RING)
		{
			float amount = 10;
			
			for (int i = 0;i<amount;i++)
			{
				Vector2 vp = Trigonometrics.getOrbitLocationDeg(player.centerX, player.centerY, ((360/amount)*i), 200);
				Projectile p = new Projectile(vp.x, vp.y, shotMap.get(Projectile.TYPE.BALL), player, Projectile.TYPE.BALL);
				p.setColor(0.1f, 0.4f, 0.9f, 0.9f);
				p.orbitDeg = ((360/amount)*i);
				p.orbiter = true;
				projectiles.add(p);
			}
			if (audio) sounds.get("bomb").play(soundVolume);
		}
		else if (type == TYPE.ANGULAR)
		{
			currentShotType = Projectile.TYPE.ANGULAR;
			if (audio) sounds.get("bomb").play(soundVolume);
		}
		else if (type == TYPE.HOMING)
		{
			currentShotType = Projectile.TYPE.HOMING;
			if (audio) sounds.get("bomb").play(soundVolume);
		}
		else if (type == TYPE.SCYTHE)
		{
			currentShotType = Projectile.TYPE.SCYTHE;
			if (audio) sounds.get("bomb").play(soundVolume);
		}
		else if (type == TYPE.SHIELD)
		{
			player.shieldHealth = 1.0f;
			if (audio) sounds.get("shield").play(soundVolume);
		}
		else if (type == TYPE.SUPER_GUN)
		{
			supergunTimer = System.currentTimeMillis()+14000;
			player.supergun = true;
			if (audio) sounds.get("supergun").play(soundVolume);
		}
		else if (type == TYPE.TURBO)
		{
			player.turbo = true;
			exh = 0;
			if (audio) sounds.get("wuush").play(soundVolume);
		}
		
	}
	
	private void addParticleEffect(String in, float x, float y)
	{
		ParticleEffect pe = new ParticleEffect();
		pe.load(Gdx.files.internal("content/particles/"+in+".p"), Gdx.files.internal("content/particles"));
		pe.start();
		pe.setPosition(x, y);
		partEffs.add(pe);
	}
	
	private void addBossExplosionEffect(float x, float y)
	{
		bossHitExplosions[bosspexplIndex].setPosition(x, y);
		bossHitExplosions[bosspexplIndex].start();
		partEffs.add(bossHitExplosions[bosspexplIndex]);
		
		bosspexplIndex++;
		if (bosspexplIndex >= TOTAL_EXPLOSION_EFFECTS) bosspexplIndex = 0;
	}
	
	private void addExplosionEffect(float x, float y)
	{
		peExplosions[pexplIndex].setPosition(x, y);
		peExplosions[pexplIndex].start();
		partEffs.add(peExplosions[pexplIndex]);
		
		pexplIndex++;
		if (pexplIndex >= TOTAL_EXPLOSION_EFFECTS) pexplIndex = 0;
	}
	
	
	private TextEffect addTextEffect(String in, float x, float y, Color col)
	{
		TextEffect te = new TextEffect(in, x, y, col);
		texts.add(te);
		return te;
	}
	
	private void startNewGame()
	{
		if (Gdx.input.getInputProcessor() != myInputProcessor)  Gdx.input.setInputProcessor(myInputProcessor);
		gameState = GAMESTATE.INGAME;
		startMenuTable.setVisible(false);

		Leaderboard.textField.setVisible(false);
		Leaderboard.submitButton.setVisible(false);
		Leaderboard.backButton.setVisible(false);

		currentShotType = Projectile.TYPE.DEFAULT;
	    exh 				= 0.0f;
	    hp					= 1f;
	    points				= 0;
	    lifes				= 3;
	    killCount			= 0;
	    player.shieldHealth = 0f;
	    player.shootingCoolCount= 0;
//	    player.shootingCooldown = 6;
	    player.shootingCooldown = 96; // 6 * 0.016 ms (16us)
	    player.speedX = 0.0f;
	    player.speedY = 0.0f;
	    player.setColor(1, 1, 1, 1);
	    
		
		projectiles.clear();
		enemies.clear();
		partEffs.clear();
		
		player.shieldHealth = 1f;
		
		if (audio)
		{
			music.setVolume(musicVolume);
		}
		
		spaceMap = new SpaceMap(atlas.findRegion("clouds1"), atlas.findRegion("spacebg"));
		
//		for (int i=0;i<16;i++)
//		{
//			spaceMap.tryToAddBgImg(atlas.findRegion("planet1"), getRandom(0, spaceMap.getWidth()), getRandom(0, spaceMap.getHeight()));
//		}
		
		boss.resetParameters();

    	player.setPosition(4000, 4000);
    	adjustCamera();
//    	player.setX(2000);
//    	player.setY(2000);
    	
    	if (!EMPTY_START)
    	{
	    	for (int i=0;i<40;i++)
			{
				if (enemies.size >= MAX_ENEMIES) return;
				addEnemyInRange(Entity.TYPE.ASTEROID, SPAWN_RANGE*4);
			}
			
			for (int i=0;i<40;i++)
			{
				if (enemies.size >= MAX_ENEMIES) return;
				addEnemyInRange(getRandomEntityType(), SPAWN_RANGE*4);
			}
    	}
	}
	
	private void addShot(float angle, Entity ship, Projectile.TYPE type)
    {
    	if (ship.shootingCoolCount > 0 || PAUSE) return;
    	angle-=180;
    	
    	float angleDifference = 12;
    	TextureRegion is = shotMap.get(type);
    	if (is == null)
    	{
    		System.err.println("Cannot find shot type: "+type);
    	}
    	int totalBullets = 1;
    	
    	//How many bullets
    	if (ship == boss) 							totalBullets = 4;
    	else if (ship.type == Entity.TYPE.CRAWLER) 	totalBullets = 30;
    	else if (type == Projectile.TYPE.SCYTHE) 	totalBullets = 3;
    	else if (type == Projectile.TYPE.HOMING && ship == player) totalBullets = 5;
    	
    	ship.shootingCooldown = 8;
    	//Lets control cooldowna
		
    	if (ship == player)
    	{
			if (type == Projectile.TYPE.BALL)
			{
				ship.shootingCooldown = 10;
			}
			else if (type == Projectile.TYPE.DEFAULT || type == Projectile.TYPE.THICK_DOT) 
			{
				ship.shootingCooldown = 10;
			}
			else if (type == Projectile.TYPE.HOMING)
			{
				ship.shootingCooldown = 40;
			}
			else if (type == Projectile.TYPE.SCYTHE)
			{
				ship.shootingCooldown = 10;
			}
    	}
    	else if (ship == boss)
    	{
    		ship.shootingCooldown = 15;
    	}
    	else 
    	{
			if (ship.type == Entity.TYPE.DRONE)
			{
				ship.shootingCooldown = 60;
			}
			else if (ship.type == Entity.TYPE.GLIDER) 
			{
				ship.shootingCooldown = 40;
			}
			else if (ship.type == Entity.TYPE.GUN)
			{
				ship.shootingCooldown = 30;
			}
			else if (ship.type == Entity.TYPE.CRAWLER)
			{
				ship.shootingCooldown = 200;
			}
    	}
		
		
		
		//angle imperfection, random aiming
		if (type == Projectile.TYPE.DEFAULT || type == Projectile.TYPE.THICK_DOT) 
		{
			if (ship == player)
			{
				angle+=getRandom(-10,10);
			}
			else
			{
				angle+=getRandom(-4,4);
			}
		}
    	
		if (ship.supergun) totalBullets*=2;
		if (ship == player)// || ship == boss)
		{
			if (type == Projectile.TYPE.HOMING || type == Projectile.TYPE.ANGULAR)
				angleDifference = 360/totalBullets;
		}
    	
    	for (int i=(int)-totalBullets/2;i<((int)totalBullets/2)+1;i++)
    	{
    		float hereAngle = angle+(i*angleDifference);
    		
			Projectile p = new Projectile(ship.centerX,ship.centerY, is, ship, type);
			
			
			//Lets handle the color
			tempShotColor.set(1, 0.6f, 0f, 1);
			p.setColor(tempShotColor);
			
			if (ship.type == Entity.TYPE.BOSS )
			{
				tempShotColor.set(1f, 0.1f, 0.1f, 1f);
				p.setColor(tempShotColor);
			}
			else if (ship.type == Entity.TYPE.GLIDER )
			{
				tempShotColor.set(0.6f, 1.0f, 0.2f, 1);
				p.setColor(tempShotColor);
			}
			else if (ship.type == Entity.TYPE.CRAWLER )
			{
				tempShotColor.set(0.6f, 1.0f, 0.2f, 1);
				p.setColor(tempShotColor);
			}
			else if (ship.type == Entity.TYPE.DRONE )
			{
				tempShotColor.set(0.24f, 0.7f, 1f, 1);
				p.setColor(tempShotColor);
			}
			else if (ship.type == Entity.TYPE.GUN )
			{
				tempShotColor.set(0.7f, 0.14f, 0.8f, 1);
				p.setColor(tempShotColor);
			}
			else if (type == Projectile.TYPE.SCYTHE)
			{
				p.setColor(PowerUp.scytheColor);
				p.rotating = true;
			}
			else if (type == Projectile.TYPE.HOMING && ship != boss)	p.setColor(PowerUp.homingColor);
			else if (type == Projectile.TYPE.BALL)						p.setColor(PowerUp.ringColor);
			else if (type == Projectile.TYPE.THICK_DOT)					p.setColor(Color.GREEN);
			else if (type == Projectile.TYPE.ANGULAR)					p.setColor(PowerUp.angularColor);

			p.setX(p.getX()-(p.getWidth()/2));
			p.setY(p.getY()-(p.getHeight()/2));
			p.setDirectionPoint(p.centerX+((float)MathUtils.cosDeg(hereAngle)*300), 
								p.centerY+((float)MathUtils.sinDeg(hereAngle)*300), enemies, player, boss);
			
			p.setColor(p.getColor().mul(GAMMA_FACTOR/2));
			
			projectiles.add(p);
    	}
    	
		ship.shootingCoolCount 	= 1;
		
		if (audio) 
		{
			if (ship == player)
			{
				if (ship.supergun)
					sounds.get("bossShot").play(soundVolume);
				else
					sounds.get("playerShot").play(soundVolume);
			}
			else if (ship == boss)
			{
				sounds.get("bossShot").play(soundVolume);
			}
			else
			{
				sounds.get("enemyShot").play(soundVolume);
			}
		}
    }
	
	private void addEnemyManually(float _x, float _y, Entity.TYPE type)
    {
		if (BOSS_ONLY_MODE) return;
		TextureRegion treg = atlas.findRegion("glider");
		
		if (type == Entity.TYPE.DRONE)		treg = atlas.findRegion("drone");
		else if (type == Entity.TYPE.CRAWLER)	treg = atlas.findRegion("crawler");
		else if (type == Entity.TYPE.GUN)	treg = atlas.findRegion("gun");
		else if (type == Entity.TYPE.ASTEROID)	treg = atlas.findRegion("asteroid");
		else if (type == Entity.TYPE.GLIDER)	treg = atlas.findRegion("glider");
		
    	Entity spawnEnemy = new Entity(_x, _y, treg, type);
    	enemies.add(spawnEnemy);
    }
	
	private void addEnemyInRange(Entity.TYPE type)
	{
		addEnemyInRange(type, SPAWN_RANGE);
	}
	
	private void addEnemyInRange(Entity.TYPE type, int range)
	{
		addEnemyManually(getRandom(player.centerX-range, player.centerX+range), getRandom(player.centerY-range, player.centerY+range), type);
	}
	
	private boolean isSpriteWithinFrustum(Sprite spr)
	{
		calcV3.x = spr.getX();
		calcV3.y = spr.getY();
		if (camera.frustum.pointInFrustum(calcV3))
		{
			return true;
		}
		
		calcV3.x = spr.getX()+spr.getWidth();
		calcV3.y = spr.getY();
		if (camera.frustum.pointInFrustum(calcV3))
		{
			return true;
		}
		
		calcV3.x = spr.getX();
		calcV3.y = spr.getY()+spr.getHeight();
		if (camera.frustum.pointInFrustum(calcV3))
		{
			return true;
		}
		
		calcV3.x = spr.getX()+spr.getWidth();
		calcV3.y = spr.getY()+spr.getHeight();
		if (camera.frustum.pointInFrustum(calcV3))
		{
			return true;
		}
		
		return false;
	}
	
	private void turnEnemiesToPlayer(Entity e)
    {
    	float dx = player.centerX;
    	float dy = player.centerY;
    	dx = dx - e.centerX;
		dy = dy - e.centerY;
		float targetAngle = 57.2957795f*MathUtils.atan2(dy, dx);
		targetAngle = targetAngle-180;
		
		e.setRotation(targetAngle);
    }
	
	private boolean isWASDPressed() 
	{
		return (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.D));
	}
	
	private boolean isArrowsPressed() 
	{
		return (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.DOWN));
	}
	
	private boolean is30FPSMode()
	{
//		return true;
		return fps30;
	}
	
	private boolean isMobilePlatform()
	{
//		return true;
		return (Gdx.app.getType() == ApplicationType.iOS || Gdx.app.getType() == ApplicationType.Android);
	}
	
	private void hurtPlayer(float amount)
	{
		if (hp <= 0.0f) return;
		
		if (hp > 0.0f) hp-=amount;
		if (hp < 0.0f) hp = 0.0f;
		
		if (player.shieldHealth > 0.0f) player.shieldHealth-=amount*10;
		if (player.shieldHealth < 0.0f) player.shieldHealth = 0.0f;
		
		//performance man
		//also you cant see shit
//		addExplosion(p.centerX+((exploAni.getWidth()/2)*SMALL_ANIMATION_SCALE), p.centerY+((exploAni.getHeight()/2)*SMALL_ANIMATION_SCALE), true);
		killCount = 0;
		flashAlpha = 1.0f;
		
		if (hp > 0.0f)
		{
			if (audio) sounds.get("hitPlayer").play(soundVolume);
		}
		else
		{
			destroyAllEnemiesOnScreen();
			lifes--;
			
			for (int i = projectiles.size - 1; i >= 0; i--)
			{
				Projectile p = projectiles.get(i);
				
				if (p.orbiter)
				{
					p.removeMe = true;
				}
			}
			
			currentShotType = Projectile.TYPE.DEFAULT;
			player.supergun = false;
			player.turbo = false;
			player.shieldHealth = 1.0f;
			respawnTime = System.currentTimeMillis()+2000;
			if (audio) sounds.get("explosion_big").play(soundVolume);
			addParticleEffect("expl01", player.centerX, player.centerY);
		}
		
		if (true);
		{
			System.out.println();
		}
		
		
		{
			
		}
	}
	
	private void gotoLeaderboard()
	{
		gameState = GAMESTATE.LEADERBOARD;
		markScore = false;
		startMenuTable.setVisible(false);
		
		System.out.println("goto leaderboard");

		Leaderboard.backButton.setVisible(true);
		
		if (audio)
		{
			music.setVolume(musicVolume/musicLowFactor);
		}
		
		Leaderboard.pollScores();
		
		haveToSubmitHighscore = false;
		
		if (Leaderboard.scores != null)
		{
			if (Leaderboard.scores[Leaderboard.scores.length-1].getPoints() <= points)
			{
				haveToSubmitHighscore = true;
			}
		}
		
		if (haveToSubmitHighscore)
		{
			Leaderboard.textField.setVisible(true);
			Leaderboard.submitButton.setVisible(true);
		}
		else
		{
			Leaderboard.textField.setVisible(false);
			Leaderboard.submitButton.setVisible(false);
		}
		
		
    	if (Gdx.input.getInputProcessor() != uiStage)
			Gdx.input.setInputProcessor(uiStage);
	}
	
	private void destroyAllEnemiesOnScreen() // used when player dies
	{
		for (int i = enemies.size - 1; i >= 0; i--)
		{
			Entity e = enemies.get(i);
			if (isSpriteWithinFrustum(e))
			{
				destroyEnemy(e);
			}
		}
		
		if (isSpriteWithinFrustum(boss))
		{
			destroyBoss();
		}
	}
	
	public void checkInputs()
	{

		//pad.getButton(i)
		
		
		
		
		//gradual turning
//		player.rotateToTargetRotation();
//		player.updateChasingpoint(200);
		
		if (gameState == GAMESTATE.INGAME)
		{
			if (isMobilePlatform())
			{
				if (leftFingerSource != null && curLeftFinger != null)
				{
//					float angle = Trigonometrics.getAngleBetweenPointsDeg(curLeftFinger.x, curLeftFinger.y, leftFingerSource.x, leftFingerSource.y);
//					
//	    			player.targetRotation = angle;
//	    			player.rotateToTargetRotation();
//	    			player.updateChasingpoint(player.targetRotation, 200);

//	    			Vector2 v = Trigonometrics.getSpeedDeg(player.chasingPoint.x, player.chasingPoint.y, player.centerX, player.centerY, player.speedFactor);
//	    		    player.speedX = v.x;
//	    		    player.speedY = v.y;
				}
				
//				if (rightFingerSource != null && curRightFinger != null)
//				{
//					float angle = Trigonometrics.getAngleBetweenPointsDeg(curRightFinger.x, curRightFinger.y, rightFingerSource.x, rightFingerSource.y);
//					if ((int)curRightFinger.x == (int)rightFingerSource.x && (int)curRightFinger.y == (int)rightFingerSource.y && (int)angle == 0)
//					{
//						angle = player.getRotation();
//					}
//					addShot(angle, player);
//				}
			}
			else
			{
				//desktop controls
				handleDesktopInputs();
			}
		}
		
		if (isMobilePlatform())
		{
			if (curLeftFinger != null && ((int)curLeftFinger.x != (int)leftFingerSource.x) || ((int)curLeftFinger.y != (int)leftFingerSource.y))
			{
				pushPlayerForward();
			}
		}
		else
		{
			if (gamepadControl)
			{
				float stickMagLeft	= (float)Math.sqrt(padStickValue[0][0] * padStickValue[0][0] + padStickValue[0][1] * padStickValue[0][1]);
				float stickMagRight = (float)Math.sqrt(padStickValue[1][0] * padStickValue[1][0] + padStickValue[1][1] * padStickValue[1][1]);
				
				float deadZone = 0.35f;
				
				float angleLeft  = Trigonometrics.getAngleBetweenPointsDeg(padStickValue[0][0], padStickValue[0][1], 0, 0);
				float angleRight = Trigonometrics.getAngleBetweenPointsDeg(padStickValue[1][0], padStickValue[1][1], 0, 0);
				
				if (stickMagLeft > deadZone)
				{
					player.targetRotation = angleLeft;
					pushPlayerForward();
				}
				
				if (stickMagRight > deadZone)
				{
					addShot(angleRight, player, currentShotType);
//					System.out.println("angle: "+angleRight);
				}
			}

			
			if (isWASDPressed())
			{
				pushPlayerForward();
			}
		}
	}
	
	private void pushPlayerForward()
	{
		if (!isMobilePlatform())
		{
			player.rotateToTargetRotation();
		}
		
		player.updateChasingpoint(200);

		Vector2 v = Trigonometrics.getSpeedDeg(player.chasingPoint.x, player.chasingPoint.y, player.centerX, player.centerY, player.speedFactor);
	    player.speedX = v.x;
	    player.speedY = v.y;
	    
	    if (player.turbo)
	    {
	    	player.speedX*=1.8f;
	    	player.speedY*=1.8f;
	    }
	}
	
	private void destroyBoss()
	{
		boss.hp = 0;
//		if (audio) sounds.get("ihunger").play(soundVolume);
		if (audio) sounds.get("explosion_big").play(soundVolume);
		points+=boss.BASE_POINTS*boss.iteration*getMultipl();
		bossPointsEffect = addTextEffect(""+boss.BASE_POINTS+" x "+boss.iteration, boss.getX(), boss.getY()-50, bossPointsColor);
		boss.goToSleep();
		killCount+=100;
	}
	
	private void handleDesktopInputs()
	{
		if (hp > 0f)
		{
			if (isWASDPressed())
			{
				float dAngle = getAngleFromWASD();//getDeltaAngleFromWASD();
//				player.
//				player.setRotation(dAngle);
				player.targetRotation = dAngle;
				
				
			}
	
			if (Gdx.input.isTouched())
			{
				if (player.shootingCoolCount == 0)
				{
					addShot(Trigonometrics.getAngleBetweenPointsDeg(getRelativeMouseX(), getRelativeMouseY(), player.centerX, player.centerY), player, currentShotType);
				}
			}
			else if (isArrowsPressed())
			{
				if (player.shootingCoolCount == 0)
				{
					addShot(getAngleFromArrows(), player, currentShotType);
				}
			}
		}
	}
	
	private float getAngleFromWASD()
	{
		if (Gdx.input.isKeyPressed(Keys.W)) //UP  AZERTY
    	{
    		if (Gdx.input.isKeyPressed(Keys.A)) // LEFT
    		{
    			return 45;
    		}
    		else if (Gdx.input.isKeyPressed(Keys.D)) //RIGHT
    		{
    			return 135;
    		}
    		else
    		{
    			return 90;
    		}
    	}
    	else if (Gdx.input.isKeyPressed(Keys.S)) //DOWN
    	{
    		if (Gdx.input.isKeyPressed(Keys.A)) // LEFT
    		{
    			return 315;
    		}
    		else if (Gdx.input.isKeyPressed(Keys.D)) //RIGHT
    		{
    			return 225;
    		}
    		else
    		{
    			return 270;
    		}
    	}
    	else if (Gdx.input.isKeyPressed(Keys.A)) // LEFT
    	{
    		return 0;
    	}
    	else if (Gdx.input.isKeyPressed(Keys.D)) //RIGHT
    	{
    		return 180;
    	}
		
		return 0;
	}
	
	private float getAngleFromArrows()
	{
		if (Gdx.input.isKeyPressed(Keys.UP)) //UP  AZERTY
    	{
    		if (Gdx.input.isKeyPressed(Keys.LEFT) ) // LEFT
    		{
    			return 45+3;
    		}
    		else if (Gdx.input.isKeyPressed(Keys.RIGHT)) //RIGHT
    		{
    			return 135+6;
    		}
    		else
    		{
    			return 90+5;
    		}
    	}
    	else if (Gdx.input.isKeyPressed(Keys.DOWN)) //DOWN
    	{
    		if (Gdx.input.isKeyPressed(Keys.LEFT)) // LEFT
    		{
    			return 315-5;
    		}
    		else if (Gdx.input.isKeyPressed(Keys.RIGHT)) //RIGHT
    		{
    			return 225-2;
    		}
    		else
    		{
    			return 270-6;
    		}
    	}
    	else if (Gdx.input.isKeyPressed(Keys.LEFT)) // LEFT
    	{
    		return 0-1;
    	}
    	else if (Gdx.input.isKeyPressed(Keys.RIGHT)) //RIGHT
    	{
    		return 180+1;
    	}
		
		return 0;
	}

	
	public void adjustCamera()
	{
		camera.position.x = player.centerX;
		camera.position.y = player.centerY;
	}
	
	private void destroyEnemy(Entity e)
	{
		e.removeMe = true;
		addExplosionEffect(e.centerX, e.centerY);
		if (audio) sounds.get("hitEnemy").play(soundVolume);
		points+=e.getPoints()*getMultipl();
		addTextEffect(""+e.getPoints(), e.getX(), e.getY()-50, new Color(1,1,0,1));

		
		int rb = Util.getRandom(1, 4);
		if (rb == 4)
		{
			addPowerUpSprite(e.centerX+Util.getRandom(-40, 40), e.centerY+Util.getRandom(-40, 40), PowerUp.TYPE.POINTS);
		}
		
		
		int r = Util.getRandom(1, 100);
		if (r <= POWERUP_CHANCE)
		{
			PowerUp.TYPE[] allTypes = PowerUp.TYPE.values();
			
			int tr = Util.getRandom(0, allTypes.length-1);
			
			addPowerUpSprite(e.centerX+Util.getRandom(-40, 40), e.centerY+Util.getRandom(-40, 40), allTypes[tr]);
		}
	}
	
	private void runBossAI()
	{
		float dx = boss.centerX - player.centerX + boss.targetSaltX; 
		float dy = boss.centerY - player.centerY + boss.targetSaltY; 
        float distance = SquareRoot.fastSqrt((int)(dx*dx + dy*dy));
        
        //SHOOTING
        float shotRotation = boss.getRotation();
        
        shotRotation+=getRandom(-14.0f, 14.0f);
        
        if (distance < Gdx.graphics.getWidth()*1.4f)
        {
        	addShot(shotRotation, boss, Projectile.TYPE.ANGULAR);
        }
	}
	
	private void runAI(Entity e)
	{
		float targetX = player.centerX;
		float targetY = player.centerY;
        
		if (e.type == Entity.TYPE.ASTEROID)
		{
			
			float newAngle = e.getRotation()+0.5f; 
			newAngle = Trigonometrics.getAngleWithoutOverUndershootDeg(newAngle);
			e.setRotation(newAngle);
		}
        
        if (e.type == Entity.TYPE.GLIDER)
    	{
        	if (e.evading)
        	{
        		if (e.stopEvadingAt < System.currentTimeMillis()) e.evading = false;
        	}
        	else
        	{
	        	for (int i = 0; i < projectiles.size; i++)
				{
	        		Projectile p = projectiles.get(i);
					if (p.owner != null && p.owner == player)
					{
						float distanceToBullet = Trigonometrics.getDistanceFast(e.centerX, e.centerY, p.centerX, p.centerY);
						if (distanceToBullet < 340)
						{
							Vector2 v = Trigonometrics.getOrbitLocationDeg(targetX, targetY, player.getRotation()+e.evadingAngle, 500);
							targetX = v.x;
							targetY = v.y;
							e.evading = true;
							e.stopEvadingAt = System.currentTimeMillis()+200;
							
							float dx = e.centerX - targetX + e.targetSaltX; 
							float dy = e.centerY - targetY + e.targetSaltY; 
					        float distance = SquareRoot.fastSqrt((int)(dx*dx + dy*dy));
							
							e.speedX = (float)(e.speedFactor*-dx/distance);
			        		e.speedY = (float)(e.speedFactor*-dy/distance);
			        		e.speedX*=3f;
		        			e.speedY*=3f;
						}
					}
				}
        	}
    	}
        
        float dx = e.centerX - targetX + e.targetSaltX; 
		float dy = e.centerY - targetY + e.targetSaltY; 
        float distance = SquareRoot.fastSqrt((int)(dx*dx + dy*dy));
        
        //SHOOTING
        float shotRotation = e.getRotation();
        
        if (e.type != Entity.TYPE.ASTEROID && hp > 0f && !e.evading) //non shooting units
        {
	        if (e.type != Entity.TYPE.GUN)
	        {
	        	shotRotation+=getRandom(-20.0f, 20.0f);
	        }
	        else
	        {
	        	shotRotation+=getRandom(-4.0f, 4.0f);
	        }
	        
	        if (distance < Gdx.graphics.getHeight())
	        {
	        	if (e.type == Entity.TYPE.GLIDER)
	        	{
	        		addShot(shotRotation, e, Projectile.TYPE.THICK_DOT);
	        	}
	        	else if (e.type == Entity.TYPE.GUN)
	        	{
	        		addShot(shotRotation, e, Projectile.TYPE.SCYTHE);
	        	}
	        	else if (e.type == Entity.TYPE.CRAWLER)
	        	{
	        		addShot(shotRotation, e, Projectile.TYPE.THIN);
	        	}
	        	else
	        	{
	        		addShot(shotRotation, e, Projectile.TYPE.THICK_DOT);
	        	}
	        	
	        	if (e.type != Entity.TYPE.CRAWLER)
	        	{
	        		turnEnemiesToPlayer(e);
	        	}
	        }
        }
        
        //MOVING
        if (e.type != Entity.TYPE.GUN && e.type != Entity.TYPE.ASTEROID && !e.evading)
        {
        	float minDistance = 1;
        	
        	if (e.type != Entity.TYPE.DRONE)
        	{
        		minDistance = Gdx.graphics.getHeight()*0.6f;
        	}
        	
        	if (distance > minDistance && hp > 0f)
            {
        		float targetAngle = 57.2957795f*MathUtils.atan2(dy, dx);
        		e.speedX = (float)(e.speedFactor*-dx/distance);
        		e.speedY = (float)(e.speedFactor*-dy/distance);
        		e.setRotation(targetAngle);
            }
        }
        
//        if (e.type == Entity.TYPE.REAPER)
//        {
//        	e.setX(e.getX()+e.getWaveOffsetSin());
//        	e.setY(e.getY()+e.getWaveOffsetSin());
//        }
	}
	
	private Entity.TYPE getRandomEntityType()
	{
		int r = Util.getRandom(1, 7);
		if (r == 1) 		return Entity.TYPE.DRONE;
		else if (r == 2) 	return Entity.TYPE.CRAWLER;
		else if (r == 4) 	return Entity.TYPE.GUN;
		else if (r == 6) 	return Entity.TYPE.ASTEROID;
		else if (r == 7) 	return Entity.TYPE.GLIDER;
		else				return Entity.TYPE.ASTEROID;
	}
	
	private void computeMainGame()
    {
		if (supergunTimer > 0)
		{
			if (supergunTimer > System.currentTimeMillis())
			{
//				player.shootingCooldown = 5;
//				player.shootingCooldown = 80; // 5 * 0.016 ms (16us)
			}
			else
			{
//				player.shootingCooldown = 8;
//				player.shootingCooldown = 96; // 8 * 0.016 ms (16us)
				player.supergun = false;
			}
		}
		
		if (powerup != null)
		{
			if (powerup.getAlpha() > 0.0f)
			{
				powerup.compute();
			}
			else
			{
				powerup = null;
			}
		}
		
		for (int i = projectiles.size - 1; i >= 0; i--)
		{
			Projectile p = projectiles.get(i);

			if (p != null && !p.removeMe) p.compute(is30FPSMode());
		}
		
		//spawn not in players view
		//TODO not working fine
		if (enemies.size < MAX_ENEMIES)
		for (int i=0;i<(MAX_ENEMIES-enemies.size);i++)
		{
			if (enemies.size >= MAX_ENEMIES) return;
			
			float sx = getRandom(player.centerX-SPAWN_RANGE, player.centerX+SPAWN_RANGE);
			float sy = getRandom(player.centerY-SPAWN_RANGE, player.centerY+SPAWN_RANGE);
			calcV3.x = sx;
			calcV3.y = sy;
			
			/*
			 * You COULD do a WHILE, however mathemathically the loop could take long, if you are unlucky
			 */
			if (camera.frustum.pointInFrustum(calcV3))
			{
				continue;
			}
			
			addEnemyManually(sx, sy, getRandomEntityType());
		}
		
		if (gameState == GAMESTATE.INGAME)
		{
			if (hp > 0.0f)
			{
				playerPropuEffect.update(Gdx.graphics.getRawDeltaTime());
				int parts = (int)((Math.max(Math.abs(player.speedX),Math.abs(player.speedY)))*0.15f);
				playerPropuEffect.findEmitter("power").addParticles(parts);
//				playerPropuEffect.findEmitter("power").getTint().setColors(float[] colors)
				
				playerPropuEffect.setPosition(player.centerX, player.centerY);
			}
		}

		for (int i = powerupSprites.size - 1; i >= 0; i--)
		{
			PowerUpSprite pspr = powerupSprites.get(i);
			
			if (isSpriteWithinFrustum(pspr) && hp > 0f)
			{
				float dx = pspr.centerX - player.centerX; 
				float dy = pspr.centerY - player.centerY; 
		        float distance = SquareRoot.fastSqrt((int)(dx*dx + dy*dy));
				if (pspr.speedX != 0f || distance < PowerUpSprite.ATTRACTION_DISTANCE)
	            {
	        		pspr.speedX = (float)(-dx/distance);
	        		pspr.speedY = (float)(-dy/distance);
	            }
			}
			
			int d = (int)(Trigonometrics.getDistanceFast(pspr.centerX, pspr.centerY, player.centerX, player.centerY));
			boolean dance = (d > (SPAWN_RANGE*1)) ? false : true;
			
			pspr.compute(is30FPSMode(), dance);
			pspr.compute(is30FPSMode(), dance);
			pspr.compute(is30FPSMode(), dance);
			
			if (hp > 0f && player.getBoundingRectangle().overlaps(pspr.getBoundingRectangle()))
			{
				if (pspr.type != TYPE.POINTS)
				{
					addPowerUp(pspr.type);
					addTextEffect(powerup.str, pspr.centerX, pspr.centerY-30, powerup.color);
				}
				else
				{
					points+=BONUS_POINTS*getMultipl();
					addTextEffect(""+(int)(BONUS_POINTS*getMultipl()), pspr.centerX, pspr.centerY-30, new Color(0.3f, 0.9f, 0.3f, 1.0f));
					if (audio) sounds.get("points").play(soundVolume);
				}
				powerupSprites.removeIndex(i);
			}
			
			
			if (d > (SPAWN_RANGE*3))
			{
				powerupSprites.removeIndex(i);
				if (DEVKIT && VERBOSE) System.out.printf("Powerup too far away ( %dpx ), removed.\n", d);
			}
		}
		
		
		for (int i = texts.size - 1; i >= 0; i--)
		{
			texts.get(i).compute();
			if (texts.get(i).removeMe)
			{
				texts.removeIndex(i);
			}
		}
		
    	if (flashAlpha > 0)
    	{
    		flashAlpha-=0.06f;
    	}
    	
    	if (hp > 0.0f)
    	{
    		player.compute(is30FPSMode());
    		
    		if (player.turbo)
        	{
        		if (exh < 0.99f)
        		{
        			exh+=0.003f;
        		}
        		else
        		{
        			player.turbo = false;
        		}
        	}
    	}
    	
    	if (curRightFinger != null && ((int)curRightFinger.x != (int)rightFingerSource.x) || ((int)curRightFinger.y != (int)rightFingerSource.y))
    		addShot(currentShotAnglePlayer, player, currentShotType);
    	
    	
    	
    	
//		if (AI)
		if (BOSSAI && !boss.sleeping)
		{
    		runBossAI();
		}

    	for (int i = enemies.size - 1; i >= 0; i--)
		{
    		Entity e = enemies.get(i);
    		
    		//KI
    		if (AI)
    		{
	    		runAI(e);
    		}
            
			e.compute(is30FPSMode());
			
			int d = (int)(Trigonometrics.getDistanceFast(e.centerX, e.centerY, player.centerX, player.centerY));
			
			//remove enemies which are too far away
			if ( d > (SPAWN_RANGE*3) )
			{
//				System.out.println(e.getType()+"         \t  too far away("+d+" px), removed.");
				if (DEVKIT && VERBOSE) System.out.printf("%s too far away ( %dpx ), removed.\n", ""+e.getType(), d);
				e.removeMe = true;
			}
			
			if (e.removeMe)
			{
				enemies.removeIndex(i);
				
				//instant respawn
				addEnemyInRange(getRandomEntityType(), SPAWN_RANGE*4);
				
				continue;
			}
			else
			{
				if (hp > 0f && e.collisionDamage > 0 && player.getBoundingRectangle().overlaps(e.getBoundingRectangle()))
				{
					hurtPlayer(e.collisionDamage);
//					player.speedX = 0;
//					player.speedY = 0;
//					player.brake = 1.0f;
					destroyEnemy(e);
				}
				
				for (int j = 0; j < projectiles.size; j++)
				{
	        		Projectile p = projectiles.get(j);
				
					if (p.owner != null && p.owner == player && p.getBoundingRectangle().overlaps(e.getBoundingRectangle()))
					{
						p.removeMe = true;
						
						destroyEnemy(e);
						killCount++;
					}
				}
			}
		}
    	
    	if (BOSSAI && !boss.sleeping)
    	{
    		float dx = player.centerX - boss.centerX;
    		float dy = player.centerY - boss.centerY;
    		float targetAngle = 57.2957795f*MathUtils.atan2(dy, dx);
    		targetAngle-=180;
    		boss.setRotation(targetAngle);
    		
    		if (boss.isInnerBoxOverlap(player.getBoundingRectangle()))
    		{
    			hurtPlayer(0.01f);
    		}
    		
    		for (int i = 0; i < projectiles.size; i++)
			{
        		Projectile p = projectiles.get(i);
				if (p.owner != null && p.owner == player && boss.isInnerBoxContains(p.getBoundingRectangle()))
				{
		    		if (true)//(distance < 40)
		    		{
		    			p.removeMe = true;
						boss.hp--;
						addBossExplosionEffect(p.centerX, p.centerY);
						if (audio) sounds.get("hitPlayer").play(soundVolume);
		    		}
				}
			}
	    	
	    	if (boss.hp <= 0)
	    	{
	    		destroyBoss();
	    	}
    	}
		
		if (hp <= 0.0f)
		{
			if (respawnTime > System.currentTimeMillis())
			{
				 // if in the future, wait for respawn time
			}
			else
			{
				if (lifes <= 0)
				{
					gotoLeaderboard();
				}
				else
				{
					hp = 1.0f;
				}
			}
		}
		
    	if (BOSSAI)
    	{
	    	boss.compute(player.centerX, player.centerY, is30FPSMode());
	    	
	    	if (boss.sleeping)
			{
				if (System.currentTimeMillis() >= boss.wentToSleep+boss.WAKEUP_TIME)
				{
					boss.wakeUp(spaceMap, player);
//					if (audio) sounds.get("beware").play(soundVolume);
				}
			}
    	}
    	
    	if (exh > 0.0f)exh -=0.00200f;
    }


	private int getRelativeMouseX()
	{
		return (int)(Gdx.input.getX()*camera.zoom)+(int)(camera.position.x)-(int)((camera.viewportWidth*camera.zoom)/2.0f);
	}
	
	private int getRelativeMouseY()
	{
		return (int)(Gdx.input.getY()*camera.zoom)+(int)(camera.position.y)-(int)((camera.viewportHeight*camera.zoom)/2.0f);
	}
	@Override
	public void resize(int width, int height)
	{
		System.out.println("RESIZE: "+width+" x "+height);
		
		uiStage.setViewport(width, height, false);
		initScaleAmount(width, height);
		
	}
 
	@Override
	public void pause()
	{
		 music.pause();
		 System.out.println("PAUSE");
	}

	@Override
	public void resume()
	{
		music.play();
		System.out.println("RESUME");
	}
 
	@Override
	public void dispose()
	{
		System.out.println("DISPOSE");
		
		batch.dispose();
		
		
		uiStage.dispose();
		titleSkin.dispose();
		Leaderboard.skin.dispose();
		
		atlas.dispose();
		
		player = null;
	    boss = null;
	    powerup = null;

	    if (audio)
	    {
	    	for (Sound snd : sounds.values())
			{
				snd.dispose();
			}
	    	sounds.clear();
	    	music.stop();
	    	music.dispose();
	    }
	    
	    imgBoss.getTexture().dispose();


	    spaceMap = null;
	    
	    projectiles.clear();
	    enemies.clear();
	    partEffs.clear();
	    powerupSprites.clear();
	    texts.clear();
	    
	    for (int i=0;i<peExplosions.length;i++)
	    {
	    	peExplosions[i].dispose();
	    }
	    
	    for (int i=0;i<bossHitExplosions.length;i++)
	    {
	    	bossHitExplosions[i].dispose();
	    }
	    
	    playerPropuEffect.dispose();
	}
	
	public static final float getRandom(float minimum, float maximum)
    {
        return ((float)Math.random()*((maximum+1.0f)-minimum)+minimum);
    }

	private float getMultipl()
	{
		return (1.0f+(killCount/20.0f));
	}
	
	private void clearStrb()
	{
		if (strb.length() > 0) strb.delete(0, strb.length());
	}
	
	private void renderBossIcon()
	{
		int border_margin = 60;
		
		float dx = boss.centerX;
    	float dy = boss.centerY;
    	dx = dx - camera.position.x;
		dy = dy - camera.position.y;
		float targetAngle = 57.2957795f*MathUtils.atan2(dy, dx);
		targetAngle-=180;
		
		float drawY = dy+camera.viewportHeight*0.5f;
		float drawY2 = drawY-(+atlas.findRegion("boss_icon").getRegionHeight()*0.5f)+(+atlas.findRegion("boss_icon_arrow").getRegionHeight()*0.5f);
		if (drawY < border_margin) drawY = border_margin;
		if (drawY > Gdx.graphics.getHeight()-border_margin-(+atlas.findRegion("boss_icon_arrow").getRegionHeight())) drawY = Gdx.graphics.getHeight()-border_margin-(+atlas.findRegion("boss_icon_arrow").getRegionHeight());
		if (drawY2 < 16+border_margin) drawY2 = 16+border_margin;
		if (drawY2 > Gdx.graphics.getHeight()-border_margin-16-(+atlas.findRegion("boss_icon").getRegionHeight())) drawY2 = Gdx.graphics.getHeight()-border_margin-16-(+atlas.findRegion("boss_icon").getRegionHeight());
		
		float drawX = dx+camera.viewportWidth*0.5f;
		float drawX2 = drawX-(+atlas.findRegion("boss_icon").getRegionWidth()*0.4f);
		if (drawX < border_margin) drawX = border_margin;
		if (drawX > Gdx.graphics.getWidth()-border_margin-(atlas.findRegion("boss_icon_arrow").getRegionWidth())) drawX = Gdx.graphics.getWidth()-border_margin-(atlas.findRegion("boss_icon_arrow").getRegionWidth());
		if (drawX2 < 16+border_margin) drawX2 = 16+border_margin;
		if (drawX2 > Gdx.graphics.getWidth()-border_margin-16-atlas.findRegion("boss_icon").getRegionWidth()) drawX2 = Gdx.graphics.getWidth()-border_margin-16-atlas.findRegion("boss_icon").getRegionWidth();
		
		batch.draw(atlas.findRegion("boss_icon_arrow"), drawX, drawY, 
				atlas.findRegion("boss_icon_arrow").getRegionWidth()*0.5f, +atlas.findRegion("boss_icon_arrow").getRegionHeight()*0.5f, 
				atlas.findRegion("boss_icon_arrow").getRegionWidth(), +atlas.findRegion("boss_icon_arrow").getRegionHeight(), 
				1, 1, 
				targetAngle);
		
		batch.draw(atlas.findRegion("boss_icon"), drawX2, drawY2);
	}
	
	private void renderHUD()
	{
		if (DEVKIT && !isMobilePlatform())
		{
			renderMiniMap();
		}
		
		if (isMobilePlatform())
		{
			if (leftFingerSource != null) batch.draw(atlas.findRegion("stick"), leftFingerSource.x-(stickBigSize*0.5f), leftFingerSource.y-(stickBigSize*0.5f), stickBigSize, stickBigSize);
			if (curLeftFinger != null) batch.draw(atlas.findRegion("mini_stick"), curLeftFinger.x-(stickSmallSize*0.5f), curLeftFinger.y-(stickSmallSize*0.5f), stickSmallSize, stickSmallSize);

			if (rightFingerSource != null) batch.draw(atlas.findRegion("stick"), rightFingerSource.x-(stickBigSize*0.5f), rightFingerSource.y-(stickBigSize*0.5f), stickBigSize, stickBigSize);
			if (curRightFinger != null) batch.draw(atlas.findRegion("mini_stick"), curRightFinger.x-(stickSmallSize*0.5f), curRightFinger.y-(stickSmallSize*0.5f), stickSmallSize, stickSmallSize);
		}
		
		clearStrb();
		strb.append((int)points);
		bigFont.setColor(Color.WHITE);
		bigFont.draw(batch, strb, 		Gdx.graphics.getWidth()-(bigFont.getBounds(strb).width)-10, 8);
		
		clearStrb();
		strb.append("x");
		strb.append((int)getMultipl());
		
		medFont.setColor(Color.ORANGE);
		medFont.draw(batch, strb, 		Gdx.graphics.getWidth()-(medFont.getBounds(strb).width)-10, 8+bigFont.getLineHeight());
		
		clearStrb();
		strb.append("CHAIN");
		
		medFont.setColor(Color.WHITE);
		float cxo = Gdx.graphics.getWidth()-(medFont.getBounds(strb).width)-10; //chain x offset
		medFont.draw(batch, strb, 	cxo, 8+bigFont.getLineHeight()*2);
		clearStrb();
		strb.append(killCount);
		medFont.draw(batch, strb, 	cxo-(medFont.getBounds(strb).width)-(medFont.getBounds(stringSlot).width*2), 8+bigFont.getLineHeight()*2);
		
		if (DEVKIT && !isMobilePlatform())
		{
			font.draw(batch, "X: "+player.centerX, 		20, 240);
			font.draw(batch, "Y: "+player.centerY, 		20, 265);
		}
		
		if (DEVKIT)
		{
			font.draw(batch, "FPS: "+Gdx.graphics.getFramesPerSecond(), 		Gdx.graphics.getWidth()-font.getBounds("FPS: 88").width-15, 240);
//			font.draw(spriteBatch, "ships: "+(enemies.size+1), 		Gdx.graphics.getWidth()-font.getBounds("ships: 888").width-10, 260);
//			font.draw(spriteBatch, "bullets: "+(bullets.size), 		Gdx.graphics.getWidth()-font.getBounds("ships: 888").width-10, 280);
////			font.draw(spriteBatch, "shipsOnScreen: "+(shipsOnScreen+1), 		Gdx.graphics.getWidth()-font.getBounds("shipsOnScreen: 888").width-10, 300);
//			font.draw(spriteBatch, "pe: "+partEffs.size, 		Gdx.graphics.getWidth()-font.getBounds("pe: 888").width-10, 320);
//			font.draw(spriteBatch, "powerSpr: "+powerupSprites.size, 		Gdx.graphics.getWidth()-font.getBounds("powerSpr: 888").width-10, 340);
//			font.draw(spriteBatch, "propu count: "+playerPropuEffect.findEmitter("power").getActiveCount(), 		Gdx.graphics.getWidth()-font.getBounds("propu count: "+playerPropuEffect.findEmitter("power").getActiveCount()).width-10, 360);
//			font.draw(spriteBatch, "hp: "+hp, 		Gdx.graphics.getWidth()-font.getBounds("propu count: "+playerPropuEffect.findEmitter("power").getActiveCount()).width-10, 380);
		}
		
		//draw bomb & life icons
//		for (int i=0;i<bombs;i++)
//		{
//			float xr = (Gdx.graphics.getWidth()/2.0f)+32+(48*i);
//			spriteBatch.draw(atlas.findRegion("bomb_icon"), xr, 10);
//		}
//		for (int i=0;i<lifes;i++)
//		{
//			float xr = (Gdx.graphics.getWidth()/2.0f)-32-32-(48*i);
//			spriteBatch.draw(atlas.findRegion("life_icon"), xr, 10);
//		}
		
		batch.draw(atlas.findRegion("life_icon"), (10*scaleAmount), ((atlas.findRegion("life_icon").getRegionHeight()+40)*scaleAmount), atlas.findRegion("life_icon").getRegionWidth()*scaleAmount, atlas.findRegion("life_icon").getRegionHeight()*scaleAmount);
//		spriteBatch.draw(atlas.findRegion("bomb_icon"), 5, 5+atlas.findRegion("hp_hud").getRegionHeight()+5+atlas.findRegion("life_icon").getRegionHeight()+2);
		clearStrb();
		strb.append("x");
		strb.append(lifes);
		medFont.setColor(Color.WHITE);
		medFont.draw(batch, strb, (45*scaleAmount), ((atlas.findRegion("life_icon").getRegionHeight()+40)*scaleAmount)+(medFont.getBounds(strb).height*0.5f));
//		medFont.draw(spriteBatch, "x"+bombs, 40, 5+atlas.findRegion("hp_hud").getRegionHeight()+5+atlas.findRegion("life_icon").getRegionHeight()+2+(atlas.findRegion("bomb_icon").getRegionHeight()*0.5f)-(medFont.getBounds("x"+bombs).height*0.5f));
		
		if (!isSpriteWithinFrustum(boss) && !boss.sleeping)
		{
			renderBossIcon();
		}
		
		clearStrb();
		
		batch.setColor(0.33f, 1, 0.33f, 1);
		medFont.setColor(0.33f, 1, 0.33f, 1);
		if (hp < 0.35f)
		{
			batch.setColor(1, 0.75f, 0.33f, 1);
			medFont.setColor(1, 0.75f, 0.33f, 1);
		}
		if (hp < 0.15f)
		{
			batch.setColor(1, 0.33f, 0.33f, 1);
			medFont.setColor(1, 0.33f, 0.33f, 1);
		}
		
//		medFont.draw(batch, ""+hpdisplay, (-2*scaleAmount)+(atlas.findRegion("hp_hud").getRegionWidth()/2.0f)-(font.getBounds(""+hpdisplay).width/2.0f),
//														(10*scaleAmount)+(atlas.findRegion("hp_hud").getRegionHeight()/2.0f));
//		medFont.draw(batch, ""+hpdisplay, (-2*scaleAmount)+(atlas.findRegion("hp_hud").getRegionWidth()*scaleAmount/2.0f)-(font.getBounds(""+hpdisplay).width/2.0f),
//				10+(atlas.findRegion("hp_hud").getRegionHeight()*scaleAmount/2.0f));
//		batch.draw(atlas.findRegion("hp_hud"), 5, 5, atlas.findRegion("hp_hud").getRegionWidth()*scaleAmount, atlas.findRegion("hp_hud").getRegionHeight()*scaleAmount);
		batch.setColor(1, 1, 1, 1);
		medFont.setColor(1, 1, 1, 1);
		
		if (powerup != null && powerup.getAlpha() > 0.0f)
		{
//			bigFont.setColor(1,1,1, powerup.getAlpha());
//			bigFont.draw(spriteBatch, "BONUS !", (Gdx.graphics.getWidth()*0.5f)-(bigFont.getBounds("BONUS").width*0.5f), (Gdx.graphics.getHeight()*0.15f)-bigFont.getBounds("BONUS").height);
			bigFont.setColor(powerup.getColor());
			bigFont.draw(batch, powerup.getTypeString(), (Gdx.graphics.getWidth()*0.5f)-(bigFont.getBounds(powerup.getTypeString()).width*0.5f), (Gdx.graphics.getHeight()*0.15f)+(bigFont.getBounds(powerup.getTypeString()).height*0.5f));
			bigFont.setColor(Color.WHITE);
		}
		
//		renderCircularHp();
		
		
		renderIkaHpSystem();

		
		//renderBarHP();
	}
	
	public void renderBarHp()
	{
		float barLength = Gdx.graphics.getWidth()/2f;
		float cx = (Gdx.graphics.getWidth()/2f);
		
		batch.draw(atlas.findRegion("hp-frame-bg-end"), cx-(barLength/2f)-atlas.findRegion("hp-frame-bg-end").getRegionWidth(), 0);
		batch.draw(atlas.findRegion("hp-frame-bg-seg"), cx-(barLength/2f), 0, barLength, atlas.findRegion("hp-frame-bg-seg").getRegionHeight());
		batch.draw(atlas.findRegion("hp-frame-bg-end"), cx+(barLength/2f), 0, atlas.findRegion("hp-frame-bg-end").getRegionWidth()/2f, atlas.findRegion("hp-frame-bg-end").getRegionHeight()/2f, atlas.findRegion("hp-frame-bg-end").getRegionWidth(), atlas.findRegion("hp-frame-bg-end").getRegionHeight(), 1f, 1f, 180);
		
		float heightDiff = atlas.findRegion("hp-frame-bg-seg").getRegionHeight() - atlas.findRegion("hp-back-end").getRegionHeight();
		
		batch.draw(atlas.findRegion("hp-back-seg"), cx-(barLength/2f), heightDiff/2f, barLength*0.8f, atlas.findRegion("hp-back-seg").getRegionHeight());
		batch.draw(atlas.findRegion("hp-fore-seg"), 1+cx-(barLength/2f), 1+(heightDiff/2f), barLength*0.8f*hp, atlas.findRegion("hp-fore-seg").getRegionHeight());
		
//		batch.draw(atlas.findRegion("hp-back-end"), cx-(barLength/2f)-atlas.findRegion("hp-back-end").getRegionWidth(), heightDiff/2f);
//		batch.draw(atlas.findRegion("hp-back-end"), cx-(barLength/2f)+(barLength*0.8f), heightDiff/2f, atlas.findRegion("hp-back-end").getRegionWidth()/2f, atlas.findRegion("hp-back-end").getRegionHeight()/2f, atlas.findRegion("hp-back-end").getRegionWidth(), atlas.findRegion("hp-back-end").getRegionHeight(), 1f, 1f, 180);
		
//		batch.draw(atlas.findRegion("hp-fore-end"), 1+cx-(barLength/2f)-atlas.findRegion("hp-fore-end").getRegionWidth(), 1+(heightDiff/2f));s
//		batch.draw(atlas.findRegion("hp-fore-end"), 1+cx-(barLength/2f)+(barLength*0.8f*hp), 1+(heightDiff/2f), atlas.findRegion("hp-fore-end").getRegionWidth()/2f, atlas.findRegion("hp-fore-end").getRegionHeight()/2f, atlas.findRegion("hp-fore-end").getRegionWidth(), atlas.findRegion("hp-fore-end").getRegionHeight(), 1f, 1f, 180);
		
		medFont.setColor(Color.WHITE);
		clearStrb();
		strb.append((int)(hp*100));
		strb.append(" %");
		medFont.draw(batch, strb, 1+cx-(barLength/2f)+(barLength*0.8f)+20, 1+(heightDiff/2f));
	}
	
	public void renderCircularHp()
	{
		batch.setColor(new Color(1.0f, 0.7f, 0.0f, 0.9f));
		for (int i = 0;i<360*hp;i+=8)
		{
			batch.draw(atlas.findRegion("nhp-line"), Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f, 0, 0, 
					atlas.findRegion("nhp-line").getRegionWidth()/2*scaleAmount, atlas.findRegion("nhp-line").getRegionHeight()/2*scaleAmount, 
					1f, 1f, i);
		}
		batch.setColor(1, 1, 1, 1);
	}
	
	public void renderIkaHpSystem()
	{
		int hpdisplay = (int)(hp*100.0f);
		if (hp > 0.00f && hp < 0.01f) hpdisplay = 1;
		String hpStr = ""+hpdisplay+" %";
		
		
		IkaBar.render(batch, hp);
		
		float ika_height = atlas.findRegion("ika-hp-box").getRegionHeight()*1;
		float ika_width  = atlas.findRegion("ika-hp-box").getRegionWidth()*1;
		for (int ii = 0;ii< 10;ii++)
		{
			batch.setColor(1, 1, 1, 1);
			batch.draw(atlas.findRegion("ika-hp-box"), 10+(ika_width*ii), 10, ika_width, ika_height);
		}

		medFont.setColor(Color.WHITE);
		medFont.draw(batch, hpStr, 10, 10+10+ika_height);
	}
	
	@Override
	public void render()
	{
		float delta = Gdx.graphics.getDeltaTime();
		checkInputs();
		
		spaceColor[0][0] = bgSpaceMapColor.r;
		spaceColor[0][1] = bgSpaceMapColor.g;
		spaceColor[0][2] = bgSpaceMapColor.b;
		
		spaceColor[1][0] = cloudMapColor.r;
		spaceColor[1][1] = cloudMapColor.g;
		spaceColor[1][2] = cloudMapColor.b;
		
		for (int ix = 0;ix<2;ix++)
		{
			//change r,g or b per frame, not all
			int attr = random.nextInt(3);
			
			if (!spaceColorChangeDirection[ix][attr])
				spaceColor[ix][attr]+=((float)Math.random()/bgColorChangeSpeedFactor);
			else
				spaceColor[ix][attr]-=((float)Math.random()/bgColorChangeSpeedFactor);
			
			if (spaceColor[ix][attr] > 0.95f)
			{
				spaceColor[ix][attr] = 0.95f;
				spaceColorChangeDirection[ix][attr] = !spaceColorChangeDirection[ix][attr];
			}
			
			if (spaceColor[ix][attr] < 0.4f)
			{
				spaceColor[ix][attr] = 0.4f;
				spaceColorChangeDirection[ix][attr] = !spaceColorChangeDirection[ix][attr];
			}
			
//			for (int iy = 0;iy<3;iy++)
//			{
//				
//			}
		}
		

		bgSpaceMapColor.set(spaceColor[0][0], spaceColor[0][1], spaceColor[0][2], 1f);
		cloudMapColor.set(spaceColor[1][0], spaceColor[1][1], spaceColor[1][2], 1f);
		
		camera.update();
		
		Gdx.graphics.getGLCommon().glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.graphics.getGLCommon().glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		if (gameState == GAMESTATE.INGAME && !PAUSE) computeMainGame();
		adjustCamera();
		
		spaceMap.render(batch, camera, player, bgSpaceMapColor);
		
		font.setColor(Color.WHITE);
		
		for (int i = 0; i < powerupSprites.size; i++)
		{
			if (isSpriteWithinFrustum(powerupSprites.get(i)))
			{
				powerupSprites.get(i).draw(batch);
			}
		}

		for (int i = 0; i < enemies.size; i++)
		{
			Entity e = enemies.get(i);
		
			if (isSpriteWithinFrustum(e))
			{
				e.render(batch);
			}
		}


		if (gameState == GAMESTATE.INGAME && hp > 0.0f)
		{
			playerPropuEffect.draw(batch, delta);
			player.render(batch);
			
			

			
			
			if (player.shieldHealth > 0f)
			{
				if (flashAlpha > 0)
				{
					batch.setColor(flashAlpha, flashAlpha, 1, player.shieldHealth);
					batch.draw(atlas.findRegion("shield"), player.centerX-(atlas.findRegion("shield").getRegionWidth()*0.5f), player.centerY-(+atlas.findRegion("shield").getRegionHeight()*0.5f));
					batch.setColor(1, 1, 1, 1);
				}
				else
				{
					batch.setColor(0, 0, 1, player.shieldHealth);
					batch.draw(atlas.findRegion("shield"), player.centerX-(atlas.findRegion("shield").getRegionWidth()*0.5f), player.centerY-(+atlas.findRegion("shield").getRegionHeight()*0.5f));
					batch.setColor(1, 1, 1, 1);
				}
			}
			else if (flashAlpha > 0)
			{
				boolean useSecondaryColorExt = true && !isMobilePlatform();
				
				if (useSecondaryColorExt)
				{
					batch.end();
					boolean canSec = org.lwjgl.opengl.GLContext.getCapabilities().GL_EXT_secondary_color;
					if (canSec){
						org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.EXTSecondaryColor.GL_COLOR_SUM_EXT);
						org.lwjgl.opengl.EXTSecondaryColor.glSecondaryColor3ubEXT((byte)(1*255), (byte)(0*255), (byte)(0*255));
					}
					//you need this glTexEnvf, but its already default
//					Gdx.graphics.getGL11().glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
					player.setColor(1, 0, 0, flashAlpha);
					batch.begin();
					player.render(batch);
					batch.end();
					player.setColor(1, 1, 1, 1);
					if (canSec){
						org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.EXTSecondaryColor.GL_COLOR_SUM_EXT);
					}
					batch.begin();
				}
				else
				{
					player.setColor(1, 0.05f, 0.05f, flashAlpha);
					player.render(batch);
					player.setColor(1, 1, 1, 1);
				}
			}
		}
		
		if (boss.getAlpha() > 0f && isSpriteWithinFrustum(boss))
			boss.draw(batch);
		
		for (int i = projectiles.size - 1; i >= 0; i--)
		{
			Projectile p = projectiles.get(i);
			
			if (p.orbiter)
			{
				p.orbitDeg+=4;
				if (p.orbitDeg > 360) p.orbitDeg-=360;
				Vector2 vp = Trigonometrics.getOrbitLocationDeg(p.owner.centerX, p.owner.centerY, p.orbitDeg, 200);
				p.setPosition(vp.x, vp.y);
			}

			p.render(batch);
			
			float distanceToPlayer = Trigonometrics.getDistanceFast(player.centerX, player.centerY, p.centerX, p.centerY);
			
			if (distanceToPlayer > SPAWN_RANGE || p.removeMe)
			{
				projectiles.removeIndex(i);
				continue;
			}
			
			if (!(p.owner != null && p.owner == player) && hp > 0f  && player.getBoundingRectangle().overlaps(p.getBoundingRectangle()))
			{
				hurtPlayer(0.005f);
				p.removeMe = true;
				projectiles.removeIndex(i);
			}
		}

		for (int i = partEffs.size - 1; i >= 0; i--)
		{
			ParticleEffect pe = partEffs.get(i);
			
			if (pe == playerPropuEffect)	continue;
			
			pe.draw(batch, delta);
			
			if (pe.isComplete())
			{
//				partEffs.get(i).dispose();
				partEffs.removeIndex(i);
			}
			
		}

    	//ORBIT
//    	float maAngle = Trigonometrics.getAngleBetweenPointsDeg(player.centerX, player.centerY, getRelativeMouseX(), getRelativeMouseY());
//    	Vector2f v = Trigonometrics.getOrbitLocationDeg(player.centerX, player.centerY, maAngle, 180);
//		font.draw(spriteBatch, "o", player.chasingPoint.x, player.chasingPoint.y);

		spaceMap.renderClouds(batch, camera, player, cloudMapColor);
		
		for (int i = 0; i < texts.size; i++)
		{
			TextEffect te = texts.get(i);
		
			if (te != bossPointsEffect)
				te.render(batch, medFont);
			else
				te.render(batch, bigFont);
		}

		
		batch.setProjectionMatrix(hud_camera.combined);
		
		if (gameState == GAMESTATE.INGAME)
		{
			renderHUD();
		}
		else if (gameState == GAMESTATE.TITLESCREEN)
		{
			renderTitleScreen();
		}
		else if (gameState == GAMESTATE.LEADERBOARD)
		{
			renderLeaderboard();
		}
			
		batch.end();
		
		
//		shapeRender.begin(ShapeType.Line);
//		 
//			shapeRender.curve(50, 50, 640, 300, 640, 300, Gdx.graphics.getWidth()-50, 50, 100);
//		 
//		shapeRender.end();
		
		
		if (gameState == GAMESTATE.LEADERBOARD || gameState == GAMESTATE.TITLESCREEN)
		{
			uiStage.act(delta);
			uiStage.draw();
		}
		
		
//		if (!gameOver) 
//		{
//			if (DeltaHelper.averageDelta == -1.0f)
//			{
//				DeltaHelper.addDeltaTime(Gdx.graphics.getDeltaTime());
//			}
//		}
		
		if (is30FPSMode()) skip.sync();
		
	}
	
	private void renderMiniMap()
	{
		float miniFactor = 10f;
		float padding = Gdx.graphics.getHeight() / 30f;
		float viewW = camera.viewportWidth  / miniFactor;
		float viewH = camera.viewportHeight / miniFactor;
		float miniW = (camera.viewportWidth*2)  / miniFactor;
		float miniH = (camera.viewportHeight*2) / miniFactor;
		float drawOffX = Gdx.graphics.getWidth() -miniW-padding;
		float drawOffY = Gdx.graphics.getHeight()-miniH-padding;
		
		
		Primitives.fillRect(batch, drawOffX, drawOffY, miniW, miniH, new Color(0,0,0,0.8f));
		
		for (int i = 0; i < enemies.size; i++)
		{
			Entity e = enemies.get(i);
			if (e.centerX > camera.position.x+(camera.viewportWidth*1.5f) || e.centerX < camera.position.x-(camera.viewportWidth*1.5f) ||
					e.centerY > camera.position.y+(camera.viewportHeight*1.5f) || e.centerY < camera.position.y-(camera.viewportHeight*1.5f))
			{
				continue;
			}
			float diffX = e.centerX - camera.position.x;
			float diffY = e.centerY - camera.position.y;
			diffX/=miniFactor*1.5f;
			diffY/=miniFactor*1.5f;
		
			if (e.type == Entity.TYPE.ASTEROID)
				Primitives.fillRect(batch, drawOffX+(miniW/2f)+diffX,   drawOffY+(miniH/2f)+diffY, 1, 1, new Color(0.4f,0.4f,0.4f,0.9f));
			else
				Primitives.fillRect(batch, drawOffX+(miniW/2f)+diffX,   drawOffY+(miniH/2f)+diffY, 1, 1, new Color(0.4f,0.4f,1.0f,0.9f));
		}
		
		
		for (int i = 0; i < projectiles.size; i++)
		{
    		Projectile e = projectiles.get(i);
    		if (e.centerX > camera.position.x+(camera.viewportWidth*1.5f) || e.centerX < camera.position.x-(camera.viewportWidth*1.5f) ||
					e.centerY > camera.position.y+(camera.viewportHeight*1.5f) || e.centerY < camera.position.y-(camera.viewportHeight*1.5f))
			{
				continue;
			}
			float diffX = e.centerX - camera.position.x;
			float diffY = e.centerY - camera.position.y;
			diffX/=miniFactor*1.5f;
			diffY/=miniFactor*1.5f;
    		
    		Primitives.fillRect(batch, drawOffX+(miniW/2f)+diffX,   drawOffY+(miniH/2f)+diffY, 1, 1, Color.ORANGE);
		}
		
		for (int i = powerupSprites.size - 1; i >= 0; i--)
		{
			PowerUpSprite pspr = powerupSprites.get(i);
    		if (pspr.centerX > camera.position.x+(camera.viewportWidth*1.5f) || pspr.centerX < camera.position.x-(camera.viewportWidth*1.5f) ||
    				pspr.centerY > camera.position.y+(camera.viewportHeight*1.5f) || pspr.centerY < camera.position.y-(camera.viewportHeight*1.5f))
			{
				continue;
			}
			float diffX = pspr.centerX - camera.position.x;
			float diffY = pspr.centerY - camera.position.y;
			diffX/=miniFactor*1.5f;
			diffY/=miniFactor*1.5f;
    		
    		Primitives.fillRect(batch, drawOffX+(miniW/2f)+diffX,   drawOffY+(miniH/2f)+diffY, 1, 1, Color.PINK);
		}
		
		Primitives.fillRect(batch, drawOffX+(miniW/2f),   drawOffY+(miniH/2f),   1, 1, Color.WHITE);
		Primitives.fillRect(batch, drawOffX+(miniW/2f)+1, drawOffY+(miniH/2f),   1, 1, Color.WHITE);
		Primitives.fillRect(batch, drawOffX+(miniW/2f)-1, drawOffY+(miniH/2f),   1, 1, Color.WHITE);
		Primitives.fillRect(batch, drawOffX+(miniW/2f),   drawOffY+(miniH/2f)+1, 1, 1, Color.WHITE);
		Primitives.fillRect(batch, drawOffX+(miniW/2f),   drawOffY+(miniH/2f)-1, 1, 1, Color.WHITE);
		Primitives.fillRect(batch, drawOffX+(miniW/2f)+2, drawOffY+(miniH/2f),   1, 1, Color.WHITE);
		Primitives.fillRect(batch, drawOffX+(miniW/2f)-2, drawOffY+(miniH/2f),   1, 1, Color.WHITE);
		Primitives.fillRect(batch, drawOffX+(miniW/2f),   drawOffY+(miniH/2f)+2, 1, 1, Color.WHITE);
		Primitives.fillRect(batch, drawOffX+(miniW/2f),   drawOffY+(miniH/2f)-2, 1, 1, Color.WHITE);
		
		Primitives.drawRect(batch, drawOffX+(miniW/2f)-(viewW/2f), drawOffY+(miniH/2f)-(viewH/2f), viewW, viewH, Color.GRAY);
		
		Primitives.drawRect(batch, drawOffX, drawOffY, miniW, miniH, Color.WHITE);
		

	}
	
	private void renderTitleScreen()
	{
		batch.draw(atlas.findRegion("title"), (Gdx.graphics.getWidth()/2.0f)-(atlas.findRegion("title").getRegionWidth()*scaleAmount/2.0f), 0+(Gdx.graphics.getHeight()/10.0f), atlas.findRegion("title").getRegionWidth()*scaleAmount, atlas.findRegion("title").getRegionHeight()*scaleAmount);
		
		
		batch.draw(atlas.findRegion("at_title"), (Gdx.graphics.getWidth()/2.0f)-(atlas.findRegion("at_title").getRegionWidth()*scaleAmount/2.0f), Gdx.graphics.getHeight()-atlas.findRegion("at_title").getRegionHeight()*scaleAmount-10, atlas.findRegion("at_title").getRegionWidth()*scaleAmount, atlas.findRegion("at_title").getRegionHeight()*scaleAmount);
	}
	
	private void renderLeaderboard()
	{
		monoFont.setColor(Color.WHITE);
		bigFont.setColor(Color.WHITE);
		medFont.setColor(Color.WHITE);
		monoFont.draw(batch, "Leaderboard", (Gdx.graphics.getWidth()/2.0f)-(monoFont.getBounds("Leaderboard").width/2.0f), 0-(monoFont.getLineHeight()*-0.5f));

		if (Leaderboard.scores != null)
		{
//			medFont.draw(spriteBatch, "HighScores", Gdx.graphics.getWidth()*0.05f, 0-(bigFont.getLineHeight()*-1.8f));
			
			float offY = (monoFont.getLineHeight()*3.4f);
			
			String myPoints = Score.addCommasToNumericString(""+points);
			
			if (!markScore)
			{
				monoFont.setColor(Color.YELLOW);
				monoFont.draw(batch, "YOU", (Gdx.graphics.getWidth()*0.08f)-(monoFont.getBounds("YOU").width/2f), 	offY-(monoFont.getLineHeight()*1.3f));
				monoFont.draw(batch, myPoints, (Gdx.graphics.getWidth()*0.9f)-monoFont.getBounds(myPoints).width, 	offY-(monoFont.getLineHeight()*1.3f));
			}
			
			
			for (int i=0;i<Leaderboard.scores.length;i++)
			{
				if (Leaderboard.scores[i] != null)
				{
					monoFont.setColor(Color.WHITE);
					if (markScore && Leaderboard.scores[i].getName().equals(Leaderboard.textField.getText()) && Leaderboard.scores[i].getPoints() == points)
					{
						monoFont.setColor(Color.YELLOW);
					}
					
					String thePoints = Leaderboard.scores[i].getPaddedPointsString();
					String theNum = ""+(i+1);
//					medFont.draw(spriteBatch, "#"+(i+1), Gdx.graphics.getWidth()*0.05f, 				0-(medFont.getLineHeight()*-5.5f)+(medFont.getLineHeight()*i*1.1f));
//					medFont.draw(spriteBatch, scores[i].getName(), Gdx.graphics.getWidth()*0.15f, 		0-(medFont.getLineHeight()*-5.5f)+(medFont.getLineHeight()*i*1.1f));
//					medFont.draw(spriteBatch, ""+scores[i].getPoints(), Gdx.graphics.getWidth()*0.45f, 	0-(medFont.getLineHeight()*-5.5f)+(medFont.getLineHeight()*i*1.1f));
					
					float yp = offY+((monoFont.getLineHeight()*1.1f)*i);
					monoFont.draw(batch, theNum, (Gdx.graphics.getWidth()*0.08f)-(monoFont.getBounds(theNum).width/2f), 	yp);
					monoFont.draw(batch, Leaderboard.scores[i].getName(), Gdx.graphics.getWidth()*0.15f, 				yp);
					monoFont.draw(batch, thePoints, (Gdx.graphics.getWidth()*0.9f)-monoFont.getBounds(thePoints).width, 	yp);
				}
			}
		}
		else
		{
			monoFont.setColor(Color.RED);
			monoFont.draw(batch, scoreErrorMsg, (Gdx.graphics.getWidth()/2f)-(monoFont.getBounds(scoreErrorMsg).width/2f), (Gdx.graphics.getHeight()/2f));
		}
	}
	
	class NebulaGamePadHandler extends ControllerAdapter
	{
		@Override
		public boolean buttonDown (Controller controller, int buttonIndex)
		{
			if (buttonIndex == XBox360Pad.BUTTON_BACK)
			{
				goBack();
			}
			
//			System.out.println("Button: "+buttonIndex );
			return false;
		}
		
		@Override
		public boolean povMoved (Controller controller, int povCode, PovDirection value)
		{
			return false;
		}
		
		@Override
		public boolean xSliderMoved (Controller controller, int sliderCode, boolean value)
		{
			
			return false;
		}
		
		@Override
		public boolean ySliderMoved (Controller controller, int sliderCode, boolean value)
		{
			return false;
		}
		
		@Override
		public boolean axisMoved (Controller controller, int axisCode, float value)
		{
			
			//  y / verti      |   x / hori
			if (axisCode == XBox360Pad.AXIS_LEFT_Y || axisCode == XBox360Pad.AXIS_LEFT_X) // left stick
			{
				System.out.println("axis: left" );
				
				if (axisCode == XBox360Pad.AXIS_LEFT_Y)
				{
					padStickValue[0][1] = value;
//					System.out.println("stick: "+axisCode+" , "+value);
				}
				else if (axisCode == XBox360Pad.AXIS_LEFT_X)
				{
					padStickValue[0][0] = value;
				}
			}
			//          x / hori    |   y / verti
			else if (axisCode == XBox360Pad.AXIS_RIGHT_X || axisCode == XBox360Pad.AXIS_RIGHT_Y) // right stick
			{
				System.out.println("stick: "+axisCode+" , "+value);
				
				if (axisCode == XBox360Pad.AXIS_RIGHT_Y)
				{
					padStickValue[1][1] = value;
				}
				else if (axisCode == XBox360Pad.AXIS_RIGHT_X)
				{
					padStickValue[1][0] = value;
				}
			}
			else
			{
				if (axisCode == XBox360Pad.AXIS_LEFT_TRIGGER)
				{
					if (value > 0.1f)
					{
						System.out.println("LT down "+value);
					}
					else if (value < -0.1f)
					{
						System.out.println("RT down "+value);
					}
				}
				
			}
			
			
			
			return false;
		}
	}
	
	private class MyInputProcessor implements InputProcessor
	{
		@Override public boolean keyDown(int keycode)
    	{
			if (keycode == Keys.ESCAPE || (Gdx.app.getType() == ApplicationType.Android && keycode == Keys.BACK))
			{
				goBack();
			}
			
			if (DEVKIT)
			{

				
				if (keycode == Keys.H)
				{
					hurtPlayer(1f);
				}
				
				if (keycode == Keys.NUM_1)
				{
					addPowerUp(TYPE.ANGULAR);
				}
				
				if (keycode == Keys.NUM_2)
				{
					addPowerUp(TYPE.HOMING);
				}
				
				if (keycode == Keys.NUM_3)
				{
					addPowerUp(TYPE.RING);
				}
				
				if (keycode == Keys.NUM_4)
				{
					addPowerUp(TYPE.SCYTHE);
				}
				
				if (keycode == Keys.NUM_5)
				{
					addPowerUp(TYPE.BALL);
				}
				
				if (keycode == Keys.NUM_6)
				{
					addPowerUp(PowerUp.TYPE.TURBO);
				}
				
				if (keycode == Keys.NUM_7)
				{
					addPowerUp(PowerUp.TYPE.SHIELD);
				}
				
				if (keycode == Keys.NUM_8)
				{
					addPowerUp(PowerUp.TYPE.SUPER_GUN);
				}
				
				if (keycode == Keys.PAGE_UP)
				{
					camera.zoom += 0.25f;
				}
				
				if (keycode == Keys.PAGE_DOWN)
				{
					camera.zoom -= 0.25f;
				}
				
				if (keycode == Keys.ENTER)
				{
					if (Gdx.input.isKeyPressed(Keys.ALT_LEFT))
					{
						//TODO proper display mode
						//this display mode would probably not exist as fullscreenmode
						DisplayMode mode = Gdx.graphics.getDesktopDisplayMode();
						Gdx.graphics.setDisplayMode(mode.width, mode.height, !Gdx.graphics.isFullscreen());
					}
				}
			}
    		
    		return true;
    	}
		
		@Override
        public boolean touchUp(int x, int y, int pointer, int button)
        {
			if (isMobilePlatform())
			{
				if (pointer == leftFingerIndex)
	        	{
//	        		leftFingerSource = null;
	        		curLeftFinger = leftFingerSource.cpy();
	        		leftFingerIndex = -1;
	        	}
	        	else if (pointer == rightFingerIndex)
	        	{
//	        		rightFingerSource = null;
	        		curRightFinger = rightFingerSource.cpy();
	        		rightFingerIndex = -1;
	        	}
			}
			
        	return true;
        }
		
		public boolean touchDragged(int x, int y, int pointer)
		{
			if (isMobilePlatform())
			{
				if (pointer == leftFingerIndex)
	        	{
	        		curLeftFinger = new Vector2(x, y);
	        	}
	        	else if (pointer == rightFingerIndex)
	        	{
	        		curRightFinger = new Vector2(x, y);
	        	}
				
				
				if (leftFingerSource != null && curLeftFinger != null)
				{
					float curAngle = Trigonometrics.getAngleBetweenPointsDeg(leftFingerSource.x, leftFingerSource.y, curLeftFinger.x, curLeftFinger.y);
					curAngle = Trigonometrics.getAngleWithoutOverUndershootDeg(curAngle);
					
					//stop over shooting
					if (Trigonometrics.getDistanceFast(curLeftFinger, leftFingerSource) > stickSmallSize)
					{
						curLeftFinger = Trigonometrics.getOrbitLocationDeg(leftFingerSource.x, leftFingerSource.y, curAngle, stickSmallSize).cpy();
					}
					
//					int deltaAngle = (int)Math.abs(curAngle)-(int)Math.abs(player.getRotation());
//					player.brake = deltaAngle/10f;
					
					player.setRotation(curAngle+180);
				}
				
				if (rightFingerSource != null && curRightFinger != null)
				{
					float angle = Trigonometrics.getAngleBetweenPointsDeg(rightFingerSource.x, rightFingerSource.y, curRightFinger.x, curRightFinger.y);
					
					//stop over shooting
					if (Trigonometrics.getDistanceFast(curRightFinger, rightFingerSource) > stickSmallSize)
					{
						curRightFinger = Trigonometrics.getOrbitLocationDeg(rightFingerSource.x, rightFingerSource.y, angle, stickSmallSize).cpy();
					}
					
					currentShotAnglePlayer = angle;
					currentShotAnglePlayer+=180;
				}
			}
			
			return true;
		}
        
        @Override
        public boolean touchDown (int x, int y, int pointer, int button)
        {
        	if (button == 2 && DEVKIT)
    		{
    			PAUSE = !PAUSE;
    		}
        	
        	if (isMobilePlatform())
        	{
        		if (x < Gdx.graphics.getWidth()*0.5f)
            	{
//            		leftFingerSource = new Vector2(x, y);
            		leftFingerIndex = pointer;
            	}
            	else
            	{
//            		rightFingerSource = new Vector2(x, y);
            		rightFingerIndex = pointer;
            	}
        	}
        	
        	return true;
        }
		
		@Override public boolean scrolled(int amount) // MOUSE WHEEL
		{
			
			return true;
		}

		@Override
		public boolean keyTyped(char arg0) {
			return false;
		}

		@Override
		public boolean keyUp(int arg0) {
			return false;
		}

		@Override
		public boolean mouseMoved(int arg0, int arg1) {
			return false;
		}
		
	}
	
	class MyStage extends Stage
	{
		@Override
		public boolean keyDown(int keycode)
		{
			if (keycode == Keys.ESCAPE || (Gdx.app.getType() == ApplicationType.Android && keycode == Keys.BACK))
			{
				goBack();
			}
			
			return super.keyDown(keycode);
		}
	}

}