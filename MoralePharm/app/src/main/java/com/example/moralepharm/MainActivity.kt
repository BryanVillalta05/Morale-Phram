package com.example.moralepharm

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moralepharm.Datos.Medicamentos
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var db: DatabaseReference
    private lateinit var orderItems: ListView
    private lateinit var totalMeds: TextView
    private val medicamentosList = mutableListOf<Medicamentos>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        orderItems = findViewById(R.id.lvOrderItems)
        totalMeds = findViewById(R.id.tvTotalAmount)

        db = FirebaseDatabase.getInstance().getReference("medicamentos")

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, mutableListOf())
        orderItems.adapter = adapter
        orderItems.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        cargarMeds()

        findViewById<Button>(R.id.btnSelectMedicine).setOnClickListener {
            calcTot()
        }
        findViewById<Button>(R.id.btnViewSales).setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun cargarMeds() {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                medicamentosList.clear()
                adapter.clear()

                for (medicamentoSnapshot in snapshot.children) {
                    val medicamento = medicamentoSnapshot.getValue(Medicamentos::class.java)
                    medicamento?.let {
                        medicamentosList.add(it)
                        // Añadir el medicamento al adaptador
                        adapter.add("${it.nombre} - $${it.precio}")
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun calcTot() {
        val itemsck = orderItems.checkedItemPositions
        val selecMeds = mutableListOf<Map<String, Any?>>()
        var total = 0.0

        for (i in 0 until orderItems.count) {
            if (itemsck.get(i)) {
                val medicamento = medicamentosList[i]
                total += medicamento.precio ?: 0.0

                val medicamentoData = mapOf(
                    "nombre" to medicamento.nombre,
                    "precio" to medicamento.precio,
                    "fecha" to System.currentTimeMillis()
                )

                selecMeds.add(medicamentoData)
            }
        }

        totalMeds.text = "$$total"
        Toast.makeText(this, getString(R.string.toast_message), Toast.LENGTH_SHORT).show()

        // Limpiar selección
        orderItems.clearChoices()
        adapter.notifyDataSetChanged()

        sVentas(selecMeds)
    }
    private fun fecForm(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return sdf.format(timestamp)
    }
    private fun sVentas(selecMeds: List<Map<String, Any?>>) {
        val ventasTable = FirebaseDatabase.getInstance().getReference("ventas")
        val ctmp = System.currentTimeMillis()
        val cFech = fecForm(ctmp)

        val saleData = mapOf(
            "fecha" to cFech,
            "medicamentos" to selecMeds
        )

        ventasTable.child(ctmp.toString()).setValue(saleData)
            .addOnSuccessListener {
                Toast.makeText(this, "Venta guardada con éxito", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar la venta", Toast.LENGTH_SHORT).show()
            }
    }



}
