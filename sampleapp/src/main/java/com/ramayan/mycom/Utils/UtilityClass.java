package com.ramayan.mycom.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;

import com.ramayan.mycom.R;


public class UtilityClass {

    private Activity context;

    public UtilityClass(Activity context) {
        this.context = context;
    }

    public static Dialog progressDialog = null;

//    public Dialog getProgressDialog() {
//        try {
//            if (progressDialog != null && progressDialog.isShowing())
//                progressDialog.dismiss();
//        } catch (Exception e) {
//            e.getMessage();
//        }
//        progressDialog = new Dialog(context, R.style.D1NoTitleDim);
//        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        Window window = progressDialog.getWindow();
//        if (window != null) {
//            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // This flag is required to set otherwise the setDimAmount method will not show any effect
//            window.setDimAmount(0.5f); //0 for no dim to 1 for full dim
//        }
//        if (progressDialog.getWindow() != null)
//            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        progressDialog.setContentView(R.layout.progress_bar);
//        progressDialog.setCancelable(false);
//        // }
//        return progressDialog;
//    }

    public boolean checkInternetConnection() {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            System.out.println("Internet Connection Not Present");
            return false;
        }
    }

//    public void DisclaimerDialog(Context context){
//        AlertDialog.Builder builder=new AlertDialog.Builder(context,R.style.MyDialogTheme);
//        builder.setCancelable(true);
//        builder.setIcon(R.mipmap.ic_launcher_round);
//        builder.setTitle(R.string.disclaimer);
//        builder.setMessage(Html.fromHtml(context.getString(R.string.app_name)+" "+context.getString(R.string.disclaimer_msg1)+" "+"<font color='#0000FF'><u>analytic.labs.kvr@gmail.com</u></font>"
//                +" "+context.getString(R.string.disclaimer_msg2)));
//        builder.setPositiveButton("OK",new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//                dialog.dismiss();
//            }
//        });
//        AlertDialog alert=builder.create();
//        alert.show();
//    }
//
//    public FancyAlertDialog getAlertDialogWithSingleButtons(final DialogButtonPressResponse response, String message) {
//        final FancyAlertDialog fancyalertdialog = new FancyAlertDialog.Builder(context)
//                .setTitle(context.getString(R.string.title))
//                .setMessage(message)
//                .setAnimation(Animation.POP)
//                .isCancellable(false)
//                .setNegativeButtonVisibility(false)
//                .OnPositiveClicked(new FancyAlertDialogListener() {
//                    @Override
//                    public void OnClick() {
//                        response.dialogOKButtonPressed();
//                    }
//                })
//                .build();
//        return fancyalertdialog;
//    }

//    public void RadioNotPlaying(Context context){
//        AlertDialog.Builder builder=new AlertDialog.Builder(context,R.style.MyDialogTheme);
//        builder.setCancelable(true);
//        builder.setIcon(R.mipmap.ic_launcher_round);
//        builder.setTitle("Heads Up!");
//        builder.setMessage(R.string.not_streaming);
//        builder.setPositiveButton("GOT IT! THANKS",new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//                dialog.dismiss();
//            }
//        });
//        AlertDialog alert=builder.create();
//        alert.show();
//    }

    public void RadioNotPlaying(Context context){
        AlertDialog.Builder builder=new AlertDialog.Builder(context, R.style.MyDialogTheme);
        builder.setCancelable(true);
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setTitle("Heads Up!");
        builder.setMessage(R.string.not_streaming);
        builder.setPositiveButton("GOT IT! THANKS",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }
}
