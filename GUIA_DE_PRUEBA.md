# 🧪 GUÍA DE PRUEBA - APP JUAN 803

## Cómo probar la aplicación

### 1. **COMPILACIÓN** ✅
La aplicación ya está compilada exitosamente. Si necesitas recompilar:

```powershell
# Establecer JAVA_HOME correctamente
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'

# Ejecutar clean build
cd C:\Users\jmedi\AndroidStudioProjects\AppJuan803
.\gradlew.bat clean build
```

---

### 2. **EJECUTAR EN EMULADOR O DISPOSITIVO FÍSICO**

#### Opción A: Desde Android Studio
1. Abre el proyecto en Android Studio
2. Click en "Run" → Selecciona emulador o dispositivo
3. Selecciona "MiProyecto" como Activity principal

#### Opción B: Desde terminal
```powershell
# Instalar en dispositivo conectado
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat installDebug
```

---

### 3. **FLUJO DE PRUEBA**

#### **Pantalla Principal (MiProyecto)**
- Debe mostrar Toolbar azul con ícono "+" (Agregar)
- BottomNavigationView con dos opciones: Administrar y Agenda
- Fragment principal debe ser AdminFragment

#### **AdminFragment - Vista de Citas**
- RecyclerView vacío si es primera vez
- SearchView en la parte superior
- FAB o botón + en Toolbar para agregar cita

---

### 4. **PRUEBA: AGREGAR CITA**

**Pasos:**
1. Toca el botón "+" en la Toolbar
2. Se abre un Modal (AlertDialog)
3. Completa los campos:
   - **Nombre del cliente**: "Juan Pérez"
   - **Teléfono**: "1234567890"
   - **Asunto**: "Consulta"
   - **Día**: Selecciona día del Spinner
   - **Hora**: Toca el ícono reloj → TimePicker → Selecciona hora

4. Toca "Aceptar"
5. **Resultado esperado:**
   - ✅ Modal se cierra
   - ✅ Mensaje Toasty verde (success)
   - ✅ Aparece la cita en el RecyclerView

---

### 5. **PRUEBA: VALIDACIÓN DE CAMPOS**

**Procedimiento:**
1. Abre el modal de agregar cita
2. Intenta agregar sin completar campos
3. Toca "Aceptar"

**Resultado esperado:**
- ✅ Mensaje de error Toasty rojo
- ✅ Modal permanece abierto
- ✅ El campo vacío se resalta

---

### 6. **PRUEBA: VISUALIZAR CITA EN RECYCLERVIEW**

**Estructura del item:**
```
┌─ Credencial   Nombre      Editar Borrar ─┐
├─────────────────────────────────────────┤
├─ Celular      Teléfono                   ┤
├─ Reloj  Hora  Calendario  Día           ┤
├─ Asunto       Descripción                ┤
└─────────────────────────────────────────┘
```

**Verificar:**
- ✅ Nombre se muestra correctamente
- ✅ Teléfono visible
- ✅ Día y Hora correctos
- ✅ Asunto se muestra
- ✅ Ícones son visibles

---

### 7. **PRUEBA: ELIMINAR CITA**

**Pasos:**
1. En un item de cita, toca el botón "Borrar"
2. Se abre AlertDialog de confirmación
3. Toca "Sí"

**Resultado esperado:**
- ✅ Modal de confirmación se cierra
- ✅ Cita desaparece del RecyclerView
- ✅ Mensaje Toasty verde (success)
- ✅ BD actualizada

---

### 8. **PRUEBA: NAVEGACIÓN**

**Pasos:**
1. En la Toolbar, cambia entre fragmentos usando BottomNavigationView
2. Toca "Administrar" → Debe mostrar AdminFragment
3. Toca "Agenda" → Debe mostrar AgendaFragment
4. Regresa a "Administrar"

**Resultado esperado:**
- ✅ Fragmentos cambian correctamente
- ✅ Las citas se mantienen en BD
- ✅ No hay crashes

---

### 9. **PRUEBA: PERSISTENCIA EN BD**

**Pasos:**
1. Agrega 3 citas
2. Cierra completamente la app
3. Abre nuevamente la app
4. Ve a AdminFragment

**Resultado esperado:**
- ✅ Las 3 citas siguen presentes
- ✅ Los datos están correctos
- ✅ No hay pérdida de información

---

### 10. **PRUEBA: TIMEPICKER**

**Pasos:**
1. En el modal de agregar cita, toca el ícono reloj
2. Se abre TimePickerDialog
3. Selecciona una hora (ej: 14:30)
4. Toca "OK"

**Resultado esperado:**
- ✅ La hora se muestra en el campo EditText
- ✅ Mensaje Toasty con la hora seleccionada
- ✅ Formato correcto (HH:MM)

---

### 11. **PRUEBA: SPINNER DE DÍAS**

**Pasos:**
1. En el modal, abre el Spinner de días
2. Selecciona un día

**Resultado esperado:**
- ✅ Dropdown se cierra
- ✅ Día seleccionado se muestra
- ✅ Al guardar, el día correcto se asigna a la cita

---

### 12. **PRUEBA: MESSAGES TOASTY**

**Tipos de mensajes que deberías ver:**
- 🟢 **Success** (Verde): Cita agregada/eliminada correctamente
- 🔴 **Error** (Rojo): Campos vacíos, validación fallida
- 🔵 **Info** (Azul): Hora seleccionada

---

### 13. **PRUEBA: SCROLLING RECYCLERVIEW**

**Con muchas citas:**
1. Agrega 10+ citas
2. Intenta hacer scroll en el RecyclerView
3. Verifica que funciona smooth

**Resultado esperado:**
- ✅ Scroll fluido sin lag
- ✅ CardViews se renderizan correctamente
- ✅ No hay memory leaks visibles

---

## 🐛 Checklist de Validación

- [ ] La app compila sin errores
- [ ] La app no crashea al abrirse
- [ ] El layout se ve correctamente
- [ ] Se pueden agregar citas
- [ ] Se pueden eliminar citas
- [ ] Los datos persisten después de cerrar la app
- [ ] Los mensajes Toasty se muestran
- [ ] El TimePicker funciona
- [ ] El Spinner de días funciona
- [ ] La navegación entre fragmentos funciona
- [ ] Los ícones son visibles
- [ ] El RecyclerView se actualiza
- [ ] No hay crashes en operaciones BD

---

## 📊 Datos de Prueba Sugeridos

```json
{
  "citas_prueba": [
    {
      "nombre": "Juan Pérez",
      "telefono": "1234567890",
      "asunto": "Consulta médica",
      "dia": "Lunes",
      "hora": "09:00"
    },
    {
      "nombre": "María García",
      "telefono": "0987654321",
      "asunto": "Reunión de trabajo",
      "dia": "Miércoles",
      "hora": "14:30"
    },
    {
      "nombre": "Carlos López",
      "telefono": "5551234567",
      "asunto": "Seguimiento",
      "dia": "Viernes",
      "hora": "11:00"
    }
  ]
}
```

---

## 🔍 Posibles Problemas y Soluciones

### Problema: "Clase no encontrada"
**Solución**: 
- Ejecuta `./gradlew clean build`
- Invalida cache de Android Studio (File → Invalidate Caches)

### Problema: Base de datos vacía
**Solución**:
- La BD se crea automáticamente con Room
- Si hay problema, reinstala la app

### Problema: Ícones no visibles
**Solución**:
- Verifica que existan en `drawable/`
- Reinicia Android Studio

### Problema: Toasty no muestra
**Solución**:
- Verifica que la dependencia Toasty esté descargada
- Ejecuta `./gradlew build` nuevamente

### Problema: TimePickerDialog no abre
**Solución**:
- Verifica que el contexto sea válido
- Asegúrate de estar en el Activity/Fragment correcto

---

## 📱 Versiones Recomendadas para Prueba

- **Emulador**: Android 12 (API 31) o superior
- **Dispositivo físico**: Android 8.0 (API 26) o superior
- **Android Studio**: Versión reciente (2023.3+)

---

**¡Listo para probar!** 🚀

