# MoneyPath - додаток для управління фінансами

## Опис
Додаток, який дозволяє користувачу окрім відстежування витрат та доходів, планувати бюджет. Надає можливість створення планів бюджету за вхідними даними користувача.

## Функціонал для користувача:
- Фіксація та відстеження витрат та доходів.
- Авторизація через Google.
- Синхрнізація транзакцій з Monobank.
- Планування бюджету з ціллю та без.
- Пропозиції альтернативних планів.
- Відстеження прогресу плану.

## Технології
- Мова програмування – **Kotlin** (застосунок), **Python** (оптимізаційний модуль).
- Android Studio – **IDE**.
- Android-біблітеки: **Hilt** (ін'єкція залежностей), **Retrofit** (робота з Rest API), **JCE** (шифрування даних), **Kotlin Coroutines** (асинхронні операції).
- Pyton частина: **CVXPY** (бібліотека для квадратичної оптимізації), **NumPy**, мікрофреймвок **Flask**, хостинг **PythonAnywhere**.
- Інтерфейс – **Jetpack Compose** за попередньо створеними макетами Figma.
- Архітектура – MVVM доповнена шаром UseCase для бізнес-логіки.
- Зовнішні сервіси: **Firebase Realtime Database** (БД), **Firebase Authentication** (авторизація), **Monobank Open Api**.
- Мова інтерфейсу – українська.


## Інтерфейс
#### Сторінка авторизації
<div style="display: flex; justify-content: space-around;">
   <img width="450" alt="Авторизація" src="https://github.com/user-attachments/assets/49a40c35-cb32-42f5-9fef-9eb137dac0e9" />
</div>

#### Головна сторінка
<div style="display: flex; justify-content: space-around;">
   <img width="450" alt="Головна" src="https://github.com/user-attachments/assets/41c1500c-a30c-445f-8d53-ef4c386a8a87" />
</div>

#### Додавання транзакції та гаманця
<div style="display: flex; justify-content: space-around;">
   <img width="450" alt="Транзакції" src="https://github.com/user-attachments/assets/10810db1-43ba-469a-ac6e-04df347badff" />
   <img width="450" alt="Гаманець" src="https://github.com/user-attachments/assets/de8dcd88-5b1a-4a42-9670-32a20247acef" />
</div>

#### Форма налаштування плану
<div style="display: flex; justify-content: space-around;">
   <img width="150" alt="Screenshot_20251218-105837" src="https://github.com/user-attachments/assets/8fb8cb31-b323-437b-93ae-a8ee5edfbdab" />
  <img width="150" alt="Screenshot_20251123-133833" src="https://github.com/user-attachments/assets/2a67ed7b-9352-4544-b741-0cc4ac6abe5e" />
  <img width="150" alt="Screenshot_20251123-133858" src="https://github.com/user-attachments/assets/f139564d-b08c-4332-802b-9167900656b8" />
  <img width="150" alt="Screenshot_20251123-133919" src="https://github.com/user-attachments/assets/0cc10a2c-ff3e-4e11-9ea4-010cd7e315c4" />
  <img width="150" alt="Screenshot_20251123-134214" src="https://github.com/user-attachments/assets/446f9a5f-9e8c-48de-bec7-96b56e017b2c" />
  <img width="150" alt="Screenshot_20251123-135601" src="https://github.com/user-attachments/assets/08929a39-dc1d-4dd0-9acc-64617858fbe5" />
  <img width="150" alt="Screenshot_20251123-135639" src="https://github.com/user-attachments/assets/02069ade-55ba-4534-bb61-c14a198701d6" />
  <img width="150" alt="Screenshot_20251123-135646" src="https://github.com/user-attachments/assets/a09f6ed0-7f8f-454c-8581-cd045d8354d4" />
  <img width="150" alt="Screenshot_20251123-135716" src="https://github.com/user-attachments/assets/0cf8e185-0450-4979-b8d7-9865670daac0" />
  <img width="150" alt="Screenshot_20251123-135730" src="https://github.com/user-attachments/assets/d51eff88-c5c6-4c95-a532-53ebc7b8abc0" />
</div>

#### Сторінка плану
<div style="display: flex; justify-content: space-around;">
   <img width="150" alt="Screenshot_20251123-135740" src="https://github.com/user-attachments/assets/922bb1ee-7f33-4c29-ae6b-8bd57d4a93eb" />
  <img width="150" alt="Screenshot_20251123-135748" src="https://github.com/user-attachments/assets/c5c802a2-982e-4647-94ff-ee12b4b86131" />
  <img width="150" alt="Screenshot_20251123-170918" src="https://github.com/user-attachments/assets/3470241b-0f6f-45aa-9c01-cefedd19e9a2" />
  <img width="150"  alt="Screenshot_20251123-170924" src="https://github.com/user-attachments/assets/7913c3bd-d807-4515-a1b6-b078caf998c5" />
</div>

#### Сторінка категорій та відстеження прогресу
<div style="display: flex; justify-content: space-around;">
  <img width="150"  alt="Screenshot_20251123-170746" src="https://github.com/user-attachments/assets/c646d688-35a9-434b-8b8a-129ce37b5f2d" />
  <img width="150"  alt="Screenshot_20251123-170752" src="https://github.com/user-attachments/assets/8e807e47-c572-4ee4-a31a-db010e6b6db1" />
  <img width="150"  alt="Screenshot_20251123-170803" src="https://github.com/user-attachments/assets/bf9d7096-3c34-402e-8835-831cac4e39f2" />
</div>

#### Сторінка "Інше" та сповіщення про закінчення плану
<div style="display: flex; justify-content: space-around;">
  <img width="150"  alt="Screenshot_20251123-160747" src="https://github.com/user-attachments/assets/1f5fa1cb-227f-4256-8caa-4b8b3adc5730" />
  <img width="150"  alt="Screenshot_20251116-212838" src="https://github.com/user-attachments/assets/471190a3-b663-4e39-b3f6-a6d7d9bbe929" />
  <img width="150"  alt="Screenshot_20251116-224321" src="https://github.com/user-attachments/assets/7cdc5f3c-7b53-4ea3-a969-b66733777ed6" />
</div>
