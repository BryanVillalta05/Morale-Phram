package com.example.moralepharm

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.database.*

class MenuActivity : AppCompatActivity() {

    private lateinit var salesListView: ListView
    private lateinit var db: DatabaseReference
    private lateinit var adapter: ArrayAdapter<String>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        salesListView = findViewById(R.id.lvSales)
        db = FirebaseDatabase.getInstance().getReference("ventas")

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        salesListView.adapter = adapter

        ldMeds()
        findViewById<Button>(R.id.btnMeds).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun ldMeds() {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                adapter.clear()

                for (saleSnapshot in snapshot.children) {
                    val fecha = saleSnapshot.child("fecha").getValue(String::class.java) ?: "Fecha no disponible"
                    val medicamentos = saleSnapshot.child("medicamentos").children.joinToString {
                        val nombre = it.child("nombre").getValue(String::class.java) ?: "Nombre no disponible"
                        val precio = it.child("precio").getValue(Double::class.java) ?: 0.0
                        "$nombre - $$precio"
                    }

                    val saleInfo = "Fecha: $fecha\nMedicamentos:\n$medicamentos"
                    adapter.add(saleInfo)
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MenuActivity, "Error al cargar las ventas", Toast.LENGTH_SHORT).show()
            }
        })
    }
}