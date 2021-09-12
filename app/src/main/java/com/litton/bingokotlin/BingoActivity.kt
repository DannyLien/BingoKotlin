package com.litton.bingokotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.litton.bingokotlin.databinding.ActivityBingoBinding

class BingoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBingoBinding
    private var isCreate: Boolean = false
    lateinit var roomId: String

    companion object {
        val TAG: String = BingoActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_bingo)
        binding = ActivityBingoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        roomId = intent.getStringExtra("ROOM_ID").toString()
        isCreate = intent.getBooleanExtra("IS_CREATE", false)
        Log.d(TAG, "roomId:$roomId")

        for (i in 1..25) {
            FirebaseDatabase.getInstance().getReference("rooms")
                .child(roomId)
                .child("numbers")
                .child(i.toString())
                .setValue(false)
        }
        val buttons: MutableList<NumberButton> = mutableListOf<NumberButton>()
        for (i in 0..24) {
            val button = NumberButton(this)
            button.number = i + 1
            buttons.add(button)
        }
        buttons.shuffle()
        binding.recycler.setHasFixedSize(true)
        binding.recycler.layoutManager = GridLayoutManager(this, 5)
        //Adapter

    }

}






