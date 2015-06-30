package com.firezenk.progressbar;



import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.example.googlecontactsintegration.R;
import com.firezenk.progressbar.FZProgressBar.Mode;

public class FzDialog {
	public static Dialog dialog;
	public static FZProgressBar bar;
	public static void ShowDialog(Context context){
		if(dialog==null){
			dialog=new Dialog(context);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			View v=LayoutInflater.from(context).inflate(R.layout.layout_fzprogress,null);
			dialog.setCancelable(false);
			dialog.setContentView(v);
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			bar=(FZProgressBar)dialog.findViewById(R.id.progressbar);
			bar.animation_config(2, 10);
	        int[] colors1 = {Color.MAGENTA,Color.TRANSPARENT};
	        bar.bar_config(1, 0, 0,Color.TRANSPARENT, colors1);
			bar.animation_start(Mode.INDETERMINATE);
			dialog.show();
		}
		
	}
	public static void dismissdialog()
	{
	   if(dialog!=null || dialog.isShowing())
	   {
		   dialog.dismiss();
	   }
	}
}
