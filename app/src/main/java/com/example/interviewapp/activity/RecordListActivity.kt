package com.example.interviewapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.interviewapp.R
import com.example.interviewapp.adapter.SportRecordAdapter
import com.example.interviewapp.config.AppDatabase
import com.example.interviewapp.entity.RecordSource
import com.example.interviewapp.entity.SportRecord
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class RecordListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chipGroup: ChipGroup
    private lateinit var chipAll: Chip
    private lateinit var chipLocal: Chip
    private lateinit var chipRemote: Chip
    private lateinit var fabAdd: FloatingActionButton

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    private var localRecords = listOf<SportRecord>()
    private var remoteRecords = listOf<SportRecord>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_record_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerViewRecords)
        chipGroup = findViewById(R.id.chipGroupFilter)
        chipAll = findViewById(R.id.chipAll)
        chipLocal = findViewById(R.id.chipLocal)
        chipRemote = findViewById(R.id.chipRemote)

        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chipLocal -> updateList(1)
                R.id.chipRemote -> updateList(2)
                else -> updateList(0)
            }
        }
        fabAdd = findViewById(R.id.fabAdd)

        recyclerView.layoutManager = LinearLayoutManager(this)


        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddRecordActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadRecords()
    }

    private fun loadRecords() {
        loadLocalRecords()
        loadRemoteRecords()
    }

    private fun loadLocalRecords() {
        lifecycleScope.launch {
            val dao = AppDatabase.getInstance(this@RecordListActivity).recordDao()
            localRecords = dao.getAll().map {
                SportRecord(it.id, it.title, it.location, it.durationMinutes, RecordSource.LOCAL)
            }
            updateList(getSelectedFilter())
        }
    }

    private fun loadRemoteRecords() {
        firestore.collection("sport_records")
            .get()
            .addOnSuccessListener { result ->
                remoteRecords = result.documents.mapNotNull { doc ->
                    val title = doc.getString("title") ?: return@mapNotNull null
                    val location = doc.getString("location") ?: return@mapNotNull null
                    val duration = doc.getLong("durationMinutes")?.toInt() ?: return@mapNotNull null
                    SportRecord(doc.id.hashCode(), title, location, duration, RecordSource.REMOTE)
                }
                updateList(getSelectedFilter())
            }
            .addOnFailureListener {
                remoteRecords = emptyList()
                updateList(getSelectedFilter())
            }
    }

    private fun updateList(filterPosition: Int) {
        val records = when (filterPosition) {
            1 -> localRecords
            2 -> remoteRecords
            else -> localRecords + remoteRecords
        }
        recyclerView.adapter = SportRecordAdapter(records)
    }

    private fun getSelectedFilter(): Int {
        val checkedId = chipGroup.checkedChipId
        return when (checkedId) {
            R.id.chipLocal -> 1
            R.id.chipRemote -> 2
            else -> 0 // R.id.chipAll or none
        }
    }
}
