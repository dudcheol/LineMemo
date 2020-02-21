package com.example.linememo.viewmodel;

import android.app.Application;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EditViewModel extends AndroidViewModel {
    public String uri;

    public EditViewModel(@NonNull Application application) {
        super(application);
        uri = "";
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
        this.uri = imageUri.toString();
        return imageUri;
    }

    public boolean getTextPassOrNot(String s) {
        if (s.trim().length() == 0) return false;
        else return true;
    }

    public boolean isMemoStorable(String[] s, List<String> ls) {
        for (String _s : s) {
            if (_s.length() != 0) return true;
        }
        if (!ls.isEmpty()) return true;
        return false;
    }
}
