package com.full.firebasedatabase.ui;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.full.firebasedatabase.R;
import com.full.firebasedatabase.adapters.CustomRecyclerViewAdapter;
import com.full.firebasedatabase.jdo.MessageJDO;
import com.full.firebasedatabase.jdo.UserDetailsJDO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, ChildEventListener {

    private FirebaseUser mFirebaseUser;
    private EditText mMessageEDT;
    private RecyclerView mRecyclerView;
    private CustomRecyclerViewAdapter mAdapter;
    private String mUserName;
    private List<MessageJDO> mMessageJDOList;
    private DatabaseReference mFirebaseDBReference;

    private static final String TAG = "ChatActivity";
    private DatabaseReference mOnlineStatusDBReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mMessageEDT = (EditText) findViewById(R.id.message_edt);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        (findViewById(R.id.floating_action_button)).setOnClickListener(this);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserName = mFirebaseUser.isAnonymous() ? "Anonymous User" : mFirebaseUser.getDisplayName();

        Snackbar.make(findViewById(R.id.constraint_layout)
                , "Welcome " + mUserName
                , Snackbar.LENGTH_SHORT).show();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mMessageJDOList = new ArrayList<>();
        mAdapter = new CustomRecyclerViewAdapter(this, mMessageJDOList, mUserName);
        mRecyclerView.setAdapter(mAdapter);

        /*
         * Firebase DB Reference
         */
        mOnlineStatusDBReference = FirebaseDatabase.getInstance().getReference("users");
        mFirebaseDBReference = FirebaseDatabase.getInstance().getReference("messages");
        mFirebaseDBReference.addChildEventListener(ChatActivity.this);
        mFirebaseDBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange: " + dataSnapshot + " " + dataSnapshot.getValue());
                if (dataSnapshot == null || dataSnapshot.getValue() == null || dataSnapshot.getValue().equals("")) {
                    //All Messages Deleted
                    mMessageJDOList.clear();
                    mAdapter.mLastUser = "";
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(ChatActivity.this, "All messages cleared by one of the user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    /**
     * Method to send notifications to all the users who are in the chat room
     */
    private void sendNotificaitons() {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                finish();
                break;
            case R.id.delete_all:
                mFirebaseDBReference.setValue("");
                mMessageJDOList.clear();
                mAdapter.mLastUser = "";
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.view_users:
                startActivity(new Intent(this, ViewUsersActivity.class));
                break;
        }
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mOnlineStatusDBReference.child(mUserName).setValue(new UserDetailsJDO(mUserName, mFirebaseUser.getEmail(), true));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_action_button:
                sendMessage();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mOnlineStatusDBReference.child(mUserName).setValue(new UserDetailsJDO(mUserName, mFirebaseUser.getEmail(), false));
    }

    /**
     * Send Message to firebase Database Common DB for now
     */
    private void sendMessage() {
        // Send message to Firebase DB

        Calendar lCalendar = Calendar.getInstance();
        if (!mMessageEDT.getText().toString().trim().equals("")) {
            mFirebaseDBReference.push().setValue(new MessageJDO(mUserName, mMessageEDT.getText().toString().trim(), String.valueOf(lCalendar.getTimeInMillis())));
            mMessageEDT.setText("");
        } else {
            final Snackbar lSnackbar = Snackbar.make(findViewById(R.id.constraint_layout), "Enter some text to send a message", Snackbar.LENGTH_SHORT);
            lSnackbar.setAction("Dismiss", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lSnackbar.dismiss();
                }
            });
            lSnackbar.show();
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.d(TAG, "onChildAdded: " + dataSnapshot.getValue(MessageJDO.class).getMessage());
        mMessageJDOList.add(dataSnapshot.getValue(MessageJDO.class));
        mAdapter.notifyItemInserted(mMessageJDOList.size());
        mRecyclerView.smoothScrollToPosition(mAdapter.getNewSize());
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
