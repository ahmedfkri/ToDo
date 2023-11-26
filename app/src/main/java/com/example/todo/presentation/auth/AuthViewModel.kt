import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser: LiveData<FirebaseUser?> = MutableLiveData()

    init {
        currentUser as MutableLiveData
        currentUser.value = firebaseAuth.currentUser
        firebaseAuth.addAuthStateListener { auth ->
            currentUser.value = auth.currentUser
        }
    }

    suspend fun signUp(email: String, password: String): Task<AuthResult> {
        return withContext(Dispatchers.IO) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
        }
    }

    suspend fun signIn(email: String, password: String): Task<AuthResult> {
        return withContext(Dispatchers.IO) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
        }
    }

    fun signOut() {
        Firebase.auth.signOut()
    }
}

