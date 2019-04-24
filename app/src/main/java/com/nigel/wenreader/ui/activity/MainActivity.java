package com.nigel.wenreader.ui.activity;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nigel.wenreader.App;
import com.nigel.wenreader.R;
import com.nigel.wenreader.db.entity.BookChapterEntity;
import com.nigel.wenreader.db.entity.CollectBookEntity;
import com.nigel.wenreader.model.repository.BookRepository;
import com.nigel.wenreader.utils.MD5Utils;
import com.nigel.wenreader.utils.PermissionsChecker;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_STORAGE_REQUEST = 1;

    static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    BookRepository repository;
    NavController mNavController;
    PermissionsChecker mPermissionsChecker;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                    start();
                }else {
                    Toast.makeText(this,"You denied the permission",Toast.
                            LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLiteStudioService.instance().start(this);
        init();
        setUpNavigation();
        setUpStatusBarTextColor();
    }

    private void init(){
        mPermissionsChecker=new PermissionsChecker(this);
        repository=BookRepository.getInstance();
    }

    //利用反射，使showAsAction:never的菜单图标和文字一起显示
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Class<?> activityCls = null;
        switch (item.getItemId()) {
            case R.id.action_search:
//                activityCls = SearchActivity.class;
                break;
            case R.id.action_login:
                break;
            case R.id.action_my_message:
                break;
            case R.id.action_download:
//                activityCls = DownloadActivity.class;
                break;
            case R.id.action_sync_bookshelf:
                break;
            case R.id.action_scan_local_book:
                if (mPermissionsChecker == null){
                    mPermissionsChecker = new PermissionsChecker(this);
                }
                //获取读取和写入SD卡的权限
                if (mPermissionsChecker.lacksPermissions(PERMISSIONS)){
                    //请求权限
                    ActivityCompat.requestPermissions(this, PERMISSIONS,PERMISSIONS_STORAGE_REQUEST);
                    return super.onOptionsItemSelected(item);
                }
                activityCls = LocalFileActivity.class;
                break;
            case R.id.action_wifi_book:
                break;
            case R.id.action_feedback:
                break;
            case R.id.action_night_mode:
                break;
            case R.id.action_settings:
                break;
            default:
                break;
        }
        if (activityCls != null){
            Intent intent = new Intent(this, activityCls);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        SQLiteStudioService.instance().stop();
        super.onDestroy();
    }

    private void setUpStatusBarTextColor() {
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void checkPermission(){
        if(mPermissionsChecker.lacksPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(this,PERMISSIONS,PERMISSIONS_STORAGE_REQUEST);
        }else{
//            start();
        }
    }

    private void setUpNavigation() {
        Toolbar toolbar=findViewById(R.id.toolbar);
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNav);
        mNavController=Navigation.findNavController(this,R.id.navHostFragment);
        AppBarConfiguration configuration=new AppBarConfiguration.Builder(
                bottomNavigationView.getMenu()).build();

        setSupportActionBar(toolbar);
        //去掉标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        NavigationUI.setupWithNavController(toolbar,mNavController,configuration);
        NavigationUI.setupWithNavController(bottomNavigationView,mNavController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    private void start(){
        try {
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                File sdCardDir=Environment.getExternalStorageDirectory();
                String path=sdCardDir.getCanonicalPath()+File.separator+"xs"+File.separator+"abc.txt";
                Log.d(TAG, "book: path="+path);
                CollectBookEntity book=new CollectBookEntity();
                book.set_id(MD5Utils.strToMd5By32(path));
                book.setTitle("同居万岁");
                book.setAuthor("霞飞双颊");
                book.setUpdated("2017-05-07T18:24:34.720Z");
                book.setLocal(true);
                book.setCover(path);
                book.setPosition(1);
                repository.saveCollectBook(book);
//                ReadActivity.startActivity(this,book,true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "book: file error");
        }
    }
}
