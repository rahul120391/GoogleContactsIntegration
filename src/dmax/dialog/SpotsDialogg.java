package dmax.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;

public class SpotsDialogg {
	public static AlertDialog dialog;
	public static void ShowDialog(Context context){
		if(dialog==null || !dialog.isShowing()){
			dialog=new dmax.dialog.SpotsDialog(context);
			dialog.setCancelable(false);
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(android.R.color.transparent)));
			dialog.show();
		}
	}
public static void DissmissDialog(){
	if(dialog!=null || dialog.isShowing()){
		dialog.dismiss();
	}
}
}
