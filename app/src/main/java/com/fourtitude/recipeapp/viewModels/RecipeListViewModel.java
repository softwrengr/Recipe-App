package com.fourtitude.recipeapp.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.fourtitude.recipeapp.models.RecipeModel;
import com.fourtitude.recipeapp.repository.RecipeListRepository;

import java.util.List;

public class RecipeListViewModel extends AndroidViewModel {

    private RecipeListRepository recipeRepository;
    private MutableLiveData<List<RecipeModel>> recipeMutableLiveData;


    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        recipeRepository = new RecipeListRepository(application);
        recipeMutableLiveData = recipeRepository.getAllRecipes();
    }

    public MutableLiveData<List<RecipeModel>> getRecipesData() {
        return recipeMutableLiveData;
    }
}