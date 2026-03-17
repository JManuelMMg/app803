package com.example.appjuan803;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {

    // Declarar variables
    Button btnIngresar, btnCancelar;
    EditText txtUsuario, txtPassword;
    String user, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Vincular variables con layout
        btnIngresar = findViewById(R.id.Ingresar);
        btnCancelar = findViewById(R.id.Cancelar);
        txtUsuario = findViewById(R.id.txtUsuario);
        txtPassword = findViewById(R.id.txtPassword);

        // Evento Cancelar
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Evento Ingresar
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    user = txtUsuario.getText().toString();
                    password = txtPassword.getText().toString();

                    // Comparar credenciales
                    if (user.equals("Juan") && password.equals("12345")) {
                        imprimirmensaje("Bienvenido " + user + "!!");

                        //cerrar la actividad y abrirr el siguiente
                        Intent intent=new Intent(login.this, MainActivity.class);
                        startActivity(intent);

                    } else {
                        imprimirmensaje("Datos Incorrectos!!!");
                        limpiarCajas();
                    }
                } catch (Exception e) {
                    // Manejo de error (opcional: registrar o mostrar)
                    imprimirmensaje("Ocurrio un error, Verifica"+e.toString());
                }
            }
        });
    }

    // 🔹 Método para mostrar mensaje (CORREGIDO)
    private void imprimirmensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    // Método para limpiar campos
    private void limpiarCajas() {
        txtUsuario.setText("");
        txtPassword.setText("");
        txtUsuario.requestFocus();
    }
}