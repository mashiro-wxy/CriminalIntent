package wengxiaoyang.hziee.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



public class CrimeListFragment extends Fragment implements CrimeFragment.Callbacks2 {

    private RecyclerView mCrimeRecyclerView;
    public CrimeAdapter mAdapter;
    //private static int mCrimeIndex;
    private boolean mSubtitleVisible;
    private TextView mNullCrimeListTextView;
    private Button mAddCrimeButton;
    private Callbacks mCallbacks;
    private CrimeFragment.Callbacks2 mCallbacks2;
    private ItemTouchHelper mItemTouchHelper;



    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    public interface Callbacks{
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onCrimeUpdated(Crime crime){

    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void initItemTouchHelper(){
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int swipeFlags = ItemTouchHelper.LEFT;
                return makeMovementFlags(0,swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                CrimeLab.get(getActivity()).removeCrime( ( (CrimeHolder)viewHolder) .mCrime );


                updateUI();
            }
        });
        mItemTouchHelper.attachToRecyclerView(mCrimeRecyclerView);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView =  view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));



        mNullCrimeListTextView = view.findViewById(R.id.null_crime_list);

        mAddCrimeButton = view.findViewById(R.id.add_crime);
        mAddCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                //Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                //startActivity(intent);
                mCallbacks.onCrimeSelected(crime);
                updateUI();
            }
        });

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();


        initItemTouchHelper();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                /*Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent);*/
                mCallbacks.onCrimeSelected(crime);
                updateUI();
                //updateCrime();
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
               updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getString(R.string.subtitle_format, crimeCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            //重绘当前可见区域
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();

            //部分重绘
            //mAdapter.notifyItemChanged(mCrimeIndex);
        }

        if (crimes.size() != 0) {
            mNullCrimeListTextView.setVisibility(View.INVISIBLE);
            mAddCrimeButton.setVisibility(View.INVISIBLE);
        } else {
            mNullCrimeListTextView.setVisibility(View.VISIBLE);
            mAddCrimeButton.setVisibility(View.VISIBLE);
        }

       //updateCrime();
        updateSubtitle();
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView mTitleTextView;
            private TextView mDateTextView;
            private Crime mCrime;
            private ImageView mSolvedImageView;

            public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.list_item_crime, parent, false));
                itemView.setOnClickListener(this);

                mTitleTextView = itemView.findViewById(R.id.crime_title);
                mDateTextView = itemView.findViewById(R.id.crime_date);
                mSolvedImageView = itemView.findViewById(R.id.crime_solved);
            }

            public void bind(Crime crime) {
                mCrime = crime;
                mTitleTextView.setText(mCrime.getTitle());

                Date date = crime.getDate();
                DateFormat format = new SimpleDateFormat("EEEE, MMMM dd, YYYY");//星期,月份 ,几号,年份
                String dateFormat = format.format(date);
                mDateTextView.setText(dateFormat);
                mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onClick(View view) {
                //Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
                //mCrimeIndex = getAdapterPosition();     //返回数据在adapter中的位置
                //startActivity(intent);
                mCallbacks.onCrimeSelected(mCrime);
            }
    }

    /*private class requirePoliceCrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Button mRequirePolice;
        private Crime mCrime;
        private ImageView mSolvedImageView;

        public requirePoliceCrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime_police, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mRequirePolice = itemView.findViewById(R.id.crime_require_police);
            mRequirePolice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "已经联系警察", Toast.LENGTH_SHORT).show();
                }
            });
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            Date date = crime.getDate();
            DateFormat format = new SimpleDateFormat("EEEE, MMMM dd, YYYY");//星期,月份 ,几号,年份
            String dateFormat = format.format(date);
            mDateTextView.setText(dateFormat);
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            //Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            mCrimeIndex = getAdapterPosition();     //返回数据在adapter中的位置
            //startActivity(intent);
            mCallbacks.onCrimeSelected(mCrime);
        }
    }*/

    /*protected class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }
        //视图类别功能
        *//*@Override
        public int getItemViewType(int position) {
            //是否报警
            if (mCrimes.get(position).isRequirePolice()) {
                return 1;
            } else {
                return 0;
            }
        }*//*

        @NonNull
        @Override
        public CrimeHolder *//*RecyclerView.ViewHolder*//* onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //先建立LayoutInflater
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            //判断上面的i的值判断使用什么布局,即getItemViewType的返回值
            *//*if (viewType == 1) {
                //return new requirePoliceCrimeHolder(layoutInflater, parent);
            } else {*//*
                return new CrimeHolder(layoutInflater, parent);
            //}

        }

        @Override
        public void onBindViewHolder(CrimeHolder *//*RecyclerView.ViewHolder*//* holder, int position) {
            Crime crime = mCrimes.get(position);

//            //得到ViewType绑定不同的holder
//            if (this.getItemViewType(position) ==1) {
//               // ((requirePoliceCrimeHolder)holder).bind(crime);
//            } else {
                ((CrimeHolder)holder).bind(crime);
                //           }

        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }
    }*/

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime> mCrimes;
        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }
        public void setCrimes(List<Crime> crimes){mCrimes = crimes;}

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }
        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

    }


}
