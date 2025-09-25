package com.example.moneypath

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.moneypath.ui.screens.SingInScreen
import com.example.moneypath.ui.theme.MoneyPathTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInActivity : ComponentActivity() {

    // Firebase аутентифікація
    private lateinit var auth: FirebaseAuth
    // Клієнт Google Sign-In
    private lateinit var googleSignInClient: GoogleSignInClient
    // Launcher для запуску активності з результатом (отримаємо вибраний акаунт)
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1) Ініціалізуємо Firebase Auth
        auth = FirebaseAuth.getInstance()

        // 2) Налаштовуємо Google Sign-In параметри
        // DEFAULT_SIGN_IN — стандартний вхід (тільки базові дані)
        // requestIdToken — потрібно для Firebase авторизації (бере client_id з google-services.json)
        // requestEmail — отримаємо email користувача
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Створюємо GoogleSignInClient з цими параметрами
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 3) Реєструємо launcher для отримання результату вибору акаунта Google
        signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Якщо користувач обрав акаунт
                val data: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Отримуємо акаунт
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account?.idToken
                    if (idToken != null) {
                        // Якщо є токен — передаємо його у Firebase
                        firebaseAuthWithGoogle(idToken)
                    } else {
                        Log.e("SignIn", "ID token is null")
                    }
                } catch (e: ApiException) {
                    Log.w("SignIn", "Google sign in failed", e)
                }
            } else {
                Log.w("SignIn", "Sign-in canceled or failed with code ${result.resultCode}")
            }
        }

        setContent {
            MoneyPathTheme {
                SingInScreen { startSignIn() }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Якщо користувач вже залогінений — відразу йдемо в MainActivity
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // Запуск Google Sign-In: відкриває вікно вибору акаунта
    private fun startSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    // Авторизація у Firebase за допомогою Google токена
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Успішний вхід
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.w("SignIn", "signInWithCredential:failure", task.exception)
                    // показати помилку користувачу (Toast/Alert)
                }
            }
    }
}
