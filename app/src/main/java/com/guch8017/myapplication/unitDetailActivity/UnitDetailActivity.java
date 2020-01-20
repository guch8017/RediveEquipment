package com.guch8017.myapplication.unitDetailActivity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.guch8017.myapplication.R;
import com.guch8017.myapplication.database.DBUnitComments;
import com.guch8017.myapplication.database.DBUnitData;
import com.guch8017.myapplication.database.DBUnitProfile;
import com.guch8017.myapplication.database.DatabaseReflector;

import java.util.ArrayList;
import java.util.List;

public class UnitDetailActivity extends AppCompatActivity {
    private final static String TAG = "Unit Detail Activity";
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private final static String[] tabTitle = new String[]{"角色","装备"};
    // 角色数据存储区
    private int unitID;
    private DBUnitProfile unitProfile;
    private List<DBUnitComments> unitComments;
    private DBUnitData unitData;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        unitID = getIntent().getIntExtra("unit_id",-1);
        if(unitID == -1){
            Log.e(TAG,"无法获取Intent传值 ERR:unit_id is -1");
            return;
        }
        DatabaseReflector reflector = new DatabaseReflector(this);
        List<DBUnitProfile> profiles = (List<DBUnitProfile>)(Object) reflector.reflectClass(
                DBUnitProfile.class.getName(), "unit_profile",
                "unit_id="+unitID);
        if(profiles == null || profiles.size() == 0){
            Log.e(TAG, "无法从数据库中获取角色数据 ERR:DBUnitProfile is null");
            return;
        }else unitProfile = profiles.get(0);
        unitComments = (List<DBUnitComments>)(Object) reflector.reflectClass(
                DBUnitComments.class.getName(),"unit_comments",
                "unit_id="+unitID);
        List<DBUnitData> data = (List<DBUnitData>)(Object) reflector.reflectClass(
                DBUnitData.class.getName(), "unit_data",
                "unit_id="+unitID);
        if(data == null || data.size() == 0){
            Log.e(TAG, "无法从数据库中获取角色数据 ERR:DBUnitData is null");
            return;
        }else unitData = data.get(0);
        setContentView(R.layout.activity_unit);
        mTabLayout = findViewById(R.id.unit_tab);
        mViewPager = findViewById(R.id.unit_pager);
        setTabs();
        setFragments();
        setTitle(unitProfile.unit_name);
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
