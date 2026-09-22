package com.campuseats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.campuseats.navigation.CampusEatsNavHost
import com.campuseats.ui.admin.AdminViewModel
import com.campuseats.ui.auth.AuthViewModel
import com.campuseats.ui.demo.RemoteDemoViewModel
import com.campuseats.ui.demo.XmlDemoViewModel
import com.campuseats.ui.student.StudentViewModel
import com.campuseats.ui.theme.CampusEatsTheme
import com.campuseats.ui.vendor.VendorViewModel

/**
 * Main Activity serving as the native single-activity container for Jetpack Compose.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as CampusEatsApp

        // Instantiate ViewModels using the repositories initialized in CampusEatsApp
        val authViewModel = AuthViewModel(app.authRepository)
        val studentViewModel = StudentViewModel(app.studentRepository, app.sessionManager)
        val vendorViewModel = VendorViewModel(app.vendorRepository, app.sessionManager)
        val adminViewModel = AdminViewModel(app.adminRepository, app.sessionManager)
        val remoteDemoViewModel = RemoteDemoViewModel(app.remoteDemoRepository, app.database.vendorDao())
        val xmlDemoViewModel = XmlDemoViewModel(app.xmlDemoRepository)

        setContent {
            CampusEatsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    CampusEatsNavHost(
                        navController = navController,
                        authViewModel = authViewModel,
                        studentViewModel = studentViewModel,
                        vendorViewModel = vendorViewModel,
                        adminViewModel = adminViewModel,
                        remoteDemoViewModel = remoteDemoViewModel,
                        xmlDemoViewModel = xmlDemoViewModel
                    )
                }
            }
        }
    }
}
