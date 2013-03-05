package com.prunn.rfdynhud.widgets.prunn.PorscheSupercup.timer;

import java.awt.Font;
import java.io.IOException;
import net.ctdp.rfdynhud.gamedata.GamePhase;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.SessionLimit;
import net.ctdp.rfdynhud.gamedata.SessionType;
import net.ctdp.rfdynhud.gamedata.YellowFlagState;
import net.ctdp.rfdynhud.properties.ColorProperty;
import net.ctdp.rfdynhud.properties.FontProperty;
import net.ctdp.rfdynhud.properties.ImagePropertyWithTexture;
import net.ctdp.rfdynhud.properties.IntProperty;
import net.ctdp.rfdynhud.properties.PropertiesContainer;
import net.ctdp.rfdynhud.properties.PropertyLoader;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.util.TimingUtil;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.BoolValue;
import net.ctdp.rfdynhud.values.EnumValue;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.values.StringValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetPorscheSupercup;

/**
 * @author Prunn
 * copyright@Prunn2011
 * 
 */
public class SessionTimerWidget extends Widget
{
	
	private final EnumValue<YellowFlagState> SCState = new EnumValue<YellowFlagState>();
    private final EnumValue<GamePhase> gamePhase = new EnumValue<GamePhase>();
    private final EnumValue<GamePhase> oldGamePhase = new EnumValue<GamePhase>();
    private final IntValue LapsLeft = new IntValue();
    private final BoolValue sectorYellowFlag = new BoolValue();
    private final BoolValue GreenFlag = new BoolValue();
    private final FloatValue sessionTime = new FloatValue(-1F, 0.1F);
    private DrawnString dsSession = null;
    private DrawnString dsSC = null;
    private String strlaptime = "";
    private final StringValue strLaptime = new StringValue( "" );
    private ImagePropertyWithTexture imgBG = new ImagePropertyWithTexture( "imgBG", "prunn/PorscheSupercup/timer_bg.png" );
    private ImagePropertyWithTexture imgBGYellow = new ImagePropertyWithTexture( "imgBGYellow", "prunn/PorscheSupercup/timer_yellow.png" );
    private ImagePropertyWithTexture imgBGGreen = new ImagePropertyWithTexture( "imgBGGreen", "prunn/PorscheSupercup/timer_green.png" );
    protected final FontProperty f1_2011Font = new FontProperty("Main Font", PrunnWidgetSetPorscheSupercup.GP2_FONT_NAME);
    private final ColorProperty fontColor1 = new ColorProperty( "fontColor1", PrunnWidgetSetPorscheSupercup.FONT_COLOR1_NAME );
    private final ColorProperty fontColor2 = new ColorProperty("fontColor2", PrunnWidgetSetPorscheSupercup.FONT_COLOR2_NAME);
    private ColorProperty drawnFontColor;
    private long visibleEnd;
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    
   
    
    @Override
    public void onRealtimeEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onRealtimeEntered( gameData, isEditorMode );
        LapsLeft.reset();
    	sectorYellowFlag.reset();
    	SCState.reset();
        gamePhase.reset();
        String cpid = "Y29weXJpZ2h0QFBydW5uMjAxMQ";
        if(!isEditorMode)
            log(cpid);
        
    }
    public void onSessionStarted(SessionType sessionType, LiveGameData gameData, boolean isEditorMode)
    {
        super.onSessionStarted(sessionType, gameData, isEditorMode);
        gamePhase.reset();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initSubTextures( LiveGameData gameData, boolean isEditorMode, int widgetInnerWidth, int widgetInnerHeight, SubTextureCollector collector )
    {
    }
    
    @Override
    protected void initialize( LiveGameData gameData, boolean isEditorMode, DrawnStringFactory drawnStringFactory, TextureImage2D texture, int width, int height )
    {
        
        int fh = TextureImage2D.getStringHeight( "0%C", f1_2011Font );
        int rowHeight = height/2;
        dsSession = drawnStringFactory.newDrawnString( "dsSession",width/2 , rowHeight/2 - fh/2 + fontyoffset.getValue(), Alignment.CENTER, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        dsSC = drawnStringFactory.newDrawnString( "dsInfo", width/2, rowHeight + rowHeight/2 - fh/2 + fontyoffset.getValue(), Alignment.CENTER, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor1.getColor() );
        imgBG.updateSize( width, rowHeight*95/100, isEditorMode );
        imgBGYellow.updateSize( width, rowHeight*95/100, isEditorMode );
        imgBGGreen.updateSize( width, rowHeight*95/100, isEditorMode );
        
    }
    
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        
        super.updateVisibility(gameData, isEditorMode);
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        SCState.update(scoringInfo.getYellowFlagState());
        sectorYellowFlag.update(scoringInfo.getSectorYellowFlag(scoringInfo.getViewedVehicleScoringInfo().getSector()));
        gamePhase.update(scoringInfo.getGamePhase());
        
        if(gamePhase.getValue() == GamePhase.GREEN_FLAG && oldGamePhase.getValue() == GamePhase.BEFORE_SESSION_HAS_BEGUN && gamePhase.hasChanged())
            visibleEnd = scoringInfo.getSessionNanos() + 5000000000l;
        
        oldGamePhase.update(scoringInfo.getGamePhase());
        
        if(scoringInfo.getSessionNanos() < visibleEnd)
            GreenFlag.update( true );
        else
            GreenFlag.update( false );
        
        if(GreenFlag.hasChanged() && !isEditorMode)
            forceCompleteRedraw(true);
        
        if((SCState.hasChanged() || sectorYellowFlag.hasChanged()) && !isEditorMode)
            forceCompleteRedraw(true);
        
        if( scoringInfo.getGamePhase() == GamePhase.FORMATION_LAP )
            return false;
        if( scoringInfo.getGamePhase() == GamePhase.STARTING_LIGHT_COUNTDOWN_HAS_BEGUN && scoringInfo.getEndTime() <= scoringInfo.getSessionTime() )
            return false;
        
        return true;
        
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        
        int rowHeight = height/2;
        
        if(sectorYellowFlag.getValue() || (SCState.getValue() != YellowFlagState.NONE && SCState.getValue() != YellowFlagState.RESUME) || isEditorMode)
            texture.clear( imgBGYellow.getTexture(), offsetX, offsetY, false, null );
        else
            texture.clear( imgBG.getTexture(), offsetX, offsetY, false, null );
    
        if((SCState.getValue() != YellowFlagState.NONE && SCState.getValue() != YellowFlagState.RESUME) || isEditorMode)
            texture.clear( imgBG.getTexture(), offsetX, offsetY + rowHeight, false, null );
        else
            if(SCState.getValue() == YellowFlagState.RESUME || GreenFlag.getValue())
                texture.clear( imgBGGreen.getTexture(), offsetX, offsetY + rowHeight, false, null );
             
    }
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        String strSC = "";
        if (scoringInfo.getSessionType().isRace() && scoringInfo.getViewedVehicleScoringInfo().getSessionLimit() == SessionLimit.LAPS)
    	{
            LapsLeft.update(scoringInfo.getMaxLaps() - scoringInfo.getLeadersVehicleScoringInfo().getLapsCompleted());
            
	    	
	    	if ( needsCompleteRedraw || LapsLeft.hasChanged() )
	    	    strlaptime = LapsLeft.getValueAsString() + " / " + scoringInfo.getMaxLaps();
	    
	    	
		}
    	else // Test day only
    		if(scoringInfo.getSessionType().isTestDay())
    		    strlaptime = scoringInfo.getViewedVehicleScoringInfo().getLapsCompleted() + "  /  ";
    		else // any other timed session (Race, Qualify, Practice)
	    	{
    		    sessionTime.update(scoringInfo.getSessionTime());
	    		float endTime = scoringInfo.getEndTime();
	    		
                if ( needsCompleteRedraw || sessionTime.hasChanged() )
		        {
		        	if(gamePhase.getValue() == GamePhase.SESSION_OVER || (endTime <= sessionTime.getValue() && gamePhase.getValue() != GamePhase.STARTING_LIGHT_COUNTDOWN_HAS_BEGUN ) )
		        	    strlaptime = "0:00";
			        else
        			    if(gamePhase.getValue() == GamePhase.STARTING_LIGHT_COUNTDOWN_HAS_BEGUN && endTime <= sessionTime.getValue())
		        		    strlaptime = "0:00";
		        		else
		        		{
		        		    
		        		    strlaptime = TimingUtil.getTimeAsString(endTime - sessionTime.getValue(), true, false);
		        	
        		        	if (strlaptime.charAt( 0 ) == '0')
        		        	    strlaptime = strlaptime.substring( 1 );
        		        	if (strlaptime.charAt( 0 ) == '0')
                                strlaptime = strlaptime.substring( 2 );
        		        	if (strlaptime.charAt( 0 ) == '0')
                                strlaptime = strlaptime.substring( 1 );
		        		}
		        }
	    		
	    	
	    	}
        
        strLaptime.update( strlaptime );
        
        if ( needsCompleteRedraw || ( clock.c() && strLaptime.hasChanged() ) )
        {
            if(sectorYellowFlag.getValue() || (SCState.getValue() != YellowFlagState.NONE && SCState.getValue() != YellowFlagState.RESUME) || isEditorMode)
                drawnFontColor = fontColor1;
            else
                drawnFontColor = fontColor1;
            
                        
            if((SCState.getValue() != YellowFlagState.NONE && SCState.getValue() != YellowFlagState.RESUME) || isEditorMode)
                strSC = "SC";
            else
                strSC = "";
            
            
            dsSession.draw( offsetX, offsetY, strlaptime,drawnFontColor.getColor(), texture );
            dsSC.draw( offsetX, offsetY, strSC, texture );
        }  
        
      
        
    }
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        writer.writeProperty( f1_2011Font, "timeFont" );
        writer.writeProperty( fontColor1, "" );
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty( fontyoffset, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        if ( loader.loadProperty( f1_2011Font ) );
        else if ( loader.loadProperty( fontColor1 ) );
        else if ( loader.loadProperty( fontColor2 ) );
        else if ( loader.loadProperty( fontyoffset ) );
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Font" );
        propsCont.addProperty( f1_2011Font );
        propsCont.addProperty( fontColor1 );
        propsCont.addProperty( fontColor2 );
        propsCont.addGroup( "Session Names" );
        propsCont.addProperty( fontyoffset );
        
    }
    @Override
    protected boolean canHaveBorder()
    {
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareForMenuItem()
    {
        super.prepareForMenuItem();
        
        getFontProperty().setFont( "Dialog", Font.PLAIN, 6, false, true );
    }
    
    public SessionTimerWidget()
    {
        super( PrunnWidgetSetPorscheSupercup.INSTANCE, PrunnWidgetSetPorscheSupercup.WIDGET_PACKAGE_PorscheSupercup, 19.0f, 5.0f );
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetPorscheSupercup.GP2_FONT_NAME );
        //getBorderProperty().setBorder( null );
    }
}
