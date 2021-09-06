package com.litton.bingokotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.litton.bingokotlin.databinding.ActivityMainBinding

import java.util.*

class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener, View.OnClickListener {
    private lateinit var binding: ActivityMainBinding

    companion object {
        val TAG = MainActivity::class.java.simpleName
        val RC_SIGN_IN = 100
    }

    var avatarIds = intArrayOf(
        R.drawable.avatar_0,
        R.drawable.avatar_1,
        R.drawable.avatar_2,
        R.drawable.avatar_3,
        R.drawable.avatar_4,
        R.drawable.avatar_5,
        R.drawable.avatar_6
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nickname.setOnClickListener {
            FirebaseAuth.getInstance().currentUser?.let {
                showNicknameDialog(it.uid, binding.nickname.text.toString())
            }
        }

        binding.groupAvatars.visibility = View.GONE
        binding.avatar.setOnClickListener {
            binding.groupAvatars.visibility =
                if (binding.groupAvatars.visibility == View.GONE)
                    View.VISIBLE
                else
                    View.GONE
        }

        binding.avatar0.setOnClickListener(this)
        binding.avatar1.setOnClickListener(this)
        binding.avatar2.setOnClickListener(this)
        binding.avatar3.setOnClickListener(this)
        binding.avatar4.setOnClickListener(this)
        binding.avatar5.setOnClickListener(this)
        binding.avatar6.setOnClickListener(this)

    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_menu_signout -> {
                FirebaseAuth.getInstance().signOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        auth.currentUser?.also {
            Log.d(TAG, it.uid)  //it == true
            FirebaseDatabase.getInstance().getReference("users")
                .child(it.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val member = dataSnapshot.getValue(Member::class.java)
                        member?.nickname?.also { nick ->
                            binding.nickname.setText(nick)
                        } ?: showNicknameDialog(it)
                        member?.let {
                            binding.avatar.setImageResource(avatarIds[it.avatarId])
                        }
                    }

                })
            it.displayName.run {
                FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(it.uid)
                    .child("displayName")
                    .setValue(this)
                    .addOnCompleteListener {
                        Log.d(TAG, "done")
                    }
            }
//            FirebaseDatabase.getInstance().getReference("users")
//                .child(it.uid)
//                .child("nickname")
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onCancelled(error: DatabaseError) {
//
//                    }
//
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        dataSnapshot.value?.also { nick ->
//                            Log.d(TAG, "nickname : $nick")    //nick == true
//                        } ?: showNicknameDialog(it)    //nick == false null
//                    }
//                })
        } ?: signUp()   //it == false null
    }

    private fun showNicknameDialog(uid: String, nick: String?) {
        val editText = EditText(this)
        editText.setText(nick)
        AlertDialog.Builder(this)
            .setTitle("Nickname")
            .setMessage("Your nickname?")
            .setView(editText)
            .setPositiveButton("OK") { dialog, which ->
                FirebaseDatabase.getInstance().getReference("users")
                    .child(uid)
                    .child("nickname")
                    .setValue(editText.text.toString())
            }
            .show()
    }

    private fun showNicknameDialog(user: FirebaseUser) {
        val nick = user.displayName
        val uid = user.uid
        showNicknameDialog(uid, nick)
    }

    private fun MainActivity.signUp() {
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                Arrays.asList(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build()
                )
            ).setIsSmartLockEnabled(false).build(), RC_SIGN_IN
        )
    }

    override fun onClick(v: View?) {
        val selectedId = when (v!!.id) {
            R.id.avatar_0 -> 0
            R.id.avatar_1 -> 1
            R.id.avatar_2 -> 2
            R.id.avatar_3 -> 3
            R.id.avatar_4 -> 4
            R.id.avatar_5 -> 5
            R.id.avatar_6 -> 6
            else -> 0
        }
        FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("avatarId")
            .setValue(selectedId)
        binding.groupAvatars.visibility = View.GONE
    }

}