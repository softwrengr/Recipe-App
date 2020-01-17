package com.fourtitude.recipeapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fourtitude.recipeapp.R;
import com.fourtitude.recipeapp.adapters.RecipeAdapter;
import com.fourtitude.recipeapp.models.RecipeModel;
import com.fourtitude.recipeapp.viewModels.RecipeListViewModel;

import java.util.List;

public class RecipeListFragment extends Fragment {

    private RecipeListViewModel recipeViewModel;
    private RecipeAdapter recipeAdapter;
    private RecyclerView rvRecipe;
    private LinearLayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recipeViewModel =
                ViewModelProviders.of(this).get(RecipeListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        rvRecipe = root.findViewById(R.id.rv_recipe);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recipeViewModel.getRecipesData().observe(this, new Observer<List<RecipeModel>>() {
            @Override
            public void onChanged(@Nullable List<RecipeModel> availableRecipeList) {
                layoutManager = new LinearLayoutManager(getActivity());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                rvRecipe.setLayoutManager(layoutManager);
                recipeAdapter = new RecipeAdapter(getActivity(), availableRecipeList);
                rvRecipe.setAdapter(recipeAdapter);
            }
        });
    }
}