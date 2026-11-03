package com.example.baitap001uth_todo.data

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.example.baitap001uth_todo.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException
import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class AuthManager(private val auth: FirebaseAuth = Firebase.auth) {

    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun signInWithGoogle(context: Context): Result<FirebaseUser> = safeCall {
        val option = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        val credentialManager = CredentialManager.create(context)

        val result = credentialManager.getCredential(
            request = request,
            context = context
        )

        val credential = result.credential

        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val firebaseCred = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
            val authResult = auth.signInWithCredential(firebaseCred).await()
            authResult.user ?: throw IllegalStateException("Không thể lấy thông tin người dùng từ Firebase")
        } else {
            throw IllegalStateException("Loại tài khoản không được hỗ trợ")
        }
    }

    suspend fun signInAnonymously(): Result<FirebaseUser> = safeCall {
        val authResult = auth.signInAnonymously().await()
        authResult.user ?: throw IllegalStateException("Đăng nhập ẩn danh thất bại")
    }
    // ===== Email / Mật khẩu =====
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> = safeCall {
        auth.signInWithEmailAndPassword(email.trim(), password).await().user!!
    }

    suspend fun registerWithEmail(email: String, password: String): Result<FirebaseUser> = safeCall {
        auth.createUserWithEmailAndPassword(email.trim(), password).await().user!!
    }

    // ===== Facebook =====
    suspend fun signInWithFacebookToken(token: String): Result<FirebaseUser> =
        signInWithCredential(FacebookAuthProvider.getCredential(token))

    // ===== Số điện thoại =====
    private fun toE164(phone: String): String {
        val p = phone.trim().replace(" ", "")
        return if (p.startsWith("+")) p else "+84" + p.removePrefix("0")
    }

    fun sendPhoneCode(
        activity: Activity,
        phone: String,
        onSent: (verificationId: String) -> Unit,
        onAuto: (PhoneAuthCredential) -> Unit,
        onFail: (String) -> Unit
    ) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) = onAuto(credential)
            override fun onVerificationFailed(e: FirebaseException) =
                onFail("Không gửi được mã: ${e.localizedMessage ?: "lỗi không xác định"}")
            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) = onSent(id)
        }
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(toE164(phone))
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    suspend fun signInWithPhoneCode(verificationId: String, code: String): Result<FirebaseUser> =
        signInWithCredential(PhoneAuthProvider.getCredential(verificationId, code.trim()))

    suspend fun signInWithCredential(credential: AuthCredential): Result<FirebaseUser> = safeCall {
        auth.signInWithCredential(credential).await().user!!
    }

    private fun viMessage(code: String) = when (code) {
        "ERROR_INVALID_EMAIL" -> "Email không hợp lệ"
        "ERROR_WRONG_PASSWORD", "ERROR_INVALID_CREDENTIAL", "ERROR_USER_NOT_FOUND" -> "Sai email hoặc mật khẩu"
        "ERROR_EMAIL_ALREADY_IN_USE" -> "Email này đã được đăng ký"
        "ERROR_WEAK_PASSWORD" -> "Mật khẩu quá yếu (tối thiểu 6 ký tự)"
        "ERROR_INVALID_VERIFICATION_CODE" -> "Mã xác minh không đúng"
        "ERROR_NETWORK_REQUEST_FAILED" -> "Không có kết nối mạng"
        else -> "Đăng nhập thất bại ($code)"
    }

    suspend fun signOut(context: Context) {
        auth.signOut()
        runCatching {
            CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
        }
    }

    private suspend fun <T> safeCall(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: GetCredentialCancellationException) {
        Result.failure(Exception("Bạn đã huỷ đăng nhập"))
    } catch (e: NoCredentialException) {
        Result.failure(Exception("Máy chưa có tài khoản Google. Hãy đăng nhập Google trong Cài đặt máy"))
    } catch (e: FirebaseAuthException) {
        Result.failure(Exception(viMessage(e.errorCode)))
    } catch (e: Exception) {
        Result.failure(Exception("Đăng nhập thất bại: ${e.localizedMessage ?: "lỗi không xác định"}"))
    }
}