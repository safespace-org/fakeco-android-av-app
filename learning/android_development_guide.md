# Android Development Guide: Building the FakeCo Anti-Virus App

This comprehensive guide documents the process of building the FakeCo Anti-Virus Android application from scratch. It's designed for programmers who are familiar with general programming concepts but new to Android development. Each section explains not just what was done, but why specific approaches were chosen and how they fit into the Android ecosystem.

## Table of Contents

1. [Project Setup and Configuration](#1-project-setup-and-configuration)
2. [Architecture and Design Patterns](#2-architecture-and-design-patterns)
3. [Core Application Components](#3-core-application-components)
4. [UI Implementation](#4-ui-implementation)
5. [Feature Implementation](#5-feature-implementation)
6. [Resource Management](#6-resource-management)
7. [Testing and Quality Assurance](#7-testing-and-quality-assurance)
8. [Lessons Learned and Best Practices](#8-lessons-learned-and-best-practices)

## 1. Project Setup and Configuration

### 1.1 Creating the Project Structure

The first step in any Android application is setting up the project structure. For the FakeCo Anti-Virus app, we created a standard Android project with the following key configurations:

- **Package Name**: `com.fakeco.security` - This follows the reverse domain name convention, which is standard in Android development.
- **Minimum SDK**: API 24 (Android 7.0 Nougat) - This provides a good balance between modern features and wide device compatibility.
- **Target SDK**: API 33 (Android 13) - Targeting the latest stable Android version ensures we can use modern APIs.

### 1.2 Gradle Configuration

The build.gradle files are crucial for Android projects as they define dependencies, build configurations, and project settings. We set up two main Gradle files:

#### Project-level build.gradle

```groovy
buildscript {
    ext {
        kotlin_version = '1.7.20'
        hilt_version = '2.44'
        nav_version = '2.5.3'
        room_version = '2.5.0'
        lifecycle_version = '2.5.1'
    }
    
    repositories {
        google()
        mavenCentral()
    }
    
    dependencies {
        classpath 'com.android.tools.build:gradle:7.3.1'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        classpath "com.google.dagger:hilt-android-gradle-plugin:$hilt_version"
    }
}
```

This file defines version constants for key libraries and includes the necessary plugins for the project. Using version variables like this makes it easier to update dependencies across the project.

#### App-level build.gradle

```groovy
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'kotlin-kapt'
    id 'dagger.hilt.android.plugin'
}

android {
    compileSdk 33
    
    defaultConfig {
        applicationId "com.fakeco.security"
        minSdk 24
        targetSdk 33
        versionCode 1
        versionName "1.0"
        
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
    
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    
    kotlinOptions {
        jvmTarget = '1.8'
    }
    
    buildFeatures {
        viewBinding true
    }
}

dependencies {
    // Core Android libraries
    implementation 'androidx.core:core-ktx:1.9.0'
    implementation 'androidx.appcompat:appcompat:1.6.0'
    implementation 'com.google.android.material:material:1.8.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    
    // Navigation component
    implementation "androidx.navigation:navigation-fragment-ktx:$nav_version"
    implementation "androidx.navigation:navigation-ui-ktx:$nav_version"
    
    // Lifecycle components
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"
    implementation "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version"
    implementation "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version"
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4'
    
    // Hilt for dependency injection
    implementation "com.google.dagger:hilt-android:$hilt_version"
    kapt "com.google.dagger:hilt-compiler:$hilt_version"
    
    // Room for database
    implementation "androidx.room:room-runtime:$room_version"
    implementation "androidx.room:room-ktx:$room_version"
    kapt "androidx.room:room-compiler:$room_version"
    
    // Preferences
    implementation 'androidx.preference:preference-ktx:1.2.0'
    
    // Lottie for animations
    implementation 'com.airbnb.android:lottie:5.2.0'
    
    // Testing
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}
```

This file includes:

- **Plugins**: Kotlin, Kapt (for annotation processing), and Hilt (for dependency injection)
- **Android Configuration**: SDK versions, application ID, and build types
- **Dependencies**: All the libraries needed for the project

**Why these dependencies?**

- **AndroidX Core and AppCompat**: Provide backward compatibility for newer Android features
- **Material Design**: Implements Google's Material Design components
- **Navigation Component**: Simplifies navigation between screens
- **Lifecycle Components**: Manage UI-related data based on the lifecycle of UI components
- **Coroutines**: Handle asynchronous operations elegantly
- **Hilt**: Simplifies dependency injection
- **Room**: Provides an abstraction layer over SQLite for database operations
- **Preferences**: Manages user preferences
- **Lottie**: Renders Adobe After Effects animations, used for our shield animation

### 1.3 AndroidManifest.xml

The AndroidManifest.xml file is essential as it declares the app's components, permissions, and features to the Android system:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.fakeco.security">

    <!-- Permissions -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.VIBRATE" />

    <application
        android:name=".FakeCoApp"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.FakeCo">
        
        <activity
            android:name=".ui.MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <!-- Services -->
        <service
            android:name=".service.ProtectionService"
            android:enabled="true"
            android:exported="false" />
            
        <!-- Receivers -->
        <receiver
            android:name=".receiver.BootReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>
    </application>
</manifest>
```

**Key components in the manifest:**

- **Permissions**: Required for internet access, file reading, background services, and notifications
- **Application**: Points to our custom Application class and sets app-wide properties
- **Activities**: Declares our MainActivity as the launcher activity
- **Services**: Includes our ProtectionService for background operations
- **Receivers**: Contains a BootReceiver to restart services after device reboot

## 2. Architecture and Design Patterns

### 2.1 MVVM Architecture

For the FakeCo Anti-Virus app, we implemented the Model-View-ViewModel (MVVM) architecture pattern, which is recommended by Google for Android applications. This pattern separates the UI (View) from the business logic (ViewModel) and data (Model), making the code more maintainable, testable, and scalable.

#### Why MVVM?

1. **Separation of Concerns**: Each component has a specific responsibility
2. **Testability**: ViewModels can be tested independently of the UI
3. **Lifecycle Awareness**: ViewModels survive configuration changes (like screen rotations)
4. **Data Binding**: Simplifies UI updates when data changes

### 2.2 Repository Pattern

We implemented the Repository pattern to abstract the data sources from the rest of the application. Repositories act as mediators between different data sources (like databases, network, or preferences) and the ViewModels.

```kotlin
class ScanRepository @Inject constructor(
    private val scanDao: ScanDao,
    private val threatDao: ThreatDao
) {
    suspend fun saveScanResult(scanResult: ScanResult): Long {
        return scanDao.insert(scanResult)
    }
    
    suspend fun saveThreats(threats: List<Threat>) {
        threatDao.insertAll(threats)
    }
    
    fun getRecentScans(limit: Int): Flow<List<ScanResult>> {
        return scanDao.getRecentScans(limit)
    }
    
    fun getThreatsForScan(scanId: Long): Flow<List<Threat>> {
        return threatDao.getThreatsForScan(scanId)
    }
}
```

**Benefits of the Repository Pattern:**

1. **Single Source of Truth**: The repository decides which data source to use
2. **Abstraction**: ViewModels don't need to know where the data comes from
3. **Testability**: Repositories can be easily mocked for testing
4. **Offline Support**: Can implement caching strategies

### 2.3 Dependency Injection with Hilt

We used Hilt, Google's recommended dependency injection library for Android, to manage dependencies throughout the app. Hilt simplifies dependency injection by generating the necessary boilerplate code.

#### Application Class with Hilt

```kotlin
@HiltAndroidApp
class FakeCoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any app-wide components here
    }
}
```

The `@HiltAndroidApp` annotation triggers Hilt's code generation.

#### Module for Providing Dependencies

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "fakeco_database"
        ).build()
    }
    
    @Provides
    fun provideScanDao(database: AppDatabase): ScanDao {
        return database.scanDao()
    }
    
    @Provides
    fun provideThreatDao(database: AppDatabase): ThreatDao {
        return database.threatDao()
    }
    
    @Provides
    @Singleton
    fun provideScanRepository(
        scanDao: ScanDao,
        threatDao: ThreatDao
    ): ScanRepository {
        return ScanRepository(scanDao, threatDao)
    }
}
```

This module provides instances of the database, DAOs, and repositories that can be injected into other classes.

#### Injecting Dependencies

```kotlin
@AndroidEntryPoint
class ScanFragment : BaseFragment<FragmentScanBinding>(FragmentScanBinding::inflate) {
    private val viewModel: ScanViewModel by viewModels()
    // ...
}

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val scanRepository: ScanRepository
) : BaseViewModel() {
    // ...
}
```

The `@AndroidEntryPoint` annotation makes Hilt aware of the fragment, and the `@HiltViewModel` annotation allows Hilt to provide the ViewModel with its dependencies.

## 3. Core Application Components

### 3.1 Resource Class for State Management

We created a `Resource` class to handle different states of data loading (Success, Error, Loading). This is a common pattern in Android development for managing asynchronous operations.

```kotlin
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val exception: Exception? = null) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
    
    companion object {
        fun <T> success(data: T): Resource<T> = Success(data)
        fun error(message: String, exception: Exception? = null): Resource<Nothing> = Error(message, exception)
        fun loading(): Resource<Nothing> = Loading
    }
}
```

**Why this approach?**

1. **Type Safety**: The sealed class ensures all possible states are handled
2. **Consistency**: Provides a standard way to represent data states across the app
3. **Error Handling**: Includes both error messages and exceptions for detailed error reporting

### 3.2 BaseViewModel

We created a `BaseViewModel` class to share common functionality across all ViewModels:

```kotlin
open class BaseViewModel : ViewModel() {
    protected val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    protected val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    protected fun <T> Flow<T>.asLiveData(): LiveData<T> {
        return androidx.lifecycle.asLiveData(viewModelScope.coroutineContext)
    }
    
    protected fun launchDataLoad(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                block()
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
```

**Key features:**

1. **Error Handling**: Provides a standard way to expose errors to the UI
2. **Loading State**: Manages loading state for UI feedback
3. **Coroutine Helpers**: Simplifies launching coroutines with error handling
4. **Flow to LiveData**: Converts Flow to LiveData for easier observation in the UI

### 3.3 BaseFragment

We created a `BaseFragment` class to share common functionality across all fragments:

```kotlin
abstract class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean) -> VB
) : Fragment() {
    
    private var _binding: VB? = null
    val binding: VB
        get() = _binding ?: throw IllegalStateException("Binding is only valid between onCreateView and onDestroyView")
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    abstract fun setupUI()
    
    abstract fun observeViewModel()
    
    protected fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
    
    protected fun showLoading(isLoading: Boolean) {
        // Implement loading indicator logic
    }
}
```

**Key features:**

1. **View Binding**: Handles the binding lifecycle to prevent memory leaks
2. **Template Methods**: Defines abstract methods for UI setup and ViewModel observation
3. **Error Handling**: Provides a standard way to show error messages
4. **Loading State**: Manages loading indicators

## 4. UI Implementation

### 4.1 MainActivity

The MainActivity serves as the host for all fragments and manages the bottom navigation:

```kotlin
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNavigation()
    }
    
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        binding.bottomNavigation.setupWithNavController(navController)
        
        // Hide bottom navigation on specific destinations
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.dashboardFragment, R.id.scanFragment, 
                R.id.protectionFragment, R.id.settingsFragment -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomNavigation.visibility = View.GONE
                }
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
```

**Key components:**

1. **View Binding**: Uses view binding for type-safe access to views
2. **Navigation Controller**: Sets up the Navigation component for fragment navigation
3. **Bottom Navigation**: Configures the bottom navigation bar with the navigation controller
4. **Destination Listener**: Shows/hides the bottom navigation based on the current destination

### 4.2 Navigation Graph

The navigation graph defines the navigation structure of the app:

```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/nav_graph"
    app:startDestination="@id/dashboardFragment">

    <fragment
        android:id="@+id/dashboardFragment"
        android:name="com.fakeco.security.ui.dashboard.DashboardFragment"
        android:label="@string/dashboard"
        tools:layout="@layout/fragment_dashboard" />

    <fragment
        android:id="@+id/scanFragment"
        android:name="com.fakeco.security.ui.scan.ScanFragment"
        android:label="@string/scan"
        tools:layout="@layout/fragment_scan" />

    <fragment
        android:id="@+id/protectionFragment"
        android:name="com.fakeco.security.ui.protection.ProtectionFragment"
        android:label="@string/protection"
        tools:layout="@layout/fragment_protection" />

    <fragment
        android:id="@+id/settingsFragment"
        android:name="com.fakeco.security.ui.settings.SettingsFragment"
        android:label="@string/settings"
        tools:layout="@layout/fragment_settings" />
</navigation>
```

This defines four main destinations corresponding to the bottom navigation tabs.

### 4.3 Activity Layout

The main activity layout includes a NavHostFragment for fragment container and a BottomNavigationView:

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".ui.MainActivity">

    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/nav_host_fragment"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:defaultNavHost="true"
        app:layout_constraintBottom_toTopOf="@+id/bottom_navigation"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:navGraph="@navigation/nav_graph" />

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_navigation"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="0dp"
        android:layout_marginEnd="0dp"
        android:background="?android:attr/windowBackground"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:menu="@menu/bottom_nav_menu" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

**Key components:**

1. **FragmentContainerView**: Hosts the current fragment from the navigation graph
2. **BottomNavigationView**: Provides navigation between the main sections of the app
3. **ConstraintLayout**: Positions the fragments and navigation bar efficiently

## 5. Feature Implementation

### 5.1 Dashboard Feature

The Dashboard is the main screen of the app, showing the security status and quick actions.

#### DashboardFragment

```kotlin
@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>(
    FragmentDashboardBinding::inflate
) {
    private val viewModel: DashboardViewModel by viewModels()
    
    override fun setupUI() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshDashboard()
        }
        
        binding.cardAntivirus.setOnClickListener {
            viewModel.toggleAntivirusProtection()
        }
        
        binding.cardFirewall.setOnClickListener {
            viewModel.toggleFirewallProtection()
        }
        
        binding.cardVpn.setOnClickListener {
            viewModel.toggleVpnProtection()
        }
        
        binding.btnScan.setOnClickListener {
            findNavController().navigate(R.id.scanFragment)
        }
    }
    
    override fun observeViewModel() {
        viewModel.securityStatus.observe(viewLifecycleOwner) { status ->
            updateSecurityScore(status.overallScore)
            updateFeatureCards(status)
            updateLastScanInfo(status.lastScanInfo)
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                showError(errorMessage)
            }
        }
    }
    
    private fun updateSecurityScore(score: Int) {
        binding.securityScoreText.text = "$score%"
        binding.securityScoreProgress.progress = score
        
        val colorRes = when {
            score >= 80 -> R.color.security_good
            score >= 50 -> R.color.security_warning
            else -> R.color.security_danger
        }
        
        binding.securityScoreProgress.progressTintList = 
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorRes))
    }
    
    private fun updateFeatureCards(status: SecurityStatus) {
        // Update antivirus card
        binding.cardAntivirus.apply {
            val antivirusStatus = status.antivirusEnabled
            binding.switchAntivirus.isChecked = antivirusStatus
            binding.antivirusStatusIndicator.setImageResource(
                if (antivirusStatus) R.drawable.ic_status_enabled else R.drawable.ic_status_disabled
            )
        }
        
        // Update firewall card
        binding.cardFirewall.apply {
            val firewallStatus = status.firewallEnabled
            binding.switchFirewall.isChecked = firewallStatus
            binding.firewallStatusIndicator.setImageResource(
                if (firewallStatus) R.drawable.ic_status_enabled else R.drawable.ic_status_disabled
            )
        }
        
        // Update VPN card
        binding.cardVpn.apply {
            val vpnStatus = status.vpnEnabled
            binding.switchVpn.isChecked = vpnStatus
            binding.vpnStatusIndicator.setImageResource(
                if (vpnStatus) R.drawable.ic_status_enabled else R.drawable.ic_status_disabled
            )
        }
    }
    
    private fun updateLastScanInfo(lastScanInfo: LastScanInfo?) {
        if (lastScanInfo != null) {
            binding.lastScanDate.text = lastScanInfo.formattedDate
            binding.lastScanResult.text = when {
                lastScanInfo.threatsFound > 0 -> 
                    getString(R.string.threats_found, lastScanInfo.threatsFound)
                else -> getString(R.string.no_threats_found)
            }
        } else {
            binding.lastScanDate.text = getString(R.string.never_scanned)
            binding.lastScanResult.text = getString(R.string.scan_now)
        }
    }
}
```

**Key components:**

1. **View Binding**: Uses view binding for type-safe access to views
2. **ViewModel Injection**: Uses Hilt to inject the ViewModel
3. **UI Setup**: Configures click listeners and SwipeRefreshLayout
4. **ViewModel Observation**: Observes LiveData from the ViewModel to update the UI
5. **UI Updates**: Updates the security score, feature cards, and last scan info based on the data

#### DashboardViewModel

```kotlin
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : BaseViewModel() {
    
    private val _securityStatus = MutableLiveData<SecurityStatus>()
    val securityStatus: LiveData<SecurityStatus> = _securityStatus
    
    init {
        refreshDashboard()
    }
    
    fun refreshDashboard() {
        launchDataLoad {
            // In a real app, this would come from the repository
            // For now, we'll generate mock data
            val mockStatus = generateMockSecurityStatus()
            _securityStatus.value = mockStatus
        }
    }
    
    fun toggleAntivirusProtection() {
        launchDataLoad {
            val currentStatus = _securityStatus.value ?: return@launchDataLoad
            val newStatus = currentStatus.copy(
                antivirusEnabled = !currentStatus.antivirusEnabled,
                overallScore = recalculateScore(
                    !currentStatus.antivirusEnabled,
                    currentStatus.firewallEnabled,
                    currentStatus.vpnEnabled
                )
            )
            _securityStatus.value = newStatus
            // In a real app, we would update this in the repository
        }
    }
    
    fun toggleFirewallProtection() {
        launchDataLoad {
            val currentStatus = _securityStatus.value ?: return@launchDataLoad
            val newStatus = currentStatus.copy(
                firewallEnabled = !currentStatus.firewallEnabled,
                overallScore = recalculateScore(
                    currentStatus.antivirusEnabled,
                    !currentStatus.firewallEnabled,
                    currentStatus.vpnEnabled
                )
            )
            _securityStatus.value = newStatus
            // In a real app, we would update this in the repository
        }
    }
    
    fun toggleVpnProtection() {
        launchDataLoad {
            val currentStatus = _securityStatus.value ?: return@launchDataLoad
            val newStatus = currentStatus.copy(
                vpnEnabled = !currentStatus.vpnEnabled,
                overallScore = recalculateScore(
                    currentStatus.antivirusEnabled,
                    currentStatus.firewallEnabled,
                    !currentStatus.vpnEnabled
                )
            )
            _securityStatus.value = newStatus
            // In a real app, we would update this in the repository
        }
    }
    
    private fun recalculateScore(antivirus: Boolean, firewall: Boolean, vpn: Boolean): Int {
        var score = 0
        if (antivirus) score += 40
        if (firewall) score += 30
        if (vpn) score += 30
        return score
    }
    
    private fun generateMockSecurityStatus(): SecurityStatus {
        val antivirusEnabled = Random.nextBoolean()
        val firewallEnabled = Random.nextBoolean()
        val vpnEnabled = Random.nextBoolean()
        
        val score = recalculateScore(antivirusEnabled, firewallEnabled, vpnEnabled)
        
        val lastScanInfo = if (Random.nextBoolean()) {
            LastScanInfo(
                date = Calendar.getInstance().apply { 
                    add(Calendar.DAY_OF_YEAR, -Random.nextInt(1, 7)) 
                }.time,
                threatsFound = Random.nextInt(0, 5)
            )
        } else null
        
        return SecurityStatus(
            overallScore = score,
            antivirusEnabled = antivirusEnabled,
            firewallEnabled = firewallEnabled,
            vpnEnabled = vpnEnabled,
            lastScanInfo = lastScanInfo
        )
    }
}
```

**Key components:**

1. **LiveData**: Uses LiveData to expose data to the UI
2. **Mock Data Generation**: Creates realistic mock data for demonstration
3. **Feature Toggles**: Implements logic for toggling security features
4. **Score Calculation**: Calculates the overall security score based on enabled features

#### Data Models

```kotlin
data class SecurityStatus(
    val overallScore: Int,
    val antivirusEnabled: Boolean,
    val firewallEnabled: Boolean,
    val vpnEnabled: Boolean,
    val lastScanInfo: LastScanInfo?
)

data class LastScanInfo(
    val date: Date,
    val threatsFound: Int
) {
    val formattedDate: String
        get() {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            return dateFormat.format(date)
        }
}
```

These data classes represent the security status and last scan information.

### 5.2 Scan Feature

The Scan feature allows users to scan their device for threats.

#### ScanFragment

```kotlin
@AndroidEntryPoint
class ScanFragment : BaseFragment<FragmentScanBinding>(
    FragmentScanBinding::inflate
) {
    private val viewModel: ScanViewModel by viewModels()
    private lateinit var threatAdapter: ThreatAdapter
    
    override fun setupUI() {
        threatAdapter = ThreatAdapter { threat ->
            // Handle threat item click
            showThreatDetails(threat)
        }
        
        binding.recyclerThreats.apply {
            adapter = threatAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
        
        binding.btnStartScan.setOnClickListener {
            viewModel.startScan()
        }
        
        binding.btnCancelScan.setOnClickListener {
            viewModel.cancelScan()
        }
        
        binding.btnQuarantineAll.setOnClickListener {
            viewModel.quarantineAllThreats()
        }
        
        binding.btnIgnoreAll.setOnClickListener {
            viewModel.ignoreAllThreats()
        }
    }
    
    override fun observeViewModel() {
        viewModel.scanState.observe(viewLifecycleOwner) { state ->
            updateScanUI(state)
        }
        
        viewModel.currentFile.observe(viewLifecycleOwner) { file ->
            binding.textCurrentFile.text = file
        }
        
        viewModel.progress.observe(viewLifecycleOwner) { progress ->
            binding.progressScan.progress = progress
            binding.textProgress.text = "$progress%"
        }
        
        viewModel.scanResult.observe(viewLifecycleOwner) { result ->
            updateResultUI(result)
        }
        
        viewModel.threats.observe(viewLifecycleOwner) { threats ->
            threatAdapter.submitList(threats)
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                showError(errorMessage)
            }
        }
    }
    
    private fun updateScanUI(state: ScanState) {
        when (state) {
            ScanState.IDLE -> {
                binding.layoutScanIdle.visibility = View.VISIBLE
                binding.layoutScanProgress.visibility = View.GONE
                binding.layoutScanResult.visibility = View.GONE
            }
            ScanState.SCANNING -> {
                binding.layoutScanIdle.visibility = View.GONE
                binding.layoutScanProgress.visibility = View.VISIBLE
                binding.layoutScanResult.visibility = View.GONE
            }
            ScanState.COMPLETED -> {
                binding.layoutScanIdle.visibility = View.GONE
                binding.layoutScanProgress.visibility = View.GONE
                binding.layoutScanResult.visibility = View.VISIBLE
            }
        }
    }
    
    private fun updateResultUI(result: ScanResult?) {
        if (result == null) return
        
        binding.textScanDate.text = result.formattedDate
        binding.textScannedFiles.text = getString(R.string.files_scanned, result.filesScanned)
        binding.textScanDuration.text = getString(R.string.scan_duration, result.durationSeconds)
        
        val threatsCount = result.threatsFound
        binding.textThreatsFound.text = getString(R.string.threats_found, threatsCount)
        
        // Show/hide action buttons based on threats found
        val actionsVisibility = if (threatsCount > 0) View.VISIBLE else View.GONE
        binding.btnQuarantineAll.visibility = actionsVisibility
        binding.btnIgnoreAll.visibility = actionsVisibility
        binding.recyclerThreats.visibility = actionsVisibility
    }
    
    private fun showThreatDetails(threat: Threat) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.threat_details))
            .setMessage(
                """
                Name: ${threat.name}
                Path: ${threat.path}
                Severity: ${threat.severity}
                Type: ${threat.type}
                """.trimIndent()
            )
            .setPositiveButton(R.string.quarantine) { _, _ ->
                viewModel.quarantineThreat(threat)
            }
            .setNegativeButton(R.string.ignore) { _, _ ->
                viewModel.ignoreThreat(threat)
            }
            .create()
        
        dialog.show()
    }
}
```

**Key components:**

1. **RecyclerView**: Uses RecyclerView with an adapter to display the list of threats
2. **State Management**: Updates the UI based on the scan state (idle, scanning, completed)
3. **Progress Tracking**: Shows the current file being scanned and the overall progress
4. **Result Display**: Shows the scan results, including the number of threats found
5. **Threat Details**: Displays detailed information about a threat when clicked

#### ScanViewModel

```kotlin
@HiltViewModel
class ScanViewModel @Inject constructor() : BaseViewModel() {
    
    private val _scanState = MutableLiveData<ScanState>(ScanState.IDLE)
    val scanState: LiveData<ScanState> = _scanState
    
    private val _currentFile = MutableLiveData<String>()
    val currentFile: LiveData<String> = _currentFile
    
    private val _progress = MutableLiveData<Int>(0)
    val progress: LiveData<Int> = _progress
    
    private val _scanResult = MutableLiveData<ScanResult>()
    val scanResult: LiveData<ScanResult> = _scanResult
    
    private val _threats = MutableLiveData<List<Threat>>(emptyList())
    val threats: LiveData<List<Threat>> = _threats
    
    private var scanJob: Job? = null
    private val random = Random
    
    fun startScan() {
        if (_scanState.value == ScanState.SCANNING) return
        
        _scanState.value = ScanState.SCANNING
        _progress.value = 0
        _threats.value = emptyList()
        
        scanJob = viewModelScope.launch {
            try {
                // Simulate scanning process
                val totalFiles = random.nextInt(1000, 5000)
                val scanDuration = random.nextInt(30, 90) // seconds
                val filesPerSecond = totalFiles / scanDuration
                
                val startTime = System.currentTimeMillis()
                var filesScanned = 0
                
                while (filesScanned < totalFiles && isActive) {
                    val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000
                    filesScanned = (elapsedSeconds * filesPerSecond).toInt().coerceAtMost(totalFiles)
                    
                    val progress = (filesScanned * 100 / totalFiles)
                    _progress.value = progress
                    
                    // Update current file being scanned
                    _currentFile.value = generateRandomFilePath()
                    
                    delay(100) // Update UI every 100ms
                }
                
                // Generate scan result
                val actualDuration = (System.currentTimeMillis() - startTime) / 1000
                val generatedThreats = generateRandomThreats()
                
                val result = ScanResult(
                    date = Date(),
                    filesScanned = totalFiles,
                    durationSeconds = actualDuration.toInt(),
                    threatsFound = generatedThreats.size
                )
                
                _scanResult.value = result
                _threats.value = generatedThreats
                _scanState.value = ScanState.COMPLETED
                
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred during scanning"
                _scanState.value = ScanState.IDLE
            }
        }
    }
    
    fun cancelScan() {
        scanJob?.cancel()
        _scanState.value = ScanState.IDLE
    }
    
    fun quarantineAllThreats() {
        val currentThreats = _threats.value ?: return
        // In a real app, we would quarantine the threats in the repository
        _threats.value = emptyList()
    }
    
    fun ignoreAllThreats() {
        // In a real app, we would mark the threats as ignored in the repository
        _threats.value = emptyList()
    }
    
    fun quarantineThreat(threat: Threat) {
        val currentThreats = _threats.value?.toMutableList() ?: return
        currentThreats.remove(threat)
        _threats.value = currentThreats
        // In a real app, we would quarantine the threat in the repository
    }
    
    fun ignoreThreat(threat: Threat) {
        val currentThreats = _threats.value?.toMutableList() ?: return
        currentThreats.remove(threat)
        _threats.value = currentThreats
        // In a real app, we would mark the threat as ignored in the repository
    }
    
    private fun generateRandomFilePath(): String {
        val directories = listOf(
            "Download", "Pictures", "DCIM", "Documents", "Music", 
            "Android/data", "WhatsApp/Media"
        )
        
        val fileTypes = listOf(
            "jpg", "png", "pdf", "doc", "apk", "mp3", "mp4", "txt", "zip"
        )
        
        val directory = directories[random.nextInt(directories.size)]
        val fileName = "file_${random.nextInt(1000)}.${fileTypes[random.nextInt(fileTypes.size)]}"
        
        return "/storage/emulated/0/$directory/$fileName"
    }
    
    private fun generateRandomThreats(): List<Threat> {
        val threatCount = random.nextInt(6) // 0-5 threats
        if (threatCount == 0) return emptyList()
        
        val threatNames = listOf(
            "Trojan.AndroidOS.Agent", "Adware.AndroidOS.Ewind", 
            "Trojan.AndroidOS.Boogr", "Backdoor.AndroidOS.Obad",
            "Trojan-Banker.AndroidOS.Asacub", "Trojan-SMS.AndroidOS.FakeInst",
            "Trojan-Dropper.AndroidOS.Hqwar", "RiskTool.AndroidOS.SMSreg"
        )
        
        val threatTypes = listOf(
            "Trojan", "Adware", "Spyware", "Backdoor", "Banker", "SMS Fraud", "Dropper"
        )
        
        return List(threatCount) {
            val name = threatNames[random.nextInt(threatNames.size)]
            val path = generateRandomFilePath()
            val severity = ThreatSeverity.values()[random.nextInt(ThreatSeverity.values().size)]
            val type = threatTypes[random.nextInt(threatTypes.size)]
            
            Threat(
                id = it.toLong(),
                name = name,
                path = path,
                severity = severity,
                type = type,
                detectionDate = Date()
            )
        }
    }
}
```

**Key components:**

1. **State Management**: Uses LiveData to track the scan state, progress, and results
2. **Coroutines**: Uses coroutines for asynchronous operations
3. **Simulation**: Simulates a realistic scanning process with random file paths and threats
4. **Threat Generation**: Creates realistic mock threats with various properties
5. **User Actions**: Implements functions for quarantining or ignoring threats

#### Data Models

```kotlin
enum class ScanState {
    IDLE, SCANNING, COMPLETED
}

enum class ThreatSeverity {
    LOW, MEDIUM, HIGH, CRITICAL
}

data class ScanResult(
    val id: Long = 0,
    val date: Date,
    val filesScanned: Int,
    val durationSeconds: Int,
    val threatsFound: Int
) {
    val formattedDate: String
        get() {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            return dateFormat.format(date)
        }
}

data class Threat(
    val id: Long = 0,
    val name: String,
    val path: String,
    val severity: ThreatSeverity,
    val type: String,
    val detectionDate: Date,
    val isQuarantined: Boolean = false
)

data class ScanStats(
    val totalScans: Int,
    val totalThreatsFound: Int,
    val lastScanDate: Date?
)
```

These data classes and enums represent the scan state, results, and threats.

### 5.3 Protection Feature

The Protection feature provides real-time protection against threats.

#### ProtectionFragment

```kotlin
@AndroidEntryPoint
class ProtectionFragment : BaseFragment<FragmentProtectionBinding>(
    FragmentProtectionBinding::inflate
) {
    private val viewModel: ProtectionViewModel by viewModels()
    private lateinit var logAdapter: ProtectionLogAdapter
    
    override fun setupUI() {
        logAdapter = ProtectionLogAdapter()
        
        binding.recyclerProtectionLogs.apply {
            adapter = logAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
        
        binding.switchProtection.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setProtectionEnabled(isChecked)
        }
        
        // Set up animation
        binding.animationView.setAnimation(R.raw.shield_animation)
    }
    
    override fun observeViewModel() {
        viewModel.protectionEnabled.observe(viewLifecycleOwner) { enabled ->
            updateProtectionUI(enabled)
        }
        
        viewModel.protectionStats.observe(viewLifecycleOwner) { stats ->
            updateStatsUI(stats)
        }
        
        viewModel.protectionLogs.observe(viewLifecycleOwner) { logs ->
            logAdapter.submitList(logs)
            binding.emptyView.visibility = if (logs.isEmpty()) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                showError(errorMessage)
            }
        }
    }
    
    private fun updateProtectionUI(enabled: Boolean) {
        binding.switchProtection.isChecked = enabled
        
        if (enabled) {
            binding.animationView.playAnimation()
            binding.textProtectionStatus.text = getString(R.string.protection_active)
            binding.textProtectionStatus.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.protection_active)
            )
        } else {
            binding.animationView.pauseAnimation()
            binding.textProtectionStatus.text = getString(R.string.protection_inactive)
            binding.textProtectionStatus.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.protection_inactive)
            )
        }
    }
    
    private fun updateStatsUI(stats: ProtectionStats) {
        binding.textTotalBlocked.text = stats.totalBlockedThreats.toString()
        binding.textHighRiskBlocked.text = stats.highRiskThreatsBlocked.toString()
        binding.textLastUpdated.text = stats.formattedLastUpdateTime
    }
}
```

**Key components:**

1. **Switch Toggle**: Allows users to enable/disable protection
2. **Animation**: Uses Lottie animation for the shield
3. **RecyclerView**: Displays the protection logs
4. **UI Updates**: Updates the UI based on the protection state and stats

#### ProtectionViewModel

```kotlin
@HiltViewModel
class ProtectionViewModel @Inject constructor() : BaseViewModel() {
    
    private val _protectionEnabled = MutableLiveData<Boolean>(false)
    val protectionEnabled: LiveData<Boolean> = _protectionEnabled
    
    private val _protectionStats = MutableLiveData<ProtectionStats>()
    val protectionStats: LiveData<ProtectionStats> = _protectionStats
    
    private val _protectionLogs = MutableLiveData<List<ProtectionLog>>(emptyList())
    val protectionLogs: LiveData<List<ProtectionLog>> = _protectionLogs
    
    private val random = Random
    
    init {
        // Initialize with mock data
        generateMockStats()
        generateMockLogs()
    }
    
    fun setProtectionEnabled(enabled: Boolean) {
        _protectionEnabled.value = enabled
        
        if (enabled) {
            // In a real app, we would start the protection service
            // For now, we'll just update the UI
            _protectionStats.value = _protectionStats.value?.copy(
                lastUpdateTime = Date()
            )
        }
    }
    
    private fun generateMockStats() {
        val totalBlocked = random.nextInt(100, 500)
        val highRiskBlocked = random.nextInt(10, totalBlocked / 3)
        
        _protectionStats.value = ProtectionStats(
            totalBlockedThreats = totalBlocked,
            highRiskThreatsBlocked = highRiskBlocked,
            lastUpdateTime = Date()
        )
    }
    
    private fun generateMockLogs() {
        val logCount = random.nextInt(5, 15)
        val threatTypes = listOf(
            "Malicious URL blocked", "Suspicious app installation prevented",
            "Phishing attempt blocked", "Malware download blocked",
            "Suspicious network connection blocked", "Potentially harmful app detected"
        )
        
        val logs = mutableListOf<ProtectionLog>()
        
        // Generate logs with dates spread over the last 7 days
        val calendar = Calendar.getInstance()
        
        for (i in 0 until logCount) {
            val daysAgo = random.nextInt(0, 7)
            val hoursAgo = random.nextInt(0, 24)
            val minutesAgo = random.nextInt(0, 60)
            
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
            calendar.add(Calendar.HOUR_OF_DAY, -hoursAgo)
            calendar.add(Calendar.MINUTE, -minutesAgo)
            
            val threatType = threatTypes[random.nextInt(threatTypes.size)]
            val severity = ThreatSeverity.values()[random.nextInt(ThreatSeverity.values().size)]
            
            logs.add(
                ProtectionLog(
                    id = i.toLong(),
                    timestamp = calendar.time,
                    description = threatType,
                    severity = severity,
                    actionTaken = "Blocked"
                )
            )
        }
        
        // Sort logs by timestamp (newest first)
        logs.sortByDescending { it.timestamp }
        
        _protectionLogs.value = logs
    }
}
```

**Key components:**

1. **Protection State**: Tracks whether protection is enabled
2. **Statistics**: Maintains protection statistics like total threats blocked
3. **Logs**: Keeps a record of protection events
4. **Mock Data Generation**: Creates realistic mock data for demonstration

#### Data Models

```kotlin
enum class ProtectionLevel {
    STANDARD, ENHANCED, MAXIMUM
}

data class ProtectionStats(
    val totalBlockedThreats: Int,
    val highRiskThreatsBlocked: Int,
    val lastUpdateTime: Date
) {
    val formattedLastUpdateTime: String
        get() {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            return dateFormat.format(lastUpdateTime)
        }
}

data class ProtectionLog(
    val id: Long,
    val timestamp: Date,
    val description: String,
    val severity: ThreatSeverity,
    val actionTaken: String
) {
    val formattedTimestamp: String
        get() {
            val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
            return dateFormat.format(timestamp)
        }
}
```

These data classes and enums represent the protection level, statistics, and logs.

### 5.4 Settings Feature

The Settings feature allows users to configure the app.

#### SettingsFragment

```kotlin
@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    
    private val viewModel: SettingsViewModel by viewModels()
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        
        setupPreferences()
    }
    
    private fun setupPreferences() {
        // Theme preference
        val themePreference = findPreference<ListPreference>("theme_preference")
        themePreference?.setOnPreferenceChangeListener { _, newValue ->
            when (newValue as String) {
                "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            true
        }
        
        // Notification preference
        val notificationPreference = findPreference<SwitchPreferenceCompat>("notifications_preference")
        notificationPreference?.setOnPreferenceChangeListener { _, newValue ->
            viewModel.setNotificationsEnabled(newValue as Boolean)
            true
        }
        
        // Auto-scan preference
        val autoScanPreference = findPreference<SwitchPreferenceCompat>("auto_scan_preference")
        autoScanPreference?.setOnPreferenceChangeListener { _, newValue ->
            viewModel.setAutoScanEnabled(newValue as Boolean)
            true
        }
        
        // Clear data preference
        val clearDataPreference = findPreference<Preference>("clear_data_preference")
        clearDataPreference?.setOnPreferenceClickListener {
            showClearDataDialog()
            true
        }
        
        // App version preference
        val versionPreference = findPreference<Preference>("app_version_preference")
        versionPreference?.summary = BuildConfig.VERSION_NAME
    }
    
    private fun showClearDataDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.clear_data)
            .setMessage(R.string.clear_data_confirmation)
            .setPositiveButton(R.string.clear) { _, _ ->
                viewModel.clearAppData()
                Toast.makeText(
                    requireContext(),
                    R.string.data_cleared,
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
```

**Key components:**

1. **PreferenceFragmentCompat**: Uses Android's preference framework for settings
2. **Preference Listeners**: Sets up listeners for preference changes
3. **Theme Switching**: Implements theme switching functionality
4. **Data Clearing**: Provides an option to clear app data

#### SettingsViewModel

```kotlin
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : BaseViewModel() {
    
    fun setNotificationsEnabled(enabled: Boolean) {
        launchDataLoad {
            // In a real app, we would update this in the repository
            // and configure the notification channels
        }
    }
    
    fun setAutoScanEnabled(enabled: Boolean) {
        launchDataLoad {
            // In a real app, we would update this in the repository
            // and schedule or cancel the auto-scan job
        }
    }
    
    fun clearAppData() {
        launchDataLoad {
            // In a real app, we would clear the database, preferences, etc.
        }
    }
}
```

**Key components:**

1. **Settings Management**: Provides functions for updating settings
2. **Data Clearing**: Implements functionality to clear app data

#### Preferences XML

```xml
<?xml version="1.0" encoding="utf-8"?>
<PreferenceScreen xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <PreferenceCategory android:title="@string/general_settings">
        <ListPreference
            android:defaultValue="system"
            android:entries="@array/theme_entries"
            android:entryValues="@array/theme_values"
            android:key="theme_preference"
            android:title="@string/theme"
            android:summary="@string/theme_summary"
            app:useSimpleSummaryProvider="true" />

        <SwitchPreferenceCompat
            android:defaultValue="true"
            android:key="notifications_preference"
            android:summary="@string/notifications_summary"
            android:title="@string/notifications" />
    </PreferenceCategory>

    <PreferenceCategory android:title="@string/scan_settings">
        <SwitchPreferenceCompat
            android:defaultValue="true"
            android:key="auto_scan_preference"
            android:summary="@string/auto_scan_summary"
            android:title="@string/auto_scan" />

        <SwitchPreferenceCompat
            android:defaultValue="true"
            android:key="scan_apps_preference"
            android:summary="@string/scan_apps_summary"
            android:title="@string/scan_apps" />

        <SwitchPreferenceCompat
            android:defaultValue="true"
            android:key="scan_downloads_preference"
            android:summary="@string/scan_downloads_summary"
            android:title="@string/scan_downloads" />
    </PreferenceCategory>

    <PreferenceCategory android:title="@string/protection_settings">
        <SwitchPreferenceCompat
            android:defaultValue="true"
            android:key="real_time_protection_preference"
            android:summary="@string/real_time_protection_summary"
            android:title="@string/real_time_protection" />

        <SwitchPreferenceCompat
            android:defaultValue="true"
            android:key="web_protection_preference"
            android:summary="@string/web_protection_summary"
            android:title="@string/web_protection" />
    </PreferenceCategory>

    <PreferenceCategory android:title="@string/data_management">
        <Preference
            android:key="clear_data_preference"
            android:summary="@string/clear_data_summary"
            android:title="@string/clear_data" />
    </PreferenceCategory>

    <PreferenceCategory android:title="@string/about">
        <Preference
            android:key="app_version_preference"
            android:title="@string/app_version" />

        <Preference
            android:key="disclaimer_preference"
            android:summary="@string/disclaimer_text"
            android:title="@string/disclaimer" />
    </PreferenceCategory>
</PreferenceScreen>
```

This XML file defines the structure and content of the settings screen.

## 6. Resource Management

### 6.1 String Resources

String resources are defined in `strings.xml` to support localization and maintain consistency:

```xml
<resources>
    <string name="app_name">FakeCo Security</string>
    
    <!-- Navigation -->
    <string name="dashboard">Dashboard</string>
    <string name="scan">Scan</string>
    <string name="protection">Protection</string>
    <string name="settings">Settings</string>
    
    <!-- Dashboard -->
    <string name="security_score">Security Score</string>
    <string name="antivirus">Antivirus</string>
    <string name="firewall">Firewall</string>
    <string name="vpn">VPN</string>
    <string name="quick_scan">Quick Scan</string>
    <string name="last_scan">Last Scan</string>
    <string name="never_scanned">Never Scanned</string>
    <string name="scan_now">Scan Now</string>
    <string name="threats_found">%d threats found</string>
    <string name="no_threats_found">No threats found</string>
    
    <!-- Scan -->
    <string name="start_scan">Start Scan</string>
    <string name="cancel_scan">Cancel</string>
    <string name="scanning">Scanning…</string>
    <string name="scan_complete">Scan Complete</string>
    <string name="files_scanned">Files Scanned: %d</string>
    <string name="scan_duration">Duration: %d seconds</string>
    <string name="quarantine_all">Quarantine All</string>
    <string name="ignore_all">Ignore All</string>
    <string name="threat_details">Threat Details</string>
    <string name="quarantine">Quarantine</string>
    <string name="ignore">Ignore</string>
    
    <!-- Protection -->
    <string name="protection_active">Protection Active</string>
    <string name="protection_inactive">Protection Inactive</string>
    <string name="total_blocked">Total Blocked</string>
    <string name="high_risk_blocked">High Risk Blocked</string>
    <string name="last_updated">Last Updated</string>
    <string name="protection_logs">Protection Logs</string>
    <string name="no_logs">No protection logs yet</string>
    
    <!-- Settings -->
    <string name="general_settings">General Settings</string>
    <string name="theme">Theme</string>
    <string name="theme_summary">Choose app theme</string>
    <string name="notifications">Notifications</string>
    <string name="notifications_summary">Enable threat notifications</string>
    <string name="scan_settings">Scan Settings</string>
    <string name="auto_scan">Auto Scan</string>
    <string name="auto_scan_summary">Automatically scan new apps</string>
    <string name="scan_apps">Scan Apps</string>
    <string name="scan_apps_summary">Include installed apps in scans</string>
    <string name="scan_downloads">Scan Downloads</string>
    <string name="scan_downloads_summary">Scan files when downloaded</string>
    <string name="protection_settings">Protection Settings</string>
    <string name="real_time_protection">Real-time Protection</string>
    <string name="real_time_protection_summary">Monitor system for threats</string>
    <string name="web_protection">Web Protection</string>
    <string name="web_protection_summary">Block malicious websites</string>
    <string name="data_management">Data Management</string>
    <string name="clear_data">Clear Data</string>
    <string name="clear_data_summary">Clear scan history and settings</string>
    <string name="clear_data_confirmation">This will clear all scan history and reset settings. Continue?</string>
    <string name="clear">Clear</string>
    <string name="cancel">Cancel</string>
    <string name="data_cleared">Data cleared successfully</string>
    <string name="about">About</string>
    <string name="app_version">App Version</string>
    <string name="disclaimer">Disclaimer</string>
    <string name="disclaimer_text">This is a demonstration app with simulated functionality for educational purposes only.</string>
    
    <!-- Theme options -->
    <string-array name="theme_entries">
        <item>Light</item>
        <item>Dark</item>
        <item>System Default</item>
    </string-array>
    <string-array name="theme_values">
        <item>light</item>
        <item>dark</item>
        <item>system</item>
    </string-array>
</resources>
```

### 6.2 Color Resources

Color resources are defined in `colors.xml` to maintain a consistent color scheme:

```xml
<resources>
    <color name="primary">#1976D2</color>
    <color name="primary_dark">#1565C0</color>
    <color name="primary_light">#BBDEFB</color>
    <color name="accent">#FF4081</color>
    <color name="primary_text">#212121</color>
    <color name="secondary_text">#757575</color>
    <color name="icons">#FFFFFF</color>
    <color name="divider">#BDBDBD</color>
    
    <color name="security_good">#4CAF50</color>
    <color name="security_warning">#FFC107</color>
    <color name="security_danger">#F44336</color>
    
    <color name="protection_active">#4CAF50</color>
    <color name="protection_inactive">#F44336</color>
    
    <color name="threat_critical">#D50000</color>
    <color name="threat_high">#F44336</color>
    <color name="threat_medium">#FFC107</color>
    <color name="threat_low">#4CAF50</color>
    
    <color name="background_light">#FAFAFA</color>
    <color name="background_dark">#121212</color>
    <color name="card_background_light">#FFFFFF</color>
    <color name="card_background_dark">#1E1E1E</color>
</resources>
```

### 6.3 Theme Resources

Theme resources are defined in `themes.xml` to style the app:

```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme -->
    <style name="Theme.FakeCo" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
        <!-- Primary brand color -->
        <item name="colorPrimary">@color/primary</item>
        <item name="colorPrimaryVariant">@color/primary_dark</item>
        <item name="colorOnPrimary">@color/icons</item>
        <!-- Secondary brand color -->
        <item name="colorSecondary">@color/accent</item>
        <item name="colorSecondaryVariant">@color/accent</item>
        <item name="colorOnSecondary">@color/icons</item>
        <!-- Status bar color -->
        <item name="android:statusBarColor">?attr/colorPrimaryVariant</item>
        <!-- Text colors -->
        <item name="android:textColorPrimary">@color/primary_text</item>
        <item name="android:textColorSecondary">@color/secondary_text</item>
    </style>
    
    <!-- Card styles -->
    <style name="CardView.FakeCo" parent="Widget.MaterialComponents.CardView">
        <item name="cardElevation">4dp</item>
        <item name="cardCornerRadius">8dp</item>
        <item name="android:layout_margin">8dp</item>
    </style>
    
    <!-- Button styles -->
    <style name="Button.FakeCo" parent="Widget.MaterialComponents.Button">
        <item name="android:textAllCaps">false</item>
        <item name="android:paddingStart">16dp</item>
        <item name="android:paddingEnd">16dp</item>
    </style>
    
    <style name="Button.FakeCo.Outlined" parent="Widget.MaterialComponents.Button.OutlinedButton">
        <item name="android:textAllCaps">false</item>
        <item name="android:paddingStart">16dp</item>
        <item name="android:paddingEnd">16dp</item>
    </style>
</resources>
```

### 6.4 Custom Drawable Resources

We created custom drawable resources for various UI elements:

#### Circular Progress Bar (circular_progress.xml)

```xml
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
        <shape android:shape="ring"
            android:thicknessRatio="16"
            android:useLevel="false">
            <solid android:color="@color/primary_light" />
        </shape>
    </item>
    <item>
        <shape android:shape="ring"
            android:thicknessRatio="16"
            android:useLevel="true">
            <solid android:color="@color/primary" />
        </shape>
    </item>
</layer-list>
```

This creates a circular progress indicator with a background track and a foreground progress indicator.

#### Status Indicator (status_indicator.xml)

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">
    <size android:width="12dp" android:height="12dp" />
    <solid android:color="@color/security_good" />
</shape>
```

This creates a simple circular indicator that can be tinted to show different statuses.

## 7. Testing and Quality Assurance

### 7.1 Unit Testing

Unit tests verify that individual components work as expected in isolation. Here's an example of a ViewModel test:

```kotlin
@RunWith(AndroidJUnit4::class)
class ScanViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()
    
    private lateinit var viewModel: ScanViewModel
    
    @Before
    fun setup() {
        viewModel = ScanViewModel()
    }
    
    @Test
    fun `initial state is IDLE`() {
        val value = viewModel.scanState.getOrAwaitValue()
        assertEquals(ScanState.IDLE, value)
    }
    
    @Test
    fun `startScan changes state to SCANNING`() {
        viewModel.startScan()
        val value = viewModel.scanState.getOrAwaitValue()
        assertEquals(ScanState.SCANNING, value)
    }
    
    @Test
    fun `cancelScan changes state to IDLE`() {
        viewModel.startScan()
        viewModel.cancelScan()
        val value = viewModel.scanState.getOrAwaitValue()
        assertEquals(ScanState.IDLE, value)
    }
}
```

### 7.2 Integration Testing

Integration tests verify that components work together correctly. Here's an example of a repository integration test:

```kotlin
@RunWith(AndroidJUnit4::class)
class ScanRepositoryTest {
    
    private lateinit var database: AppDatabase
    private lateinit var scanDao: ScanDao
    private lateinit var threatDao: ThreatDao
    private lateinit var repository: ScanRepository
    
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        scanDao = database.scanDao()
        threatDao = database.threatDao()
        repository = ScanRepository(scanDao, threatDao)
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun saveScanResultAndRetrieve() = runBlockingTest {
        // Create a scan result
        val scanResult = ScanResult(
            date = Date(),
            filesScanned = 1000,
            durationSeconds = 30,
            threatsFound = 2
        )
        
        // Save it
        val id = repository.saveScanResult(scanResult)
        
        // Retrieve recent scans
        val recentScans = repository.getRecentScans(1).first()
        
        // Verify
        assertEquals(1, recentScans.size)
        assertEquals(id, recentScans[0].id)
        assertEquals(scanResult.filesScanned, recentScans[0].filesScanned)
    }
}
```

### 7.3 UI Testing

UI tests verify that the user interface works correctly. Here's an example of an Espresso test:

```kotlin
@RunWith(AndroidJUnit4::class)
class ScanFragmentTest {
    
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    
    @Before
    fun navigateToScanFragment() {
        // Navigate to the scan fragment
        onView(withId(R.id.scanFragment)).perform(click())
    }
    
    @Test
    fun testScanButtonVisible() {
        onView(withId(R.id.btnStartScan))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.start_scan)))
    }
    
    @Test
    fun testStartScanShowsProgress() {
        // Click the scan button
        onView(withId(R.id.btnStartScan)).perform(click())
        
        // Verify that the progress layout is visible
        onView(withId(R.id.layoutScanProgress))
            .check(matches(isDisplayed()))
        
        // Verify that the cancel button is visible
        onView(withId(R.id.btnCancelScan))
            .check(matches(isDisplayed()))
    }
}
```

## 8. Lessons Learned and Best Practices

### 8.1 Architecture Decisions

1. **MVVM Architecture**: We chose MVVM because it provides a clear separation of concerns, making the code more maintainable and testable. The ViewModel acts as a mediator between the View and the Model, handling UI-related logic and surviving configuration changes.

2. **Repository Pattern**: We implemented the Repository pattern to abstract the data sources from the rest of the application. This makes it easier to change the data source (e.g., from local database to network) without affecting the rest of the app.

3. **Dependency Injection with Hilt**: We used Hilt for dependency injection because it simplifies the boilerplate code required for DI and is well-integrated with Android components.

### 8.2 UI/UX Considerations

1. **Material Design**: We followed Material Design guidelines to create a modern, intuitive user interface. This includes using Material components, proper spacing, and consistent typography.

2. **Responsive Layouts**: We designed layouts that adapt to different screen sizes and orientations, ensuring a good user experience on all devices.

3. **Accessibility**: We added content descriptions to images and used appropriate contrast ratios to make the app accessible to all users.

4. **Animations**: We used animations sparingly to enhance the user experience without being distracting. For example, the shield animation in the Protection screen provides visual feedback about the protection status.

### 8.3 Performance Optimization

1. **Lazy Loading**: We implemented lazy loading for RecyclerViews to improve performance when displaying large lists.

2. **ViewBinding**: We used ViewBinding instead of findViewById to improve type safety and performance.

3. **Coroutines**: We used Coroutines for asynchronous operations, which are more lightweight than traditional threading approaches.

4. **Resource Management**: We optimized resource usage by properly managing lifecycles and cleaning up resources when they're no longer needed.

### 8.4 Testing Strategy

1. **Unit Tests**: We wrote unit tests for individual components, particularly ViewModels and Repositories, to verify that they work correctly in isolation.

2. **Integration Tests**: We wrote integration tests to verify that components work together correctly, such as the interaction between Repositories and DAOs.

3. **UI Tests**: We wrote UI tests using Espresso to verify that the user interface works correctly from the user's perspective.

4. **Test Doubles**: We used mock objects and fake implementations to isolate the component being tested and make tests more reliable.

### 8.5 Code Quality

1. **Consistent Naming**: We used consistent naming conventions throughout the codebase to make it more readable and maintainable.

2. **Documentation**: We added comments and documentation to explain complex logic and the purpose of classes and functions.

3. **Code Organization**: We organized the code into packages based on features and responsibilities, making it easier to navigate and understand.

4. **Error Handling**: We implemented proper error handling throughout the app, providing meaningful error messages to users and logging detailed information for debugging.

### 8.6 Future Improvements

1. **Real Functionality**: Replace mock implementations with real functionality for scanning, protection, and VPN.

2. **Offline Support**: Improve offline support by implementing proper caching strategies.

3. **Analytics**: Add analytics to track user behavior and app performance.

4. **Localization**: Add support for multiple languages to make the app accessible to a wider audience.

5. **Deeper Integration**: Integrate with other security features of the Android platform, such as the SafetyNet API.

## Conclusion

Building the FakeCo Anti-Virus Android application involved applying modern Android development practices, including MVVM architecture, dependency injection, and comprehensive testing. By following these practices, we created a maintainable, testable, and user-friendly application that demonstrates the key features of an anti-virus app.

This guide has covered the entire development process, from project setup to feature implementation and testing. By understanding the decisions and approaches taken in this project, you'll be better equipped to build your own Android applications using best practices.