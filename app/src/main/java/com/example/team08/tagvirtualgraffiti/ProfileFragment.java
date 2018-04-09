package com.example.team08.tagvirtualgraffiti;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private Button mLogoutButton;
    private Button mChangeTagButton;
    private Button mChangeUsernameButton;
    private ImageView mTagImageView;
    private TextView mNameTv;
    private TextView mEmailTv;
    private TextView mScoreTv;

    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);


        //NOTE: Button is derived from TextView whereas ImageButton is derived from ImageView
        mLogoutButton = (Button) v.findViewById(R.id.logout_button);
        mChangeTagButton = (Button) v.findViewById(R.id.change_tag_button);
        mTagImageView = (ImageView) v.findViewById(R.id.profile_tag_image);
        mNameTv = (TextView) v.findViewById(R.id.profile_username);
        mChangeUsernameButton = (Button) v.findViewById(R.id.change_username_button);
        mEmailTv = (TextView) v.findViewById(R.id.profile_email);
        mScoreTv = (TextView) v.findViewById(R.id.profile_score);

        mNameTv.setText(TagApplication.mCurrentUser.getUsername());
        mEmailTv.setText(TagApplication.mCurrentUser.getEmail());
        mScoreTv.setText("Score: " + TagApplication.mCurrentUser.getScore());

        if (mLogoutButton != null) {
            mLogoutButton.setOnClickListener(this);
        }

        if (mChangeTagButton != null) {
            mChangeTagButton.setOnClickListener(this);
        }

        if (mChangeUsernameButton != null){
            mChangeUsernameButton.setOnClickListener(this);
        }

        loadTagPicture();

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void logout() {
        //TODO: Verify that we don't need to check check Connection status here
        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    if (getContext() != null) {
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                }
            }
        };
        FirebaseAuth.getInstance().addAuthStateListener(authListener);
        FirebaseAuth.getInstance().signOut();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout_button:
                Toast.makeText(getContext(), "Logging Out...", Toast.LENGTH_SHORT).show();
                logout();
                break;
            case R.id.change_tag_button:
                if(TagApplication.isOnline(getContext())) {
                    Toast.makeText(getActivity(), "Opening Gallery...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                }else{
                    TagApplication.makeDialog(getActivity(), R.string.title_offline, R.string.msg_offline).show();
                }
                break;
            //case R.id.change_username_button:


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            uploadToFirebase(filePath);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                mTagImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadToFirebase(Uri filePath) {
        if (filePath != null) {
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + TagApplication.mCurrentUser.getId());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                            loadTagPicture();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }


    private void loadTagPicture() {
        if (TagApplication.isOnline(getContext())) {
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            StorageReference ref = storageReference.child("images/" + TagApplication.mCurrentUser.getId());
            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(ref)
                    .signature(new StringSignature(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(mTagImageView);
        } else {
            TagApplication.makeDialog(getActivity(), R.string.title_offline, R.string.msg_offline).show();
        }
    }

}