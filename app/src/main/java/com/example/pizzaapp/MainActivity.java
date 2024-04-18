package com.example.pizzaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editTextName, editTextAddress, editTextPhoneNumber, editTextQuantity;
    private RadioGroup radioGroupDeliveryPickup;
    private RadioButton radioButtonDelivery, radioButtonPickup;
    private Spinner spinnerGender, spinnerPizzaSize, spinnerPizzaBase, spinnerToppingOptions, spinnerSpiceLevel, spinnerSideDressing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        editTextName = findViewById(R.id.editTextName);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        radioGroupDeliveryPickup = findViewById(R.id.radioGroupDeliveryPickup);
        radioButtonDelivery = findViewById(R.id.radioButtonDelivery);
        radioButtonPickup = findViewById(R.id.radioButtonPickup);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerPizzaSize = findViewById(R.id.spinnerPizzaSize);
        spinnerPizzaBase = findViewById(R.id.spinnerPizzaBase);
        spinnerToppingOptions = findViewById(R.id.spinnerToppingOptions);
        spinnerSpiceLevel = findViewById(R.id.spinnerSpiceLevel);
        spinnerSideDressing = findViewById(R.id.spinnerSideDressing);

        // RadioGroup listener to see if Order is delivery/pickup
        radioGroupDeliveryPickup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonDelivery) {
                // Enable all fields for delivery
                editTextName.setEnabled(true);
                spinnerGender.setEnabled(true);
                editTextAddress.setEnabled(true);
                editTextPhoneNumber.setEnabled(true);
            } else if (checkedId == R.id.radioButtonPickup) {
                // Hide gender and address fields for pickup
                editTextName.setEnabled(true);
                spinnerGender.setEnabled(false);
                editTextAddress.setEnabled(false);
                editTextPhoneNumber.setEnabled(true);
            } else {
                // None of the radio buttons are checked
                Toast.makeText(MainActivity.this, "Please select delivery or pickup", Toast.LENGTH_SHORT).show();
            }
        });

        // Method to Initialize spinners and populate them with options
        populateSpinners();

        // Button for adding quantity
        findViewById(R.id.btnPlus).setOnClickListener(v -> incrementQuantity());
        // Button for subtracting quantity
        findViewById(R.id.btnMinus).setOnClickListener(v -> decrementQuantity());

        // Button for canceling the order
        findViewById(R.id.btnCancel).setOnClickListener(v -> resetForm());
        // Button for placing order
        findViewById(R.id.btnPlaceOrder).setOnClickListener(v -> {
            // Perform validation before placing the order
            if (radioButtonDelivery.isChecked() || radioButtonPickup.isChecked()) {
                if (validateOrder()) {
                    placeOrder();
                }
            } else {
                Toast.makeText(MainActivity.this, "Please select delivery or pickup", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void placeOrder() {
        // Check if pickup is selected
        if (radioButtonPickup.isChecked()) {
            // If pickup is selected, bypass address and gender validation
            // Sends to Summary Activity
            Intent intent = new Intent(MainActivity.this, OrderSummary.class);
            intent.putExtra("name", editTextName.getText().toString());
            intent.putExtra("phoneNumber", editTextPhoneNumber.getText().toString());
            intent.putExtra("gender", spinnerGender.getSelectedItem().toString());
            intent.putExtra("size", spinnerPizzaSize.getSelectedItem().toString());
            intent.putExtra("base", spinnerPizzaBase.getSelectedItem().toString());
            intent.putExtra("topping", spinnerToppingOptions.getSelectedItem().toString());
            intent.putExtra("spiceLevel", spinnerSpiceLevel.getSelectedItem().toString());
            intent.putExtra("dressing", spinnerSideDressing.getSelectedItem().toString());
            intent.putExtra("quantity", editTextQuantity.getText().toString());
            intent.putExtra("deliveryOrPickup", "Pickup");
            startActivity(intent);
        } else {
            // If delivery is selected, perform validation
            if (validateOrder()) {
                // Navigate to Summary Activity
                Intent intent = new Intent(MainActivity.this, OrderSummary.class);
                intent.putExtra("name", editTextName.getText().toString());
                intent.putExtra("address", editTextAddress.getText().toString());
                intent.putExtra("phoneNumber", editTextPhoneNumber.getText().toString());
                intent.putExtra("gender", spinnerGender.getSelectedItem().toString());
                intent.putExtra("size", spinnerPizzaSize.getSelectedItem().toString());
                intent.putExtra("base", spinnerPizzaBase.getSelectedItem().toString());
                intent.putExtra("topping", spinnerToppingOptions.getSelectedItem().toString());
                intent.putExtra("spiceLevel", spinnerSpiceLevel.getSelectedItem().toString());
                intent.putExtra("dressing", spinnerSideDressing.getSelectedItem().toString());
                intent.putExtra("quantity", editTextQuantity.getText().toString());
                intent.putExtra("deliveryOrPickup", "Delivery");
                startActivity(intent);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Retrieve order details if they are already created
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            setOrderDetails(extras);
        }
    }


    private void setOrderDetails(Bundle extras) {
        String name = extras.getString("name");
        String address = extras.getString("address");
        String phoneNumber = extras.getString("phoneNumber");
        String gender = extras.getString("gender");
        String size = extras.getString("size");
        String base = extras.getString("base");
        String topping = extras.getString("topping");
        String spiceLevel = extras.getString("spiceLevel");
        String dressing = extras.getString("dressing");
        String quantity = extras.getString("quantity");
        String deliveryOrPickup = extras.getString("deliveryOrPickup");

        // Set order details to corresponding views
        editTextName.setText(name);
        editTextAddress.setText(address);
        editTextPhoneNumber.setText(phoneNumber);
        spinnerGender.setSelection(getIndex(spinnerGender, gender));
        spinnerPizzaSize.setSelection(getIndex(spinnerPizzaSize, size));
        spinnerPizzaBase.setSelection(getIndex(spinnerPizzaBase, base));
        spinnerToppingOptions.setSelection(getIndex(spinnerToppingOptions, topping));
        spinnerSpiceLevel.setSelection(getIndex(spinnerSpiceLevel, spiceLevel));
        spinnerSideDressing.setSelection(getIndex(spinnerSideDressing, dressing));
        editTextQuantity.setText(quantity);
        if (deliveryOrPickup.equals("Delivery")) {
            radioButtonDelivery.setChecked(true);
        } else {
            radioButtonPickup.setChecked(true);
        }
    }

    private int getIndex(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                    return i;
                }
            }
        }
        return 0;
    }

    // Initialize spinners and populate them with options
    private void populateSpinners() {
        populateSpinner(spinnerGender, R.array.gender_options);
        populateSpinner(spinnerPizzaSize, R.array.pizza_sizes);
        populateSpinner(spinnerPizzaBase, R.array.pizza_bases);
        populateSpinner(spinnerToppingOptions, R.array.topping_options);
        populateSpinner(spinnerSpiceLevel, R.array.spice_levels);
        populateSpinner(spinnerSideDressing, R.array.side_dressings);
    }

    private void populateSpinner(Spinner spinner, int arrayResourceId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                arrayResourceId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void resetForm() {
        // Reset all to their default values
        editTextName.setText("");
        editTextAddress.setText("");
        editTextPhoneNumber.setText("");
        spinnerGender.setSelection(0);
        editTextQuantity.setText("");
        radioButtonPickup.setChecked(true);
        spinnerPizzaSize.setSelection(0);
        spinnerPizzaBase.setSelection(0);
        spinnerToppingOptions.setSelection(0);
        spinnerSpiceLevel.setSelection(0);
        spinnerSideDressing.setSelection(0);
    }

    private void incrementQuantity() {
        // Method to add quantity
        String quantityStr = editTextQuantity.getText().toString();
        int quantity = Integer.parseInt(quantityStr.isEmpty() ? "0" : quantityStr);
        quantity++;
        editTextQuantity.setText(String.valueOf(quantity));
    }

    private void decrementQuantity() {
        // Method to subtract quantity
        String quantityStr = editTextQuantity.getText().toString();
        int quantity = Integer.parseInt(quantityStr.isEmpty() ? "0" : quantityStr);
        if (quantity > 0) {
            quantity--;
            editTextQuantity.setText(String.valueOf(quantity));
        }
    }

    private boolean validateOrder() {
        // Validate order details
        String name = editTextName.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        String quantity = editTextQuantity.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();

        if (name.isEmpty()) {
            editTextName.setError("Please enter your name");
            editTextName.requestFocus();
            return false;
        }

        if (radioButtonDelivery.isChecked() && address.isEmpty()) {
            editTextAddress.setError("Please enter your address");
            editTextAddress.requestFocus();
            return false;
        }

        if (phoneNumber.isEmpty()) {
            editTextPhoneNumber.setError("Please enter your phone number");
            editTextPhoneNumber.requestFocus();
            return false;

        } else if (!isValidPhoneNumber(phoneNumber)) {
            editTextPhoneNumber.setError("Please enter a valid phone number");
            editTextPhoneNumber.requestFocus();
            return false;
        }

        if (quantity.isEmpty()) {
            editTextQuantity.setError("Please enter the quantity");
            editTextQuantity.requestFocus();
            return false;
        } else {
            int quantityValue = Integer.parseInt(quantity);
            if (quantityValue <= 0) {
                editTextQuantity.setError("Please enter a valid quantity");
                editTextQuantity.requestFocus();
                return false;
            }
        }

        if (radioButtonDelivery.isChecked() && address.isEmpty()) {
            editTextAddress.setError("Please enter your address");
            editTextAddress.requestFocus();
            return false;
        }

        if (radioButtonPickup.isChecked() && phoneNumber.isEmpty()) {
            editTextPhoneNumber.setError("Please enter your phone number");
            editTextPhoneNumber.requestFocus();
            return false;
        }

        // Validate gender if it's enabled and required
        if (spinnerGender.isEnabled() && spinnerGender.getSelectedItemPosition() == 0) {
            Toast.makeText(MainActivity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String PHONE_REGEX = "\\d{10}";
        return Pattern.matches(PHONE_REGEX, phoneNumber);
    }
}