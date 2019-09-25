package com.example.jasontd.rescuepro405;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    private Button mManager;
    private Button mEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmployee = (Button) findViewById(R.id.employee_login);

        //mManager.setBackgroundColor(getResources().getColor(R.color.grey_enabled));
        //mEmployee.setBackgroundColor(getResources().getColor(R.color.grey_enabled));

        mEmployee.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(LoginActivity.this, IndividualActivity.class);
                startActivity(intent);
            }
        });
    }
}
