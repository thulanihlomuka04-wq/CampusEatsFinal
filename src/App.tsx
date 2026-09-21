import React, { useState } from 'react';
import {
  Smartphone,
  Layers,
  Database,
  ShieldCheck,
  FolderTree,
  Navigation,
  CheckCircle2,
  Download,
  Users,
  UtensilsCrossed,
  FileCode,
  LayoutDashboard,
  ShoppingBag
} from 'lucide-react';

export default function App() {
  const [activeTab, setActiveTab] = useState<'architecture' | 'screens' | 'entities' | 'export'>('architecture');

  const screens = [
    { category: 'Authentication & Security', items: ['Login (LoginScreen.kt) with Role-Aware Routing', 'Student Registration (RegisterScreen.kt) with Strict Student-Only Enforcement', 'SessionManager (Persistent SharedPreferences + StateFlow)', 'Salted SHA-256 PasswordHasher & CredentialValidator', 'Route-Level Role-Based Access Control (RBAC) Guard'] },
    { category: 'Student Module', items: ['Student Home (StudentHomeScreen.kt)', 'Campus Vendors (VendorsScreen.kt)', 'Menu & Dietary Flags (MenuScreen.kt)', 'Cart & Preparation Notes (CartScreen.kt)', 'Order Status & 4-Digit PIN (OrderStatusScreen.kt)', 'Order History (OrderHistoryScreen.kt)'] },
    { category: 'Vendor Module', items: ['Vendor Dashboard (VendorDashboardScreen.kt)', 'Food Item Management & Availability (FoodManagementScreen.kt)', 'Vendor Incoming Orders & Kitchen Workflow (VendorOrdersScreen.kt)'] },
    { category: 'Admin Module', items: ['Admin Dashboard (AdminDashboardScreen.kt)', 'User Management (UserManagementScreen.kt)', 'Vendor Stalls Management (VendorManagementScreen.kt)', 'Audit Reports & Financials (ReportsScreen.kt)'] }
  ];

  const entities = [
    { name: 'UserEntity', description: 'Table for students, vendors, and admins with SHA-256 hashed credentials.' },
    { name: 'VendorEntity', description: 'Table for campus food stalls, locations, operating hours, ratings.' },
    { name: 'FoodItemEntity', description: 'Table for menu items with Foreign Key to Vendor, pricing, calories, vegetarian flags.' },
    { name: 'OrderEntity', description: 'Table for order transactions with Foreign Keys to User & Vendor, status, pickup PIN.' },
    { name: 'OrderItemEntity', description: 'Line items breakdown with Foreign Key to Order.' }
  ];

  return (
    <div className="min-h-screen bg-neutral-900 text-neutral-100 font-sans">
      {/* Header */}
      <header className="border-b border-neutral-800 bg-neutral-950/80 backdrop-blur sticky top-0 z-50">
        <div className="max-w-6xl mx-auto px-6 py-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-orange-600 flex items-center justify-center text-white font-bold shadow-lg shadow-orange-600/20">
              <Smartphone className="w-5 h-5" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-xl font-bold tracking-tight">CAMPUS EATS</h1>
                <span className="text-xs px-2 py-0.5 rounded-full bg-orange-500/20 text-orange-400 font-medium border border-orange-500/30">
                  Native Android (Kotlin)
                </span>
              </div>
              <p className="text-xs text-neutral-400">Mobile Computing 2B Group Project • Package: <code className="text-neutral-300">com.campuseats</code></p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <div className="px-3 py-1.5 rounded-lg bg-neutral-800 text-xs text-neutral-300 border border-neutral-700 flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
              Android Studio Ready
            </div>
          </div>
        </div>
      </header>

      {/* Hero Banner */}
      <div className="bg-gradient-to-b from-neutral-950 to-neutral-900 border-b border-neutral-800 py-8 px-6">
        <div className="max-w-6xl mx-auto">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
            <div>
              <span className="text-orange-500 font-semibold text-xs tracking-wider uppercase">Genuine Native Android Architecture</span>
              <h2 className="text-2xl md:text-3xl font-extrabold text-white mt-1">
                University Food-Ordering System
              </h2>
              <p className="text-neutral-400 text-sm max-w-2xl mt-2 leading-relaxed">
                Engineered specifically with <strong>Kotlin 2.0</strong>, <strong>Jetpack Compose</strong>, <strong>Material 3</strong>, <strong>Room SQLite</strong>, and <strong>Gradle Kotlin DSL</strong>. Complete separation of UI, Repositories, DAOs, and Network layers.
              </p>
            </div>
            <div className="flex items-center gap-3">
              <div className="p-4 rounded-xl bg-neutral-800/80 border border-neutral-700/60 text-center min-w-[120px]">
                <div className="text-2xl font-bold text-orange-400">14</div>
                <div className="text-xs text-neutral-400">Compose Screens</div>
              </div>
              <div className="p-4 rounded-xl bg-neutral-800/80 border border-neutral-700/60 text-center min-w-[120px]">
                <div className="text-2xl font-bold text-emerald-400">3</div>
                <div className="text-xs text-neutral-400">User Roles</div>
              </div>
              <div className="p-4 rounded-xl bg-neutral-800/80 border border-neutral-700/60 text-center min-w-[120px]">
                <div className="text-2xl font-bold text-blue-400">5</div>
                <div className="text-xs text-neutral-400">Room Tables</div>
              </div>
            </div>
          </div>

          {/* Navigation Tabs */}
          <div className="flex items-center gap-2 mt-8 border-b border-neutral-800 pb-2">
            <button
              onClick={() => setActiveTab('architecture')}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors flex items-center gap-2 ${
                activeTab === 'architecture'
                  ? 'bg-orange-600 text-white shadow'
                  : 'text-neutral-400 hover:text-white hover:bg-neutral-800'
              }`}
            >
              <Layers className="w-4 h-4" /> Architecture & Layers
            </button>
            <button
              onClick={() => setActiveTab('screens')}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors flex items-center gap-2 ${
                activeTab === 'screens'
                  ? 'bg-orange-600 text-white shadow'
                  : 'text-neutral-400 hover:text-white hover:bg-neutral-800'
              }`}
            >
              <Navigation className="w-4 h-4" /> Top-Level Destinations
            </button>
            <button
              onClick={() => setActiveTab('entities')}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors flex items-center gap-2 ${
                activeTab === 'entities'
                  ? 'bg-orange-600 text-white shadow'
                  : 'text-neutral-400 hover:text-white hover:bg-neutral-800'
              }`}
            >
              <Database className="w-4 h-4" /> Room SQLite Database
            </button>
            <button
              onClick={() => setActiveTab('export')}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors flex items-center gap-2 ${
                activeTab === 'export'
                  ? 'bg-orange-600 text-white shadow'
                  : 'text-neutral-400 hover:text-white hover:bg-neutral-800'
              }`}
            >
              <Download className="w-4 h-4" /> Android Studio Setup
            </button>
          </div>
        </div>
      </div>

      {/* Main Content Area */}
      <main className="max-w-6xl mx-auto px-6 py-8">
        {activeTab === 'architecture' && (
          <div className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Architecture Overview */}
              <div className="p-6 rounded-2xl bg-neutral-950 border border-neutral-800">
                <div className="flex items-center gap-3 mb-4">
                  <div className="p-2.5 rounded-lg bg-orange-500/10 text-orange-400">
                    <FolderTree className="w-5 h-5" />
                  </div>
                  <div>
                    <h3 className="font-bold text-white text-base">MVVM & Clean Architecture</h3>
                    <p className="text-xs text-neutral-400">Strict separation of concerns across packages</p>
                  </div>
                </div>

                <div className="space-y-3 text-xs font-mono">
                  <div className="p-2.5 rounded-lg bg-neutral-900 border border-neutral-800">
                    <span className="text-orange-400 font-bold">com.campuseats.data.local</span>
                    <p className="text-neutral-400 font-sans mt-0.5">Room SQLite Database, TypeConverters, DAOs, and relational Entities</p>
                  </div>
                  <div className="p-2.5 rounded-lg bg-neutral-900 border border-neutral-800">
                    <span className="text-emerald-400 font-bold">com.campuseats.data.network</span>
                    <p className="text-neutral-400 font-sans mt-0.5">Retrofit REST API Service stubs, ApiResponse & DTO definitions</p>
                  </div>
                  <div className="p-2.5 rounded-lg bg-neutral-900 border border-neutral-800">
                    <span className="text-blue-400 font-bold">com.campuseats.data.repository</span>
                    <p className="text-neutral-400 font-sans mt-0.5">AuthRepository, StudentRepository, VendorRepository, AdminRepository</p>
                  </div>
                  <div className="p-2.5 rounded-lg bg-neutral-900 border border-neutral-800">
                    <span className="text-purple-400 font-bold">com.campuseats.ui</span>
                    <p className="text-neutral-400 font-sans mt-0.5">ViewModels, Compose Screens (auth, student, vendor, admin), Material 3 Theme</p>
                  </div>
                  <div className="p-2.5 rounded-lg bg-neutral-900 border border-neutral-800">
                    <span className="text-amber-400 font-bold">com.campuseats.security</span>
                    <p className="text-neutral-400 font-sans mt-0.5">UserRole Enum, SHA-256 PasswordHasher, SessionManager</p>
                  </div>
                </div>
              </div>

              {/* Roles Breakdown */}
              <div className="p-6 rounded-2xl bg-neutral-950 border border-neutral-800">
                <div className="flex items-center gap-3 mb-4">
                  <div className="p-2.5 rounded-lg bg-emerald-500/10 text-emerald-400">
                    <Users className="w-5 h-5" />
                  </div>
                  <div>
                    <h3 className="font-bold text-white text-base">3 Distinct User Roles</h3>
                    <p className="text-xs text-neutral-400">Tailored dashboards and permissions</p>
                  </div>
                </div>

                <div className="space-y-3">
                  <div className="p-3 rounded-xl bg-neutral-900 border border-neutral-800">
                    <div className="flex items-center justify-between mb-1">
                      <span className="text-sm font-bold text-emerald-400">1. STUDENT</span>
                      <span className="text-xs text-neutral-500">6 Destinations</span>
                    </div>
                    <p className="text-xs text-neutral-400">
                      Browse open campus food stalls, view food items with dietary info, manage shopping cart, place orders, and track preparation with a 4-digit pickup PIN.
                    </p>
                  </div>

                  <div className="p-3 rounded-xl bg-neutral-900 border border-neutral-800">
                    <div className="flex items-center justify-between mb-1">
                      <span className="text-sm font-bold text-orange-400">2. VENDOR</span>
                      <span className="text-xs text-neutral-500">3 Destinations</span>
                    </div>
                    <p className="text-xs text-neutral-400">
                      View real-time pending & kitchen orders, advance orders from Pending → Preparing → Ready for Pickup → Completed, add/edit menu items, and toggle dish availability.
                    </p>
                  </div>

                  <div className="p-3 rounded-xl bg-neutral-900 border border-neutral-800">
                    <div className="flex items-center justify-between mb-1">
                      <span className="text-sm font-bold text-purple-400">3. ADMINISTRATOR</span>
                      <span className="text-xs text-neutral-500">4 Destinations</span>
                    </div>
                    <p className="text-xs text-neutral-400">
                      University-wide dashboard monitoring registered users, onboarding new campus food stalls, auditing order fulfillment, and tracking platform gross revenue.
                    </p>
                  </div>
                </div>
              </div>
            </div>

            {/* Verification Checklist */}
            <div className="p-6 rounded-2xl bg-neutral-950 border border-neutral-800">
              <h3 className="font-bold text-white text-base mb-4 flex items-center gap-2">
                <ShieldCheck className="w-5 h-5 text-emerald-400" />
                University Mobile Computing 2B Technical Compliance
              </h3>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 text-xs">
                <div className="flex items-start gap-2 text-neutral-300">
                  <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
                  <span>Kotlin 2.0 with Gradle Kotlin DSL (.kts)</span>
                </div>
                <div className="flex items-start gap-2 text-neutral-300">
                  <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
                  <span>Jetpack Compose + Material 3 Theme</span>
                </div>
                <div className="flex items-start gap-2 text-neutral-300">
                  <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
                  <span>Room SQLite with DAOs & Entities</span>
                </div>
                <div className="flex items-start gap-2 text-neutral-300">
                  <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
                  <span>MVVM + Repository Pattern Architecture</span>
                </div>
                <div className="flex items-start gap-2 text-neutral-300">
                  <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
                  <span>Jetpack Navigation Compose with sealed routes</span>
                </div>
                <div className="flex items-start gap-2 text-neutral-300">
                  <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
                  <span>Lifecycle-aware Coroutines & StateFlow</span>
                </div>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'screens' && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {screens.map((group, idx) => (
              <div key={idx} className="p-6 rounded-2xl bg-neutral-950 border border-neutral-800">
                <h3 className="font-bold text-orange-400 text-base mb-3 flex items-center gap-2">
                  <Navigation className="w-4 h-4" /> {group.category}
                </h3>
                <ul className="space-y-2.5">
                  {group.items.map((item, i) => (
                    <li key={i} className="flex items-center gap-2 text-sm text-neutral-300 bg-neutral-900/60 p-2.5 rounded-lg border border-neutral-800">
                      <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
                      <span>{item}</span>
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
        )}

        {activeTab === 'entities' && (
          <div className="space-y-4">
            <div className="p-4 rounded-xl bg-neutral-950 border border-neutral-800 text-sm text-neutral-400">
              The application uses <strong>Android Room SQLite</strong> with automatic seed data generated in <code className="text-orange-400">AppDatabase.kt</code> upon first launch, providing ready-to-test vendors, menu items, and accounts.
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {entities.map((e, idx) => (
                <div key={idx} className="p-5 rounded-xl bg-neutral-950 border border-neutral-800">
                  <div className="flex items-center gap-2 mb-2">
                    <Database className="w-4 h-4 text-orange-400" />
                    <span className="font-bold text-white text-base">{e.name}</span>
                  </div>
                  <p className="text-xs text-neutral-400 leading-relaxed">{e.description}</p>
                </div>
              ))}
            </div>
          </div>
        )}

        {activeTab === 'export' && (
          <div className="p-6 rounded-2xl bg-neutral-950 border border-neutral-800 space-y-6">
            <h3 className="font-bold text-white text-lg flex items-center gap-2">
              <Download className="w-5 h-5 text-orange-400" />
              Opening in Android Studio
            </h3>

            <div className="space-y-4 text-sm text-neutral-300">
              <div className="flex items-start gap-3 p-4 rounded-xl bg-neutral-900 border border-neutral-800">
                <span className="w-6 h-6 rounded-full bg-orange-600 text-white font-bold flex items-center justify-center text-xs shrink-0 mt-0.5">1</span>
                <div>
                  <h4 className="font-bold text-white mb-1">Export Repository to ZIP or GitHub</h4>
                  <p className="text-xs text-neutral-400">In Google AI Studio, click the project settings menu at top-right and select <strong>Export to ZIP</strong> or <strong>Export to GitHub</strong>.</p>
                </div>
              </div>

              <div className="flex items-start gap-3 p-4 rounded-xl bg-neutral-900 border border-neutral-800">
                <span className="w-6 h-6 rounded-full bg-orange-600 text-white font-bold flex items-center justify-center text-xs shrink-0 mt-0.5">2</span>
                <div>
                  <h4 className="font-bold text-white mb-1">Open in Android Studio</h4>
                  <p className="text-xs text-neutral-400">Launch Android Studio (version Hedgehog 2023.1 or newer). Click <strong>Open</strong> and choose the root directory where <code className="text-orange-400">settings.gradle.kts</code> is located.</p>
                </div>
              </div>

              <div className="flex items-start gap-3 p-4 rounded-xl bg-neutral-900 border border-neutral-800">
                <span className="w-6 h-6 rounded-full bg-orange-600 text-white font-bold flex items-center justify-center text-xs shrink-0 mt-0.5">3</span>
                <div>
                  <h4 className="font-bold text-white mb-1">Run on Android Emulator or Physical Device</h4>
                  <p className="text-xs text-neutral-400">Android Studio automatically triggers Gradle sync. Press <kbd className="bg-neutral-800 px-2 py-0.5 rounded border border-neutral-700 text-xs">Shift + F10</kbd> or click the green <strong>Play</strong> button.</p>
                </div>
              </div>
            </div>

            {/* Test Accounts Box */}
            <div className="p-4 rounded-xl bg-orange-950/30 border border-orange-800/40">
              <h4 className="text-xs font-bold text-orange-400 uppercase tracking-wider mb-2">Pre-Seeded Testing Accounts</h4>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 text-xs">
                <div className="p-2.5 rounded-lg bg-neutral-900 border border-neutral-800">
                  <div className="font-bold text-emerald-400">Student Role</div>
                  <div className="text-neutral-300">student@university.ac.za</div>
                  <div className="text-neutral-500">password123</div>
                </div>
                <div className="p-2.5 rounded-lg bg-neutral-900 border border-neutral-800">
                  <div className="font-bold text-orange-400">Vendor Role</div>
                  <div className="text-neutral-300">vendor@campuseats.ac.za</div>
                  <div className="text-neutral-500">password123</div>
                </div>
                <div className="p-2.5 rounded-lg bg-neutral-900 border border-neutral-800">
                  <div className="font-bold text-purple-400">Admin Role</div>
                  <div className="text-neutral-300">admin@campuseats.ac.za</div>
                  <div className="text-neutral-500">password123</div>
                </div>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
