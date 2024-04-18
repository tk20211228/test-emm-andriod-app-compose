package isb.mdm.myemm.myemmtestcompose

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import isb.mdm.myemm.myemmtestcompose.ui.theme.MyemmtestcomposeTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val navController = rememberNavController()
        setContent {
            MyApp(navController,devicePolicyManager)
        }
    }
}

@Composable
fun MyApp(navController: NavHostController,devicePolicyManager: DevicePolicyManager) {
    MyemmtestcomposeTheme {
        // Surface色セットと共にScaffoldを使い、トップバーを表示します
        Surface(color = MaterialTheme.colorScheme.background) {
            HomeScreen(navController, paddingValues = PaddingValues())
        }
    }
}
@Composable
fun HomeScreen(navController: NavHostController,  paddingValues: PaddingValues) {
    LazyColumn(contentPadding = paddingValues) {
        items(DelegatedScope.allScopes) { scope ->
            DelegatedScopeItem(
                scopeName = scope.scopeName,
                scopeDescription = scope.description,
                icon = scope.icon,
                onClick = {
                    // cert_install_screen に遷移しますが、他の画面も同様に処理できます。
                    val screenRoute = when (scope) {
                        DelegatedScope.CERT_INSTALL -> navController.navigate("cert_install_screen")
                        DelegatedScope.MANAGED_CONFIGURATIONS -> navController.navigate("cert_install_screen")
                        // 他の DelegatedScope に対応する画面のルートをここで指定します…
                        else -> "${scope.scopeName.lowercase()}_screen"
                    }

                }
            )
        }
    }
}
@Composable
fun NavigationHost(navController: NavHostController, devicePolicyManager: DevicePolicyManager) {

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController, PaddingValues())
        }
        composable("cert_install_screen") {
            CertInstallScreen(devicePolicyManager, PaddingValues())
        }
        // 他のルート...
    }
}


@Composable
fun DelegatedScopeItem(
    scopeName: String,
    scopeDescription: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        leadingContent = { Icon(imageVector = icon, contentDescription = "$scopeName icon") },
        headlineContent = { Text(text = scopeName) },
        supportingContent = { Text(text = scopeDescription) },
        modifier = Modifier.clickable { onClick() }
    )
}

enum class DelegatedScope(val scopeName: String, val description: String, val icon: ImageVector) {
    CERT_INSTALL(
        "CERT_INSTALL",
        "CA証明書のインストール確認",
        Icons.Outlined.Lock
    ),
    MANAGED_CONFIGURATIONS(
        "MANAGED_CONFIGURATIONS",
        "アプリ管理構成の確認",
        Icons.Default.Settings
    ),
    BLOCK_UNINSTALL(
        "BLOCK_UNINSTALL",
        "アンインストールをブロックするためのアクセス確認",
        Icons.Outlined.Star // ここでは例としてStarアイコンを使っていますが、適切なアイコンに置き換えてください
    ),
    CERT_INSTALL1(
        "CERT_INSTALL",
        "CA証明書のインストール確認",
        Icons.Default.Settings
    ),

    // 他のDelegated Scopesについても同じ形式で追加します…
    ;

    companion object {
        val allScopes = enumValues<DelegatedScope>().toList()
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewHomeScreenNew() {
//    MyemmtestcomposeTheme {
//        // 空のPaddingValuesをプレビューに渡すことで、実際の動作と異ならず表示できます。
//        HomeScreen(paddingValues = PaddingValues())
//    }
//}

//@Composable
//fun NavigationHost(devicePolicyManager: DevicePolicyManager) {
//    NavHost(startDestination = "home") {
//        composable("home") {
//            HomeScreen()
//        }
//        composable("cert_install_screen") {
//            CertInstallScreen(devicePolicyManager)
//        }
//        // 他の画面のルートも同様にここに追加...
//    }
//}
//
//@Composable
//fun CertInstallScreen(devicePolicyManager: DevicePolicyManager, paddingValues: PaddingValues) {
//    Surface {
////        Column {
//        Column(modifier = Modifier.padding(paddingValues)) { // ここでpaddingValuesを適用
//            val certificates = remember { mutableStateOf<List<X509Certificate>?>(null) }
//            val context = LocalContext.current
//
//            LaunchedEffect(Unit) {
//                val certs = loadInstalledCaCerts(devicePolicyManager, context)
//                certificates.value = certs
//            }
//
//            certificates.value?.let { certList ->
//                if (certList.isEmpty()) {
//                    Text("インストールされているCA証明書はありません。")
//                } else {
//                    LazyColumn {
//                        items(certList) { certificate ->
//                            ListItem(
//                                headlineContent = { Text(certificate.subjectDN.name) },
//                                modifier = Modifier.padding(vertical = 4.dp)
//                            )
//                        }
//                    }
//                }
//            } ?: Text("CA証明書の読み込み中...")
//        }
//    }
//}
//
//private fun loadInstalledCaCerts(
//    devicePolicyManager: DevicePolicyManager,
//    context: Context
//): List<X509Certificate>? {
//    return try {
//        val caBytesList = devicePolicyManager.getInstalledCaCerts(null)
//        if (caBytesList.isEmpty()) {
//            Toast.makeText(context, "インストールされているCA証明書はありません。",
//                Toast.LENGTH_LONG).show()
//            null
//        } else {
//            caBytesList.mapNotNull { bytes ->
//                try {
//                    val certificate = CertificateFactory.getInstance("X.509")
//                        .generateCertificate(bytes.inputStream())
//                    certificate as? X509Certificate
//                } catch (e: Exception) {
//                    null
//                }
//            }
//        }
//    } catch (e: SecurityException) {
//        Toast.makeText(context, "権限がありません。",
//            Toast.LENGTH_LONG).show()
//        null
//    } catch (e: Exception) {
//        Toast.makeText(context, "想定外のエラーが発生しました。",
//            Toast.LENGTH_LONG).show()
//        null
//    }
//}
