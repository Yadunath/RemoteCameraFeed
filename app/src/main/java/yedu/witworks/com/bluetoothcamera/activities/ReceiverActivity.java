package yedu.witworks.com.bluetoothcamera.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import yedu.witworks.com.bluetoothcamera.R;
import yedu.witworks.com.bluetoothcamera.manage.Info;
import yedu.witworks.com.bluetoothcamera.receiver.ServiceConnect;

public class ReceiverActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,DialogInterface.OnCancelListener,View.OnClickListener{

    private ServiceConnect mServiceConnect = null;

    /**
     * @param Layout
     */
    private ImageView iv_image = null;
    
    private ImageView takePictureButton;
    private ImageView galleryButton;
    
    private Boolean divConnected = false;

    private ProgressDialog waitDialog = null;
    
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
/*        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        
        setContentView(R.layout.activity_receiver);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        takePictureButton=(ImageView)findViewById(R.id.imageView);
        
        
        takePictureButton.setOnClickListener(this);
        
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(),R.color.receiver_bar)));
        getSupportActionBar().setTitle("Camera Feed");
        
        setServerAction();
        setWaitShow();
        
    }




    private void setServerAction() {
        mServiceConnect = new ServiceConnect(this, mHandler);
        mServiceConnect.acceptstart();
    }


    private void setWaitShow() {
        waitDialog = new ProgressDialog(ReceiverActivity.this);
        waitDialog.setMessage("connection wait...");
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.setCancelable(true);
        waitDialog.setOnCancelListener(this);
        waitDialog.show();
    }

    private void setpicPhotoShow() {
        waitDialog = new ProgressDialog(this);
        waitDialog.setMessage("Photo data wait...");
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.setCancelable(false);
        waitDialog.setOnCancelListener(this);
        waitDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void cancel() {
        if (mServiceConnect != null) {
            mServiceConnect.cancel();
            mServiceConnect = null;
        }
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Info.HANDLER_ACCEPT_INFO:
                    if (Info.HANDLER_ACCEPT_INFO_CONNECTEND == msg.arg1) {
                        Log.d("OUT", "ACCEPT_INFO_CONNECTEND");

                        waitDialog.dismiss();
                        setpicPhotoShow();
                        divConnected = true;

                    }
                    break;

                case Info.HANDLER_DEVICE_INFO:
                    if (Info.HANDLER_DEVICE_INFO_LIST == msg.arg1) {

                    }
                    break;

                case Info.HANDLER_READ_INFO:
                    if (Info.HANDLER_READ_INFO_END == msg.arg1) {
                        if (waitDialog != null) {
                            waitDialog.dismiss();
                            waitDialog = null;
                        }

                        byte[] image = (byte[]) msg.obj;
                        Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
                        BitmapDrawable ob = new BitmapDrawable(getResources(), bmp);
                        iv_image.setBackground(ob);
                    }
                    break;

                case Info.HANDLER_DELETE_MESSAGE:
                    finishAcitivity();
                    break;
            }
        }
    };

    private void finishAcitivity() {
        cancel();
        finish();
    }
    public static Bitmap getBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    
    private void showFinishMessage() {

        AlertDialog.Builder finishDia = new AlertDialog.Builder(this);
        finishDia.setTitle("End processing");
        finishDia.setMessage("Do you want to exit ");
        finishDia.setPositiveButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!divConnected) {
                            setWaitShow();
                        }
                    }
                });
        finishDia.setNegativeButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAcitivity();
                    }
                });

        AlertDialog adFinish = finishDia.create();
        adFinish.show();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (e.getAction() == KeyEvent.ACTION_UP) {
                if (divConnected) {
//                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                } else {
                    showFinishMessage();
                }
                return false;
            }
        }
        return super.dispatchKeyEvent(e);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        showFinishMessage();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imageView:
                saveImage();
                break;
      /*      case R.id.imageView2:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        "content://media/internal/images/media"));
                startActivity(intent);
                break;*/
        }
    }
    public void saveImage()
    {
        iv_image.buildDrawingCache();
        Bitmap bm=iv_image.getDrawingCache();
        OutputStream fOut = null;
        Uri outputFileUri;
        try {
            String path = Environment.getExternalStorageDirectory().toString();
            File folder1=new File(path+"/bluetoothfeed/");
            if (!folder1.exists())
            {
                folder1.mkdirs();
            }
            File sdImageMainDirectory = new File(path+"/bluetoothfeed/", "myPicName.jpg");
            outputFileUri = Uri.fromFile(sdImageMainDirectory);
            fOut = new FileOutputStream(sdImageMainDirectory);
        } catch (Exception e) {
            Toast.makeText(this, "Error occured. Please try again later.",
                    Toast.LENGTH_SHORT).show();
        }

        try {
            bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
        }
        Toast.makeText(ReceiverActivity.this,"Image is saved in bluetoothfeed folder in sd card",Toast.LENGTH_LONG).show();
    }
}
