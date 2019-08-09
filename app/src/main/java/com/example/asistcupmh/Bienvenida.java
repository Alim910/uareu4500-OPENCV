package com.example.asistcupmh;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class Bienvenida extends AppCompatActivity {
    ViewPager viewPager;
    BottomNavigationView navigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_bienvenida);

        if(!isConnected(Bienvenida.this)) buildDialog(Bienvenida.this).show();
        else {
            Toast.makeText(Bienvenida.this,"Welcome", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_bienvenida);


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new LoginFragment()).addToBackStack(null).commit();
        }


    }


    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
        else return false;
        } else
        return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        return builder;
    }


 private BottomNavigationView.OnNavigationItemSelectedListener navListener= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            switch (menuItem.getItemId()){
                case R.id.nav_user:
                    selectedFragment = new LoginFragment();
                    break;
                case R.id.nav_enrollment:
                    selectedFragment = new EnrollmentFragment();
                    break;
                case R.id.nav_attendance:
                    selectedFragment = new AttendanceFragment();
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).addToBackStack(null).commit();
            return true;
        }
    };


/*
public class PageChange implements ViewPager.OnPageChangeListener {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }
    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                navigation.setSelectedItemId(R.id.nav_user);
                break;
            case 1:
                navigation.setSelectedItemId(R.id.nav_enrollment);
                break;
            case 2:
                navigation.setSelectedItemId(R.id.nav_groups);
                break;
            case 3:
                navigation.setSelectedItemId(R.id.nav_attendance);
                break;
        }
    }
    @Override
    public void onPageScrollStateChanged(int state) {
    }
}*/


}

