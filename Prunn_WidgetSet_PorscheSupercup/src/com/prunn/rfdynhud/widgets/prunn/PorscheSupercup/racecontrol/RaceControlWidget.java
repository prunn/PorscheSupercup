package com.prunn.rfdynhud.widgets.prunn.PorscheSupercup.racecontrol;

import java.awt.Font;
import java.io.IOException;

import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetPorscheSupercup;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.gamedata.YellowFlagState;
import net.ctdp.rfdynhud.properties.BooleanProperty;
import net.ctdp.rfdynhud.properties.ColorProperty;
import net.ctdp.rfdynhud.properties.DelayProperty;
import net.ctdp.rfdynhud.properties.FontProperty;
import net.ctdp.rfdynhud.properties.ImagePropertyWithTexture;
import net.ctdp.rfdynhud.properties.IntProperty;
import net.ctdp.rfdynhud.properties.PropertiesContainer;
import net.ctdp.rfdynhud.properties.PropertyLoader;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.BoolValue;
import net.ctdp.rfdynhud.values.EnumValue;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

/**
 * @author Prunn
 * copyright@Prunn2011
 * 
 */


public class RaceControlWidget extends Widget
{
    private DrawnString dsRC = null;
    private DrawnString dsMessage = null;
    private final EnumValue<YellowFlagState> SCState = new EnumValue<YellowFlagState>();
    private int widgetpart = 0;
    private IntValue Penalties[];
    private IntValue Pentotal =  new IntValue();
    private int flaggeddriver = 0;
    private int LastSC = 0;
    private final ImagePropertyWithTexture imgTitle = new ImagePropertyWithTexture( "imgName", "prunn/PorscheSupercup/data_title.png" );
    private final ImagePropertyWithTexture imgTime = new ImagePropertyWithTexture( "imgTime", "prunn/PorscheSupercup/race_control.png" );
    private BooleanProperty ShowDRS = new BooleanProperty("Show drs enabled", false); 
    private BoolValue DRS_enabled = new BoolValue();
    protected final FontProperty f1_2011Font = new FontProperty("Main Font", PrunnWidgetSetPorscheSupercup.GP2_FONT_NAME);
    private final ColorProperty fontColor1 = new ColorProperty( "fontColor1", PrunnWidgetSetPorscheSupercup.FONT_COLOR1_NAME );
    private final ColorProperty fontColor2 = new ColorProperty( "fontColor2", PrunnWidgetSetPorscheSupercup.FONT_COLOR2_NAME );
    private final DelayProperty visibleTime;
    private long visibleEnd;
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    
    
    @Override
    public void onCockpitEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onCockpitEntered( gameData, isEditorMode );
        String cpid = "Y29weXJpZ2h0QFBydW5uMjAxMQ";
        if(!isEditorMode)
            log(cpid);
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
        int numveh = gameData.getScoringInfo().getNumVehicles();
        
        imgTitle.updateSize( Math.round(width * 0.25f), (height/2)*95/100, isEditorMode );
        imgTime.updateSize( width, (height/2)*95/100, isEditorMode );
        
        dsRC = drawnStringFactory.newDrawnString( "dsRC", imgTitle.getTexture().getWidth()/2, height/4 - fh/2 + fontyoffset.getValue(), Alignment.CENTER, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        dsMessage = drawnStringFactory.newDrawnString( "dsMessage", width*2/100, height*3/4 - fh/2 + fontyoffset.getValue(), Alignment.LEFT, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor1.getColor(), null, "" );
        Penalties = new IntValue[numveh];
        for(int i=0;i < numveh;i++)
        { 
            Penalties[i] = new IntValue();
            Penalties[i].update(0);
            Penalties[i].setUnchanged();
        }
        DRS_enabled.update( false );
        
    }
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        
        
        super.updateVisibility(gameData, isEditorMode);
        int numveh = gameData.getScoringInfo().getNumVehicles();
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        SCState.update(scoringInfo.getYellowFlagState());
        
        if(scoringInfo.getYellowFlagState() == YellowFlagState.LAST_LAP)
            LastSC = scoringInfo.getLeadersVehicleScoringInfo().getLapsCompleted() + 1;
        
        if(scoringInfo.getSessionNanos() < visibleEnd)
            return true;
        
        if(scoringInfo.getViewedVehicleScoringInfo().getLapsCompleted() < 2 + LastSC)
            DRS_enabled.update( false );
        else
            DRS_enabled.update( true );
        
        if(SCState.hasChanged() && (SCState.getValue() == YellowFlagState.PENDING || SCState.getValue() == YellowFlagState.LAST_LAP))
        {
            widgetpart = 1;
            visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
            return true;
        }
        
        if(DRS_enabled.hasChanged() && ShowDRS.getValue())// && scoringInfo.getLeadersVehicleScoringInfo().getLapsCompleted() > 1
        {
            widgetpart = 2;
            visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
            return true;
        }
        
        if(scoringInfo.getSessionType().isRace())
        {
            
            int total=0;
            for(int j=0;j < numveh;j++)
            {
                total += scoringInfo.getVehicleScoringInfo( j ).getNumOutstandingPenalties();
            }
            Pentotal.update( total );
           
            if(Pentotal.getValue() > Pentotal.getOldValue() && Pentotal.hasChanged() && Pentotal.getValue() > 0)
            {
               widgetpart = 0;
               visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
               return true;
            }
            else
                Pentotal.hasChanged();
        }
        
        return false;   
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        texture.clear( imgTitle.getTexture(), offsetX, offsetY, false, null );
        texture.clear( imgTime.getTexture(), offsetX, offsetY + height/2, false, null );
     
    }
    
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        int numveh = gameData.getScoringInfo().getNumVehicles();
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        if(widgetpart == 1)
        {
            if ( needsCompleteRedraw || SCState.getValue() == YellowFlagState.PENDING)
                dsMessage.draw( offsetX, offsetY, "Safety Car Deployed", texture );
            else
                if ( needsCompleteRedraw || SCState.getValue() == YellowFlagState.LAST_LAP)
                    dsMessage.draw( offsetX, offsetY, "Safety Car In This Lap", texture );
               
        }
        else
        {
            for(int i=0;i < numveh;i++)
            {
               Penalties[i].update( scoringInfo.getVehicleScoringInfo( i ).getNumOutstandingPenalties() );
                            
               if(Penalties[i].hasChanged() && Penalties[i].getValue() > 0 )
                   flaggeddriver = i;
            }
            VehicleScoringInfo vsi = gameData.getScoringInfo().getVehicleScoringInfo( flaggeddriver );
            
            dsMessage.draw( offsetX, offsetY, "DRIVE THROUGH PENALTY FOR" + " " + PrunnWidgetSetPorscheSupercup.ShortName(vsi.getDriverName().toUpperCase()), texture );
            
        }
        
        dsRC.draw( offsetX, offsetY, "Race Control", texture );
                    
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        writer.writeProperty( f1_2011Font, "" );
        writer.writeProperty( fontColor1, "" );
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty(visibleTime, "");
        writer.writeProperty( fontyoffset, "" );
        writer.writeProperty( ShowDRS, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        if ( loader.loadProperty( f1_2011Font ) );
        else if ( loader.loadProperty( fontColor1 ) );
        else if ( loader.loadProperty( fontColor2 ) );
        else if( loader.loadProperty(visibleTime));
        else if ( loader.loadProperty( fontyoffset ) );
        else if ( loader.loadProperty( ShowDRS ) );
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Colors" );
        propsCont.addProperty( f1_2011Font );
        propsCont.addProperty( fontColor1 );
        propsCont.addProperty( fontColor2 );
        propsCont.addProperty(visibleTime);
        propsCont.addProperty( fontyoffset );
        propsCont.addProperty( ShowDRS );
        
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
    
    public RaceControlWidget()
    {
        super( PrunnWidgetSetPorscheSupercup.INSTANCE, PrunnWidgetSetPorscheSupercup.WIDGET_PACKAGE_PorscheSupercup_Race, 72.0f, 6.7f );
        visibleTime = new DelayProperty("visibleTime", net.ctdp.rfdynhud.properties.DelayProperty.DisplayUnits.SECONDS, 6);
        visibleEnd = 0;
        Penalties = null;
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetPorscheSupercup.GP2_FONT_NAME );
    }
    
}
