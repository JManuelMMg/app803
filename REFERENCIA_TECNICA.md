# 🔧 REFERENCIA TÉCNICA RÁPIDA

## 📂 Estructura de Paquetes

```
com.example.appjuan803/
├── AdminFragment.java              (Fragment principal)
├── AgendaFragment.java             (Fragment secundario)
├── MiProyecto.java                 (Activity principal)
├── CitaAdapter.java                (Adapter RecyclerView)
├── Calculadora.java
├── Controles.java
├── GestorDatos.java
├── Gasto.java
├── login.java
├── MainActivity.java
├── menu_inicio.java
│
├── models/
│   └── Cita.java                   (Entidad Room)
│
├── dao/
│   └── CitaDao.java                (Data Access Object)
│
├── database/
│   └── AppDatabase.java            (Base de datos)
│
└── utils/
    └── Utils.java                  (Utilidades)
```

---

## 📦 Dependencias Principales

| Dependencia | Versión | Uso |
|---|---|---|
| Navigation | 2.7.6 | Navegación entre fragmentos |
| Room | 2.7.0 | Base de datos SQLite |
| RecyclerView | 1.3.2 | Listas dinámicas |
| Toasty | 1.5.2 | Mensajes personalizados |

---

## 🗄️ Esquema de Base de Datos

### Tabla: `citas`

| Columna | Tipo | Restricciones |
|---|---|---|
| `id_cita` | TEXT | PRIMARY KEY, NOT NULL |
| `nombre_cliente` | TEXT | |
| `tel_cliente` | TEXT | |
| `asunto_cliente` | TEXT | |
| `hora_cita` | TEXT | |
| `dia_cita` | TEXT | |

---

## 🎨 Colores Utilizados

```xml
<!-- En @color/color_menu.xml -->
<color>@color/md_green_900</color>  <!-- Ícono activo -->
<color>@color/md_blue_500</color>   <!-- Toolbar -->
<color>@color/md_blue_50</color>    <!-- BottomNav fondo -->
```

---

## 🖼️ Recursos Gráficos

### Ícones
- `icon_agregar.png` - Botón agregar cita
- `icon_administrar.png` - Opción Administrar
- `icon_agenda.png` - Opción Agenda
- `icon_credencial.png` - Identificación de cliente
- `icon_celular.png` - Teléfono
- `icon_reloj.png` - Hora
- `icon_calendario.png` - Día
- `icon_asunto.png` - Asunto
- `icon_editar.png` - Editar cita
- `icon_borrar.png` - Eliminar cita

### Drawables
- `shape.xml` - Rectángulo redondeado
- `back_spinner.xml` - Fondo del spinner
- `spinner_background.xml` - Selector de spinner

---

## 🔐 Constantes y IDs

### Fragment IDs (nav.xml)
```xml
@id/action_administrar      <!-- AdminFragment -->
@id/action_agenda            <!-- AgendaFragment -->
```

### Menu IDs (menu_superior.xml)
```xml
@id/action_agregar           <!-- Botón agregar -->
```

### Menu IDs (menu_inferior.xml)
```xml
@id/action_administrar       <!-- Administrar -->
@id/action_agenda            <!-- Agenda -->
```

### Layout IDs (alert_dialog_add_update_cita.xml)
```xml
@id/et_nombre_cliente        <!-- Nombre -->
@id/et_telefono_cliente      <!-- Teléfono -->
@id/et_asunto_cita           <!-- Asunto -->
@id/et_hora_cita             <!-- Hora -->
@id/ibtn_hora                <!-- Botón hora -->
@id/spiDias                  <!-- Spinner días -->
```

### Layout IDs (fragment_admin.xml)
```xml
@id/svCliente                <!-- SearchView -->
@id/rvCitas                  <!-- RecyclerView -->
```

### Layout IDs (item_rv_citas.xml)
```xml
@id/ivCredencial             <!-- Ícono cliente -->
@id/tvNomNombre              <!-- Nombre cliente -->
@id/ibtnEditar               <!-- Botón editar -->
@id/ibtnBorrar               <!-- Botón borrar -->
@id/tvTelCliente             <!-- Teléfono -->
@id/tvHoraCita               <!-- Hora -->
@id/tvDiaCita                <!-- Día -->
@id/tvAsuntoCliente          <!-- Asunto -->
```

### Layout IDs (activity_mi_proyecto.xml)
```xml
@id/ToolBar                  <!-- Toolbar -->
@id/fragmento_principal      <!-- FragmentContainerView -->
@id/barraNavegacion          <!-- BottomNavigationView -->
```

---

## 🔄 Ciclo de Vida AdminFragment

```
onCreate()
    ↓
setHasOptionsMenu(true)
    ↓
onCreateView()
    ↓
setupRecyclerView()  → Configura RecyclerView + Adapter
    ↓
obtenerCitas()       → Carga datos desde BD (AsyncTask)
    ↓
onPostExecute()      → Actualiza UI con datos
    ↓
onCreateOptionsMenu() → Infla menu_superior.xml
    ↓
onOptionsItemSelected() → Maneja clic en botón +
    ↓
lanzarAlertDialogCita() → Abre modal
```

---

## 🔄 Flujo de Agregar Cita

```
Usuario toca "+"
    ↓
lanzarAlertDialogCita()
    ↓
Configura Spinner y TimePicker
    ↓
Usuario completa formulario
    ↓
Usuario toca "Aceptar"
    ↓
validarCampos() → true/false
    ↓ (si true)
Crea objeto Cita
    ↓
agregarCita() → doInBackground()
    ↓
db.citaDao().agregarCita(cita)
    ↓
obtenerCitas() → Recarga desde BD
    ↓
adapter.notifyDataSetChanged()
    ↓
Toasty.success()
```

---

## 🔄 Flujo de Eliminar Cita

```
Usuario toca botón "X"
    ↓
onDeleteClick(cita)
    ↓
AlertDialog de confirmación
    ↓
Usuario confirma
    ↓
eliminarCita() → doInBackground()
    ↓
db.citaDao().eliminarCita(cita)
    ↓
obtenerCitas() → Recarga desde BD
    ↓
adapter.notifyDataSetChanged()
    ↓
Toasty.success()
```

---

## 📋 Métodos Clave

### AdminFragment.java

```java
// Configuración
setupRecyclerView()
obtenerCitas()

// Dialog
lanzarAlertDialogCita()
obtenerHora(EditText)
validarCampos(...)

// CRUD
agregarCita(Cita)
actualizarCita(Cita)
eliminarCita(Cita)

// Callbacks
onEditClick(Cita)
onDeleteClick(Cita)
```

### CitaAdapter.java

```java
// Constructor
CitaAdapter(List<Cita>, listener)

// Override
onCreateViewHolder(...)
onBindViewHolder(...)
getItemCount()

// Actualizador
setCitas(List<Cita>)
```

### Utils.java

```java
// Singleton
getDatabase(Context)
```

### MiProyecto.java

```java
// Configuración
activarMenu()
```

---

## 🎯 Validaciones Implementadas

- ✅ Nombre cliente no vacío
- ✅ Teléfono no vacío
- ✅ Asunto no vacío
- ✅ Hora seleccionada
- ✅ Teléfono máximo 10 caracteres
- ✅ Día obligatorio (Spinner)

---

## 🔧 Configuración de Build

```kotlin
// build.gradle.kts
android {
    namespace = "com.example.appjuan803"
    compileSdk = 36
    
    defaultConfig {
        applicationId = "com.example.appjuan803"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    
    buildFeatures {
        viewBinding = true
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
```

---

## 📱 Atributos del Manifest

```xml
<!-- AndroidManifest.xml debe incluir -->
<application>
    <activity android:name=".MiProyecto"
              android:exported="true">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
</application>
```

---

## 🎨 Estilos Utilizados

```xml
<!-- Material Design Components -->
com.google.android.material.textfield.TextInputLayout
com.google.android.material.textfield.TextInputEditText
com.google.android.material.bottomnavigation.BottomNavigationView

<!-- AndroidX Components -->
androidx.appcompat.widget.Toolbar
androidx.appcompat.widget.SearchView
androidx.fragment.app.FragmentContainerView
androidx.recyclerview.widget.RecyclerView
androidx.cardview.widget.CardView
```

---

## 🚀 Compilación y Ejecución

```bash
# Limpiar
./gradlew clean

# Compilar
./gradlew build

# Instalar en dispositivo
./gradlew installDebug

# Ejecutar
./gradlew runDebug
```

---

## 🔐 Manejo de Errores

### Try-Catch (Recomendado agregar)
```java
try {
    // Operación BD
} catch (Exception e) {
    Toasty.error(context, "Error: " + e.getMessage()).show();
}
```

### Null Checks
```java
if (getActivity() != null) {
    // Usar getActivity()
}
```

---

## 📊 Estadísticas de Código

| Métrica | Valor |
|---|---|
| Archivos Java | 3 principales |
| Layouts XML | 5 principales |
| Drawables | 10+ |
| Clases | 7 |
| Métodos en AdminFragment | 12+ |

---

## ⚡ Optimizaciones Implementadas

- ✅ AsyncTask para BD
- ✅ ViewBinding en lugar de findViewById
- ✅ Singleton para AppDatabase
- ✅ RecyclerView eficiente
- ✅ Manejo correcto de contexto
- ✅ Material Design moderno

---

**Referencia técnica completa** ✨

