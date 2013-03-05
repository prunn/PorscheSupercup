/**
 * Copyright (C) 2009-2010 Cars and Tracks Development Project (CTDP).
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * @author Prunn
 * copyright@Prunn2011
 * 
 */
package com.prunn.rfdynhud.widgets.prunn._util;

import net.ctdp.rfdynhud.util.FontUtils;
import net.ctdp.rfdynhud.widgets.WidgetsConfiguration;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;
import net.ctdp.rfdynhud.widgets.base.widget.WidgetPackage;
import net.ctdp.rfdynhud.widgets.base.widget.WidgetSet;

public class PrunnWidgetSetPorscheSupercup extends WidgetSet
{
    /*
     *  @author Prunn
     * copyright@Prunn2011
     */
    private PrunnWidgetSetPorscheSupercup()
    {
        super( composeVersion( 1, 0, 0 ) );
    }
    public static final PrunnWidgetSetPorscheSupercup INSTANCE = new PrunnWidgetSetPorscheSupercup();
    
    public static final WidgetPackage WIDGET_PACKAGE = new WidgetPackage( INSTANCE, "Prunn", INSTANCE.getIcon( "com/prunn/rfdynhud/widgets/prunn/prunn.png" ) );
    public static final WidgetPackage WIDGET_PACKAGE_PorscheSupercup = new WidgetPackage( INSTANCE, "Prunn/PSC", INSTANCE.getIcon( "com/prunn/rfdynhud/widgets/prunn/prunn.png" ), INSTANCE.getIcon( "com/prunn/rfdynhud/widgets/prunn/PorscheSupercup.png" ) );
    public static final WidgetPackage WIDGET_PACKAGE_PorscheSupercup_Race = new WidgetPackage( INSTANCE, "Prunn/PSC/Race", INSTANCE.getIcon( "com/prunn/rfdynhud/widgets/prunn/prunn.png" ), INSTANCE.getIcon( "com/prunn/rfdynhud/widgets/prunn/PorscheSupercup.png" ), INSTANCE.getIcon( "com/prunn/rfdynhud/widgets/prunn/PorscheSupercup.png" ) );
    
    public static final String FONT_COLOR1_NAME = "FontColor1";
    public static final String FONT_COLOR2_NAME = "FontColor2";
    public static final String FONT_COLOR3_NAME = "FontColor3";
    public static final String FONT_COLOR4_NAME = "FontColor4";
    public static final String GAP_FONT_COLOR1_NAME = "GapFontColor1";
    public static final String GAP_FONT_COLOR2_NAME = "GapFontColor2";
    public static final String GP2_FONT_NAME = "GP2Font";
    public static final String POS_FONT_NAME = "PosFont";
    public static final String MY_FONT_NAME = "MyFont";
    
    public String getDefaultNamedColorValue( String name )
    {
        if(name.equals("StandardFontColor"))
            return "#E9E9E9";
        if ( name.equals( FONT_COLOR1_NAME ) )
            return ( "#000000" );
        if ( name.equals( FONT_COLOR2_NAME ) )
            return ( "#EFEFEF" );
        if ( name.equals( FONT_COLOR3_NAME ) )
            return ( "#B21B16" );
        if ( name.equals( FONT_COLOR4_NAME ) )
            return ( "#A50000" );
        if ( name.equals( GAP_FONT_COLOR1_NAME ) )
            return ( "#FAFAFA" );
        if ( name.equals( GAP_FONT_COLOR2_NAME ) )
            return ( "#050505" );
        
        return ( null );
    }
    
    public String getDefaultNamedFontValue( String name )
    {
        if ( name.equals( GP2_FONT_NAME ) )
            return ( FontUtils.getFontString( "Dialog", 1, 24, true, true ) );
        if ( name.equals( POS_FONT_NAME ) )
            return ( FontUtils.getFontString( "Dialog", 1, 48, true, true ) );
        
        return ( null );
    }
    
    @SuppressWarnings( "unchecked" )
    public static final <W extends Widget> W getWidgetByClass( Class<W> clazz, boolean includeSubclasses, WidgetsConfiguration widgetsConfig )
    {
        int n = widgetsConfig.getNumWidgets();
        
        if ( includeSubclasses )
        {
            for ( int i = 0; i < n; i++ )
            {
                Widget w = widgetsConfig.getWidget( i );
                
                if ( clazz.isAssignableFrom( w.getClass() ) )
                    return ( (W)w );
            }
        }
        else
        {
            for ( int i = 0; i < n; i++ )
            {
                Widget w = widgetsConfig.getWidget( i );
                
                if ( clazz == w.getClass() )
                    return ( (W)w );
            }
        }
        
        return ( null );
    }
}
