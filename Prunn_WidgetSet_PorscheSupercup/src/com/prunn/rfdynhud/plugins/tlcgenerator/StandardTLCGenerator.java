package com.prunn.rfdynhud.plugins.tlcgenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import net.ctdp.rfdynhud.util.RFDHLog;
import net.ctdp.rfdynhud.util.ThreeLetterCodeGenerator;

public class StandardTLCGenerator implements ThreeLetterCodeGenerator
{
   
    public String generateThreeLetterCode( String driverName )
    {
        //check if name is in ini file
        File ini;
        //ini = new File(GameFileSystem.INSTANCE.getConfigFolder(), "three_letter_codes.ini");
        ini = new File("D:/rFactor/Plugins/rfDynHUD/config/", "three_letter_codes.ini");
        
        if(ini.exists())
        {    
            try
            {
                int delimiter;
                String line;
                String fromFileTLC="";
                BufferedReader br = new BufferedReader( new FileReader( ini ) );
                
                while ((line = br.readLine()) != null)
                {   
                    delimiter = line.lastIndexOf( '=' );
                    
                    if(driverName.toUpperCase().equals(line.substring( 0, delimiter ).toUpperCase()))
                    {
                        fromFileTLC = line.substring( line.length() - 3, line.length() ).toUpperCase();
                        //RFDHLog.exception( "TLC:" + fromFileTLC ) ;
                        return fromFileTLC;
                    }
                           
                        
                }
                
            }
            catch ( Throwable t )
            {
               
            }
        }
        else
            RFDHLog.exception( "WARNING: No three_letter_codes.ini found." );
        
        
        if ( driverName.length() <= 3 )
        {
            return ( driverName.toUpperCase() );
        }
        
        int sp = driverName.lastIndexOf( ' ' );
        if ( sp == -1 )
        {
            return ( driverName.substring( 0, 3 ).toUpperCase() );
        }
        
        String tlc = driverName.substring( sp + 1, Math.min( sp + 4, driverName.length() ) ).toUpperCase();
        
        return ( tlc );
    }
    
    public String generateShortForm( String driverName )
    {
        int sp = driverName.lastIndexOf( ' ' );
        if ( sp == -1 )
        {
            return ( driverName );
        }
        
        String sf = driverName.charAt( 0 ) + ". " + driverName.substring( sp + 1 );
        
        return ( sf );
    }

    public String ShortName( String driverName )
    {
        int sp = driverName.lastIndexOf( ' ' );
        if ( sp == -1 )
        {
            return ( driverName );
        }
        
        String sf = driverName.charAt( 0 ) + " " + driverName.substring( sp + 1 );
        
        return ( sf );
    }
    
    public String ShortNameWTCC( String driverName )
    {
        int sp = driverName.lastIndexOf( ' ' );
        if ( sp == -1 )
        {
            return ( driverName );
        }
        
        String sf = driverName.substring( sp + 1 );
        
        return ( sf );
    }
    public String generateShortTeamNames( String teamName, java.io.File getConfigFolder)
    {
        //open ini file
        File ini;
        //ini = new File(gameData.getFileSystem().getConfigFolder()GameFileSystem.INSTANCE.getConfigFolder(), "short_teams_names.ini");
        ini = new File(getConfigFolder, "short_teams_names.ini");
        
        if(ini.exists())
        {    
            try
            {
                int delimiter;
                String line;
                String fromFileTeam="";
                BufferedReader br = new BufferedReader( new FileReader( ini ) );
                
                while ((line = br.readLine()) != null)
                {   
                    delimiter = line.lastIndexOf( '=' );
                    
                    if(teamName.toUpperCase().equals(line.substring( 0, delimiter ).toUpperCase()))
                    {
                        fromFileTeam = line.substring( delimiter+1, line.length() );
                        br.close();
                        return fromFileTeam;
                    }
                }
                br.close();
            }
            catch ( Throwable t )
            {
               
            }
        }
        else
            RFDHLog.exception( "WARNING: No short_teams_names.ini found." );
        
        //check if team matches
        //else return same thing or cut the end if its too long
        return ( teamName );
    }
    
    public String generateThreeLetterCode2( String driverName, java.io.File getConfigFolder )
    {
        
        //check if name is in ini file
        File ini;
        //ini = new File(gameData.getFileSystem().getConfigFolder(), "three_letter_codes.ini");
        ini = new File(getConfigFolder, "three_letter_codes.ini");
        
        
        if(ini.exists())
        {    
            try
            {
                int delimiter;
                String line;
                String fromFileTLC="";
                BufferedReader br = new BufferedReader( new FileReader( ini ) );
                
                while ((line = br.readLine()) != null)
                {   
                    delimiter = line.lastIndexOf( '=' );
                    
                    if(driverName.toUpperCase().equals(line.substring( 0, delimiter ).toUpperCase()))
                    {
                        fromFileTLC = line.substring( line.length() - 3, line.length() ).toUpperCase();
                        //RFDHLog.exception( "TLC:" + fromFileTLC ) ;
                        return fromFileTLC;
                    }
                           
                        
                }
                
            }
            catch ( Throwable t )
            {
               
            }
        }
        else
            RFDHLog.exception( "WARNING: No three_letter_codes.ini found." );
        
        
        if ( driverName.length() <= 3 )
        {
            return ( driverName.toUpperCase() );
        }
        
        int sp = driverName.lastIndexOf( ' ' );
        if ( sp == -1 )
        {
            return ( driverName.substring( 0, 3 ).toUpperCase() );
        }
        
        String tlc = driverName.substring( sp + 1, Math.min( sp + 4, driverName.length() ) ).toUpperCase();
        
        return ( tlc );
    }
    

    public String A1GPTeams( String teamName, java.io.File getConfigFolder )
    {
        //open ini file
        File ini;
        //ini = new File(gameData.getFileSystem().getConfigFolder(), "short_teams_names.ini");
        ini = new File(getConfigFolder, "a1gp_teamcodes.ini");
        
        if(ini.exists())
        {    
            try
            {
                int delimiter;
                String line;
                String fromFileTeam="";
                BufferedReader br = new BufferedReader( new FileReader( ini ) );
                
                while ((line = br.readLine()) != null)
                {   
                    delimiter = line.lastIndexOf( '=' );
                    
                    if(teamName.toUpperCase().equals(line.substring( 0, delimiter ).toUpperCase()))
                    {
                        fromFileTeam = line.substring( delimiter+1, line.length() );
                        br.close();
                        return fromFileTeam;
                    }
                }
                br.close();
            }
            catch ( Throwable t )
            {
               
            }
        }
        else
            RFDHLog.exception( "WARNING: No a1gp_teamcodes.ini found." );
        
        //check if team matches
        //else return same thing or cut the end if its too long
        return ( teamName.substring(teamName.length() - 3 ).toUpperCase());
    }
}
