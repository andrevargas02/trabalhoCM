package com.example.trabalho

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.trabalho.models.Issue
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import androidx.appcompat.app.AlertDialog
import com.example.trabalho.IssueListActivity

class CreateIssueActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var imageUri: Uri? = null
    private val PICK_IMAGE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_issue)

        // back
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        // urgências no spinner
        val spinner = findViewById<Spinner>(R.id.spinnerUrgency)
        spinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Baixa", "Média", "Alta")
        )

        // Anexar imagem
        findViewById<Button>(R.id.btnAttach).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Selecionar imagem"), PICK_IMAGE)
        }

        val inputLocation = findViewById<EditText>(R.id.inputLocation)
        inputLocation.setOnClickListener {
            showLocationDialog()
        }

        // Submeter
        findViewById<Button>(R.id.btnSubmitIssue).setOnClickListener {
            val desc = findViewById<EditText>(R.id.inputDescription).text.toString()
            val loc = findViewById<EditText>(R.id.inputLocation).text.toString()
            val urg = spinner.selectedItem.toString()

            if (desc.isBlank() || loc.isBlank()) {
                Toast.makeText(this, "Preenche todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (imageUri == null) {
                // sem anexo -> guarda diretamente
                saveIssue(desc, urg, loc, "")
            } else {
                // faz upload primeiro
                uploadImageThenSave(desc, urg, loc)
            }
        }
    }

    private fun showLocationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_location, null)

        val spinnerInstitution = dialogView.findViewById<Spinner>(R.id.spinnerInstitution)
        val spinnerLocation = dialogView.findViewById<Spinner>(R.id.spinnerLocation)
        val editRoom = dialogView.findViewById<EditText>(R.id.editRoom)

        val institutions = listOf("ESTG", "ESS", "ESE")
        val locations = listOf("Biblioteca", "Laboratório", "Sala de Aula")

        spinnerInstitution.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, institutions)
        spinnerLocation.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locations)

        AlertDialog.Builder(this)
            .setTitle("Selecionar Localização")
            .setView(dialogView)
            .setPositiveButton("Confirmar") { _, _ ->
                val institution = spinnerInstitution.selectedItem.toString()
                val location = spinnerLocation.selectedItem.toString()
                val room = editRoom.text.toString()

                val result = "$institution - $location - $room"
                findViewById<EditText>(R.id.inputLocation).setText(result)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun uploadImageThenSave(desc: String, urg: String, loc: String) {
        val fileName = UUID.randomUUID().toString()
        val ref = storage.reference.child("issue_images/$fileName")

        ref.putFile(imageUri!!)
            .continueWithTask { ref.downloadUrl }
            .addOnSuccessListener { uri ->
                saveIssue(desc, urg, loc, uri.toString())
            }
            .addOnFailureListener {
                Toast.makeText(this, "Falha no upload da imagem", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveIssue(desc: String, urg: String, loc: String, imgPath: String) {
        val uid = auth.currentUser?.uid ?: return
        val issue = Issue(
            description = desc,
            urgency = urg,
            status = "ativa",
            image_path = imgPath,
            createdAt = System.currentTimeMillis(),
            createdBy = uid,
            technicianId = "",
            location_id = loc
        )

        db.collection("issues").add(issue)
            .addOnSuccessListener {
                Toast.makeText(this, "Avaria submetida!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, IssueListActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao gravar avaria", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            Toast.makeText(this, "Imagem selecionada", Toast.LENGTH_SHORT).show()
        }
    }
}
