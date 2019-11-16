package com.imagelistview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.imagelistview.adapter.CustomImageAdapter;
import com.imagelistview.getset.GetSet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    CustomImageAdapter customImageAdapter;
    ArrayList<GetSet> getSets;
    ListView listView;

    // Temp save listItem position
    int position;

    int imageCount;
    String imageTempName;
    String[] imageFor;
    Bitmap bitmap;
    // declaration and initialise ArrayList
    ArrayList<String>
            img_array = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.captureList);
        getSets = new ArrayList<GetSet>();
        imageFor = getResources().getStringArray(R.array.imageFor);
        for (int i = 0; i < 3; i++) {

            GetSet inflate = new GetSet();
            // Global Values
            inflate.setUid(String.valueOf(i));

            inflate.setLabel("Image");
            inflate.setHaveImage(false);
            inflate.setSubtext(imageFor[i]);
            inflate.setStatus(true);

            getSets.add(inflate);
        }
        customImageAdapter = new CustomImageAdapter(getSets, MainActivity.this);
        listView.setAdapter(customImageAdapter);
    }


    /**
     * Capture Image and save into database
     */

    public void captureImage(int pos, String imageName) {
        position = pos;
        imageTempName = imageName;
      //  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 100);
    }

    /**
     * Set capture image to database and set to image preview
     *
     * @param data
     */
    private void onCaptureImageResult(Intent data) {
        Uri uri = data.getData();
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = getImageUri(getApplicationContext(), bitmap, imageTempName);
            String picturePath = getRealPathFromURI(tempUri);
            customImageAdapter.setImageInItem(position, bitmap, picturePath);
            getStringImage(bitmap);

            Toast.makeText(this, Arrays.toString(imageFor), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Bundle extras = data.getExtras();
//        Bitmap imageBitmap = (Bitmap) extras.get("data");




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
                onCaptureImageResult(data);

        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage, String imageName) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, imageName, null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    //upload gambar volley
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
       // img_array.add("f");
        img_array.add("\n -----img----"+encodedImage);
        String[] str = GetStringArray(img_array);
        Log.i("img_array", "getStringImage: "+ Arrays.toString(str));
        return encodedImage;
    }

    // Function to convert ArrayList<String> to String[]
    public static String[] GetStringArray(ArrayList<String> arr)
    {

        // declaration and initialise String Array
        String str[] = new String[arr.size()];

        // ArrayList to Array Conversion
        for (int j = 0; j < arr.size(); j++) {

            // Assign each value to String array
            str[j] = arr.get(j);
        }

        return str;
    }

    public Bitmap convertSrcToBitmap(String imageSrc) {
        Bitmap myBitmap = null;
        File imgFile = new File(imageSrc);
        if (imgFile.exists()) {
            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return myBitmap;
    }

}
