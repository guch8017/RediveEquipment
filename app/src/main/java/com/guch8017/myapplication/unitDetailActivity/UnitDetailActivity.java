package com.guch8017.myapplication.unitDetailActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.guch8017.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class UnitDetailActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private final static String[] tabTitle = new String[]{"角色","装备"};
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit);
        mTabLayout = findViewById(R.id.unit_tab);
        mViewPager = findViewById(R.id.unit_pager);
        setTabs();
        setFragments();

    }

    private void setTabs(){
        if(mTabLayout != null){
            for(String title: tabTitle){
                mTabLayout.addTab(mTabLayout.newTab().setText(title));
            }
        }
    }

    private void setFragments(){
        if(mTabLayout != null){
            List<Fragment> fragments = new ArrayList<>();
            fragments.add(new UnitProfileFragment());
            fragments.add(new UnitEquipmentFragment());
            UnitDetailFragmentAdapter adapter = new UnitDetailFragmentAdapter(getSupportFragmentManager());
            adapter.setFragments(fragments);
            mViewPager.setAdapter(adapter);
            mTabLayout.setupWithViewPager(mViewPager);
        }
    }

    private class UnitDetailFragmentAdapter extends FragmentPagerAdapter{
        private List<Fragment> mFragments;
        private String[] mTitle = tabTitle;

        UnitDetailFragmentAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public int getCount(){
            if(mFragments != null) {
                return mFragments.size();
            }else{
                return 0;
            }
        }

        @Override
        public Fragment getItem(int id){
            return mFragments.get(id);
        }

        @Override
        public CharSequence getPageTitle(int id){
            return mTitle[id];
        }

        void setFragments(List<Fragment> fragments){
            mFragments = fragments;
        }
    }

}
