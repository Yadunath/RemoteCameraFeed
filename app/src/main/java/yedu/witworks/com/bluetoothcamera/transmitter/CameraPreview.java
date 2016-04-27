package yedu.witworks.com.bluetoothcamera.transmitter;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import yedu.witworks.com.bluetoothcamera.manage.Info;

/**
 * Created by yedunath on 27/4/16.
 */
public class CameraPreview extends SurfaceView implements
        SurfaceHolder.Callback, Camera.PreviewCallback {

    private Camera mCam = null;
    private Handler mHandler = null;

   
    public CameraPreview(Context context, Camera cam, Handler mHandler) {
        super(context);

        this.mHandler = mHandler;
        this.mCam = cam;
        mCam.setDisplayOrientation(90);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("OUT", "surfaceCreated ");
        try {
            // The camera instance , set the image display destination
            mCam.setPreviewDisplay(holder);
            mCam.setPreviewCallback(this);
            // Preview start
            mCam.startPreview();
        } catch (IOException e) {
            //
            Log.d("OUT", "surfaceCreated error ", e);
        }
    }

    public void stopCamera() {
        if (mCam != null) {

            mCam.stopPreview();
            mCam.release();
            mCam = null;
        }
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (Info.sendImage) {
            Info.sendImage = false;
            if (data != null) {
                
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = parameters.getPreviewSize();

                
                YuvImage image = new YuvImage(data,
                        parameters.getPreviewFormat(), size.width, size.height,
                        null);

                
                String filePath = Environment.getExternalStorageDirectory()
                        + "/camera_data.jpg";

                File file = new File(filePath);
                FileOutputStream filecon;
                try {
                    filecon = new FileOutputStream(file);
                    image.compressToJpeg(
                            new Rect(0, 0, image.getWidth(), image.getHeight()),
                            90, filecon);
                    mHandler.obtainMessage(Info.HANDLER_CAMERA_INFO,
                            Info.HANDLER_CAMERA_INFO_SAVE_DATA, 0)
                            .sendToTarget();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}