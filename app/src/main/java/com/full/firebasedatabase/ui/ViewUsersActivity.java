package com.full.firebasedatabase.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.full.firebasedatabase.R;
import com.full.firebasedatabase.jdo.UserDetailsJDO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewUsersActivity extends AppCompatActivity {

    private ListView mListView;
    private DatabaseReference mDatabaseReferece;
    String mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mListView = (ListView) findViewById(R.id.user_list_view);
        mDatabaseReferece = FirebaseDatabase.getInstance().getReference("users");

        FirebaseUser lFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUser = lFirebaseUser.isAnonymous() ? "Anonymous User" : lFirebaseUser.getDisplayName();

        FirebaseListAdapter<UserDetailsJDO> lFirebaseListAdapter = new FirebaseListAdapter<UserDetailsJDO>(this, UserDetailsJDO.class, R.layout.list_view_users_item, mDatabaseReferece) {
            @Override
            protected void populateView(View view, UserDetailsJDO userDetailsJDO, int i) {
                ((TextView) view.findViewById(R.id.tv_user_name)).setText(userDetailsJDO.getName());
                if (userDetailsJDO.getIsActive() || userDetailsJDO.getName().equalsIgnoreCase(mCurrentUser))
                    view.findViewById(R.id.iv_is_online).setBackgroundColor(getResources().getColor(R.color.green));
                else
                    view.findViewById(R.id.iv_is_online).setBackgroundColor(getResources().getColor(R.color.red));
;
            }
        };

        mListView.setAdapter(lFirebaseListAdapter);
    }
}
