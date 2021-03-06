package com.orthopg.snaphy.orthopg.Fragment.NewCase;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.androidsdk.snaphy.snaphyandroidsdk.list.DataList;
import com.androidsdk.snaphy.snaphyandroidsdk.list.Listen;
import com.androidsdk.snaphy.snaphyandroidsdk.presenter.Presenter;
import com.orthopg.snaphy.orthopg.Constants;
import com.orthopg.snaphy.orthopg.CustomModel.NewCase;
import com.orthopg.snaphy.orthopg.CustomModel.TrackImage;
import com.orthopg.snaphy.orthopg.MainActivity;
import com.orthopg.snaphy.orthopg.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CaseUploadImageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CaseUploadImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CaseUploadImageFragment extends android.support.v4.app.Fragment {

    private OnFragmentInteractionListener mListener;
    public static String TAG = "CaseUploadImageFragment";
    MainActivity mainActivity;
    @Bind(R.id.fragment_case_upload_image_recycler_view)
    RecyclerView recyclerView;
    CaseUploadImageFragmentAdapter caseUploadImageFragmentAdapter;
    public static CaseUploadImageFragment that;
    final int CROP_PIC = 2;
    Context context;
    //http://stackoverflow.com/questions/15807766/android-crop-image-size

    public CaseUploadImageFragment() {
        // Required empty public constructor
    }

    public static CaseUploadImageFragment newInstance() {
        CaseUploadImageFragment fragment = new CaseUploadImageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getContext();
        that = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_case_upload_image, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));

        EasyImage.configuration(mainActivity)
                .setImagesFolderName("OrthoPG")
                .saveInRootPicturesDirectory()
                .setCopyExistingPicturesToPublicLocation(true);
        //Load previous data..
        loadImages();
        return view;
    }

    @OnClick(R.id.fragment_case_upload_image_imageButton1)
    void backButton() {
        mainActivity.onBackPressed();
    }

    /*
    @OnClick(R.id.fragment_case_upload_image_imageButton2) void postAsAnonymous() {

    }
    */

    @OnClick(R.id.fragment_case_upload_image_linear_layout3)
    void openGallery() {
        openGalleryFolder();
    }

    @OnClick(R.id.fragment_case_upload_image_imageButton3)
    void cameraButton() {
        openGalleryFolder();
    }

    @OnClick(R.id.fragment_case_upload_image_textview1)
    void cameraText() {
        openGalleryFolder();
    }

    @OnClick(R.id.fragment_case_upload_image_button1)
    void nextButton() {
        mainActivity.replaceFragment(R.id.fragment_case_upload_image_button1, null);
    }

    public void loadImages() {
        if (Presenter.getInstance().getModel(NewCase.class, Constants.ADD_NEW_CASE) != null) {
            final DataList<TrackImage> trackImages = Presenter.getInstance().getModel(NewCase.class, Constants.ADD_NEW_CASE).getTrackImages();
            if (trackImages != null) {
                trackImages.subscribe(this, new Listen<TrackImage>() {
                    @Override
                    public void onInit(DataList<TrackImage> dataList) {
                        caseUploadImageFragmentAdapter = new CaseUploadImageFragmentAdapter(mainActivity, trackImages);
                        recyclerView.setAdapter(caseUploadImageFragmentAdapter);
                    }

                    @Override
                    public void onChange(DataList<TrackImage> dataList) {
                        caseUploadImageFragmentAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onClear() {
                        super.onClear();
                    }

                    @Override
                    public void onRemove(TrackImage element, int index, DataList<TrackImage> dataList) {

                        if (element != null) {
                            if (element.isDownloaded()) {
                                if (element.getImageModel() != null) {
                                    //Store it to deleted models for later..use..
                                    //TODO: Delete the items of deletedModels after save..of images
                                    Presenter.getInstance().getModel(NewCase.class, Constants.ADD_NEW_CASE).getDeletedModels().add(element.getImageModel());
                                }
                            } else {
                                //TODO: Later remove cropped from local file too..
                            }
                        }
                    }
                });

            }
        }
    }


    public void openGalleryFolder() {
        View view1 = mainActivity.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
        int permissionCheck = ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            EasyImage.openDocuments(this);
        } else {
            Nammu.askForPermission(mainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    EasyImage.openGallery(mainActivity);
                }

                @Override
                public void permissionRefused() {

                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(data != null) {
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    TrackImage trackImage = new TrackImage();
                    if (resultUri != null) {
                        trackImage.setUri(resultUri);
                        trackImage.setDownloaded(false);
                        //Add to list....
                        Presenter.getInstance().getModel(NewCase.class, Constants.ADD_NEW_CASE).getTrackImages().add(trackImage);
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    }
            } else {
                TrackImage trackImage = new TrackImage();
                if (mainActivity.globalUri != null) {
                    trackImage.setUri(mainActivity.globalUri);
                    trackImage.setDownloaded(false);
                    //Add to list....
                    Presenter.getInstance().getModel(NewCase.class, Constants.ADD_NEW_CASE).getTrackImages().add(trackImage);
                }
            }

        } else {
            EasyImage.handleActivityResult(requestCode, resultCode, data, mainActivity, new DefaultCallback() {
                @Override
                public void onImagePickerError(Exception e, EasyImage.ImageSource source) {
                    Log.v(Constants.TAG, e.toString());
                    //Some error handling
                }

                @Override
                public void onImagePicked(File imageFile, EasyImage.ImageSource source) {
                    //Handle the image
                    final Uri uri = Uri.fromFile(imageFile);
                    //Add a global instance of global uri..
                    mainActivity.globalUri = uri;
                    // for fragment (DO NOT use `getActivity()`)
                    CropImage.activity(uri)
                            .start(getContext(), that);
                    //performCrop(uri);
                   /* CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(mainActivity);*/
                }

            });
        }



    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
        mainActivity = (MainActivity) getActivity();
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
