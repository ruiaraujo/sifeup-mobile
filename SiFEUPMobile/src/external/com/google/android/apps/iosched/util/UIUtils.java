/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package external.com.google.android.apps.iosched.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.Time;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.widget.TextView;

import java.util.Formatter;
import java.util.List;
import java.util.TimeZone;

import pt.up.fe.mobile.R;

/**
 * An assortment of UI helpers.
 */
public class UIUtils {
    /** {@link Formatter} used for formatting time block. */

    private static StyleSpan sBoldSpan = new StyleSpan(Typeface.BOLD);

    
    public static String TIME_REFERENCE = TimeZone.getDefault().getDisplayName();

    /**
     * Populate the given {@link TextView} with the requested text, formatting
     * through {@link Html#fromHtml(String)} when applicable. Also sets
     * {@link TextView#setMovementMethod} so inline links are handled.
     */
    public static void setTextMaybeHtml(TextView view, String text) {
        if (TextUtils.isEmpty(text)) {
            view.setText("");
            return;
        }
        if (text.contains("<") && text.contains(">")) {
            view.setText(Html.fromHtml(text));
            view.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            view.setText(text);
        }
    }

    public static void setSessionTitleColor(long blockStart, long blockEnd, TextView title,
            TextView subtitle) {
        int colorId = R.color.body_text_1;
        int subColorId = R.color.body_text_2;


        final Resources res = title.getResources();
        title.setTextColor(res.getColor(colorId));
        subtitle.setTextColor(res.getColor(subColorId));
    }

    /**
     * Given a snippet string with matching segments surrounded by curly
     * braces, turn those areas into bold spans, removing the curly braces.
     */
    public static Spannable buildStyledSnippet(String snippet) {
        final SpannableStringBuilder builder = new SpannableStringBuilder(snippet);

        // Walk through string, inserting bold snippet spans
        int startIndex = -1, endIndex = -1, delta = 0;
        while ((startIndex = snippet.indexOf('{', endIndex)) != -1) {
            endIndex = snippet.indexOf('}', startIndex);

            // Remove braces from both sides
            builder.delete(startIndex - delta, startIndex - delta + 1);
            builder.delete(endIndex - delta - 1, endIndex - delta);

            // Insert bold style
            builder.setSpan(sBoldSpan, startIndex - delta, endIndex - delta - 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            delta += 2;
        }

        return builder;
    }


    private static final int BRIGHTNESS_THRESHOLD = 130;

    /**
     * Calculate whether a color is light or dark, based on a commonly known
     * brightness formula.
     *
     * @see {@literal http://en.wikipedia.org/wiki/HSV_color_space%23Lightness}
     */
    public static boolean isColorDark(int color) {
        return ((30 * Color.red(color) +
                59 * Color.green(color) +
                11 * Color.blue(color)) / 100) <= BRIGHTNESS_THRESHOLD;
    }

    public static long getCurrentTime(boolean utc) {
    	long now = System.currentTimeMillis (); //Gets current local time in ms
        if ( !utc )
        {
	        TimeZone local_tz = TimeZone.getDefault();  //Gets current local TZ of phone
	        long tz_offset_gmt = local_tz.getOffset(System.currentTimeMillis ())/3600000; // Get Offset in ms, divide by 3600000
	        now += tz_offset_gmt*3600000;
        }
        return now;
    }
    
    public static long convertToUtc(long now) {
    	TimeZone local_tz = TimeZone.getDefault();  //Gets current local TZ of phone
        long tz_offset_gmt = local_tz.getOffset(System.currentTimeMillis ())/3600000; // Get Offset in ms, divide by 3600000
        now -= tz_offset_gmt*3600000;
        
        return now;
    }

    public static Drawable getIconForIntent(final Context context, Intent i) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
        if (infos.size() > 0) {
            return infos.get(0).loadIcon(pm);
        }
        return null;
    }
    
    
    public static int secondYearOfSchoolYear(){
    	Time nowT = new Time(TIME_REFERENCE);
    	nowT.setToNow();
    	nowT.normalize(false);
    	if ( nowT.month >= 8 )
    		return nowT.year+1;
    	return nowT.year;
    }
    
    public static int secondYearOfSchoolYear(long millis){
    	Time nowT = new Time();
    	nowT.set(millis);
    	nowT.normalize(false);
    	if ( nowT.month >= 8 )
    		return nowT.year+1;
    	return nowT.year;
    }
}
