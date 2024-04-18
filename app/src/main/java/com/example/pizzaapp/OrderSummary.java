package com.example.pizzaapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class OrderSummary extends AppCompatActivity {

    private TextView textViewName, textViewAddress, textViewPhoneNumber, textViewGender, textViewSize, textViewBase, textViewTopping, textViewSpiceLevel, textViewSideDressing, textViewQuantity, textViewDeliveryPickup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        textViewName = findViewById(R.id.textViewName);
        textViewAddress = findViewById(R.id.textViewAddress);
        textViewPhoneNumber = findViewById(R.id.textViewPhoneNumber);
        textViewGender = findViewById(R.id.textViewGender);
        textViewSize = findViewById(R.id.textViewSize);
        textViewBase = findViewById(R.id.textViewBase);
        textViewTopping = findViewById(R.id.textViewTopping);
        textViewSpiceLevel = findViewById(R.id.textViewSpiceLevel);
        textViewSideDressing = findViewById(R.id.textViewSideDressing);
        textViewQuantity = findViewById(R.id.textViewQuantity);
        textViewDeliveryPickup = findViewById(R.id.textViewDeliveryPickup);

        // Retrieve order details from the intent like implemented on Class
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            displayOrderDetails(extras);
        }

        // Button for editing the order
        Button btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Goes back to MainActivity for editing the order
                Intent editIntent = new Intent(OrderSummary.this, MainActivity.class);
                // Pass the existing order details back to MainActivity
                editIntent.putExtras(getIntent().getExtras());
                startActivity(editIntent);
            }
        });

        // Button for confirming the order
        Button btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        btnConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrder();
            }
        });
    }

    //This part is done with help of Google search and youtube video
    private void confirmOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Order for " + textViewName.getText().toString() + " Confirmed. Thank you, Come Again")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Goes back to MainActivity with default values
                        Intent intent = new Intent(OrderSummary.this, MainActivity.class);
                        // Add flags to clear the back stack and start fresh.
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            displayOrderDetails(extras);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save order details
        outState.putString("name", textViewName.getText().toString());
        outState.putString("address", textViewAddress.getText().toString());
        outState.putString("phoneNumber", textViewPhoneNumber.getText().toString());
        outState.putString("gender", textViewGender.getText().toString());
        outState.putString("size", textViewSize.getText().toString());
        outState.putString("base", textViewBase.getText().toString());
        outState.putString("topping", textViewTopping.getText().toString());
        outState.putString("spiceLevel", textViewSpiceLevel.getText().toString());
        outState.putString("dressing", textViewSideDressing.getText().toString());
        outState.putString("quantity", textViewQuantity.getText().toString());
        outState.putString("deliveryOrPickup", textViewDeliveryPickup.getText().toString());

        super.onSaveInstanceState(outState);
    }

    private void displayOrderDetails(Bundle extras) {
        textViewName.setText(extras.getString("name", ""));
        textViewAddress.setText(extras.getString("address", ""));
        textViewPhoneNumber.setText(extras.getString("phoneNumber", ""));
        textViewSize.setText(extras.getString("size", ""));
        textViewBase.setText(extras.getString("base", ""));
        textViewTopping.setText(extras.getString("topping", ""));
        textViewSpiceLevel.setText(extras.getString("spiceLevel", ""));
        textViewSideDressing.setText(extras.getString("dressing", ""));
        textViewQuantity.setText(extras.getString("quantity", ""));
        textViewDeliveryPickup.setText(extras.getString("deliveryOrPickup", ""));

        // Update gender field based on delivery or pickup option (if user decide to go from delivery to pick or vice versa)
        String deliveryOrPickup = extras.getString("deliveryOrPickup", "");
        if (deliveryOrPickup.equals("Delivery")) {
            // Display gender for delivery orders
            textViewGender.setText(extras.getString("gender", ""));
            textViewGender.setVisibility(View.VISIBLE);
        } else {
            // For pickup orders, hide the gender field
            textViewGender.setVisibility(View.GONE);
        }
    }
}