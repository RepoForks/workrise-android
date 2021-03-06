package io.reyurnible.android.workrise.presentation.reportsetting

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reyurnible.android.workrise.domain.model.entity.FormSetting
import io.reyurnible.android.workrise.extensions.addDisposableToBag
import io.reyurnible.android.workrise.usecase.reportsetting.EditReportSettingUseCase
import io.reyurnible.android.workrise.usecase.reportsetting.GetReportSettingUseCase
import javax.inject.Inject

class ReportSettingPresenter
@Inject constructor(
        private val getReportSettingUseCase: GetReportSettingUseCase,
        private val editReportSettingUseCase: EditReportSettingUseCase
) {
    private lateinit var view: ReportSettingPresenter.ReportSettingView
    private val disposableBag: CompositeDisposable = CompositeDisposable()

    private val formSettings: MutableList<FormSetting> = mutableListOf()
    private var editingFormSetting: FormSetting? = null

    fun initialize(view: ReportSettingPresenter.ReportSettingView) {
        this.view = view
        getReportSettingUseCase.get()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ reportSetting ->
                    formSettings.clear()
                    formSettings.addAll(reportSetting.formSettings)
                    view.setFormSettings(formSettings)
                }, { error ->
                    view.showErrorDialog(error)
                })
                .addDisposableToBag(disposableBag)
    }

    fun release() {
        disposableBag.clear()
    }

    fun clickFormSettingItem(formSetting: FormSetting) {

    }

    fun clickAddFormSetting() {
        editingFormSetting = null
        view.showFormSettingEditDialog(null)
    }

    fun clickEditFormSetting(formSetting: FormSetting) {
        editingFormSetting = formSetting
        view.showFormSettingEditDialog(formSetting)
    }

    fun clickDeleteFormSetting(formSetting: FormSetting) {
        editingFormSetting = null
        formSettings.remove(formSetting)
        view.setFormSettings(formSettings)
    }

    fun submitFormSetting(formSetting: FormSetting) {
        editingFormSetting?.let(formSettings::indexOf)?.takeIf { it >= 0 }?.let { index ->
            formSettings.set(index, formSetting)
        } ?: let {
            formSettings.add(formSetting)
        }
        view.setFormSettings(formSettings)
        // set null
        editingFormSetting = null
    }

    fun clickSubmitReportSetting() {
        editReportSettingUseCase.edit(formSettings)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::finish, view::showErrorDialog)
                .addDisposableToBag(disposableBag)
    }

    interface ReportSettingView {
        fun setFormSettings(formSettings: List<FormSetting>?)
        fun finish()
        fun showFormSettingEditDialog(editValue: FormSetting?)
        fun showErrorDialog(error: Throwable)
    }

}
