package com.prunn.rfdynhud.widgets.prunn.PorscheSupercup.qtime;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetPorscheSupercup;

import net.ctdp.rfdynhud.gamedata.FinishStatus;
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
import net.ctdp.rfdynhud.values.EnumValue;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

/**
 * @author Prunn
 * copyright@Prunn2011
 * 
 */


public class QualTimeWidget extends Widget
{
    private static enum Situation
    {
        LAST_SECONDS_OF_SECTOR_1,
        SECTOR_1_FINISHED_BEGIN_SECTOR_2,
        LAST_SECONDS_OF_SECTOR_2,
        SECTOR_2_FINISHED_BEGIN_SECTOR_3,
        LAST_SECONDS_OF_SECTOR_LAP,
        LAP_FINISHED_BEGIN_NEW_LAP,
        OTHER,
        ;
    }
    
    private static final float SECTOR_DELAY = 5f;
    
    private DrawnString dsPos = null;
    private DrawnString dsPosFrom = null;
    private DrawnString dsName = null;
    private DrawnString dsTime = null;
    private DrawnString dsGap = null;
    
    private TextureImage2D texPos = null;
    private final ImagePropertyWithTexture imgPos = new ImagePropertyWithTexture( "imgPos", "prunn/PorscheSupercup/pos.png" );
    private final ImagePropertyWithTexture imgPosFirst = new ImagePropertyWithTexture( "imgPos", "prunn/PorscheSupercup/pos1.png" );
    private final ImagePropertyWithTexture imgPosFrom = new ImagePropertyWithTexture( "imgPosFromFirst", "prunn/PorscheSupercup/pos1_neutral.png" );
    private final ImagePropertyWithTexture imgPosFromSlow = new ImagePropertyWithTexture( "imgPosFromFirst", "prunn/PorscheSupercup/pos1_slower.png" );
    private final ImagePropertyWithTexture imgPosFromFast = new ImagePropertyWithTexture( "imgPosFromFirst", "prunn/PorscheSupercup/pos1_faster.png" );
    private final ImagePropertyWithTexture imgName = new ImagePropertyWithTexture( "imgName", "prunn/PorscheSupercup/data_neutral.png" );
    private final ImagePropertyWithTexture imgNameFirst = new ImagePropertyWithTexture( "imgName", "prunn/PorscheSupercup/data_title.png" );
    private final ImagePropertyWithTexture imgTimeBlack = new ImagePropertyWithTexture( "imgTimeBlack", "prunn/PorscheSupercup/data_neutral.png" );
    private final ImagePropertyWithTexture imgTimeGreen = new ImagePropertyWithTexture( "imgTimeGreen", "prunn/PorscheSupercup/data_faster.png" );
    private final ImagePropertyWithTexture imgTimeGreenFinish = new ImagePropertyWithTexture( "imgTimeGreenFinish", "prunn/PorscheSupercup/data_faster.png" );
    private final ImagePropertyWithTexture imgTimeYellow = new ImagePropertyWithTexture( "imgTimeYellow", "prunn/PorscheSupercup/data_slower.png" );
    private final ImagePropertyWithTexture imgTimeYellowFinish = new ImagePropertyWithTexture( "imgTimeYellowFinish", "prunn/PorscheSupercup/data_slower.png" );
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    
    private final ColorProperty fontColor2 = new ColorProperty("fontColor2", PrunnWidgetSetPorscheSupercup.FONT_COLOR2_NAME);
    private final FontProperty posFont = new FontProperty("positionFont", PrunnWidgetSetPorscheSupercup.POS_FONT_NAME);
    private final ColorProperty fontColor1 = new ColorProperty("fontColor1", PrunnWidgetSetPorscheSupercup.FONT_COLOR1_NAME);
    
    
    private final EnumValue<Situation> situation = new EnumValue<Situation>();
    private final IntValue leaderID = new IntValue();
    private final IntValue leaderPos = new IntValue();
    private final IntValue ownPos = new IntValue();
    private float leadsec1 = -1f;
    private float leadsec2 = -1f;
    private float leadlap = -1f;
    private final FloatValue cursec1 = new FloatValue(-1f, 0.001f);
    private final FloatValue cursec2 = new FloatValue(-1f, 0.001f);
    private final FloatValue curlap = new FloatValue(-1f, 0.001f);
    private final FloatValue oldbesttime = new FloatValue(-1f, 0.001f);
    private final FloatValue gapOrTime = new FloatValue(-1f, 0.001f);
    private final FloatValue lastLaptime = new FloatValue(-1f, 0.001f);
    private final FloatValue fastestlap = new FloatValue(-1f, 0.001f);
    private final BoolValue gapAndTimeInvalid = new BoolValue();
    private float oldbest = 0;
    private static Boolean isvisible = false;
    public static Boolean visible()
    {
        return isvisible;
    }
    
    
    @Override
    public void onCockpitEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onCockpitEntered( gameData, isEditorMode );
        String cpid = "Y29weXJpZ2h0QFBydW5uMjAxMQ";
        if(!isEditorMode)
            log(cpid);
        situation.reset();
        leaderID.reset();
        leaderPos.reset();
        ownPos.reset();
        cursec1.reset();
        cursec2.reset();
        curlap.reset();
        oldbesttime.reset();
        gapOrTime.reset();
        lastLaptime.reset();
        gapAndTimeInvalid.reset();
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
        int rowHeight = height / 3;
        int fh = TextureImage2D.getStringHeight( "09gy", getFontProperty() );
        
        imgName.updateSize( width, rowHeight*95/100, isEditorMode );
        imgNameFirst.updateSize( width, rowHeight*95/100, isEditorMode );
        imgTimeBlack.updateSize( width, rowHeight*95/100, isEditorMode );
        imgTimeGreen.updateSize( width, rowHeight*95/100, isEditorMode );
        imgTimeGreenFinish.updateSize( width, rowHeight*95/100, isEditorMode );
        imgTimeYellow.updateSize( width, rowHeight*95/100, isEditorMode );
        imgTimeYellowFinish.updateSize( width, rowHeight*95/100, isEditorMode );
        
        Color blackFontColor = getFontColor();
        Color whiteFontColor = fontColor1.getColor();
        
        int textOff = ( rowHeight - fh ) / 2;
        
        dsName = drawnStringFactory.newDrawnString( "dsName", width*94/100, textOff + fontyoffset.getValue(), Alignment.RIGHT, false, getFont(), isFontAntiAliased(), whiteFontColor);
        dsTime = drawnStringFactory.newDrawnString( "dsTime", width*94/100, rowHeight + textOff + fontyoffset.getValue(), Alignment.RIGHT, false, getFont(), isFontAntiAliased(), whiteFontColor);
        dsPos = drawnStringFactory.newDrawnString( "dsPos", width*12/100, textOff + fontyoffset.getValue(), Alignment.CENTER, false, getFont(), isFontAntiAliased(), whiteFontColor);
        dsPosFrom = drawnStringFactory.newDrawnString( "dsPosFrom", width*12/100, rowHeight * 2 + textOff + fontyoffset.getValue(), Alignment.CENTER, false, getFont(), isFontAntiAliased(), whiteFontColor );
        dsGap = drawnStringFactory.newDrawnString( "dsTime", width*94/100, rowHeight * 2 + textOff + fontyoffset.getValue(), Alignment.RIGHT, false, getFont(), isFontAntiAliased(), blackFontColor);
        
    }
    
    private VehicleScoringInfo getLeaderCarInfos( ScoringInfo scoringInfo )
    {
        
        return ( scoringInfo.getLeadersVehicleScoringInfo() );
        
    }
    
    private void updateSectorValues( ScoringInfo scoringInfo )
    {
        VehicleScoringInfo currentcarinfos = scoringInfo.getViewedVehicleScoringInfo();
        VehicleScoringInfo leadercarinfos = getLeaderCarInfos( scoringInfo );
        
        if(leadercarinfos.getFastestLaptime() != null && leadercarinfos.getFastestLaptime().getLapTime() >= 0)
        {
            leadsec1 = leadercarinfos.getFastestLaptime().getSector1();
            leadsec2 = leadercarinfos.getFastestLaptime().getSector1And2();
            leadlap = leadercarinfos.getFastestLaptime().getLapTime();
        }
        else
        {
            leadsec1 = 0f;
            leadsec2 = 0f;
            leadlap = 0f;
        }
        
        cursec1.update( currentcarinfos.getCurrentSector1() );
        cursec2.update( currentcarinfos.getCurrentSector2( true ) );

        if ( scoringInfo.getSessionTime() > 0f )
            curlap.update( currentcarinfos.getCurrentLaptime() );
        else
            curlap.update( scoringInfo.getSessionNanos() / 1000000000f - currentcarinfos.getLapStartTime() ); 
            

    }
    
    private boolean updateSituation( VehicleScoringInfo currentcarinfos )
    {
        final byte sector = currentcarinfos.getSector();
        
        if(sector == 1 && curlap.getValue() > leadsec1 - SECTOR_DELAY && leadlap > 0)
        {
            situation.update( Situation.LAST_SECONDS_OF_SECTOR_1 );
        }
        else if(sector == 2 && curlap.getValue() - cursec1.getValue() <= SECTOR_DELAY && leadlap > 0)
        {
            situation.update( Situation.SECTOR_1_FINISHED_BEGIN_SECTOR_2 );
        }
        else if(sector == 2  && curlap.getValue() > leadsec2 - SECTOR_DELAY && leadlap > 0)
        {
            situation.update( Situation.LAST_SECONDS_OF_SECTOR_2 );
        }
        else if(sector == 3 && curlap.getValue() - cursec2.getValue() <= SECTOR_DELAY && leadlap > 0)
        {
            situation.update( Situation.SECTOR_2_FINISHED_BEGIN_SECTOR_3 );
        }
        else if(sector == 3 && curlap.getValue() > leadlap - SECTOR_DELAY && leadlap > 0)
        {
            situation.update( Situation.LAST_SECONDS_OF_SECTOR_LAP );
        }
        else if(sector == 1 && curlap.getValue() <= SECTOR_DELAY && currentcarinfos.getLastLapTime() > 0)
        {
            situation.update( Situation.LAP_FINISHED_BEGIN_NEW_LAP );
        }
        else
        {
            situation.update( Situation.OTHER );
        }
        
        return ( situation.hasChanged() );
    }
    
    @Override
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        
        super.updateVisibility(gameData, isEditorMode);
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        updateSectorValues( scoringInfo );
        VehicleScoringInfo currentcarinfos = scoringInfo.getViewedVehicleScoringInfo();
        
        
        
        fastestlap.update(scoringInfo.getLeadersVehicleScoringInfo().getBestLapTime());
        //if( gameData.getScoringInfo().getNumVehicles() >= posKnockout.getValue() )
         //   knockoutlap.update(scoringInfo.getVehicleScoringInfo( posKnockout.getValue()-1 ).getBestLapTime());
        
        if ( (updateSituation( currentcarinfos )  || fastestlap.hasChanged()) && !isEditorMode)
            forceCompleteRedraw( true );
        
        if ( currentcarinfos.isInPits() )
        {
            isvisible = false;
            return false;
        }
        
        if(currentcarinfos.getFinishStatus() == FinishStatus.FINISHED && situation.getValue() != Situation.LAP_FINISHED_BEGIN_NEW_LAP )
            return false;
        
        float curLaptime;
        if ( scoringInfo.getSessionTime() > 0f )
            curLaptime = currentcarinfos.getCurrentLaptime();
        else
            curLaptime = scoringInfo.getSessionNanos() / 1000000000f - currentcarinfos.getLapStartTime();
        
        if ( curLaptime > 0f )
        {
            isvisible = true;
            //forceCompleteRedraw( true );
            return true;
        }
            
        return false;
         
    }
    
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        VehicleScoringInfo currentcarinfos = gameData.getScoringInfo().getViewedVehicleScoringInfo();
        
        int rowHeight = height / 3;
        
        if(currentcarinfos.getPlace( false ) == 1)
            texture.clear( imgNameFirst.getTexture(), offsetX, offsetY, false, null );
        else
            texture.clear( imgName.getTexture(), offsetX, offsetY, false, null );
        
        texture.clear( imgName.getTexture(), offsetX, offsetY + rowHeight, false, null );
        
        switch ( situation.getValue() )
        {
            case LAST_SECONDS_OF_SECTOR_1:
                texture.clear( imgTimeBlack.getTexture(), offsetX, offsetY + rowHeight*2, false, null );
                texPos = imgPosFrom.getImage().getScaledTextureImage( width*22/100, rowHeight*95/100, texPos, isEditorMode );
                texture.drawImage( texPos, offsetX + width*1/100, offsetY+rowHeight*2, true, null );
                break;
                
            case SECTOR_1_FINISHED_BEGIN_SECTOR_2:
                if( cursec1.getValue() <= leadsec1 )
                {
                    texture.clear( imgTimeGreen.getTexture(), offsetX, offsetY + rowHeight*2, false, null );
                    texPos = imgPosFromFast.getImage().getScaledTextureImage( width*22/100, rowHeight*95/100, texPos, isEditorMode );
                
                }
                else
                {
                    texture.clear( imgTimeYellow.getTexture(), offsetX, offsetY + rowHeight*2, false, null );
                    texPos = imgPosFromSlow.getImage().getScaledTextureImage( width*22/100, rowHeight*95/100, texPos, isEditorMode );
                    
                }
                texture.drawImage( texPos, offsetX + width*1/100, offsetY+rowHeight*2, true, null );
                
                break;
                
            case LAST_SECONDS_OF_SECTOR_2:
                
                texture.clear( imgTimeBlack.getTexture(), offsetX, offsetY + rowHeight*2, false, null );
                texPos = imgPosFrom.getImage().getScaledTextureImage( width*22/100, rowHeight*95/100, texPos, isEditorMode );
                texture.drawImage( texPos, offsetX + width*1/100, offsetY+rowHeight*2, true, null );
                
                break;
                
            case SECTOR_2_FINISHED_BEGIN_SECTOR_3:
                if( cursec2.getValue() <= leadsec2 )
                {
                    texture.clear( imgTimeGreen.getTexture(), offsetX, offsetY + rowHeight*2, false, null );
                    texPos = imgPosFromFast.getImage().getScaledTextureImage( width*22/100, rowHeight*95/100, texPos, isEditorMode );
                    
                }
                else
                {
                    texture.clear( imgTimeYellow.getTexture(), offsetX, offsetY + rowHeight*2, false, null );
                    texPos = imgPosFromSlow.getImage().getScaledTextureImage( width*22/100, rowHeight*95/100, texPos, isEditorMode );
                    
                }  
                texture.drawImage( texPos, offsetX + width*1/100, offsetY+rowHeight*2, true, null );
                
                break;
                
            case LAST_SECONDS_OF_SECTOR_LAP:
                texture.clear( imgTimeBlack.getTexture(), offsetX, offsetY + rowHeight*2, false, null );
                texPos = imgPosFrom.getImage().getScaledTextureImage( width*22/100, rowHeight*95/100, texPos, isEditorMode );
                texture.drawImage( texPos, offsetX + width*1/100, offsetY+rowHeight*2, true, null );
                
                break;
                
            case LAP_FINISHED_BEGIN_NEW_LAP:
                if( currentcarinfos.getLastLapTime() <= leadlap )
                {
                    texture.clear( imgTimeGreenFinish.getTexture(), offsetX, offsetY + rowHeight*2, false, null );
                }
                else
                    if( currentcarinfos.getPlace( false ) == 1)
                        texture.clear( imgTimeYellowFinish.getTexture(), offsetX, offsetY + rowHeight*2, false, null );
                    else
                    {
                       texture.clear( imgTimeYellow.getTexture(), offsetX, offsetY + rowHeight*2, false, null );
                       texPos = imgPosFromSlow.getImage().getScaledTextureImage( width*22/100, rowHeight*95/100, texPos, isEditorMode );
                       texture.drawImage( texPos, offsetX + width*1/100, offsetY+rowHeight*2, true, null );
                    }
                
                
                     
                if(currentcarinfos.getPlace( false ) == 1)
                    texPos = imgPosFirst.getImage().getScaledTextureImage( width*22/100, rowHeight*95/100, texPos, isEditorMode );
                else 
                    texPos = imgPos.getImage().getScaledTextureImage( width*22/100, rowHeight*95/100, texPos, isEditorMode );
                    
                texture.drawImage( texPos, offsetX + width*1/100, offsetY, true, null );
                       
                break;
                
            
        }
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
        updateSectorValues( scoringInfo );
        
        VehicleScoringInfo currentcarinfos = scoringInfo.getViewedVehicleScoringInfo();
        VehicleScoringInfo leadercarinfos = getLeaderCarInfos( scoringInfo );
        
            
        leaderID.update( leadercarinfos.getDriverId() );
        leaderPos.update( leadercarinfos.getPlace( false ) );
        
        if ( needsCompleteRedraw || ( clock.c() && leaderID.hasChanged() ) )
        {
            dsName.draw( offsetX, offsetY, PrunnWidgetSetPorscheSupercup.ShortName( currentcarinfos.getDriverNameShort()),(currentcarinfos.getPlace(false) == 1) ? fontColor2.getColor() : fontColor1.getColor(), texture );
            
        }
        
        
        switch ( situation.getValue() )
        {
            case LAST_SECONDS_OF_SECTOR_1:
                gapAndTimeInvalid.update( false );
                gapOrTime.update( leadsec1 );
                
                if ( needsCompleteRedraw || ( clock.c() && gapOrTime.hasChanged() ) )
                    dsGap.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString( gapOrTime.getValue() ), fontColor1.getColor() , texture);
                if ( needsCompleteRedraw || ( clock.c() && leaderPos.hasChanged() ) )
                    dsPosFrom.draw( offsetX, offsetY, leaderPos.getValueAsString(), texture );  
                if ( needsCompleteRedraw || ( clock.c() && curlap.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, TimingUtil.getTimeAsString( curlap.getValue(), false, false, true, false ) + "    ", texture);
                break;
                
            case SECTOR_1_FINISHED_BEGIN_SECTOR_2:
                gapAndTimeInvalid.update( false );
                gapOrTime.update( cursec1.getValue() - leadsec1 );
                
                if ( needsCompleteRedraw || ( clock.c() && gapOrTime.hasChanged() ) )
                    dsGap.draw( offsetX, offsetY, getTimeAsGapString2( gapOrTime.getValue() ), ( gapOrTime.getValue() <= 0 ) ? fontColor2.getColor() : fontColor1.getColor() , texture);
                if ( needsCompleteRedraw || ( clock.c() && leaderPos.hasChanged() ) )
                    dsPosFrom.draw( offsetX, offsetY, leaderPos.getValueAsString(), texture );
                if ( needsCompleteRedraw || ( clock.c() && cursec1.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, TimingUtil.getTimeAsString( cursec1.getValue(), false, false, true, true ) , texture);
                break;
                
            case LAST_SECONDS_OF_SECTOR_2:
                gapAndTimeInvalid.update( false );
                gapOrTime.update( leadsec2 );
                
                if ( needsCompleteRedraw || ( clock.c() && gapOrTime.hasChanged() ) )
                    dsGap.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString( leadsec2 ), fontColor1.getColor() , texture);
                if ( needsCompleteRedraw || ( clock.c() && leaderPos.hasChanged() ) )
                    dsPosFrom.draw( offsetX, offsetY, leaderPos.getValueAsString(), texture );
                if ( needsCompleteRedraw || ( clock.c() && curlap.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, TimingUtil.getTimeAsString( curlap.getValue(), false, false, true, false ) + "    ", texture);
                break;
                
            case SECTOR_2_FINISHED_BEGIN_SECTOR_3:
                gapAndTimeInvalid.update( false );
                gapOrTime.update( cursec2.getValue() - leadsec2 );
                
                if ( needsCompleteRedraw || ( clock.c() && gapOrTime.hasChanged() ) )
                    dsGap.draw( offsetX, offsetY, getTimeAsGapString2( gapOrTime.getValue() ), ( gapOrTime.getValue() <= 0 ) ? fontColor2.getColor() : fontColor1.getColor() , texture);
                if ( needsCompleteRedraw || ( clock.c() && leaderPos.hasChanged() ) )
                    dsPosFrom.draw( offsetX, offsetY, leaderPos.getValueAsString(), texture );
                if ( needsCompleteRedraw || ( clock.c() && cursec2.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, TimingUtil.getTimeAsString( cursec2.getValue(), false, false, true, true ) , texture);
                break;
                
            case LAST_SECONDS_OF_SECTOR_LAP:
                gapAndTimeInvalid.update( false );
                gapOrTime.update( leadlap );
                
                if ( needsCompleteRedraw || ( clock.c() && gapOrTime.hasChanged() ) )
                    dsGap.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString( gapOrTime.getValue() ), fontColor1.getColor() , texture);
                if ( needsCompleteRedraw || ( clock.c() && leaderPos.hasChanged() ) )
                    dsPosFrom.draw( offsetX, offsetY, leaderPos.getValueAsString(), texture );
                if ( needsCompleteRedraw || ( clock.c() && curlap.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, TimingUtil.getTimeAsString( curlap.getValue(), false, false, true, false ) + "    ", texture);
                break;
                
            case LAP_FINISHED_BEGIN_NEW_LAP:
                //plan: if allready first show gap to previous own best time. else if newly first show gap to second                             
                
                float secondbest=0;
                oldbesttime.update( currentcarinfos.getBestLapTime() );
                //oldposition.update( currentcarinfos.getPlace( false ) );
                ownPos.update( currentcarinfos.getPlace( false ) );
                
                if(oldbesttime.hasChanged())
                    oldbest = oldbesttime.getOldValue();
                
                if(ownPos.getValue() == 1)
                {
                    if(gameData.getScoringInfo().getSecondFastestLapVSI() != null && ownPos.getValue() != 1)
                        secondbest = gameData.getScoringInfo().getSecondFastestLapVSI().getBestLapTime(); 
                    else
                        secondbest = oldbest;
                }
                
                
                if (currentcarinfos.getLastLapTime() <= leadercarinfos.getBestLapTime() && secondbest < 0)
                    gapOrTime.update(  currentcarinfos.getLastLapTime() - oldbest );
                else
                    if( currentcarinfos.getLastLapTime() <= leadercarinfos.getBestLapTime() )
                        gapOrTime.update( currentcarinfos.getLastLapTime() - secondbest );
                    else
                        gapOrTime.update( currentcarinfos.getLastLapTime() - leadercarinfos.getBestLapTime() );
                    
                if ( needsCompleteRedraw || ( clock.c() && gapOrTime.hasChanged() ) )
                    dsGap.draw( offsetX, offsetY, getTimeAsGapString2( gapOrTime.getValue() ), ( gapOrTime.getValue() <= 0 ) ? fontColor2.getColor() : fontColor1.getColor(), texture);
                
                
                
                lastLaptime.update( currentcarinfos.getLastLapTime() );
                gapAndTimeInvalid.update( false );
                
                if ( needsCompleteRedraw || ( clock.c() && leaderPos.hasChanged() ) )
                {
                    if( (currentcarinfos.getLastLapTime() <= leadlap && leadercarinfos.getPlace( false ) == 1) || ownPos.getValue() == 1)
                        dsPosFrom.draw( offsetX, offsetY, "", texture );
                    else
                        dsPosFrom.draw( offsetX, offsetY, leaderPos.getValueAsString(), texture );
                    
                }
                if ( needsCompleteRedraw || ( clock.c() && ownPos.hasChanged() ) )
                    dsPos.draw( offsetX, offsetY, ownPos.getValueAsString(),( ownPos.getValue() == 1 ) ? fontColor1.getColor() : fontColor2.getColor(), texture );
                if ( needsCompleteRedraw || ( clock.c() && lastLaptime.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, TimingUtil.getTimeAsString( lastLaptime.getValue(), false, false, true, true ) , texture);
                break;
              
            case OTHER:
                // other cases not info not drawn
                gapAndTimeInvalid.update( true );
                
                if ( needsCompleteRedraw || ( clock.c() && gapAndTimeInvalid.hasChanged() ) )
                {
                    dsGap.draw( offsetX, offsetY, "", texture);
                    dsPosFrom.draw( offsetX, offsetY, "", texture );
                }
                if ( needsCompleteRedraw || ( clock.c() && curlap.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, TimingUtil.getTimeAsString( curlap.getValue(), false, false, true, false ) + "    ", texture);
                break;
        }
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty( posFont, "" );
        writer.writeProperty( fontColor1, "" );
        writer.writeProperty( fontyoffset, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        
        if ( loader.loadProperty( fontColor2 ) );
        else if ( loader.loadProperty( posFont ) );
        else if ( loader.loadProperty( fontColor1 ) );
        else if ( loader.loadProperty( fontyoffset ) );
    }
    
    @Override
    protected void addFontPropertiesToContainer( PropertiesContainer propsCont, boolean forceAll )
    {
        propsCont.addGroup( "Colors and Fonts" );
        
        super.addFontPropertiesToContainer( propsCont, forceAll );
        
        propsCont.addProperty( fontColor1 );
        propsCont.addProperty( fontColor2 );
        propsCont.addProperty( posFont );
        
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Specific" );
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
    
    public QualTimeWidget()
    {
        super( PrunnWidgetSetPorscheSupercup.INSTANCE, PrunnWidgetSetPorscheSupercup.WIDGET_PACKAGE_PorscheSupercup, 26.2f, 10.75f );
        
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetPorscheSupercup.GP2_FONT_NAME );
    }
    
}
