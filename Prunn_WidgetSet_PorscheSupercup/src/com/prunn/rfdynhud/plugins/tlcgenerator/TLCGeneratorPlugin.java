package com.prunn.rfdynhud.plugins.tlcgenerator;

import java.io.File;

import net.ctdp.rfdynhud.gamedata.GameEventsPlugin;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.render.WidgetsManager;
import net.ctdp.rfdynhud.util.ThreeLetterCodeGenerator;

public class TLCGeneratorPlugin extends GameEventsPlugin// implements GameEventsListener, ScoringInfoUpdateListener, WidgetsRenderListener
{
    private final StandardTLCGenerator standardTLCGenerator = new StandardTLCGenerator();
    private ThreeLetterCodeGenerator originalTLCGen = null;
    
    public TLCGeneratorPlugin( File baseFolder )
    {
        super( "TLCGeneratorPlugin", baseFolder );
    }
    
    @Override
    public void onPluginStarted( LiveGameData gameData, boolean isEditorMode, WidgetsManager widgetsManager )
    {
        originalTLCGen = gameData.getScoringInfo().getThreeLetterCodeGenerator();
        gameData.getScoringInfo().setThreeLetterCodeGenerator( standardTLCGenerator );
    }
    
    @Override
    public void onPluginShutdown( LiveGameData gameData, boolean isEditorMode, WidgetsManager widgetsManager )
    {
        gameData.getScoringInfo().setThreeLetterCodeGenerator( originalTLCGen );
    }
}
