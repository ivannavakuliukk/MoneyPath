package com.example.moneypath.data.repository

import android.util.Log
import com.example.moneypath.data.models.BudgetPlanDB
import com.example.moneypath.data.models.SettingsDB
import com.example.moneypath.domain.models.Transaction
import com.example.moneypath.data.models.Wallet
import com.example.moneypath.data.remote.mappers.toData
import com.example.moneypath.data.remote.mappers.toDomain
import com.example.moneypath.data.remote.models.TransactionDto
import com.example.moneypath.utils.getDayRangeFromUnix
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
    private var transactionsGoalCurrentListener: ValueEventListener? = null
    private var transactionsGoalQuery: Query? = null

    // --- Акаунт ---

    // Отримати поточного користувача
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // Вихід з акаунту
    suspend fun logOut(): Boolean {
        return try {
            auth.signOut()
            true
        } catch (e: Exception) {
            Log.d("Firebase Repository", "Error Sign out user")
            false
        }
    }

    // Видалити акаунт
    suspend fun deleteAccount(): Boolean {
        return try {
            val user = getCurrentUser() ?: return false
            user.delete().await()
            true
        } catch (e: Exception) {
            Log.d("Firebase Repository", "Error deleting user account")
            false
        }
    }

    // --- Гаманці ---

    // Отримати посилання на вузол гаманців - users/{uid}/wallets
    private fun getUserWalletsRef(): DatabaseReference? {
        return getCurrentUser()?.uid?.let {
            database.getReference("users").child(it).child("wallets")
        }
    }

    // Listener(Слухач) - підписка на зміни у списку гаманців
    fun getWallets(
        onUpdate: (List<Wallet>) -> Unit,
        onError: (String) -> Unit
    ) {
        walletsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val wallets = snapshot.children.mapNotNull { it.getValue(Wallet::class.java) }
                onUpdate(wallets)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("getWallets", "Problem in wallets reading, ${error.message}")
                onError("Проблема при завантаженні гаманців")
            }
        }
        getUserWalletsRef()?.addValueEventListener(walletsListener!!)
    }

    // Перевірити чи гаманці існують
    suspend fun checkIfWalletsExist(): Boolean {
        return try {
            val snapshot = getUserWalletsRef()?.get()?.await()
            snapshot != null && snapshot.exists() && snapshot.childrenCount > 0
        } catch (e: Exception) {
            Log.e("checkIfWalletsExist", "Error checking wallets: ${e.message}")
            false
        }
    }

    // Видалити Listener
    fun removeWalletsEventListener() {
        walletsListener?.let {
            getUserWalletsRef()?.removeEventListener(it)
        }
        walletsListener = null
    }

    // Додати або змінити гаманець
    suspend fun addOrUpdateWallet(wallet: Wallet): Boolean {
        val walletId = wallet.id.ifEmpty { UUID.randomUUID().toString() }
        return try {
            getUserWalletsRef()?.child(walletId)?.setValue(wallet.copy(id = walletId))?.await()
            true
        } catch (e: Exception) {
            Log.d("Firebase Repository", "Error adding wallet", e)
            false
        }
    }

    // Отримати гаманець за id
    suspend fun getWalletById(walletId: String): Wallet? {
        return try {
            val snapshot = getUserWalletsRef()?.child(walletId)?.get()?.await()
            snapshot?.getValue(Wallet::class.java)
        } catch (e: Exception) {
            Log.d("Firebase Repository", "Can`t get wallet by id", e)
            null
        }
    }

    // Отримати баланс гаманця
    suspend fun getWalletBalance(walletId: String): Pair<String, String>? {
        return try {
            val snapshot = getUserWalletsRef()?.child(walletId)?.get()?.await()
            val balanceEnc = snapshot?.child("balanceEnc")?.getValue(String::class.java)
            val balanceIv = snapshot?.child("balanceIv")?.getValue(String::class.java)
            if(balanceIv!= null && balanceEnc!=null){
                balanceEnc to balanceIv
            }else null
        } catch (e: Exception) {
            Log.d("Firebase Repository", "Error getting wallet`s ballance", e)
            null
        }
    }

    // Оновити поле (баланс) гаманця
    suspend fun updateWalletBalance(walletId: String, newBalanceEnc: String, newBalanceIv: String): Boolean {
        return try {
            val updates = mapOf<String, Any>(
                "balanceEnc" to newBalanceEnc,
                "balanceIv" to newBalanceIv
            )
            getUserWalletsRef()?.child(walletId)?.updateChildren(updates)?.await()
            true
        } catch (e: Exception) {
            Log.d("Firebase Repository", "Error updating wallet balance", e)
            false
        }
    }

    // Видалити гаманець
    suspend fun deleteWallet(walletId: String): Boolean {
        return try {
            deleteTransactionsByWallet(walletId)
            getUserWalletsRef()?.child(walletId)?.removeValue()?.await()
            true
        } catch (e: Exception) {
            Log.d("Firebase Repository", "Error deleting wallet", e)
            false
        }

    }

    // ---Транзакції---

    // Отримати посилання на вузол транзакцій - users/{uid}/transactions
    private fun getUserTransactionsRef(): DatabaseReference? {
        return getCurrentUser()?.uid?.let {
            database.getReference("users").child(it).child("transactions")
        }
    }

    // Додати транзакцію
    suspend fun addTransaction(transaction: Transaction): Boolean {
        val transactionId = transaction.id.ifEmpty { UUID.randomUUID().toString() }
        val transactionDto = transaction.toData()
        return try {
            getUserTransactionsRef()?.child(transactionId)
                ?.setValue(transactionDto.copy(id = transactionId))?.await()
            true
        } catch (e: Exception) {
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
    suspend fun deleteTransaction(transactionId: String): Boolean {
        return try {
            getUserTransactionsRef()?.child(transactionId)?.removeValue()?.await()
            true
        } catch (e: Exception) {
            Log.d("Firebase Repository", "Error deleting transaction", e)
            false
        }
    }

    // Отримати транзакцію за id
    suspend fun getTransactionById(transactionId: String): Transaction? {
        return try {
            val snapshot = getUserTransactionsRef()?.child(transactionId)?.get()?.await()
            snapshot?.getValue(TransactionDto::class.java)?.toDomain()
        } catch (e: Exception) {
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
    ) {
        val (dateStart, dateEnd) = getDayRangeFromUnix(date)
        // Відписка від попереднього слухача
        transactionsCurrentListener?.let { listener ->
            transactionsQuery?.removeEventListener(listener)
        }

        // Новий запит(нова дата)
        transactionsQuery = getUserTransactionsRef()
            ?.orderByChild("date")
            ?.startAt(dateStart.toDouble())
            ?.endAt(dateEnd.toDouble())


        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactions =
                    snapshot.children.mapNotNull { it.getValue(TransactionDto::class.java) }
                onUpdate(transactions.toDomain())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Firebase Repository", "Problem in transactions reading, ${error.message}")
                onError("Проблема при завантаженні гаманців")
            }
        }
        transactionsQuery?.addValueEventListener(listener)
        transactionsCurrentListener = listener
    }

    fun removeTransactionsEventListener() {
        transactionsCurrentListener?.let {
            getUserWalletsRef()?.removeEventListener(it)
        }
        transactionsCurrentListener = null
    }

    /*
    Отримати список транзакцій за конкретну категорію
    Створення нового Listener на кожну категорію
     */
    fun getTransactionsByCategory(
        categoryId: String,
        onUpdate: (List<Transaction>) -> Unit,
        onError: (String) -> Unit
    ) {
        transactionsGoalCurrentListener?.let { listener ->
            transactionsQuery?.removeEventListener(listener)
        }

        transactionsGoalQuery = getUserTransactionsRef()
            ?.orderByChild("categoryId")
            ?.equalTo(categoryId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactions =
                    snapshot.children.mapNotNull { it.getValue(TransactionDto::class.java) }
                onUpdate(transactions.toDomain())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Firebase Repository", "Problem in transactions reading, ${error.message}")
                onError("Проблема при завантаженні транзакцій")
            }
        }

        transactionsGoalQuery?.addValueEventListener(listener)
        transactionsGoalCurrentListener = listener
    }

    fun removeTransactionsGoalEventListener() {
        transactionsGoalCurrentListener?.let {
            getUserWalletsRef()?.removeEventListener(it)
        }
        transactionsGoalCurrentListener = null
    }


    // Видалити транзакції певного гаманця
    private suspend fun deleteTransactionsByWallet(walletId: String): Boolean {
        return try {
            val query = getUserTransactionsRef()?.orderByChild("walletId")?.equalTo(walletId)
            query?.get()?.await()?.children?.forEach { snapshot ->
                snapshot.ref.removeValue().await()
            }
            true
        } catch (e: Exception) {
            Log.d("Firebase Repository", "Error deleting transactions", e)
            false
        }
    }

    // Видалити транзакції певної категорії
    suspend fun deleteTransactionsByCategory(categoryId: String): Boolean {
        return try {
            val query = getUserTransactionsRef()
                ?.orderByChild("categoryId")
                ?.equalTo(categoryId)

            query?.get()?.await()?.children?.forEach { snapshot ->
                snapshot.ref.removeValue().await()
            }

            true
        } catch (e: Exception) {
            Log.d("Firebase Repository", "Error deleting category transactions", e)
            false
        }
    }


    // Отримати транзакції між двома датами (одноразово)
    suspend fun getTransactionsBetweenDates(
        startDate: Long,
        endDate: Long,
        walletIds: List<String>? = null // якщо null — беремо всі гаманці
    ): List<Transaction> {
        return try {
            val query = getUserTransactionsRef()
                ?.orderByChild("date")
                ?.startAt(startDate.toDouble())
                ?.endAt(endDate.toDouble())

            val snapshot = query?.get()?.await()
            val transactions = snapshot?.children?.mapNotNull { it.getValue(TransactionDto::class.java) } ?: emptyList()
            // Фільтруємо по walletId, якщо переданий список
            if (!walletIds.isNullOrEmpty()) {
                transactions.filter { it.walletId in walletIds }.toDomain()
            } else {
                transactions.toDomain()
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error getting transactions", e)
            emptyList()
        }
    }


    // --Дані для шифрування--

    // Отримати посилання вузол для шифрування
    // Отримати посилання на вузол транзакцій - users/{uid}/transactions
    private fun getUserSecurityRef(): DatabaseReference? {
        return getCurrentUser()?.uid?.let {
            database.getReference("users").child(it).child("security")
        }
    }


    // Додати salt
    suspend fun addSalt(saltBase64: String): Boolean {
        return try {
            getUserSecurityRef()?.child("salt")?.setValue(saltBase64)?.await()
            true
        } catch (e: Exception) {
            Log.d("FirebaseRepository", "Error adding salt", e)
            false
        }
    }

    // Отримати salt
    suspend fun getSalt(): String? {
        return try {
            val snapshot = getUserSecurityRef()?.child("salt")?.get()?.await()
            snapshot?.getValue(String::class.java)
        } catch (e: Exception) {
            Log.d("FirebaseRepository", "Error getting salt", e)
            null
        }
    }

    // Додати обгорнутий дек
    suspend fun addWrappedDEK(wrappedDEKBase64: String, ivBase64: String): Boolean {
        return try {
            val updates = mapOf(
                "wrappedDEK" to wrappedDEKBase64,
                "wrappedDEK_iv" to ivBase64
            )
            getUserSecurityRef()?.updateChildren(updates)?.await()
            true
        } catch (e: Exception) {
            Log.d("FirebaseRepository", "Error saving wrappedDEK", e)
            false
        }
    }

    // Отримати обгорнутий дек
    suspend fun getWrappedDEK(): Pair<String, String>? {
        return try {
            val snapshot = getUserSecurityRef()?.get()?.await()
            val wrapped = snapshot?.child("wrappedDEK")?.getValue(String::class.java)
            val iv = snapshot?.child("wrappedDEK_iv")?.getValue(String::class.java)
            if (wrapped != null && iv != null) wrapped to iv else null
        } catch (e: Exception) {
            Log.d("FirebaseRepository", "Error getting wrappedDEK", e)
            null
        }
    }


    // --- Зберегти тестовий рядок (canary) ---
    suspend fun addCanaryData(encrypted: String, iv: String): Boolean {
        return try {
            val updates = mapOf(
                "canary" to encrypted,
                "canary_iv" to iv
            )
            getUserSecurityRef()?.updateChildren(updates)?.await()
            true
        } catch (e: Exception) {
            Log.d("FirebaseRepository", "Error saving canary", e)
            false
        }
    }

    // --- Отримати тестовий рядок (canary) ---
    suspend fun getCanaryData(): Pair<String, String>? {
        return try {
            val snapshot = getUserSecurityRef()?.get()?.await()
            val encrypted = snapshot?.child("canary")?.getValue(String::class.java)
            val iv = snapshot?.child("canary_iv")?.getValue(String::class.java)

            if (encrypted != null && iv != null) encrypted to iv else null
        } catch (e: Exception) {
            Log.d("FirebaseRepository", "Error getting canary", e)
            null
        }
    }

    // Отримати посилання на вузол планування - users/{uid}/planning
    private fun getUserPlanningRef(): DatabaseReference? {
        return getCurrentUser()?.uid?.let {
            database.getReference("users").child(it).child("planning")
        }
    }

    // Видалити вузол планування
    fun deleteUserPlanning() {
        getUserPlanningRef()?.removeValue()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "Planning node deleted successfully")
            } else {
                Log.e("Firebase", "Failed to delete planning node", task.exception)
            }
        }
    }

    suspend fun updateSetup(data: SettingsDB): Boolean{
        return try {
            getUserPlanningRef()?.child("set_up")?.setValue(data)?.await()
            true
        } catch (e: Exception) {
            Log.d("Firebase Repository", "Error adding setup", e)
            false
        }
    }

    suspend fun getSetup(): SettingsDB? {
        return try {
            val snapshot = getUserPlanningRef()?.child("set_up")?.get()?.await()
            snapshot?.getValue(SettingsDB::class.java)
        } catch (e: Exception) {
            Log.d("Firebase Repository", "Error fetching setup", e)
            null
        }
    }

    suspend fun updateTotalPlan(data: BudgetPlanDB): Boolean{
        return try {
            getUserPlanningRef()?.child("total_plan")?.setValue(data)?.await()
            true
        } catch (e: Exception) {
            Log.d("Firebase Repository", "Error adding total plan", e)
            false
        }
    }

    suspend fun getTotalPlan():BudgetPlanDB?{
        return try{
            val snapshot = getUserPlanningRef()?.child("total_plan")?.get()?.await()
            snapshot?.getValue(BudgetPlanDB::class.java)
        }catch (e: Exception) {
            Log.d("Firebase Repository", "Can`t get total plan", e)
            null
        }
    }

    suspend fun getTotalPlanEnd():Long?{
        return try{
            val snapshot = getUserPlanningRef()?.child("total_plan")?.child("stable_plan_end")?.get()?.await()
            snapshot?.getValue(Long::class.java)
        }catch (e: Exception) {
            Log.d("Firebase Repository", "Can`t get total plan end", e)
            null
        }
    }

    suspend fun addAdditionalPlan(data: BudgetPlanDB, num: Int):Boolean{
        return try {
            getUserPlanningRef()?.child("additional_plans")?.child(num.toString())?.setValue(data)?.await()
            true
        } catch (e: Exception) {
            Log.d("Firebase Repository", "Error adding additional plan", e)
            false
        }
    }

    suspend fun getAdditionalPlans(): List<BudgetPlanDB> {
        return try {
            val ref = getUserPlanningRef()?.child("additional_plans") ?: return emptyList()
            val snapshot = ref.get().await()
            val plans = mutableListOf<BudgetPlanDB>()

            for (child in snapshot.children) {
                val plan = child.getValue(BudgetPlanDB::class.java)
                if (plan != null) plans.add(plan)
            }

            plans
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error getting additional plans", e)
            emptyList()
        }
    }


}
