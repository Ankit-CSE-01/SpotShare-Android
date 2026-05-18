@file:Suppress("DEPRECATION")

package com.spotshare.presentation.screens.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.spotshare.presentation.theme.Primary
import com.spotshare.presentation.theme.Secondary
import com.spotshare.presentation.theme.SpotShareTheme
import com.spotshare.R
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToSignUp: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    
    LoginContent(
        authState = authState,
        onLoginClick = { email, password -> viewModel.login(email, password) },
        onGoogleSignInClick = { idToken -> viewModel.onGoogleSignIn(idToken) },
        onResetPasswordClick = { email -> viewModel.resetPassword(email) },
        onLoginSuccess = onLoginSuccess,
        onNavigateToSignUp = onNavigateToSignUp
    )
}

@Composable
fun LoginContent(
    authState: AuthState,
    onLoginClick: (String, String) -> Unit,
    onGoogleSignInClick: (String) -> Unit,
    onResetPasswordClick: (String) -> Unit,
    onLoginSuccess: (String) -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val googleSignInOptions = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, googleSignInOptions) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                onGoogleSignInClick(idToken)
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar("Google Sign-In failed: ID Token is null.")
                }
            }
        } catch (e: ApiException) {
            scope.launch {
                snackbarHostState.showSnackbar("Google Sign-In Error: ${e.statusCode}")
            }
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            val userEmail = authState.email
            snackbarHostState.showSnackbar("Welcome to SpotShare, $userEmail!")
            onLoginSuccess(userEmail)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Primary, Secondary)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome Back",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator()
                    } else {
                        Button(
                            onClick = { onLoginClick(email, password) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Log In")
                        }
                    }

                    if (authState is AuthState.Error) {
                        Text(
                            text = authState.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    TextButton(onClick = { onResetPasswordClick(email) }) {
                        Text("Forgot Password?")
                    }
                    
                    if (authState is AuthState.PasswordResetSent) {
                        Text(
                            text = "Reset link sent to your email!",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    TextButton(
                        onClick = onNavigateToSignUp,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Don't have an account? Sign Up")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    OutlinedButton(
                        onClick = { launcher.launch(googleSignInClient.signInIntent) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Sign in with Google")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    SpotShareTheme {
        LoginContent(
            authState = AuthState.Idle,
            onLoginClick = { _, _ -> },
            onGoogleSignInClick = {},
            onResetPasswordClick = {},
            onLoginSuccess = {},
            onNavigateToSignUp = {}
        )
    }
}
