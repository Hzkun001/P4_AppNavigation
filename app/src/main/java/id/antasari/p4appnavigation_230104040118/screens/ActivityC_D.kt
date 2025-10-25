package id.antasari.p4appnavigation_230104040118.screens

// ===== imports UI =====
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// ===== imports lifecycle & VM =====
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope

// ===== imports DataStore =====
import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/* -----------------------------------------------------------
   DataStore setup (di file ini saja biar praktis untuk praktikum)
----------------------------------------------------------- */
private val Context.dataStore by preferencesDataStore("form_prefs")
private val KEY_NAME = stringPreferencesKey("name")
private val KEY_NIM  = stringPreferencesKey("nim")

/* -----------------------------------------------------------
   State + ViewModel (satu sumber kebenaran)
----------------------------------------------------------- */
data class FormState(val name: String = "", val nim: String = "")

class FormViewModel(app: Application) : AndroidViewModel(app) {
    private val _state = MutableStateFlow(FormState())
    val state: StateFlow<FormState> = _state.asStateFlow()

    init {
        // Prefill dari DataStore (lulus: DKA & back ke C)
        viewModelScope.launch {
            app.dataStore.data
                .map { p -> FormState(p[KEY_NAME] ?: "", p[KEY_NIM] ?: "") }
                .collect { s -> _state.value = s }
        }
    }

    fun onNameChange(v: String) = _state.update { it.copy(name = v) }
    fun onNimChange(v: String)  = _state.update { it.copy(nim  = v) }

    fun persistNow() = viewModelScope.launch {
        val s = _state.value
        getApplication<Application>().dataStore.edit { p ->
            p[KEY_NAME] = s.name.trim()
            p[KEY_NIM]  = s.nim.trim()
        }
    }
}

/* -----------------------------------------------------------
   Activity C (Form) — pakai VM + DataStore + Snackbar + autosave
----------------------------------------------------------- */
@Composable
fun ActivityCScreen(onSend: (String, String) -> Unit) {
    val vm: FormViewModel = viewModel()
    val ui by vm.state.collectAsState()

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { inner ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Data Input Form",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Name
                    OutlinedTextField(
                        value = ui.name,
                        onValueChange = vm::onNameChange,
                        label = { Text("Name") },
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // NIM
                    OutlinedTextField(
                        value = ui.nim,
                        onValueChange = vm::onNimChange,
                        label = { Text("Student ID (NIM)") },
                        leadingIcon = { Icon(Icons.Outlined.CreditCard, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            // Validasi + Snackbar
                            val nameOk = ui.name.trim().length >= 3
                            val nimOk  = ui.nim.trim().length in 5..20
                            if (!nameOk || !nimOk) {
                                scope.launch {
                                    snackbar.showSnackbar(
                                        when {
                                            !nameOk && !nimOk -> "Nama minimal 3 huruf & NIM 5–20 karakter"
                                            !nameOk -> "Nama minimal 3 huruf"
                                            else -> "NIM 5–20 karakter"
                                        }
                                    )
                                }
                                return@Button
                            }
                            // Persist + navigate
                            vm.persistNow()
                            onSend(ui.name.trim(), ui.nim.trim())
                        },
                        enabled = ui.name.isNotBlank() && ui.nim.isNotBlank(),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Send to Detail")
                    }
                }
            }

            InfoCard(
                title = "Intent Extras",
                bullets = listOf(
                    "Data dikirim sebagai key–value pairs",
                    "Mendukung primitif, String, Parcelable",
                    "Di Compose Navigation, kita gunakan argumen route"
                )
            )
        }
    }

    // Auto-save saat layar C ditutup (pindah ke D/Home) — lulus back & DKA
    DisposableEffect(ui.name, ui.nim) {
        onDispose { vm.persistNow() }
    }
}

/* -----------------------------------------------------------
   Activity D (Display)
----------------------------------------------------------- */
@Composable
fun ActivityDScreen(name: String, nim: String, onResend: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Received Data",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                ElevatedCard {
                    ListItem(
                        headlineContent = { Text("Name") },
                        supportingContent = { Text(name) },
                        leadingContent = { Icon(Icons.Outlined.Person, null) }
                    )
                }
                ElevatedCard {
                    ListItem(
                        headlineContent = { Text("Student ID") },
                        supportingContent = { Text(nim) },
                        leadingContent = { Icon(Icons.Outlined.CreditCard, null) }
                    )
                }

                // Cuplikan edukasi (intent klasik)
                CodeBlock(
                    """
                    val name = intent.getStringExtra("NAME")
                    val studentId = intent.getStringExtra("STUDENT_ID")
                    """.trimIndent()
                )

                OutlinedButton(onClick = onResend) { Text("Resend / Edit") }
            }
        }

        InfoCard(
            title = "Data Flow",
            bullets = listOf(
                "Activity C: user input",
                "Data dikemas (argumen route)",
                "Activity D: tampilkan hasil"
            )
        )
    }
}

/* -----------------------------------------------------------
   Komponen kecil pendukung UI
----------------------------------------------------------- */
@Composable
private fun CodeBlock(text: String) {
    Surface(tonalElevation = 2.dp, shape = MaterialTheme.shapes.medium) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun InfoCard(title: String, bullets: List<String>) {
    Card {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            bullets.forEach { Text("- $it") }
        }
    }
}
