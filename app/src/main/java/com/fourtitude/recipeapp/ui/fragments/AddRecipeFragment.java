package com.fourtitude.recipeapp.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.fourtitude.recipeapp.R;
import com.fourtitude.recipeapp.viewModels.NotificationsViewModel;
import com.fourtitude.utilities.PermissionUtills;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

public class AddRecipeFragment extends Fragment {
    @BindView(R.id.add_image)
    ImageView addImage;
    @BindView(R.id.upload)
    Button btnUpload;
    private Spinner spinnerRecipes;
    private File file;
    private boolean successfulBoolean = false;
    StorageReference mStorageRef;
    final int CAMERA_CAPTURE = 10;
    final int RESULT_LOAD_IMAGE = 20;

    private String strImageUrl;

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        ButterKnife.bind(this, root);
        PermissionUtills.checkPermision(getActivity());

        firebaseDatabase = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("RecipesImages");
        databaseReference = firebaseDatabase.getReference().child("Recipes");

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraBuilder();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecipesMethod();
            }
        });

        return root;
    }

    //open camera view
    public void cameraBuilder() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Open");
        String[] pictureDialogItems = {
                "\tGallery",
                "\tCamera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                galleryIntent();
                                break;
                            case 1:
                                cameraIntent();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void cameraIntent() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(captureIntent, CAMERA_CAPTURE);
    }

    public void galleryIntent() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && null != data) {
            String imagepath = getPath(data.getData());
            file = new File(imagepath);
            try {
                file = new Compressor(getActivity()).compressToFile(file);
                uploadImage(data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_OK && requestCode == CAMERA_CAPTURE && data != null) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            thumbnail.compress(Bitmap.CompressFormat.PNG, 90, bytes);
            file = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");
            FileOutputStream fo;
            try {
                file.createNewFile();
                fo = new FileOutputStream(file);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            addImage.setImageBitmap(thumbnail);
            uploadImage(data.getData());
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(projection[0]);
        String filePath = cursor.getString(columnIndex);
        addImage.setImageBitmap(BitmapFactory.decodeFile(filePath));
        return cursor.getString(column_index);

    }

    private boolean uploadImage(Uri data) {

        if (file != null) {
            String out = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(new Date());
            final StorageReference storageReference = mStorageRef.child(out + ".jpg");

            storageReference.putFile(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    strImageUrl = uri.toString();
                                }
                            });

                            successfulBoolean = true;
                            Toast.makeText(getActivity(), "successful", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                            successfulBoolean = false;
                        }
                    });

            return successfulBoolean;

        }

        return false;
    }

    public  void addRecipesMethod() {

        HashMap hashMap_record = new HashMap<String, String>();
        hashMap_record.put("title", "Hello");
        hashMap_record.put("description","yes");
        hashMap_record.put("image",strImageUrl);
        databaseReference.push().setValue(hashMap_record);
    }
}