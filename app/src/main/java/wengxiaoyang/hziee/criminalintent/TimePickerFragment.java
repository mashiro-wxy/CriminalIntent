package wengxiaoyang.hziee.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimePickerFragment extends DialogFragment {
    //两个按钮公用一个EXTRA_DATE,所以不用新添加一个TimePicker专用的RXTRA_TIME
    public static final String EXTRA_DATE = "wengxiaoyang.hziee.criminalintent.date";

    private static final String ARG_DATE = "date";

    private TimePicker mTimePicker;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //不加final的话,GregorianCalendar的构造方法会报错
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        //时分
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);

        mTimePicker = v.findViewById(R.id.dialog_time_picker);
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minute);

        return new AlertDialog.Builder(getActivity()).setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int hour = mTimePicker.getCurrentHour();
                                int minute = mTimePicker.getCurrentMinute();

                                Date date = new GregorianCalendar(year, month, day, hour, minute).getTime();
                                sendResult(Activity.RESULT_OK, date);
                            }
                        })
                .create();
    }

    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode,intent);
    }

    public static TimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
}


