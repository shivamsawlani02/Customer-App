package com.dsdairysytem.clientappfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class AddMilkman extends AppCompatActivity {

    EditText name,mobile;
    Button next;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_milkman);

        name = findViewById(R.id.ed_milkman_name);
        mobile = findViewById(R.id.ed_milkman_mobile);
        next = findViewById(R.id.btnl_next);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddMilkman.this,MainActivity.class));
                finish();
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().isEmpty())
                    name.setError(getString(R.string.name_is_required));
                else if (mobile.getText().toString().length() != 10)
                    mobile.setError(getString(R.string.invalid_mobile_number));
                else {
                    Intent intent = new Intent(AddMilkman.this, AddProductAndUsualOrder.class);
                    intent.putExtra("name", name.getText().toString());
                    intent.putExtra("mobile", "+91" + mobile.getText().toString());
                    startActivity(intent);
                }
            }
        });

    }
}