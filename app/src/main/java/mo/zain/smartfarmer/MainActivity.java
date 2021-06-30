package mo.zain.smartfarmer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavController navController = Navigation.findNavController(this, R.id.fragment);
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bottom);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);

    }
}