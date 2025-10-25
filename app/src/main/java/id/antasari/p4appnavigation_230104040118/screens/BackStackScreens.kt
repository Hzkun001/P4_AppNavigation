package id.antasari.p4appnavigation_230104040118.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StepScreen(
    step: Int,
    onNext: (() -> Unit)? = null,
    onClearToHome: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress 1/3, 2/3, 3/3
        LinearProgressIndicator(progress = step / 3f)

        // Ringkasan stack saat ini
        Card {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Current Stack State", fontWeight = FontWeight.SemiBold)
                ElevatedCard {
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Stack depth (approx): ${(step + 1)}")
                        Text("Current screen: Step $step")
                        Text("Previous: ${if (step == 1) "Home" else if (step == 2) "Home → Step 1" else "Home → Step 1 → Step 2"}")
                    }
                }
            }
        }

        if (onNext != null && step < 3) {
            Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
                Text("Continue to Step ${(step + 1)}")
            }
        } else {
            Button(onClick = onClearToHome, modifier = Modifier.fillMaxWidth()) {
                Text("Clear to Home")
            }
        }

        // Visualisasi "stack"
        Card {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Navigation Steps", style = MaterialTheme.typography.titleMedium)
                repeat(3) { i ->
                    val idx = i + 1
                    val mark = if (idx <= step) "✓" else "-"
                    Text(
                        "$mark ${
                            when (idx) {
                                1 -> "First Step"
                                2 -> "Second Step"
                                else -> "Final Step"
                            }
                        }"
                    )
                }
            }
        }
        InfoCard(
            title = "Back Stack Concepts",
            bullets = listOf(
                "Setiap navigasi mendorong layar baru ke back stack",
                "Back system/toolbar akan men-pop layar paling atas",
                "Stack bisa dibersihkan dengan popUpTo/flags untuk kembali ke Home",
                "Android dapat mengelola memori dengan menghentikan layar di belakang"
            )
        )
    }
}

/* Komponen kecil agar seragam */
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

