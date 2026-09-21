# CAMPUS EATS - Native Android Application

A real native Android application for university food ordering, built for the **Mobile Computing 2B** university group project.

---

## 📱 Technology Stack
- **Language**: Kotlin 2.0+
- **UI Toolkit**: Jetpack Compose with Material 3
- **Local Persistence**: Room SQLite Database (Lifecycle-aware Flow & Coroutines)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Navigation**: Jetpack Navigation Compose
- **Build System**: Gradle Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`, Version Catalog `libs.versions.toml`)
- **Compatibility**: Android Studio Hedgehog / Iguana / Jellyfish / Koala (minSdk: 24, targetSdk: 34)

---

## 🏛️ Project Architecture (`com.campuseats`)

```
com.campuseats/
├── CampusEatsApp.kt                 # Application class initializing Room DB, Repositories, Session
├── MainActivity.kt                  # Single Activity hosting Jetpack Compose & Navigation
│
├── data/
│   ├── local/
│   │   ├── database/
│   │   │   ├── AppDatabase.kt       # Room Database with automated seed data callback
│   │   │   └── Converters.kt        # Room Type Converters for Enums and dates
│   │   ├── dao/
│   │   │   ├── UserDao.kt           # User profile & credential queries
│   │   │   ├── VendorDao.kt         # Vendor stalls queries
│   │   │   ├── FoodItemDao.kt       # Menu items queries
│   │   │   └── OrderDao.kt          # Order transactions & status management
│   │   └── entity/
│   │       ├── UserEntity.kt        # User table
│   │       ├── VendorEntity.kt      # Vendor table
│   │       ├── FoodItemEntity.kt    # Food items table with Foreign Keys
│   │       ├── OrderEntity.kt       # Orders table
│   │       ├── OrderItemEntity.kt   # Order line items table
│   │       └── OrderStatus.kt       # PENDING, PREPARING, READY_FOR_PICKUP, COMPLETED, CANCELLED
│   │
│   ├── network/
│   │   ├── api/
│   │   │   └── CampusEatsApiService.kt # Retrofit REST service endpoints blueprint
│   │   └── model/
│   │       ├── ApiResponse.kt       # Generic response wrapper
│   │       ├── AuthDto.kt           # Login/Register request & response DTOs
│   │       └── OrderDto.kt          # Remote order creation & response DTOs
│   │
│   └── repository/
│       ├── AuthRepository.kt        # Interface + implementation for authentication & sessions
│       ├── StudentRepository.kt     # Interface + implementation for cart, menu, ordering
│       ├── VendorRepository.kt      # Interface + implementation for food management & orders
│       └── AdminRepository.kt       # Interface + implementation for users, vendors, reports
│
├── ui/
│   ├── auth/
│   │   ├── AuthViewModel.kt
│   │   ├── LoginScreen.kt           # Login with Quick Demo account buttons
│   │   └── RegisterScreen.kt        # Registration with role selector
│   │
│   ├── student/
│   │   ├── StudentViewModel.kt
│   │   ├── StudentHomeScreen.kt     # Greeting, categories, active orders, featured vendors
│   │   ├── VendorsScreen.kt         # Filterable list of campus vendors
│   │   ├── MenuScreen.kt            # Menu items, categories, dietary flags, add to cart
│   │   ├── CartScreen.kt            # Cart items, quantities, order notes, checkout
│   │   ├── OrderStatusScreen.kt     # Live order progress bar & 4-digit pickup PIN
│   │   └── OrderHistoryScreen.kt    # Chronological history of past orders
│   │
│   ├── vendor/
│   │   ├── VendorViewModel.kt
│   │   ├── VendorDashboardScreen.kt # Pending orders, metrics, menu items, total sales
│   │   ├── FoodManagementScreen.kt  # Add new dish, toggle availability, delete
│   │   └── VendorOrdersScreen.kt    # Live incoming orders, status transitions
│   │
│   ├── admin/
│   │   ├── AdminViewModel.kt
│   │   ├── AdminDashboardScreen.kt  # Central system control panel & metrics
│   │   ├── UserManagementScreen.kt  # Manage accounts across all roles
│   │   ├── VendorManagementScreen.kt# Register campus stalls, audit status
│   │   └── ReportsScreen.kt         # Financial audit & fulfillment breakdown
│   │
│   ├── components/
│   │   ├── CampusEatsTopBar.kt      # Reusable TopAppBar with role indicators & logout
│   │   ├── CampusEatsBottomBar.kt   # Role-adaptive bottom navigation
│   │   ├── StatusBadge.kt           # Color-coded order status chip
│   │   ├── RoleIndicatorCard.kt     # Role badge (STUDENT, VENDOR, ADMIN)
│   │   └── EmptyStateView.kt        # Polished empty state with action button
│   │
│   └── theme/
│       ├── Color.kt                 # Material 3 color tokens (Campus Orange, Sage Green, etc.)
│       ├── Type.kt                  # Typography scale
│       └── Theme.kt                 # Material 3 Theme setup with dynamic system bars
│
├── navigation/
│   ├── NavRoutes.kt                 # Sealed routes hierarchy
│   └── CampusEatsNavHost.kt         # Jetpack Compose NavHost
│
├── security/
│   ├── UserRole.kt                  # STUDENT, VENDOR, ADMIN enum
│   ├── PasswordHasher.kt            # SHA-256 password hashing utility
│   └── SessionManager.kt            # Shared preferences session management
│
├── multimedia/
│   └── ImageLoaderUtils.kt          # Image placeholder and asset handling
│
└── utils/
    ├── Resource.kt                  # Sealed Resource<T> (Success, Error, Loading)
    ├── CurrencyFormatter.kt         # Currency formatting utility (ZAR standard)
    └── DateUtils.kt                 # Timestamp & date formatting
```

---

## 🚀 How to Open and Run in Android Studio

1. **Export the Project**:
   - In Google AI Studio, click the Settings menu (top right) and select **"Export to ZIP"** or **"Export to GitHub"**.
2. **Open in Android Studio**:
   - Launch **Android Studio** (Hedgehog, Iguana, Jellyfish, or newer).
   - Select **Open an Existing Project** and browse to the extracted root folder.
3. **Gradle Sync**:
   - Android Studio will detect `settings.gradle.kts` and `build.gradle.kts` and automatically sync the dependencies.
4. **Run**:
   - Select an Android Emulator or connected physical device (Android 7.0 / API 24 or higher).
   - Click **Run 'app'** (`Shift + F10`).

---

## 👥 Pre-Seeded Demo Accounts for Testing

| Role | Email | Password | Details |
|---|---|---|---|
| **STUDENT** | `student@university.ac.za` | `password123` | Student ID: `ST2024098` |
| **VENDOR** | `vendor@campuseats.ac.za` | `password123` | Varsity Grill & Burgers |
| **ADMIN** | `admin@campuseats.ac.za` | `password123` | Campus Facilities Admin |

*Note: Quick demo buttons are built right into `LoginScreen.kt` for 1-tap testing!*
