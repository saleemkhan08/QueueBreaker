package com.thnki.queuebreaker.auth.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.thnki.queuebreaker.R
import com.thnki.queuebreaker.home.HomeActivity
import com.thnki.queuebreaker.home.explore.ExploreFragment
import com.thnki.queuebreaker.home.explore.Restaurants
import com.thnki.queuebreaker.model.ToastMsg
import com.thnki.queuebreaker.restaurant.MenuKotlinActivity
import com.thnki.queuebreaker.utils.BaseActivity
import kotlinx.android.synthetic.main.activity_admin_auth.*
import java.util.concurrent.TimeUnit


class AdminAuthActivity : BaseActivity(), FirebaseAuth.AuthStateListener {

    companion object {
        const val USERS = "users"
    }

    private var verificationId: String? = ""

    private var firebaseAuth = FirebaseAuth.getInstance()

    override fun getContentView(): Int {
        return R.layout.activity_admin_auth
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        requestVerificationCode.setOnClickListener {
            requestVerificationCode()
        }
        sendVerificationCode.setOnClickListener {
            sendVerificationCode()
        }

        verificationCode.setOnEditorActionListener { v, actionId, event ->
            handleVerificationCodeAction(actionId)
        }

        phoneNo.setOnEditorActionListener { v, actionId, event ->
            handlePhoneNoAction(actionId)
        }
    }

    override fun onAuthStateChanged(it: FirebaseAuth) {
        Log.d("MultiLaunchError", "AdminAuthActivity : onAuthStateChanged")
        if (it.currentUser != null) {
            launchHomeActivity()
        }
    }

    private fun launchHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun launchRestaurantActivity(restaurant: Restaurants?) {
        val intent = Intent(this, MenuKotlinActivity::class.java)
        intent.putExtra(ExploreFragment.RESTAURANT, restaurant)
        startActivity(intent)
        finish()
    }







    private fun handlePhoneNoAction(actionId: Int): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            requestVerificationCode.performClick()
            return true
        }
        return false
    }

    private fun handleVerificationCodeAction(actionId: Int): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            sendVerificationCode.performClick()
            return true
        }
        return false
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(this)
    }

    private fun requestVerificationCode() {
        val phoneNoStr = phoneNo.text.toString()
        Log.d("phoneVerify", "requestVerificationCode: $phoneNoStr")
        if (phoneNoStr.length == 10 && phoneNoStr.matches("[0-9]+".toRegex())) {
            showVerificationUi()
            setupPhoneAuthProvider(phoneNoStr)
        } else {
            ToastMsg.show(R.string.please_enter_a_valid_phone_num)
        }
    }

    private fun showVerificationUi() {
        phoneNo.isEnabled = false
        verificationCode.visibility = View.VISIBLE
        requestVerificationCode.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun setupPhoneAuthProvider(phoneNoStr: String) {
        val indianPhoneNoStr = "+91$phoneNoStr"
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                indianPhoneNoStr,
                120,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks
        )
    }

    private val verificationCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential?) {
            ToastMsg.show(R.string.auto_verified)
            signInWithPhoneAuthCredential(credential)
            Log.d("phoneVerify", "onVerificationCompleted : $credential")
        }

        override fun onVerificationFailed(e: FirebaseException?) {
            progressBar.visibility = View.GONE
            sendVerificationCode.visibility = View.VISIBLE
            Log.d("phoneVerify", "onVerificationFailed : ${e?.message}")
            if (e is FirebaseAuthInvalidCredentialsException) {
                ToastMsg.show(R.string.invalid_code)
            } else {
                ToastMsg.show(R.string.code_could_not_be_verified)
            }

        }

        override fun onCodeSent(id: String?, token: PhoneAuthProvider.ForceResendingToken?) {
            Log.d("phoneVerify", "onCodeSent : $id, $token")
            verificationId = id
            progressBar.visibility = View.GONE
            sendVerificationCode.visibility = View.VISIBLE
        }

    }

    private fun sendVerificationCode() {
        val code = verificationCode.text.toString()
        Log.d("phoneVerify", "sendVerificationCode : $code")
        if (code.length == 6 && code.matches("[0-9]+".toRegex())) {
            showVerificationWaitingUi()
            val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId!!, code)
            signInWithPhoneAuthCredential(credential)
        } else {
            ToastMsg.show(R.string.please_enter_a_valid_code)
        }
    }

    private fun showVerificationWaitingUi() {
        phoneNo.isEnabled = false
        verificationCode.isEnabled = false
        sendVerificationCode.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential?) {
        Log.d("phoneVerify", "signInWithPhoneAuthCredential")
        firebaseAuth.signInWithCredential(credential!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //val user = task.result.user
                        Log.d("phoneVerify", "signInWithPhoneAuthCredential : task.isSuccessful")
                    } else {
                        ToastMsg.show(R.string.login_failed)
                    }
                }
    }
}
