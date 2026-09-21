package com.campuseats.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.campuseats.data.local.dao.FoodItemDao
import com.campuseats.data.local.dao.OrderDao
import com.campuseats.data.local.dao.UserDao
import com.campuseats.data.local.dao.VendorDao
import com.campuseats.data.local.entity.FoodItemEntity
import com.campuseats.data.local.entity.OrderEntity
import com.campuseats.data.local.entity.OrderItemEntity
import com.campuseats.data.local.entity.UserEntity
import com.campuseats.data.local.entity.VendorEntity
import com.campuseats.security.PasswordHasher
import com.campuseats.security.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * The Room SQLite database instance for Campus Eats, establishing clean local persistence.
 */
@Database(
    entities = [
        UserEntity::class,
        VendorEntity::class,
        FoodItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun vendorDao(): VendorDao
    abstract fun foodItemDao(): FoodItemDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "campus_eats_database.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        seedRequiredAccounts(database)
                    }
                }
            }

            private suspend fun seedRequiredAccounts(database: AppDatabase) {
                val userDao = database.userDao()

                // ADMIN: admin@campuseats.com / Admin@123
                userDao.insertUser(
                    UserEntity(
                        id = "user_admin_primary",
                        email = "admin@campuseats.com",
                        passwordHash = PasswordHasher.hash("Admin@123"),
                        fullName = "Campus Eats Administrator",
                        studentOrStaffId = "ADM001",
                        role = UserRole.ADMIN,
                        phoneNumber = "0119876000"
                    )
                )

                // VENDOR: vendor@campuseats.com / Vendor@123
                userDao.insertUser(
                    UserEntity(
                        id = "user_vendor_primary",
                        email = "vendor@campuseats.com",
                        passwordHash = PasswordHasher.hash("Vendor@123"),
                        fullName = "Varsity Grill Manager",
                        studentOrStaffId = "VND5001",
                        role = UserRole.VENDOR,
                        phoneNumber = "0839876543"
                    )
                )

                // STUDENT: student@campuseats.com / Student@123
                userDao.insertUser(
                    UserEntity(
                        id = "user_student_primary",
                        email = "student@campuseats.com",
                        passwordHash = PasswordHasher.hash("Student@123"),
                        fullName = "Campus Student",
                        studentOrStaffId = "ST2024001",
                        role = UserRole.STUDENT,
                        phoneNumber = "0821234567"
                    )
                )
            }

            private suspend fun populateInitialData(database: AppDatabase) {
                seedRequiredAccounts(database)
                val userDao = database.userDao()
                val vendorDao = database.vendorDao()
                val foodItemDao = database.foodItemDao()

                // Default legacy/alias demo accounts
                val defaultPasswordHash = PasswordHasher.hash("password123")

                userDao.insertUser(
                    UserEntity(
                        id = "user_student_1",
                        email = "student@university.ac.za",
                        passwordHash = defaultPasswordHash,
                        fullName = "Thulani Hlomuka",
                        studentOrStaffId = "ST2024098",
                        role = UserRole.STUDENT,
                        phoneNumber = "0821234567"
                    )
                )

                userDao.insertUser(
                    UserEntity(
                        id = "user_vendor_1",
                        email = "vendor@campuseats.ac.za",
                        passwordHash = defaultPasswordHash,
                        fullName = "Varsity Grill Manager",
                        studentOrStaffId = "VN5001",
                        role = UserRole.VENDOR,
                        phoneNumber = "0839876543"
                    )
                )

                userDao.insertUser(
                    UserEntity(
                        id = "user_admin_1",
                        email = "admin@campuseats.ac.za",
                        passwordHash = defaultPasswordHash,
                        fullName = "Campus Facilities Admin",
                        studentOrStaffId = "AD001",
                        role = UserRole.ADMIN,
                        phoneNumber = "0119876000"
                    )
                )

                // Default Vendors
                val vendors = listOf(
                    VendorEntity(
                        id = "vendor_1",
                        name = "Varsity Grill & Burgers",
                        description = "Flame-grilled burgers, crispy fries, and cold sodas right on campus.",
                        campusLocation = "Student Center, Ground Floor",
                        rating = 4.8,
                        isOpen = true,
                        openingHours = "08:30 - 18:00",
                        estimatedPrepTimeMinutes = 15
                    ),
                    VendorEntity(
                        id = "vendor_2",
                        name = "Campus Brew & Cafe",
                        description = "Artisan campus coffee, breakfast rolls, sandwiches and pastries.",
                        campusLocation = "Science Library Courtyard",
                        rating = 4.6,
                        isOpen = true,
                        openingHours = "07:30 - 16:30",
                        estimatedPrepTimeMinutes = 10
                    ),
                    VendorEntity(
                        id = "vendor_3",
                        name = "Green Bowl & Wraps",
                        description = "Fresh salads, protein bowls, smoothies and healthy wraps.",
                        campusLocation = "Engineering Building Piazza",
                        rating = 4.7,
                        isOpen = true,
                        openingHours = "09:00 - 15:30",
                        estimatedPrepTimeMinutes = 12
                    )
                )
                vendorDao.insertVendors(vendors)

                // Default Food Items for Varsity Grill
                val grillFoods = listOf(
                    FoodItemEntity(
                        id = "food_101",
                        vendorId = "vendor_1",
                        name = "Classic Smash Burger",
                        description = "100% pure beef patty, melted cheddar, lettuce, tomato and campus secret sauce on a brioche bun.",
                        price = 65.00,
                        category = "Burgers",
                        isAvailable = true,
                        calories = 620
                    ),
                    FoodItemEntity(
                        id = "food_102",
                        vendorId = "vendor_1",
                        name = "Double Bacon Cheeseburger",
                        description = "Twin patties, double smoked bacon, mature cheddar and caramelized onions.",
                        price = 85.00,
                        category = "Burgers",
                        isAvailable = true,
                        calories = 890
                    ),
                    FoodItemEntity(
                        id = "food_103",
                        vendorId = "vendor_1",
                        name = "Peri-Peri Chicken Burger",
                        description = "Crispy spiced chicken fillet, peri mayo, pickles and shredded lettuce.",
                        price = 68.00,
                        category = "Burgers",
                        isAvailable = true,
                        calories = 540
                    ),
                    FoodItemEntity(
                        id = "food_104",
                        vendorId = "vendor_1",
                        name = "Seasoned Campus Fries",
                        description = "Golden hand-cut fries seasoned with rosemary and paprika salt.",
                        price = 28.00,
                        category = "Sides",
                        isAvailable = true,
                        isVegetarian = true,
                        calories = 340
                    ),
                    FoodItemEntity(
                        id = "food_105",
                        vendorId = "vendor_1",
                        name = "Chilled Soft Drink 440ml",
                        description = "Coca-Cola, Sprite, or Fanta can.",
                        price = 18.00,
                        category = "Drinks",
                        isAvailable = true
                    )
                )
                foodItemDao.insertFoodItems(grillFoods)

                // Default Food Items for Cafe
                val cafeFoods = listOf(
                    FoodItemEntity(
                        id = "food_201",
                        vendorId = "vendor_2",
                        name = "Flat White Coffee",
                        description = "Double shot espresso with silky micro-foam milk.",
                        price = 32.00,
                        category = "Hot Drinks",
                        isAvailable = true,
                        isVegetarian = true
                    ),
                    FoodItemEntity(
                        id = "food_202",
                        vendorId = "vendor_2",
                        name = "Toasted Cheese & Tomato",
                        description = "Sourdough toastie with aged cheddar and fresh roma tomatoes.",
                        price = 38.00,
                        category = "Breakfast",
                        isAvailable = true,
                        isVegetarian = true
                    )
                )
                foodItemDao.insertFoodItems(cafeFoods)
            }
        }
    }
}
