package com.example.petonfirebase;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddPetActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView petImageView;
    private EditText petNameEditText;
    private Spinner speciesSpinner;
    private EditText breedEditText;
    private EditText ageEditText;
    private RadioGroup genderRadioGroup;
    private EditText weightEditText;
    private Button birthdayButton;
    private EditText notesEditText;
    private Uri imageUri;
    private String imageBase64;
    private Date selectedBirthday;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        db = FirebaseFirestore.getInstance();
        initializeViews();
        setupSpinner();
        setClickListeners();
    }

    private void initializeViews() {
        petImageView = findViewById(R.id.petImageView);
        petNameEditText = findViewById(R.id.petNameEditText);
        speciesSpinner = findViewById(R.id.speciesSpinner);
        breedEditText = findViewById(R.id.breedEditText);
        ageEditText = findViewById(R.id.ageEditText);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        weightEditText = findViewById(R.id.weightEditText);
        birthdayButton = findViewById(R.id.birthdayButton);
        notesEditText = findViewById(R.id.notesEditText);
    }

    private void setupSpinner() {
        //מתאם בין הנתןנים לבין הרכיב בממשק משתמש ArrayAdapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, // Context
                R.array.species_array,// מזהה המערך ב-resources
                android.R.layout.simple_spinner_item);// Layout
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speciesSpinner.setAdapter(adapter);
    }

    private void setClickListeners() {
        findViewById(R.id.selectImageButton).setOnClickListener(v -> openImagePicker());
        findViewById(R.id.savePetButton).setOnClickListener(v -> savePet());
        birthdayButton.setOnClickListener(v -> showDatePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "בחר תמונה"), PICK_IMAGE_REQUEST);
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year1, monthOfYear, dayOfMonth);
                    selectedBirthday = calendar.getTime();
                    birthdayButton.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .format(selectedBirthday));
                }, year, month, day);
        datePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                bitmap = getResizedBitmap(bitmap, 800); // Max 800px width
                imageBase64 = bitmapToBase64(bitmap);
                petImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "שגיאה בטעינת התמונה", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap getResizedBitmap(Bitmap image, int maxWidth) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width <= maxWidth) return image;

        float ratio = (float) width / height;
        int newHeight = Math.round(maxWidth / ratio);

        return Bitmap.createScaledBitmap(image, maxWidth, newHeight, true);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void savePet() {
        String name = petNameEditText.getText().toString().trim();
        String species = speciesSpinner.getSelectedItem().toString();
        String breed = breedEditText.getText().toString().trim();
        String ageStr = ageEditText.getText().toString().trim();
        String weightStr = weightEditText.getText().toString().trim();
        String notes = notesEditText.getText().toString().trim();

        if (name.isEmpty() || species.isEmpty() || ageStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "אנא מלא את כל השדות החובה", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageBase64 == null) {
            Toast.makeText(this, "אנא בחר תמונה", Toast.LENGTH_SHORT).show();
            return;
        }

        double age = Double.parseDouble(ageStr);
        double weight = Double.parseDouble(weightStr);
        String gender = ((RadioButton)findViewById(genderRadioGroup.getCheckedRadioButtonId()))
                .getText().toString();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("שומר את פרטי החיה...");
        progressDialog.show();

        Pet pet = new Pet(name, species, breed, age, gender, weight, imageBase64, selectedBirthday);
        pet.setNotes(notes);

        db.collection("pets")
                .add(pet)
                .addOnSuccessListener(documentReference -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "החיה נשמרה בהצלחה", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "שגיאה בשמירת החיה: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.e("SaveError", "Error saving pet", e);
                });
    }
}