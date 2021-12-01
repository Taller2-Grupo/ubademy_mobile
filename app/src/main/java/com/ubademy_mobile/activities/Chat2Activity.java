package com.ubademy_mobile.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.ubademy_mobile.Fragments.ChatsFragment;
import com.ubademy_mobile.Fragments.UsersFragment;
import com.ubademy_mobile.R;

import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.ArrayList;

public class Chat2Activity extends AppCompatActivity {

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        String name = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).getString("full_name", null);
        Log.d("full name", "--------------- " + name);

        viewPagerAdapter.addFragment(new ChatsFragment(getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).getString("email", null), getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).getString("full_name", null)), "Chats");
        viewPagerAdapter.addFragment(new UsersFragment(getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).getString("email", null), getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).getString("full_name", null)), "Usuarios");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}
