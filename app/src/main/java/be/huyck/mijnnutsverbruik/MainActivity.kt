package be.huyck.mijnnutsverbruik

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import be.huyck.mijnnutsverbruik.databinding.ActivityMainBinding
import be.huyck.mijnnutsverbruik.viewmodel.VerbruiksViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    val TAG = "be.huyck.mijnnutsverbruik.MainActivity"

    private lateinit var auth : FirebaseAuth
    lateinit var MyViewModel : VerbruiksViewModel

    //private lateinit var binding: ActivityMainBinding

    private lateinit var googleSignInClient: GoogleSignInClient

    //lateinit var mySharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)*/

        val navView = findViewById<BottomNavigationView>(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_day, R.id.navigation_week,R.id.navigation_month, R.id.navigation_year))

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //auth = FirebaseAuth.getInstance()

        // [START config_signin]
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        // [END config_signin]


        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth
        // [END initialize_auth]



        MyViewModel = ViewModelProvider(this)[VerbruiksViewModel::class.java]
        /*mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        MyViewModel.GeefWaterWeerInGrafiek = mySharedPreferences.getBoolean("switch_preference_water",true)
        MyViewModel.GeefGasWeerInGrafiek = mySharedPreferences.getBoolean("switch_preference_gas",true)*/

        val gebruiker = auth.currentUser
        if (gebruiker != null) {
            MyViewModel.loadAllData()
            //val useruid = gebruiker.uid.toString()
            val username = gebruiker.displayName
            Snackbar.make(getWindow().getDecorView(), username + ' ' + getString(R.string.snackbar_userloggedin), Snackbar.LENGTH_LONG).show()
        }
        else{
            createSignInIntent()
        }

        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Log.d(TAG,"onCreate fun - opstart")

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            menu!!.findItem(R.id.login_settings).setVisible(false)
            menu.findItem(R.id.logout_settings).setVisible(true)
        }
        else{
            menu!!.findItem(R.id.login_settings).setVisible(true)
            menu.findItem(R.id.logout_settings).setVisible(false)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                CreateSettingsIntent()
                true
            }
            R.id.login_settings ->{
                createSignInIntent()
                true
            }
            R.id.logout_settings ->{
                signOut()
                true
            }
            R.id.refresh_settings -> {
                RefreshData()
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    fun RefreshData(){
        MyViewModel.cleardata()
        MyViewModel.loadAllData()
        Snackbar.make(getWindow().getDecorView(), getString(R.string.snackbar_refreshdata), Snackbar.LENGTH_LONG).show()
    }

    // setting activity
    fun CreateSettingsIntent()
    {
        startActivity(Intent(this@MainActivity,SettingsActivity::class.java))
        //val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        //viewModel.backupOpDitToestel = mySharedPreferences.getBoolean("backup_op_dit_toestel",true)
    }

    // firebase functies 2.0

    private fun createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
        // [END auth_fui_create_intent]
    }

    // [START auth_fui_create_launcher]
    // See: https://developer.android.com/training/basics/intents/result
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }
    // [END auth_fui_create_launcher]

    // [START auth_fui_result]
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            if (user!= null){
                val useruid = user.uid.toString()
                MyViewModel.loadAllData()
                val username = user.displayName
                Log.d(TAG, "Gebruiker " + username + " met userid " + useruid + " is ingelogd")
                Snackbar.make(getWindow().getDecorView(), username + ' ' + getString(R.string.snackbar_userloggedin), Snackbar.LENGTH_LONG).show()
                //val nu = LocalDateTime.now()
                Log.d(TAG, "data gelezen")
            }
            else
            {
                Log.d(TAG, "Er is geen Gebruiker ingelogd")
            }
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }
    // [END auth_fui_result]

    private fun signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                MyViewModel.cleardata()
            }
        // [END auth_fui_signout]
    }



    // firebase functies
    /*fun createSignInIntent() {

        startActivity(Intent(this@MainActivity,GoogleSignInActivity::class.java))
        // [START auth_fui_create_intent]
        // Choose authentication providers
        /*val providers = Arrays.asList(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )*/
        // [END auth_fui_create_intent]


    }

    // [START auth_fui_result]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG,"resultcode: $resultCode")
        Log.d(TAG,"requestCode: $requestCode")

        if (requestCode == RC_SIGN_IN) {
            //val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                if (user!= null){
                    val useruid = user.uid.toString()
                    val username = user.displayName
                    Log.d(TAG, "Gebruiker " + username + " met userid " + useruid + " is ingelogd")
                    Snackbar.make(getWindow().getDecorView(), username + ' ' + getString(R.string.snackbar_userloggedin), Snackbar.LENGTH_LONG).show()
                    MyViewModel.loadAllData()
                    //val nu = LocalDateTime.now()
                    Log.d(TAG, "data gelezen")
                }
                else
                {
                    Log.d(TAG, "Er is geen Gebruiker ingelogd")
                }

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
    // [END auth_fui_result]

    fun signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                MyViewModel.cleardata()
            }


        // [END auth_fui_signout]
    }

    companion object {
        private const val RC_SIGN_IN = 45263
    }
    */


}
