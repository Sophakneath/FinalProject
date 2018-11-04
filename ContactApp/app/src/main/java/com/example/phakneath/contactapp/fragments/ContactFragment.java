package com.example.phakneath.contactapp.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.example.phakneath.contactapp.ContactActivity;
import com.example.phakneath.contactapp.ProfileActivity;
import com.example.phakneath.contactapp.R;
import com.example.phakneath.contactapp.SharingProfileActivity;
import com.example.phakneath.contactapp.adapter.UserSharingAdapter;
import com.example.phakneath.contactapp.dialog.ContactInPhoneDialog;
import com.example.phakneath.contactapp.model.ContactInPhone;
import com.example.phakneath.contactapp.adapter.contactInPhoneAdapter;
import com.example.phakneath.contactapp.model.User;
import com.example.phakneath.contactapp.model.UserInformation;
import com.example.phakneath.contactapp.singleton.ContactInMemory;
import com.example.phakneath.contactapp.singleton.ContactInPhoneObj;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ContactFragment extends Fragment implements com.example.phakneath.contactapp.adapter.contactInPhoneAdapter.OpenDialogDetail, View.OnClickListener, UserSharingAdapter.DetailSharing, UserSharingAdapter.DeleteSharing{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView horizontalList, verticalList;
    private List<ContactInPhone> contactInPhones = new ArrayList<>();
    private List<User> users;
    private List<UserInformation> userInformations;
    private contactInPhoneAdapter contactInPhoneAdapter;
    private CircleImageView img;
    private TextView username, userPhoneNum, total, total1;
    private RelativeLayout profileInfo;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mChild;
    private String uID;
    private UserSharingAdapter userSharingAdapter;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private ContactInMemory contactInMemory = ContactInPhoneObj.getRepository();
    FirebaseStorage storage;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ContactFragment() {
        // Required empty public constructor
        //contactInPhones = ContactInPhoneObj.getRepository().getUsers();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactFragment.
     */

    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    public void setupAdapter(List<ContactInPhone>  contactInPhones)
    {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        horizontalList.setLayoutManager(layoutManager);
        contactInPhoneAdapter = new contactInPhoneAdapter(getContext(), contactInPhones);
        horizontalList.setAdapter(contactInPhoneAdapter);
        contactInPhoneAdapter.openDialogDetail = this::onClick;
        total.setText(contactInPhones.size() + " Contacts");
    }

    public void setUpUserSharingAdapter(List<User> users)
    {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        verticalList.setLayoutManager(layoutManager);
        userSharingAdapter = new UserSharingAdapter(getContext(), users);
        //userSharingAdapter.SetUsers(users);
        total1.setText(users.size() + " Contacts");
        verticalList.setAdapter(userSharingAdapter);
        userSharingAdapter.detailSharing = this::onOpenDetail;
        userSharingAdapter.deleteSharing = this::onDelete;

    }

    @Override
    public void onStart() {
        super.onStart();
        getInformation();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            //contactInPhones = getArguments().getParcelableArray("c");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        horizontalList = view.findViewById(R.id.contactInPhone);
        verticalList = view.findViewById(R.id.contactByShare);
        img = view.findViewById(R.id.profile);
        username = view.findViewById(R.id.username);
        userPhoneNum = view.findViewById(R.id.userPhoneNum);
        profileInfo = view.findViewById(R.id.profileInfo);
        progressBar = view.findViewById(R.id.progressbar);
        total = view.findViewById(R.id.total);
        total1 = view.findViewById(R.id.total1);
        //setupAdapter();
        new RunBackGround().execute();
        getSharingContact();
        profileInfo.setOnClickListener(this::onClick);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //interface for open dialog from custom adapter
    @Override
    public void onClick(int position, ContactInPhone contactInPhone) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("contact", contactInPhone);
        ContactInPhoneDialog c = new ContactInPhoneDialog();
        c.setArguments(bundle);
        c.show(getActivity().getFragmentManager(),"Dialog");
    }

    //override function onClick by OnClickListener
    @Override
    public void onClick(View v) {
        if(v == profileInfo)
        {
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            startActivity(intent);
        }
    }

    public void UpdateUIProfile(User user)
    {
        userPhoneNum.setText(user.getPhone());
        username.setText(user.getUsername());
        String getImage = user.getImage() + "." + user.getExtension();
        if(user.getImage() != null) getImage(user,img,getImage, getContext());
    }

    private void getInformation()
    {
        progressBar.setVisibility(View.VISIBLE);
        uID  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mChild = mDatabase.child("user").child("id").child(uID);
        mChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.d("ooooo", "onDataChange: " + dataSnapshot.child("email").getValue());
                String username = dataSnapshot.child("username").getValue(String.class);
                String phone = dataSnapshot.child("phone").getValue(String.class);
                String image = dataSnapshot.child("image").getValue(String.class);
                String extension = dataSnapshot.child("extension").getValue(String.class);

                Log.d("ooooo", "onDataChange: " + username + phone + image + extension);
                if(image == null) image = "";

                User user = new User(username,phone,image,extension);
                progressBar.setVisibility(View.GONE);
                UpdateUIProfile(user);
                //getImage(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getImage(User user, ImageView img, String getImage, Context context)
    {
        storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference().child("profile/" + getImage);
        try {
            /*localFile = File.createTempFile("Images", user.getExtension());
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //Picasso.with(getContext()).load(localFile.getAbsolutePath()).into(img);
                    my_image = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    img.setImageBitmap(my_image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });*/
            /*Glide.with(this)
                    .load(ref)
                    .into(img);*/

            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    try {
                        Glide.with(context).load(uri).into(img);
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSharingContact()
    {
        uID  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mChild = mDatabase.child("user").child("id").child(uID).child("sharing");
        mChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<>();
                User user = new User();
                for(DataSnapshot dps : dataSnapshot.getChildren())
                {
                    user = dps.getValue(User.class);
                    users.add(user);
                }

                //setup Adapter
                setUpUserSharingAdapter(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onOpenDetail(User user) {
        Intent intent = new Intent(getContext(), SharingProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("getUser", user);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onDelete(User user) {
        new FancyAlertDialog.Builder(getActivity())
                .setTitle("Confirmation")
                .setBackgroundColor(Color.parseColor("#56c0fb"))  //Don't pass R.color.colorvalue
                .setMessage("Are you sure you want to delete this information?")
                .setNegativeBtnText("No")
                .setPositiveBtnBackground(Color.parseColor("#56c0fb"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("Yes")
                .setNegativeBtnBackground(Color.parseColor("#56c0fb"))  //Don't pass R.color.colorvalue
                .setAnimation(Animation.SIDE)
                .isCancellable(true)
                .setIcon(R.drawable.infos, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        remove(user);
                        Toast.makeText(getContext(), "Delete Successfull", Toast.LENGTH_SHORT).show();
                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                    }
                })
                .build();
    }

    public void remove(User user)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child("id").child(uID).child("sharing").child(user.getId()).removeValue();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //get contact in phone
    public class RunBackGround extends AsyncTask<String, Integer, ContactInMemory> {
        @Override
        protected ContactInMemory doInBackground(String... strings) {
            if (contactInMemory.getUsers().size() == 0) {
                showContacts();
            }
            return contactInMemory;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(ContactInMemory contactInMemory) {
            super.onPostExecute(contactInMemory);
            contactInPhones = contactInMemory.getUsers();
            setupAdapter(contactInPhones);
        }
    }

    //ask permission to access contact
    public void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getContext().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {

            List<ContactInPhone> contact;
            contact = getContactNames();
            for(int i=0; i<contact.size(); i++)
                contactInMemory.saveUsers(contact.get(i));


            // contact = ContactInPhoneObj.getRepository().getUsers();
            //Log.d("ooooo", "showContacts: " + contact.size());
            //  Toast.makeText(getContext(), contactInPhones.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    public List<ContactInPhone> getContactNames() {
        List<String> phone;
        List<ContactInPhone> contact = new ArrayList<>();

        // Get the ContentResolver
        ContentResolver cr = getContext().getContentResolver();

        // Get the Cursor of all the contacts
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        Bitmap photoDefault = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.userprofile);
        Bitmap photo = null;
        InputStream inputStream;

        if (cursor.moveToFirst()) {
            do {

                phone = new ArrayList<>();

                // Get the contacts name
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);

                while (phones.moveToNext()) {
                    String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phone.add(number);
                }
                phones.close();

                try {
                    inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContext().getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));

                    if (inputStream != null) {
                        photo = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();
                    }
                    else photo = photoDefault;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                contact.add(new ContactInPhone(name, phone, photo));

            } while (cursor.moveToNext());
        }
        cursor.close();

        return contact;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(getContext(), "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
