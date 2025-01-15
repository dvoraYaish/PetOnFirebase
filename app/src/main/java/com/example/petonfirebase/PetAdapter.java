package com.example.petonfirebase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {
    private List<Pet> petList;

    public PetAdapter(List<Pet> petList) {
        this.petList = petList;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pet_item, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);
        holder.bind(pet);
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        private ImageView petImage;
        private TextView petName;
        private TextView petSpecies;
        private TextView petBreed;
        private TextView petAge;
        private TextView petWeight;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            petImage = itemView.findViewById(R.id.petItemImage);
            petName = itemView.findViewById(R.id.petItemName);
            petSpecies = itemView.findViewById(R.id.petItemSpecies);
            petBreed = itemView.findViewById(R.id.petItemBreed);
            petAge = itemView.findViewById(R.id.petItemAge);
            petWeight = itemView.findViewById(R.id.petItemWeight);
        }

        public void bind(Pet pet) {
            // הצגת התמונה מ-Base64
            if (pet.getImageBase64() != null) {
                byte[] decodedString = Base64.decode(pet.getImageBase64(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                petImage.setImageBitmap(bitmap);
            }

            petName.setText(pet.getName());
            petSpecies.setText(pet.getSpecies());
            petBreed.setText(pet.getBreed());
            petAge.setText(String.format("גיל: %.1f", pet.getAge()));
            petWeight.setText(String.format("משקל: %.1f ק\"ג", pet.getWeight()));

            // הוספת לחיצה על הכרטיסייה
            itemView.setOnClickListener(v -> {
                // כאן אפשר להוסיף מעבר למסך פרטים מלא
                Context context = itemView.getContext();
                Toast.makeText(context, "נבחר: " + pet.getName(), Toast.LENGTH_SHORT).show();
            });
        }
    }
}