package com.prunn.rfdynhud.widgets.prunn.PorscheSupercup.persosectors;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetPorscheSupercup;

import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.properties.ColorProperty;
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
import net.ctdp.rfdynhud.values.BoolValue;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.values.StringValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

/**
 * @author Prunn
 * copyright@Prunn2011
 * 
 */


public class PersonalSectorsWidget extends Widget
{
    private DrawnString dsGap1 = null;
    private DrawnString dsGap2 = null;
    private DrawnString dsGap3 = null;
    private DrawnString dsLap = null;
    
    private final ImagePropertyWithTexture imgSector = new ImagePropertyWithTexture( "imgSector", "prunn/PorscheSupercup/data_neutral.png" );
    private final ImagePropertyWithTexture imgPersonalBest = new ImagePropertyWithTexture( "imgPersonalBest", "prunn/PorscheSupercup/data_title.png" );
    private final ImagePropertyWithTexture imgFast = new ImagePropertyWithTexture( "imgFast", "prunn/PorscheSupercup/data_faster.png" );
    private final ImagePropertyWithTexture imgSlow = new ImagePropertyWithTexture( "imgSlow", "prunn/PorscheSupercup/data_slower.png" );
    private final ImagePropertyWithTexture imgLap = new ImagePropertyWithTexture( "imgSector", "prunn/PorscheSupercup/data_neutral.png" );
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    
    protected final FontProperty f1_2011Font = new FontProperty("Main Font", PrunnWidgetSetPorscheSupercup.GP2_FONT_NAME);
    private final ColorProperty fontColor1 = new ColorProperty( "fontColor1", PrunnWidgetSetPorscheSupercup.FONT_COLOR1_NAME );
    private final ColorProperty fontColor2 = new ColorProperty("fontColor2", PrunnWidgetSetPorscheSupercup.FONT_COLOR2_NAME);
    private ColorProperty Gap1FontColor;
    private ColorProperty Gap2FontColor;
    private ColorProperty Gap3FontColor;
    private ColorProperty LapFontColor;
    private BoolValue ClearGaps = new BoolValue();
    private final IntValue currentSector = new IntValue();
    private IntValue cveh = new IntValue();
    private float oldBestS1 = 0;
    private float oldBestS2 = 0;
    private float oldBestS3 = 0;
    private float oldBestLap = 0;
    private float personalBestS1 = 0;
    private float personalBestS2 = 0;
    private float personalBestS3 = 0;
    private Boolean personalBestMode = true;
    private FloatValue personalBestLap = new FloatValue();
    private float oldPersonalBestS1 = 0;
    private float oldPersonalBestS2 = 0;
    private float oldPersonalBestS3 = 0;
    private float oldPersonalBestLap = 0;
    private StringValue gap1 = new StringValue();
    private StringValue gap2 = new StringValue(); 
    private StringValue gap3 = new StringValue();
    private StringValue lap = new StringValue(); 
    private boolean flagPB = false;
    
    
    
    @Override
    public void onRealtimeEntered( LiveGameData gameData, boolean isEditorMode )
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
        
        if(!isEditorMode)
            GetPersonalBestLogFile(gameData);
        
        gap1.update( "" );
        gap2.update( "" ); 
        gap3.update( "" );
        lap.update( "" ); 
        
        Color whiteFontColor = fontColor2.getColor();
        int fh = TextureImage2D.getStringHeight( "09gy", f1_2011Font );
        int w2 = width*88/100;
        int top1 = height/4 - height/8 - fh/2;
        int top2 = height*2/4 - height/8 - fh/2;
        int top3 = height*3/4 - height/8 - fh/2;
        int top4 = height - height/8 - fh/2;
        
        imgSector.updateSize( width, (height/4)*95/100, isEditorMode );
        imgPersonalBest.updateSize( width, (height/4)*95/100, isEditorMode );
        imgFast.updateSize( width, (height/4)*95/100, isEditorMode );
        imgSlow.updateSize( width, (height/4)*95/100, isEditorMode );
        imgLap.updateSize( width, (height/4)*95/100, isEditorMode );
        
        dsGap1 = drawnStringFactory.newDrawnString( "dsGap1", w2, top1 + fontyoffset.getValue(), Alignment.RIGHT, false, f1_2011Font.getFont(), isFontAntiAliased(), whiteFontColor);
        dsGap2 = drawnStringFactory.newDrawnString( "dsGap2", w2, top2 + fontyoffset.getValue(), Alignment.RIGHT, false, f1_2011Font.getFont(), isFontAntiAliased(), whiteFontColor);
        dsGap3 = drawnStringFactory.newDrawnString( "dsGap3", w2, top3 + fontyoffset.getValue(), Alignment.RIGHT, false, f1_2011Font.getFont(), isFontAntiAliased(), whiteFontColor);
        dsLap = drawnStringFactory.newDrawnString( "dsLap", w2, top4 + fontyoffset.getValue(), Alignment.RIGHT, false, f1_2011Font.getFont(), isFontAntiAliased(), whiteFontColor);
        
    }
    
    @Override
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        
        super.updateVisibility(gameData, isEditorMode);
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        VehicleScoringInfo currentcarinfos = scoringInfo.getViewedVehicleScoringInfo();
        currentSector.update( currentcarinfos.getSector() );
        cveh.update(currentcarinfos.getDriverId());
        
        if(currentcarinfos.isPlayer() && currentcarinfos.getFastestLaptime() != null)
            personalBestLap.update( Math.min( personalBestLap.getValue(), currentcarinfos.getFastestLaptime().getLapTime() ));
        
        if(personalBestLap.hasChanged() && !isEditorMode && currentcarinfos.getFastestLaptime() != null)
        {    
            personalBestS1 = currentcarinfos.getFastestLaptime().getSector1();
            personalBestS2 = currentcarinfos.getFastestLaptime().getSector2();
            personalBestS3 = currentcarinfos.getFastestLaptime().getSector3();
            flagPB = true;
            UpdatePersonalBestLogFile(gameData);
        }
        
        if(currentcarinfos.getFastestLaptime() != null && ( currentcarinfos.getCurrentLaptime() >= currentcarinfos.getFastestLaptime().getSector1()/2 || currentcarinfos.getLapsCompleted() == 1) )
        {
            if(flagPB)
            {
                oldPersonalBestS1 = currentcarinfos.getFastestLaptime().getSector1();
                oldPersonalBestS2 = currentcarinfos.getFastestLaptime().getSector2();
                oldPersonalBestS3 = currentcarinfos.getFastestLaptime().getSector3();
                oldPersonalBestLap = personalBestLap.getValue();
                flagPB = false;
            }
            ClearGaps.update( false );
            oldBestS1 = currentcarinfos.getFastestLaptime().getSector1();
            oldBestS2 = currentcarinfos.getFastestLaptime().getSector2();
            oldBestS3 = currentcarinfos.getFastestLaptime().getSector3();
            oldBestLap = currentcarinfos.getFastestLaptime().getLapTime();
        }
        else
            ClearGaps.update( true );
        
        if((cveh.hasChanged() || currentSector.hasChanged() || ClearGaps.hasChanged()) && !isEditorMode)
            forceCompleteRedraw( true );
        
        return true;
        
         
    }
    protected void UpdatePersonalBestLogFile(LiveGameData gameData)
    {
        
        try
        {
            Writer output = null;
            File file = new File(gameData.getFileSystem().getCacheFolder() + "/data/personalbests/" + gameData.getModInfo().getName() + "." + gameData.getTrackInfo().getTrackName() + ".eve");
            output = new BufferedWriter(new FileWriter(file));
            output.write(personalBestLap.getValueAsString() + "\r\n");
            output.write(String.valueOf(personalBestS1) + "\r\n");
            output.write(String.valueOf(personalBestS2) + "\r\n");
            output.write(String.valueOf(personalBestS3) + "\r\n");
            output.close();     
        }
        catch (Exception e)
        {
            log(e);
        }
               
    }
    protected void GetPersonalBestLogFile(LiveGameData gameData)
    {
        String lines;
        
        try
        {
            File file = new File(gameData.getFileSystem().getCacheFolder() + "/data/personalbests/" + gameData.getModInfo().getName() + "." + gameData.getTrackInfo().getTrackName() + ".eve");
            BufferedReader br = new BufferedReader( new FileReader( file ) );
            
            lines = br.readLine();
            
            if(lines.equals(" [FastLap]"))
            {
                lines = br.readLine();
                lines = br.readLine();
                lines = br.readLine();
                lines = br.readLine();
                lines = br.readLine();
                lines = br.readLine();
                personalBestS2 = Float.valueOf(lines.substring( 4 ));
                lines = br.readLine();
                personalBestS1 = Float.valueOf(lines.substring( 4 ));
                lines = br.readLine();
                personalBestS3 = Float.valueOf(lines.substring( 4 ));
                br.close();
                personalBestS2 = personalBestS2 - personalBestS1;
            }
            else
            {
                lines = br.readLine();
                personalBestS1 = Float.valueOf(lines);
                lines = br.readLine();
                personalBestS2 = Float.valueOf(lines);
                lines = br.readLine();
                personalBestS3 = Float.valueOf(lines); 
            }
            personalBestLap.update( personalBestS1 + personalBestS2 + personalBestS3 );
            oldPersonalBestS1 = personalBestS1;
            oldPersonalBestS2 = personalBestS2;
            oldPersonalBestS3 = personalBestS3;
            oldPersonalBestLap = personalBestLap.getValue();
            
            log("init: " + personalBestLap.getValue());
        }
        catch (Exception e)
        {
            log("no personal best file found :: personalBestMode -> off");
            personalBestS1 = 0;
            personalBestS2 = 0;
            personalBestS3 = 0;
            personalBestLap.update(500000000f);
            personalBestMode = false;
        }
        
            
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        VehicleScoringInfo vsi = scoringInfo.getViewedVehicleScoringInfo();
        
        //////////////////////////  SECTOR 1  /////////////////////////////////////
        
        if(ClearGaps.getValue() && vsi.getFastestLaptime() != null && oldBestS1 > 0)
        {
            if(vsi.getLastSector1() <= oldPersonalBestS1 && oldPersonalBestS1 > 0 && vsi.isPlayer() && personalBestMode && vsi.getLastSector1() <= oldBestS1)
            {
                texture.clear( imgPersonalBest.getTexture(), offsetX, offsetY, false, null );
            }
            else
                if(vsi.getLastSector1() <= oldBestS1)
                    texture.clear( imgFast.getTexture(), offsetX, offsetY, false, null );
                else
                    texture.clear( imgSlow.getTexture(), offsetX, offsetY, false, null );  
        }
        else 
            if( vsi.getCurrentSector1() > 0  && vsi.getFastestLaptime() != null)
            {
                if(vsi.getCurrentSector1() <= oldPersonalBestS1 && oldPersonalBestS1 > 0 && vsi.isPlayer() && personalBestMode && vsi.getCurrentSector1() <= vsi.getFastestLaptime().getSector1())
                {
                    texture.clear( imgPersonalBest.getTexture(), offsetX, offsetY, false, null );
                }
                else
                    if(vsi.getCurrentSector1() <= vsi.getFastestLaptime().getSector1())
                        texture.clear( imgFast.getTexture(), offsetX, offsetY, false, null );
                    else
                        texture.clear( imgSlow.getTexture(), offsetX, offsetY, false, null );
            }
            else
                if(vsi.getCurrentSector1() > 0 || vsi.getFastestLaptime() != null)
                    texture.clear( imgSector.getTexture(), offsetX, offsetY, false, null );
            
        
        
            
        
        
        //////////////////////////  SECTOR 2  /////////////////////////////////////
        
        if(ClearGaps.getValue() && vsi.getFastestLaptime() != null && oldBestS2 > 0)
        {
            if(vsi.getLastSector2(false) <= oldPersonalBestS2 && oldPersonalBestS2 > 0 && vsi.isPlayer() && personalBestMode && vsi.getLastSector2(false) <= oldBestS2)
                texture.clear( imgPersonalBest.getTexture(), offsetX, offsetY + height/4, false, null );
            else
                if(vsi.getLastSector2(false) <= oldBestS2)
                    texture.clear( imgFast.getTexture(), offsetX, offsetY + height/4, false, null );
                else
                    texture.clear( imgSlow.getTexture(), offsetX, offsetY + height/4, false, null ); 
        }
        else 
            if( vsi.getCurrentSector2(false) > 0  && vsi.getFastestLaptime() != null)
            {
                if(vsi.getCurrentSector2(false) <= oldPersonalBestS2 && oldPersonalBestS2 > 0 && vsi.isPlayer() && personalBestMode && vsi.getCurrentSector2(false) <= vsi.getFastestLaptime().getSector2())
                    texture.clear( imgPersonalBest.getTexture(), offsetX, offsetY + height/4, false, null );
                else
                    if(vsi.getCurrentSector2(false) <= vsi.getFastestLaptime().getSector2())
                        texture.clear( imgFast.getTexture(), offsetX, offsetY + height/4, false, null );
                    else
                        texture.clear( imgSlow.getTexture(), offsetX, offsetY + height/4, false, null );  
            }
            else
                if(vsi.getCurrentSector2( false ) > 0 || vsi.getFastestLaptime() != null)
                    texture.clear( imgSector.getTexture(), offsetX, offsetY + height/4, false, null );
            
        
        //////////////////////////  SECTOR 3  /////////////////////////////////////
        
        if(ClearGaps.getValue() && vsi.getFastestLaptime() != null && oldBestS3 > 0)
        {
            if(vsi.getLastSector3() < oldPersonalBestS3 && oldPersonalBestS3 > 0 && vsi.isPlayer() && personalBestMode && vsi.getLastSector3() <= oldBestS3)
                texture.clear( imgPersonalBest.getTexture(), offsetX, offsetY + height*2/4, false, null );
            else
                if(vsi.getLastSector3() <= oldBestS3)
                    texture.clear( imgFast.getTexture(), offsetX, offsetY + height*2/4, false, null );
                else
                    texture.clear( imgSlow.getTexture(), offsetX, offsetY + height*2/4, false, null );
        }
        else 
            if(vsi.getFastestLaptime() != null)
                texture.clear( imgSector.getTexture(), offsetX, offsetY + height*2/4, false, null );
        
        
        //////////////////////////  Lap  /////////////////////////////////////
        
        if(ClearGaps.getValue() && vsi.getFastestLaptime() != null && oldBestLap > 0)
        {
            if(vsi.getLastLapTime() < oldPersonalBestLap && oldPersonalBestLap > 0 && vsi.isPlayer() && personalBestMode)
            {
                texture.clear( imgPersonalBest.getTexture(), offsetX, offsetY + height*3/4, false, null );
            }
            else
                if(vsi.getLastLapTime() <= oldBestLap)
                    texture.clear( imgFast.getTexture(), offsetX, offsetY + height*3/4, false, null );
                else
                    texture.clear( imgSlow.getTexture(), offsetX, offsetY + height*3/4, false, null );
        }
        else 
            if(vsi.getFastestLaptime() != null)
                texture.clear( imgLap.getTexture(), offsetX, offsetY + height*3/4, false, null );
        
        
    }
    
    private static final String getTimeAsGapString2( float gap )
    {
        if ( gap == 0f )
            return ( "- " + TimingUtil.getTimeAsLaptimeString( 0f ) );
        
        if ( gap < 0f )
            return ( "- " + TimingUtil.getTimeAsLaptimeString( -gap ) );
        
        return ( "+ " + TimingUtil.getTimeAsLaptimeString( gap ) );
    }
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        VehicleScoringInfo currentcarinfos = scoringInfo.getViewedVehicleScoringInfo();
        
        
        
        /////////////////////////////////////////////////////////////////
        if(ClearGaps.getValue() && currentcarinfos.getFastestLaptime() != null && oldBestS1 > 0)
        {
            if(currentcarinfos.getLastLapTime() <= currentcarinfos.getFastestLaptime().getLapTime())
                gap1.update( getTimeAsGapString2( currentcarinfos.getLastSector1() - oldBestS1 ));
            else   
                gap1.update( getTimeAsGapString2( currentcarinfos.getLastSector1() - currentcarinfos.getFastestLaptime().getSector1() ));
            if(currentcarinfos.getLastSector1() <= oldBestS1)
                Gap1FontColor = fontColor2;
            else
                Gap1FontColor = fontColor1;  
        }
        else 
            if( currentcarinfos.getCurrentSector1() > 0 && currentcarinfos.getFastestLaptime() != null )
            {
                gap1.update( getTimeAsGapString2( currentcarinfos.getCurrentSector1() - currentcarinfos.getFastestLaptime().getSector1()));
                if(currentcarinfos.getCurrentSector1() <= currentcarinfos.getFastestLaptime().getSector1())
                    Gap1FontColor = fontColor2;
                else
                    Gap1FontColor = fontColor1;
            }
            else
            {
                if(currentcarinfos.getFastestLaptime() != null)
                    gap1.update( TimingUtil.getTimeAsLaptimeString( currentcarinfos.getFastestLaptime().getSector1() ));
                else
                    if(currentcarinfos.getCurrentSector1() > 0)
                        gap1.update( TimingUtil.getTimeAsLaptimeString( currentcarinfos.getCurrentSector1() ));
                Gap1FontColor = fontColor1;
            }
        
        
            
        
        
        
        /////////////////////////////////////////////////////////////////
        //log("Sector 2");
        if(ClearGaps.getValue() && currentcarinfos.getFastestLaptime() != null && oldBestS2 > 0)
        {
            if(currentcarinfos.getLastLapTime() <= currentcarinfos.getFastestLaptime().getLapTime())
                gap2.update( getTimeAsGapString2( currentcarinfos.getLastSector2(false) - oldBestS2 ));
            else
                gap2.update( getTimeAsGapString2( currentcarinfos.getLastSector2(false) - currentcarinfos.getFastestLaptime().getSector2() ));
            if(currentcarinfos.getLastSector2(false) <= oldBestS2)
                Gap2FontColor = fontColor2;
            else
                Gap2FontColor = fontColor1; 
        }
        else 
            if( currentcarinfos.getCurrentSector2(false) > 0 && currentcarinfos.getFastestLaptime() != null )
            {
                gap2.update( getTimeAsGapString2( currentcarinfos.getCurrentSector2(false) - currentcarinfos.getFastestLaptime().getSector2()));
                if(currentcarinfos.getCurrentSector2(false) <= currentcarinfos.getFastestLaptime().getSector2())
                    Gap2FontColor = fontColor2;
                else
                    Gap2FontColor = fontColor1;  
            }
            else
            {
                if(currentcarinfos.getFastestLaptime() != null)
                    gap2.update( TimingUtil.getTimeAsLaptimeString( currentcarinfos.getFastestLaptime().getSector2() ));
                else
                    if(currentcarinfos.getCurrentSector2( false ) > 0)
                        gap2.update( TimingUtil.getTimeAsLaptimeString( currentcarinfos.getCurrentSector2( false ) ));
                
                Gap2FontColor = fontColor1;
            }
        
        
        ///////////////////////////////////////////////////////////////////
        //log("Sector 3");
        if(ClearGaps.getValue() && currentcarinfos.getFastestLaptime() != null && oldBestS3 > 0)
        {
            if(currentcarinfos.getLastLapTime() <= currentcarinfos.getFastestLaptime().getLapTime())
                gap3.update( getTimeAsGapString2( currentcarinfos.getLastSector3() - oldBestS3 ));
            else    
                gap3.update( getTimeAsGapString2( currentcarinfos.getLastSector3() - currentcarinfos.getFastestLaptime().getSector3() ));
            if(currentcarinfos.getLastSector3() <= oldBestS3)
                Gap3FontColor = fontColor2;
            else
                Gap3FontColor = fontColor1;
        }
        else 
        {
            if(currentcarinfos.getFastestLaptime() != null)
                gap3.update( TimingUtil.getTimeAsLaptimeString( currentcarinfos.getFastestLaptime().getSector3() ));
            Gap3FontColor = fontColor1;
        }
        
        //////////////////////////////////////Lap/////////////////////////
        //log("Full Lap Time");
        if(ClearGaps.getValue() && currentcarinfos.getFastestLaptime() != null && oldBestLap > 0)
        {
            if(currentcarinfos.getLastLapTime() <= currentcarinfos.getFastestLaptime().getLapTime())
            {
                lap.update( getTimeAsGapString2( currentcarinfos.getLastLapTime() - oldBestLap ));
                LapFontColor = fontColor2;
            }
            else
            {
                lap.update( getTimeAsGapString2( currentcarinfos.getLastLapTime() - currentcarinfos.getFastestLaptime().getLapTime() ));
                LapFontColor = fontColor1;
            }
        }
        else 
        {
            if(currentcarinfos.getFastestLaptime() != null)
                lap.update( TimingUtil.getTimeAsLaptimeString( currentcarinfos.getFastestLaptime().getLapTime() ));
            LapFontColor = fontColor1;
        }
        
        if( needsCompleteRedraw || ( clock.c() && gap1.hasChanged() ) )
            dsGap1.draw( offsetX, offsetY, gap1.getValue(), Gap1FontColor.getColor(), texture );
        if( needsCompleteRedraw || ( clock.c() && gap2.hasChanged() ) )
            dsGap2.draw( offsetX, offsetY, gap2.getValue(), Gap2FontColor.getColor(), texture );
        if( needsCompleteRedraw || ( clock.c() && gap3.hasChanged() ) )
            dsGap3.draw( offsetX, offsetY, gap3.getValue(), Gap3FontColor.getColor(), texture );
        if( needsCompleteRedraw || ( clock.c() && lap.hasChanged() ) )
            dsLap.draw( offsetX, offsetY, lap.getValue(), LapFontColor.getColor(), texture );
        
        
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty( fontColor1, "" );
        writer.writeProperty( f1_2011Font, "" );
        writer.writeProperty( fontyoffset, "" );
        
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        
        if ( loader.loadProperty( fontColor2 ) );
        else if ( loader.loadProperty( fontColor1 ) );
        else if ( loader.loadProperty( f1_2011Font ) );
        else if ( loader.loadProperty( fontyoffset ) );
        
    }
    
    @Override
    protected void addFontPropertiesToContainer( PropertiesContainer propsCont, boolean forceAll )
    {
        propsCont.addGroup( "Colors and Fonts" );
        
        super.addFontPropertiesToContainer( propsCont, forceAll );
        
        propsCont.addProperty( f1_2011Font );
        propsCont.addProperty( fontColor1 );
        propsCont.addProperty( fontColor2 );

        propsCont.addProperty( fontyoffset );
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Specific" );
        
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
    
    public PersonalSectorsWidget()
    {
        super( PrunnWidgetSetPorscheSupercup.INSTANCE, PrunnWidgetSetPorscheSupercup.WIDGET_PACKAGE_PorscheSupercup, 13.0f, 14.1f );
        
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetPorscheSupercup.GP2_FONT_NAME );
        getFontColorProperty().setColor( PrunnWidgetSetPorscheSupercup.FONT_COLOR1_NAME );
    }
    
}
