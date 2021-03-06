package com.orthopg.snaphy.orthopg.QualificationFragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidsdk.snaphy.snaphyandroidsdk.callbacks.ObjectCallback;
import com.androidsdk.snaphy.snaphyandroidsdk.list.DataList;
import com.androidsdk.snaphy.snaphyandroidsdk.list.Listen;
import com.androidsdk.snaphy.snaphyandroidsdk.models.Customer;
import com.androidsdk.snaphy.snaphyandroidsdk.models.Qualification;
import com.androidsdk.snaphy.snaphyandroidsdk.presenter.Presenter;
import com.androidsdk.snaphy.snaphyandroidsdk.repository.QualificationRepository;
import com.orthopg.snaphy.orthopg.Constants;
import com.orthopg.snaphy.orthopg.HorizontalDividerItemDecoration;
import com.orthopg.snaphy.orthopg.MainActivity;
import com.orthopg.snaphy.orthopg.R;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QualificationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QualificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QualificationFragment extends Fragment {
    MainActivity mainActivity;
    private OnFragmentInteractionListener mListener;
    public final static String TAG = "QualificationFragment";
    @Bind(R.id.fragment_qualification_recyclerview1) RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    QualificationPresenter qualificationPresenter;
    QualificationAdapter qualificationAdapter;
    DataList<Qualification> qualificationDataList = new DataList<>();
    DataList<Qualification> updatedQualification = new DataList<>();

    public QualificationFragment() {
        // Required empty public constructor
    }

    public static QualificationFragment newInstance() {
        QualificationFragment fragment = new QualificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qualification, container, false);
        ButterKnife.bind(this,view);
        linearLayoutManager = new LinearLayoutManager(mainActivity);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(Color.parseColor("#EEEEEE"))
                .sizeResId(R.dimen.divider)
                .marginResId(R.dimen.left_margin1,R.dimen.right_margin1)
                .build());
        subscribe();
        return view;
    }

    public void loadPresenter(){

        qualificationPresenter = new QualificationPresenter(mainActivity.snaphyHelper.getLoopBackAdapter(),mainActivity);

        qualificationPresenter.fetchQualification(true);
    }

    public void subscribe(){
        qualificationDataList = Presenter.getInstance().getList(Qualification.class, Constants.QUALIFICATION_LIST);
        qualificationDataList.subscribe(this, new Listen<Qualification>() {
            @Override
            public void onInit(DataList<Qualification> dataList) {
                super.onInit(dataList);
                qualificationAdapter = new QualificationAdapter(mainActivity, qualificationDataList);
                recyclerView.setAdapter(qualificationAdapter);
            }

            @Override
            public void onChange(DataList<Qualification> dataList) {
                super.onChange(dataList);
                qualificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onClear() {
                super.onClear();
                qualificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onRemove(Qualification element, int index, DataList<Qualification> dataList) {
                super.onRemove(element, index, dataList);
            }
        });
    }

    @OnClick(R.id.fragment_qualification_imageview1) void onBack(){
        mainActivity.onBackPressed();
    }


    @OnClick(R.id.fragment_qualification_button1) void onUpdateQualification(){

        updatedQualification = Presenter.getInstance().getList(Qualification.class, Constants.CUSTOMER_QUALIFICATION_LIST);
        if(updatedQualification!=null){
            if(updatedQualification.size()!=0){
                DataList<String> updatedQualificationList = new DataList<>();
                for(Qualification q : updatedQualification){
                    updatedQualificationList.add(q.getId().toString());
                }
                Customer customer = Presenter.getInstance().getModel(Customer.class, Constants.LOGIN_CUSTOMER);
                String customerId = (String)customer.getId();
                if(customerId!=null){
                    QualificationRepository qualificationRepository = mainActivity.snaphyHelper.getLoopBackAdapter().createRepository(QualificationRepository.class);
                    qualificationRepository.updateQualification(customerId, updatedQualificationList, new ObjectCallback<JSONObject>() {
                        @Override
                        public void onBefore() {
                            super.onBefore();
                            mainActivity.startProgressBar(mainActivity.progressBar);
                        }

                        @Override
                        public void onSuccess(JSONObject object) {
                            super.onSuccess(object);
                            mainActivity.onBackPressed();
                            TastyToast.makeText(mainActivity,"Successfully updated qualifications", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                        }

                        @Override
                        public void onError(Throwable t) {
                            super.onError(t);
                            Log.e(Constants.TAG, t.toString());
                            TastyToast.makeText(mainActivity, "Error in updating qualification", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        }

                        @Override
                        public void onFinally() {
                            super.onFinally();
                            mainActivity.stopProgressBar(mainActivity.progressBar);
                        }
                    });
                }
            }
        }
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
        mainActivity = (MainActivity)getActivity();
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
