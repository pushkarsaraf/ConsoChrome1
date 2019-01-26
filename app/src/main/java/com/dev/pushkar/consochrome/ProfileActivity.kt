package com.dev.pushkar.consochrome

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import java.util.Arrays

//import com.nispok.snackbar;

class ProfileActivity : AppCompatActivity() {


    internal var firstname: EditText
    internal var lastname: EditText
    internal var number: EditText
    internal var branch: Spinner? = null
    internal var username: String? = null
    internal var tv: TextView
    internal var bool: Boolean = false

    internal var fl: CoordinatorLayout


    internal var sp: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        number = findViewById(R.id.number)
        bool = intent.getBooleanExtra("navigation", false)
        sp = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        firstname = findViewById(R.id.fn)
        lastname = findViewById(R.id.ln)
        //branch=findViewById(R.id.branchSp);
        tv = findViewById(R.id.username)
        /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        branch.setAdapter(adapter);
*/
        firstname.setText(sp.getString("firstname", ""))
        lastname.setText(sp.getString("lastname", ""))
        number.setText(sp.getString("phone", "0"))
        //branch.setOnItemSelectedListener(this);
        username = firstname.text.toString() + " " + lastname.text.toString()

        fl = findViewById(R.id.frame)
        firstname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                username = firstname.text.toString().trim { it <= ' ' } + " " + lastname.text.toString().trim { it <= ' ' }
                tv.text = username!!.toUpperCase()
            }
        })
        lastname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                username = firstname.text.toString().trim { it <= ' ' } + " " + lastname.text.toString().trim { it <= ' ' }

                tv.text = username!!.toUpperCase()

            }
        })


        val providers = Arrays.asList<IdpConfig>(
                AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())



        if (bool) {

            try {


                val user = FirebaseAuth.getInstance().currentUser!!.uid
                if (user != null) {
                    val intent = Intent(this@ProfileActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN)
                }


            } catch (ex: NullPointerException) {
                Log.d("ex", ex.toString())

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN)
            }

        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser


                val intent = Intent()
                if (bool) {
                    val sp = getSharedPreferences("prefs", Context.MODE_PRIVATE).getBoolean("firstTime", true)
                    if (!getSharedPreferences("prefs", Context.MODE_PRIVATE).contains("firstTime")) {
                        val editor = getSharedPreferences("prefs", Context.MODE_PRIVATE).edit()
                        editor.putBoolean("firstTime", false)
                        editor.apply()
                    } else {
                        intent.setClass(this@ProfileActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
                // ...
            } else {
                // Sign in failed, check response for error code
                // ...
            }
        }
    }

    fun setId(view: View) {
        if (username != null) {
            if (username!!.length > 15 || username!!.trim { it <= ' ' }.length == 0) {
                Toast.makeText(this, "Invalid Length for" + username!!, Toast.LENGTH_LONG).show()
            } else {
                val db = FirebaseDatabase.getInstance()
                val ref = db.getReference("profiles/" + username!!)
                try {
                    ref.setValue(number.text.toString())
                    val edit = sp.edit()
                    edit.putString("firstname", firstname.text.toString())
                    edit.putString("lastname", lastname.text.toString())
                    edit.putString("phone", number.text.toString())
                    edit.putString("username", username!!.toUpperCase())
                    edit.apply()

                } catch (ex: Exception) {
                    Toast.makeText(this, "Please enter valid info", Toast.LENGTH_LONG).show()
                }

                Snackbar.make(fl, username!!, BaseTransientBottomBar.LENGTH_LONG) // context
                        .show() // activity where it is displayed

                //Snackbar.make(,firstname.getText().toString()+" "+lastname.getText().toString(),Toast.LENGTH_LONG).show();
                val intent = Intent(this@ProfileActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    companion object {
        private val RC_SIGN_IN = 123
    }


}
