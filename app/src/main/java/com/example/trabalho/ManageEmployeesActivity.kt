package com.example.trabalho

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.trabalho.models.User
import com.google.firebase.firestore.FirebaseFirestore

class ManageEmployeesActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var btnAddEmployee: Button
    private lateinit var btnBack: ImageButton
    private lateinit var adapter: ArrayAdapter<String>
    private val userList = mutableListOf<User>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_employees)

        // 1) Botão voltar
        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        // 2) Lista e botão de adicionar
        listView = findViewById(R.id.listEmployees)
        btnAddEmployee = findViewById(R.id.btnAddEmployee)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        // 3) Carrega utilizadores
        loadUsers()

        // 4) Clicar em item para alterar papel
        listView.setOnItemClickListener { _, _, position, _ ->
            showRoleChangeDialog(userList[position])
        }

        // 5) Adicionar funcionário
        btnAddEmployee.setOnClickListener {
            startActivity(Intent(this, RegisterEmployeeActivity::class.java))
        }
    }

    private fun loadUsers() {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                userList.clear()
                val displayList = mutableListOf<String>()
                for (doc in result) {
                    val user = doc.toObject(User::class.java)
                    userList.add(user)
                    displayList.add("${user.name} — ${user.role}")
                }
                adapter.clear()
                adapter.addAll(displayList)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("ManageEmployeesAct", "Erro ao buscar utilizadores", e)
                Toast.makeText(
                    this,
                    "Erro ao carregar utilizadores",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun showRoleChangeDialog(user: User) {
        val roles = arrayOf("gestor", "trabalhador")
        AlertDialog.Builder(this)
            .setTitle("Trocar papel de ${user.name}")
            .setItems(roles) { _, which ->
                val newRole = roles[which]
                db.collection("users")
                    .whereEqualTo("email", user.email)
                    .get()
                    .addOnSuccessListener { docs ->
                        for (doc in docs) {
                            db.collection("users").document(doc.id)
                                .update("role", newRole)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "${user.name} agora é $newRole",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadUsers()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "Erro ao atualizar papel",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Erro ao localizar utilizador",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .show()
    }
}
