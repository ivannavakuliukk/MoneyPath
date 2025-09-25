package com.example.moneypath.data.repository

import android.util.Log
import com.example.moneypath.data.models.Transaction
import com.example.moneypath.data.models.Wallet
import com.example.moneypath.utils.getDayRangeFromUnix
import com.example.moneypath.utils.unixToDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

/**
 * Репозиторій для роботи з Firebase Authentication та Realtime Database.
 *
 * Відповідає за отримання інформації про користувача,
 * збереження та завантаження його гаманців та транзакцій.
 *
 * @property auth екземпляр FirebaseAuth для роботи з авторизацією
 * @property database екземпляр FirebaseDatabase для роботи з базою даних
 */
class FirebaseRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) {

    private var walletsListener: ValueEventListener? = null
    private var transactionsCurrentListener: ValueEventListener? = null
    private var transactionsQuery: Query? = null

    // --- Акаунт ---

    // Отримати поточного користувача
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // Вихід з акаунту
    suspend fun logOut(): Boolean{
        return try{
            auth.signOut()
            true
        }catch (e:Exception){
            Log.d("Firebase Repository", "Error Sign out user")
            false
        }
    }

    // Видалити акаунт
    suspend fun deleteAccount(): Boolean{
        return try{
            val user = getCurrentUser() ?: return false
            user.delete().await()
            true
        }catch (e:Exception){
            Log.d("Firebase Repository", "Error deleting user account")
            false
        }
    }

    // --- Гаманці ---

    // Отримати посилання на вузол гаманців - users/{uid}/wallets
    private fun getUserWalletsRef():DatabaseReference?{
        return getCurrentUser()?.uid?.let{database.getReference("users").child(it).child("wallets")}
    }

    // Listener(Слухач) - підписка на зміни у списку гаманців
    fun getWallets(
        onUpdate: (List<Wallet>) -> Unit,
        onError: (String) -> Unit
    ){
        walletsListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                val wallets = snapshot.children.mapNotNull{it.getValue(Wallet::class.java)}
                onUpdate(wallets)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("getWallets", "Problem in wallets reading, ${error.message}")
                onError("Проблема при завантаженні гаманців")
            }
        }
        getUserWalletsRef()?.addValueEventListener(walletsListener!!)
    }
    // Видалити Listener
    fun removeWalletsEventListener(){
        walletsListener?.let{
            getUserWalletsRef()?.removeEventListener(it)
        }
        walletsListener = null
    }

    // Додати або змінити гаманець
    suspend fun addOrUpdateWallet(wallet:Wallet): Boolean{
        val walletId = wallet.id.ifEmpty { UUID.randomUUID().toString() }
        return try{
            getUserWalletsRef()?.child(walletId)?.setValue(wallet.copy(id = walletId))?.await()
            true
        }catch (e:Exception){
            Log.d("Firebase Repository", "Error adding wallet", e)
            false
        }
    }

    // Отримати гаманець за id
    suspend fun getWalletById(walletId: String):Wallet?{
        return try{
            val snapshot = getUserWalletsRef()?.child(walletId)?.get()?.await()
            snapshot?.getValue(Wallet::class.java)
        }catch(e:Exception){
            Log.d("Firebase Repository", "Can`t get wallet by id", e)
            null
        }
    }

    // Отримати баланс гаманця
    suspend fun getWalletBalance(walletId: String):Double?{
        return try{
            val snapshot = getUserWalletsRef()?.child(walletId)?.child("balance")?.get()?.await()
            snapshot?.getValue(Double::class.java)
        }catch (e:Exception){
            Log.d("Firebase Repository", "Error getting wallet`s ballance", e)
            null
        }
    }

    // Оновити поле (баланс) гаманця
    suspend fun updateWalletBalance(walletId: String, newBalance:Double):Boolean{
        return try{
            val updates = mapOf<String, Any>(
                "balance" to newBalance
            )
            getUserWalletsRef()?.child(walletId)?.updateChildren(updates)?.await()
            true
        }catch (e:Exception){
            Log.d("Firebase Repository", "Error updating wallet balance", e)
            false
        }
    }

    // Видалити гаманець
    suspend fun deleteWallet(walletId: String): Boolean{
        return try{
            deleteTransactionsByWallet(walletId)
            getUserWalletsRef()?.child(walletId)?.removeValue()?.await()
            true
        } catch (e: Exception){
            Log.d("Firebase Repository", "Error deleting wallet", e)
            false
        }

    }

    // ---Транзакції---

    // Отримати посилання на вузол транзакцій - users/{uid}/transactions
    private fun getUserTransactionsRef():DatabaseReference?{
        return getCurrentUser()?.uid?.let{database.getReference("users").child(it).child("transactions")}
    }

    // Додати транзакцію
    suspend fun addTransaction(transaction: Transaction):Boolean{
        val transactionId = transaction.id.ifEmpty { UUID.randomUUID().toString() }
        return try{
            getUserTransactionsRef()?.child(transactionId)?.setValue(transaction.copy(id = transactionId))?.await()
            true
        }catch (e: Exception){
            Log.d("Firebase Repository", "Error adding transaction", e)
            false
        }
    }

    // Додати декілька транзакцій
    suspend fun addTransactions(transactions: List<Transaction>): Boolean {
        return try {
            transactions.forEach { transaction ->
                val success = addTransaction(transaction)
                if (!success) return false
            }
            true
        } catch (e: Exception) {
            Log.d("Firebase Repository", "Error adding multiple transactions", e)
            false
        }
    }


    // Видалити транзакцію
    suspend fun deleteTransaction(transactionId: String): Boolean{
        return try{
            getUserTransactionsRef()?.child(transactionId)?.removeValue()?.await()
            true
        }catch(e:Exception){
            Log.d("Firebase Repository", "Error deleting transaction", e)
            false
        }
    }

    // Отримати транзакцію за id
    suspend fun getTransactionById(transactionId: String):Transaction?{
        return try{
            val snapshot = getUserTransactionsRef()?.child(transactionId)?.get()?.await()
            snapshot?.getValue(Transaction::class.java)
        }catch (e:Exception){
            Log.d("Firebase Repository", "Can`t get transaction by id.", e)
            null
        }
    }

    /*
    Отримати список транзакцій за конкретну дату
    Створення нового Listener на кожну дату
     */
    fun getTransactionsByDate(
        date: Long,
        onUpdate: (List<Transaction>) -> Unit,
        onError: (String) -> Unit
    ){
        val(dateStart, dateEnd) = getDayRangeFromUnix(date)
        // Відписка від попереднього слухача
        transactionsCurrentListener?.let { listener->
            transactionsQuery?.removeEventListener(listener)
        }

        // Новий запит(нова дата)
        transactionsQuery = getUserTransactionsRef()
            ?.orderByChild("date")
            ?.startAt(dateStart.toDouble())
            ?.endAt(dateEnd.toDouble())


        val listener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                val transactions = snapshot.children.mapNotNull{it.getValue(Transaction::class.java)}
                onUpdate(transactions)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("Firebase Repository", "Problem in transactions reading, ${error.message}")
                onError("Проблема при завантаженні гаманців")
            }
        }
        transactionsQuery?.addValueEventListener(listener)
        transactionsCurrentListener = listener
    }

    fun removeTransactionsEventListener(){
        transactionsCurrentListener?.let{
            getUserWalletsRef()?.removeEventListener(it)
        }
        transactionsCurrentListener = null
    }

    // Видалити транзакції певного гаманця
    private suspend fun deleteTransactionsByWallet(walletId: String):Boolean{
        return try{
            val query = getUserTransactionsRef()?.orderByChild("walletId")?.equalTo(walletId)
            query?.get()?.await()?.children?.forEach{snapshot->
                snapshot.ref.removeValue().await()
            }
            true
        }catch(e:Exception){
            Log.d("Firebase Repository", "Error deleting transactions", e)
            false
        }
    }
}