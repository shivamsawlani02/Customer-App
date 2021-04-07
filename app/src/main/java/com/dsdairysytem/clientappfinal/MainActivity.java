package com.dsdairysytem.clientappfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;
    public DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav=findViewById(R.id.nav_bottom);
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(getIntent().getStringExtra("frag_name") != null) {

            Fragment fragment = null;
            if (getIntent().getStringExtra("frag_name").equals("Order")) {
                fragment = new OrderFragment();
                bottomNav.setSelectedItemId(R.id.order);
            }
            if(getIntent().getStringExtra("frag_name").equalsIgnoreCase("Profile"))
            {
                Log.d("INTENT","TRUE");
                fragment = new ProfileFragment();
                bottomNav.setSelectedItemId(R.id.profile);
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame,new HomeFragment()).commit();
        }

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment=null;
                switch (item.getItemId())
                {
                    case R.id.home:
                        selectedFragment=new HomeFragment();
                        break;
                    case R.id.order:
                        selectedFragment=new OrderFragment();
                        break;
                    case R.id.profile:
                        selectedFragment=new ProfileFragment();
                        break;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frame,selectedFragment).commit();

                return true;
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.connect:
                        Intent QRCode = new Intent(MainActivity.this,QRCodeGenerator.class);
                        startActivity(QRCode);
                        break;
                    case R.id.add_self_log:
                        Intent addSelfLog = new Intent(MainActivity.this,AddMilkman.class);
                        startActivity(addSelfLog);
                        break;
                    case R.id.recommendations:
                        Intent recommend = new Intent(MainActivity.this,Geolocation.class);
                        startActivity(recommend);
                        break;
                    case R.id.change_lang:
                        Intent changeLang = new Intent(MainActivity.this,ChangeLang.class);
                        startActivity(changeLang);
                        finish();
                        break;
                    case R.id.about:
                        break;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        Intent login = new Intent(MainActivity.this,ChooseLang.class);
                        startActivity(login);
                        finish();
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        navigationView.setCheckedItem(R.id.connect);


    }
    @Override
    public void onBackPressed()
    {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }
}