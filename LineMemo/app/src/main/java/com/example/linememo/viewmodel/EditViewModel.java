package com.example.linememo.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EditViewModel extends AndroidViewModel {
    private final static String TAG = "EditViewModel";
    private String uri;
    private Application application;

    public EditViewModel(@NonNull Application application) {
        super(application);
        this.uri = "";
        this.application = application;
    }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "LINEMEMO_" + timeStamp + "_";
        // getExternalFilesDir : LineMemo 앱 이외에는 비공개, 디렉터리에 저장한 파일은 사용자가 앱을 제거할 때 삭제됨
        File storageDir = getApplication().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        return image;
    }

    public Uri createImageUri(File image) {
        Uri imageUri = FileProvider.getUriForFile(getApplication(),
                "com.example.linememo.fileprovider",
                image);
        setUri(imageUri.toString());
        return imageUri;
    }

    public void deleteFile(File file) {
        if (file.exists()) file.delete();
    }

    public boolean getTextPassOrNot(String s) {
        if (TextUtils.isEmpty(s.trim())) return false;
        else return true;
    }

    public boolean isMemoStorable(String[] s, List<String> ls) {
        for (String _s : s) {
            if (!TextUtils.isEmpty(_s.trim())) return true;
        }
        if (!ls.isEmpty()) return true;
        return false;
    }

    public Intent openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(application.getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                return null;
            }
            if (photoFile != null) {
                Uri photoURI = createImageUri(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                return takePictureIntent;
            }
        }
        return null;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
