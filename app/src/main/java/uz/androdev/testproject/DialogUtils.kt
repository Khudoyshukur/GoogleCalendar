package uz.androdev.testproject

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

/**
 * Created by: androdev
 * Date: 11-07-2023
 * Time: 3:26 PM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

fun Context.showOkAlert(
    title: String = "",
    message: String,
    btnPositiveText: String = getString(android.R.string.ok),
    onPositiveClicked: (() -> Boolean)? = null,
    isCancelable: Boolean = false
): Dialog {
    val builder = AlertDialog.Builder(this).apply {
        setCancelable(isCancelable)
        setTitle(title)
        setMessage(message)
        setPositiveButton(btnPositiveText) { dialog, _ ->
            if (onPositiveClicked?.invoke() != true) {
                dialog.dismiss()
            }
        }
    }
    return builder.create().also { it.show() }
}

fun Context.showChoiceAlertNew(
    title: String = "",
    message: String,
    negativeButtonTitle: String,
    positiveButtonTitle: String,
    onNegativeButtonClicked: (() -> Unit)?,
    onPositiveButtonClicked: (() -> Unit)?,
    isCancelable: Boolean = false
): Dialog {
    val builder = AlertDialog.Builder(this)
    val dialog = builder.setMessage(message)
        .setTitle(title)
        .setCancelable(isCancelable)
        .setPositiveButton(positiveButtonTitle) { dialog, _ ->
            dialog.dismiss()
            onPositiveButtonClicked?.invoke()
        }
        .setNegativeButton(negativeButtonTitle) { dialog, _ ->
            dialog.dismiss()
            onNegativeButtonClicked?.invoke()
        }
        .create()
    return dialog.also { it.show() }
}

fun Context.tryOpenPermissionSettings(
    onActivityNotFound: (() -> Unit) = {
        Toast.makeText(this, R.string.app_settings_cannot_be_opened, Toast.LENGTH_SHORT).show()
    }
) {
    val uri = Uri.fromParts("package", packageName, null)
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)

    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        onActivityNotFound()
    }
}