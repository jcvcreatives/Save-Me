package com.example.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.DoodleColors
import com.example.DoodleWordTitleHeader
import com.example.R
import com.example.SketchyContainer
import com.example.data.PlayerAccount
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

private const val TAG = "RegistrationAuthScreen"

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun RegistrationAuthScreen(
    currentUser: FirebaseUser?,
    cloudAccount: PlayerAccount?,
    isGuestSession: Boolean = false,
    isSyncing: Boolean,
    authErrorMessage: String?,
    localLevelNumber: Int,
    localCoins: Int,
    completedLevelsCount: Int = 0,
    fontFamily: FontFamily,
    onGoogleIdTokenReceived: (idToken: String, preferredNickname: String?, preferredEmail: String?, authMethod: String) -> Unit,
    onUpdateCloudProfile: (nickname: String, email: String?, authMethod: String) -> Unit,
    onContinueAsGuest: () -> Unit,
    onContinueToGame: () -> Unit,
    onSignOut: () -> Unit,
    onSetAuthError: (String?) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var nicknameInput by remember(cloudAccount?.displayName, currentUser?.displayName, isGuestSession) {
        mutableStateOf(
            if (currentUser != null) {
                cloudAccount?.displayName
                    ?: currentUser.displayName?.takeIf { it.isNotBlank() }
                    ?: ""
            } else {
                ""
            }
        )
    }
    var emailInput by remember(cloudAccount?.email, currentUser?.email, isGuestSession) {
        mutableStateOf(
            if (currentUser != null) {
                cloudAccount?.email
                    ?: currentUser.email?.takeIf { it.isNotBlank() }
                    ?: ""
            } else {
                ""
            }
        )
    }
    var showCreateAccountForm by remember { mutableStateOf(true) }
    var isLaunchingCredentialManager by remember { mutableStateOf(false) }

    fun triggerGoogleSignIn(authMethod: String) {
        val activity = context.findActivity()
        if (activity == null) {
            onSetAuthError("Unable to launch Google Sign-In: Activity context unavailable.")
            return
        }
        if (authMethod == "account" && nicknameInput.trim().isEmpty()) {
            onSetAuthError("Please enter a Doodle Nickname to create your account!")
            return
        }

        onSetAuthError(null)
        isLaunchingCredentialManager = true
        coroutineScope.launch {
            try {
                val credentialManager = CredentialManager.create(activity)
                val webClientId = context.getString(R.string.default_web_client_id)
                val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(
                    serverClientId = webClientId
                ).build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(signInWithGoogleOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = activity
                )
                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    onGoogleIdTokenReceived(
                        googleIdTokenCredential.idToken,
                        nicknameInput.trim().ifEmpty { googleIdTokenCredential.displayName },
                        emailInput.trim().ifEmpty { googleIdTokenCredential.id },
                        authMethod
                    )
                } else {
                    onSetAuthError("Unexpected credential type returned from Google Sign-In.")
                }
            } catch (e: GetCredentialCancellationException) {
                Log.i(TAG, "User cancelled Google Sign-In", e)
                onSetAuthError("Google Sign-In was cancelled. You can try again or Log-in as Guest.")
            } catch (e: NoCredentialException) {
                Log.w(TAG, "No Google account on device", e)
                onSetAuthError(
                    "No Google account found on this device/emulator. Add a Google account in Android Settings or tap 'Log-in as Guest' to play immediately!"
                )
            } catch (e: GetCredentialException) {
                Log.e(TAG, "Credential Manager error", e)
                onSetAuthError(
                    e.localizedMessage ?: "Could not complete Google Sign-In. Try again or Log-in as Guest."
                )
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected sign-in error", e)
                onSetAuthError(e.localizedMessage ?: "Authentication error occurred.")
            } finally {
                isLaunchingCredentialManager = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 560.dp)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .testTag("registration_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Header Row: Optional Back Button
        if (currentUser != null || isGuestSession) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                SketchyContainer(
                    fillColor = Color.White,
                    borderColor = DoodleColors.InkBlack,
                    cornerRadius = 12.dp,
                    onClick = onContinueToGame,
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("reg_back_to_menu_button")
                ) {
                    Text(
                        text = "← Back",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = DoodleColors.InkBlack,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }

        // Doodle Stickman Welcome Illustration + WORD Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(92.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(92.dp)) {
                val cx = size.width * 0.5f
                val cy = size.height * 0.5f
                // Soft yellow sketch halo
                drawCircle(
                    color = DoodleColors.DoodleYellow.copy(alpha = 0.45f),
                    radius = size.minDimension * 0.44f,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = DoodleColors.InkBlack,
                    radius = size.minDimension * 0.44f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 3.5f)
                )
                // Stickman head
                val headCenter = Offset(cx, cy - 16f)
                drawCircle(
                    color = Color.White,
                    radius = 15f,
                    center = headCenter
                )
                drawCircle(
                    color = DoodleColors.InkBlack,
                    radius = 15f,
                    center = headCenter,
                    style = Stroke(width = 4f)
                )
                // Eyes & happy smile
                drawCircle(color = DoodleColors.InkBlack, radius = 2.2f, center = Offset(cx - 5f, cy - 18f))
                drawCircle(color = DoodleColors.InkBlack, radius = 2.2f, center = Offset(cx + 5f, cy - 18f))
                drawArc(
                    color = DoodleColors.InkBlack,
                    startAngle = 20f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(cx - 6f, cy - 16f),
                    size = androidx.compose.ui.geometry.Size(12f, 9f),
                    style = Stroke(width = 3f, cap = StrokeCap.Round)
                )
                // Body & waving arms
                drawLine(
                    color = DoodleColors.InkBlack,
                    start = Offset(cx, cy - 1f),
                    end = Offset(cx, cy + 22f),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = DoodleColors.InkBlack,
                    start = Offset(cx, cy + 6f),
                    end = Offset(cx - 18f, cy - 8f),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = DoodleColors.InkBlack,
                    start = Offset(cx, cy + 6f),
                    end = Offset(cx + 18f, cy - 10f),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
                // Legs
                drawLine(
                    color = DoodleColors.InkBlack,
                    start = Offset(cx, cy + 22f),
                    end = Offset(cx - 12f, cy + 36f),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = DoodleColors.InkBlack,
                    start = Offset(cx, cy + 22f),
                    end = Offset(cx + 12f, cy + 36f),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
            }
        }

        DoodleWordTitleHeader(fontFamily = fontFamily)

        Text(
            text = if (currentUser != null) {
                "Welcome back! Ready to play."
            } else {
                "Log in to your Account, Create a New Account, or Play as Guest"
            },
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = DoodleColors.InkBlue,
            textAlign = TextAlign.Center
        )

        // Error or info banner
        AnimatedVisibility(visible = !authErrorMessage.isNullOrBlank()) {
            SketchyContainer(
                fillColor = Color(0xFFFFEBEE),
                borderColor = DoodleColors.WrongRed,
                cornerRadius = 14.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_error_banner")
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = authErrorMessage.orEmpty(),
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFFB71C1C),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (currentUser != null) {
            // Signed-In Account Card
            SketchyContainer(
                fillColor = Color(0xFFE8F5E9),
                borderColor = DoodleColors.InkBlack,
                cornerRadius = 18.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signed_in_account_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "👤 ${cloudAccount?.displayName ?: currentUser.displayName ?: "Doodle Player"}",
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = DoodleColors.InkBlack
                            )
                            val activeEmail = cloudAccount?.email ?: currentUser.email
                            if (!activeEmail.isNullOrBlank()) {
                                Text(
                                    text = activeEmail,
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = DoodleColors.InkBlue
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(DoodleColors.CorrectGreen.copy(alpha = 0.25f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "SYNCED",
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }

                    Text(
                        text = "Campaign: Level $localLevelNumber / 1000  •  Coins: ⭐ $localCoins",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DoodleColors.InkBlack
                    )

                    OutlinedTextField(
                        value = nicknameInput,
                        onValueChange = { nicknameInput = it.take(50) },
                        label = { Text("Update Doodle Nickname", fontFamily = fontFamily) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DoodleColors.InkBlack,
                            unfocusedBorderColor = DoodleColors.InkBlack.copy(alpha = 0.6f),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reg_nickname_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SketchyContainer(
                            fillColor = DoodleColors.DoodleYellow,
                            cornerRadius = 14.dp,
                            onClick = {
                                onUpdateCloudProfile(
                                    nicknameInput.trim().ifEmpty { "Doodle Player" },
                                    emailInput.trim().ifEmpty { currentUser.email },
                                    cloudAccount?.authMethod ?: "account"
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("save_cloud_profile_button")
                        ) {
                            Text(
                                text = if (isSyncing) "Syncing..." else "💾 Save & Sync",
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = DoodleColors.InkBlack
                            )
                        }

                        SketchyContainer(
                            fillColor = Color(0xFFFFCDD2),
                            cornerRadius = 14.dp,
                            onClick = {
                                nicknameInput = ""
                                emailInput = ""
                                onSignOut()
                            },
                            modifier = Modifier
                                .height(48.dp)
                                .padding(horizontal = 4.dp)
                                .testTag("sign_out_button")
                        ) {
                            Text(
                                text = "Sign Out",
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = Color(0xFFB71C1C),
                                modifier = Modifier.padding(horizontal = 14.dp)
                            )
                        }
                    }

                    SketchyContainer(
                        fillColor = DoodleColors.CorrectGreen,
                        cornerRadius = 22.dp,
                        onClick = onContinueToGame,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("continue_to_game_button")
                    ) {
                        Text(
                            text = "▶ Play Level $localLevelNumber",
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            // 0. LOG INTO DOODLE WORD CARD (For already registered players - matches sample layout)
            var loginIdentifier by remember { mutableStateOf("") }
            var loginPassword by remember { mutableStateOf("") }
            var createPassword by remember { mutableStateOf("") }

            SketchyContainer(
                fillColor = Color.White,
                borderColor = DoodleColors.InkBlack,
                cornerRadius = 18.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_account_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Log into Doodle Word",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = DoodleColors.InkBlack
                    )

                    OutlinedTextField(
                        value = loginIdentifier,
                        onValueChange = { loginIdentifier = it.take(150) },
                        label = { Text("Email or Doodle Nickname", fontFamily = fontFamily) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DoodleColors.InkBlack,
                            unfocusedBorderColor = DoodleColors.InkBlack.copy(alpha = 0.6f),
                            focusedContainerColor = DoodleColors.PaperOffWhite,
                            unfocusedContainerColor = DoodleColors.PaperOffWhite
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_identifier_input")
                    )

                    OutlinedTextField(
                        value = loginPassword,
                        onValueChange = { loginPassword = it.take(100) },
                        label = { Text("Password", fontFamily = fontFamily) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DoodleColors.InkBlack,
                            unfocusedBorderColor = DoodleColors.InkBlack.copy(alpha = 0.6f),
                            focusedContainerColor = DoodleColors.PaperOffWhite,
                            unfocusedContainerColor = DoodleColors.PaperOffWhite
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input")
                    )

                    SketchyContainer(
                        fillColor = Color(0xFF0064E0),
                        borderColor = Color(0xFF004AAD),
                        cornerRadius = 24.dp,
                        onClick = {
                            val cleanId = loginIdentifier.trim()
                            if (cleanId.isEmpty()) {
                                onSetAuthError("Please enter your Email or Doodle Nickname to log in!")
                            } else if (loginPassword.isEmpty()) {
                                onSetAuthError("Please enter your password to log in!")
                            } else if (!isLaunchingCredentialManager && !isSyncing) {
                                if (cleanId.contains("@")) {
                                    emailInput = cleanId
                                    if (nicknameInput.isBlank()) {
                                        nicknameInput = cleanId.substringBefore("@")
                                    }
                                } else {
                                    nicknameInput = cleanId
                                }
                                triggerGoogleSignIn(authMethod = "account")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_submit_button")
                    ) {
                        Text(
                            text = "Log in",
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSetAuthError(
                                    "Enter your Email or Doodle Nickname above and sign in with Google to restore your cloud account!"
                                )
                            }
                            .padding(vertical = 4.dp)
                            .testTag("forgot_password_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Forgot password?",
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DoodleColors.InkBlack
                        )
                    }

                    SketchyContainer(
                        fillColor = Color.White,
                        borderColor = Color(0xFF0064E0),
                        cornerRadius = 24.dp,
                        onClick = {
                            showCreateAccountForm = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("open_create_account_button")
                    ) {
                        Text(
                            text = "Create new account",
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = Color(0xFF0064E0)
                        )
                    }
                }
            }

            // 1. CREATE ACCOUNT CARD (Doodle Nickname + Gmail / Google Cloud Profile)
            SketchyContainer(
                fillColor = Color.White,
                borderColor = DoodleColors.InkBlack,
                cornerRadius = 18.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_account_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCreateAccountForm = !showCreateAccountForm },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "✏️", fontSize = 18.sp)
                            Column {
                                Text(
                                    text = "Create Account",
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp,
                                    color = DoodleColors.InkBlack
                                )
                                Text(
                                    text = "Create your Doodle Word player profile",
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp,
                                    color = DoodleColors.InkBlue
                                )
                            }
                        }
                        Text(
                            text = if (showCreateAccountForm) "▲" else "▼",
                            fontWeight = FontWeight.Bold,
                            color = DoodleColors.InkBlack
                        )
                    }

                    AnimatedVisibility(visible = showCreateAccountForm) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = nicknameInput,
                                onValueChange = { nicknameInput = it.take(50) },
                                label = { Text("Doodle Nickname (e.g. SharkSlayer)", fontFamily = fontFamily) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DoodleColors.InkBlack,
                                    unfocusedBorderColor = DoodleColors.InkBlack.copy(alpha = 0.6f),
                                    focusedContainerColor = DoodleColors.PaperOffWhite,
                                    unfocusedContainerColor = DoodleColors.PaperOffWhite
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_nickname_input")
                            )

                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it.take(150) },
                                label = { Text("Gmail Address (optional)", fontFamily = fontFamily) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DoodleColors.InkBlack,
                                    unfocusedBorderColor = DoodleColors.InkBlack.copy(alpha = 0.6f),
                                    focusedContainerColor = DoodleColors.PaperOffWhite,
                                    unfocusedContainerColor = DoodleColors.PaperOffWhite
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_email_input")
                            )

                            OutlinedTextField(
                                value = createPassword,
                                onValueChange = { createPassword = it.take(100) },
                                label = { Text("Password", fontFamily = fontFamily) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DoodleColors.InkBlack,
                                    unfocusedBorderColor = DoodleColors.InkBlack.copy(alpha = 0.6f),
                                    focusedContainerColor = DoodleColors.PaperOffWhite,
                                    unfocusedContainerColor = DoodleColors.PaperOffWhite
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_password_input")
                            )

                            SketchyContainer(
                                fillColor = DoodleColors.DoodleYellow,
                                cornerRadius = 20.dp,
                                onClick = {
                                    if (!isLaunchingCredentialManager && !isSyncing) {
                                        triggerGoogleSignIn(authMethod = "account")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("create_account_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (isLaunchingCredentialManager || isSyncing) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            strokeWidth = 2.dp,
                                            color = DoodleColors.InkBlack
                                        )
                                    }
                                    Text(
                                        text = "✨ Create Account",
                                        fontFamily = fontFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = DoodleColors.InkBlack
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. CREATE USING GMAIL BUTTON (1-Tap Google Sign-In via Credential Manager)
            SketchyContainer(
                fillColor = Color(0xFFE3F2FD),
                borderColor = DoodleColors.InkBlack,
                cornerRadius = 24.dp,
                onClick = {
                    if (!isLaunchingCredentialManager && !isSyncing) {
                        triggerGoogleSignIn(authMethod = "gmail")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("create_gmail_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = Color(0xFFDB4437)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Create using Gmail",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = DoodleColors.InkBlack
                    )
                }
            }

            // 3. LOG-IN AS GUEST BUTTON (Instant Offline / Local Play)
            SketchyContainer(
                fillColor = Color.White,
                borderColor = DoodleColors.InkBlack,
                cornerRadius = 24.dp,
                onClick = {
                    nicknameInput = ""
                    emailInput = ""
                    onContinueAsGuest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("login_guest_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(text = "🕵️", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Log-in as Guest",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = DoodleColors.InkBlack
                    )
                }
            }
        }
    }
}
