package com.fourtitude.recipeapp.repository;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.fourtitude.recipeapp.models.RecipeModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecipeListRepository {
    private Context context;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ArrayList<RecipeModel> recipeList = new ArrayList<>();
    private MutableLiveData<List<RecipeModel>> recipeMutableList = new MutableLiveData<>();

    public RecipeListRepository(Application application) {
        context = application.getApplicationContext();
        getDataFromFirebase();
    }

    private void getDataFromFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Recipes");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    RecipeModel model = postSnapshot.getValue(RecipeModel.class);
                    recipeList.add(model);
                    recipeMutableList.setValue(recipeList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "sorry there are some error from firebase", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public MutableLiveData<List<RecipeModel>> getAllRecipes() {
        return recipeMutableList;
    }
}
