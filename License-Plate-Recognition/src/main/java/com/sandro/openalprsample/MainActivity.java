package com.sandro.openalprsample;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.openalpr.Constants;
import org.openalpr.MicroServiceAccess;
import org.openalpr.model.Result;
import org.openalpr.Results;
import org.openalpr.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.openalpr.Configuration;

public class MainActivity extends AppCompatActivity {

    private static final String infoTextStr = "License Plate Recognition";
    private static final int REQUEST_IMAGE = 100;
    private static final int STORAGE=1;
    private String ANDROID_DATA_DIR;
    private static File destination;
    private TextView infoTextView;
    private TextView resultTextView;
    private TextView whereToProcessTextView;
    private EditText omsEditText;
    private ImageView imageView;
    private long resizeElapsedTime;
    MicroServiceAccess sa = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ANDROID_DATA_DIR = this.getApplicationInfo().dataDir;

        infoTextView = (TextView) findViewById(R.id.textView);
        whereToProcessTextView = (TextView) findViewById(R.id.textView_where_to_process);
        imageView = (ImageView) findViewById(R.id.imageView);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        omsEditText = (EditText) findViewById(R.id.editText_OMS_address);

        if (sa == null) {
            sa = new MicroServiceAccess();
        }
        // Below is to execute kernel server on the device.
        // Android limits network call to async operation in Main activity.
        new OpenAlprMicroServiceInit(sa).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Utils.copyAssetFolder(MainActivity.this.getAssets(), "runtime_data", ANDROID_DATA_DIR + File.separatorChar + "runtime_data");
        infoTextView.setText(infoTextStr);

        whereToProcessTextView.setText(Configuration.getWhereToProcess());
        omsEditText.setText(Configuration.getNatOmsString());
        View view = this.getCurrentFocus();
        closeSoftKeyboard(view);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultTextView.setText("");
                checkPermission();
            }
        });

        findViewById(R.id.button_device).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Configuration.WhereToProcess = Configuration.ProcessEntity.DEVICE;
                whereToProcessTextView.setText(Configuration.getWhereToProcess());
            }
        });
        findViewById(R.id.button_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Configuration.WhereToProcess = Configuration.ProcessEntity.SERVER;
                whereToProcessTextView.setText(Configuration.getWhereToProcess());
            }
        });
        findViewById(R.id.editText_OMS_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                omsEditText.setFocusable(true);
                omsEditText.setFocusableInTouchMode(true);
            }
        });

        findViewById(R.id.editText_OMS_address).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && (i == KeyEvent.KEYCODE_ENTER)) {
                    String inputText = omsEditText.getText().toString();
                    int splitterIdx = inputText.indexOf(':');
                    if (splitterIdx != -1) {
                        String address = inputText.substring(0, splitterIdx).trim();
                        String port = inputText.substring(splitterIdx + 1).trim();

                        try {
                            Integer.parseInt(port);
                        } catch (Exception e) {
                            resultTextView.setText("Parsing port information failed.\n" + Configuration.omsUsage);
                            closeSoftKeyboard(view);
                            return false;
                        }

                        Configuration.natOmsAddress = new String [] { address, port};
                        resultTextView.setText("OMS was successfully updated: " + Configuration.getNatOmsString());
                        closeSoftKeyboard(view);
                    } else {
                        resultTextView.setText(": is missing.\n" + Configuration.omsUsage);
                        closeSoftKeyboard(view);
                        return false;
                    }
                    closeSoftKeyboard(view);
                    return true;
                }

                return false;
            }
        });
    }

    private void closeSoftKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final long startTime = System.currentTimeMillis();

        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            try {

                final ProgressDialog progress
                        = ProgressDialog.show(this, "Loading", "Parsing result...", true);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 10;

                // Picasso requires permission.WRITE_EXTERNAL_STORAGE
                Picasso.with(MainActivity.this).load(destination).fit().centerCrop().into(imageView);
                resultTextView.setMovementMethod(new ScrollingMovementMethod());
                infoTextView.setText("Processing");

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        Results results = null;
                        try {
                            String imageFilePath = destination.getAbsolutePath();
                            final long resizeStartTime =  System.currentTimeMillis();
                            resizeImageIfNecessary(imageFilePath);
                            resizeElapsedTime = System.currentTimeMillis() - resizeStartTime;

                            Utils.copyAssetFolder
                                    (MainActivity.this.getAssets(), "runtime_data", ANDROID_DATA_DIR + File.separatorChar + "runtime_data");

                            results = new OpenAlprMicroService(
                                    ANDROID_DATA_DIR, "us", "", imageFilePath, Configuration.WhereToProcess, sa)
                                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();

                        } catch(Exception e) {
                            e.printStackTrace();
                        }

                        //Log.d("OPEN ALPR", result);
                        final Results finalResults = results;

                        try {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    long elapsedTime = System.currentTimeMillis() - startTime;

                                    if (finalResults == null || finalResults.getResults() == null || finalResults.getResults().size() == 0) {
                                        Toast.makeText(MainActivity.this, "It was not possible to detect the licence plate.", Toast.LENGTH_LONG).show();
                                        resultTextView.setText("It was not possible to detect the licence plate.");
                                    } else {
                                        String textToShow = "";
                                        textToShow += "Total processing time: " + String.format("%.2f", ((elapsedTime/1000.0)%60)) + " seconds\n";
                                        textToShow += "Image resize time: " + String.format("%.2f", ((resizeElapsedTime/1000.0)%60)) + " seconds\n";
                                        textToShow += "File upload time: " + String.format("%.2f", ((MicroServiceAccess.fileUploadTime/1000.0)%60)) + " seconds\n";
                                        textToShow += "Processing algorithm run time: " + String.format("%.2f", ((MicroServiceAccess.processingTime/1000.0)%60)) + " seconds\n";
                                        textToShow += "Number of plates found: " + finalResults.getResults().size() +"\n\n";

                                        for (Result result : finalResults.getResults()) {
                                            textToShow += "Plate: " + result.getPlate() + "  Found so far:" + result.getCount() + "\n";
                                        }

                                        resultTextView.setText(textToShow);
                                    }

                                    infoTextView.setText(infoTextStr);
                                }
                            });

                        } catch (final Exception e) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    resultTextView.setText("Unexpected exception occurred: + " + e);
                                }
                            });
                        }


                        progress.dismiss();
                    }
                });
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Resize the image saved by the camera if either width or height is bigger than the allowed maximum size and overwrite.
     */
    private void resizeImageIfNecessary(String imageFilePath) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap photo = BitmapFactory.decodeFile(imageFilePath, options);
        int bmOriginalWidth = photo.getWidth();
        int bmOriginalHeight = photo.getHeight();
        if (bmOriginalWidth <= Constants.maxSizeOfPictureToProcess && bmOriginalHeight <= Constants.maxSizeOfPictureToProcess) {
            // No need to reduce the size of original picture.
            return;
        }

        // Reduce the size of image to the maximum allowed size as defined in Constants.
        double reduceRatio = 0;
        if (bmOriginalWidth > bmOriginalHeight) {
            reduceRatio = 1.0 * Constants.maxSizeOfPictureToProcess / bmOriginalWidth;
        } else {
            reduceRatio = 1.0 * Constants.maxSizeOfPictureToProcess / bmOriginalHeight;
        }
        int newWidth = (int)((double) bmOriginalWidth * reduceRatio);
        int newHeight = (int)((double) bmOriginalHeight * reduceRatio);
        photo = Bitmap.createScaledBitmap(photo, newWidth, newHeight, false);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File f = new File(imageFilePath);
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkPermission() {
        List<String> permissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissions.isEmpty()) {
            Toast.makeText(this, "Storage access needed to manage the picture.", Toast.LENGTH_LONG).show();
            String[] params = permissions.toArray(new String[permissions.size()]);
            ActivityCompat.requestPermissions(this, params, STORAGE);
        } else { // We already have permissions, so handle as normal
            takePicture();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case STORAGE:{
                Map<String, Integer> perms = new HashMap<>();
                // Initial
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for WRITE_EXTERNAL_STORAGE
                Boolean storage = perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                if (storage) {
                    // permission was granted, yay!
                    takePicture();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Storage permission is needed to analyse the picture.", Toast.LENGTH_LONG).show();
                }
            }
            default:
                break;
        }
    }

    public String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());

        return df.format(date);
    }

    public void takePicture() {
        try {
            createImageFile();
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        if (destination != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra("outputX", 1280);
            intent.putExtra("outputY", 720);
            intent.putExtra("scale", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(MainActivity.this, getApplicationContext().getPackageName() + ".fileprovider", destination));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, REQUEST_IMAGE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (destination != null) {// Picasso does not seem to have an issue with a null value, but to be safe
            Picasso.with(MainActivity.this).load(destination).fit().centerCrop().into(imageView);
        }
    }

    private void createImageFile() {
        // Use a folder to store all results
        File folder = new File(Environment.getExternalStorageDirectory() + "/OpenALPR/");
        if (!folder.exists()) {
            folder.mkdir();
        }

        // Generate the path for the next photo
        String name = dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        destination = new File(folder, name + ".jpg");
    }

    /**
     * This method cannot be used in Linux as Android package is non-existent.
     * It should be removed but retaining in case this can be used to determine whether to run different JNI package.
     * @param context
     * @param packageName
     * @return
     */
    public boolean isAppRunningOnAndroid(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }

        return false;
    }
}
