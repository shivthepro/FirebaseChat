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
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mListView = (ListView) findViewById(R.id.user_list_view);
        mDatabaseReferece = FirebaseDatabase.getInstance().getReference("users");

         mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUser = mFirebaseUser.isAnonymous() ? "Anonymous User" : mFirebaseUser.getDisplayName();

        FirebaseListAdapter<UserDetailsJDO> lFirebaseListAdapter = new FirebaseListAdapter<UserDetailsJDO>(this, UserDetailsJDO.class, R.layout.list_view_users_item, mDatabaseReferece) {
            @Override
            protected void populateView(View view, UserDetailsJDO userDetailsJDO, int i) {
                ((TextView) view.findViewById(R.id.tv_user_name)).setText(userDetailsJDO.getName());
                if (userDetailsJDO.getIsActive())
                    view.findViewById(R.id.iv_is_online).setBackgroundColor(getResources().getColor(R.color.green));
                else
                    view.findViewById(R.id.iv_is_online).setBackgroundColor(getResources().getColor(R.color.red));
;
            }
        };

        mListView.setAdapter(lFirebaseListAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDatabaseReferece.child(mCurrentUser).setValue(new UserDetailsJDO(mCurrentUser, mFirebaseUser.getEmail(), false));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDatabaseReferece.child(mCurrentUser).setValue(new UserDetailsJDO(mCurrentUser, mFirebaseUser.getEmail(), true));
    }
}
