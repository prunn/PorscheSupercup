package com.prunn.rfdynhud.widgets.prunn.PorscheSupercup.resultmonitor;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import net.ctdp.rfdynhud.gamedata.FinishStatus;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.SessionType;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.properties.BooleanProperty;
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


public class ResultMonitorWidget extends Widget
{
    private DrawnString[] dsPos = null;
    private DrawnString[] dsName = null;
    private DrawnString[] dsTeam = null;
    private DrawnString[] dsTime = null;
    private DrawnString dsTrack = null;
    //private DrawnString dsSession = null;
    
    private TextureImage2D texCountry = null;
    private final ImagePropertyWithTexture imgCountry = new ImagePropertyWithTexture( "imgTime", "prunn/PorscheSupercup/flag.png" );
    
    private TextureImage2D texPos = null;
    private TextureImage2D texGapFirst = null;
    private final ImagePropertyWithTexture imgGapFirst = new ImagePropertyWithTexture( "imgTime", "prunn/PorscheSupercup/data_first.png" );
    private final ImagePropertyWithTexture imgPos = new ImagePropertyWithTexture( "imgPos", "prunn/PorscheSupercup/pos.png" );
    private final ImagePropertyWithTexture imgPosFirst = new ImagePropertyWithTexture( "imgPos", "prunn/PorscheSupercup/pos1.png" );
    
    private final ImagePropertyWithTexture imgTrack = new ImagePropertyWithTexture( "imgTrack", "prunn/PorscheSupercup/data_title_long.png" );
    private final ImagePropertyWithTexture imgFirst = new ImagePropertyWithTexture( "imgFirst", "prunn/PorscheSupercup/data_title_long.png" );
    private final ImagePropertyWithTexture imgNames = new ImagePropertyWithTexture( "imgPos", "prunn/PorscheSupercup/race_control.png" );
    
    protected final FontProperty f1_2011Font = new FontProperty("Main Font", PrunnWidgetSetPorscheSupercup.GP2_FONT_NAME);
    private final ColorProperty fontColor1 = new ColorProperty( "fontColor1", PrunnWidgetSetPorscheSupercup.FONT_COLOR1_NAME );
    private final ColorProperty fontColor2 = new ColorProperty( "fontColor2", PrunnWidgetSetPorscheSupercup.FONT_COLOR2_NAME );
    
    private final IntProperty numVeh = new IntProperty( "numberOfVehicles", 10 );
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    private IntProperty fontxposoffset = new IntProperty("X Position Font Offset", 0);
    private IntProperty fontxnameoffset = new IntProperty("X Name Font Offset", 0);
    private IntProperty fontxtimeoffset = new IntProperty("X Time Font Offset", 0);
    private IntProperty MaxTeamLengh = new IntProperty("Max Team Name Lenght", 20); 
    private IntValue[] positions = null;
    private StringValue[] driverNames = null;
    private StringValue[] driverTeam = null;
    private FloatValue[] gaps = null;
    private BooleanProperty AbsTimes = new BooleanProperty("Use absolute times", false) ;
    private int NumOfPNG = 0;
    private String[] listPNG;
    
    
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
    
    private void initValues()
    {
        int maxNumItems = numVeh.getValue();
        
        if ( ( positions != null ) && ( positions.length == maxNumItems ) )
            return;
        
        gaps = new FloatValue[maxNumItems];
        positions = new IntValue[maxNumItems];
        driverNames = new StringValue[maxNumItems];
        driverTeam = new StringValue[maxNumItems];
        
        for(int i=0;i < maxNumItems;i++)
        { 
            positions[i] = new IntValue();
            driverNames[i] = new StringValue();
            driverTeam[i] = new StringValue();
            gaps[i] = new FloatValue();
        }
        
        
    }
    
    @Override
    protected void initialize( LiveGameData gameData, boolean isEditorMode, DrawnStringFactory drawnStringFactory, TextureImage2D texture, int width, int height )
    {
        int maxNumItems = numVeh.getValue();
        int fh = TextureImage2D.getStringHeight( "0%C", getFontProperty() );
        int rowHeight = height / (maxNumItems + 1);
        
        imgTrack.updateSize( width, rowHeight*95/100, isEditorMode );
        imgFirst.updateSize( width, rowHeight*95/100, isEditorMode );
        imgNames.updateSize( width, rowHeight*95/100, isEditorMode );
        texGapFirst = imgGapFirst.getImage().getScaledTextureImage( width*18/100, rowHeight*95/100, texGapFirst, isEditorMode );
        Color BlackFontColor = fontColor1.getColor();
        
        dsPos = new DrawnString[maxNumItems];
        dsName = new DrawnString[maxNumItems];
        dsTeam = new DrawnString[maxNumItems];
        dsTime = new DrawnString[maxNumItems];
        
        
        int top = ( rowHeight - fh ) / 2;
        
        dsTrack = drawnStringFactory.newDrawnString( "dsTrack", width*5/100, top, Alignment.LEFT, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor2.getColor() );
        top += rowHeight;
        
        for(int i=0;i < maxNumItems;i++)
        {
            dsPos[i] = drawnStringFactory.newDrawnString( "dsPos", width*7/200 + fontxposoffset.getValue(), top + fontyoffset.getValue(), Alignment.CENTER, false, f1_2011Font.getFont(), isFontAntiAliased(), BlackFontColor );
            dsName[i] = drawnStringFactory.newDrawnString( "dsName", width*8/100 + fontxnameoffset.getValue(), top + fontyoffset.getValue(), Alignment.LEFT, false, f1_2011Font.getFont(), isFontAntiAliased(), BlackFontColor );
            dsTeam[i] = drawnStringFactory.newDrawnString( "dsTeam", width*40/100 + fontxnameoffset.getValue(), top + fontyoffset.getValue(), Alignment.LEFT, false, f1_2011Font.getFont(), isFontAntiAliased(), BlackFontColor );
            dsTime[i] = drawnStringFactory.newDrawnString( "dsTime",  width*97/100 + fontxtimeoffset.getValue(), top + fontyoffset.getValue(), Alignment.RIGHT, false, f1_2011Font.getFont(), isFontAntiAliased(), BlackFontColor );
            
            top += rowHeight;
        }
        
        //Scan Country Folder
        
        File dir = new File(gameData.getFileSystem().getImagesFolder().toString() + "/prunn/PorscheSupercup/Countries");

        String[] children = dir.list();
        NumOfPNG = 0;
        listPNG = new String[children.length];
        
        for (int i=0; i < children.length; i++) 
        {
            // Get filename of file or directory
            String filename = children[i];
            
            if(filename.substring( filename.length()-4 ).toUpperCase().equals( ".PNG" ) )
            {
                //log(filename.substring( 0, filename.length()-4 ));
                listPNG[NumOfPNG] = filename.substring( 0, filename.length()-4 );
                NumOfPNG++;
            }    
        }
        
        

        //end of scan
    }
    
    @Override
    protected Boolean updateVisibility( LiveGameData gameData, boolean isEditorMode )
    {
        super.updateVisibility( gameData, isEditorMode );
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        int drawncars = Math.min( scoringInfo.getNumVehicles(), numVeh.getValue() );
        initValues();
        
        for(int i=0;i < drawncars;i++)
        { 
            VehicleScoringInfo vsi = scoringInfo.getVehicleScoringInfo( i );
            
            if(vsi != null)
            {
                positions[i].update( vsi.getPlace( false ) );
                driverNames[i].update( PrunnWidgetSetPorscheSupercup.ShortName( vsi.getDriverNameShort()) );
                
                driverTeam[i].update( PrunnWidgetSetPorscheSupercup.generateShortTeamNames( vsi.getVehicleInfo().getTeamName(), gameData.getFileSystem().getConfigFolder() ));
                if(driverTeam[i].getValue().length() > 8 && (driverTeam[i].getValue().substring( 0, 5 ).equals( "PMSCS" ) || driverTeam[i].getValue().substring( 0, 5 ).equals( "PCCAU" )))
                    driverTeam[i].update( driverTeam[i].getValue().substring( 8 ) );
                else if(driverTeam[i].getValue().length() > 7 && (driverTeam[i].getValue().substring( 0, 4 ).equals("PMSC") || driverTeam[i].getValue().substring( 0, 4 ).equals("PCCG") || driverTeam[i].getValue().substring( 0, 4 ).equals("PCCA")  || driverTeam[i].getValue().substring( 0, 4 ).equals("ALMS")))
                    driverTeam[i].update( driverTeam[i].getValue().substring( 7 ) );
                else if(driverTeam[i].getValue().length() > 6 && (driverTeam[i].getValue().substring( 0, 3 ).equals("LMS") || driverTeam[i].getValue().substring( 0, 3 ).equals("FIA")))
                    driverTeam[i].update( driverTeam[i].getValue().substring( 6 ) );
                else if(driverTeam[i].getValue().length() > 5 && (driverTeam[i].getValue().substring( 0, 2 ).equals("LM")))
                    driverTeam[i].update( driverTeam[i].getValue().substring( 5 ) );
                
                if(driverTeam[i].getValue().length() > MaxTeamLengh.getValue())
                    driverTeam[i].update( driverTeam[i].getValue().substring( 0, MaxTeamLengh.getValue() ) );
                  
                if(scoringInfo.getSessionType() != SessionType.RACE1)
                    gaps[i].update(vsi.getBestLapTime());
                else
                    gaps[i].update(vsi.getNumPitstopsMade());
                    
                    
                
            }
        }
        return true;
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        int maxNumItems = numVeh.getValue();
        int drawncars = Math.min( scoringInfo.getNumVehicles(), maxNumItems );
        int rowHeight = height / (maxNumItems + 1);
        
        texture.clear( imgTrack.getTexture(), offsetX, offsetY, false, null );
        texture.clear( imgFirst.getTexture(), offsetX, offsetY+rowHeight, false, null );
        
        texPos = imgPosFirst.getImage().getScaledTextureImage( width*6/100, rowHeight*95/100, texPos, isEditorMode );
        texture.drawImage( texPos, offsetX + width/150, offsetY+rowHeight, true, null );
        texture.drawImage( texGapFirst, offsetX + width*82/100, offsetY+rowHeight, true, null );
        
        if(scoringInfo.getVehicleScoringInfo(0).getVehicleInfo()!=null){
            String headquarters = scoringInfo.getVehicleScoringInfo(0).getVehicleInfo().getTeamHeadquarters().toUpperCase();
            for(int j=0; j < NumOfPNG; j++)
            {
                if(headquarters.length() >= listPNG[j].length() && headquarters.contains( listPNG[j].toUpperCase() )) 
                {
                    imgCountry.setValue("prunn/PorscheSupercup/Countries/" + listPNG[j] + ".png");
                    texCountry = imgCountry.getImage().getScaledTextureImage( width*5/100, rowHeight*61/100, texCountry, isEditorMode );
                    texture.drawImage( texCountry, offsetX + width*76/100, offsetY + rowHeight + rowHeight*20/100, true, null );
                    break;
                }
            }
        }
        texPos = imgPos.getImage().getScaledTextureImage( width*6/100, rowHeight*95/100, texPos, isEditorMode );
        
        
        for(int i=1;i < drawncars;i++)
        {
            texture.clear( imgNames.getTexture(), offsetX, offsetY+rowHeight*(i+1), false, null );
            if(scoringInfo.getVehicleScoringInfo( i ).getFinishStatus() != FinishStatus.DQ && scoringInfo.getVehicleScoringInfo( i ).getFinishStatus() != FinishStatus.DNF)
                texture.drawImage( texPos, offsetX + width/150, offsetY+rowHeight*(i+1), true, null );
            
            if(scoringInfo.getVehicleScoringInfo(i).getVehicleInfo()!=null){
                String headquarters = scoringInfo.getVehicleScoringInfo(i).getVehicleInfo().getTeamHeadquarters().toUpperCase();
                for(int j=0; j < NumOfPNG; j++)
                {
                    if(headquarters.length() >= listPNG[j].length() && headquarters.contains( listPNG[j].toUpperCase() )) 
                    {
                        imgCountry.setValue("prunn/PorscheSupercup/Countries/" + listPNG[j] + ".png");
                        texCountry = imgCountry.getImage().getScaledTextureImage( width*5/100, rowHeight*61/100, texCountry, isEditorMode );
                        texture.drawImage( texCountry, offsetX + width*76/100, offsetY + rowHeight*(i+1) + rowHeight*20/100, true, null );
                        break;
                    }
                }
            }   
        }
        
    }
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        int drawncars = Math.min( scoringInfo.getNumVehicles(), numVeh.getValue() );
        String SessionName;
        //one time for leader
        
        if ( needsCompleteRedraw || clock.c())
        {
            switch(scoringInfo.getSessionType())
            {
                case RACE1: case RACE2: case RACE3: case RACE4:
                    SessionName = "Race";
                    break;
                case QUALIFYING1: case QUALIFYING2: case QUALIFYING3:  case QUALIFYING4:
                    SessionName = "Qualifying";
                    break;
                case PRACTICE1:
                    SessionName = "Practice 1";
                    break;
                case PRACTICE2:
                    SessionName = "Practice 2";
                    break;
                case PRACTICE3:
                    SessionName = "Practice 3";
                    break;
                case PRACTICE4:
                    SessionName = "Practice 4";
                    break;
                case TEST_DAY:
                    SessionName = "Test";
                    break;
                case WARMUP:
                    SessionName = "Warmup";
                    break;
                default:
                    SessionName = "";
                    break;
                        
            }
            //" Session Classification"
            dsTrack.draw( offsetX, offsetY, SessionName + " Session", texture);
            //dsSession.draw( offsetX, offsetY, gameData.getTrackInfo().getTrackName(), texture);
            
            dsPos[0].draw( offsetX, offsetY, positions[0].getValueAsString(), texture );
            dsName[0].draw( offsetX, offsetY, driverNames[0].getValue(),fontColor2.getColor(), texture );
            dsTeam[0].draw( offsetX, offsetY, driverTeam[0].getValue(),fontColor2.getColor(), texture );
            
            if(scoringInfo.getSessionType() == SessionType.RACE1 )
            {
                String stops = ( scoringInfo.getLeadersVehicleScoringInfo().getNumPitstopsMade() > 1 ) ? " " + "Stops" : " " + "Stop";
                dsTime[0].draw( offsetX, offsetY, scoringInfo.getLeadersVehicleScoringInfo().getNumPitstopsMade() + stops, texture);
            }
            else
                if(gaps[0].isValid())
                    dsTime[0].draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(gaps[0].getValue() ) , texture);
                else
                    dsTime[0].draw( offsetX, offsetY, "No Time", texture);
        
        
            // the other guys"No Time Set"
            for(int i=1;i < drawncars;i++)
            { 
                if ( needsCompleteRedraw || clock.c() )
                {
                    dsPos[i].draw( offsetX, offsetY, positions[i].getValueAsString(), fontColor2.getColor(), texture );
                    dsName[i].draw( offsetX, offsetY,driverNames[i].getValue() , texture );  
                    dsTeam[i].draw( offsetX, offsetY, driverTeam[i].getValue(), texture );
                    if(scoringInfo.getVehicleScoringInfo( i ).getFinishStatus() == FinishStatus.DQ)
                        dsTime[i].draw( offsetX, offsetY, "DQ", texture);
                    else
                        if(scoringInfo.getSessionType() == SessionType.RACE1 )
                        {
                            if(scoringInfo.getVehicleScoringInfo( i ).getFinishStatus() == FinishStatus.DNF)
                                dsTime[i].draw( offsetX, offsetY, "DNF", texture); 
                            else
                            {
                                String stops = ( scoringInfo.getVehicleScoringInfo( i ).getNumPitstopsMade() > 1 ) ? " " + "Stops" : " " + "Stop";
                                dsTime[i].draw( offsetX, offsetY, scoringInfo.getVehicleScoringInfo( i ).getNumPitstopsMade() + stops, texture);
                            }
                        }
                        else
                            if(!gaps[i].isValid())
                                dsTime[i].draw( offsetX, offsetY, "No Time", texture);
                            else
                                if(AbsTimes.getValue() || !gaps[0].isValid())
                                   dsTime[i].draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(gaps[i].getValue() ), texture);
                                else
                                    dsTime[i].draw( offsetX, offsetY,"+ " + TimingUtil.getTimeAsLaptimeString(Math.abs( gaps[i].getValue() - gaps[0].getValue() )) , texture);
                 }
                
            }
        }
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        
        writer.writeProperty( f1_2011Font, "" );
        writer.writeProperty( fontColor1, "" );
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty( numVeh, "" );
        writer.writeProperty( AbsTimes, "" );
        writer.writeProperty( fontyoffset, "" );
        writer.writeProperty( fontxposoffset, "" );
        writer.writeProperty( fontxnameoffset, "" );
        writer.writeProperty( fontxtimeoffset, "" );
        writer.writeProperty( MaxTeamLengh, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        
        if ( loader.loadProperty( f1_2011Font ) );
        else if ( loader.loadProperty( fontColor1 ) );
        else if ( loader.loadProperty( fontColor2 ) );
        else if ( loader.loadProperty( numVeh ) );
        else if ( loader.loadProperty( AbsTimes ) );
        else if ( loader.loadProperty( fontyoffset ) );
        else if ( loader.loadProperty( fontxposoffset ) );
        else if ( loader.loadProperty( fontxnameoffset ) );
        else if ( loader.loadProperty( fontxtimeoffset ) );
        else if ( loader.loadProperty( MaxTeamLengh ) );
    }
    
    @Override
    protected void addFontPropertiesToContainer( PropertiesContainer propsCont, boolean forceAll )
    {
        propsCont.addGroup( "Colors and Fonts" );
        
        super.addFontPropertiesToContainer( propsCont, forceAll );
        propsCont.addProperty( f1_2011Font );
        propsCont.addProperty( fontColor1 );
        propsCont.addProperty( fontColor2 );
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Specific" );
        
        propsCont.addProperty( numVeh );
        propsCont.addProperty( AbsTimes );
        propsCont.addGroup( "Font Displacement" );
        propsCont.addProperty( fontyoffset );
        propsCont.addProperty( fontxposoffset );
        propsCont.addProperty( fontxnameoffset );
        propsCont.addProperty( fontxtimeoffset );
        propsCont.addProperty( MaxTeamLengh );
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
    
    public ResultMonitorWidget()
    {
        super( PrunnWidgetSetPorscheSupercup.INSTANCE, PrunnWidgetSetPorscheSupercup.WIDGET_PACKAGE_PorscheSupercup, 66.4f, 46.5f );
        
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetPorscheSupercup.GP2_FONT_NAME );
        getFontColorProperty().setColor( PrunnWidgetSetPorscheSupercup.FONT_COLOR1_NAME );
    }
}
