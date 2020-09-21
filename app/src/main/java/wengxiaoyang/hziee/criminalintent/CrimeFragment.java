package wengxiaoyang.hziee.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.support.v4.app.ShareCompat;

import android.text.format.DateFormat;
import android.widget.ImageButton;
import android.widget.ImageView;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "dialogDate";
    private static final String DIALOG_TIME = "dialogTime";
    private static final String DIALOG_PHOTO = "dialogPhoto";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 3;

    private Crime mCrime;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private CheckBox mPoliceCheckBox;
    private Button mTimeButton;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Callbacks2 mCallbacks;

    public interface Callbacks2 {
        void onCrimeUpdated(Crime crime);
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks2) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        setHasOptionsMenu(true);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = v.findViewById(R.id.crime_date);
        Date date = mCrime.getDate();
       /* DateFormat format1 = new SimpleDateFormat("EEE, MMM dd, YYYY");//星期,月份 ,几号,年份
        String dateFormat1 = format1.format(date);*/
        //String dateFormat1 = "EEE, MMM dd, YYYY";
        //String dateString1 = (String) DateFormat.format("EEE, MMM dd, yyyy", date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mTimeButton = v.findViewById(R.id.crime_time);
        /*DateFormat format2 = new SimpleDateFormat("h:mm a");//
        String dateFormat2 = format2.format(date);*/
        String dateString2 = (String) DateFormat.format("h:mm a", date);
        updateTime(dateString2);
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(Objects.requireNonNull(manager), DIALOG_TIME);  //第二个参数是tag
            }
        });

        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        mPoliceCheckBox = v.findViewById(R.id.crime_police);
        mPoliceCheckBox.setChecked(mCrime.isRequirePolice());
        mPoliceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setRequirePolice(isChecked);
                updateCrime();
            }
        });

        mCallButton = v.findViewById(R.id.call_suspect);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                Uri phone = Uri.parse("tel:" + mCrime.getPhone());
                i.setData(phone);
                startActivity(i);
            }
        });

        mReportButton = v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder i = ShareCompat.IntentBuilder.from(getActivity());
                i.setType("text/plain");
                i.setText(getCrimeReport());
                i.setSubject(getString(R.string.crime_report_subject));
                i.createChooserIntent();
                i.startChooser();
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        //pickContact.addCategory(Intent.CATEGORY_HOME);    //阻止联系人应用和intent匹配
        mSuspectButton = v.findViewById(R.id.crime_suspect);
        //mSuspectButton.setEnabled(false);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                int hasWriteContactsPermission = getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS);
                //没有获得的话，使用requestPermissions方法要求权限。
                if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.READ_CONTACTS}, 0);
                }

                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "wengxiaoyang.hziee.criminalintent.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity().getPackageManager()
                        .queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        mPhotoView = v.findViewById(R.id.crime_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhotoFile == null || !mPhotoFile.exists()) {
                    mPhotoView.setImageDrawable(null);
                } else {
                    FragmentManager manager = getFragmentManager();
                    PhotoDetailFragment dialog = PhotoDetailFragment.newInstance(mPhotoFile);
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_PHOTO);
                    dialog.show(manager, DIALOG_PHOTO);
                }
            }
        });
        ViewTreeObserver mPhotoObserver = mPhotoView.getViewTreeObserver();
        mPhotoObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updatePhotoView(mPhotoView.getWidth(), mPhotoView.getHeight());
            }
        });


        updateCrime();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        /*Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
        String dateFormat1 = (String) DateFormat.format("EEE, MMM dd, yyyy", date);
        String dateFormat2 = (String)DateFormat.format("h:mm a", date);
        mCrime.setDate(mCrime.getDate());*/
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateCrime();
            updateDate();
        }
        if (requestCode == REQUEST_TIME) {
            updateCrime();
            Date time = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            String dateString2 = (String) DateFormat.format("h:mm a", time);
            updateTime(dateString2);
        }
        else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            //指定希望查询返回值的字段
            //增加了一个ContactsContract.Contacts._ID 目的是为了得到目标联系人ID
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME,ContactsContract.Contacts._ID
            };

           String[] phoneProject = new String[] {
                   ContactsContract.CommonDataKinds.Phone.NUMBER
            };
            //执行查询-这里的contactUri类似于“where”子句
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            if (c != null) {
                try {
                    //再次检查你是否真的得到了结果
                    if (c.getCount() == 0) {
                        return;
                    }

                    //拉出第一行数据的第一列-这是嫌疑人的名字
                    c.moveToFirst();
                    String suspect = c.getString(0);
                    mCrime.setSuspect(suspect);
                    updateCrime();
                    mSuspectButton.setText(suspect);

                    String contactId = c.getString(1);
                    Cursor phone = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, phoneProject,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                    if (phone.moveToNext()){
                        String mPhone = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        mCrime.setPhone(mPhone);
                        updateCrime();
                        mCallButton.setText("call:" + mPhone);
                    }

                } finally {
                    c.close();
                }

            }
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "wengxiaoyang.hziee.criminalintent.fileprovider", mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateCrime();
            updatePhotoView(mPhotoView.getWidth(), mPhotoView.getHeight());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime:
                CrimeLab.get(getActivity()).removeCrime(mCrime);
                updateCrime();
                //getActivity().finish();
                return true;
            case android.R.id.home:     //向上导航时保证子标题可见状态
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private void updateTime(String s) {
        mTimeButton.setText(s);
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String requirePoliceString = null;
        if (mCrime.isRequirePolice()) {
            requirePoliceString = getString(R.string.crime_report_requirePolice);
        } else {
            requirePoliceString = getString(R.string.crime_report_not_requirePolice);
        }

        /*DateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, YYYY");
        String dateString = dateFormat.format(mCrime.getDate().toString());*/

        /*String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();*/

        String dateString = (String) DateFormat.format("EEEE, MMMM dd, yyyy", mCrime.getDate());


        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, requirePoliceString, suspect);

        return report;
    }

    private void updatePhotoView(int width, int height) {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), width, height);
            bitmap = PictureUtils.Toturn(bitmap, mPhotoFile.getPath());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

}
