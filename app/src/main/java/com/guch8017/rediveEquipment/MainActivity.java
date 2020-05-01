package com.guch8017.rediveEquipment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.guch8017.rediveEquipment.util.Constant;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        if(Build.VERSION.SDK_INT >= 23){
            getRWPermission();
        }
        ImageLoader.getInstance().init(Constant.imageLoaderConfiguration(this));
    }

    @TargetApi(23)
    private void getRWPermission(){
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("外部存储权限")
                        .setMessage("程序需要外部存储权限来存储下载的数据")
                        .setPositiveButton("OK",null);
            }else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

}
