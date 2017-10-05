package io.reyurnible.android.workrise.presentation.reportsetting

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.RadioGroup
import io.reyurnible.android.workrise.R
import io.reyurnible.android.workrise.domain.model.entity.FormSetting
import kotlinx.android.synthetic.main.report_setting_dialog_form_setting_edit.view.*

/**
 * Dialog Fragment
 */
class FormSettingEditDialog : DialogFragment() {
    companion object {
        fun createInstance(): FormSettingEditDialog =
                FormSettingEditDialog()
    }

    private lateinit var titleEditText: EditText
    private lateinit var typeRadioGroup: RadioGroup

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(context)
                    .setView(LayoutInflater.from(context).inflate(R.layout.report_setting_dialog_form_setting_edit, null, false).apply {
                        this@FormSettingEditDialog.titleEditText = titleEditText
                        this@FormSettingEditDialog.typeRadioGroup = typeRadioGroup
                    })
                    .setPositiveButton(R.string.form_setting_dialog_btn_create, { dialog, which ->
                        val formSetting = FormSetting(
                                title = titleEditText.text.toString(),
                                type = when (typeRadioGroup.checkedRadioButtonId) {
                                    R.id.typeCheckListButton -> FormSetting.FormType.CheckList
                                    R.id.typeTextButton -> FormSetting.FormType.Text
                                    else -> throw IllegalArgumentException("Invalid check radiobutton type.")
                                }
                        )
                        listener?.onFormSettingEditSubmitClicked(this@FormSettingEditDialog, formSetting)
                    })
                    .setNegativeButton(R.string.form_setting_dialog_btn_cancel, { dialog, which ->
                        listener?.onFormSettingEditCancelClicked(this@FormSettingEditDialog)
                    })
                    .create()

    private val listener: OnFormSettingEditDialogListener?
        get() = (parentFragment as? OnFormSettingEditDialogListener) ?:
                (targetFragment as? OnFormSettingEditDialogListener) ?:
                (activity as? OnFormSettingEditDialogListener)

    interface OnFormSettingEditDialogListener {
        fun onFormSettingEditSubmitClicked(dialog: FormSettingEditDialog, value: FormSetting)
        fun onFormSettingEditCancelClicked(dialog: FormSettingEditDialog)
    }
}
