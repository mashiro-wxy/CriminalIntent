package wengxiaoyang.hziee.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;

import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;


public class PhotoDetailFragment extends DialogFragment {
    private static final String ARG_FILE = "file";
    private ImageView mPhotoView;

    public static PhotoDetailFragment newInstance(File file) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FILE, file);

        PhotoDetailFragment fragment = new PhotoDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        File file = (File) getArguments().getSerializable(ARG_FILE);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo, null);
        mPhotoView = v.findViewById(R.id.crime_photo_detail);
        Bitmap bitmap = PictureUtils.getScaledBitmap(file.getPath(), getActivity());
        mPhotoView.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity()).setView(v)
                .setPositiveButton(android.R.string.ok, null).create();
    }

}
