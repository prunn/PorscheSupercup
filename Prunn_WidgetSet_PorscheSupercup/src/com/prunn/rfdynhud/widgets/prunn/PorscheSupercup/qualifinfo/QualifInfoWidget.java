package com.prunn.rfdynhud.widgets.prunn.PorscheSupercup.qualifinfo;

import java.awt.Font;
import java.io.File;
import java.io.IOException;

import com.prunn.rfdynhud.plugins.tlcgenerator.StandardTLCGenerator;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetPorscheSupercup;
import com.prunn.rfdynhud.widgets.prunn.PorscheSupercup.qtime.QualTimeWidget;

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
import net.ctdp.rfdynhud.util.NumberUtil;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.SubTextureCollector;
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


public class QualifInfoWidget extends Widget
{
    
    private DrawnString dsName = null;
    private DrawnString dsTeam = null;
    private final ImagePropertyWithTexture imgName = new ImagePropertyWithTexture( "imgName", "prunn/PorscheSupercup/data_neutral.png" );
    private final ImagePropertyWithTexture imgNameFirst = new ImagePropertyWithTexture( "imgName", "prunn/PorscheSupercup/data_title.png" );
    private TextureImage2D texCountry = null;
    private final ImagePropertyWithTexture imgCountry = new ImagePropertyWithTexture( "imgTime", "prunn/PorscheSupercup/flag.png" );
    private int NumOfPNG = 0;
    private String[] listPNG;
    
    protected final FontProperty GP2Font = new FontProperty("Main Font", PrunnWidgetSetPorscheSupercup.GP2_FONT_NAME);
    private final ColorProperty fontColor1 = new ColorProperty("fontColor1", PrunnWidgetSetPorscheSupercup.FONT_COLOR1_NAME);
    private final ColorProperty fontColor2 = new ColorProperty("fontColor2", PrunnWidgetSetPorscheSupercup.FONT_COLOR2_NAME);
    private IntProperty knockout = new IntProperty("Knockout position", 10);
    private final FloatValue sessionTime = new FloatValue(-1F, 0.1F);
    private IntProperty MaxTeamLengh = new IntProperty("Max Team Name Lenght", 20); 
    
    private final DelayProperty visibleTime;
    private long visibleEnd;
    private IntValue cveh = new IntValue();
    private BoolValue cpit = new BoolValue();
    StandardTLCGenerator gen = new StandardTLCGenerator();
    private StringValue team = new StringValue();
    private StringValue name = new StringValue();
    private StringValue pos = new StringValue();
    //private StringValue gap = new StringValue();
    //private StringValue time = new StringValue();
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    
    
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
        
        team.update( "" );
        name.update( "" ); 
        pos.update( "" );
        //gap.update( "" ); 
        //time.update( "" ); 
        
        int rowHeight = height / 2;
        int fh = TextureImage2D.getStringHeight( "0", GP2Font );
        
        imgName.updateSize(width, rowHeight*95/100, isEditorMode );
        imgNameFirst.updateSize(width, rowHeight*95/100, isEditorMode );
        
        int top1 = ( rowHeight - fh ) / 2;
        int top2 = rowHeight + ( rowHeight - fh ) / 2;
        dsName = drawnStringFactory.newDrawnString( "dsName", width*8/100, top1 + fontyoffset.getValue(), Alignment.LEFT, false, GP2Font.getFont(), isFontAntiAliased(), fontColor1.getColor() );
        dsTeam = drawnStringFactory.newDrawnString( "dsTeam", width*8/100, top2 + fontyoffset.getValue(), Alignment.LEFT, false, GP2Font.getFont(), isFontAntiAliased(), fontColor1.getColor() );
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
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        
        
        super.updateVisibility(gameData, isEditorMode);
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        cveh.update(scoringInfo.getViewedVehicleScoringInfo().getDriverId());
        cpit.update(scoringInfo.getViewedVehicleScoringInfo().isInPits());
        
        if(QualTimeWidget.visible())
            return false;
        
        if((cveh.hasChanged() || cpit.hasChanged()) && !isEditorMode)
        {
            forceCompleteRedraw(true);
            visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
            return true;
        }
        
        if(scoringInfo.getSessionNanos() < visibleEnd || cpit.getValue())
            return true;
        
        
        return false;	
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        VehicleScoringInfo currentcarinfos = gameData.getScoringInfo().getViewedVehicleScoringInfo();
        int rowHeight = height / 2;
         
        if(currentcarinfos.getPlace( false ) == 1)
            texture.clear( imgNameFirst.getTexture(), offsetX, offsetY, false, null );
        else
            texture.clear( imgName.getTexture(), offsetX, offsetY, false, null );
        
        texture.clear( imgName.getTexture(), offsetX, offsetY + rowHeight, false, null );
        //player
        
        if(currentcarinfos.isPlayer())
        {
            if(new File(gameData.getFileSystem().getImagesFolder() + "/prunn/PorscheSupercup/Nations/" + gameData.getProfileInfo().getNationality() + ".png").exists())
                imgCountry.setValue("prunn/PorscheSupercup/Nations/" + gameData.getProfileInfo().getNationality() + ".png");
            else
                imgCountry.setValue("prunn/PorscheSupercup/Nations/Alien.png");
            
            texCountry = imgCountry.getImage().getScaledTextureImage( width*14/100, rowHeight*61/100, texCountry, isEditorMode );
            texture.drawImage( texCountry, offsetX + width*78/100, offsetY + rowHeight*20/100, true, null );
        }
        //team
        for(int j=0; j < NumOfPNG; j++)
        {
            String headquarters = currentcarinfos.getVehicleInfo().getTeamHeadquarters().toUpperCase();
            if(headquarters.length() >= listPNG[j].length() && headquarters.contains( listPNG[j].toUpperCase() )) 
            {
                imgCountry.setValue("prunn/PorscheSupercup/Countries/" + listPNG[j] + ".png");
                texCountry = imgCountry.getImage().getScaledTextureImage( width*14/100, rowHeight*61/100, texCountry, isEditorMode );
                texture.drawImage( texCountry, offsetX + width*78/100, offsetY + rowHeight + rowHeight*20/100, true, null );
                break;
            }
        }
    }
    
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        ScoringInfo scoringInfo = gameData.getScoringInfo();
    	sessionTime.update(scoringInfo.getSessionTime());
    	
    	if ( needsCompleteRedraw )
        {
    	    VehicleScoringInfo currentcarinfos = gameData.getScoringInfo().getViewedVehicleScoringInfo();
            
        	name.update( gen.ShortName( currentcarinfos.getDriverNameShort() ) );
            pos.update( NumberUtil.formatFloat( currentcarinfos.getPlace(false), 0, true));
            team.update( gen.generateShortTeamNames( currentcarinfos.getVehicleInfo().getTeamName(), gameData.getFileSystem().getConfigFolder() ));
                
            if(team.getValue().length() > 8 && (team.getValue().substring( 0, 5 ).equals( "PMSCS" ) || team.getValue().substring( 0, 5 ).equals( "PCCAU" )))
                team.update( team.getValue().substring( 8 ) );
            else if(team.getValue().length() > 7 && (team.getValue().substring( 0, 4 ).equals("PMSC") || team.getValue().substring( 0, 4 ).equals("PCCG") || team.getValue().substring( 0, 4 ).equals("PCCA") || team.getValue().substring( 0, 4 ).equals("ALMS")))
                team.update( team.getValue().substring( 7 ) );
            else if(team.getValue().length() > 6 && (team.getValue().substring( 0, 3 ).equals("LMS") || team.getValue().substring( 0, 3 ).equals("FIA")))
                team.update( team.getValue().substring( 6 ) );
            else if(team.getValue().length() > 5 && (team.getValue().substring( 0, 2 ).equals("LM")))
                team.update( team.getValue().substring( 5 ) );
           
            if(team.getValue().length() > MaxTeamLengh.getValue())
                team.update( team.getValue().substring( 0, MaxTeamLengh.getValue() ) );
            
            /*if( currentcarinfos.getFastestLaptime() != null && currentcarinfos.getFastestLaptime().getLapTime() > 0 )
            {
                if(currentcarinfos.getPlace( false ) > 1)
                { 
                    time.update( TimingUtil.getTimeAsLaptimeString(currentcarinfos.getBestLapTime() ));
                    gap.update( "+ " +  TimingUtil.getTimeAsLaptimeString( currentcarinfos.getBestLapTime() - gameData.getScoringInfo().getLeadersVehicleScoringInfo().getBestLapTime() ));
                }
                else
                {
                    time.update("");
                    gap.update( TimingUtil.getTimeAsLaptimeString(currentcarinfos.getBestLapTime()));
                }
                    
            }
            else
            {
                time.update("");
                gap.update("");
            }*/
            dsName.draw( offsetX, offsetY, name.getValue(),(currentcarinfos.getPlace(false) == 1) ? fontColor2.getColor() : fontColor1.getColor(), texture );
            dsTeam.draw( offsetX, offsetY, team.getValue(), texture );
                
        }
         
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        writer.writeProperty( GP2Font, "" );
        writer.writeProperty( fontColor1, "" );
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty(visibleTime, "");
        writer.writeProperty( knockout, "" );
        writer.writeProperty( fontyoffset, "" );
        writer.writeProperty( MaxTeamLengh, "" );
        
        
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        if ( loader.loadProperty( GP2Font ) );
        else if ( loader.loadProperty( fontColor1 ) );
        else if ( loader.loadProperty( fontColor2 ) );
        else if( loader.loadProperty(visibleTime));
        else if ( loader.loadProperty( knockout ) );
        else if ( loader.loadProperty( fontyoffset ) );
        else if ( loader.loadProperty( MaxTeamLengh ) );
        
        
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Colors" );
        propsCont.addProperty( GP2Font );
        propsCont.addProperty( fontColor1 );
        propsCont.addProperty( fontColor2 );
        propsCont.addProperty(visibleTime);
        propsCont.addProperty( knockout );
        propsCont.addProperty( fontyoffset );
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
    
    public QualifInfoWidget()
    {
        super( PrunnWidgetSetPorscheSupercup.INSTANCE, PrunnWidgetSetPorscheSupercup.WIDGET_PACKAGE_PorscheSupercup, 40.0f, 7.0f );
        visibleTime = new DelayProperty("visibleTime", net.ctdp.rfdynhud.properties.DelayProperty.DisplayUnits.SECONDS, 6);
        visibleEnd = 0x8000000000000000L;
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetPorscheSupercup.GP2_FONT_NAME );
    }
    
}
