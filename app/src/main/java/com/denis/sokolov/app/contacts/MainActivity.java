package com.denis.sokolov.app.contacts;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;

// MainActivity.java
public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = "myTag";
    private ListView lvContactsList;
    private SimpleAdapter adapter;
    private ArrayList<ContactMap> contactsList = new ArrayList<>();
    private static final int MY_PERMISSIONS_READ_CONTACTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvContactsList = findViewById(R.id.contactsList);
        adapter = new SimpleAdapter(this, contactsList, R.layout.list_item,
                new String[] { ContactMap.NAME, ContactMap.PHONE }, new int[] {
                R.id.text_view_name, R.id.text_view_phone });
        lvContactsList.setAdapter(adapter);

        // In an actual app, you'd want to request a permission when the user performs an action
        // that requires that permission.
        requestContactsPermissions();
    }

    private void readContacts() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext())
            {
                String hasPhone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                if (hasPhone.equals("1")) {
                    Cursor phones = getContentResolver().query
                            (ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                            + " = " + contactId, null, null);
                    while (phones != null && phones.moveToNext()) {
                        String number = phones.getString(phones.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[-() ]", "");
                        Log.d(LOG_TAG, "name = " + name + ", number = " + number);

                        contactsList.add(new ContactMap(name, number));
                    }
                    if(phones!=null) phones.close();
                }
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
        Log.d(LOG_TAG, "contactsList:" + contactsList);
    }

    private void requestContactsPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(this, "Please grant permissions to contacts",
                        Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_READ_CONTACTS);

                Log.d(LOG_TAG, "check permission granted 1");

            } else {
                // Show user dialog to grant permission to contacts
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_READ_CONTACTS);

                Log.d(LOG_TAG, "check permission granted 2");
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

            readContacts();
            Log.d(LOG_TAG, "permission is granted 3");

        }
    }

    //Handling callback
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    readContacts();
                    Log.d(LOG_TAG, "onRequestPermissionsResult: YES");
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(LOG_TAG, "onRequestPermissionsResult: NO");
                    Toast.makeText(this, "Permissions Denied to contacts", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

}
