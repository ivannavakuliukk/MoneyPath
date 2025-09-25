package com.example.moneypath.data.models

import com.example.moneypath.R

class Category(
    val id: String,
    val name: String,
    val iconRes: Int,
    val mccCodes: Set<Int> = emptySet()
)

object Categories{
    val expensesCategory = listOf(
        Category("food", "Продукти", R.drawable.category_food,
            setOf(5411, 5422, 5441, 5451, 5462, 5499, 5921) ),
        Category("cafe", "Кафе/ресторани", R.drawable.category_cafe,
            setOf(5811, 5812, 5813, 5814)),
        Category("entertainment", "Розваги/спорт", R.drawable.category_entertainment,
            setOf(
                7832, 7841, 7911, 7922, 7929, 7932, 7933,
                7941, 7991, 7992, 7993, 7994, 7996, 7997, 7998, 7999,
                5940, 5941
            )),
        Category("pets", "Тварини", R.drawable.category_pets,
            setOf(5995, 742)),

        Category("travel", "Подорожі", R.drawable.category_travel,
            (setOf(4511, 4722, 7011, 7012, 7032, 7033) +
                    (3000..3299) + (3300..3499) + (3500..3999))),
        Category("car", "Авто", R.drawable.category_car,
            setOf(
                5511, 5521, 5532, 5541, 5542, 5983, 7523,
                7542, 7549, 7531, 7534, 7535, 7538, 7512, 7513, 7519
            )),
        Category("taxi", "Таксі", R.drawable.category_taxi,
            setOf(4121)),
        Category("beauty", "Краса/Здоров'я", R.drawable.category_beauty,
            (setOf(7230, 7298, 5977, 5912, 8071, 8050) + (8011..8099))),
        Category("rent", "Оренда/Комуналка", R.drawable.category_rent,
            setOf(6513, 4900)),
        Category("shopping", "Одяг/Взуття", R.drawable.category_shopping,
            ((5611..5699).toSet() + setOf(7296))),
        Category("houserepair", "Дім та ремонт", R.drawable.category_houserepair,
            (setOf(5200, 5211, 5231, 5251, 5261, 5722,
                1711, 1731, 1740, 1761, 1771, 7623, 7629, 7641, 7692, 7699, 7342) + (5712..5719))),
        Category("houseelectronics", "Побутова техніка/електроніка", R.drawable.category_houseelectronics,
            setOf(5732, 5734, 5735, 5946, 7622)),
        Category("subscription", "Телефон/підписки", R.drawable.category_subscription,
            (setOf(4812, 4814, 4899, 5968) + (5815..5818))),
        Category("gambling", "Азартні ігри", R.drawable.category_gambling,
            setOf(7995, 7800, 7801, 7802)),
        Category("education", "Освіта", R.drawable.category_education,
            (8211..8299).toSet()),
        Category("charity", "Благодійність", R.drawable.category_charity,
            setOf(8398)),
        Category("delivery", "Пошта/доставка", R.drawable.category_delivery,
            setOf(9402, 4214, 4215)),
        Category("books", "Книги/хобі/канцелярія", R.drawable.category_books,
            setOf(5942, 5943, 5945, 5947, 5992, 5994)),
        Category("government", "Державні платежі", R.drawable.category_government,
            setOf(9211, 9222, 9223, 9311, 9399)),
        Category("profservice", "Профпослуги", R.drawable.category_profservice,
            setOf(8111, 8931, 8999, 7392, 7399, 7349)),
        Category("other", "Інше", R.drawable.category_more,
            emptySet())
    )

    val incomeCategory = listOf(
        Category("salary", "Зарплата", R.drawable.category_salary),
        Category("percents", "Відсотки/Дивіденти", R.drawable.category_percents),
        Category("rentalincome", "Дохід від оредни", R.drawable.category_rentalincome),
        Category("gambling", "Азартні ігри", R.drawable.category_gambling,
            setOf(7995, 7800, 7801, 7802)),
        Category("gift", "Подарунок", R.drawable.category_gift),
        Category("debt", "Повернення боргу", R.drawable.category_debt)
    )

    val otherCategory = listOf(
        Category("goal", "Відкладення на ціль", R.drawable.category_goal),
        Category("transfer", "Перекази", R.drawable.category_transfer,
            setOf(6010, 6011, 6012, 6050, 6051, 4829) + (6536..6540)),
        Category("savings", "Збереження", R.drawable.category_savings)
    )
}

// Знайти категорію за id
fun findCategoryById(id: String): Category {
    return (Categories.expensesCategory + Categories.incomeCategory + Categories.otherCategory)
        .firstOrNull{id == it.id}?:Categories.expensesCategory.last()
}

// Знайти категорію за mcc (Для транзакцій з Api)
fun findCategoryByMcc(mcc:Int): Category{
    return Categories.expensesCategory.firstOrNull{it.mccCodes.contains(mcc)}?:
           Categories.incomeCategory.firstOrNull{it.mccCodes.contains(mcc)}?:
           Categories.otherCategory.firstOrNull{it.mccCodes.contains(mcc)}?:
           Categories.expensesCategory.last()
}
