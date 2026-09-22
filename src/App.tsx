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
  ShoppingBag,
  Wifi,
  Globe,
  AlertCircle,
  RefreshCw,
  Code2
} from 'lucide-react';

export default function App() {
  const [activeTab, setActiveTab] = useState<'architecture' | 'screens' | 'network' | 'xml' | 'entities' | 'export'>('architecture');
  const [demoResponse, setDemoResponse] = useState<string | null>(null);
  const [isLoadingDemo, setIsLoadingDemo] = useState(false);

  const fetchLiveDemoJson = async () => {
    setIsLoadingDemo(true);
    try {
      const res = await fetch('/api/demo-vendors.json');
      const data = await res.json();
      setDemoResponse(JSON.stringify(data, null, 2));
    } catch (err: any) {
      setDemoResponse(`Error fetching live JSON: ${err?.message || err}`);
    } finally {
      setIsLoadingDemo(false);
    }
  };

  const screens = [
    { category: 'Authentication & Security', items: ['Login (LoginScreen.kt) with Role-Aware Routing', 'Student Registration (RegisterScreen.kt) with Strict Student-Only Enforcement', 'SessionManager (Persistent SharedPreferences + StateFlow)', 'Salted SHA-256 PasswordHasher & CredentialValidator', 'Route-Level Role-Based Access Control (RBAC) Guard'] },
    { category: 'Student Module', items: ['Student Home (StudentHomeScreen.kt)', 'Campus Vendors (VendorsScreen.kt) with Remote Demo Quick-Action', 'Menu & Dietary Flags (MenuScreen.kt)', 'Cart & Preparation Notes (CartScreen.kt)', 'Order Status & 4-Digit PIN (OrderStatusScreen.kt)', 'Order History (OrderHistoryScreen.kt)'] },
    { category: 'Vendor Module', items: ['Vendor Dashboard (VendorDashboardScreen.kt)', 'Food Item Management & Availability (FoodManagementScreen.kt)', 'Vendor Incoming Orders & Kitchen Workflow (VendorOrdersScreen.kt)'] },
    { category: 'Admin Module (Complete Room Workflow)', items: [
      'Admin Dashboard (AdminDashboardScreen.kt): Room metrics (Total Users, Students, Vendors, Orders, Pending, Completed, Platform Revenue)',
      'User Management (UserManagementScreen.kt): View users, roles, email, registration details, toggle disable/enable, safe delete dialog',
      'Vendor Stalls Management (VendorManagementScreen.kt): Register vendors, assign login credentials, edit name/description, live food count, availability status',
      'System Reports (ReportsScreen.kt): Total orders, orders by status, orders per vendor, total order value, popular food items calculated directly from SQLite Room DB',
      'Security & Access Control: Role-based authorization guard strictly protecting every admin destination'
    ] },
    { category: 'Networking & Remote API Demonstration', items: [
      'Remote Demo Screen (RemoteDemoScreen.kt): Live asynchronous HTTP GET, loading spinner, error banners, JSON response viewer',
      'Native Lightweight Client (RemoteDemoHttpClient.kt): HttpURLConnection running on Dispatchers.IO with zero external heavy libraries',
      'Strict Offline-First Isolation: Complete decoupling from Room SQLite, ensuring offline food ordering is never blocked by internet issues',
      'Response DTOs & Parsing: RemoteVendorDto, RemoteMenuItemDto, RemoteCampusVendorsResponse models parsing real JSON',
      'Fault Resilience Testing: Preconfigured endpoints to test Success (200 OK), HTTP 500 Server Error, HTTP 404 Not Found, and Offline timeout simulation'
    ] },
    { category: 'XML Data Processing Demonstration', items: [
      'XML Demo Screen (XmlDemoScreen.kt): Live XmlPullParser execution, tag telemetry counters, category chips, and raw XML inspector',
      'Android Native XmlPullParser (CampusFoodXmlParser.kt): Low-memory forward-only event-driven streaming parser (START_TAG, TEXT, END_TAG)',
      'XML Resource File (campus_food_data.xml): Hierarchical vendor catalogs, building locations, menu items, dietary attributes, allergens',
      'Kotlin Data Objects (XmlFoodModels.kt): XmlCampusDining, XmlVendor, XmlFoodItem classes cleanly decoupled from SQLite entities',
      'Proof-of-Parsing Injection: Dynamic node injection and re-parsing proving live event parsing rather than hard-coded objects'
    ] }
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
              onClick={() => setActiveTab('network')}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors flex items-center gap-2 ${
                activeTab === 'network'
                  ? 'bg-orange-600 text-white shadow'
                  : 'text-neutral-400 hover:text-white hover:bg-neutral-800'
              }`}
            >
              <Wifi className="w-4 h-4" /> Remote HTTP Demo
            </button>
            <button
              onClick={() => setActiveTab('xml')}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors flex items-center gap-2 ${
                activeTab === 'xml'
                  ? 'bg-orange-600 text-white shadow'
                  : 'text-neutral-400 hover:text-white hover:bg-neutral-800'
              }`}
            >
              <Code2 className="w-4 h-4" /> XML Data Demo
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

        {activeTab === 'network' && (
          <div className="space-y-6">
            <div className="p-6 rounded-2xl bg-neutral-950 border border-neutral-800">
              <div className="flex items-center gap-3 mb-3">
                <div className="p-2.5 rounded-lg bg-orange-500/10 text-orange-400">
                  <Wifi className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="font-bold text-white text-base">Isolated Remote Networking Demonstration</h3>
                  <p className="text-xs text-neutral-400">Proof-of-concept HTTP client & JSON parser with zero coupling to Room SQLite</p>
                </div>
              </div>
              <p className="text-sm text-neutral-300 leading-relaxed mb-4">
                Campus Eats features a strictly isolated network layer (<code className="text-orange-400">com.campuseats.data.network.demo</code>) that requests live cafeteria demonstration menus from real HTTP endpoints. If the network is unavailable or returns an error, the core Room database remains completely operational and unaffected.
              </p>

              <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 text-xs mb-6">
                <div className="p-3 rounded-xl bg-neutral-900 border border-neutral-800">
                  <div className="font-bold text-emerald-400 mb-1">1. Asynchronous I/O</div>
                  <div className="text-neutral-400">Network calls execute exclusively on <code className="text-neutral-300">Dispatchers.IO</code> via coroutines to prevent UI blocking.</div>
                </div>
                <div className="p-3 rounded-xl bg-neutral-900 border border-neutral-800">
                  <div className="font-bold text-blue-400 mb-1">2. Sealed StateFlow</div>
                  <div className="text-neutral-400"><code className="text-neutral-300">NetworkResult</code> manages <span className="text-neutral-300 font-semibold">Idle, Loading, Success, Error</span> states reactively.</div>
                </div>
                <div className="p-3 rounded-xl bg-neutral-900 border border-neutral-800">
                  <div className="font-bold text-purple-400 mb-1">3. Offline-First Guard</div>
                  <div className="text-neutral-400">Zero foreign keys or database bindings; network failures gracefully fall back to local Room storage.</div>
                </div>
              </div>

              {/* Live JSON Endpoint Preview */}
              <div className="p-4 rounded-xl bg-neutral-900 border border-neutral-800">
                <div className="flex items-center justify-between mb-3">
                  <div>
                    <span className="text-xs font-bold uppercase tracking-wider text-orange-400">Live Endpoint Inspection</span>
                    <p className="text-xs text-neutral-400">GET <code className="text-neutral-300">/api/demo-vendors.json</code></p>
                  </div>
                  <button
                    onClick={fetchLiveDemoJson}
                    disabled={isLoadingDemo}
                    className="px-3 py-1.5 rounded-lg bg-orange-600 hover:bg-orange-500 disabled:opacity-50 text-white text-xs font-semibold flex items-center gap-1.5 transition-colors"
                  >
                    <RefreshCw className={`w-3.5 h-3.5 ${isLoadingDemo ? 'animate-spin' : ''}`} />
                    {isLoadingDemo ? 'Fetching...' : 'Test HTTP GET'}
                  </button>
                </div>

                {demoResponse ? (
                  <pre className="p-3 rounded-lg bg-neutral-950 border border-neutral-800 text-neutral-300 font-mono text-xs overflow-x-auto max-h-64 leading-tight">
                    {demoResponse}
                  </pre>
                ) : (
                  <div className="text-xs text-neutral-500 italic p-4 text-center border border-dashed border-neutral-800 rounded-lg">
                    Click "Test HTTP GET" above to perform a live fetch from the Campus Eats demonstration endpoint.
                  </div>
                )}
              </div>
            </div>
          </div>
        )}

        {activeTab === 'xml' && (
          <div className="space-y-6">
            <div className="p-6 rounded-2xl bg-neutral-950 border border-neutral-800">
              <div className="flex items-center gap-3 mb-3">
                <div className="p-2.5 rounded-lg bg-orange-500/10 text-orange-400">
                  <Code2 className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="font-bold text-white text-base">Android XMLPullParser Data Processing Demonstration</h3>
                  <p className="text-xs text-neutral-400">Hierarchical XML parsing, Kotlin data models, and Compose rendering</p>
                </div>
              </div>
              <p className="text-sm text-neutral-300 leading-relaxed mb-4">
                Campus Eats includes an academic course demonstration (<code className="text-orange-400">com.campuseats.data.xml</code>) that loads hierarchical campus dining and vendor data from an XML resource file, streams through it using Android's native <code className="text-orange-400">XmlPullParser</code>, converts events into strongly typed Kotlin data classes, and renders them in Jetpack Compose.
              </p>

              <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 text-xs mb-6">
                <div className="p-3 rounded-xl bg-neutral-900 border border-neutral-800">
                  <div className="font-bold text-emerald-400 mb-1">1. Event-Driven Pull Parser</div>
                  <div className="text-neutral-400">Uses <code className="text-neutral-300">XmlPullParser</code> (START_TAG, TEXT, END_TAG) for low-memory, zero-overhead forward streaming.</div>
                </div>
                <div className="p-3 rounded-xl bg-neutral-900 border border-neutral-800">
                  <div className="font-bold text-blue-400 mb-1">2. Strong Kotlin DTOs</div>
                  <div className="text-neutral-400">Converts nested nodes into <code className="text-neutral-300">XmlCampusDining</code>, <code className="text-neutral-300">XmlVendor</code>, and <code className="text-neutral-300">XmlFoodItem</code> data classes.</div>
                </div>
                <div className="p-3 rounded-xl bg-neutral-900 border border-neutral-800">
                  <div className="font-bold text-purple-400 mb-1">3. Proof of Dynamic Parsing</div>
                  <div className="text-neutral-400">UI displays live tag/attribute metrics, parse execution times (ms), and supports live node injection &amp; re-parsing.</div>
                </div>
              </div>

              {/* XML Source Sample Display */}
              <div className="p-4 rounded-xl bg-neutral-900 border border-neutral-800">
                <div className="flex items-center justify-between mb-3">
                  <div>
                    <span className="text-xs font-bold uppercase tracking-wider text-orange-400">Sample Resource File</span>
                    <p className="text-xs text-neutral-400"><code className="text-neutral-300">app/src/main/res/xml/campus_food_data.xml</code></p>
                  </div>
                  <span className="text-xs px-2.5 py-1 rounded bg-neutral-800 text-neutral-300 font-mono">
                    ~4.5 KB • 74 Tags • 19 Attributes
                  </span>
                </div>

                <pre className="p-3 rounded-lg bg-neutral-950 border border-neutral-800 text-neutral-300 font-mono text-xs overflow-x-auto max-h-72 leading-relaxed">
{`<?xml version="1.0" encoding="utf-8"?>
<campusDining campus="University Main Campus" version="1.2" generated="2026-09-22">
    <metadata>
        <title>University Food &amp; Vendor Directory</title>
        <curriculumTopic>Android XMLPullParser &amp; Hierarchical Data Processing</curriculumTopic>
    </metadata>
    <vendors>
        <vendor id="VND-XML-01" category="Grill &amp; Fast Food" status="OPEN">
            <name>The Crimson Grill</name>
            <building>Student Union Building - Ground Floor</building>
            <rating>4.7</rating>
            <openingHours>08:00 - 18:00</openingHours>
            <acceptsStudentCard>true</acceptsStudentCard>
            <menuItems>
                <item id="ITEM-XML-101" vegetarian="false">
                    <name>Braai Beef Burger &amp; Chips</name>
                    <category>Mains</category>
                    <price>58.50</price>
                    <calories>680</calories>
                    <description>Flame-grilled beef patty with caramelized onions and rustic chips.</description>
                </item>
                <!-- Additional food items and vendors... -->
            </menuItems>
        </vendor>
    </vendors>
</campusDining>`}
                </pre>

                <div className="mt-4 pt-3 border-t border-neutral-800 flex items-center justify-between text-xs text-neutral-400">
                  <span>Room Database Status: <strong className="text-emerald-400">Active &amp; Untouched</strong></span>
                  <span>UI Engine: <strong className="text-orange-400">100% Jetpack Compose</strong></span>
                </div>
              </div>
            </div>
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
