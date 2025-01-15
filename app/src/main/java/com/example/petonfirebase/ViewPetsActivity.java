package com.example.petonfirebase;


import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewPetsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PetAdapter adapter;
    private List<Pet> petList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pets);

        recyclerView = findViewById(R.id.petsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        petList = new ArrayList<>();
        adapter = new PetAdapter(petList);
        recyclerView.setAdapter(adapter);

        // הוספת קו מפריד בין הפריטים
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // טעינת החיות מ-Firestore
        loadPets();
    }

    private void loadPets() {
        db = FirebaseFirestore.getInstance();
        db.collection("pets")
                .orderBy("name")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "שגיאה בטעינת החיות: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    petList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        Pet pet = doc.toObject(Pet.class);
                        pet.setId(doc.getId());
                        petList.add(pet);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}


