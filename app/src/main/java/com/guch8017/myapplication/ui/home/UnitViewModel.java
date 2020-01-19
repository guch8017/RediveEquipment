package com.guch8017.myapplication.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.guch8017.myapplication.database.DBUnitProfile;

import java.util.List;

public class UnitViewModel extends ViewModel {

    private MutableLiveData<List<DBUnitProfile>> mText;

    public UnitViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(null);
    }

    public LiveData<List<DBUnitProfile>> getProfileList() {
        return mText;
    }
    public void setProfileList(List<DBUnitProfile> profiles){
        mText.setValue(profiles);
    }
}