/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * aapt tool from the resource data it found.  It
 * should not be modified by hand.
 */

package com.bitizen.counterswipe;

public final class R {
    public static final class attr {
        /** <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
         */
        public static final int buttonBarButtonStyle=0x7f010001;
        /** <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
         */
        public static final int buttonBarStyle=0x7f010000;
    }
    public static final class color {
        public static final int black_overlay=0x7f040000;
    }
    public static final class dimen {
        public static final int padding_large=0x7f050002;
        public static final int padding_medium=0x7f050001;
        public static final int padding_small=0x7f050000;
    }
    public static final class drawable {
        public static final int crosshairs=0x7f020000;
        public static final int ic_launcher=0x7f020001;
    }
    public static final class id {
        public static final int btnHost=0x7f09000a;
        public static final int btnJoin=0x7f090009;
        public static final int btnLeave=0x7f09000f;
        public static final int btnNext=0x7f090002;
        public static final int btnReplay=0x7f09000e;
        public static final int btnTeamA=0x7f090010;
        public static final int btnTeamB=0x7f090011;
        public static final int camera_preview=0x7f090003;
        public static final int etUsername=0x7f090008;
        public static final int image_view_captured_image=0x7f09000c;
        public static final int mi_begin=0x7f090012;
        public static final int mi_kick=0x7f090014;
        public static final int mi_quit=0x7f090013;
        public static final int mi_ready=0x7f090015;
        public static final int rbHOpponent1=0x7f090005;
        public static final int rbHTeammate1=0x7f090004;
        public static final int rbMatch1=0x7f090001;
        public static final int rbPlayerA1=0x7f090006;
        public static final int rbPlayerB1=0x7f090007;
        public static final int text_view_camera_description=0x7f09000b;
        public static final int tvResults=0x7f09000d;
        public static final int tvUsernameInAM=0x7f090000;
    }
    public static final class layout {
        public static final int activity_availablematches=0x7f030000;
        public static final int activity_camera=0x7f030001;
        public static final int activity_hostlobby=0x7f030002;
        public static final int activity_lobby=0x7f030003;
        public static final int activity_login=0x7f030004;
        public static final int activity_precamera=0x7f030005;
        public static final int activity_results=0x7f030006;
        public static final int activity_teamselect=0x7f030007;
    }
    public static final class menu {
        public static final int menu_host=0x7f080000;
        public static final int menu_player=0x7f080001;
    }
    public static final class string {
        public static final int app_name=0x7f060000;
        public static final int begin_item=0x7f060001;
        public static final int kick_item=0x7f060003;
        public static final int quit_item=0x7f060002;
        public static final int ready_item=0x7f060005;
        public static final int switchTeam_item=0x7f060004;
    }
    public static final class style {
        /** 
        Base application theme, dependent on API level. This theme is replaced
        by AppBaseTheme from res/values-vXX/styles.xml on newer devices.
    

            Theme customizations available in newer API levels can go in
            res/values-vXX/styles.xml, while customizations related to
            backward-compatibility can go here.
        

        Base application theme for API 11+. This theme completely replaces
        AppBaseTheme from res/values/styles.xml on API 11+ devices.
    
 API 11 theme customizations can go here. 

        Base application theme for API 14+. This theme completely replaces
        AppBaseTheme from BOTH res/values/styles.xml and
        res/values-v11/styles.xml on API 14+ devices.
    
 API 14 theme customizations can go here. 
         */
        public static final int AppBaseTheme=0x7f070000;
        /**  Application theme. 
 All customizations that are NOT specific to a particular API-level can go here. 
         */
        public static final int AppTheme=0x7f070001;
    }
    public static final class styleable {
        /** 
         Declare custom theme attributes that allow changing which styles are
         used for button bars depending on the API level.
         ?android:attr/buttonBarStyle is new as of API 11 so this is
         necessary to support previous API levels.
    
           <p>Includes the following attributes:</p>
           <table>
           <colgroup align="left" />
           <colgroup align="left" />
           <tr><th>Attribute</th><th>Description</th></tr>
           <tr><td><code>{@link #ButtonBarContainerTheme_buttonBarButtonStyle com.bitizen.counterswipe:buttonBarButtonStyle}</code></td><td></td></tr>
           <tr><td><code>{@link #ButtonBarContainerTheme_buttonBarStyle com.bitizen.counterswipe:buttonBarStyle}</code></td><td></td></tr>
           </table>
           @see #ButtonBarContainerTheme_buttonBarButtonStyle
           @see #ButtonBarContainerTheme_buttonBarStyle
         */
        public static final int[] ButtonBarContainerTheme = {
            0x7f010000, 0x7f010001
        };
        /**
          <p>This symbol is the offset where the {@link com.bitizen.counterswipe.R.attr#buttonBarButtonStyle}
          attribute's value can be found in the {@link #ButtonBarContainerTheme} array.


          <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
          @attr name android:buttonBarButtonStyle
        */
        public static final int ButtonBarContainerTheme_buttonBarButtonStyle = 1;
        /**
          <p>This symbol is the offset where the {@link com.bitizen.counterswipe.R.attr#buttonBarStyle}
          attribute's value can be found in the {@link #ButtonBarContainerTheme} array.


          <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
          @attr name android:buttonBarStyle
        */
        public static final int ButtonBarContainerTheme_buttonBarStyle = 0;
    };
}
