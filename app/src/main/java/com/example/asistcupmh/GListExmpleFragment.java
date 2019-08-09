package com.example.asistcupmh;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.asistcupmh.adapter.GmailAdapter;
import com.example.asistcupmh.model.VersionModel;

import java.util.ArrayList;
import java.util.List;


public class GListExmpleFragment extends Fragment {
    RecyclerView recyclerView;
    GmailAdapter adapter;
    boolean showFAB = true;

    private ListView listk;
    SparseBooleanArray sparseBooleanArray ;
    String[] ListData;
    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_attendance,container,false);


        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.gmail_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.gmail_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        List<String> listData = new ArrayList<String>();
        int ct = 0;
        for (int i = 0; i < VersionModel.data.length * 3; i++) {
            listData.add(VersionModel.data[ct]);
            ct++;
            if (ct == VersionModel.data.length) {
                ct = 0;
            }
        }

        if (adapter == null) {
            adapter = new GmailAdapter(getActivity().getApplicationContext(), listData);
            recyclerView.setAdapter(adapter);
        }

        /**
         * Bottom Sheet
         */

        // To handle FAB animation upon entrance and exit
        final Animation growAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.simple_grow);
        final Animation shrinkAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.simple_shrink);





        shrinkAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.gmail_coordinator);


        return rootView;

    }




    public void checkList() {
        ArrayAdapter<String> adaptador2= new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_checked,android.R.id.text1,ListData);

        adaptador2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        listk.setAdapter(adaptador2);

        listk.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sparseBooleanArray = listk.getCheckedItemPositions();

                String ValueHolder = "" ;

                int i = 0 ;

                while (i < sparseBooleanArray.size()) {

                    if (sparseBooleanArray.valueAt(i)) {

                        ValueHolder += ListData [ sparseBooleanArray.keyAt(i) ] + ",";
                    }

                    i++ ;
                }

                ValueHolder = ValueHolder.replaceAll("(,)*$", "");

                // Toast.makeText(getActivity(), "ListView Selected Values = " + ValueHolder, Toast.LENGTH_LONG).show();

            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        ((AppCompatActivity)getActivity()).getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                ((AppCompatActivity)getActivity()).supportFinishAfterTransition();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
