# 🎥 SOLUCIÓN: Problemas de Permisos de Cámara

## 📋 Problemas Identificados

### ✗ Permisos de CÁMARA NO declarados en AndroidManifest.xml
El archivo `AndroidManifest.xml` estaba **faltando completamente los permisos de cámara y audio**, lo cual es la razón por la que:
- El botón de cámara solo muestra "Solicitar Permisos"
- Al tocar "Solicitar Permisos" no pasa nada
- La cámara no puede inicializarse
- Los permisos no pueden ser otorgados si no están declarados en el manifiesto

---

## ✅ SOLUCIONES APLICADAS

### Permisos añadidos al AndroidManifest.xml

Se añadieron los siguientes permisos **ANTES de la etiqueta `<application>`**:

```xml
<!-- Permisos de cámara -->
<uses-permission android:name="android.permission.CAMERA" />
<!-- Permiso de grabación de audio -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<!-- Permiso de acceso a almacenamiento para guardar fotos y videos -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

**Qué hace cada permiso:**

| Permiso | Propósito |
|---------|----------|
| `CAMERA` | Acceso a la cámara del dispositivo (frontal/trasera) |
| `RECORD_AUDIO` | Grabación de audio para videos |
| `READ_EXTERNAL_STORAGE` | Leer fotos/videos guardados (Android 10+) |
| `WRITE_EXTERNAL_STORAGE` | Guardar fotos y videos tomados (Android 10+) |

---

## 🧪 Cómo funcionan los permisos de cámara

### Flujo de ejecución:

```
App inicia CamaraActivity
        ↓
CameraScreen.kt verifica permisos (línea 58-63)
        ↓
¿Permisos otorgados? 
    └─ ❌ NO → Muestra pantalla "Solicitar Permisos" (línea 209-231)
    └─ ✅ SÍ → Inicializa cámara y muestra preview (línea 328+)
```

### Permisos Dinámicos (Runtime Permissions):

El código ya implementa correctamente **Android Runtime Permissions** usando la librería `Accompanist` (línea 45):

```kotlin
val permissionsState = rememberMultiplePermissionsState(
    permissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )
)

LaunchedEffect(Unit) {
    permissionsState.launchMultiplePermissionRequest()  // Solicita permisos
}
```

**Nota:** Aunque el código solicita automáticamente, ahora con los permisos declarados en el manifiesto, debería funcionar correctamente.

---

## ⏱️ Android 10+ y Almacenamiento

Dado que tu `targetSdk = 36`, es importante saber que:

- **Android 11+** requiere **permisos de almacenamiento especiales**
- El app usa `READ_EXTERNAL_STORAGE` y `WRITE_EXTERNAL_STORAGE` para guardar fotos/videos
- Si quieres mejorar esto en el futuro, puedes usar:
  - `MANAGE_EXTERNAL_STORAGE` para acceso completo
  - O guardar en el almacenamiento específico de la app usando `getExternalFilesDir()`

---

## 📝 Resumen de cambios

### ✏️ Archivo modificado: `app/src/main/AndroidManifest.xml`

**Antes:**
```xml
<!-- Faltaban permisos de cámara -->
<uses-permission android:name="android.permission.INTERNET" />
```

**Después:**
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

---

## 🧪 Cómo probar

1. **Reconstruye el proyecto**: `Build → Rebuild Project` o `Ctrl+F9`
2. **Ejecuta en emulador o dispositivo físico**
3. **Abre el app y ve al menú principal**
4. **Toca el botón "Cámara"**
5. **Deberías ver la pantalla pidiendo permisos**
6. **Toca "Solicitar Permisos" → Acepta en el diálogo del sistema**
7. **Deberías ver el preview de la cámara con los controles de fotos/videos**

---

## 📸 Características de la Cámara

El app implementa:

- ✅ **Preview en tiempo real** de la cámara
- ✅ **Captura de fotos** (toca el botón circular)
- ✅ **Grabación de videos** (modo video)
- ✅ **Detección de QR** (análisis automático)
- ✅ **Detección de objetos** (ML Kit)
- ✅ **Cambiar camara** (frontal/trasera)
- ✅ **Galería** (ver última foto tomada)

---

## ⚠️ Si aún hay problemas

### Problema: Aún muestra "Solicitar Permisos" después de aceptar

**Solución**: 
1. Abre `Settings → Apps → Tu App → Permissions`
2. Activa `Camera` y `Microphone` manualmente
3. Cierra y reabre la app

### Problema: Error de "No se puede inicializar la cámara"

**Posibles causas:**
- El emulador no tiene cámara virtual configurada
- El dispositivo físico está siendo usado por otra app
- El permiso sigue sin ser otorgado

**Solución:**
- En emulador: `Device Settings → Apps → Tu App → Permissions → Camera/Microphone → Allow`
- En dispositivo: Activa los permisos manualmente en Configuración

### Problema: Video no se guarda

**Causa**: Los permisos de almacenamiento deben estar también otorgados
**Solución**: Asegúrate de aceptar todos los permisos cuando se soliciten

---

## 🔗 Referencias

- [Android Permissions (Developer Guide)](https://developer.android.com/guide/topics/permissions/overview)
- [CameraX Documentation](https://developer.android.com/training/camerax)
- [Accompanist Permissions](https://github.com/google/accompanist/tree/main/permissions)
- [Runtime Permissions Best Practices](https://developer.android.com/training/permissions/requesting)

