package com.es.hello.chat.consts;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.TextView;

public class FontTypeUtils
{

    public static Typeface getFont_Myriad_Pro_Regular(Context ctx)
    {

	return Typeface.createFromAsset(ctx.getAssets(), "Myriad Pro Regular.ttf");
    }

    public static Typeface getFont_Myriad_Pro_Conden_Italic(Context ctx)
    {

	return Typeface.createFromAsset(ctx.getAssets(), "myriadwebpro-condenseditalic.ttf");
    }

    public static Typeface getFont_Myriad_Pro_Conden(Context ctx)
    {

	return Typeface.createFromAsset(ctx.getAssets(), "MyriadPro-Condensed.ttf");
    }

    public static void setFontForTittleBar(Activity activity, Context ctx)
    {

	int padding_in_dp = 3;
	final float scale = ctx.getResources().getDisplayMetrics().density;
	int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

	int titleId = ctx.getResources().getIdentifier("action_bar_title", "id", "android");
	TextView yourTextView = (TextView) activity.findViewById(titleId);
	yourTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 29);
	yourTextView.setTextColor(Color.parseColor("#474749"));
	yourTextView.setPadding(0, padding_in_px, 0, 0);
	// yourTextView.setTextAppearance(ctx,
	// android.R.style.TextAppearance_Holo_Widget_ActionBar_Title);
	yourTextView.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden(ctx), Typeface.BOLD);
    }

    public static void setFontForTittleBar_CHAT_PAGE_ONLY(Activity activity, Context ctx)
    {

	int titleId = ctx.getResources().getIdentifier("action_bar_title", "id", "android");
	TextView yourTextView = (TextView) activity.findViewById(titleId);
	yourTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21.5f);
	// yourTextView.setTextAppearance(ctx,
	// android.R.style.TextAppearance_Holo_Widget_ActionBar_Title);
	yourTextView.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Regular(ctx), Typeface.BOLD);
    }

    public static void setFontFor_SUB_TittleBar_CHAT_PAGE_ONLY(Activity activity, Context ctx)
    {

	int titleId = ctx.getResources().getIdentifier("action_bar_subtitle", "id", "android");
	TextView yourTextView = (TextView) activity.findViewById(titleId);
	yourTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17.25f);
	yourTextView.setTextColor(Color.parseColor("#666666"));
	yourTextView.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden(ctx), Typeface.BOLD);
    }

}
