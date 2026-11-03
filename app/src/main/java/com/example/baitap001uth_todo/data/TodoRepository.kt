package com.example.baitap001uth_todo.repository

import android.util.Log
import com.example.baitap001uth_todo.model.Todo
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

/** Mọi việc nằm ở: users/{uid}/todos/{todoId} */
class TodoRepository(
    private val auth: FirebaseAuth = Firebase.auth,
    private val db: FirebaseFirestore = Firebase.firestore
) {
    private fun todosOf(uid: String): CollectionReference =
        db.collection("users").document(uid).collection("todos")

    private fun myTodos(): CollectionReference {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("Bạn chưa đăng nhập")
        return todosOf(uid)
    }

    /** Phát ra uid hiện tại, tự cập nhật khi đăng nhập / đăng xuất. */
    private fun uidFlow(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.uid) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    /** READ realtime: Firestore đổi là danh sách trong app đổi theo. */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTodosFlow(): Flow<List<Todo>> = uidFlow().flatMapLatest { uid ->
        if (uid == null) flowOf(emptyList()) else listen(todosOf(uid))
    }

    private fun listen(col: CollectionReference): Flow<List<Todo>> = callbackFlow {
        val registration = col
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("TodoRepo", "Đọc lỗi: ${error.message}", error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObjects(Todo::class.java) ?: emptyList())
            }
        awaitClose { registration.remove() }
    }

    /** CREATE */
    suspend fun addTodo(todo: Todo) {
        myTodos().add(todo).await()
    }

    /** UPDATE: đổi trạng thái hoàn thành */
    suspend fun setCompleted(id: String, completed: Boolean) {
        myTodos().document(id).update("isCompleted", completed).await()
    }

    /** UPDATE: sửa nội dung */
    suspend fun updateTodo(id: String, title: String, category: String, time: String) {
        myTodos().document(id)
            .update(mapOf("title" to title, "category" to category, "time" to time))
            .await()
    }

    /** DELETE */
    suspend fun deleteTodo(id: String) {
        myTodos().document(id).delete().await()
    }
}