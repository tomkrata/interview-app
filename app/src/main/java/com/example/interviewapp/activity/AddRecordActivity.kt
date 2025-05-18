package com.example.interviewapp.activity

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.interviewapp.R
import com.example.interviewapp.config.AppDatabase
import com.example.interviewapp.entity.RecordSource
import com.example.interviewapp.entity.SportRecord
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class AddRecordActivity : AppCompatActivity() {
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_record)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val titleEditText = findViewById<EditText>(R.id.editTextTitle)
        val locationEditText = findViewById<EditText>(R.id.editTextLocation)
        val durationEditText = findViewById<EditText>(R.id.editTextDuration)
        val radioLocal = findViewById<RadioButton>(R.id.radioLocal)
        val radioRemote = findViewById<RadioButton>(R.id.radioRemote)
        val buttonSave = findViewById<Button>(R.id.buttonSave)

        buttonSave.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val location = locationEditText.text.toString().trim()
            val durationText = durationEditText.text.toString().trim()

            if (title.isEmpty() || location.isEmpty() || durationText.isEmpty()) {
                Toast.makeText(this, "Vyplňte všechna pole", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val duration = durationText.toIntOrNull()
            if (duration == null || duration <= 0) {
                Toast.makeText(this, "Délka musí být kladné číslo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (radioLocal.isChecked) {
                // ✅ Save to local Room DB
                val record = SportRecord(
                    title = title,
                    location = location,
                    durationMinutes = duration,
                    source = RecordSource.LOCAL
                )

                lifecycleScope.launch {
                    AppDatabase.getInstance(this@AddRecordActivity)
                        .recordDao()
                        .insert(record)

                    Toast.makeText(this@AddRecordActivity, "Uloženo do lokální DB", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else if (radioRemote.isChecked) {
                // ✅ Save to Firestore
                val recordMap = mapOf(
                    "title" to title,
                    "location" to location,
                    "durationMinutes" to duration
                )

                firestore.collection("sport_records")
                    .add(recordMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Uloženo do Firestore", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Chyba při ukládání na backend", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
