package com.prunn.rfdynhud.widgets.prunn.PorscheSupercup.racegap;

import java.awt.Font;
import java.io.IOException;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetPorscheSupercup;
import com.prunn.rfdynhud.widgets.prunn.PorscheSupercup.raceinfos.RaceInfosWidget;
import com.prunn.rfdynhud.widgets.prunn.PorscheSupercup.racetower.RaceTowerWidget;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
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
import net.ctdp.rfdynhud.util.TimingUtil;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

/**
 * @author Prunn
 * copyright@Prunn2011
 * 
 */


public class RaceGapWidget extends Widget
{
    private DrawnString dsPos = null;
    private DrawnString dsPos2 = null;
    private DrawnString dsName = null;
    private DrawnString dsName2 = null;
    private DrawnString dsTime = null;
    
    private TextureImage2D texTime = null;
    private TextureImage2D texPos = null;
    private TextureImage2D texDataFirst = null;
    private TextureImage2D texGapFirst = null;
    private TextureImage2D texGap = null;
    private final ImagePropertyWithTexture imgPos = new ImagePropertyWithTexture( "imgPos", "prunn/PorscheSupercup/pos.png" );
    private final ImagePropertyWithTexture imgPosFirst = new ImagePropertyWithTexture( "imgPos", "prunn/PorscheSupercup/pos1.png" );
    private final ImagePropertyWithTexture imgData = new ImagePropertyWithTexture( "imgPos", "prunn/PorscheSupercup/data_neutral.png" );
    private final ImagePropertyWithTexture imgDataFirst = new ImagePropertyWithTexture( "imgPos", "prunn/PorscheSupercup/data_title.png" );
    private final ImagePropertyWithTexture imgTime = new ImagePropertyWithTexture( "imgTime", "prunn/PorscheSupercup/data_neutral.png" );
    private final ImagePropertyWithTexture imgGapFirst = new ImagePropertyWithTexture( "imgTime", "prunn/PorscheSupercup/data_first.png" );
    private final ImagePropertyWithTexture imgGap = new ImagePropertyWithTexture( "imgTime", "prunn/PorscheSupercup/data_gap.png" );

    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    protected final IntProperty frequency = new IntProperty("appearence frequency", "frequency", 3);
    protected final FontProperty PSCFont = new FontProperty("Main Font", PrunnWidgetSetPorscheSupercup.GP2_FONT_NAME);
    protected final ColorProperty fontColor1 = new ColorProperty("fontColor1", PrunnWidgetSetPorscheSupercup.FONT_COLOR1_NAME);
    protected final ColorProperty fontColor2 = new ColorProperty("fontColor2", PrunnWidgetSetPorscheSupercup.FONT_COLOR2_NAME);
    
    private final DelayProperty visibleTime;
    private long visibleEnd;
    private final IntValue CurrentSector = new IntValue();
    private String  name, name2;
    private int place, place2;
    private String gap;
    //private IntValue cpos = new IntValue();
    
    public static Boolean isvisible = false;
    public static Boolean visible()
    {
        return isvisible;
    }
    
    
    @Override
    public void onCockpitEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onRealtimeEntered( gameData, isEditorMode );
        String cpid = "Y29weXJpZ2h0QFBydW5uMjAxMQ";
        if(!isEditorMode)
            log(cpid);
    }
    private static final String getTimeAsGapString2( float gap )
    {
        return ( "+ " + TimingUtil.getTimeAsLaptimeString( gap ) );
    }
    private void fillvsis(ScoringInfo scoringInfo)
    {
        VehicleScoringInfo viewedvsi = scoringInfo.getViewedVehicleScoringInfo();
        VehicleScoringInfo vsi1;
        VehicleScoringInfo vsi2;
        float GapFront = 1000.000f;
        float GapBehind = 0.000f;
        int LapFront = 1000;
        int LapBehind = 0;
        
        if(viewedvsi.getNextInFront( false ) != null)
        {
            GapFront = Math.abs(viewedvsi.getTimeBehindNextInFront( false ));
            LapFront = viewedvsi.getLapsBehindNextInFront( false );
        }
        if(viewedvsi.getNextBehind( false ) != null)
        {
            GapBehind = Math.abs( viewedvsi.getNextBehind( false ).getTimeBehindNextInFront( false ));
            LapBehind = viewedvsi.getNextBehind( false ).getLapsBehindNextInFront( false );
        }
        
        if(viewedvsi.getNextBehind( false ) == null || GapFront < GapBehind || LapFront < LapBehind)
        {
            vsi1 = viewedvsi.getNextInFront( false );
            vsi2 = viewedvsi;
            if(LapFront == 0)
                gap = getTimeAsGapString2(GapFront);
            else
            {
                String laps = ( LapFront > 1 ) ? " Laps" : " Lap";
                gap = "+ " + LapFront + laps;
            }
        }
        else
        {
            vsi1 = viewedvsi;
            vsi2 = viewedvsi.getNextBehind( false );
            if(LapBehind == 0)
                gap = getTimeAsGapString2(GapBehind);
            else
            {
                String laps = ( LapBehind > 1 ) ? " Laps" : " Lap";
                gap = "+ " + LapBehind + laps;
            }
        }
        
        place = vsi1.getPlace(false);
        name = PrunnWidgetSetPorscheSupercup.ShortName( vsi1.getDriverNameShort());
        place2 = vsi2.getPlace(false);
        name2 = PrunnWidgetSetPorscheSupercup.ShortName( vsi2.getDriverNameShort());
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
        
        imgData.updateSize( width*56/100, height, isEditorMode );
        imgDataFirst.updateSize(width*56/100, height, isEditorMode );
        imgTime.updateSize( width - imgData.getTexture().getWidth()*2 + width*4/100, height, isEditorMode );
        texTime = imgTime.getImage().getScaledTextureImage( width*10/100, height, texTime, isEditorMode );
        texDataFirst = imgDataFirst.getImage().getScaledTextureImage( width*56/100, height, texDataFirst, isEditorMode );
        texGapFirst = imgGapFirst.getImage().getScaledTextureImage( width*18/100, height, texGapFirst, isEditorMode );
        texGap = imgGap.getImage().getScaledTextureImage( width*18/100, height, texGap, isEditorMode );
        
        int fh = TextureImage2D.getStringHeight( "0yI", PSCFont );
        int top1 = ( height - fh ) / 2 + fontyoffset.getValue();
        
        dsPos = drawnStringFactory.newDrawnString( "dsPos", width*4/100, top1, Alignment.CENTER, false, PSCFont.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        dsName = drawnStringFactory.newDrawnString( "dsName", width*10/100, top1, Alignment.LEFT, false, PSCFont.getFont(), isFontAntiAliased(), fontColor1.getColor(), null, "" );
        dsTime = drawnStringFactory.newDrawnString( "dsTime", width/2, top1, Alignment.CENTER, false, PSCFont.getFont(), isFontAntiAliased(), fontColor1.getColor(), null, "" );
        dsPos2 = drawnStringFactory.newDrawnString( "dsPos2", width*96/100, top1, Alignment.CENTER, false, PSCFont.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        dsName2 = drawnStringFactory.newDrawnString( "dsName2", width*90/100, top1, Alignment.RIGHT, false, PSCFont.getFont(), isFontAntiAliased(), fontColor1.getColor(), null, "" );
        
    }
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        super.updateVisibility(gameData, isEditorMode);
        ScoringInfo scoringInfo = gameData.getScoringInfo();
         
        CurrentSector.update(scoringInfo.getViewedVehicleScoringInfo().getSector());
        
        if(isEditorMode)
        {
            fillvsis(scoringInfo);
            return true;
        }
        
        if(RaceTowerWidget.visible() || RaceInfosWidget.visible() || scoringInfo.getLeadersVehicleScoringInfo().getLapsCompleted() < 1 || scoringInfo.getViewedVehicleScoringInfo().getFinishStatus().isFinished())
        {
            isvisible = false;
            return false;
        }
        
        if(scoringInfo.getSessionNanos() < visibleEnd)
        {
            isvisible = true;
            return true;
        }
            
        
        if( CurrentSector.hasChanged() && scoringInfo.getNumVehicles() > 1)
        {
            if( (int)(Math.random()*frequency.getValue()) == 0 )
            {
                //cpos.update(scoringInfo.getViewedVehicleScoringInfo().getPlace( false ));
                if(!isEditorMode)
                    forceCompleteRedraw( true );
                fillvsis(scoringInfo);
                visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
                isvisible = true;
                return true;
            }
        }
        isvisible = false;
        return false;
    		
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        
        
        texture.clear( imgData.getTexture(), offsetX + width - imgData.getTexture().getWidth(), offsetY , false, null );
        texPos = imgPos.getImage().getScaledTextureImage( width*8/100, height, texPos, isEditorMode );
        texture.drawImage( texPos, offsetX + width*184/200, offsetY, true, null );
        
        if(place == 1)
        {
            texture.drawImage( texDataFirst, offsetX, offsetY, true, null );
            //texture.clear( imgDataFirst.getTexture(), offsetX, offsetY, false, null );
            texPos = imgPosFirst.getImage().getScaledTextureImage( width*8/100, height, texPos, isEditorMode );
            texture.drawImage( texGapFirst, offsetX + width/2 - texGapFirst.getWidth()/2, offsetY, true, null );
        }
        else
        {
            texture.clear( imgData.getTexture(), offsetX, offsetY, false, null );
            texPos = imgPos.getImage().getScaledTextureImage( width*8/100, height, texPos, isEditorMode );texture.drawImage( texGapFirst, offsetX + width/2 - texGapFirst.getWidth()/2, offsetY, true, null );
            texture.drawImage( texGap, offsetX + width/2 - texGap.getWidth()/2, offsetY, true, null );
            
        }
        texture.drawImage( texPos, offsetX + width*1/200, offsetY, true, null );
        
        //texture.drawImage( texTime, offsetX + width*45/100, offsetY, true, null );
        
        
        
    }
    
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        if ( needsCompleteRedraw  )
        {
            dsPos.draw( offsetX, offsetY, Integer.toString( place ),(place==1) ? fontColor1.getColor() : fontColor2.getColor() ,texture );
            dsName.draw( offsetX, offsetY, name,(place==1) ? fontColor2.getColor() : fontColor1.getColor(), texture );
            dsTime.draw( offsetX, offsetY, gap , texture);
        	dsPos2.draw( offsetX, offsetY, Integer.toString( place2 ), texture );
        	dsName2.draw( offsetX, offsetY, name2, texture );
        }
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        writer.writeProperty( PSCFont, "" );
        writer.writeProperty( fontColor1, "" );
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty(visibleTime, "");
        writer.writeProperty(frequency, "");
        writer.writeProperty( fontyoffset, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        if ( loader.loadProperty( PSCFont ) );
        else if ( loader.loadProperty( fontColor1 ) );
        else if ( loader.loadProperty( fontColor2 ) );
        else if( loader.loadProperty(visibleTime));
        else if( loader.loadProperty(frequency));
        else if ( loader.loadProperty( fontyoffset ) );
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Colors" );
        propsCont.addProperty( PSCFont );
        propsCont.addProperty( fontColor1 );
        propsCont.addProperty( fontColor2 );
        propsCont.addProperty(visibleTime);
        propsCont.addProperty(frequency);
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
    
    public RaceGapWidget()
    {
        super( PrunnWidgetSetPorscheSupercup.INSTANCE, PrunnWidgetSetPorscheSupercup.WIDGET_PACKAGE_PorscheSupercup_Race, 51.0f, 4.0f );
        visibleTime = new DelayProperty("visibleTime", net.ctdp.rfdynhud.properties.DelayProperty.DisplayUnits.SECONDS, 15);
        visibleEnd = 0;
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetPorscheSupercup.GP2_FONT_NAME );
        
    }
    
}
