package ehomeshop.com.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import ehomeshop.com.AddPromotionCodeActivity;
import ehomeshop.com.R;

public class PromotionCodesActivity extends AppCompatActivity {

    private ImageButton backBtn, addPromoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_codes);

        //init ui views
        backBtn = findViewById(R.id.backBtn);
        addPromoBtn = findViewById(R.id.addPromoBtn);

        //handle click, go back
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //handle click, open add promo code activity
        addPromoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PromotionCodesActivity.this, AddPromotionCodeActivity.class));
            }
        });
    }
}