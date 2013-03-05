package com.prunn.rfdynhud.widgets.prunn.PorscheSupercup.qualiftower;

import java.awt.Font;
import java.io.IOException;

import com.prunn.rfdynhud.plugins.tlcgenerator.StandardTLCGenerator;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.input.InputAction;
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
import net.ctdp.rfdynhud.values.StringValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetPorscheSupercup;

/**
 * @author Prunn
 * copyright@Prunn2011
 * 
 */


public class QualifTowerWidget extends Widget
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
    private final IntValue drawnCars = new IntValue();
    private final IntValue carsHasALap = new IntValue();
    private short[] positions = null;
    private String[] names = null;
    private StringValue[] gaps = null;
    StandardTLCGenerator gen = new StandardTLCGenerator();
    private static final InputAction showWidget = new InputAction( "Show Widget", true );
    public static Boolean isvisible = false;
    public static Boolean visible()
    {
        return isvisible;
    }
   
    @Override
    public void onRealtimeEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onRealtimeEntered( gameData, isEditorMode );
        String cpid = "Y29weXJpZ2h0QFBydW5uMjAxMQ";
        if(!isEditorMode)
            log(cpid);
        drawnCars.reset();
        visibleEnd = 0;
    }
    @Override
    public InputAction[] getInputActions()
    {
        return ( new InputAction[] { showWidget } );
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Boolean onBoundInputStateChanged( InputAction action, boolean state, int modifierMask, long when, LiveGameData gameData, boolean isEditorMode )
    {
        Boolean result = super.onBoundInputStateChanged( action, state, modifierMask, when, gameData, isEditorMode );
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        if ( action == showWidget )
            visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
         
        return ( result );
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
        dsPos = new DrawnString[8];
        dsName = new DrawnString[8];
        dsTime = new DrawnString[8];
                
        int fh = TextureImage2D.getStringHeight( "0%C", getFontProperty() );
        int rowHeight = height / maxNumItems*2;
        
        imgBG.updateSize( width*55/100, rowHeight*95/100, isEditorMode );
        imgBGFirst.updateSize( width*55/100, rowHeight*95/100, isEditorMode );
        texBG = imgBG.getImage().getScaledTextureImage( width*55/100, rowHeight*95/100, texBG, isEditorMode );
        texGapFirst = imgGapFirst.getImage().getScaledTextureImage( width*15/100, rowHeight*95/100, texGapFirst, isEditorMode );
        
        int top = ( rowHeight - fh ) / 2;
        
        for(int i=0;i < maxNumItems;i++)
        { 
            if ( i == 4 )
                top -= rowHeight * 4;
            
            if(i%8 < 4)
            {
                dsPos[i] = drawnStringFactory.newDrawnString( "dsPos", width*4/100 + fontxposoffset.getValue(), top + fontyoffset.getValue(), Alignment.CENTER, false, getFont(), isFontAntiAliased(), fontColor2.getColor() );
                dsName[i] = drawnStringFactory.newDrawnString( "dsName", width*9/100 + fontxnameoffset.getValue(), top + fontyoffset.getValue(), Alignment.LEFT, false, getFont(), isFontAntiAliased(), fontColor1.getColor() );
                dsTime[i] = drawnStringFactory.newDrawnString( "dsTime", width*45/100 + fontxtimeoffset.getValue(), top + fontyoffset.getValue(), Alignment.RIGHT, false, getFont(), isFontAntiAliased(), fontColor1.getColor() );
                
            }
            else
            {
                dsPos[i] = drawnStringFactory.newDrawnString( "dsPos", width*51/100 + fontxposoffset.getValue(), top + fontyoffset.getValue(), Alignment.CENTER, false, getFont(), isFontAntiAliased(), fontColor2.getColor() );
                dsName[i] = drawnStringFactory.newDrawnString( "dsName", width*56/100 + fontxnameoffset.getValue(), top + fontyoffset.getValue(), Alignment.LEFT, false, getFont(), isFontAntiAliased(), fontColor1.getColor() );
                dsTime[i] = drawnStringFactory.newDrawnString( "dsTime", width*98/100 + fontxtimeoffset.getValue(), top + fontyoffset.getValue(), Alignment.RIGHT, false, getFont(), isFontAntiAliased(), fontColor1.getColor() );
                
            }
            top += rowHeight;
        }
        
        
    }
    
    private void FillArrayValues(int onLeaderLap, ScoringInfo scoringInfo)
    {
        if(positions == null || gaps == null || names == null)
        {
            positions = new short[8];
            gaps = new StringValue[8];
            names = new String[8];
            for(int i=0;i<8;i++)
                gaps[i] = new StringValue();
            
        }
        
        float leaderTime = scoringInfo.getLeadersVehicleScoringInfo().getFastestLaptime().getLapTime();
            
        for(int i=0;i<onLeaderLap;i++)
        {
            VehicleScoringInfo vsi = scoringInfo.getVehicleScoringInfo( i );
            positions[i] = vsi.getPlace( false );
            names[i] = gen.ShortName( vsi.getDriverName() );
            if(i==0)
                gaps[i].update(TimingUtil.getTimeAsLaptimeString(leaderTime));
            else
                gaps[i].update(TimingUtil.getTimeAsGapString( vsi.getFastestLaptime().getLapTime() - leaderTime));
        }
    }
    
    
    @Override
    protected Boolean updateVisibility( LiveGameData gameData, boolean isEditorMode )
    {
        super.updateVisibility( gameData, isEditorMode );
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        if(scoringInfo.getSessionNanos() < visibleEnd || isEditorMode)
        {
            int onlap = 0;
            for(int j=0;j < Math.min( scoringInfo.getNumVehicles(), 8 ); j++)
            {
                if(scoringInfo.getVehicleScoringInfo( j ).getFastestLaptime() != null && scoringInfo.getVehicleScoringInfo( j ).getFastestLaptime().getLapTime() > 0)
                    onlap++;
            }
                
            carsHasALap.update( Math.min( onlap, 8 ) );
                
            if (carsHasALap.hasChanged() && !isEditorMode)
                forceCompleteRedraw( true );
            FillArrayValues( carsHasALap.getValue(), scoringInfo);
            
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
        
        int maxNumItems = 8;
        int rowHeight = height / maxNumItems*2;
        int drawncars = Math.min( carsHasALap.getValue(), maxNumItems );
        
        for(int i=0;i < drawncars;i++)
        {
            if(positions[i] > 0 || isEditorMode)
            {
                if(i%8 < 4)
                {
                    if(i==0)
                    {
                        texture.clear( imgBGFirst.getTexture(), offsetX, offsetY+rowHeight*i, false, null );
                        texPos = imgPosFirst.getImage().getScaledTextureImage( width*8/100, rowHeight*95/100, texPos, isEditorMode );
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
        
        int drawncars = Math.min( carsHasALap.getValue(), 8 );
        
        for(int i=0;i < drawncars;i++)
        { 
            if(positions[i] > 0)
            {
                if(( needsCompleteRedraw || clock.c()) || gaps[i].hasChanged())
                {
                    dsPos[i].draw( offsetX, offsetY, String.valueOf(positions[i]),(positions[i] == 1) ? fontColor1.getColor() : fontColor2.getColor(), texture );
                    dsName[i].draw( offsetX, offsetY, names[i],(positions[i] == 1) ? fontColor2.getColor() : fontColor1.getColor(), texture );
                    dsTime[i].draw( offsetX, offsetY, gaps[i].getValue(), texture );
                }
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
    
    public QualifTowerWidget()
    {
        super( PrunnWidgetSetPorscheSupercup.INSTANCE, PrunnWidgetSetPorscheSupercup.WIDGET_PACKAGE_PorscheSupercup, 20.0f, 32.5f );
        
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetPorscheSupercup.GP2_FONT_NAME );
        getFontColorProperty().setColor( PrunnWidgetSetPorscheSupercup.FONT_COLOR1_NAME );
    }
}
