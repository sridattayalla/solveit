package com.example.solveit;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    LayoutInflater layoutInflater;
    RecyclerView appsRecyclerView;
    ArrayList<ResolveInfo> pkgAppsList = new ArrayList<>(), customList = new ArrayList<>();
    AppsAdapter appsAdapter = new AppsAdapter();
    EditText searchEditText;
    LinearLayout searchLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton diallerButton;
    String searchVal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initializing ui
        searchEditText = findViewById(R.id.search_et);
        searchLayout = findViewById(R.id.search_layout);
        swipeRefreshLayout = findViewById(R.id.srl);
        diallerButton = findViewById(R.id.dialler_button);
        appsRecyclerView = findViewById(R.id.apps_container);
        appsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        appsRecyclerView.setHasFixedSize(true);
        appsRecyclerView.setAdapter(appsAdapter);
        //inflater
        layoutInflater=LayoutInflater.from(getApplicationContext());

        populateApps();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateApps();
            }
        });

        final List<ResolveInfo> dialerPackages = AppCategoryHelper.getPackagesOfDialerApps(getApplicationContext());
        if(dialerPackages.size() > 0){
            diallerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityInfo activity= dialerPackages.get(0).activityInfo;
                    ComponentName name=new ComponentName(activity.applicationInfo.packageName,
                            activity.name);
                    Intent i=new Intent(Intent.ACTION_MAIN);

                    i.addCategory(Intent.CATEGORY_LAUNCHER);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    i.setComponent(name);

                    startActivity(i);
                }
            });
        }

    }

    void retainSearchResults(String val){
        customList.clear();
        if(searchVal.equals("")){
            customList.addAll(pkgAppsList);
        }
        else {
            Log.d("threade", Integer.toString(pkgAppsList.size()));
            for (int i = 0; i < pkgAppsList.size(); i++) {
                ResolveInfo temp = pkgAppsList.get(i);
                Log.d("threade", temp.loadLabel(getPackageManager()).toString());
                if (temp.loadLabel(getPackageManager()).toString().toUpperCase().startsWith(val.toUpperCase())) {
                    Log.d("threade", searchVal);
                    customList.add(temp);
                }
            }
        }
    }

    //get apps and populate data
    void populateApps(){
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        pkgAppsList = (ArrayList<ResolveInfo>) getApplicationContext().getPackageManager().queryIntentActivities( mainIntent, 0);
        Collections.sort(pkgAppsList, new ResolveInfo.DisplayNameComparator(getPackageManager()));
        for(int i=0; i<pkgAppsList.size(); i++){
            Log.d("package names", pkgAppsList.get(i).loadLabel(getPackageManager()).toString());
        }
        // maintaining [pkgAppsList] untouched as we are going to use it as base every time user searches
        retainSearchResults(searchVal);
        swipeRefreshLayout.setRefreshing(false);
        appsAdapter.notifyDataSetChanged();
    }

    class AppsAdapter extends RecyclerView.Adapter<AppsViewHolder>{
        View v;
        @NonNull
        @Override
        public AppsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            v = layoutInflater.inflate(R.layout.single_app, parent, false);
            return new AppsViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull AppsViewHolder holder, int position) {
            holder.setDetails(customList.get(position));
        }

        @Override
        public int getItemCount() {
            return customList.size();
        }
    }

    class AppsViewHolder extends RecyclerView.ViewHolder{
        View v;
        public AppsViewHolder(@NonNull View itemView) {
            super(itemView);
            v = itemView;


        }

        public void setDetails(final ResolveInfo appInfo){
            TextView appNameTextView = v.findViewById(R.id.app_name_tv);
            appNameTextView.setText(appInfo.loadLabel(getPackageManager()).toString());

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityInfo activity= appInfo.activityInfo;
                    Log.d("actname", activity.name);
//                    ComponentName name=new ComponentName(activity.applicationInfo.packageName,
//                            activity.name);
//                    Intent i=new Intent(Intent.ACTION_MAIN);
//
//                    i.addCategory(Intent.CATEGORY_LAUNCHER);
//                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
//                            Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                    i.setComponent(name);
//
//                    startActivity(i);

                    android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                    CalcDialogFragment dialogFragment = new CalcDialogFragment();
                    Bundle args = new Bundle();
                    args.putString("packageName", activity.applicationInfo.packageName);
                    args.putString("activityName", activity.name);
                    dialogFragment.setArguments(args);
//                    dialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogFragmentTheme);
                    dialogFragment.show(ft, "dialog");
                }
            });
        }
    }
}

