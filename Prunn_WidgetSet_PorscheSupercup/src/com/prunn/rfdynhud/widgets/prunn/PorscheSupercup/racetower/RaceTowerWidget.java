package com.prunn.rfdynhud.widgets.prunn.PorscheSupercup.racetower;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import net.ctdp.rfdynhud.gamedata.GamePhase;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.properties.ColorProperty;
import net.ctdp.rfdynhud.properties.DelayProperty;
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
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetPorscheSupercup;

/**
 * @author Prunn
 * copyright@Prunn2011
 * 
 */


public class RaceTowerWidget extends Widget
{
    private TextureImage2D texBG = null;
    private TextureImage2D texPos = null;
    private TextureImage2D texGapFirst = null;
    private final ImagePropertyWithTexture imgPos = new ImagePropertyWithTexture( "imgPos", "prunn/PorscheSupercup/pos.png" );
    private final ImagePropertyWithTexture imgPos5 = new ImagePropertyWithTexture( "imgPos", "prunn/PorscheSupercup/pos5right.png" );
    private final ImagePropertyWithTexture imgPosFirst = new ImagePropertyWithTexture( "imgPos", "prunn/PorscheSupercup/pos1.png" );
    private final ImagePropertyWithTexture imgBG = new ImagePropertyWithTexture( "imgPos", "prunn/PorscheSupercup/data_neutral.png" );
    private final ImagePropertyWithTexture imgBGFirst = new ImagePropertyWithTexture( "imgPos", "prunn/PorscheSupercup/data_title.png" );
    private final ImagePropertyWithTexture imgGapFirst = new ImagePropertyWithTexture( "imgTime", "prunn/PorscheSupercup/data_first.png" );
    private final ColorProperty fontColor1 = new ColorProperty("fontColor1", PrunnWidgetSetPorscheSupercup.FONT_COLOR1_NAME);
    private final ColorProperty fontColor2 = new ColorProperty( "fontColor2", PrunnWidgetSetPorscheSupercup.FONT_COLOR2_NAME );
    private final DelayProperty visibleTime = new DelayProperty( "visibleTime", DelayProperty.DisplayUnits.SECONDS, 12 );
    private long visibleEnd = 0;
    private DrawnString[] dsPos = null;
    private DrawnString[] dsName = null;
    private DrawnString[] dsTime = null;
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    private IntProperty fontxposoffset = new IntProperty("X Position Font Offset", 0);
    private IntProperty fontxnameoffset = new IntProperty("X Name Font Offset", 0);
    private IntProperty fontxtimeoffset = new IntProperty("X Time Font Offset", 0);
    private IntProperty randMulti = new IntProperty("Show Multiplier", 0);
    private final IntValue currentLap = new IntValue();
    private short shownData = 0; //0-2-4-gaps 1-place gained
    private final IntValue drawnCars = new IntValue();
    private final IntValue carsOnLeadLap = new IntValue();
    private short[] positions = null;
    private String[] names = null;
    private String[] gaps = null;
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
        drawnCars.reset();
        visibleEnd = 0;
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
        int maxNumItems = 8;
        dsPos = new DrawnString[maxNumItems];
        dsName = new DrawnString[maxNumItems];
        dsTime = new DrawnString[maxNumItems];
                
        int fh = TextureImage2D.getStringHeight( "0%C", getFontProperty() );
        int rowHeight = height / maxNumItems*2;
        
        imgBG.updateSize( width*55/100, rowHeight*95/100, isEditorMode );
        imgBGFirst.updateSize( width*55/100, rowHeight*95/100, isEditorMode );
        texBG = imgBG.getImage().getScaledTextureImage( width*55/100, rowHeight*95/100, texBG, isEditorMode );
        texGapFirst = imgGapFirst.getImage().getScaledTextureImage( width*15/100, rowHeight*95/100, texGapFirst, isEditorMode );
        
        Color whiteFontColor = fontColor2.getColor();
        
        int top = ( rowHeight - fh ) / 2;
        
        for(int i=0;i < maxNumItems;i++)
        { 
            if ( i == 4 )
                top -= rowHeight * 4;
            
            if(i%8 < 4)
            {
                dsPos[i] = drawnStringFactory.newDrawnString( "dsPos", width*4/100 + fontxposoffset.getValue(), top + fontyoffset.getValue(), Alignment.CENTER, false, getFont(), isFontAntiAliased(), whiteFontColor );
                dsName[i] = drawnStringFactory.newDrawnString( "dsName", width*9/100 + fontxnameoffset.getValue(), top + fontyoffset.getValue(), Alignment.LEFT, false, getFont(), isFontAntiAliased(), fontColor1.getColor() );
                dsTime[i] = drawnStringFactory.newDrawnString( "dsTime", width*45/100 + fontxtimeoffset.getValue(), top + fontyoffset.getValue(), Alignment.RIGHT, false, getFont(), isFontAntiAliased(), fontColor1.getColor() );
                
            }
            else
            {
                dsPos[i] = drawnStringFactory.newDrawnString( "dsPos", width*51/100 + fontxposoffset.getValue(), top + fontyoffset.getValue(), Alignment.CENTER, false, getFont(), isFontAntiAliased(), whiteFontColor );
                dsName[i] = drawnStringFactory.newDrawnString( "dsName", width*56/100 + fontxnameoffset.getValue(), top + fontyoffset.getValue(), Alignment.LEFT, false, getFont(), isFontAntiAliased(), fontColor1.getColor() );
                dsTime[i] = drawnStringFactory.newDrawnString( "dsTime", width*98/100 + fontxtimeoffset.getValue(), top + fontyoffset.getValue(), Alignment.RIGHT, false, getFont(), isFontAntiAliased(), fontColor1.getColor() );
                
            }
            top += rowHeight;
        }
        
        
    }
    private void clearArrayValues(int maxNumCars)
    {
        positions = new short[maxNumCars];
        gaps = new String[maxNumCars];
        names = new String[maxNumCars];
        
        for(int i=0;i<maxNumCars;i++)
        {
            positions[i] = -1;
            gaps[i] = "";
            names[i] = "";
        }
    }
    private void FillArrayValues(int onLeaderLap, ScoringInfo scoringInfo, int data, boolean isEditorMode, LiveGameData gameData)
    {
        if(isEditorMode)
        {
            data = 0;
            onLeaderLap = 8;
        }
        
        for(int i=0;i<onLeaderLap;i++)
        {
            
            if(positions[i] == -1)
            {
                
            
                VehicleScoringInfo vsi = scoringInfo.getVehicleScoringInfo( i );
                positions[i] = vsi.getPlace( false );
                names[i] = PrunnWidgetSetPorscheSupercup.ShortName( vsi.getDriverName() );
                if(data==0)
                {
                    if(i==0)
                        gaps[i] = "Lap " + String.valueOf( vsi.getLapsCompleted());
                    else
                        gaps[i] = TimingUtil.getTimeAsGapString( vsi.getTimeBehindLeader( false ));
                }
             }
        }
    }
    
    
    @Override
    protected Boolean updateVisibility( LiveGameData gameData, boolean isEditorMode )
    {
        super.updateVisibility( gameData, isEditorMode );
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        currentLap.update( scoringInfo.getLeadersVehicleScoringInfo().getLapsCompleted() );
        
        if( currentLap.hasChanged() && currentLap.getValue() > 0 && (short)( Math.random() * randMulti.getValue()) == 0 || isEditorMode)
        {
            
            //fetch what data is shown others-gaps 1-places gained/lost
            if(scoringInfo.getLeadersVehicleScoringInfo().getFinishStatus().isFinished() || isEditorMode)
                shownData = 0 ;
            else
                shownData = (short)( Math.random() * 2 );
            
            if(scoringInfo.getGamePhase() == GamePhase.SESSION_OVER)
                visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos()*3;
            else
                visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
            
            clearArrayValues(scoringInfo.getNumVehicles());
            FillArrayValues( 1, scoringInfo, shownData, isEditorMode, gameData);
            if(!isEditorMode)
                forceCompleteRedraw( true );
            isvisible = true;
            return true;
            
        }
        
        if(scoringInfo.getSessionNanos() < visibleEnd || isEditorMode)
        {
            //how many on the same lap?
            int onlap = 0;
            for(int j=0;j < scoringInfo.getNumVehicles(); j++)
            {
                if(scoringInfo.getVehicleScoringInfo( j ).getLapsCompleted() == scoringInfo.getLeadersVehicleScoringInfo().getLapsCompleted() )
                    onlap++;
            }
                
            carsOnLeadLap.update( onlap );
            if (carsOnLeadLap.hasChanged() && !isEditorMode )
            {
                FillArrayValues( onlap, scoringInfo, shownData, false, gameData);
                forceCompleteRedraw( true );
            }
            isvisible = true;
            return true;
        }
        
        isvisible = false;
        return false;
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        int maxNumItems = 8;
        int rowHeight = height / maxNumItems*2;
        int drawncars = Math.min( scoringInfo.getNumVehicles(), maxNumItems );
        if(isEditorMode)
            shownData = 0;
        
        for(int i=0;i < drawncars;i++)
        {
            if(positions[i] != -1 || isEditorMode)
            {
                if(i%8 < 4)
                {
                    if(i==0)
                    {
                        texture.clear( imgBGFirst.getTexture(), offsetX, offsetY+rowHeight*i, false, null );
                        texPos = imgPosFirst.getImage().getScaledTextureImage( width*8/100, rowHeight*95/100, texPos, isEditorMode );
                        if(shownData == 0)
                            texture.drawImage( texGapFirst, offsetX + width*32/100, offsetY, true, null );
                        
                    }
                    else
                    {
                        texture.clear( imgBG.getTexture(), offsetX, offsetY+rowHeight*i, false, null );
                        texPos = imgPos.getImage().getScaledTextureImage( width*8/100, rowHeight*95/100, texPos, isEditorMode );
                    }
                    texture.drawImage( texPos, offsetX + width*1/200, offsetY+rowHeight*i, true, null );
                }
                else
                {
                    texture.drawImage( texBG, offsetX+ width*45/100, offsetY+rowHeight*(i-4), true, null );
                    if(i==4)
                    {
                        texture.clear( imgBGFirst.getTexture(), offsetX, offsetY, false, null );
                        texPos = imgPosFirst.getImage().getScaledTextureImage( width*8/100, rowHeight*95/100, texPos, isEditorMode );
                        if(shownData == 0)
                            texture.drawImage( texGapFirst, offsetX + width*32/100, offsetY, true, null );
                        texture.drawImage( texPos, offsetX + width*1/200, offsetY, true, null );
                        texPos = imgPos5.getImage().getScaledTextureImage( width*8/100, rowHeight*95/100, texPos, isEditorMode );
                        texture.drawImage( texPos, offsetX + width*95/200, offsetY+rowHeight*(i-4), true, null );
                        
                    }
                    else
                    {
                        texPos = imgPos.getImage().getScaledTextureImage( width*8/100, rowHeight*95/100, texPos, isEditorMode );
                        texture.drawImage( texPos, offsetX + width*95/200, offsetY+rowHeight*(i-4), true, null );
                    }
                }
            }
        }
        
    }
    
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        if ( needsCompleteRedraw)
        {
            int drawncars = Math.min( scoringInfo.getNumVehicles(), 8 );
            
            
            for(int i=0;i < drawncars;i++)
            { 
                if(positions[i] != -1)
                    dsPos[i].draw( offsetX, offsetY, String.valueOf(positions[i]),(positions[i] == 1) ? fontColor1.getColor() : fontColor2.getColor(), texture );
                
                dsName[i].draw( offsetX, offsetY, names[i],(positions[i] == 1) ? fontColor2.getColor() : fontColor1.getColor(), texture );
                dsTime[i].draw( offsetX, offsetY, gaps[i], texture );
            }
        }
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty( fontColor1, "" );
        writer.writeProperty( visibleTime, "visibleTime" );
        writer.writeProperty( randMulti, "ShowMultiplier" );
        writer.writeProperty( fontyoffset, "" );
        writer.writeProperty( fontxposoffset, "" );
        writer.writeProperty( fontxnameoffset, "" );
        writer.writeProperty( fontxtimeoffset, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        
        if ( loader.loadProperty( fontColor2 ) );
        else if ( loader.loadProperty( fontColor1 ) );
        else if ( loader.loadProperty( visibleTime ) );
        else if ( loader.loadProperty( randMulti ) );
        else if ( loader.loadProperty( fontyoffset ) );
        else if ( loader.loadProperty( fontxposoffset ) );
        else if ( loader.loadProperty( fontxnameoffset ) );
        else if ( loader.loadProperty( fontxtimeoffset ) );
    }
    
    @Override
    protected void addFontPropertiesToContainer( PropertiesContainer propsCont, boolean forceAll )
    {
        propsCont.addGroup( "Colors and Fonts" );
        
        super.addFontPropertiesToContainer( propsCont, forceAll );
        propsCont.addProperty( fontColor1 );
        
        propsCont.addProperty( fontColor2 );
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Specific" );
        propsCont.addProperty( visibleTime );
        propsCont.addProperty( randMulti );
        propsCont.addGroup( "Font Displacement" );
        propsCont.addProperty( fontyoffset );
        propsCont.addProperty( fontxposoffset );
        propsCont.addProperty( fontxnameoffset );
        propsCont.addProperty( fontxtimeoffset );
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
    
    public RaceTowerWidget()
    {
        super( PrunnWidgetSetPorscheSupercup.INSTANCE, PrunnWidgetSetPorscheSupercup.WIDGET_PACKAGE_PorscheSupercup_Race, 20.0f, 32.5f );
        
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetPorscheSupercup.GP2_FONT_NAME );
        getFontColorProperty().setColor( PrunnWidgetSetPorscheSupercup.FONT_COLOR1_NAME );
    }
}
