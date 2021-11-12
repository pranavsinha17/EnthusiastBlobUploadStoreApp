package com.example.enthusiasticblobuploads;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.File;
import java.io.FileInputStream;

import br.com.onimur.handlepathoz.HandlePathOz;
import br.com.onimur.handlepathoz.HandlePathOzListener;
import br.com.onimur.handlepathoz.model.PathOz;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphImageButton;

import static java.lang.Thread.sleep;

public class BlobUploadActivity extends AppCompatActivity implements HandlePathOzListener.SingleUri {

    private LinearLayout welcomeLayout;
    private Animation animation_fade_in;

    private NeumorphImageButton btnImgLoad;
    private NeumorphImageButton btnPdfLoad;
    private NeumorphButton btnUploadToAzure;
    private Boolean isPictureUploaded;


    private String imgPath = null;
    private String pdfPath = null;
    private static final String storageConnectionString = "YOUR_AZURE_STORAGE_CONNECTION_STRING";
    private static final String storageContainer = "YOUR_STORAGE_CONTAINER_NAME";
    private String documentName = "";
    private HandlePathOz handlePathOz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blob_upload);
        Utils.blackIconStatusBar(BlobUploadActivity.this, R.color.light_backgroud);
        welcomeLayout = findViewById(R.id.layout_invisible);
        animation_fade_in = AnimationUtils.loadAnimation(BlobUploadActivity.this, R.anim.fade_in);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                welcomeLayout.setVisibility(View.VISIBLE);
                welcomeLayout.setAnimation(animation_fade_in);
            }
        }, 2000);


        btnImgLoad = findViewById(R.id.buttonLoadPicture);
        btnPdfLoad = findViewById(R.id.buttonLoadPdf);
        btnUploadToAzure = findViewById(R.id.uploadToAzure);
        isPictureUploaded = false;
        handlePathOz = new HandlePathOz(this, this);
        Boolean isFirstTime;

        SharedPreferences app_preferences = PreferenceManager
                .getDefaultSharedPreferences(BlobUploadActivity.this);

        SharedPreferences.Editor editor = app_preferences.edit();

        isFirstTime = app_preferences.getBoolean("isFirstTimeGuide", true);

        if (isFirstTime) {

//implement your first time logic
            new TapTargetSequence(this)
                    .targets(

                            TapTarget.forView(btnImgLoad,"Image Picker","Click on Image Picker if file format is jpg.")
                                    .outerCircleColor(R.color.outer_circle_backgroud)
                                    .outerCircleAlpha(0.96f)
                                    .targetCircleColor(R.color.circle_backgroud)
                                    .titleTextSize(20)
                                    .titleTextColor(R.color.text_color)
                                    .descriptionTextSize(10)
                                    .descriptionTextColor(R.color.text_color)
                                    .textColor(R.color.text_color)
                                    .textTypeface(Typeface.SANS_SERIF)
                                    .dimColor(R.color.light_backgroud)
                                    .drawShadow(true)
                                    .cancelable(false)
                                    .tintTarget(true)
                                    .transparentTarget(true)
                                    .targetRadius(60),
                            TapTarget.forView(btnPdfLoad,"PDF Picker","Click on PDF Picker if file format is PDF.")
                                    .outerCircleColor(R.color.outer_circle_backgroud)
                                    .outerCircleAlpha(0.96f)
                                    .targetCircleColor(R.color.circle_backgroud)
                                    .titleTextSize(20)
                                    .titleTextColor(R.color.text_color)
                                    .descriptionTextSize(10)
                                    .descriptionTextColor(R.color.text_color)
                                    .textColor(R.color.text_color)
                                    .textTypeface(Typeface.SANS_SERIF)
                                    .dimColor(R.color.light_backgroud)
                                    .drawShadow(true)
                                    .cancelable(false)
                                    .tintTarget(true)
                                    .transparentTarget(true)
                                    .targetRadius(60),
                            TapTarget.forView(btnUploadToAzure,"Uploader","This will upload your data to Azure Blob storage. If want to upload both image and pdf, first select PDF and upload then select image and upload")
                                    .outerCircleColor(R.color.outer_circle_backgroud)
                                    .outerCircleAlpha(0.96f)
                                    .targetCircleColor(R.color.circle_backgroud)
                                    .titleTextSize(20)
                                    .titleTextColor(R.color.text_color)
                                    .descriptionTextSize(10)
                                    .descriptionTextColor(R.color.text_color)
                                    .textColor(R.color.text_color)
                                    .textTypeface(Typeface.SANS_SERIF)
                                    .dimColor(R.color.light_backgroud)
                                    .drawShadow(true)
                                    .cancelable(false)
                                    .tintTarget(true)
                                    .transparentTarget(true)
                                    .targetRadius(60)).listener(new TapTargetSequence.Listener() {
                @Override
                public void onSequenceFinish() {



                }

                @Override
                public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    Toast.makeText(BlobUploadActivity.this,"GREAT!",Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onSequenceCanceled(TapTarget lastTarget) {

                }
            }).start();


            editor.putBoolean("isFirstTimeGuide", false);
            editor.commit();

        }

        btnImgLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(BlobUploadActivity.this)
                        .crop()
                        .compress(1024)     //image size <= 1 MB
                        .maxResultSize(1080, 1080)
                        .start();
                isPictureUploaded = true;
            }

        });

        btnPdfLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPdf();
            }
        });

        btnUploadToAzure.setEnabled(false);
        btnUploadToAzure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgPath != null || pdfPath != null) {
                    uploadToBlobStorage(imgPath, pdfPath);
                }
            }
        });

    }

    private void uploadToBlobStorage(final String imgPath, final String pdfPath) {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    if (imgPath != null) {
                        CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
                        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

                        CloudBlobContainer container = blobClient.getContainerReference(storageContainer);

                        container.createIfNotExists();

                        CloudBlockBlob blob = container.getBlockBlobReference(documentName);
                        File source = new File(imgPath);
                        blob.upload(new FileInputStream(source), source.length());
                    } else if (pdfPath != null) {
                        CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
                        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

                        CloudBlobContainer container = blobClient.getContainerReference(storageContainer);

                        container.createIfNotExists();

                        CloudBlockBlob blob = container.getBlockBlobReference(documentName);
                        File source = new File(pdfPath);
                        blob.upload(new FileInputStream(source), source.length());

                    }
                } catch (Exception e) {
                    Toast.makeText(BlobUploadActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(BlobUploadActivity.this, "Upload Task Executed", Toast.LENGTH_SHORT).show();
            }
        };
        task.execute();

    }

    private void selectPdf() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "PDF FILE SELECT"), 12);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri uri = data.getData();

        if (uri != null && isPictureUploaded) {
            Context context = getApplicationContext();
            imgPath = RealPathUtil.getRealPath(context, uri);
            File f = new File(imgPath);
            documentName = f.getName();
            btnUploadToAzure.setEnabled(true);

            Toast.makeText(this, documentName + " SELECTED", Toast.LENGTH_LONG).show();

        }

        if (requestCode == 12 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uris = data.getData();
            //set Uri to handle
            documentName="";
            getDocumentNameFromUri(uris);
            handlePathOz.getRealPath(uris);
            //show Progress Loading
            System.out.println(pdfPath);
            Toast.makeText(this, documentName + " SELECTED", Toast.LENGTH_LONG).show();
            btnUploadToAzure.setEnabled(true);

        }


    }

    private String getDocumentNameFromUri(Uri sourceUri) {
        DocumentFile documentFile = DocumentFile.fromSingleUri(this, sourceUri);
        documentName= documentFile.getName();
        return documentName;
    }

    @Override
    public void onRequestHandlePathOz(PathOz pathOz, Throwable throwable) {
        pdfPath = pathOz.getPath();
        //Toast.makeText(this, "The real path is: " + pathOz.getPath() + "\n The type is: " + pathOz.getType(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}