package com.example.moneypath.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PrefsHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveGoalName(name: String) = prefs.edit().putString("goal_name", name).apply()
    fun saveGoalAmount(amount: Int) = prefs.edit().putString("goal_amount", amount.toString()).apply()
    fun saveContinued(isContinued: Boolean) = prefs.edit().putBoolean("goal_continued", isContinued).apply()
    fun savePlanEnd(date:Long) = prefs.edit().putString("plan_end", date.toString()).apply()

    fun getGoalName(): String? = prefs.getString("goal_name", null)
    fun getGoalAmount(): Int? = prefs.getString("goal_amount", null)?.toIntOrNull()
    fun getContinued(): Boolean = prefs.getBoolean("goal_continued", false)
    fun getPlanEnd(): Long? = prefs.getString("plan_end", null)?.toLongOrNull()

    fun removeGoalName() = prefs.edit().remove("goal_name").apply()
    fun removeGoalAmount() = prefs.edit().remove("goal_amount").apply()
    fun removeContinued() = prefs.edit().remove("goal_continued").apply()
    fun removePlanEnd() = prefs.edit().remove("plan_end").apply()


    fun clearAll() = prefs.edit().clear().apply()
}