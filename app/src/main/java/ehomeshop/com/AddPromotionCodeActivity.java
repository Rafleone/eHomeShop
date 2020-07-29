package ehomeshop.com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddPromotionCodeActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private EditText promoCodeEt, promoDescriptionEt, promoPriceEt, minimumOrderPriceEt;
    private TextView expireDateTv;
    private Button addBtn;

    //firebase auth
    FirebaseAuth firebaseAuth;

    //progress dialog
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promotion_code);

        //init ui views
        backBtn = findViewById(R.id.backBtn);
        promoCodeEt = findViewById(R.id.promoCodeEt);
        promoDescriptionEt = findViewById(R.id.promoDescriptionEt);
        promoPriceEt = findViewById(R.id.promoPriceEt);
        minimumOrderPriceEt = findViewById(R.id.minimumOrderPriceEt);
        expireDateTv = findViewById(R.id.expireDateTv);
        addBtn = findViewById(R.id.addBtn);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //init setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        //handle click, go back
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //handle click, pick date
        expireDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickDialog();
            }
        });

        //handle click, add promotion code to firebase db
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
    }

    private void datePickDialog() {
        //Get current date to set on calendar
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        //date pick dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                DecimalFormat mFormat = new DecimalFormat("00");
                String pDay = mFormat.format(dayOfMonth);
                String pMonth = mFormat.format(monthOfYear);
                String pYear = ""+ year;
                String pDate = pDay + "/" + pMonth + "/" + pYear; //e.g 27/06/2020

                expireDateTv.setText(pDate);
            }
        }, mYear, mMonth, mDay);

        //show dialog
        datePickerDialog.show();

        //disable paste dates selection on calendar
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
    }

    private String description, promoCode, promoPrice, minimumOrderPrice, expireDate;
    private void  inputData(){
        //input data
        promoCode = promoCodeEt.getText().toString().trim();
        description = promoDescriptionEt.getText().toString().trim();
        promoPrice = promoPriceEt.getText().toString().trim();
        minimumOrderPrice = minimumOrderPriceEt.getText().toString().trim();
        expireDate = expireDateTv.getText().toString().trim();

        //validate form data
        if (TextUtils.isEmpty(promoCode)){
            Toast.makeText(this, "Enter discount code...", Toast.LENGTH_SHORT).show();
            return;//dont procede further
        }
        if (TextUtils.isEmpty(description)){
            Toast.makeText(this, "Enter description...", Toast.LENGTH_SHORT).show();
            return;//dont procede further
        }
        if (TextUtils.isEmpty(promoPrice)){
            Toast.makeText(this, "Enter promotion price...", Toast.LENGTH_SHORT).show();
            return;//dont procede further
        }
        if (TextUtils.isEmpty(minimumOrderPrice)){
            Toast.makeText(this, "Enter minimum order price...", Toast.LENGTH_SHORT).show();
            return;//dont procede further
        }
        if (TextUtils.isEmpty(expireDate)){
            Toast.makeText(this, "Choose expired date...", Toast.LENGTH_SHORT).show();
            return;//dont procede further
        }

        //fields entered, add data to db
        addDataToDb();
    }

    private void addDataToDb() {
        progressDialog.setMessage("Adding Promotion Code...");
        progressDialog.show();

        String timestamp = ""+ System.currentTimeMillis();

        //setup data to add in db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", ""+timestamp);
        hashMap.put("timestamp", ""+timestamp);
        hashMap.put("description", ""+description);
        hashMap.put("promoCode", ""+promoCode);
        hashMap.put("promoPrice", ""+promoPrice);
        hashMap.put("minimumOrderPrice", ""+minimumOrderPrice);
        hashMap.put("expireDate", ""+expireDate);

        //init db reference Users > Current User > Promotions > PromoID > Promo Data
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Promotions").child(timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //code added
                        progressDialog.dismiss();
                        Toast.makeText(AddPromotionCodeActivity.this, "Promotion code added...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //adding code failed
                        progressDialog.dismiss();
                        Toast.makeText(AddPromotionCodeActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}